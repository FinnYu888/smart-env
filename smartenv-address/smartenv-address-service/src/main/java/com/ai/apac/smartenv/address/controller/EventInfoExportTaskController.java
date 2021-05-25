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
package com.ai.apac.smartenv.address.controller;

import com.ai.apac.smartenv.address.dto.EventinfoExportTaskDTO;
import com.ai.apac.smartenv.address.entity.EventinfoExportTask;
import com.ai.apac.smartenv.address.entity.TrackExportTask;
import com.ai.apac.smartenv.address.query.EventExportTaskQuery;
import com.ai.apac.smartenv.address.service.IAddressAsyncService;
import com.ai.apac.smartenv.address.service.IEventinfoExportTaskService;
import com.ai.apac.smartenv.address.vo.EventinfoExportTaskVO;
import com.ai.apac.smartenv.address.wrapper.EventinfoExportTaskWrapper;
import com.ai.apac.smartenv.common.constant.AddressConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.event.dto.EventQueryDTO;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.event.vo.EventInfoVO;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 事件记录导出任务表 控制器
 *
 * @author Blade
 * @since 2020-05-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/eventInfoeexporttask")
@Api(value = "事件记录导出任务表", tags = "事件导出任务表接口")
public class EventInfoExportTaskController extends BladeController {

