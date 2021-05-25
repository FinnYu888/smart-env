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

import com.ai.apac.smartenv.common.constant.FacilityConstant;
import com.ai.apac.smartenv.common.constant.InventoryConstant;
import com.ai.apac.smartenv.inventory.entity.ResSpec;
import com.ai.apac.smartenv.inventory.service.IResManageService;
import com.ai.apac.smartenv.inventory.service.IResSpecService;
import com.ai.apac.smartenv.inventory.vo.ResTypeSpecVO;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.SystemException;
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
import com.ai.apac.smartenv.inventory.entity.ResType;
import com.ai.apac.smartenv.inventory.vo.ResTypeVO;
import com.ai.apac.smartenv.inventory.wrapper.ResTypeWrapper;
import com.ai.apac.smartenv.inventory.service.IResTypeService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.List;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-02-25
 */
@RestController
@AllArgsConstructor
@RequestMapping("/restype")
@Api(value = "资源类型", tags = "资源类型")
public class ResTypeController extends BladeController {

	private IResTypeService resTypeService;
	private IResManageService resManageService;
	private IResSpecService resSpecService;
	/**
	 * 详情
	 */
	@ApiLog(value = "查询物资类型")
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入resType")
	public R<ResTypeVO> detail(ResType resType) {
		ResType detail = resTypeService.getOne(Condition.getQueryWrapper(resType));
		return R.data(ResTypeWrapper.build().entityVO(detail));
	}

	/**
	 * 查询类型列表
	 */
	@ApiLog(value = "查询物资类型")
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "查询类型列表", notes = "传入resType")
	public R<List<ResTypeVO>> list(ResType resType, BladeUser user) {
		QueryWrapper queryWrapper = Condition.getQueryWrapper(resType);
		if (null != user && StringUtil.isNotBlank(user.getTenantId())) queryWrapper.eq("tenant_id",user.getTenantId());
		List<ResType> pages = resTypeService.list( queryWrapper);
		return R.data(BeanUtil.copy(pages,ResTypeVO.class));
	}


	/**
	 * 自定义分页 
	 */
	@ApiLog(value = "查询物资类型")
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "自定义分页", notes = "传入resType")
	public R<IPage<ResTypeVO>> page(ResTypeVO resType, Query query,BladeUser user) {
		QueryWrapper queryWrapper = new QueryWrapper();
		if (StringUtil.isNotBlank(resType.getTypeName())) queryWrapper.like("type_name",resType.getTypeName());
		if (null != user && StringUtil.isNotBlank(user.getTenantId())) queryWrapper.eq("tenant_id",user.getTenantId());
		IPage<ResTypeVO> pages = resTypeService.page(Condition.getPage(query), queryWrapper);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@ApiLog(value = "新增物资类型")
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入resType")
	public R save(@Valid @RequestBody ResType resType,BladeUser user)
	{

		//校验名称是否已存在
		String resTypeName = resType.getTypeName();
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("type_name",resTypeName);
		if (null != user && StringUtil.isNotBlank(user.getTenantId())) queryWrapper.eq("tenant_id",user.getTenantId());
		ResType type = resTypeService.getOne(queryWrapper);
		if (null != type && StringUtil.isNotBlank(type.getTypeName())) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_RESTYPE_NAME));
		}
		if (null == resType.getParentTypeId()) {
			resType.setParentTypeId(0L);
		}
		return R.status(resTypeService.save(resType));
	}

	/**
	 * 修改 
	 */
	@ApiLog(value = "修改物资类型")
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入resType")
	public R update(@Valid @RequestBody ResType resType,BladeUser user) {
		//校验名称是否存在
		String resTypeName = resType.getTypeName();
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("TYPE_NAME",resTypeName);
		queryWrapper.ne("id",resType.getId());
		if (null != user && StringUtil.isNotBlank(user.getTenantId())) queryWrapper.eq("tenant_id",user.getTenantId());
		ResType type = resTypeService.getOne(queryWrapper);
		if (null != type && StringUtil.isNotBlank(type.getTypeName())) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_RESTYPE_NAME));
		}
		if (null == resType.getParentTypeId()) {
			resType.setParentTypeId(0L);
		}
		return R.status(resTypeService.updateById(resType));
	}


	
	/**
	 * 删除 
	 */
	@ApiLog(value = "逻辑删除物资类型")
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids,BladeUser user) {
		//是否有绑定的规格
		ResSpec resSpec = new ResSpec();
		resSpec.setResType(Func.toLong(ids));
		if (null != user && StringUtil.isNotBlank(user.getTenantId())) resSpec.setTenantId(user.getTenantId());
		List<ResSpec> specList = resSpecService.list(Condition.getQueryWrapper(resSpec));
		if (CollectionUtil.isNotEmpty(specList)) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_RESTYPE_DEL));
		}
		return R.status(resTypeService.deleteLogic(Func.toLongList(ids)));
	}

	/**
	 * 删除
	 */
	@ApiLog(value = "按照层级查找资源类型和资源规格")
	@GetMapping("/getResTypeSpec")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "按照层级查找资源类型和资源规格", notes = "")
	public R<List<ResTypeSpecVO>> getResTypeSpec() {
		//是否有绑定的规格
		List<ResTypeSpecVO> resTypeSpecVOS = resTypeService.selectResTypeSpec();
		return R.data(resTypeSpecVOS);
	}
}
