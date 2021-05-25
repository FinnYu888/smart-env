package com.ai.apac.smartenv.vehicle.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.omnic.feign.IMappingClient;
import com.ai.apac.smartenv.vehicle.dto.VehicleSyncDto;
import com.ai.apac.smartenv.vehicle.service.IVehicleAsyncService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
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
 * @Description: 车辆同步接口
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
@Api(value = "车辆信息同步", tags = "车辆信息同步")
@AllArgsConstructor
public class VehicleSyncController {


    private IVehicleAsyncService vehicleAsyncService;

    /**
     * 导入人员信息表
     */
    @PostMapping("/vehicleInfo")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "第三方同步人员信息", notes = "第三方同步人员信息")
    @ApiLog(value = "第三方同步人员信息")
    public R<Boolean> syncVehicleInfo(@RequestBody JSONObject obj) {
        List<List<String>> datasList = initializeData(obj);
        String tenantId = AuthUtil.getTenantId();

        return R.data(vehicleAsyncService.thirdVehicleInfoAsync(datasList,tenantId,"",false));
    }

    /**
     * 导入人员信息表
     */
    @DeleteMapping("/vehicleInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "第三方删除人员信息", notes = "第三方删除人员信息")
    @ApiLog(value = "第三方删除人员信息")
    public R<Boolean> delVehicleInfo(@RequestBody JSONObject obj) {
        List<List<String>> datasList = initializeData(obj);
        String tenantId = AuthUtil.getTenantId();
        return R.data(vehicleAsyncService.thirdVehicleInfoAsync(datasList,tenantId,OmnicConstant.ACTION_TYPE.DELETE,false));
    }


    private List<List<String>> initializeData(JSONObject obj){
        List<List<String>> datasList = new ArrayList<List<String>>();
        List<String> data = new ArrayList<String>();
        if(ObjectUtil.isNotEmpty(obj.getStr("vehicleCode"))){
            data.add(obj.getStr("vehicleCode"));
        }else{
            throw new ServiceException("车辆编号不能为空");
        }

        String plateNumber = obj.getStr("plateNumber")==null?"":obj.getStr("plateNumber");
        data.add(plateNumber);

        String categoryId = obj.getStr("categoryId")==null?"":obj.getStr("categoryId");
        data.add(categoryId);

        String brand = obj.getStr("brand")==null?"":obj.getStr("brand");
        data.add(brand);

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

        String personCode = obj.getStr("personCode")==null?"":obj.getStr("personCode");
        data.add(personCode);

        datasList.add(data);

        return datasList;
    }

    /**
     * 根据项目将车辆最新状态同步刷新到MongoDB中
     * @param projectCode
     * @return
     */
    @PostMapping("/deviceStatus/{projectCode}")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "同步项目将车辆状态到Mongo", notes = "根据项目将车辆最新状态同步刷新到MongoDB中")
    public R syncVehicleDeviceStatusToMongo(@PathVariable String projectCode){
        vehicleAsyncService.syncVehicleDeviceStatusToMongo(projectCode);
        return R.status(true);
    }
}
