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
package com.ai.apac.smartenv.assessment.controller;

import com.ai.apac.smartenv.assessment.service.IKpiDefService;
import com.ai.apac.smartenv.assessment.vo.KpiDefVO;
import com.ai.apac.smartenv.system.vo.DeptVO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;

import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.assessment.cache.AssessmentCache;
import com.ai.apac.smartenv.assessment.entity.KpiCatalog;
import com.ai.apac.smartenv.assessment.vo.KpiCatalogVO;
import com.ai.apac.smartenv.assessment.wrapper.KpiCatalogWrapper;
import com.ai.apac.smartenv.assessment.service.IKpiCatalogService;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;

/**
 * 考核指标分类 控制器
 *
 * @author Blade
 * @since 2020-02-08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/kpicatalog")
@Api(value = "考核指标分类", tags = "考核指标分类接口")
public class KpiCatalogController extends BladeController {

    private IKpiCatalogService kpiCatalogService;

    private IKpiDefService kpiDefService;


    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入kpiCatalog")
    @ApiLog(value = "查询考核指标分类详情")
    public R<KpiCatalogVO> detail(KpiCatalog kpiCatalog) {
    	KpiCatalog detail = AssessmentCache.getKpiCatalogById(kpiCatalog.getId());
        KpiCatalogVO kpiCatalogVO = KpiCatalogWrapper.build().entityVO(detail);
        KpiDefVO kpiDefVO = new KpiDefVO();
        kpiDefVO.setKpiCatalog(detail.getId());
        Integer kpiCount = kpiDefService.countKpiDef(kpiDefVO);
        kpiCatalogVO.setKpiCount(kpiCount);
    	if(detail.getParentId() != 0L){
            KpiCatalog parentDetail = AssessmentCache.getKpiCatalogById(detail.getParentId());
            kpiCatalogVO.setParentName(parentDetail.getCatalogName());
        }
        return R.data(kpiCatalogVO);
    }

    /**
     * 分页 考核指标分类
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入kpiCatalog")
    @ApiLog(value = "查询考核指标分类列表")
    public R<IPage<KpiCatalogVO>> list(KpiCatalog kpiCatalog, Query query) {
		IPage<KpiCatalog> pages = kpiCatalogService.page(kpiCatalog, query);
		IPage<KpiCatalogVO> pageVO = KpiCatalogWrapper.build().pageVO(pages);
        return R.data(pageVO);
    }

    @GetMapping("/listAll")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "所有", notes = "传入kpiCatalog")
    @ApiLog(value = "查询所有考核指标分类")
    public R<List<KpiCatalogVO>> listAll(KpiCatalog kpiCatalog) {
    	List<KpiCatalog> list = kpiCatalogService.listAll(kpiCatalog);
    	List<KpiCatalogVO> listVO = KpiCatalogWrapper.build().listVO(list);
    	return R.data(listVO);
    }


    /**
     * 自定义分页 考核指标分类
     */
	/*@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入kpiCatalog")
	public R<IPage<KpiCatalogVO>> page(KpiCatalogVO kpiCatalog, Query query) {
	    IPage<KpiCatalogVO> pages = kpiCatalogService.selectKpiCatalogPage(Condition.getPage(query), kpiCatalog);
	    return R.data(pages);
	}*/

    /**
     * 新增 考核指标分类
     */
    @PostMapping("")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入kpiCatalog")
    @ApiLog(value = "新增考核指标分类")
    public R save(@RequestBody KpiCatalog kpiCatalog) {
    	// 验证入参
		validateKpiCatalog(kpiCatalog);
		boolean save = kpiCatalogService.saveKpiCatalog(kpiCatalog);
        return R.status(save);
    }



	/**
     * 修改 考核指标分类
     */
    @PutMapping("")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入kpiCatalog")
    @ApiLog(value = "新增考核指标分类")
    public R update(@RequestBody KpiCatalog kpiCatalog) {
    	// 验证入参
		validateKpiCatalog(kpiCatalog);
		boolean update = kpiCatalogService.updateKpiCatalogById(kpiCatalog);
        return R.status(update);
    }



    /**
     * 新增或修改 考核指标分类
     */
	/*@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入kpiCatalog")
	public R submit(@Valid @RequestBody KpiCatalog kpiCatalog) {
	    return R.status(kpiCatalogService.saveOrUpdate(kpiCatalog));
	}*/

    /**
     * 删除 考核指标分类
     */
    @DeleteMapping("")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    @ApiLog(value = "删除考核指标分类")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
    	List<Long> idList = Func.toLongList(ids);
    	kpiCatalogService.removeKpiCatalog(idList);
        return R.status(true);
    }

	private void validateKpiCatalog(@Valid KpiCatalog kpiCatalog) {
		Set<ConstraintViolation<@Valid KpiCatalog>> validateSet = Validation.buildDefaultValidatorFactory()
				.getValidator().validate(kpiCatalog, new Class[0]);
		if (validateSet != null && !validateSet.isEmpty()) {
			String messages = validateSet.stream().map(ConstraintViolation::getMessage)
					.reduce((m1, m2) -> m1 + "；" + m2).orElse("参数输入有误！");
			throw new ServiceException(messages);
		}
	}

    /**
     * 获取考核指标分类树形结构
     *
     * @return
     */
    @GetMapping("/tree")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "考核指标分类结构", notes = "考核指标分类结构")
    @ApiLog(value = "获取考核指标分类树形结构")
    public R<List<KpiCatalogVO>> tree(BladeUser bladeUser) {
        List<KpiCatalogVO> tree = kpiCatalogService.tree();
        return R.data(tree);
    }
}
