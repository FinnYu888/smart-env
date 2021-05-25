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

import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.vo.ScheduleVO;
import com.ai.apac.smartenv.omnic.vo.QScheduleObjectVO;

import java.util.List;

import javax.validation.Valid;

import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;

import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 排班表 服务类
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface IScheduleService extends BaseService<Schedule> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param schedule
	 * @return
	 */
	IPage<ScheduleVO> selectSchedulePage(IPage<ScheduleVO> page, ScheduleVO schedule);

	/**
	 * 
	 * @Function: IScheduleService::updateByScheduleById
	 * @Description: 修改，字段wznull，则更新表为null
	 * @param schedule
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月14日 下午4:20:47 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	Integer updateByScheduleById(Schedule schedule);

	/**
	 * 
	 * @Function: IScheduleService::getSchedulePeriod
	 * @Description: 获取该班次每周情况
	 * @param scheduleVO
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月15日 上午11:06:31 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<String> getSchedulePeriod(Schedule schedule);

	/**
	 * 
	 * @Function: IScheduleService::getSchedulePriods
	 * @Description: 获取该班次每周情况
	 * @param scheduleId
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月15日 上午11:06:14 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<Integer> getSchedulePriods(Long scheduleId);

	/**
	 * 
	 * @Function: IScheduleService::page
	 * @Description: 分页查询
	 * @param schedule
	 * @param query
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月22日 下午4:14:17 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	IPage<Schedule> page(ScheduleVO schedule, Query query);

	String checkScheduleTime(Schedule schedule);

	String buildScheduleTime(Schedule schedule);

	/**
	 * 
	 * @Function: IScheduleService::listAll
	 * @Description: 条件查询所有
	 * @param schedule
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月4日 下午8:09:41 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<Schedule> listAll(ScheduleVO schedule);

	/**
	 * 
	 * @Function: IScheduleService::saveSchedule
	 * @Description: 保存班次
	 * @param schedule
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月4日 下午8:09:18 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	boolean saveSchedule(Schedule schedule);

	Long syncSchedule(Schedule schedule);


	/**
	 * 
	 * @Function: IScheduleService::removeSchedule
	 * @Description: 逻辑删除班次
	 * @param ids
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @return 
	 * @date: 2020年3月4日 下午8:09:29 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	boolean removeSchedule(List<Long> ids);

}
