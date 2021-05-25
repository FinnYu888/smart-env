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

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.EventConstant;
import com.ai.apac.smartenv.common.utils.ExcelUtil;
import com.ai.apac.smartenv.event.dto.EventKpiTplDefDTO;
import com.ai.apac.smartenv.event.dto.EventKpiTplRelDTO;
import com.ai.apac.smartenv.event.entity.*;
import com.ai.apac.smartenv.event.service.IEventKpiTplDefService;
import com.ai.apac.smartenv.event.service.IEventKpiTplRelService;
import com.ai.apac.smartenv.event.vo.EventKpiDefVO;
import com.ai.apac.smartenv.event.vo.EventKpiTplDefVO;
import com.ai.apac.smartenv.event.vo.EventKpiTplRelVO;
import com.ai.apac.smartenv.event.wrapper.EventKpiDefWrapper;
import com.ai.apac.smartenv.event.wrapper.EventKpiTplDefWrapper;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.smartenv.cache.util.SmartCache;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
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
@RequestMapping("/eventKpiTplDef")
@Api(value = "考核指标模板定义表", tags = "考核指标模板定义表接口")
public class EventKpiTplDefController extends BladeController {

	private IEventKpiTplDefService eventKpiTplDefService;

	private IEventKpiTplRelService eventKpiTplRelService;




