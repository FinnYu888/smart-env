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
package com.ai.apac.smartenv.event.controller;

import com.ai.apac.smartenv.event.cache.EventCache;
import com.ai.apac.smartenv.event.entity.EventKpiCatalog;
import com.ai.apac.smartenv.event.entity.EventKpiDef;
import com.ai.apac.smartenv.event.service.IEventKpiCatalogService;
import com.ai.apac.smartenv.event.service.IEventKpiDefService;
import com.ai.apac.smartenv.event.vo.EventKpiCatalogVO;
import com.ai.apac.smartenv.event.vo.EventKpiDefVO;
import com.ai.apac.smartenv.event.wrapper.EventKpiCatalogWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import java.util.List;
import java.util.Set;

/**
 * 事件指标分类 控制器
 *
 * @author Blade
 * @since 2020-02-08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/eventKpiCatalog")
@Api(value = "事件指标分类", tags = "事件指标分类接口")
public class EventKpiCatalogController extends BladeController {

    private IEventKpiCatalogService eventKpiCatalogService;

    private IEventKpiDefService eventKpiDefService;


    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入EventKpiCatalog")
    @ApiLog(value = "查询事件指标分类详情")
    public R<EventKpiCatalogVO> detail(EventKpiCatalog eventKpiCatalog) {
    	EventKpiCatalog detail = EventCache.getEventKpiCatalogById(eventKpiCatalog.getId());
        EventKpiCatalogVO eventKpiCatalogVO = EventKpiCatalogWrapper.build().entityVO(detail);
        EventKpiDefVO eventKpiDefVO = new EventKpiDefVO();
        eventKpiDefVO.setEventKpiCatalog(detail.getId());
        Integer kpiCount = eventKpiDefService.countEventKpiDef(eventKpiDefVO);
        eventKpiCatalogVO.setEventKpiCount(kpiCount);
    	if(detail.getParentId() != 0L){
            EventKpiCatalog parentDetail = EventCache.getEventKpiCatalogById(detail.getParentId());
            eventKpiCatalogVO.setParentName(parentDetail.getCatalogName());
        }
        return R.data(eventKpiCatalogVO);
    }

    /**
     * 分页 事件指标分类
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入EventKpiCatalog")
    @ApiLog(value = "查询事件指标分类列表")
    public R<IPage<EventKpiCatalogVO>> list(EventKpiCatalog eventKpiCatalog, Query query) {
		IPage<EventKpiCatalog> pages = eventKpiCatalogService.page(eventKpiCatalog, query);
		IPage<EventKpiCatalogVO> pageVO = EventKpiCatalogWrapper.build().pageVO(pages);
        return R.data(pageVO);
    }

    @GetMapping("/listAll")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "所有", notes = "传入EventKpiCatalog")
    @ApiLog(value = "查询所有事件指标分类")
    public R<List<EventKpiCatalogVO>> listAll(EventKpiCatalog eventKpiCatalog) {
    	List<EventKpiCatalog> list = eventKpiCatalogService.listAll(eventKpiCatalog);
    	List<EventKpiCatalogVO> listVO = EventKpiCatalogWrapper.build().listVO(list);
    	return R.data(listVO);
    }


    /**
     * 新增 事件指标分类
     */
    @PostMapping("")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入EventKpiCatalog")
    @ApiLog(value = "新增事件指标分类")
    public R save(@RequestBody EventKpiCatalog eventKpiCatalog) {
    	// 验证入参
		validateEventKpiCatalog(eventKpiCatalog);
		boolean save = eventKpiCatalogService.saveEventKpiCatalog(eventKpiCatalog);
        return R.status(save);
    }



	/**
     * 修改 事件指标分类
     */
    @PutMapping("")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入EventKpiCatalog")
    @ApiLog(value = "修改事件指标分类")
    public R update(@RequestBody EventKpiCatalog eventKpiCatalog) {
    	// 验证入参
		validateEventKpiCatalog(eventKpiCatalog);
		boolean update = eventKpiCatalogService.updateEventKpiCatalogById(eventKpiCatalog);
        return R.status(update);
    }



    /**
     * 删除 事件指标分类
     */
    @DeleteMapping("")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    @ApiLog(value = "删除事件指标分类")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
    	List<Long> idList = Func.toLongList(ids);
    	List<EventKpiDef> eventKpiDefList = eventKpiDefService.list(new QueryWrapper<EventKpiDef>().lambda().in(EventKpiDef::getEventKpiCatalog,idList));
    	if(ObjectUtil.isNotEmpty(eventKpiDefList) && eventKpiDefList.size() > 0 ){
            throw new ServiceException("所选事件指标分类下含有指标消息，请先删除事件指标");
        }
    	eventKpiCatalogService.removeEventKpiCatalog(idList);
        return R.status(true);
    }

	private void validateEventKpiCatalog(@Valid EventKpiCatalog eventKpiCatalog) {
		Set<ConstraintViolation<@Valid EventKpiCatalog>> validateSet = Validation.buildDefaultValidatorFactory()
				.getValidator().validate(eventKpiCatalog, new Class[0]);
		if (validateSet != null && !validateSet.isEmpty()) {
			String messages = validateSet.stream().map(ConstraintViolation::getMessage)
					.reduce((m1, m2) -> m1 + "；" + m2).orElse("参数输入有误！");
			throw new ServiceException(messages);
		}

        Long catalogId = eventKpiCatalog.getParentId();
        List<EventKpiDef> defList = eventKpiDefService.list(new QueryWrapper<EventKpiDef>().lambda().eq(EventKpiDef::getEventKpiCatalog,catalogId));
		if(ObjectUtil.isNotEmpty(defList) && defList.size() > 0 ){
            throw new ServiceException("父级分类下已配置指标，不能再定义子分类");
        }
	}

    /**
     * 获取事件指标分类树形结构
     *
     * @return
     */
    @GetMapping("/tree")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "事件指标分类结构", notes = "事件指标分类结构")
    @ApiLog(value = "获取事件指标分类树形结构")
    public R<List<EventKpiCatalogVO>> tree(BladeUser bladeUser) {
        List<EventKpiCatalogVO> tree = eventKpiCatalogService.tree(null);
        return R.data(tree);
    }
}
