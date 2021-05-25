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
package com.ai.apac.smartenv.arrange.service;

import java.time.LocalDate;

import javax.validation.Valid;

import org.springblade.core.secure.BladeUser;
import org.springframework.scheduling.annotation.AsyncResult;

import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.vo.ScheduleObjectVO;

public interface IArrangeAsyncService {

	void submitArrangeAsync(ScheduleObjectVO scheduleObject, BladeUser bladeUser);

	void syncForChangeSchedule(Schedule schedule, LocalDate now);

	void syncForChangePeriods(Schedule schedule, LocalDate now);

}
