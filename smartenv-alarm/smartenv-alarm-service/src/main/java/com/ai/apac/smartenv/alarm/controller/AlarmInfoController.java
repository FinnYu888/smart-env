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

import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.ai.apac.smartenv.alarm.dto.AlarmInfoQueryDTO;
import com.ai.apac.smartenv.alarm.entity.AlarmInfo;
import com.ai.apac.smartenv.alarm.service.IAlarmInfoService;
import com.ai.apac.smartenv.alarm.vo.AlarmAmountVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleResultVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoScreenViewVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoVO;
import com.ai.apac.smartenv.alarm.wrapper.AlarmInfoWrapper;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tenant.constant.TenantConstant;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 告警基本信息表 控制器
 *
 * @author Blade
 * @since 2020-02-18
 */
@RestController
@AllArgsConstructor
@RequestMapping("/alarminfo")
@Api(value = "告警基本信息表", tags = "告警基本信息表接口")
public class AlarmInfoController extends BladeController {

	private IAlarmInfoService alarmInfoService;

	private BaiduMapUtils baiduMapUtils;

	private CoordsTypeConvertUtil coordsTypeConvertUtil;

	/**
	 * 详情
	 */
	@GetMapping("/detail/{coordsType}")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入alarmInfo")
    @ApiLog(value = "查详情")
	public R<AlarmInfoVO> detail(AlarmInfo alarmInfo) throws IOException {
		AlarmInfo detail = alarmInfoService.getOne(Condition.getQueryWrapper(alarmInfo));

//
//		if (coordsType!=null&&coordsType.equals(BaiduMapUtils.CoordsSystem.BD09LL)){
//			List<Coords> coords=new ArrayList<>();
//			List<Coords> result = baiduMapUtils.coordsToBaiduMapllAll(BaiduMapUtils.CoordsSystem.GC02, coords);
//			if (CollectionUtil.isNotEmpty(result)){
//				Coords coords1 = result.get(0);
//				String longitude = coords1.getLongitude();
//				String latitude = coords1.getLatitude();
//				detail.setLongitude(longitude);
//				detail.setLatitudinal(latitude);
//			}
//		}
		List<AlarmInfo> list=new ArrayList<>();
		list.add(detail);
		coordsTypeConvertUtil.toWebConvert(list);

		return R.data(AlarmInfoWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 告警基本信息表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入alarmInfo")
    @ApiLog(value = "根据条件分页查告警信息详情")
	public R<IPage<AlarmInfoHandleInfoVO>> list(AlarmInfoQueryDTO alarmInfoQuery, Query query) {
		// 获取当前登录用户tenantId
		alarmInfoQuery.setTenantId(AuthUtil.getTenantId());
		IPage<AlarmInfoHandleInfoVO> alarmInfoHandleInfoVOIPage = alarmInfoService.listAlarmHandleInfoPage(alarmInfoQuery, query);

		List<AlarmInfoHandleInfoVO> alarmInfoHandleInfoVOS = coordsTypeConvertUtil.toWebConvert(alarmInfoHandleInfoVOIPage.getRecords());
		alarmInfoHandleInfoVOIPage.setRecords(alarmInfoHandleInfoVOS);

		return R.data(alarmInfoHandleInfoVOIPage);
	}


	/**
	 * 自定义分页 告警基本信息表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入alarmInfo")
	public R<IPage<AlarmInfoVO>> page(AlarmInfoVO alarmInfo, Query query) {
		IPage<AlarmInfoVO> pages = alarmInfoService.selectAlarmInfoPage(Condition.getPage(query), alarmInfo);
		List<AlarmInfoVO> alarmInfoHandleInfoVOS = coordsTypeConvertUtil.toWebConvert(pages.getRecords());
		pages.setRecords(alarmInfoHandleInfoVOS);

		return R.data(pages);
	}


	/**
	 * 修改 告警基本信息表
	 */
	@PutMapping
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入alarmInfo")
    @ApiLog(value = "根据条件修改告警信息")
	public R update(@Valid @RequestBody AlarmInfo alarmInfo) {
		return R.status(alarmInfoService.updateById(alarmInfo));
	}

	/**
	 * 新增或修改 告警基本信息表
	 */
	/*@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入alarmInfo")
	public R submit(@Valid @RequestBody AlarmInfo alarmInfo) {
		return R.status(alarmInfoService.saveOrUpdate(alarmInfo));
	}*/


	/**
	 * 删除 告警基本信息表
	 */
	@DeleteMapping
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
    @ApiLog(value = "根据主键逻辑删除告警信息")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(alarmInfoService.deleteLogic(Func.toLongList(ids)));
	}

	/**
	 * 批量处理告警信息 告警基本信息表
	 */
	@PostMapping("/batchHandle")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "批量处理告警信息", notes = "传入AlarmInfoHandleResultVO")
    @ApiLog(value = "批量处理告警信息")
    public R batchHandle(@RequestBody AlarmInfoHandleResultVO alarmInfoHandleResultVO) {
		alarmInfoService.batchHandle(alarmInfoHandleResultVO);
		return R.status(true);
	}

