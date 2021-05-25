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
package com.ai.apac.smartenv.alarm.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.ai.apac.smartenv.alarm.dto.AlarmRuleInfoDTO;
import com.ai.apac.smartenv.alarm.dto.VehicleTypeQueryDTO;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleExtService;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleInfoService;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleRelService;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleInfoVO;
import com.ai.apac.smartenv.alarm.wrapper.AlarmRuleInfoWrapper;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 告警规则基本信息表 控制器
 *
 * @author Blade
 * @since 2020-02-15
 */
@RestController
@AllArgsConstructor
@RequestMapping("/alarmruleinfo")
@Api(value = "告警规则基本信息表", tags = "告警规则基本信息表接口")
public class AlarmRuleInfoController extends BladeController {

	private IAlarmRuleInfoService alarmRuleInfoService;

	private IAlarmRuleExtService alarmRuleExtService;

	private IAlarmRuleRelService alarmRuleRelService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入alarmRuleInfo")
	@ApiLog(value = "根据条件查询告警规则详情")
	public R<AlarmRuleInfoVO> detail(AlarmRuleInfo alarmRuleInfo) {
		AlarmRuleInfo detail = alarmRuleInfoService.getOne(Condition.getQueryWrapper(alarmRuleInfo));
		return R.data(AlarmRuleInfoWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 告警规则基本信息表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入alarmRuleInfo")
	@ApiLog(value = "根据条件分页查询规则信息详情")
	public R<IPage<AlarmRuleInfoVO>> list(AlarmRuleInfo alarmRuleInfo, Query query) {
		IPage<AlarmRuleInfo> pages = alarmRuleInfoService.page(Condition.getPage(query), Condition.getQueryWrapper(alarmRuleInfo));
		return R.data(AlarmRuleInfoWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 告警规则基本信息表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入alarmRuleInfo")
	public R<IPage<AlarmRuleInfoVO>> page(AlarmRuleInfoVO alarmRuleInfo, Query query) {
		IPage<AlarmRuleInfoVO> pages = alarmRuleInfoService.selectAlarmRuleInfoPage(Condition.getPage(query), alarmRuleInfo);
		return R.data(pages);
	}

	/**
	 * 获取告警规则及参数，用于前端展示
	 */
	@GetMapping("/param")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "取告警规则及参数", notes = "告警类型编码")
	@ApiLog(value = "获取告警规则及参数，用于前端展示")
	public R constructNewAlarmRuleInfo(@RequestParam Long alarmEntityCategoryId) {
		AlarmRuleInfoVO alarmRuleInfoVO = new AlarmRuleInfoVO();
		alarmRuleInfoVO.setEntityCategoryId(alarmEntityCategoryId);
		alarmRuleInfoVO.setTenantId(AuthUtil.getTenantId());
		return R.data(alarmRuleInfoService.constructNewAlarmRuleInfo(alarmRuleInfoVO));
	}

	/**
	 * 修改 告警规则基本信息表
	 */
	@PutMapping
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "新增或修改告警规则", notes = "传入alarmRuleInfo")
	@ApiLog(value = "新增或修改告警规则")
	public R saveOrUpdate(@Valid @RequestBody AlarmRuleInfoVO alarmRuleInfoVO) {
		return R.status(alarmRuleInfoService.saveOrUpdateAlarmRuleInfo(alarmRuleInfoVO));
	}

	/**
	 * 新增或修改 告警规则基本信息表
	 */
/*	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入alarmRuleInfo")
	public R submit(@Valid @RequestBody AlarmRuleInfo alarmRuleInfo) {
		return R.status(alarmRuleInfoService.saveOrUpdate(alarmRuleInfo));
	}*/

	
	/**
	 * 删除 告警规则基本信息表
	 */
	@DeleteMapping
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@ApiLog(value = "逻辑删除告警规则")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		List<Long> idList = Func.toLongList(ids);
		List<AlarmRuleInfo> alarmRuleInfos = alarmRuleInfoService.listByIds(idList);
		if (CollectionUtil.isNotEmpty(alarmRuleInfos)) {
			alarmRuleInfos.forEach(alarmRuleInfo -> {
				//  状态设置成删除
				alarmRuleInfo.setIsDeleted(BladeConstant.DB_IS_DELETED);
				boolean success = alarmRuleInfoService.removeById(alarmRuleInfo.getId());
				if (success) {
					alarmRuleInfoService.postAlarmRuleDataToBigData(AlarmRuleInfoWrapper.build().entityVO(alarmRuleInfo), BigDataHttpClient.OptFlag.REMOVE); // // 同步大数据
					alarmRuleExtService.removeByAlarmRuleId(alarmRuleInfo.getId()); // 逻辑删除扩展信息
					alarmRuleRelService.removeAlarmRelByAlarmRuleId(AlarmRuleInfoWrapper.build().entityVO(alarmRuleInfo)); // 逻辑删除传感器关联关系
				}
			});
		}
		return R.status(true);
	}


	/**
	 * 详情及详情扩展表
	 */
	@GetMapping("/details/detailexts")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "查询告警规则详情及其扩展信息并分页", notes = "传入alarmRuleInfo")
	@ApiLog(value = "查询告警规则详情及其扩展信息并分页")
	public R<IPage<AlarmRuleInfoVO>> detailsAndDetailExts(AlarmRuleInfoDTO alarmRuleInfo, Query query) {
		alarmRuleInfo.setTenantId(AuthUtil.getTenantId());
		return R.data(alarmRuleInfoService.detailsAndDetailExts(alarmRuleInfo, query));
	}

