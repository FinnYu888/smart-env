package com.ai.apac.smartenv.facility.controller;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.FacilityConstant;
import com.ai.apac.smartenv.common.constant.ResultCodeConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.facility.entity.FacilityInfo;
import com.ai.apac.smartenv.facility.entity.FacilityRel;
import com.ai.apac.smartenv.facility.entity.FacilityTranstationDetail;
import com.ai.apac.smartenv.facility.service.IFacilityInfoService;
import com.ai.apac.smartenv.facility.service.IFacilityTranstationDetailService;
import com.ai.apac.smartenv.facility.vo.FacilityInfoVO;
import com.ai.apac.smartenv.facility.vo.FacilityMangeListVO;
import com.ai.apac.smartenv.facility.vo.FacilityTranstationDetailVO;
import com.ai.apac.smartenv.system.feign.IDictBizClient;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.websocket.feign.IBigScreenDataClient;
import com.ai.apac.smartenv.websocket.feign.IHomeDataClient;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.*;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;

import java.text.DecimalFormat;
import java.util.*;
import com.ai.apac.smartenv.facility.cache.FacilityOdorLevelCache;
import com.ai.apac.smartenv.facility.service.IFacilityRelService;
import org.springblade.core.tool.utils.Func;
import java.io.IOException;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/facilityManage")
@Api(value = "??????????????????", tags = "??????????????????")
public class FacilityManageController extends BladeController {
    private IFacilityInfoService facilityInfoService;
    private IFacilityTranstationDetailService facilityTranstationDetailService;
    private IDictBizClient dictBizClient;
    private IDictClient dictClient;
    private IDeviceRelClient deviceRelClient;

    private IBigScreenDataClient bigScreenDataClient;

    private IHomeDataClient homeDataClient;

    private String getExceptionMsg(String key) {
        String msg = dictBizClient.getValue(FacilityConstant.ExceptionMsg.CODE, key).getData();
        if (StringUtils.isBlank(msg)) {
            msg = key;
        }
        return msg;
    }

