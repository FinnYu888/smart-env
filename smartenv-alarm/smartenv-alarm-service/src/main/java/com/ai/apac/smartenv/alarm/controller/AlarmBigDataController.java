package com.ai.apac.smartenv.alarm.controller;

import com.ai.apac.smartenv.alarm.entity.AlarmInfo;
import com.ai.apac.smartenv.alarm.service.IAlarmInfoService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.logger.BladeLogger;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: AlarmBigDataController
 * @Description: 提供给大数据调用的
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/12  20:10    panfeng          v1.0.0             修改原因
 */
@RestController
@AllArgsConstructor
@RequestMapping("/alarm_bigdata")
public class AlarmBigDataController extends BladeController {


    private BladeLogger bladeLogger;

    private IAlarmInfoService alarmInfoService;

    /**
     * 新增 告警基本信息表
     * 大数据同步数据需要带上token，这样才能区分租户
     */
    @PostMapping
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "新增", notes = "传入alarmInfo")
    @ApiLog(value = "大数据同步告警信息")
    public R save(@Valid @RequestBody AlarmInfo alarmInfo) throws Exception {
        bladeLogger.info("sync alarm info",alarmInfo.getData());
        return R.status(alarmInfoService.handleBigDataAlarmInfo(alarmInfo));
    }

    @GetMapping
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "测试刷mongodb", notes = "")
    @ApiLog(value = "从数据库中刷数据到mongodb")
    public R pushDataToMongodb() {
        return R.status(alarmInfoService.pushDataToMongodb());
    }
}
