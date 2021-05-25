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
package com.ai.apac.smartenv.arrange.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.vo.ScheduleVO;

/**
 * 排班表包装类,返回视图层所需的字段
 *
 * @author Blade
 * @since 2020-01-16
 */
public class ScheduleWrapper extends BaseEntityWrapper<Schedule, ScheduleVO>  {

	public static ScheduleWrapper build() {
		return new ScheduleWrapper();
 	}

	@Override
	public ScheduleVO entityVO(Schedule schedule) {
		ScheduleVO scheduleVO = BeanUtil.copy(schedule, ScheduleVO.class);

		return scheduleVO;
	}

}
