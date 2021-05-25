package com.ai.apac.smartenv.omnic.controller;

import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.enums.PersonStatusEnum;
import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.omnic.dto.DeviceWorkAreaDTO;
import com.ai.apac.smartenv.omnic.service.PolymerizationService;
import com.ai.apac.smartenv.websocket.feign.IBigScreenDataClient;
import com.ai.apac.smartenv.websocket.feign.IHomeDataClient;
import com.ai.apac.smartenv.websocket.feign.IPolymerizationDataClient;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: PolymerizationController
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/12  15:53    panfeng          v1.0.0             修改原因
 */
@RestController
@RequestMapping("/polymerization")
@AllArgsConstructor
@Slf4j
@Api(value = "聚合数据", tags = "聚合数据")
public class PolymerizationController {

    @Autowired
    private PolymerizationService polymerizationService;

    @Autowired
    private IDeviceClient deviceClient;

    @Autowired
    private IHomeDataClient homeDataClient;

    @Autowired
    private IBigScreenDataClient bigScreenDataClient;

    @Autowired
    private IDeviceRelClient deviceRelClient;

    @Autowired
    private IScheduleClient scheduleClient;


    @Autowired
    private IPolymerizationDataClient polymerizationDataClient;


    @GetMapping("/initCount")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "综合大屏初始化数字", notes = "综合大屏初始化数字")
    public R<Boolean> updatePolymerizationCount(@RequestParam String entityType) {
        String tenantId = AuthUtil.getTenantId();
        return polymerizationDataClient.updatePolymerizationCountRedis(tenantId, entityType);
    }

    @GetMapping("/workStatusChange")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "更新工作状态", notes = "更新工作状态")
    public R<Boolean> updateWorkType(@RequestParam Integer workAreaType, @RequestParam String deviceCode) {
        log.info("updateWorkType Params[workAreaType={},deviceCode={}]", workAreaType, deviceCode);
        return R.status(polymerizationService.updateWorkStatus(workAreaType,deviceCode));
    }

    @PostMapping("/batchWorkStatusChange")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "批量更新工作状态", notes = "批量更新工作状态")
    public R<Boolean> batchUpdateWorkType(@RequestBody List<DeviceWorkAreaDTO> deviceList) {
        return R.status(polymerizationService.batchUpdateWorkStatus(deviceList));
    }


    @GetMapping("/initSynthInfo")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "初始化公司下所有项目的数据统计信息", notes = "初始化公司下所有项目的数据统计信息")
    public R<Boolean> initSynthInfo(@RequestParam String tenantId) {
       polymerizationService.initSynthInfo(tenantId);
       return R.data(true);
    }

}
