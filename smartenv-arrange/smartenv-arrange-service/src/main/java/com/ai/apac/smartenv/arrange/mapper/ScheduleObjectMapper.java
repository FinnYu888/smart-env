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
package com.ai.apac.smartenv.arrange.mapper;

import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.vo.ScheduleObjectVO;
import com.ai.apac.smartenv.omnic.vo.QScheduleObjectVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 敏捷排班表 Mapper 接口
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface ScheduleObjectMapper extends BaseMapper<ScheduleObject> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param scheduleObject
	 * @return
	 */
	List<ScheduleObjectVO> selectScheduleObjectPage(IPage page, ScheduleObjectVO scheduleObject);

	ScheduleObject getByIdWithDel(@Param("param1") Long scheduleObjectId);

	Integer countByDate(@Param("scheduleDate") LocalDate scheduleDate, @Param("entityType") String entityType,
			@Param("tenantId") String tenantId, @Param("entityIdList") List<Long> entityIdList);

}