	/**
	 * 停用或者启用告警规则
	 * 大数据协商停用时同步给他们删除，启用再同步数据过去
	 */
	@PostMapping("/enableOrDisable")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "停用，启用规则", notes = "传入id, status")
	@ApiLog(value = "停用，启用规则")
	public R enableOrDisableRule(@RequestBody AlarmRuleInfo alarmRuleInfo) {
		return R.status(alarmRuleInfoService.enableOrDisableRule(alarmRuleInfo.getId(), alarmRuleInfo.getStatus()));
	}

	/**
	 * 复制默认租户的告警规则数据到新租户，可指定具体某一条规则
	 * @param alarmRuleInfo
	 * @return
	 */
	@PostMapping("/copyRule")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "复制告警规则", notes = "传入租户，告警规则Id")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "tenantId", value = "指定租户Id", paramType = "query", dataType = "string", required = true),
			@ApiImplicitParam(name = "id", value = "告警规则Id", paramType = "query", dataType = "string")
	})
	@ApiLog(value = "复制告警规则")
	@Deprecated
	public R copyDefaultAlarmRule4SpecifiedTenantOrRuleId(@ApiIgnore @RequestBody AlarmRuleInfo alarmRuleInfo) {
        @NotEmpty(message = "租户Id不能为空") String tenantId = alarmRuleInfo.getTenantId();
//		if (!AuthUtil.isAdministrator()) {
//			throw new ServiceException("复制告警规则的用户需要有管理员角色！");
//		}
        return R.status(alarmRuleInfoService.copyDefaultAlarmRule4SpecifiedTenantOrRuleId(tenantId, alarmRuleInfo.getId()));
	}

	@GetMapping("/alarmType")
	@ApiOperationSupport(order = 11)
	@ApiOperation(value = "查询可配置的告警规则类型", notes = "传入是车辆还是人员告警规则")
	@ApiLog(value = "传入是车辆还是人员告警规则")
	public R listAvailableAlarmTypes(@RequestParam String alarmTypeCode, @RequestParam(value = "isSearch", defaultValue = "false") Boolean isSearch) {
		return R.data(alarmRuleInfoService.listAvailableAlarmTypes(AuthUtil.getTenantId(), alarmTypeCode, isSearch));
	}
	
	@PostMapping("/vehicleType")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "查询可绑定的车辆类型", notes = "传入告警类型Id")
	@ApiLog(value = "查询可绑定的车辆类型")
	public R listAvailableVehicleType(@RequestBody VehicleTypeQueryDTO vehicleTypeQueryDTO) {
		vehicleTypeQueryDTO.setTenantId(AuthUtil.getTenantId());
		return R.data(alarmRuleInfoService.listAvailableVehicleCategory(vehicleTypeQueryDTO));
	}
}
