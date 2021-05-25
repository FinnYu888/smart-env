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
package com.ai.apac.smartenv.workarea.controller;

import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.common.constant.WorkAreaConstant;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.vo.PersonNode;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.vo.VehicleNode;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.service.IWorkareaInfoService;
import com.ai.apac.smartenv.workarea.vo.*;
import io.swagger.annotations.Api;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.wrapper.WorkareaRelWrapper;
import com.ai.apac.smartenv.workarea.service.IWorkareaRelService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.List;

/**
 * 工作区域关联表 控制器
 *
 * @author Blade
 * @since 2020-01-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/workarearel")
@Api(value = "工作区域关联表", tags = "工作区域关联表接口")
public class WorkareaRelController extends BladeController {

	private IWorkareaRelService workareaRelService;

	private IWorkareaInfoService workareaInfoService;

	private ISysClient sysClient;
	private BaiduMapUtils baiduMapUtils;


	private CoordsTypeConvertUtil coordsTypeConvertUtil;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入workareaRel")
	@ApiLog("查详情")
	public R<WorkareaRelVO> detail(WorkareaRel workareaRel) {
		WorkareaRel detail = workareaRelService.getOne(Condition.getQueryWrapper(workareaRel));
		return R.data(WorkareaRelWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 工作区域关联表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入workareaRel")
	@ApiLog("分页查询工作区域关联表")
	public R<IPage<WorkareaRelVO>> list(WorkareaRel workareaRel, Query query) {
		IPage<WorkareaRel> pages = workareaRelService.page(Condition.getPage(query), Condition.getQueryWrapper(workareaRel));
		return R.data(WorkareaRelWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 工作区域关联表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入workareaRel")
	@ApiLog("默认分页查询")
	public R<IPage<WorkareaRelVO>> page(WorkareaRelVO workareaRel, Query query) {
		IPage<WorkareaRelVO> pages = workareaRelService.selectWorkareaRelPage(Condition.getPage(query), workareaRel);
		return R.data(pages);
	}

	/**
	 * 新增 工作区域关联表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入workareaRel")
	@ApiLog("默认保存方法")
	public R save(@Valid @RequestBody WorkareaRel workareaRel) {
		return R.status(workareaRelService.save(workareaRel));
	}

	/**
	 * 修改 工作区域关联表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入workareaRel")
	@ApiLog("默认更新方法")
	public R update(@Valid @RequestBody WorkareaRel workareaRel) {
		return R.status(workareaRelService.updateById(workareaRel));
	}

	/**
	 * 新增或修改 工作区域关联表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入workareaRel")
	@ApiLog("默认submit方法")
	public R submit(@Valid @RequestBody WorkareaRel workareaRel) {
		return R.status(workareaRelService.saveOrUpdate(workareaRel));
	}

	
	/**
	 * 删除 工作区域关联表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@ApiLog("默认删除方法")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(workareaRelService.deleteLogic(Func.toLongList(ids)));
	}


	/**
	 * 绑定，解绑工作区域或路线与车辆或人员的关系
	 * 原本解绑绑定都用这个接口，讨论后觉得分开，此接口目前仍支持绑定，解绑时若将数据传全也可用此接口
	 */
	@PostMapping("/bindOrUnbind")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "绑定，解绑", notes = "传入workareaRelList")
	@ApiLog("绑定解绑工作区域与车辆或人员的关系")
	public R bindOrUnbind(@RequestBody List<WorkareaRel> workareaRelList, BladeUser bladeUser) throws Exception {
		return R.status(workareaInfoService.bindOrUnbind(workareaRelList, bladeUser));
	}

	/**
	 * 绑定车辆或人员与工作区域或路线的关系
	 */
	@PostMapping("/bindOrUnbindWorkareas")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "车辆或人员绑定路线", notes = "workareaIds，workareaRel")
	@ApiLog("车辆或人员绑定路线")
	public R bindWorkareas(@ApiParam(value = "路线或区域id列表", required = true)  @RequestParam List<String> workareaIds,
						   @RequestBody WorkareaRel workareaRel) throws Exception {
		return R.status(workareaInfoService.bindWorkareas(workareaIds,workareaRel));
	}

	/**
	 * 重新绑定当前车辆或人员与路线或区域的关系
	 */
	@PostMapping("/reBindWorkareas")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "车辆或人员重新绑定路线或区域", notes = "ids，workareaRel")
	@ApiLog("车辆或人员重新绑定路线或区域")
	public R reBindWorkarea(@ApiParam(value = "路线或区域id列表", required = true)  @RequestParam List<String> ids,
							@RequestBody WorkareaRel workareaRel) throws Exception {
		return R.status(workareaInfoService.reBindWorkarea(ids,workareaRel));
	}

	/**
	 * 解绑车辆或人员与路线或区域的关系
	 */
	@PostMapping("/unbindWorkareas")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "解绑车辆或人员与路线或区域的关系", notes = "ids")
	@ApiLog("解绑车辆或人员与路线或区域的关系")
	public R unbindWorkareas(@ApiParam(value = "区域或路线id列表", required = true)  @RequestParam List<String> ids,
							 @RequestBody WorkareaRel workareaRel) throws Exception {
		return R.status(workareaInfoService.unbindWorkareas(ids,workareaRel));
	}

	/**
	 * 分页  查询车辆或人员绑定的工作区域信息列表
	 */
	@GetMapping("/getListByEntityId")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "分页查询人员或车辆绑定的区域或路线", notes = "传入workareaRel,workareaInfo")
	@ApiLog("分页查询人员或车辆绑定的区域或路线")
	public R<IPage<WorkareaInfoVO>> getListByEntityId(WorkareaRel workareaRel, WorkareaInfo workareaInfo, Query query, BladeUser user) {
		String tanetId = user.getTenantId();
		workareaRel.setTenantId(tanetId);
		List<WorkareaInfoVO> workareaInfoVOList = workareaInfoService.getWorkAreaInfoPages(workareaRel,workareaInfo);
		int count = workareaInfoVOList.size();
		IPage<WorkareaInfoVO> page = Condition.getPage(query);
		Double pages = Math.ceil((double) page.getTotal() / (double) page.getSize());
		page.setPages(pages.longValue());
		page.setTotal(count);
		int start = ((int)page.getCurrent() - 1) * (int)page.getSize();
		page.setRecords(workareaInfoVOList.subList(start,count-start > page.getSize()?start+(int)page.getSize() :count));

		for (WorkareaInfoVO record : page.getRecords()) {
			record.setDivisionName(sysClient.getRegion(record.getDivision()).getData().getRegionName());
			if(record.getAreaType() == 1L) {
				record.setWorkAreaName(DictCache.getValue("road_type",String.valueOf(record.getWorkAreaType())));
			}else if(record.getAreaType() == 2L) {
				record.setWorkAreaName(DictCache.getValue("area_type",String.valueOf(record.getWorkAreaType())));
			}

		}

		return R.data(page);
	}

	/**
	 * 查询人员所属区域
	 */
	@GetMapping("/getAreaListByPersonId")
	@ApiOperationSupport(order = 11)
	@ApiOperation(value = "查询人员所属区域", notes = "传入entityId")
	@ApiLog("查询人员所属区域")
	public R<List<WorkareaViewVO>> getAreaListByPersonId(String entityId, BladeUser user) throws Exception {
		String tanetId = user.getTenantId();
		List<WorkareaViewVO> workareaInfoVOList = workareaInfoService.getAreaListByPersonId(entityId,tanetId);

		return R.data(workareaInfoVOList);
	}

	/**
	 * 根据部门和工作区域查询用户，标识出已绑定工作区域或路线的用户
	 */
	@PostMapping("/userInfoByAreaIdAndDeptId")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "查询部门人员列表", notes = "传入部门id和工作区域id")
	@ApiLog("查询部门人员列表")
	public R<List<Person>> userInfoByAreaIdAndDeptId(@ApiParam(value = "部门id", required = true) @RequestParam String deptId,
												   @ApiParam(value = "工作区域或路线id") @RequestParam String workareaId,
												   @ApiParam(value = "关联实体类型", required = false) @RequestParam String entityType) throws Exception {
		List<Person> list = workareaRelService.userInfoByAreaIdAndDeptId(deptId, workareaId, entityType);
		return R.data(list);
	}

	/**
	 * 根据部门和工作区域查询车辆，标识出已绑定工作区域或路线的车辆
	 */
	@PostMapping("/vehicleInfoByAreaIdAndDeptId")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "查询部门车辆列表", notes = "传入部门id和工作区域id")
	@ApiLog("查询部门车辆列表")
	public R<List<VehicleVO>> vehicleInfoByAreaIdAndDeptId(@ApiParam(value = "部门id", required = true) @RequestParam String deptId,
															 @ApiParam(value = "工作区域或路线id", required = false) @RequestParam String workareaId,
															 @ApiParam(value = "关联实体类型", required = false) @RequestParam String entityType) throws Exception {
		List<VehicleVO> list = workareaRelService.vehicleInfoByAreaIdAndDeptId(deptId, workareaId, entityType);
		return R.data(list);
	}
	
	/**
	 * 全加载，工作区域查询用户，标识出已绑定工作区域或路线的用户
	 */
	@PostMapping("/userInfoByAreaId")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "查询部门人员列表", notes = "工作区域id")
	@ApiLog("查询部门人员列表")
	public R<List<PersonNode>> userInfoByAreaId(@RequestParam String workareaId, PersonNode personNode, BladeUser user) throws Exception {
		String entityType = WorkAreaConstant.WorkareaRelEntityType.PERSON;
		List<PersonNode> list = workareaRelService.userInfoByAreaId(workareaId, entityType, user.getTenantId(), personNode.getNodeName());
		return R.data(list);
	}

	/**
	 * 全加载，工作区域查询车辆，标识出已绑定工作区域或路线的车辆
	 */
	@PostMapping("/vehicleInfoByAreaId")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "查询部门车辆列表", notes = "工作区域id")
	@ApiLog("查询部门车辆列表")
	public R<List<VehicleNode>> vehicleInfoByAreaId(@RequestParam String workareaId, VehicleNode vehicleNode, BladeUser user) throws Exception {
		String entityType = WorkAreaConstant.WorkareaRelEntityType.VEHICLE;
		List<VehicleNode> list = workareaRelService.vehicleInfoByAreaId(workareaId, entityType, user.getTenantId(), vehicleNode.getNodeName());
		return R.data(list);
	}

	/**
	 * 事件上报查询区域已绑定人员
	 */
	@PostMapping("/eventPerson")
	@ApiOperationSupport(order = 11)
	@ApiOperation(value = "事件上报查询区域已绑定人员", notes = "传入部门id和工作区域id")
	@ApiLog("事件上报查询区域已绑定人员")
	public R<List<UserVO>> eventPerson(@ApiParam(value = "工作区域或路线id") @RequestParam String workareaId,
											@ApiParam(value = "关联实体类型", required = false) @RequestParam String entityType) throws Exception {
		List<UserVO> list = workareaInfoService.eventPerson( workareaId);
		return R.data(list);
	}

	/**
	 * 查询已绑定人员
	 */
	@PostMapping("/boundUser")
	@ApiOperationSupport(order = 11)
	@ApiOperation(value = "查询已绑定人员", notes = "传入部门id和工作区域id")
	@ApiLog("查询已绑定人员")
	public R<List<BoundPersonVO>> boundUser(@ApiParam(value = "工作区域或路线id") @RequestParam String workareaId,
												   @ApiParam(value = "关联实体类型", required = false) @RequestParam String entityType) throws Exception {
		List<BoundPersonVO> list = workareaRelService.boundUser( workareaId, entityType);
		return R.data(list);
	}

	/**
	 * 查询已绑定车辆
	 */
	@PostMapping("/boundVehicle")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "查询已绑定车辆", notes = "传入部门id和工作区域id")
	@ApiLog("查询已绑定车辆")
	public R<List<BoundVehicleVO>> boundVehicle(@ApiParam(value = "工作区域或路线id", required = false) @RequestParam String workareaId,
											@ApiParam(value = "关联实体类型", required = false) @RequestParam String entityType) throws Exception {
		List<BoundVehicleVO> list = workareaRelService.boundVehicle( workareaId, entityType);
		return R.data(list);
	}
}