	/**
	 * 考核指标模板分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiLog(value = "查询考核指标模板列表")
	@ApiOperation(value = "分页", notes = "传入EventKpiDef")
	public R<IPage<EventKpiTplDefVO>> list(EventKpiTplDef eventKpiTplDef, Query query) {
		IPage<EventKpiTplDef> pages = eventKpiTplDefService.page(eventKpiTplDef, query);
		IPage<EventKpiTplDefVO> pageVO = EventKpiTplDefWrapper.build().pageVO(pages);
		List<EventKpiTplDefVO> records = pageVO.getRecords();
		records.forEach(record -> {
			record = getEventKpiTplDefAllInfoByVO(record);
		});
		return R.data(pageVO);
	}


	@GetMapping("")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "获取事件指标模板缓存，用于校验是否存在缓存，模板中指标的查询")
	@ApiLog(value = "获取事件指标模板缓存")
	public R<EventKpiTplDefDTO> getTplDefCache(@ApiParam(value = "主键", required = true) @RequestParam(required = true) String eventKpiTplId
			,@ApiParam(value = "指标分类", required = false) @RequestParam(required = false) String catalogId
			,@ApiParam(value = "指标名称", required = false) @RequestParam(required = false) String eventKpiName) {
		return R.data(eventKpiTplDefService.getTplDefCache(eventKpiTplId,catalogId,eventKpiName));
	}


	/**
	 * 获取事件指标模板缓存
	 */
	@GetMapping("/init")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "初始化事件指标模板缓存")
	@ApiLog(value = "初始化事件指标模板缓存")
	public R<EventKpiTplDefDTO> initTplDefCache(@ApiParam(value = "主键", required = false) @RequestParam String eventKpiTplId) {
		EventKpiTplDefDTO eventKpiTplDefDTO = eventKpiTplDefService.initTplDefCache(eventKpiTplId);
		return R.data(eventKpiTplDefDTO);
	}


	/**
	 * 删除事件指标模板缓存
	 */
	@DeleteMapping("/cache")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "删除事件指标模板缓存")
	@ApiLog(value = "删除事件指标模板缓存")
	public R delTplDefCache(@ApiParam(value = "主键", required = true) @RequestParam String eventKpiTplId) {
		eventKpiTplDefService.delTplDefCache(eventKpiTplId);
		return R.status(true);
	}

	/**
	 * 删除事件指标模板缓存
	 */
	@DeleteMapping("")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "删除事件指标模板")
	@ApiLog(value = "删除事件指标模板")
	public R delTplDef(@ApiParam(value = "主键", required = true) @RequestParam String eventKpiTplIds) {
		List<Long> eventKpiTplIdlist = 	Func.toLongList(eventKpiTplIds);
		eventKpiTplDefService.removeEventKpiTplDef(eventKpiTplIdlist);
		return R.status(true);
	}

	/**
	 * 删除事件指标模板缓存
	 */
	@PutMapping("/status")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "激活/取消激活事件指标模板")
	@ApiLog(value = "激活/取消激活事件指标模板")
	public R<Integer> updateTplDefStatus(@ApiParam(value = "主键", required = true) @RequestParam String eventKpiTplIds,
								@ApiParam(value = "主键", required = true) @RequestParam Integer status) {
		List<Long> eventKpiTplIdlist = 	Func.toLongList(eventKpiTplIds);
		if(!checkTplStation(eventKpiTplIdlist,status)){
			return R.data(CommonConstant.RES_CODE.CODE302,"相同岗位下只能激活一个模板");
		}
		if(!checkTplThreshold(eventKpiTplIdlist,status)){
			return R.data(CommonConstant.RES_CODE.CODE301,"已选模板的部分事件指标分值为空或零，请先补全");
		}
		eventKpiTplDefService.updateTplDefStatus(eventKpiTplIdlist,status);
		return R.data(200);
	}


	private boolean checkTplStation(List<Long> tplIdList,Integer status){
		if(EventConstant.Event_Kpi_Tpl_Status.LIVE.equals(status)){
			List<EventKpiTplDef> tplDefList = eventKpiTplDefService.list(new QueryWrapper<EventKpiTplDef>().lambda()
					.in(EventKpiTplDef::getId,tplIdList));
			if(ObjectUtil.isNotEmpty(tplDefList) && tplDefList.size() > 0) {
				List<Long> positionIds = tplDefList.stream().map(eventKpiTplDef -> eventKpiTplDef.getPositionId()).collect(Collectors.toList());
				List<EventKpiTplDef> tplDefList1 = eventKpiTplDefService.list(new QueryWrapper<EventKpiTplDef>().lambda()
						.in(EventKpiTplDef::getPositionId,positionIds).eq(EventKpiTplDef::getStatus,status));
				if(ObjectUtil.isNotEmpty(tplDefList1) && tplDefList1.size() > 0 ){
					return false;
				}
			}
		}
		return true;
	}

	private boolean checkTplThreshold(List<Long> tplIdList,Integer status){
		if(EventConstant.Event_Kpi_Tpl_Status.LIVE.equals(status)){
			List<EventKpiTplRel> tplRelList = eventKpiTplRelService.list(new QueryWrapper<EventKpiTplRel>().lambda().in(EventKpiTplRel::getEventKpiTplId,tplIdList));
			Long count = tplRelList.stream().filter(eventKpiTplRel -> ObjectUtil.isEmpty(eventKpiTplRel.getThreshold()) || eventKpiTplRel.getThreshold() <= 0).count();
			if(count > 0){
				return false;
			}
		}
		return true;
	}

	/**
	 * 考核指标模板配置指标提交
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "保存事件指标模板入库", notes = "传入eventKpiTplDefDTO")
	@ApiLog(value = "保存事件指标模板入库")
	public R saveTplDef(@ApiParam(value = "主键", required = true) @RequestParam String eventKpiTplId) {
		boolean save = eventKpiTplDefService.saveEventKpiTplDef(eventKpiTplId);
        return R.status(save);
	}

	/**
	 * 考核指标模板配置指标提交
	 */
	@PutMapping("/rels/cache")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "指标模板新增指标更新缓存", notes = "传入eventKpiTplDefDTO")
	@ApiLog(value = "指标模板新增指标更新缓存")
	public R updateTplRelsCache(@ApiParam(value = "主键", required = true) @RequestParam String eventKpiTplId,
								@RequestBody List<EventKpiTplRelDTO> eventKpiTplRelDTOList ) {
		eventKpiTplDefService.updateTplRelsCache(eventKpiTplId,eventKpiTplRelDTOList);
		return R.status(true);
	}

	/**
	 * 考核指标模板配置指标提交
	 */
	@DeleteMapping("/rel/cache")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "指标模板批量删除指标缓存", notes = "传入eventKpiTplDefDTO")
	@ApiLog(value = "指标模板新增指标更新缓存")
	public R updateTplRelsCache(@ApiParam(value = "主键", required = true) @RequestParam String eventKpiTplId,
								@RequestParam String eventKpiIds ) {
		List<Long> eventKpiIdList = Func.toLongList(eventKpiIds);
		eventKpiTplDefService.deleteTplRelsCache(eventKpiTplId,eventKpiIdList);
		return R.status(true);
	}

	/**
	 * 考核指标模板配置指标提交
	 */
	@PutMapping("/info/cache")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "指标模板基本信息更新缓存", notes = "传入eventKpiTplDefDTO")
	@ApiLog(value = "指标模板基本信息更新缓存")
	public R updateTplRelsCache(@RequestBody EventKpiTplDefDTO eventKpiTplDefDTO ) {
		eventKpiTplDefService.updateTplInfoCache(eventKpiTplDefDTO);
		return R.status(true);
	}

	/**
	 * 考核指标模板配置指标提交
	 */
	@PutMapping("/threshold/cache")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "指标模板中指标修改分数更新缓存", notes = "传入eventKpiTplDefDTO")
	@ApiLog(value = "指标模板新增指标更新缓存")
	public R updateTplThresholdCache(@ApiParam(value = "主键", required = true) @RequestParam String eventKpiTplId,
									 @ApiParam(value = "事件指标ID", required = true) @RequestParam String eventKpiId,
									 @ApiParam(value = "阈值", required = false) @RequestParam double threshold) {
		eventKpiTplDefService.updateTplThresholdCache(eventKpiTplId,eventKpiId,threshold);
		return R.status(true);
	}

	/**
	 * 考核指标模板配置指标提交
	 */
	@GetMapping("/preview")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "预览指标模板", notes = "传入eventKpiTplDefDTO")
	@ApiLog(value = "预览指标模板")
	public void previewEventKpiTpl(@ApiParam(value = "主键", required = true) @RequestParam String eventKpiTplId, HttpServletResponse response) {
		eventKpiTplDefService.previewEventKpiTpl(eventKpiTplId,response);
	}



	private void validateEventKpiTplDef(@Valid EventKpiTplDef eventKpiTplDef) {
		Set<ConstraintViolation<@Valid EventKpiTplDef>> validateSet = Validation.buildDefaultValidatorFactory()
				.getValidator().validate(eventKpiTplDef, new Class[0]);
		if (validateSet != null && !validateSet.isEmpty()) {
			String messages = validateSet.stream().map(ConstraintViolation::getMessage)
					.reduce((m1, m2) -> m1 + "；" + m2).orElse("参数输入有误！");
			throw new ServiceException(messages);
		}
	}
	
	private EventKpiTplDefVO getEventKpiTplDefAllInfoByVO(EventKpiTplDefVO eventKpiTplDefVO) {
		eventKpiTplDefVO.setStatusName(DictCache.getValue("event_kpi_tpl_status",eventKpiTplDefVO.getStatus()));
		eventKpiTplDefVO.setPositionName(StationCache.getStationName(eventKpiTplDefVO.getPositionId()));
		return eventKpiTplDefVO;
	}
}
