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
package com.ai.apac.smartenv.system.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tenant.constant.TenantConstant;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.vo.EntityCategoryVO;
import com.ai.apac.smartenv.system.wrapper.EntityCategoryWrapper;
import com.ai.apac.smartenv.system.service.IEntityCategoryService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.ArrayList;
import java.util.List;

/**
 * 车辆,设备,物资等实体的分类信息 控制器
 *
 * @author Blade
 * @since 2020-02-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/entitycategory")
@Api(value = "车辆,设备,物资等实体的分类信息", tags = "车辆,设备,物资等实体的分类信息接口")
public class EntityCategoryController extends BladeController {

	private IEntityCategoryService entityCategoryService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入entityCategory")
	@ApiLog(value = "查看实体详情")
	public R<EntityCategoryVO> detail(EntityCategory entityCategory) {
		AuthUtil.getUser().setTenantId(TenantConstant.DEFAULT_TENANT_ID);
		EntityCategory detail = entityCategoryService.getOne(Condition.getQueryWrapper(entityCategory));
		return R.data(EntityCategoryWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 车辆,设备,物资等实体的分类信息
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入entityCategory")
	@ApiLog(value = "分页查询实体分类信息")
	public R<IPage<EntityCategoryVO>> list(EntityCategory entityCategory, Query query) {
		AuthUtil.getUser().setTenantId(TenantConstant.DEFAULT_TENANT_ID);
		IPage<EntityCategory> pages = entityCategoryService.page(Condition.getPage(query),Condition.getQueryWrapper(entityCategory));
		return R.data(EntityCategoryWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 车辆,设备,物资等实体的分类信息
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入entityCategory")
	@ApiLog(value = "自定义分页查询实体分类信息")
	public R<IPage<EntityCategoryVO>> page(EntityCategoryVO entityCategory, Query query) {
		AuthUtil.getUser().setTenantId(TenantConstant.DEFAULT_TENANT_ID);
		IPage<EntityCategoryVO> pages = entityCategoryService.selectEntityCategoryPage(Condition.getPage(query), entityCategory);
		return R.data(pages);
	}

	/**
	 * 新增 车辆,设备,物资等实体的分类信息
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入entityCategory")
	@ApiLog(value = "新增实体分类信息")
	public R save(@Valid @RequestBody EntityCategory entityCategory) {
		AuthUtil.getUser().setTenantId(TenantConstant.DEFAULT_TENANT_ID);
		return R.status(entityCategoryService.save(entityCategory));
	}

	/**
	 * 修改 车辆,设备,物资等实体的分类信息
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入entityCategory")
	@ApiLog(value = "修改实体分类信息")
	public R update(@Valid @RequestBody EntityCategory entityCategory) {
		AuthUtil.getUser().setTenantId(TenantConstant.DEFAULT_TENANT_ID);
		return R.status(entityCategoryService.updateById(entityCategory));
	}

	/**
	 * 新增或修改 车辆,设备,物资等实体的分类信息
	 */
/*	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入entityCategory")
	public R submit(@Valid @RequestBody EntityCategory entityCategory) {
		return R.status(entityCategoryService.saveOrUpdate(entityCategory));
	}*/

	
	/**
	 * 删除 车辆,设备,物资等实体的分类信息
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@ApiLog(value = "删除实体分类信息")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		AuthUtil.getUser().setTenantId(TenantConstant.DEFAULT_TENANT_ID);
		return R.status(entityCategoryService.deleteLogic(Func.toLongList(ids)));
	}

	/**
	 * 车辆,设备,物资等实体的分类信息树
	 */
	@GetMapping("/listTree")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "根据条件查询", notes = "传入entityCategory")
	@ApiLog(value = "车辆,设备,物资等实体的分类信息树")
	public R<List<EntityCategoryVO>> listTree(EntityCategory entityCategory) {
		AuthUtil.getUser().setTenantId(TenantConstant.DEFAULT_TENANT_ID);
		List<EntityCategory> list = entityCategoryService.list(Condition.getQueryWrapper(entityCategory));
		List<EntityCategoryVO> entityCategoryVOS = EntityCategoryWrapper.build().listVO(list);
		if (CollectionUtil.isNotEmpty(entityCategoryVOS)) {
			entityCategoryVOS.forEach(entityCategoryVO -> {
				List<EntityCategoryVO> entityCategoryVOList = listEntityCategoryByParentCategoryId(entityCategoryVO.getId());
				entityCategoryVO.setChildEntityCategoryVOS(entityCategoryVOList);
			});
		}
		return R.data(entityCategoryVOS);
	}

	private List<EntityCategoryVO> listEntityCategoryByParentCategoryId(Long parentEntityCagegoryId) {
		List<EntityCategoryVO> parentVOS = new ArrayList<>();
		EntityCategory wrapper = new EntityCategory();
		wrapper.setParentCategoryId(parentEntityCagegoryId);
		List<EntityCategory> parent = entityCategoryService.list(Condition.getQueryWrapper(wrapper));
		if (CollectionUtil.isNotEmpty(parent)) {
			parentVOS = EntityCategoryWrapper.build().listVO(parent);
			parentVOS.forEach(parentVO -> {
				List<EntityCategoryVO> children = this.listEntityCategoryByParentCategoryId(parentVO.getId());
				parentVO.setChildEntityCategoryVOS(children);
			});
		}
		return parentVOS;
	}
}
