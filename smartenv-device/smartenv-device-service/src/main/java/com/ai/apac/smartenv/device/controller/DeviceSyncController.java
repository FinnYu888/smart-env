package com.ai.apac.smartenv.device.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.device.service.IDeviceAsyncService;
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
public class DeviceSyncController {


    private IDeviceAsyncService deviceAsyncService;

    /**
     * 导入人员信息表
     */
    @PostMapping("/deviceInfo")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "第三方同步设备信息", notes = "第三方同步设备信息")
    @ApiLog(value = "第三方同步设备信息")
    public R<Boolean> syncDeviceInfo(@RequestBody JSONObject obj) {
        List<List<String>> datasList = initializeData(obj);
        String tenantId = AuthUtil.getTenantId();
        return R.data(deviceAsyncService.thirdDeviceInfoAsync(datasList,tenantId, "",false));
    }

    /**
     * 导入人员信息表
     */
    @DeleteMapping("/deviceInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "第三方删除设备信息", notes = "第三方删除设备信息")
    @ApiLog(value = "第三方删除设备信息")
    public R<Boolean> delDeviceInfo(@RequestBody JSONObject obj) {
        List<List<String>> datasList = initializeData(obj);
        String tenantId = AuthUtil.getTenantId();
        return R.data(deviceAsyncService.thirdDeviceInfoAsync(datasList,tenantId,OmnicConstant.ACTION_TYPE.DELETE,false));
    }


    private List<List<String>> initializeData(JSONObject obj){
        List<List<String>> datasList = new ArrayList<List<String>>();
        List<String> data = new ArrayList<String>();
        if(ObjectUtil.isNotEmpty(obj.getStr("deviceCode"))){
            data.add(obj.getStr("deviceCode"));
        }else{
            throw new ServiceException("设备编号不能为空");
        }

        String deviceName = obj.getStr("deviceName")==null?"":obj.getStr("deviceName");
        data.add(deviceName);

        datasList.add(data);

        return datasList;
    }
}

