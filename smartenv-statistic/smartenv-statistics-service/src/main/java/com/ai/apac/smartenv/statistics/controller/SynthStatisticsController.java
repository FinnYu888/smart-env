package com.ai.apac.smartenv.statistics.controller;

import com.ai.apac.smartenv.statistics.dto.*;
import com.ai.apac.smartenv.statistics.service.IDataStatisticsService;
import com.ai.apac.smartenv.statistics.service.IRptPersonInfoService;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/data")
@Api(value = "", tags = "数据统计接口")
public class SynthStatisticsController {

    private IDataStatisticsService dataStatisticsService;

    private IProjectClient projectClient;

    /**
     * 初始化公司下所有项目的数据统计信息
     * @return
     */
    @PostMapping("/initialSynthInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "初始化项目的数据统计信息(此接口是部署在沧州政务云上，用于同步数据过去)", notes = "(此接口是部署在沧州政务云上，用于同步数据过去)")
    public R<Boolean> initialSynthInfo(@RequestBody SynthInfoDTO synthInfoDTO) {
        return R.data(dataStatisticsService.initialSynthInfo(synthInfoDTO));
    }

    /**
     * 同步车辆作业信息
     * @return
     */
    @PostMapping("/vehicleWork/sync")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "同步车辆作业信息", notes = "同步车辆作业信息")
    public R<Boolean> synthVehicleWorkInfo(@RequestBody VehicleWorkSynthInfoDTO vehicleWorkSynthInfoDTO) {

        return R.data(dataStatisticsService.synthVehicleWorkInfo(vehicleWorkSynthInfoDTO));
    }

    /**
     * 同步人员作业信息
     * @return
     */
    @PostMapping("/personWork/sync")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "同步人员作业信息", notes = "同步人员作业信息")
    public R<Boolean> synthPersonWorkInfo(@RequestBody PersonWorkSynthInfoDTO personWorkSynthInfoDTO) {
        return R.data(dataStatisticsService.synthPersonWorkInfo(personWorkSynthInfoDTO));
    }

    /**
     * 同步车辆统计信息
     * @return
     */
    @PostMapping("/vehicleStat/sync")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "同步车辆统计信息", notes = "同步车辆统计信息")
    public R<Boolean> synthVehicleStatInfo(@RequestBody VehicleStatSynthInfoDTO vehicleStatSynthInfoDTO) {
        return R.data(dataStatisticsService.synthVehicleStatInfo(vehicleStatSynthInfoDTO));
    }

    /**
     * 同步人员统计信息
     * @return
     */
    @PostMapping("/personStat/sync")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "同步人员统计信息", notes = "同步人员统计信息")
    public R<Boolean> synthPersonStatInfo(@RequestBody PersonStatSynthInfoDTO personStatSynthInfoDTO) {
        return R.data(dataStatisticsService.synthPersonStatInfo(personStatSynthInfoDTO));
    }



    /**
     * 同步第三方告警信息
     * @return
     */
    @PostMapping("/alarmInfo/sync")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "同步告警信息", notes = "同步告警信息")
    public R<Boolean> synthAlarmInfo(@RequestBody AlarmSynthInfoDTO alarmSynthInfoDTO) {
        return R.data(dataStatisticsService.synthAlarmInfo(alarmSynthInfoDTO));
    }


}