    /**
     * ???????????????????????????
     */
    @ApiLog(value = "???????????????????????????")
    @PostMapping("/receiveTranstationInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "???????????????????????????", notes = "??????FacilityTranstationDetailVO")
    public R receiveTranstationInfo(@RequestBody FacilityTranstationDetailVO facilityTranstationDetailVO, BladeUser bladeUser) {
        if (null == facilityTranstationDetailVO.getFacilityId() && null == facilityTranstationDetailVO.getDeviceId()) {
            throw new ServiceException(getExceptionMsg(FacilityConstant.ExceptionMsg.KEY_DEVICE_NULL));
        }
        String tenantId = "";
        if (null != bladeUser && StringUtils.isNotEmpty(bladeUser.getTenantId())) {
            tenantId = bladeUser.getTenantId();
        }

        log.info("facilityTranstationDetailVO:{}", JSON.toJSONString(facilityTranstationDetailVO));
        FacilityTranstationDetail facilityTranstationDetail = new FacilityTranstationDetail();
        BeanUtil.copy(facilityTranstationDetailVO, facilityTranstationDetail);

        //???????????????id?????????????????????????????????????????????????????????????????????
        if (null == facilityTranstationDetailVO.getFacilityId()) {
            Long deviceId = facilityTranstationDetailVO.getDeviceId();
            FacilityRel facilityRel = new FacilityRel();
            facilityRel.setEntityId(deviceId);
            R<List<DeviceRel>> returnDeviceRelList = deviceRelClient.getEntityRels(deviceId, null);

            if (null == returnDeviceRelList || null == returnDeviceRelList.getData() || returnDeviceRelList.getData().size() == 0) {
                throw new ServiceException(getExceptionMsg(FacilityConstant.ExceptionMsg.KEY_DEVICE_FACILITY_ERR));
            }
            DeviceRel deviceRel = returnDeviceRelList.getData().get(0);
            facilityTranstationDetail.setFacilityId(deviceRel.getEntityId());
        }
        if (StringUtils.isEmpty(tenantId) && null != facilityTranstationDetail.getFacilityId()) {
            FacilityInfo facilityInfo = facilityInfoService.getById(facilityTranstationDetailVO.getFacilityId());
            if (null != facilityInfo) tenantId = facilityInfo.getTenantId();
        }
        facilityTranstationDetail.setTenantId(tenantId);
        //??????????????????2?????????
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        facilityTranstationDetail.setGarbageWeight(decimalFormat.format(Func.toFloat(facilityTranstationDetail.getGarbageWeight())));
        if (null == facilityTranstationDetailVO.getTransferTimes()) {
            facilityTranstationDetail.setTransferTimes(1);
        }
        if (null == facilityTranstationDetail.getTransferTime()) {
            facilityTranstationDetail.setTransferTime(TimeUtil.getSysDate());
        }
        log.info("facilityTranstationDetailVO:{}", JSON.toJSONString(facilityTranstationDetail));
        Boolean  status = facilityTranstationDetailService.saveDetail(facilityTranstationDetail);
        return R.status(status);
    }

    /**
     * ?????????????????????????????????
     */
    @ApiLog(value = "?????????????????????????????????")
    @PostMapping("/getTranstationTotalList")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "?????????????????????????????????", notes = "FacilityTranstationTotalVO")
    public R<FacilityMangeListVO> getTranstationTotalList(@RequestParam(required = false) String companyCode, @RequestParam(required = false) String status,
                                                          @RequestParam(required = false) String facilityName,@RequestParam(required = false,name = "statusFlag") boolean statusFlag, Query query, BladeUser user) {
        FacilityMangeListVO mangeListVO = new FacilityMangeListVO();
        Double garbageWeight = 0.0;
        int transferTimes = 0;

        IPage<FacilityInfoVO> infoIPage = facilityInfoService.page(Condition.getPage(query), getQueryWrapper(companyCode,facilityName,status,statusFlag,user.getTenantId()));

        if (infoIPage.getTotal() > 0) {
            List<FacilityInfoVO> totalList = facilityInfoService.selectTranstationTotalList(Condition.getPage(query), companyCode,facilityName,status,statusFlag,user.getTenantId());
            infoIPage.setRecords(totalList);

            if (null != totalList || totalList.size() > 0) {
               List<Long> facilityList = new ArrayList<>();
               List<String> facilityNoList= new ArrayList<>();
                for (FacilityInfoVO total : totalList) {
                    //???????????????id
                    facilityList.add(total.getId());
                    facilityNoList.add(total.getProjectNo());
                    //?????????????????????
                    String statusName = getDictName(FacilityConstant.TranStationStatus.CODE, total.getStatus().toString());
                    total.setStatusName(statusName);

                    //?????????????????????
                    String modelName = getDictName(FacilityConstant.TranStationModel.CODE, total.getExt1());
                    total.setTranstationModel(modelName);
                    //???????????????
                    if (StringUtil.isBlank(total.getGarbageWeight())) total.setGarbageWeight("0");
                    if (null == total.getTransferTimes()) total.setTransferTimes(0);

                    try {
                        garbageWeight += Double.valueOf(total.getGarbageWeight());
                        transferTimes += Integer.valueOf(total.getTransferTimes());
                    } catch (Exception e) {
                        log.error("???????????????????????????,?????????id=" + total.getId(), e);
                        throw new ServiceException(getExceptionMsg(FacilityConstant.ExceptionMsg.KEY_NUMER_FORMAT_ERR));
                    }


                    total.setOdoyLevel("0");
                }
                mangeListVO.setTranstationTotaPage(infoIPage);



                //??????????????????
                Map<Long,Long> deviceMap = new HashMap<>();
                R<Map<Long,Long>> deviceResponse =deviceRelClient.getDeviceCount(facilityList,CommonConstant.ENTITY_TYPE.FACILITY);
                if (ResultCodeConstant.ResponseCode.SUCCESS == deviceResponse.getCode()) {
                    deviceMap = deviceResponse.getData();
                }
                Map<String,String> odorLevelMap = getOdorLevel(facilityNoList);
                for (FacilityInfoVO total : totalList) {
                    if (null != odorLevelMap.get(total.getProjectNo())) {
                        total.setOdoyLevel(odorLevelMap.get(total.getProjectNo()));
                    }else {
                        total.setOdoyLevel("0.0(??????)");
                    }

                    total.setDeviceCount(deviceMap.get(total.getId()));
                }

            }

        }
        mangeListVO.setTranstationNum(String.valueOf(infoIPage.getTotal()));
        mangeListVO.setGarbageWeightTotal(String.valueOf(garbageWeight));
        mangeListVO.setTransitTimesTotal(String.valueOf(transferTimes));
        return R.data(mangeListVO);
    }

