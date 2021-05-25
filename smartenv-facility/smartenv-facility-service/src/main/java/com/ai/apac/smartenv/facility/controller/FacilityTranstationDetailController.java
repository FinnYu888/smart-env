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
package com.ai.apac.smartenv.facility.controller;

import com.ai.apac.smartenv.common.constant.FacilityConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.facility.entity.FacilityTranstationDetail;
import com.ai.apac.smartenv.facility.entity.GarbageAmountDaily;
import com.ai.apac.smartenv.facility.entity.TranstationEveryDay;
import com.ai.apac.smartenv.facility.service.IFacilityTranstationDetailService;
import com.ai.apac.smartenv.facility.vo.FacilityTransitDetailListVO;
import com.ai.apac.smartenv.facility.vo.FacilityTranstationDetailVO;
import com.ai.apac.smartenv.facility.vo.LastDaysGarbageAmountVO;
import com.ai.apac.smartenv.facility.vo.LastDaysRegionGarbageAmountVO;
import com.ai.apac.smartenv.facility.wrapper.FacilityTranstationDetailWrapper;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tenant.constant.TenantConstant;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-02-14
 */
@RestController
@AllArgsConstructor
@RequestMapping("/facilitytranstationdetail")
@Api(value = "中转站转运信息详情", tags = "中转站转运信息详情接口")
public class FacilityTranstationDetailController extends BladeController {

	private IFacilityTranstationDetailService facilityTranstationDetailService;

	private IDictClient dictClient;