	/**
	 * 导出告警信息查询结果
	 */
	@GetMapping("/export")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "导出excel", notes = "传入查询条件")
    @ApiLog(value = "根据查询条件，导出告警信息查询结果")
	public void exportAlarmInfo(AlarmInfoQueryDTO alarmInfoQuery) {
		// 获取当前登录用户tenantId
		alarmInfoQuery.setTenantId(AuthUtil.getTenantId());
		alarmInfoService.exportAlarmInfo(alarmInfoQuery);
	}

	/**
	 * 查当天所有告警数量
	 * @return
	 */
	@GetMapping("/currentDay/amount")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "查当天所有告警数量", notes = "没有入参")
    @ApiLog(value = "查当天所有告警数量")
	public R<Integer> countAlarmInfoAmount() {
		AlarmInfo alarmInfo = new AlarmInfo();
		alarmInfo.setTenantId(AuthUtil.getTenantId());
		return R.data(alarmInfoService.countAlarmInfoAmount(alarmInfo));
	}

	/**
	 * 查当天所有种类的告警数量
	 * @return
	 */
	@GetMapping("/currentDay/amountDetails")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "查当天所有种类的告警数量", notes = "")
	@ApiLog(value = "查当天所有种类的告警数量")
	public R<AlarmAmountVO> countAllRuleAlarmAmount() {
		AlarmAmountVO alarmAmountVO = new AlarmAmountVO();
		AlarmInfo alarmInfo = new AlarmInfo();
		if(StringUtil.isNotBlank(AuthUtil.getTenantId())){
			alarmInfo.setTenantId(AuthUtil.getTenantId());
		}else{
			alarmInfo.setTenantId(TenantConstant.DEFAULT_TENANT_ID);
		}
		alarmInfo.setParentRuleCategoryId(AlarmConstant.PERSON_ABNORMAL_ALARM_CATEGORY);
		alarmAmountVO.setPersonUnusualAlarmCount(alarmInfoService.countAlarmInfoAmount(alarmInfo));
		alarmInfo.setParentRuleCategoryId(AlarmConstant.PERSON_VIOLATION_ALARM_CATEGORY);
		alarmAmountVO.setPersonViolationAlarmCount(alarmInfoService.countAlarmInfoAmount(alarmInfo));
		alarmInfo.setParentRuleCategoryId(AlarmConstant.VEHICLE_VIOLATION_ALARM_CATEGORY);
		alarmAmountVO.setVehicleViolationAlarmCount(alarmInfoService.countAlarmInfoAmount(alarmInfo));
		alarmInfo.setParentRuleCategoryId(AlarmConstant.VEHICLE_OUT_OF_AREA_ALARM_CATEGORY);
		alarmAmountVO.setVehicleOutOfAreaAlarmCount(alarmInfoService.countAlarmInfoAmount(alarmInfo));
		alarmInfo.setParentRuleCategoryId(AlarmConstant.VEHICLE_OVERSPEED_ALARM_CATEGORY);
		alarmAmountVO.setVehicleSpeedingAlarmCount(alarmInfoService.countAlarmInfoAmount(alarmInfo));
		return R.data(alarmAmountVO);
	}

	/**
	 * 查当天所有种类的告警数量
	 * @return
	 */
	@GetMapping("/lastAlarmInfosDaily")
	@ApiOperationSupport(order = 13)
	@ApiOperation(value = "大屏实时告警信息监控", notes = "大屏实时告警信息监控")
	@ApiLog(value = "大屏实时告警信息监控")
	public R<List<AlarmInfoScreenViewVO>> getLastAlarmInfosDaily(@ApiParam(value = "最大条数", required = true) @RequestParam Long nums) {
		return R.data(alarmInfoService.getLastAlarmInfosDaily(nums,AuthUtil.getTenantId()));
	}

	/**
	 * 根据Id查详情
	 * @param id
	 * @return
	 */
	@GetMapping
	@ApiOperationSupport(order = 11)
	@ApiOperation(value = "根据Id查详情", notes = "传入主键id")
    @ApiLog(value = "根据Id查告警详情")
    public R<AlarmInfoHandleInfoVO> detailById(@RequestParam(value = "id") Long id) throws IOException {

		AlarmInfoHandleInfoVO alarmInfoHandleInfoVO = alarmInfoService.detailByIdFromMongo(id);
//		if (coordsType!=null&&coordsType.equals(BaiduMapUtils.CoordsSystem.BD09LL.value)){
//			List<Coords> coords=new ArrayList<>();
//			Coords coord=new Coords();
//			coord.setLatitude(alarmInfoHandleInfoVO.getLatitudinal());
//			coord.setLongitude(alarmInfoHandleInfoVO.getLongitude());
//			coords.add(coord);
//			List<Coords> result = baiduMapUtils.coordsToBaiduMapllAll(BaiduMapUtils.CoordsSystem.GC02, coords);
//			if (CollectionUtil.isNotEmpty(result)){
//				Coords coords1 = result.get(0);
//				String longitude = coords1.getLongitude();
//				String latitude = coords1.getLatitude();
//				alarmInfoHandleInfoVO.setLongitude(longitude);
//				alarmInfoHandleInfoVO.setLatitudinal(latitude);
//			}
//		}

		List<AlarmInfoHandleInfoVO> alarmInfoHandleInfoVOList=new ArrayList<>();
		alarmInfoHandleInfoVOList.add(alarmInfoHandleInfoVO);
		coordsTypeConvertUtil.toWebConvert(alarmInfoHandleInfoVOList);


		return R.data(CollectionUtil.isNotEmpty(alarmInfoHandleInfoVOList)?alarmInfoHandleInfoVOList.get(0):alarmInfoHandleInfoVO);
	}
}