    /**
     * ????????????????????????
     *0?????????
     *      * 0-1???????????????
     *      * 1-2???????????????
     *      * 2-3???????????????
     *      * 3-4???????????????
     *      * 4-5: ??????
     *      * 5???????????????
     *     */
    private Map<String, String> getOdorLevel(List<String> facilityNos) {
        Map<String,String> odorMap = new HashMap<>();
        if (null == facilityNos|| 0== facilityNos.size()) {
            return odorMap;
        }
        StringBuffer facilityBuffer = new StringBuffer();
        facilityNos.forEach(facilityNo->{
            facilityBuffer.append(facilityNo).append(",");
        });
        String facilityStr = facilityBuffer.substring(0,facilityBuffer.lastIndexOf(","));
        try {
            Map<String,Object> param = new HashMap<>();
            param.put(BigDataHttpClient.Odoy_Request.WTS_IDS,facilityStr);
            log.debug("request getOdoyLevel===="+facilityStr);
            String reStr = BigDataHttpClient.getBigDataBody(BigDataHttpClient.getOdlyLevelURL,param);
            log.debug(" response /smartenv-api/wts/odour/search==="+reStr);
            if (StringUtil.isNotBlank(reStr) && null != JSONUtil.parseObj(reStr).get(BigDataHttpClient.RESPONSE.CODE) && "0".equals(JSONUtil.parseObj(reStr).get(BigDataHttpClient.RESPONSE.CODE).toString())) {
                JSONObject data = (JSONObject)JSONUtil.parseObj(reStr).get("data");
                JSONArray wtsOdours = (JSONArray) data.get(BigDataHttpClient.Odoy_Request.WTS_ODOURS);
                for (int i=0;i<wtsOdours.size();i++) {
                    JSONObject object = wtsOdours.getJSONObject(i);
                    //{"deviceNumber":"867959034034859","facilityId":"zz190917173709","facilityName":"????????????????????????","lat":29.338291,"lng":115.766724,"senSorValue":3.5,"time":"2020-07-21 09:18:48"}
                    Double odorlevel  = (Double)object.get("senSorValue");
                    String facilityNo = (String) object.get("facilityId");
                    StringBuffer buffer = new StringBuffer(String.valueOf(odorlevel));

                    int level =  odorlevel.intValue();
                    level = odorlevel-level == 0?level:level + 1;


                    String value = dictClient.getValue(FacilityConstant.FacilityOdorLevel.CODE,String.valueOf(level)).getData();
                    buffer.append("(").append(value).append(")");

                    odorMap.put(facilityNo,buffer.toString());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        }
        return odorMap;
    }

    private QueryWrapper getQueryWrapper(String companyCode,String facilityName,String status,boolean statusFlag,String tenantId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (StringUtil.isNotBlank(companyCode)) queryWrapper.eq("COMPAYN_CODE", companyCode);
        if (StringUtil.isNotBlank(status)) queryWrapper.eq("STATUS", status);
        if (StringUtil.isNotBlank(facilityName)) queryWrapper.like("FACILITY_NAME", facilityName);
        if (StringUtil.isNotBlank(tenantId)) queryWrapper.eq("tenant_id",tenantId);
        if (statusFlag) queryWrapper.notIn("status",new ArrayList<String>(Arrays.asList(FacilityConstant.TranStationStatus.PLANNING,FacilityConstant.TranStationStatus.DROP)));
        return queryWrapper;
    }
    private String getDictName(String code, String dictKey) {
        R<String> value = dictClient.getValue(code, dictKey);
        if (value.isSuccess() && StringUtil.isNotBlank(value.getData())) {
            return value.getData();
        }
        return "";
    }
}
