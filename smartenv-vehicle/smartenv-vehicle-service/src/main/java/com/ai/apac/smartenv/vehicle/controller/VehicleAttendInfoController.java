/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.vehicle.controller;

import io.swagger.annotations.Api;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.vehicle.entity.VehicleAttendInfo;
import com.ai.apac.smartenv.vehicle.vo.VehicleAttendInfoVO;
import com.ai.apac.smartenv.vehicle.wrapper.VehicleAttendInfoWrapper;
import com.ai.apac.smartenv.vehicle.service.IVehicleAttendInfoService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 车辆出勤信息表 控制器
 *
 * @author Blade
 * @since 2020-01-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/vehicleattendinfo")
@Api(value = "车辆出勤信息表", tags = "车辆出勤信息表接口")
public class VehicleAttendInfoController extends BladeController {

    private IVehicleAttendInfoService vehicleAttendInfoService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入vehicleAttendInfo")
    public R<VehicleAttendInfoVO> detail(VehicleAttendInfo vehicleAttendInfo) {
        VehicleAttendInfo detail = vehicleAttendInfoService.getOne(Condition.getQueryWrapper(vehicleAttendInfo));
        return R.data(VehicleAttendInfoWrapper.build().entityVO(detail));
    }

    /**
     * 分页 车辆出勤信息表
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入vehicleAttendInfo")
    public R<IPage<VehicleAttendInfoVO>> list(VehicleAttendInfo vehicleAttendInfo, Query query) {
        IPage<VehicleAttendInfo> pages = vehicleAttendInfoService.page(Condition.getPage(query), Condition.getQueryWrapper(vehicleAttendInfo));
        return R.data(VehicleAttendInfoWrapper.build().pageVO(pages));
    }


    /**
     * 自定义分页 车辆出勤信息表
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "分页", notes = "传入vehicleAttendInfo")
    public R<IPage<VehicleAttendInfoVO>> page(VehicleAttendInfoVO vehicleAttendInfo, Query query) {
        IPage<VehicleAttendInfoVO> pages = vehicleAttendInfoService.selectVehicleAttendInfoPage(Condition.getPage(query), vehicleAttendInfo);
        return R.data(pages);
    }

    /**
     * 新增 车辆出勤信息表
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入vehicleAttendInfo")
    public R save(@Valid @RequestBody VehicleAttendInfo vehicleAttendInfo) {
        return R.status(vehicleAttendInfoService.save(vehicleAttendInfo));
    }

    /**
     * 修改 车辆出勤信息表
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入vehicleAttendInfo")
    public R update(@Valid @RequestBody VehicleAttendInfo vehicleAttendInfo) {
        return R.status(vehicleAttendInfoService.updateById(vehicleAttendInfo));
    }

    /**
     * 新增或修改 车辆出勤信息表
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入vehicleAttendInfo")
    public R submit(@Valid @RequestBody VehicleAttendInfo vehicleAttendInfo) {
        return R.status(vehicleAttendInfoService.saveOrUpdate(vehicleAttendInfo));
    }


    /**
     * 删除 车辆出勤信息表
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(vehicleAttendInfoService.deleteLogic(Func.toLongList(ids)));
    }


}
