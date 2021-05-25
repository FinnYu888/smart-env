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

import com.ai.apac.smartenv.device.vo.DeviceInfoVO;
import com.ai.apac.smartenv.facility.entity.FacilityRel;
import com.ai.apac.smartenv.facility.service.IFacilityRelService;
import com.ai.apac.smartenv.facility.vo.FacilityRelVO;
import com.ai.apac.smartenv.facility.wrapper.FacilityRelWrapper;
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
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-01-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/facilityrel")
@Api(value = "设置绑定终端管理接口", tags = "设置绑定终端管理接口")
public class FacilityRelController extends BladeController {

	private IFacilityRelService facilityRelService;
	//private IDeviceClient deviceClientFeign;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入facilityRel")
	public R<FacilityRelVO> detail(FacilityRel facilityRel) {
		FacilityRel detail = facilityRelService.getOne(Condition.getQueryWrapper(facilityRel));
		return R.data(FacilityRelWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入facilityRel")
	public R<IPage<FacilityRelVO>> list(FacilityRel facilityRel, Query query) {
		IPage<FacilityRel> pages = facilityRelService.page(Condition.getPage(query), Condition.getQueryWrapper(facilityRel));
		return R.data(FacilityRelWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入facilityRel")
	public R<IPage<FacilityRelVO>> page(FacilityRelVO facilityRel, Query query) {
		IPage<FacilityRelVO> pages = facilityRelService.selectFacilityRelPage(Condition.getPage(query), facilityRel);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入facilityRel")
	public R save(@Valid @RequestBody FacilityRel facilityRel) {
		return R.status(facilityRelService.save(facilityRel));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入facilityRel")
	public R update(@Valid @RequestBody FacilityRel facilityRel) {
		return R.status(facilityRelService.updateById(facilityRel));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入facilityRel")
	public R submit(@Valid @RequestBody FacilityRel facilityRel) {
		return R.status(facilityRelService.saveOrUpdate(facilityRel));
	}

	
	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(facilityRelService.deleteLogic(Func.toLongList(ids)));
	}

	/**
	 * 获取中转站绑定设备信息
	 */
	@ApiLog(value = "获取中转站绑定设备信息")
	@GetMapping("/getFacilityRelDevice")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "详情", notes = "传入facilityInfo")
	public R<List<DeviceInfoVO>> getFacilityRelDevice(Long facilityId,String deviceType) {
		FacilityRel facilityRel = new FacilityRel();
		facilityRel.setFacilityId(facilityId);
		if (StringUtil.isNotBlank(deviceType)) {
			facilityRel.setEntityType(deviceType);
		}

		List<FacilityRel> rels = facilityRelService.list(Condition.getQueryWrapper(facilityRel));
		if (null != rels) {
			List<DeviceInfoVO> deviceInfoVOS = new ArrayList<>();
			for (FacilityRel rel:rels) {
				/*DeviceInfo deviceInfo = deviceClientFeign.getDeviceById(rel.getEntityId().toString()).getData();
				if (null != deviceInfo) {
					DeviceInfoVO deviceInfoVO = new DeviceInfoVO();
					BeanUtil.copy(deviceInfo,deviceInfoVO);
					deviceInfoVOS.add(deviceInfoVO);
				}*/
			}
			return R.data(deviceInfoVOS);
		}

		return R.data(null);
	}
	
}
