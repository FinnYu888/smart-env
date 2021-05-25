package com.ai.apac.smartenv.system.service.impl;

import com.ai.apac.smartenv.common.constant.AddressConstant;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.omnic.dto.ThirdSyncImportResultModel;
import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.feign.IMappingClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.service.IRegionAsyncService;
import com.ai.apac.smartenv.system.service.IRegionService;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.feign.IWorkareaNodeClient;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class RegionAsyncServiceImpl implements IRegionAsyncService {


    private IRegionService regionService;

    private IWorkareaNodeClient workareaNodeClient;

    private IMappingClient mappingClient;

    private IOssClient ossClient;


    @Override
    public Boolean thirdRegionInfoAsync(List<List<String>> datasList, String tenantId, String actionType,Boolean isAsyn) {

        List<ThirdSyncImportResultModel> resultModelList = new ArrayList<ThirdSyncImportResultModel>();
        for (List<String> datas : datasList) {
            ThirdSyncImportResultModel resultModel = new ThirdSyncImportResultModel();
            try {
                resultModel.setCode(datas.get(0));
                resultModel.setType(OmnicConstant.THIRD_INFO_TYPE.AREA);
                resultModel.setStatus("1");
                Region region = new Region();
                //如果操作符是更新或者删除
                if (!OmnicConstant.ACTION_TYPE.NEW.equals(actionType)) {
                    AiMapping mapping = new AiMapping();
                    mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.AREA));
                    mapping.setThirdCode(datas.get(0));
                    mapping.setTenantId(tenantId);
                    AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
                    if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                        region = regionService.getById(mappingRes.getSscpCode());
                        //如果根据第三方code没有找到业务区域，直接返回这次的结果
                        if (ObjectUtil.isEmpty(region)) {
                            if(!isAsyn){
                                throw new ServiceException("区域" + datas.get(0) + "不存在");
                            }
                            resultModel.setStatus("0");
                            resultModel.setReason("区域" + datas.get(0) + "不存在");
                            resultModelList.add(resultModel);
                            continue;
                        } else {
                            //不管是更新还是删除,先把坐标点先解绑了
                            Long regionId = region.getId();
                            QueryWrapper<WorkareaNode> wrapper = new QueryWrapper<WorkareaNode>();
                            wrapper.lambda().eq(WorkareaNode::getRegionId, regionId);
                            workareaNodeClient.deleteWorkAreaNodes(regionId);
                            //如果是删除操作,把业务区域基本信息删了。
                            if (OmnicConstant.ACTION_TYPE.DELETE.equals(actionType)) {
                                regionService.removeById(regionId);
                                mappingClient.delMapping(regionId.toString(), Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.AREA));
                                resultModelList.add(resultModel);
                                continue;
                            }
                        }
                    } else {
                        if(!isAsyn){
                            throw new ServiceException("区域" + datas.get(0) + "不存在");
                        }
                        resultModel.setStatus("0");
                        resultModel.setReason("区域" + datas.get(0) + "不存在");
                        resultModelList.add(resultModel);
                        continue;
                    }
                } else {
                    region.setTenantId(tenantId);
                }
                List<WorkareaNode> workareaNodeList = new ArrayList<WorkareaNode>();
                checkRegionInfo(resultModel, datas, region,isAsyn);
                if (resultModel.getStatus().equals("1")) {
                    regionService.saveOrUpdate(region);
                    if (OmnicConstant.ACTION_TYPE.NEW.equals(actionType)) {
                        AiMapping mapping0 = new AiMapping();
                        mapping0.setTenantId(tenantId);
                        mapping0.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.AREA));
                        mapping0.setSscpCode(region.getId().toString());
                        mapping0.setThirdCode(datas.get(0));
                        mappingClient.saveMappingCode(mapping0);
                    }

                    if (ObjectUtil.isNotEmpty(datas.get(3))) {
                        String[] nodeStrList = datas.get(3).split("\\|");
                        if (ObjectUtil.isNotEmpty(nodeStrList) && nodeStrList.length > 0) {
                            for (String nodeStr : nodeStrList) {
                                Long index = 1L;
                                String[] node = nodeStr.split(",");
                                if (ObjectUtil.isNotEmpty(node) && node.length == 2) {
                                    WorkareaNode workareaNode = new WorkareaNode();
                                    workareaNode.setNodeSeq(index);
                                    workareaNode.setLongitude(node[0]);
                                    workareaNode.setLatitudinal(node[1]);
                                    workareaNode.setRegionId(region.getId());
                                    workareaNode.setTenantId(region.getTenantId());
                                    index++;
                                    workareaNodeList.add(workareaNode);
                                }
                            }
                            for (WorkareaNode workareaNode_ : workareaNodeList) {
                                workareaNodeClient.saveWorkAreaNode(workareaNode_);
                            }
                        }
                    }


                }
            } catch (Exception ex) {
                if(!isAsyn){
                    throw new ServiceException("区域导入不存在");
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
        String data = ossClient.putBase64Stream(AddressConstant.BUCKET, "第三方" + tenantId + "业务地区导入结果.xlsx", stringData).getData();
        log.info("URL------------------" + data);
    }


    private void checkRegionInfo(ThirdSyncImportResultModel resultModel, List<String> datas, Region region,Boolean isAsyn) {
        if (ObjectUtil.isNotEmpty(datas.get(1))) {
            region.setRegionName(datas.get(1));
        } else {
            if(!isAsyn){
                throw new ServiceException("区域名称不能为空");
            }
            resultModel.setStatus("0");
            resultModel.setReason("区域名称不能为空");
        }
        if (ObjectUtil.isNotEmpty(datas.get(2))) {
            region.setRegionType(Long.parseLong(datas.get(2)));
        }
    }

}
