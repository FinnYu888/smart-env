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

import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.vo.ScheduleVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 排班表 Mapper 接口
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface ScheduleMapper extends BaseMapper<Schedule> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param schedule
	 * @return
	 */
	List<ScheduleVO> selectSchedulePage(IPage page, ScheduleVO schedule);

	Integer updateByScheduleById(@Param("record")Schedule schedule);

}
