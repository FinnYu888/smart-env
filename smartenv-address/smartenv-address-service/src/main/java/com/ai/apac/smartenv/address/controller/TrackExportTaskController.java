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

import com.ai.apac.smartenv.address.dto.TrackExportTaskDTO;
import com.ai.apac.smartenv.address.query.TrackExportTaskQuery;
import com.ai.apac.smartenv.address.service.IAddressAsyncService;
import com.ai.apac.smartenv.common.constant.AddressConstant;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.address.entity.TrackExportTask;
import com.ai.apac.smartenv.address.vo.TrackExportTaskVO;
import com.ai.apac.smartenv.address.wrapper.TrackExportTaskWrapper;
import com.ai.apac.smartenv.address.service.ITrackExportTaskService;
import org.springblade.core.boot.ctrl.BladeController;

import java.sql.Timestamp;
import java.util.List;

/**
 * 历史轨迹导出任务表 控制器
 *
 * @author Blade
 * @since 2020-03-03
 */
@RestController
@AllArgsConstructor
@RequestMapping("/trackexporttask")
@Api(value = "历史轨迹导出任务表", tags = "历史轨迹导出任务表接口")
public class TrackExportTaskController extends BladeController {

	private ITrackExportTaskService trackExportTaskService;
	private IVehicleClient vehicleClient;

	private IPersonClient personClient;

	private IDictClient dictClient;


	@Autowired
	@Lazy
	private IAddressAsyncService addressAsyncService;


	@GetMapping("/reTask/{id}")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "重新执行", notes = "传入trackExportTask")
	@ApiLog(value = "重新历史轨迹导出任务")
	public R reTask(@PathVariable Long id){
		TrackExportTask task = trackExportTaskService.getById(id);
		BladeUser bladeUser=getUser();

		TrackExportTask updateEntity=new TrackExportTask();
		updateEntity.setId(id);
		updateEntity.setExportStatus(AddressConstant.ExportStatus.EXPORTING);
		trackExportTaskService.updateById(updateEntity);
		addressAsyncService.exportExcelToOss(task,bladeUser);
		R<String> data = R.data(null);
		data.setMsg("执行开始");
		return data;
	}


	/**
	 * 自定义分页 历史轨迹导出任务表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入trackExportTask")
	@ApiLog(value = "分页查询历史轨迹导出任务")
	public R<IPage<TrackExportTaskVO>> page( TrackExportTaskQuery trackExportTask) {
		TrackExportTask exportTask=new TrackExportTask();
		BeanUtils.copyProperties(trackExportTask,exportTask);
		QueryWrapper<TrackExportTask> queryWrapper = Condition.getQueryWrapper(exportTask);
		String entityName = trackExportTask.getEntityName();
		if (StringUtil.isNotBlank(entityName)){
			queryWrapper.like("entity_name",entityName);
		}
		if (trackExportTask.getConditionBeginTime()!=null){
			queryWrapper.gt("export_time",new Timestamp(Long.parseLong(trackExportTask.getConditionBeginTime())));
		}
		if (trackExportTask.getConditionEndTime()!=null){
			queryWrapper.lt("export_time",new Timestamp(Long.parseLong(trackExportTask.getConditionEndTime())));
		}
		queryWrapper.orderByDesc("export_time");
		IPage<TrackExportTask> pages = trackExportTaskService.page(Condition.getPage(trackExportTask), queryWrapper);
		IPage<TrackExportTaskVO> trackExportTaskVOIPage = TrackExportTaskWrapper.build().pageVO(pages);
		List<TrackExportTaskVO> records = trackExportTaskVOIPage.getRecords();
		if (CollectionUtil.isNotEmpty(records)){
			records.forEach(record->{
				JSONObject jsonObject = JSON.parseObject(record.getExportCondition());
				Long conditionBeginTime = jsonObject.getLong("conditionBeginTime");
				Long conditionEndTime = jsonObject.getLong("conditionEndTime");
				Long deviceStatus = jsonObject.getLong("deviceStatus");
				Integer exportStatus = record.getExportStatus();
				if (conditionBeginTime!=null){
					record.setConditionBeginTime(new Timestamp(conditionBeginTime));
				}
				if (conditionEndTime!=null){
					record.setConditionEndTime(new Timestamp(conditionEndTime));
				}
				if (deviceStatus!=null){
					String device_status = dictClient.getValue("BIGDATA_DEVICE_STATUS", deviceStatus.toString()).getData();
					record.setDeviceStatusName(device_status);
				}
				if (exportStatus!=null){
					String export_status = dictClient.getValue("export_status", exportStatus.toString()).getData();
					record.setExportStatusName(export_status);
				}
			});

		}

		return R.data(trackExportTaskVOIPage);
	}


	/**
	 * 新增 历史轨迹导出任务表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入trackExportTask")
	@ApiLog(value = "新增历史轨迹导出任务")
	public R save(@Valid @RequestBody TrackExportTaskDTO save) {


		BladeUser user = getUser();

		JSONObject jsonObject=new JSONObject();
		jsonObject.put("conditionBeginTime",save.getConditionBeginTime());
		jsonObject.put("conditionEndTime",save.getConditionEndTime());
		jsonObject.put("deviceStatus",save.getDeviceStatus());
		String conditionString = jsonObject.toJSONString();

		TrackExportTask queryDto=new TrackExportTask();
		queryDto.setExportCondition(conditionString);
		queryDto.setEntityType(save.getEntityType());
		queryDto.setEntityId(save.getEntityId());
		Wrapper<TrackExportTask> queryWrapper=new QueryWrapper<>(queryDto);
		List<TrackExportTask> list = trackExportTaskService.list(queryWrapper);
		if (CollectionUtil.isNotEmpty(list)){
//			throw new ServiceException("该查询条件已经存在");

			R<String> data = R.data(null);
			data.setMsg("该查询条件已经存在");
			return data;
		}
		TrackExportTask trackExportTask=new TrackExportTask();
		BeanUtils.copyProperties(save,trackExportTask);
		trackExportTask.setExportCondition(conditionString);
		trackExportTask.setExportStatus(AddressConstant.ExportStatus.EXPORTING);
		trackExportTask.setExportTime(new Timestamp(System.currentTimeMillis()));
		Integer entityType = trackExportTask.getEntityType();
		String name=null;
		if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(entityType.longValue())){
			VehicleInfo data = vehicleClient.vehicleInfoById(trackExportTask.getEntityId()).getData();
			trackExportTask.setEntityName(data.getPlateNumber());

		}else if (CommonConstant.ENTITY_TYPE.PERSON.equals(entityType.longValue())){
			Person data = personClient.getPerson(trackExportTask.getEntityId()).getData();
			trackExportTask.setEntityName(data.getPersonName()+"("+data.getJobNumber()+")");
		}

		boolean resu = trackExportTaskService.addTrackExportTask(trackExportTask,user);
		return R.status(resu);
	}


	
}
