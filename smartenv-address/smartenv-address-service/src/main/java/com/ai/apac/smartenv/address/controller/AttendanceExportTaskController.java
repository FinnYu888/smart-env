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

import com.ai.apac.smartenv.address.dto.AttendanceExportTaskDTO;
import com.ai.apac.smartenv.address.dto.AttendanceExportTaskQueryDTO;
import com.ai.apac.smartenv.common.constant.AddressConstant;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.EntityCategoryCache;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.address.entity.AttendanceExportTask;
import com.ai.apac.smartenv.address.vo.AttendanceExportTaskVO;
import com.ai.apac.smartenv.address.wrapper.AttendanceExportTaskWrapper;
import com.ai.apac.smartenv.address.service.IAttendanceExportTaskService;
import org.springblade.core.boot.ctrl.BladeController;
import springfox.documentation.spring.web.json.Json;

import java.util.*;

/**
 * 考勤记录导出任务表 控制器
 *
 * @author Blade
 * @since 2020-05-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/attendanceexporttask")
@Api(value = "考勤记录导出任务表", tags = "考勤记录导出任务表接口")
public class AttendanceExportTaskController extends BladeController {

	private IAttendanceExportTaskService attendanceExportTaskService;


	private ISysClient sysClient;


	private IEntityCategoryClient entityCategoryClient;

	/**
	 * 分页 考勤记录导出任务表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入attendanceExportTask")
	public R<IPage<AttendanceExportTaskVO>> list(AttendanceExportTaskQueryDTO attendanceExportTask, Query query) {
		AttendanceExportTask exportTask=new AttendanceExportTask();
		exportTask.setExportStatus(attendanceExportTask.getExportStatus());
		QueryWrapper<AttendanceExportTask> queryWrapper = Condition.getQueryWrapper(exportTask);
		if (attendanceExportTask.getCategoryId()!=null){
			queryWrapper.like("category_id_tags",attendanceExportTask.getCategoryId());
		}

		if (attendanceExportTask.getDeptId()!=null){
			queryWrapper.like("dept_id_tags",attendanceExportTask.getDeptId());

		}

		if (attendanceExportTask.getRegionId()!=null){
			queryWrapper.like("region_id_tags",attendanceExportTask.getRegionId());
		}
		if (attendanceExportTask.getQueryBeginTime()!=null&&attendanceExportTask.getQueryEndTime()!=null){
			queryWrapper.ge("export_time",new Date(attendanceExportTask.getQueryBeginTime()));
			queryWrapper.le("export_time",new Date(attendanceExportTask.getQueryEndTime()));
		}
		queryWrapper.orderByDesc("update_time");


		IPage<AttendanceExportTask> pages = attendanceExportTaskService.page(Condition.getPage(query), queryWrapper);
		Map<Long,String> categoryIdMap=new HashMap<>();
		Map<Long,String> deptIdMap=new HashMap<>();
		Map<Long,String> regionIdMap=new HashMap<>();
		IPage<AttendanceExportTaskVO> attendanceExportTaskVOIPage = AttendanceExportTaskWrapper.build().pageVO(pages);

		attendanceExportTaskVOIPage.getRecords().forEach(record->{
			String categoryIdTags = record.getCategoryIdTags();
			String deptIdTags = record.getDeptIdTags();
			String regionIdTags = record.getRegionIdTags();

			List<Long> categoryIds = Func.toLongList(",", categoryIdTags);
			List<Long> deptIds = Func.toLongList(",", deptIdTags);
			List<Long> regionIds = Func.toLongList(",", regionIdTags);

			List<String> categoryNames = new ArrayList<>();
			List<String> deptNames = new ArrayList<>();
			List<String> regionNames = new ArrayList<>();
			categoryIds.forEach(categoryId->{
				if(categoryIdMap.get(categoryId)!=null){
					categoryNames.add(categoryIdMap.get(categoryId));
					return;
				}
				String categoryName = EntityCategoryCache.getCategoryNameById(categoryId);

				if (StringUtil.isNotBlank(categoryName)){
					categoryIdMap.put(categoryId,categoryName);
					categoryNames.add(categoryName);
				}
			});

			deptIds.forEach(deptId->{
				if(deptIdMap.get(deptId)!=null){
					deptNames.add(deptIdMap.get(deptId));
					return;
				}
				Dept dept = DeptCache.getDept(deptId);

				if (dept.getId()!=null){
					deptIdMap.put(deptId,dept.getDeptName());
					deptNames.add(dept.getDeptName());
				}

			});
			regionIds.forEach(regionId->{
				if(regionIdMap.get(regionId)!=null){
					regionNames.add(regionIdMap.get(regionId));
					return;
				}
				Region region = sysClient.getRegion(regionId).getData();
				if (region.getId()!=null){
					regionIdMap.put(regionId,region.getRegionName());
					regionNames.add(region.getRegionName());
				}

			});
			Integer exportStatus = record.getExportStatus();

			record.setExportStatusName(DictCache.getValue("export_status", exportStatus));


			record.setCategoryNameTags(StringUtils.arrayToDelimitedString(categoryNames.toArray(),","));
			record.setDeptNameTags(StringUtils.arrayToDelimitedString(deptNames.toArray(),","));
			record.setRegionNameTags(StringUtils.arrayToDelimitedString(regionNames.toArray(),","));

			String exportCondition = record.getExportCondition();
			JSONObject jsonObject = JSON.parseObject(exportCondition);
			String conditionDate = jsonObject.getString("conditionDate");

			record.setConditionDate(conditionDate);


		});


		return R.data(attendanceExportTaskVOIPage);
	}


	/**
	 * 新增 考勤记录导出任务表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入attendanceExportTask")
	public R save(@Valid @RequestBody AttendanceExportTaskDTO attendanceExportTask) {

		JSONObject jsonObject=new JSONObject();
		jsonObject.put("conditionIds",attendanceExportTask.getConditionIds());
		jsonObject.put("conditionDate",attendanceExportTask.getConditionDate());
		jsonObject.put("conditionRegionId",attendanceExportTask.getConditionRegionId());
		jsonObject.put("conditionVehicleCategoryId",attendanceExportTask.getConditionVehicleCategoryId());
		jsonObject.put("conditionDeptId",attendanceExportTask.getConditionDeptId());
		jsonObject.put("plateNumber",attendanceExportTask.getPlateNumber());
		String conditionString = jsonObject.toJSONString();
		attendanceExportTask.setExportCondition(conditionString);
		attendanceExportTask.setExportTime(new Date());
		attendanceExportTask.setExportStatus(AddressConstant.ExportStatus.EXPORTING);


		return R.status(attendanceExportTaskService.addAttendanceExport(attendanceExportTask,getUser()));
	}


	@GetMapping("/reTask/{id}")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "重新执行", notes = "传入trackExportTask")
	@ApiLog(value = "重新考勤记录导出任务")
	public R<String> reTask(@PathVariable Long id){
		attendanceExportTaskService.reExport(id,getUser());
		return R.data("success");
	}

}