	private ISysClient sysClient;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入facilityTranstationDetail")
	public R<FacilityTranstationDetailVO> detail(FacilityTranstationDetail facilityTranstationDetail) {
		FacilityTranstationDetail detail = facilityTranstationDetailService.getOne(Condition.getQueryWrapper(facilityTranstationDetail));
		return R.data(FacilityTranstationDetailWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入facilityTranstationDetail")
	public R<IPage<FacilityTranstationDetailVO>> list(FacilityTranstationDetail facilityTranstationDetail, Query query) {
		QueryWrapper queryWrapper = Condition.getQueryWrapper(facilityTranstationDetail);
		IPage<FacilityTranstationDetail> pages = facilityTranstationDetailService.page(Condition.getPage(query), Condition.getQueryWrapper(facilityTranstationDetail));
		return R.data(FacilityTranstationDetailWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入facilityTranstationDetail")
	public R<IPage<FacilityTranstationDetailVO>> page(FacilityTranstationDetailVO facilityTranstationDetail, Query query) {
		IPage<FacilityTranstationDetailVO> pages = facilityTranstationDetailService.selectFacilityTranstationDetailPage(Condition.getPage(query), facilityTranstationDetail);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入facilityTranstationDetail")
	public R save(@Valid @RequestBody FacilityTranstationDetail facilityTranstationDetail) {
		return R.status(facilityTranstationDetailService.save(facilityTranstationDetail));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入facilityTranstationDetail")
	public R update(@Valid @RequestBody FacilityTranstationDetail facilityTranstationDetail) {
		return R.status(facilityTranstationDetailService.updateById(facilityTranstationDetail));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入facilityTranstationDetail")
	public R submit(@Valid @RequestBody FacilityTranstationDetail facilityTranstationDetail) {
		return R.status(facilityTranstationDetailService.saveOrUpdate(facilityTranstationDetail));
	}

	
	/**
	 * 删除 
	 */
	@DeleteMapping
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(facilityTranstationDetailService.deleteLogic(Func.toLongList(ids)));
	}
	/**
	*根据条件查询中转站转运记录详情
	*/
	@ApiLog(value = "根据条件查询中转站转运记录详情")
	@PostMapping("/getTranstationDetail")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "根据条件查询中转站详情信息", notes = "传入ids")
	public R getTranstationDetail(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(facilityTranstationDetailService.deleteLogic(Func.toLongList(ids)));
	}
	/**
	 * 根据开始结束时间，查询中转站垃圾转运详细记录
	 */
	@ApiLog(value = "根据开始结束时间，查询中转站垃圾转运详细记录")
	@GetMapping("/listfacilityTranstationDetail")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "根据开始结束时间，查询中转站垃圾转运详细记录", notes = "传入facilityTranstationDetail,startDate,endDate")
	public R<IPage<FacilityTranstationDetailVO>> listfacilityTranstationDetail( FacilityTranstationDetail facilityTranstationDetail,Query query, @RequestParam(required = false) String startDate,
																				@RequestParam(required = false) String endDate,@RequestParam(required = false) String garbageType ){

		IPage pages = facilityTranstationDetailService.listfacilityTranstationDetail(Condition.getPage(query), facilityTranstationDetail.getFacilityId(),startDate,endDate,garbageType);
		List<FacilityTranstationDetail> details = pages.getRecords();
		if (null != details && details.size()>0) {
			Map<String,Object> map = dictClient.getMap(FacilityConstant.GarbageType.CODE).getData();
			List<FacilityTranstationDetailVO> detailVOS = new ArrayList<>();
			for (FacilityTranstationDetail detail : details) {
				FacilityTranstationDetailVO detailVO = new FacilityTranstationDetailVO();
				BeanUtil.copy(detail,detailVO);
				if (StringUtil.isNotBlank(detail.getGarbageType())) {
					Object garbtype = map.get(detail.getGarbageType());
					if (null != garbtype) 	detailVO.setGarbageTypeName(garbtype.toString());
				}

				detailVOS.add(detailVO);
			}
			pages.setRecords(detailVOS);
		}

		return R.data(pages);
	}
	/**
	 * 根据开始结束时间，按天 统计中转站垃圾转运详细记录
	 */
	@ApiLog(value = "根据开始结束时间，按天 统计中转站垃圾转运详细记录")
	@GetMapping("/listranstationDetailEveryDay")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "根据开始结束时间，按天 统计中转站垃圾转运详细记录", notes = "传入facilityTranstationDetail,startDate,endDate")
	public R<FacilityTransitDetailListVO> listranstationDetailEveryDay(FacilityTranstationDetailVO facilityTranstationDetail, Query query, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, BladeUser user) {


		if (null == endDate) {
			endDate = TimeUtil.getYYYYMMDDHHMMSS(TimeUtil.getSysDate());
		}
        FacilityTransitDetailListVO returnVO = new FacilityTransitDetailListVO();
		//按天统计记录数
		IPage<TranstationEveryDay> transtationEveryDayIPage = facilityTranstationDetailService.staticsTranstationEveryDay(Condition.getPage(query),facilityTranstationDetail.getFacilityId(),startDate,endDate);
		if (null == transtationEveryDayIPage || null == transtationEveryDayIPage.getRecords() || 0 == transtationEveryDayIPage.getRecords().size()) {
			returnVO.setGarbageWeightTotal("0");
			returnVO.setTransferTimesTotal(0);
			returnVO.setHarmfulGarbageWeight("0");
			returnVO.setKitchenGarbageWeight("0");
			returnVO.setRecyclableGarbageWeight("0");
			returnVO.setOtherGarbageWeight("0");
		    return R.data(returnVO);
        }

		//根据条件统计总数量
		QueryWrapper totalWrapper = new QueryWrapper();
		totalWrapper.select(	"  SUM(garbage_weight) garbage_weight","SUM(transfer_times) transfer_times",
				"SUM(CASE GARBAGE_TYPE WHEN '1' THEN GARBAGE_WEIGHT ELSE 0 END) TENANT_ID",
				"SUM(CASE GARBAGE_TYPE WHEN '2' THEN GARBAGE_WEIGHT ELSE 0 END) ODOR_LEVEL",
				"SUM(CASE GARBAGE_TYPE WHEN '3' THEN GARBAGE_WEIGHT ELSE 0 END) OP_ID",
				"SUM(CASE GARBAGE_TYPE WHEN '4' THEN GARBAGE_WEIGHT ELSE 0 END) ORG_ID");
		totalWrapper.ge("transfer_time",startDate);
		totalWrapper.le("transfer_time",endDate);
		totalWrapper.eq("facility_Id",facilityTranstationDetail.getFacilityId());
		if (null != user) totalWrapper.eq("tenant_id",user.getTenantId());
		FacilityTranstationDetail detailTotal = facilityTranstationDetailService.getOne(totalWrapper);
		returnVO.setDetailIPage(transtationEveryDayIPage);
		if (null != detailTotal  ) {
			DecimalFormat decimalFormat=new DecimalFormat("0.00");
			returnVO.setGarbageWeightTotal(decimalFormat.format(Func.toFloat(detailTotal.getGarbageWeight())));
			returnVO.setTransferTimesTotal(detailTotal.getTransferTimes());
			returnVO.setHarmfulGarbageWeight(decimalFormat.format(Func.toFloat(detailTotal.getOpId())));
			returnVO.setKitchenGarbageWeight(decimalFormat.format(Func.toFloat(detailTotal.getTenantId())));
			returnVO.setRecyclableGarbageWeight(decimalFormat.format(Func.toFloat(detailTotal.getOdorLevel())));
			returnVO.setOtherGarbageWeight(decimalFormat.format(Func.toFloat(detailTotal.getOrgId())));
		}else {
			returnVO.setGarbageWeightTotal("0");
			returnVO.setTransferTimesTotal(0);
			returnVO.setHarmfulGarbageWeight("0");
			returnVO.setKitchenGarbageWeight("0");
			returnVO.setRecyclableGarbageWeight("0");
			returnVO.setOtherGarbageWeight("0");
		}

		return R.data(returnVO);
	}


	@ApiLog(value = "最近N天垃圾收集吨数")
	@GetMapping("/lastDaysGarbageAmount")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "最近N天垃圾收集吨数", notes = "最近N天垃圾收集吨数")
	public R<List<LastDaysGarbageAmountVO>> lastDaysGarbageAmount(@RequestParam(required = true) Integer days) {

		final String tenantId = StringUtil.isNotBlank(AuthUtil.getTenantId())?AuthUtil.getTenantId():TenantConstant.DEFAULT_TENANT_ID;

		List<LastDaysGarbageAmountVO> lastDaysGarbageAmountVOList = new ArrayList<LastDaysGarbageAmountVO>();
		List<Dict> dictList = dictClient.getList("GarbageType").getData();
		Timestamp endDate = TimeUtil.getSysDate();
		Timestamp startDate = TimeUtil.getStartTime(TimeUtil.addOrMinusDays(endDate.getTime(),-days+1));
		dictList.forEach(dict -> {
			LastDaysGarbageAmountVO lastDaysGarbageAmountVO = new LastDaysGarbageAmountVO();
			List<GarbageAmountDaily> garbageAmountDailyList = facilityTranstationDetailService.lastDaysGarbageAmount(dict.getDictKey(),TimeUtil.getYYYY_MM_DD_HH_MM_SS(startDate),TimeUtil.getYYYY_MM_DD_HH_MM_SS(endDate),tenantId);
			lastDaysGarbageAmountVO.setGarbageAmountDailyList(garbageAmountDailyList);
			lastDaysGarbageAmountVO.setGarbageTypeId(dict.getDictKey());
			lastDaysGarbageAmountVO.setGarbageTypeName(dict.getDictValue());
			lastDaysGarbageAmountVOList.add(lastDaysGarbageAmountVO);
		});
		return R.data(lastDaysGarbageAmountVOList);
	}

	@ApiLog(value = "最近N天各区域收集总数统计")
	@GetMapping("/lastDaysGarbageAmountGroupByRegion")
	@ApiOperationSupport(order = 11)
	@ApiOperation(value = "最近N天各区域收集总数统计", notes = "最近N天各区域收集总数统计")
	public R<List<LastDaysRegionGarbageAmountVO>> lastDaysGarbageAmountGroupByRegion(@RequestParam(required = true) Integer days) {
		String tenantId = TenantConstant.DEFAULT_TENANT_ID;
		if(StringUtil.isNotBlank(AuthUtil.getTenantId())){
			tenantId = AuthUtil.getTenantId();
		}

		Timestamp endDate = TimeUtil.getSysDate();
		Timestamp startDate = TimeUtil.getStartTime(TimeUtil.addOrMinusDays(endDate.getTime(),-days+1));
		List<LastDaysRegionGarbageAmountVO> lastDaysRegionGarbageAmountVOList  = facilityTranstationDetailService.lastDaysGarbageAmountGroupByRegion(TimeUtil.getYYYY_MM_DD_HH_MM_SS(startDate),TimeUtil.getYYYY_MM_DD_HH_MM_SS(endDate),tenantId);

		lastDaysRegionGarbageAmountVOList.forEach(lastDaysRegionGarbageAmountVO -> {
			Region region = sysClient.getRegion(Long.parseLong(lastDaysRegionGarbageAmountVO.getRegionId())).getData();
			if(ObjectUtil.isNotEmpty(region)){
				lastDaysRegionGarbageAmountVO.setRegionName(region.getRegionName());
			}
		});

		return R.data(lastDaysRegionGarbageAmountVOList);
	}

}
