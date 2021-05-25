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
package com.ai.apac.smartenv.address.service;

import com.ai.apac.smartenv.address.entity.AttendanceExportTask;
import com.ai.apac.smartenv.address.vo.AttendanceExportTaskVO;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.secure.BladeUser;
import org.springframework.scheduling.annotation.Async;

/**
 * 考勤记录导出任务表 服务类
 *
 * @author Blade
 * @since 2020-05-12
 */
public interface IAttendanceExportTaskService extends BaseService<AttendanceExportTask> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param attendanceExportTask
	 * @return
	 */
	IPage<AttendanceExportTaskVO> selectAttendanceExportTaskPage(IPage<AttendanceExportTaskVO> page, AttendanceExportTaskVO attendanceExportTask);

	boolean addAttendanceExport(AttendanceExportTask exportTask, BladeUser user);

	boolean reExport(Long id, BladeUser user);
}
