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
package com.ai.apac.smartenv.inventory.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ai.apac.smartenv.common.constant.InventoryConstant;
import com.ai.apac.smartenv.inventory.entity.ResInfo;
import com.ai.apac.smartenv.inventory.entity.ResOrder;
import com.ai.apac.smartenv.inventory.entity.ResOrderDtl;
import com.ai.apac.smartenv.inventory.service.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.inventory.entity.ResSpec;
import com.ai.apac.smartenv.inventory.vo.ResSpecVO;
import com.ai.apac.smartenv.inventory.wrapper.ResSpecWrapper;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.ArrayList;
import java.util.List;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-02-25
 */
@RestController
@AllArgsConstructor
@RequestMapping("resspec")
@Api(value = "资源规格", tags = "资源规格")
public class ResSpecController extends BladeController {

	private IResSpecService resSpecService;
	private IResOrderDtlService resOrderDtlService;
	private IResOrderService resOrderService;
	private IResManageService resManageService;
	private IResInfoService resInfoService;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入resSpec")
	public R<ResSpecVO> detail(ResSpec resSpec) {
		ResSpec detail = resSpecService.getOne(Condition.getQueryWrapper(resSpec));
		return R.data(ResSpecWrapper.build().entityVO(detail));
	}

	/**
	 * 查询列表
	 */
	@GetMapping("/list")
	@ApiLog(value = "查询物资规格列表")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "查询列表", notes = "传入resSpec")
	public R<List<ResSpecVO>> list(ResSpec resSpec,BladeUser user) {
		QueryWrapper queryWrapper = Condition.getQueryWrapper(resSpec);
		if (null != user && StringUtil.isNotBlank(user.getTenantId())) queryWrapper.eq("tenant_id",user.getTenantId());
		List<ResSpec> pages = resSpecService.list( queryWrapper);
		return R.data(BeanUtil.copy(pages,ResSpecVO.class));
	}


	/**
	 * 自定义分页 
	 */
	@ApiLog(value = "查询物资规格")
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "自定义分页", notes = "传入resSpec")
	public R<IPage<ResSpecVO>> page(ResSpecVO resSpec, Query query, BladeUser user) {

		QueryWrapper queryWrapper = new QueryWrapper();
		if (null != resSpec.getResType()) queryWrapper.eq("res_type",resSpec.getResType());
		if (StringUtil.isNotBlank(resSpec.getSpecName())) queryWrapper.like("spec_name",resSpec.getSpecName());
		if (null != user && StringUtil.isNotBlank(user.getTenantId())) queryWrapper.eq("spec.tenant_id",user.getTenantId());
		IPage<ResSpecVO> pages = resSpecService.selectResSpecInfoPage(Condition.getPage(query), queryWrapper);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@ApiLog(value = "新增物资规格")
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入resSpec")
	public R save(@Valid @RequestBody ResSpec resSpec,BladeUser user) {
		String specName = resSpec.getSpecName();
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("SPEC_NAME",specName);
		if (null != user && StringUtil.isNotBlank(user.getTenantId())) queryWrapper.eq("tenant_id",user.getTenantId());
		ResSpec spec  = resSpecService.getOne(queryWrapper);
		if (null != spec && StringUtil.isNotBlank(spec.getSpecName())) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_RESSPEC_NAME));
		}
		return R.status(resSpecService.save(resSpec));
	}

	/**
	 * 修改 
	 */
	@ApiLog(value = "修改物资规格")
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入resSpec")
	public R update(@Valid @RequestBody ResSpec resSpec,BladeUser user) {
		String specName = resSpec.getSpecName();
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("SPEC_NAME",specName);
		queryWrapper.ne("ID",resSpec.getId());
		if (null != user && StringUtil.isNotBlank(user.getTenantId())) queryWrapper.eq("tenant_id",user.getTenantId());
		ResSpec spec  = resSpecService.getOne(queryWrapper);
		if (null != spec && StringUtil.isNotBlank(spec.getSpecName())) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_RESSPEC_NAME));
		}
		return R.status(resSpecService.updateById(resSpec));
	}

	
	/**
	 * 删除 
	 */
	@ApiLog(value = "删除物资规格")
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		//判断当前规格是否有绑定的物资
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("res_spec_id",Func.toLong(ids));
		List<ResInfo> resInfoList = resInfoService.list(queryWrapper);
		if (CollectionUtil.isNotEmpty(resInfoList)) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_RESSPEC_DEL));
		}

		//判断当前规格是否有在途单
		QueryWrapper<ResOrder> resOrderQueryWrapper = new  QueryWrapper<ResOrder>();
		List<Integer> orderStatusList = new ArrayList<>();
		orderStatusList.add(2);
		orderStatusList.add(5);
		resOrderQueryWrapper.lambda().in(ResOrder::getOrderStatus,orderStatusList);
		List<ResOrder> resOrderList =  resOrderService.list(resOrderQueryWrapper);
		if(ObjectUtil.isNotEmpty(resOrderList) && resOrderList.size() > 0 ){
			List<Long> resOrderIds = new ArrayList<Long>();
			resOrderList.forEach(resOrder -> {
				resOrderIds.add(resOrder.getId());
			});
			QueryWrapper<ResOrderDtl> resOrderDtlQueryWrapper = new  QueryWrapper<ResOrderDtl>();
			resOrderDtlQueryWrapper.lambda().in(ResOrderDtl::getOrderId,resOrderIds);
			resOrderDtlQueryWrapper.lambda().in(ResOrderDtl::getResSpecId,Func.toLong(ids));
			List<ResOrderDtl> resOrderDtlList =  resOrderDtlService.list(resOrderDtlQueryWrapper);
			if (CollectionUtil.isNotEmpty(resOrderDtlList)) {
				throw new ServiceException("所选的资源规格有在途单，不能删除");
			}
		}

		//todo 66578 告警规则

		return R.status(resSpecService.deleteLogic(Func.toLongList(ids)));
	}

	
}
