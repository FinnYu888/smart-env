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
import com.ai.apac.smartenv.event.entity.EventInfoKpiRel;
import com.ai.apac.smartenv.event.entity.EventKpiCatalog;
import com.ai.apac.smartenv.event.entity.EventKpiDef;
import com.ai.apac.smartenv.event.service.IEventInfoKpiRelService;
import com.ai.apac.smartenv.event.service.IEventKpiCatalogService;
import com.ai.apac.smartenv.event.service.IEventKpiDefService;
import com.ai.apac.smartenv.event.vo.EventKpiCatalogVO;
import com.ai.apac.smartenv.event.vo.EventKpiDefVO;
import com.ai.apac.smartenv.event.wrapper.EventKpiDefWrapper;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 考核指标定义表 控制器
 *
 * @author Blade
 * @since 2020-02-08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/eventKpiDef")
@Api(value = "考核指标定义表", tags = "考核指标定义表接口")
public class EventKpiDefController extends BladeController {

	private IEventKpiDefService eventKpiDefService;

	private IEventKpiCatalogService eventKpiCatalogService;

	private IEventInfoKpiRelService eventInfoKpiRelService;


	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入EventKpiDef")
	@ApiLog(value = "查询考核指标定义详情")
	public R<EventKpiDefVO> detail(EventKpiDef eventKpiDef) {
		return R.data(eventKpiDefService.getEventKpiDef(eventKpiDef.getId()));
	}


	/**
	 * 分页 考核指标定义表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiLog(value = "查询考核指标定义列表")
	@ApiOperation(value = "分页", notes = "传入EventKpiDef")
	public R<IPage<EventKpiDefVO>> list(EventKpiDef eventKpiDef, Query query) {
		IPage<EventKpiDef> pages = eventKpiDefService.page(eventKpiDef, query);
		IPage<EventKpiDefVO> pageVO = EventKpiDefWrapper.build().pageVO(pages);
		List<EventKpiDefVO> records = pageVO.getRecords();
		records.forEach(record -> {
			record = getEventKpiDefAllInfoByVO(record);
		});
		return R.data(pageVO);
	}



	@GetMapping(value = {"/noPageList"})
	@ApiOperationSupport(order = 2)
	@ApiLog(value = "不分页查询考核指标")
	@ApiOperation(value = "不分页查询", notes = "传入EventKpiDef")
	public R<List<EventKpiDefVO>> noPageList(EventKpiDef eventKpiDef){
		String eventKpiDefName=eventKpiDef.getEventKpiName();
		EventKpiDef queryDef = BeanUtil.copy(eventKpiDef, EventKpiDef.class);
		queryDef.setTenantId(getUser().getTenantId());
		queryDef.setIsDeleted(0);
		QueryWrapper<EventKpiDef> wrapper=new QueryWrapper<>(queryDef);
		if (StringUtil.isNotBlank(eventKpiDefName)){
			wrapper.like("kpi_name",eventKpiDefName);
		}
		List<EventKpiDef> list = eventKpiDefService.list(wrapper);
		List<EventKpiDefVO> eventKpiDefVOS = EventKpiDefWrapper.build().listVO(list);
		eventKpiDefVOS.forEach(record -> {
			record = getEventKpiDefAllInfoByVO(record);
		});
		return R.data(eventKpiDefVOS);
	}


	@GetMapping(value = {"/getKpiDefByPersonId/{personId}"})
	@ApiOperationSupport(order = 2)
	@ApiLog(value = "根据人员ID查询指标")
	@ApiOperation(value = "根据人员ID查询指标", notes = "传入人员ID")
	public R<List<EventKpiCatalogVO>> getKpiDefByPersonId(@PathVariable Long personId){
		List<EventKpiCatalogVO> eventKpiCatalogVOS = eventKpiDefService.treeKpiDefByPersonId(personId);

		return R.data(eventKpiCatalogVOS);
	}
	@GetMapping(value = {"/getKpiDefTreeByEventId/{eventId}"})
	@ApiOperationSupport(order = 2)
	@ApiLog(value = "根据事件ID查询指标树")
	@ApiOperation(value = "根据事件ID查询指标树", notes = "传入人员ID")
	public R<List<EventKpiCatalogVO>> getKpiDefTreeByEventId(@PathVariable Long eventId){

		List<EventKpiCatalogVO> eventKpiCatalogVOS = eventKpiDefService.getKpiDefByEventId(eventId);

		return R.data(eventKpiCatalogVOS);

	}
	@GetMapping(value = {"/getKpiDefListByEventId/{eventId}"})
	@ApiOperationSupport(order = 2)
	@ApiLog(value = "根据事件ID查询指标")
	@ApiOperation(value = "根据事件ID查询指标", notes = "根据事件ID查询指标")
	public R<List<EventKpiDefVO>> getKpiDefListByEventId(@PathVariable Long eventId){
//		EventInfoKpiRel eventInfoKpiRelQuery=new EventInfoKpiRel();
//		eventInfoKpiRelQuery.setEventInfoId(eventId);
		QueryWrapper<EventInfoKpiRel> eventInfoKpiRelQueryWrapper=new QueryWrapper<>();
		eventInfoKpiRelQueryWrapper.eq("event_info_id",eventId);
		List<EventInfoKpiRel> eventInfoKpiRels = eventInfoKpiRelService.list(eventInfoKpiRelQueryWrapper);
		List<Long> kpiIdList = eventInfoKpiRels.stream().map(EventInfoKpiRel::getKpiId).collect(Collectors.toList());

		if (CollectionUtil.isEmpty(kpiIdList)){
			return R.data(null);
		}


		QueryWrapper<EventKpiDef> query=new QueryWrapper<>();
		query.in("id",kpiIdList);
		List<EventKpiDef> list = eventKpiDefService.list(query);
		Map<Long, List<EventInfoKpiRel>> kpiRelMap = eventInfoKpiRels.stream().collect(Collectors.groupingBy(EventInfoKpiRel::getKpiId));


		List<EventKpiDefVO> eventKpiDefVOS = list.stream().map(eventKpiDef -> {
			EventKpiDefVO eventKpiDefVO = EventKpiDefWrapper.build().entityVO(eventKpiDef);
			EventInfoKpiRel eventInfoKpiRel = kpiRelMap.get(eventKpiDef.getId())==null?null:kpiRelMap.get(eventKpiDef.getId()).get(0);
			if (eventInfoKpiRel!=null){
				eventKpiDefVO.setThreshold(Double.valueOf(eventInfoKpiRel.getDeducted()));
			}else {
				eventKpiDefVO.setThreshold(0D);
			}
			return eventKpiDefVO;

		}).collect(Collectors.toList());

		return R.data(eventKpiDefVOS);

	}



	/**
	 * 新增 考核指标定义表
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入EventKpiDef")
	@ApiLog(value = "新增考核指标定义")
	public R save(@RequestBody EventKpiDef eventKpiDef) {
		// 验证入参
		validateEventKpiDef(eventKpiDef);
		boolean save = eventKpiDefService.saveEventKpiDef(eventKpiDef);
		return R.status(save);
	}


	/**
	 * 修改 考核指标定义表
	 */
	@PutMapping("")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入EventKpiDef")
	@ApiLog(value = "修改考核指标定义")
	public R update(@RequestBody EventKpiDef eventKpiDef) {
		// 验证入参
		validateEventKpiDef(eventKpiDef);
		boolean update = eventKpiDefService.updateEventKpiDefById(eventKpiDef);
        return R.status(update);
	}


