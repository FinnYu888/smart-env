package com.ai.apac.smartenv.system.service.impl;

import com.ai.apac.smartenv.common.constant.AddressConstant;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.omnic.dto.ThirdSyncImportResultModel;
import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.feign.IMappingClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.entity.Station;
import com.ai.apac.smartenv.system.service.IDeptAsyncService;
import com.ai.apac.smartenv.system.service.IDeptService;
import com.ai.apac.smartenv.system.service.IRegionAsyncService;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.springframework.util.Base64Utils;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class DeptAsyncServiceImpl implements IDeptAsyncService {

    private IMappingClient mappingClient;

    private IDeptService deptService;

    private IOssClient ossClient;

    @Override
    public Boolean thirdDeptInfoAsync(List<List<String>> datasList, String tenantId, String actionType,Boolean isAsyn) {

        if(ObjectUtil.isEmpty(actionType)){
            /**
             * 对实时调用接口，我们支持新增或者更新，需求根据code是否存在来判断操作类型
             */
            AiMapping reqMapping = new AiMapping();
            reqMapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.DEPT));
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
                resultModel.setType(OmnicConstant.THIRD_INFO_TYPE.DEPT);
                resultModel.setStatus("1");
                Dept dept = new Dept();
                //如果操作符是更新或者删除

                if (!OmnicConstant.ACTION_TYPE.NEW.equals(actionType)) {
                    AiMapping mapping = new AiMapping();
                    mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.DEPT));
                    mapping.setThirdCode(datas.get(0));
                    mapping.setTenantId(tenantId);
                    AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
                    if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                        dept = deptService.getById(mappingRes.getSscpCode());
                        //如果根据第三方code没有找到业务区域，直接返回这次的结果
                        if (ObjectUtil.isEmpty(dept)) {
                            if(isAsyn){
                                resultModel.setStatus("0");
                                resultModel.setReason("部门" + datas.get(0) + "不存在");
                                resultModelList.add(resultModel);
                            }
                            continue;
                        } else {
                            Long deptId = dept.getId();
                            //如果是删除操作,把部门信息删了。
                            if (OmnicConstant.ACTION_TYPE.DELETE.equals(actionType)) {
                                deptService.removeById(deptId);
                                mappingClient.delMapping(deptId.toString(), Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.DEPT));
                                resultModelList.add(resultModel);
                                continue;
                            }
                        }
                    } else {
                        if(isAsyn){
                            resultModel.setStatus("0");
                            resultModel.setReason("部门" + datas.get(0) + "不存在");
                            resultModelList.add(resultModel);
                        }

                        continue;
                    }
                } else {
                    dept.setTenantId(tenantId);
                }
                checkPersonInfo(resultModel, datas, dept);
                if (resultModel.getStatus().equals("1")) {

                    deptService.saveOrUpdate(dept);
                    if (OmnicConstant.ACTION_TYPE.NEW.equals(actionType)) {
                        AiMapping mapping = new AiMapping();
                        mapping.setTenantId(tenantId);
                        mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.DEPT));
                        mapping.setSscpCode(dept.getId().toString());
                        mapping.setThirdCode(datas.get(0));
                        mappingClient.saveMappingCode(mapping);
                    }
                }
            } catch (Exception ex) {
                if(!isAsyn){
                    throw new ServiceException("部门" + datas.get(0) + "同步失败");
                }
                resultModel.setStatus("0");
                resultModel.setReason(ex.getMessage());
            }
            log.info("resultModel----------------" + resultModel.toString());
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
        String data = ossClient.putBase64Stream(AddressConstant.BUCKET, "第三方" + tenantId + "部门导入结果.xlsx", stringData).getData();
        log.info("URL------------------" + data);
    }

    private void checkPersonInfo(ThirdSyncImportResultModel resultModel, List<String> datas, Dept dept) {
        if (datas.size() > 1) {
            dept.setDeptName(datas.get(1));
            dept.setFullName(datas.get(1));
        } else {
            resultModel.setStatus("0");
            resultModel.setReason("部门名称不能为空");
        }

        if (datas.size() > 2) {
            AiMapping mapping = new AiMapping();
            mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.DEPT));
            mapping.setThirdCode(datas.get(2));
            mapping.setTenantId(dept.getTenantId());
            AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
            if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                dept.setParentId(Long.parseLong(mappingRes.getSscpCode()));
            }
        }
    }

}

