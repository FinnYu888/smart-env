package com.ai.apac.smartenv.facility.service.impl;

import com.ai.apac.smartenv.common.constant.AddressConstant;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.facility.entity.FacilityInfo;
import com.ai.apac.smartenv.facility.service.IFacilityAsyncService;
import com.ai.apac.smartenv.facility.service.IFacilityInfoService;
import com.ai.apac.smartenv.omnic.dto.ThirdSyncImportResultModel;
import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.feign.IMappingClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.entity.Person;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class FacilityAsyncServiceImpl implements IFacilityAsyncService {

    private IMappingClient mappingClient;

    private IDeviceRelClient deviceRelClient;

    private IDeviceClient deviceClient;

    private IFacilityInfoService facilityInfoService;

    private IOssClient ossClient;


    @Override
    public Boolean thirdFacilityInfoAsync(List<List<String>> datasList, String tenantId, String actionType,Boolean isAsyn) {

        if(ObjectUtil.isEmpty(actionType)){
            /**
             * 对实时调用接口，我们支持新增或者更新，需求根据code是否存在来判断操作类型
             */
            AiMapping reqMapping = new AiMapping();
            reqMapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.FACILITY));
            reqMapping.setThirdCode(datasList.get(0).get(0));
            reqMapping.setTenantId(tenantId);
            AiMapping resMapping = mappingClient.getSscpCodeByThirdCode(reqMapping).getData();
            if(ObjectUtil.isNotEmpty(resMapping) && ObjectUtil.isNotEmpty(resMapping.getSscpCode())){
                actionType = OmnicConstant.ACTION_TYPE.UPDATE;
            }else{
                actionType = OmnicConstant.ACTION_TYPE.NEW;
            }
        }

        List<ThirdSyncImportResultModel> resultModelList = new ArrayList<ThirdSyncImportResultModel>();
        for (List<String> datas : datasList) {
            ThirdSyncImportResultModel resultModel = new ThirdSyncImportResultModel();
            try {
                resultModel.setCode(datas.get(0));
                resultModel.setType(OmnicConstant.THIRD_INFO_TYPE.FACILITY);
                resultModel.setStatus("1");
                FacilityInfo facilityInfo = new FacilityInfo();
                //如果操作符是更新或者删除
                if (!OmnicConstant.ACTION_TYPE.NEW.equals(actionType)) {
                    AiMapping mapping = new AiMapping();
                    mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.FACILITY));
                    mapping.setThirdCode(datas.get(0));
                    mapping.setTenantId(tenantId);
                    AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
                    if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                        facilityInfo = facilityInfoService.getById(mappingRes.getSscpCode());
                        //如果根据第三方code没有找到中转站，直接返回这次的结果
                        if (ObjectUtil.isEmpty(facilityInfo)) {
                            if(isAsyn){
                                resultModel.setStatus("0");
                                resultModel.setReason("中转站" + datas.get(0) + "不存在");
                                resultModelList.add(resultModel);
                            }
                            continue;
                        } else {
                            //不管是更新还是删除,先把设备先解绑了
                            Long facilityId = facilityInfo.getId();
                            deviceClient.unbindDevice(facilityId, CommonConstant.ENTITY_TYPE.FACILITY);
                            //如果是删除操作,把人删了。
                            if (OmnicConstant.ACTION_TYPE.DELETE.equals(actionType)) {
                                facilityInfoService.removeById(facilityId);
                                mappingClient.delMapping(facilityId.toString(), Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.FACILITY));
                                resultModelList.add(resultModel);
                                continue;
                            }
                        }
                    } else {
                        if(isAsyn){
                            resultModel.setStatus("0");
                            resultModel.setReason("中转站" + datas.get(0) + "不存在");
                            resultModelList.add(resultModel);
                        }
                        continue;
                    }
                }else {
                    facilityInfo.setTenantId(tenantId);
                    facilityInfo.setExt1("1");
                }
                String deviceIds = "";
                checkFacilityInfo(resultModel, datas, facilityInfo,isAsyn);
                if (resultModel.getStatus().equals("1")) {
                    facilityInfoService.saveOrUpdate(facilityInfo);
                    if (OmnicConstant.ACTION_TYPE.NEW.equals(actionType)) {
                        AiMapping mapping0 = new AiMapping();
                        mapping0.setTenantId(tenantId);
                        mapping0.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.FACILITY));
                        mapping0.setSscpCode(facilityInfo.getId().toString());
                        mapping0.setThirdCode(datas.get(0));
                        mappingClient.saveMappingCode(mapping0);
                    }

                    if (ObjectUtil.isNotEmpty(datas.get(7))) {
                        String[] thirdCodeList = datas.get(7).split("\\|");
                        if (thirdCodeList.length > 0) {
                            for (String thirdCode : thirdCodeList) {
                                AiMapping mapping = new AiMapping();
                                mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.DEVICE));
                                mapping.setThirdCode(thirdCode);
                                mapping.setTenantId(facilityInfo.getTenantId());
                                AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
                                if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                                    deviceIds = deviceIds + mappingRes.getSscpCode() + ",";
                                }
                            }
                            deviceIds = deviceIds.substring(0, deviceIds.length() - 1);
                        }
                    }
                    if (ObjectUtil.isNotEmpty(deviceIds)) {
                        deviceRelClient.bindDevice(CommonConstant.ENTITY_TYPE.FACILITY.toString(), facilityInfo.getId(), deviceIds);
                    }

                }
            } catch (Exception ex) {
                resultModel.setStatus("0");
                resultModel.setReason(ex.getMessage());
            }
            log.info("resultModel-----------------" + resultModel.toString());
            resultModelList.add(resultModel);
        }

        if(isAsyn){
            saveResultModel(resultModelList,tenantId);
        }

        return true;
    }


    private void saveResultModel(List<ThirdSyncImportResultModel> resultModelList,String tenantId){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ExcelWriter writer = new ExcelWriter(byteArrayOutputStream, ExcelTypeEnum.XLSX);
        Sheet sheet1 = new Sheet(1, 0, ThirdSyncImportResultModel.class);

        sheet1.setSheetName("sheet1");
        writer.write(resultModelList, sheet1);
        writer.finish();

        byte[] bytes = byteArrayOutputStream.toByteArray();
        String stringData = Base64Utils.encodeToString(bytes);
        String data = ossClient.putBase64Stream(AddressConstant.BUCKET, "第三方" + tenantId + "中转站导入结果.xlsx", stringData).getData();
        log.info("URL------------------" + data);
    }

    private void checkFacilityInfo(ThirdSyncImportResultModel resultModel, List<String> datas, FacilityInfo facilityInfo,Boolean isAsyn) {
        if (ObjectUtil.isNotEmpty(datas.get(1))) {
            facilityInfo.setFacilityName(datas.get(1));
        } else {
            if(!isAsyn){
                throw new ServiceException("中转站名称不能为空");
            }
            resultModel.setStatus("0");
            resultModel.setReason("中转站名称不能为空");
        }

        if (ObjectUtil.isNotEmpty(datas.get(2))) {
            facilityInfo.setLat(datas.get(2));
        }

        if (ObjectUtil.isNotEmpty(datas.get(3))) {
            facilityInfo.setLng(datas.get(3));
        }

        if (ObjectUtil.isNotEmpty(datas.get(5))) {
            facilityInfo.setLocation(datas.get(5));
        }


    }
}