	private IEventinfoExportTaskService eventInfoExportTaskService;
	private IDictClient dictClient;
    private IEventInfoClient eventinfoClient;
	/**
	 * 分页 事件记录导出任务表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入eventInfoeExportTask")
	@ApiLog(value = "分页查询事件导出任务")
	public R<IPage<EventinfoExportTaskVO>> list(EventExportTaskQuery eventExportTaskQuery) {
		EventinfoExportTask eventInfoExportTask=new EventinfoExportTask();
		BeanUtils.copyProperties(eventExportTaskQuery,eventInfoExportTask);
		QueryWrapper<EventinfoExportTask> queryWrapper = Condition.getQueryWrapper(eventInfoExportTask);
		if (eventInfoExportTask.getExportStatus() != null && eventInfoExportTask.getExportStatus() != 0){
			queryWrapper.eq("export_status",eventInfoExportTask.getExportStatus());
		}
		if (StringUtils.isNotBlank(eventInfoExportTask.getBelongArea())){
			queryWrapper.like("belong_area",eventInfoExportTask.getBelongArea());
		}
		if (StringUtils.isNotBlank(eventExportTaskQuery.getConditionBeginTime())){
			queryWrapper.gt("export_time",new Timestamp(Long.parseLong(eventExportTaskQuery.getConditionBeginTime())));
		}
		if (StringUtils.isNotBlank(eventExportTaskQuery.getConditionEndTime())){
			queryWrapper.lt("export_time",new Timestamp(Long.parseLong(eventExportTaskQuery.getConditionEndTime())));
		}
		queryWrapper.orderByDesc("export_time");
		IPage<EventinfoExportTask> pages = eventInfoExportTaskService.page(Condition.getPage(eventExportTaskQuery), queryWrapper);
		IPage<EventinfoExportTaskVO> trackExportTaskVOIPage = EventinfoExportTaskWrapper.build().pageVO(pages);
		List<EventinfoExportTaskVO> records = trackExportTaskVOIPage.getRecords();
		if (CollectionUtil.isNotEmpty(records)){
			records.forEach(record->{
				Integer exportStatus = record.getExportStatus();
				if (exportStatus!=null){
					String export_status = dictClient.getValue("export_status", exportStatus.toString()).getData();
					record.setExportStatusName(export_status);
				}
			});

		}
		return R.data(trackExportTaskVOIPage);
	}



	/**
	 * 新增 事件记录导出任务表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入eventInfoExportTask")
	public R save(@Valid @RequestBody EventinfoExportTaskDTO eventInfoExportTask,BladeUser user) {
	    String tenantId= "";
        if (user != null) {
            tenantId = user.getTenantId();
        }
		JSONObject jsonObject=new JSONObject();
        jsonObject.put("belongArea", eventInfoExportTask.getBelongArea());
        jsonObject.put("handleStatus", eventInfoExportTask.getHandleStatus());
        jsonObject.put("eventType", eventInfoExportTask.getEventType());
        jsonObject.put("eventInspectType", eventInfoExportTask.getCheckType());
        jsonObject.put("eventLevel", eventInfoExportTask.getEventLevel());
        jsonObject.put("conditionEventIds", Func.toLongList(eventInfoExportTask.getConditionEventIds()));
		jsonObject.put("conditionStartDate",eventInfoExportTask.getConditionStartDate().toString());
        jsonObject.put("conditionEndDate",eventInfoExportTask.getConditionEndDate().toString());
        String conditionString = jsonObject.toJSONString();
        eventInfoExportTask.setExportTime(TimeUtil.getSysDate());
        eventInfoExportTask.setExportCondition(conditionString);


        List<Long> eventIds = new ArrayList<>();
        if (eventInfoExportTask.getConditionEventIds() != null && StringUtils.isNotBlank(eventInfoExportTask.getConditionEventIds())&& !"[]".equals(eventInfoExportTask.getConditionEventIds())) {
            eventIds = Func.toLongList(eventInfoExportTask.getConditionEventIds());
        }else {  //无勾选或全部导出
            EventQueryDTO eventQueryDTO = new EventQueryDTO();
            eventQueryDTO.setStartTime(eventInfoExportTask.getConditionStartDate().getTime());
            eventQueryDTO.setEndTime(eventInfoExportTask.getConditionEndDate().getTime());
            if (eventInfoExportTask.getBelongArea() != null && StringUtils.isNotBlank(eventInfoExportTask.getBelongArea())) {
                eventQueryDTO.setBelongArea(Long.valueOf(eventInfoExportTask.getBelongArea()));
            }
            if (eventInfoExportTask.getEventType() != null && StringUtils.isNotBlank(eventInfoExportTask.getEventType())) {
                eventQueryDTO.setEventType(eventInfoExportTask.getEventType());
            }
            if (eventInfoExportTask.getCheckType() != null && StringUtils.isNotBlank(eventInfoExportTask.getCheckType())) {
                eventQueryDTO.setEventInspectType(eventInfoExportTask.getCheckType());
            }
            if (eventInfoExportTask.getEventLevel() != null && StringUtils.isNotBlank(eventInfoExportTask.getEventLevel())) {
                eventQueryDTO.setEventLevel(eventInfoExportTask.getEventLevel());
            }
            if (eventInfoExportTask.getHandleStatus() != null && StringUtils.isNotBlank(eventInfoExportTask.getHandleStatus())) {
                eventQueryDTO.setStatus(Integer.valueOf(eventInfoExportTask.getHandleStatus()));
            }
            if(StringUtils.isNotBlank(tenantId)) {
                eventQueryDTO.setTenantId(tenantId);
            }
            List<EventInfoVO> eventInfoVOList = eventinfoClient.listEventInfoByParam(eventQueryDTO).getData();
            if (eventInfoVOList != null && eventInfoVOList.size() > 0) {
                for (EventInfoVO eventInfoVO : eventInfoVOList) {
                    eventIds.add(eventInfoVO.getId());
                }
            }
        }

		return R.status(eventInfoExportTaskService.addEventinfoExport(eventInfoExportTask,eventIds));
	}


	@GetMapping("/exportagain/{id}")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "重新执行", notes = "传入导出记录id")
	@ApiLog(value = "重新事件导出任务")
	public R exportEventAgain(@PathVariable Long id,BladeUser user){
		EventinfoExportTask task = eventInfoExportTaskService.getById(id);
        if (task != null && task.getBelongArea() != null) {
            String condition = task.getExportCondition();
            JSONObject jsonObject = JSON.parseObject(condition);
            String conditionBeginTime = jsonObject.getString("conditionStartDate");
            String conditionEndTime = jsonObject.getString("conditionEndDate");
            String belongArea = jsonObject.getString("belongArea");
            String handleStatus = jsonObject.getString("handleStatus");
            String eventType = jsonObject.getString("eventType");
            String eventInspectType = jsonObject.getString("eventInspectType");
            String eventLevel = jsonObject.getString("eventLevel");
            String conditionEventIds = jsonObject.getString("conditionEventIds");
            String tenantId = user.getTenantId();



            List<Long> eventIds = new ArrayList<>();
            if (StringUtils.isNotBlank(conditionEventIds) && !"[]".equals(conditionEventIds)) {
                eventIds = Func.toLongList(conditionEventIds.replace("[","").replace("]",""));
            }else {  //无勾选或全部导出
                EventQueryDTO eventQueryDTO = new EventQueryDTO();
                eventQueryDTO.setStartTime(TimeUtil.stringParseTimeStamp(conditionBeginTime).getTime());
                eventQueryDTO.setEndTime(TimeUtil.stringParseTimeStamp(conditionEndTime).getTime());
                if (StringUtils.isNotBlank(belongArea)) {
                    eventQueryDTO.setBelongArea(Long.valueOf(belongArea));
                }
                if (StringUtils.isNotBlank(eventType)) {
                    eventQueryDTO.setEventType(eventType);
                }
                if (StringUtils.isNotBlank(eventInspectType)) {
                    eventQueryDTO.setEventInspectType(eventInspectType);
                }
                if (StringUtils.isNotBlank(eventLevel)) {
                    eventQueryDTO.setEventLevel(eventLevel);
                }
                if (StringUtils.isNotBlank(handleStatus)) {
                    eventQueryDTO.setStatus(Integer.valueOf(handleStatus));
                }
                if (StringUtils.isNotBlank(tenantId)) {
                    eventQueryDTO.setTenantId(tenantId);
                }
                List<EventInfoVO> eventInfoVOList = eventinfoClient.listEventInfoByParam(eventQueryDTO).getData();
                if (eventInfoVOList != null && eventInfoVOList.size() > 0) {
                    for (EventInfoVO eventInfoVO : eventInfoVOList) {
                        eventIds.add(eventInfoVO.getId());
                    }
                }
            }

            return R.status(eventInfoExportTaskService.addEventinfoExport(task,eventIds));
        }


		R<String> data = R.data(null);
		data.setMsg("执行开始");
		return data;
	}
	
}
