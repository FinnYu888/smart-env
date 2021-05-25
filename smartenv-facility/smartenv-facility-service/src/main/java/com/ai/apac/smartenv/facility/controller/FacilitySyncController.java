package com.ai.apac.smartenv.facility.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.facility.service.IFacilityAsyncService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleSyncController
 * @Description: 设施同步接口
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/11/25
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/11/25  19:07    panfeng          v1.0.0             修改原因
 */
@RestController
@RequestMapping("/sync")
@AllArgsConstructor
public class FacilitySyncController {


    private IFacilityAsyncService facilityAsyncService;

    /**
     * 导入中转站信息表
     */
    @PostMapping("/transtationInfo")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "第三方同步中转站信息", notes = "第三方同步中转站信息")
    @ApiLog(value = "第三方同步中转站信息")
    public R<Boolean> syncFacilityInfo(@RequestBody JSONObject obj) {
        List<List<String>> datasList = initializeData(obj);
        String tenantId = AuthUtil.getTenantId();
        return R.data(facilityAsyncService.thirdFacilityInfoAsync(datasList,tenantId,"",false));
    }

    /**
     * 删除中转站信息表
     */
    @DeleteMapping("/transtationInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "第三方删除中转站信息", notes = "第三方删除中转站信息")
    @ApiLog(value = "第三方删除中转站信息")
    public R<Boolean> delFacilityInfo(@RequestBody JSONObject obj) {
        List<List<String>> datasList = initializeData(obj);
        String tenantId = AuthUtil.getTenantId();
        return R.data(facilityAsyncService.thirdFacilityInfoAsync(datasList,tenantId,OmnicConstant.ACTION_TYPE.DELETE,false));
    }


    private List<List<String>> initializeData(JSONObject obj){
        List<List<String>> datasList = new ArrayList<List<String>>();
        List<String> data = new ArrayList<String>();
        if(ObjectUtil.isNotEmpty(obj.getStr("stationCode"))){
            data.add(obj.getStr("stationCode"));
        }else{
            throw new ServiceException("中转站编号不能为空");
        }

        String stationName = obj.getStr("stationName")==null?"":obj.getStr("stationName");
        data.add(stationName);

        String lat = obj.getStr("lat")==null?"":obj.getStr("lat");
        data.add(lat);

        String lng = obj.getStr("lng")==null?"":obj.getStr("lng");
        data.add(lng);

        String scale = obj.getStr("scale")==null?"":obj.getStr("scale");
        data.add(scale);

        String address = obj.getStr("address")==null?"":obj.getStr("address");
        data.add(address);

        String deptCode = obj.getStr("deptCode")==null?"":obj.getStr("deptCode");
        data.add(deptCode);

        JSONArray deviceCodes = obj.getJSONArray("deviceCodes");
        if(ObjectUtil.isNotEmpty(deviceCodes) && deviceCodes.size() > 0){
            StringBuilder deviceCodeStr = new StringBuilder();
            Object[] deviceCodeSet = deviceCodes.toArray();
            for(Object deviceCode:deviceCodeSet){
                deviceCodeStr.append(deviceCode).append("|");
            }
            data.add(deviceCodeStr.substring(0,deviceCodeStr.length()-1));
        }else{
            data.add("");
        }


        datasList.add(data);

        return datasList;
    }
}