	/**
	 * 删除 考核指标定义表
	 */
	@DeleteMapping("")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@ApiLog(value = "删除考核指标定义")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		List<Long> idList = Func.toLongList(ids);
		if(!StringUtil.isEmpty(ids) && "-1".equals(ids)){
			List<EventKpiDef> eventKpiDefList = eventKpiDefService.list(new QueryWrapper<EventKpiDef>());
			idList = eventKpiDefList.stream().map(EventKpiDef::getId).collect(Collectors.toList());
		}
		//eventkpiTplDefService.verifyKpiIsUsed(idList);
		boolean remove = eventKpiDefService.removeEventKpiDef(idList);
        return R.status(remove);
	}

	/**
	 * 删除 考核指标定义表
	 */
	@DeleteMapping("/ByCatalog")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "逻辑删除分类下全部指标", notes = "传入指标分类Id")
	@ApiLog(value = "删除指标分类下考核指标定义")
	public R removeByCatalog(@ApiParam(value = "指标分类Id", required = true) @RequestParam String id) {
		List<Long> idList = new ArrayList<Long>();
		idList.add(Long.parseLong(id));
		eventKpiCatalogService.getChildCatalogIdList(Long.parseLong(id),idList);
		List<EventKpiDef> eventKpiDefList = eventKpiDefService.list(new QueryWrapper<EventKpiDef>().lambda().in(EventKpiDef::getEventKpiCatalog,idList));
		List<Long> defIdList = eventKpiDefList.stream().map(EventKpiDef::getId).collect(Collectors.toList());
		boolean remove = eventKpiDefService.removeEventKpiDef(defIdList);
		return R.status(remove);
	}



	private void validateEventKpiDef(@Valid EventKpiDef eventKpiDef) {
		Set<ConstraintViolation<@Valid EventKpiDef>> validateSet = Validation.buildDefaultValidatorFactory()
				.getValidator().validate(eventKpiDef, new Class[0]);
		if (validateSet != null && !validateSet.isEmpty()) {
			String messages = validateSet.stream().map(ConstraintViolation::getMessage)
					.reduce((m1, m2) -> m1 + "；" + m2).orElse("参数输入有误！");
			throw new ServiceException(messages);
		}

		Long catalog = eventKpiDef.getEventKpiCatalog();

		List<EventKpiCatalog> eventKpiCatalogList = eventKpiCatalogService.list(new QueryWrapper<EventKpiCatalog>().lambda().eq(EventKpiCatalog::getParentId,catalog));
		if(ObjectUtil.isNotEmpty(eventKpiCatalogList) && eventKpiCatalogList.size() > 0){
			throw new ServiceException("所选分类下已包含子分类，不能再配置事件指标");
		}


	}

	private EventKpiDefVO getEventKpiDefAllInfoByVO(EventKpiDefVO eventKpiDefVO) {
		Long kpiCatalogId = eventKpiDefVO.getEventKpiCatalog();
		EventKpiCatalog eventKpiCatalog = EventCache.getEventKpiCatalogById(kpiCatalogId);
		if (eventKpiCatalog != null && StringUtils.isNotBlank(eventKpiCatalog.getCatalogName())) {
			eventKpiDefVO.setEventKpiCatalogName(eventKpiCatalog.getCatalogName());
			eventKpiDefVO.setEventKpiCatalogLevel(eventKpiCatalog.getCatalogLevel());
		}
		if(ObjectUtil.isNotEmpty(eventKpiDefVO.getHandleLimitTime())) {
			eventKpiDefVO.setHandleLimitTimeDesc(DictBizCache.getValue(AuthUtil.getTenantId(),"event_handle_time", eventKpiDefVO.getHandleLimitTime()));
		}
		return eventKpiDefVO;
	}

}
