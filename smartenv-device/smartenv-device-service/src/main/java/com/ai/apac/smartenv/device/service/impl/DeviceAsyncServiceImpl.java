package com.ai.apac.smartenv.device.service.impl;

import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.constant.AddressConstant;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.device.service.IDeviceAsyncService;
import com.ai.apac.smartenv.device.service.IDeviceInfoService;
import com.ai.apac.smartenv.device.service.IDeviceRelService;
import com.ai.apac.smartenv.omnic.dto.ThirdSyncImportResultModel;
import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.feign.IMappingClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class DeviceAsyncServiceImpl implements IDeviceAsyncService {

    private IDeviceInfoService deviceInfoService;
    private IDeviceRelService deviceRelService;

    private IMappingClient mappingClient;
    private IOssClient ossClient;


    @Override
    public Boolean thirdDeviceInfoAsync(List<List<String>> datasList, String tenantId, String actionType,Boolean isAsyn) {
        String message = "";
        if(ObjectUtil.isEmpty(actionType)){
            /**
             * ?????????????????????????????????????????????????????????????????????code?????????????????????????????????
             */
            AiMapping reqMapping = new AiMapping();
            reqMapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.DEVICE));
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
                resultModel.setType(OmnicConstant.THIRD_INFO_TYPE.DEVICE);
                resultModel.setStatus("1");
                DeviceInfo deviceInfo = new DeviceInfo();
                //????????????????????????????????????
                if (!OmnicConstant.ACTION_TYPE.NEW.equals(actionType)) {
                    AiMapping mapping = new AiMapping();
                    mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.DEVICE));
                    mapping.setThirdCode(datas.get(0));
                    mapping.setTenantId(tenantId);
                    AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
                    if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                        deviceInfo = deviceInfoService.getById(mappingRes.getSscpCode());
                        //?????????????????????code??????????????????????????????????????????????????????
                        if (ObjectUtil.isEmpty(deviceInfo)) {
                            if(isAsyn){
                                resultModel.setStatus("0");
                                resultModel.setReason("??????" + datas.get(0) + "?????????");
                                resultModelList.add(resultModel);
                            }
                            continue;
                        } else {
                            Long deviceInfoId = deviceInfo.getId();
                            //?????????????????????,?????????????????????????????????????????????
                            if (OmnicConstant.ACTION_TYPE.DELETE.equals(actionType)) {
                                deviceInfoService.removeById(deviceInfoId);
                                List<DeviceRel> deviceRelList = deviceRelService.getDeviceRelByDeviceId(deviceInfoId);
                                if (ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0) {
                                    for (DeviceRel deviceRel : deviceRelList) {
                                        deviceInfoService.deleteBindDevice(deviceRel.getEntityType(), deviceRel.getEntityId(), deviceRel.getDeviceId().toString());
                                    }
                                }
                                mappingClient.delMapping(deviceInfoId.toString(), Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.DEVICE));
                                resultModelList.add(resultModel);
                                continue;
                            }
                        }
                    } else {
                        if(isAsyn){
                            resultModel.setStatus("0");
                            resultModel.setReason("??????" + datas.get(0) + "?????????");
                            resultModelList.add(resultModel);
                        }
                        continue;
                    }
                } else {
                    deviceInfo.setTenantId(tenantId);
                }
                deviceInfo.setDeviceStatus(Long.parseLong(DeviceConstant.DeviceStatus.NO));
                checkFacilityInfo(resultModel, datas, deviceInfo,isAsyn);
                if (resultModel.getStatus().equals("1")) {
                    deviceInfoService.saveOrUpdate(deviceInfo);
                    if (OmnicConstant.ACTION_TYPE.NEW.equals(actionType)) {
                        AiMapping mapping = new AiMapping();
                        mapping.setTenantId(tenantId);
                        mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.DEVICE));
                        mapping.setSscpCode(deviceInfo.getId().toString());
                        mapping.setThirdCode(datas.get(0));
                        mappingClient.saveMappingCode(mapping);
                    }
                }
            } catch (Exception ex) {
                if(!isAsyn){
                    throw new ServiceException(message);
                }
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
        String data = ossClient.putBase64Stream(AddressConstant.BUCKET, "?????????" + tenantId + "??????????????????.xlsx", stringData).getData();
        log.info("URL------------------" + data);
    }

    private void checkFacilityInfo(ThirdSyncImportResultModel resultModel, List<String> datas, DeviceInfo deviceInfo,Boolean isAsyn) {
        if (ObjectUtil.isNotEmpty(datas.get(0))) {
            deviceInfo.setDeviceCode(datas.get(0));
        } else {
            if(!isAsyn){
                throw new ServiceException("????????????????????????");
            }
            resultModel.setStatus("0");
            resultModel.setReason("????????????????????????");
        }

        if (ObjectUtil.isNotEmpty(datas.get(1))) {
            deviceInfo.setDeviceName(datas.get(1));
        } else {
            resultModel.setStatus("0");
            resultModel.setReason("????????????????????????");
        }

    }
}
