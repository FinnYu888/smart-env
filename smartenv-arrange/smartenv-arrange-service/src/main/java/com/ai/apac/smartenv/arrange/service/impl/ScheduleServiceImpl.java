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
package com.ai.apac.smartenv.arrange.service.impl;

import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.arrange.vo.ScheduleVO;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.arrange.mapper.ScheduleMapper;
import com.ai.apac.smartenv.arrange.service.IScheduleObjectService;
import com.ai.apac.smartenv.arrange.service.IScheduleService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.ai.apac.smartenv.job.feign.IJobScheduleClient;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 排班表 服务实现类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Service
public class ScheduleServiceImpl extends BaseServiceImpl<ScheduleMapper, Schedule> implements IScheduleService {

	@Autowired
	@Lazy
	private IScheduleObjectService scheduleObjectService;

	@Autowired
	@Lazy
	private IJobScheduleClient jobScheduleClient;
	
	@Override
	public IPage<ScheduleVO> selectSchedulePage(IPage<ScheduleVO> page, ScheduleVO schedule) {
		return page.setRecords(baseMapper.selectSchedulePage(page, schedule));
	}

	@Override
	public Integer updateByScheduleById(Schedule schedule) {
		QueryWrapper<ScheduleObject> queryWrapper = new QueryWrapper<>();
		queryWrapper.in("schedule_id", schedule.getId());
		int count = scheduleObjectService.count(queryWrapper);
		if (count > 0) {
			throw new ServiceException("已设置考勤，不允许修改");
		}
		String scheduleName = checkScheduleTime(schedule);
		if (StringUtils.isNotBlank(scheduleName)) {
			throw new ServiceException(scheduleName + ", 已有相同班次周期和时间，不允许提交");
		}
		BladeUser user = AuthUtil.getUser();
		if (user != null) {
			schedule.setUpdateUser(user.getUserId());
		}
		schedule.setUpdateTime(DateUtil.now());
		schedule.setScheduleMonday(schedule.getScheduleMonday() != null && schedule.getScheduleMonday() == 1 ? 1 : 0);
		schedule.setScheduleTuesday(schedule.getScheduleTuesday() != null && schedule.getScheduleTuesday() == 1 ? 1 : 0);
		schedule.setScheduleWednesday(schedule.getScheduleWednesday() != null && schedule.getScheduleWednesday() == 1 ? 1 : 0);
		schedule.setScheduleThursday(schedule.getScheduleThursday() != null && schedule.getScheduleThursday() == 1 ? 1 : 0);
		schedule.setScheduleFriday(schedule.getScheduleFriday() != null && schedule.getScheduleFriday() == 1 ? 1 : 0);
		schedule.setScheduleSaturday(schedule.getScheduleSaturday() != null && schedule.getScheduleSaturday() == 1 ? 1 : 0);
		schedule.setScheduleSunday(schedule.getScheduleSunday() != null && schedule.getScheduleSunday() == 1 ? 1 : 0);
		// 取修改前比较修改的内容
		Integer updateFlag = ArrangeConstant.UPDATE_SCHEDULE_FLAG.UPDATE_NAME;
		Long scheduleId = schedule.getId();
		Schedule oldSchedule = ScheduleCache.getScheduleById(scheduleId);
		Date oldScheduleBeginTime = oldSchedule.getScheduleBeginTime();
		Date oldScheduleEndTime = oldSchedule.getScheduleEndTime();
		Date oldBreaksBeginTime = oldSchedule.getBreaksBeginTime();
		Date oldBreaksEndTime = oldSchedule.getBreaksEndTime();
		if (!oldScheduleBeginTime.equals(schedule.getScheduleBeginTime())
				||!oldScheduleEndTime.equals(schedule.getScheduleEndTime())
				||(oldBreaksBeginTime == null && schedule.getBreaksBeginTime() != null)
				||(oldBreaksBeginTime != null && schedule.getBreaksBeginTime() == null)
				||(oldBreaksBeginTime != null && schedule.getBreaksBeginTime() != null && !oldBreaksBeginTime.equals(schedule.getBreaksBeginTime()))
				||(oldBreaksEndTime == null && schedule.getBreaksEndTime() != null)
				||(oldBreaksEndTime != null && schedule.getBreaksEndTime() == null)
				||(oldBreaksEndTime != null && schedule.getBreaksEndTime() != null && !oldBreaksEndTime.equals(schedule.getBreaksEndTime()))
				) {
			updateFlag = ArrangeConstant.UPDATE_SCHEDULE_FLAG.UPDATE_TIME;
		}
		Integer oldScheduleMonday = oldSchedule.getScheduleMonday();
		Integer oldScheduleTuesday = oldSchedule.getScheduleTuesday();
		Integer oldScheduleWednesday = oldSchedule.getScheduleWednesday();
		Integer oldScheduleThursday = oldSchedule.getScheduleThursday();
		Integer oldScheduleFriday = oldSchedule.getScheduleFriday();
		Integer oldScheduleSaturday = oldSchedule.getScheduleSaturday();
		Integer oldScheduleSunday = oldSchedule.getScheduleSunday();
		if (oldScheduleMonday != schedule.getScheduleMonday()
				|| oldScheduleTuesday != schedule.getScheduleTuesday()
				|| oldScheduleWednesday != schedule.getScheduleWednesday()
				|| oldScheduleThursday != schedule.getScheduleThursday()
				|| oldScheduleFriday != schedule.getScheduleFriday()
				|| oldScheduleSaturday != schedule.getScheduleSaturday()
				|| oldScheduleSunday != schedule.getScheduleSunday()) {
			updateFlag = ArrangeConstant.UPDATE_SCHEDULE_FLAG.UPDATE_PERIODS;
		}
		ScheduleCache.delSchedule(schedule.getId());
		baseMapper.updateByScheduleById(schedule);
		return updateFlag;
	}

	@Override
	public List<String> getSchedulePeriod(Schedule schedule) {
		List<String> schedulePeriodList = new ArrayList<>();
		if (schedule.getScheduleMonday() != null && schedule.getScheduleMonday() == ArrangeConstant.TureOrFalse.INT_TRUE) {
			schedulePeriodList.add(ArrangeConstant.SchedulePeriod.MONDAY);
		}
		if (schedule.getScheduleTuesday() != null && schedule.getScheduleTuesday() == ArrangeConstant.TureOrFalse.INT_TRUE) {
			schedulePeriodList.add(ArrangeConstant.SchedulePeriod.TUESDAY);
		}
		if (schedule.getScheduleWednesday() != null && schedule.getScheduleWednesday() == ArrangeConstant.TureOrFalse.INT_TRUE) {
			schedulePeriodList.add(ArrangeConstant.SchedulePeriod.WEDNESDAY);
		}
		if (schedule.getScheduleThursday() != null && schedule.getScheduleThursday() == ArrangeConstant.TureOrFalse.INT_TRUE) {
			schedulePeriodList.add(ArrangeConstant.SchedulePeriod.THURSDAY);
		}
		if (schedule.getScheduleFriday() != null && schedule.getScheduleFriday() == ArrangeConstant.TureOrFalse.INT_TRUE) {
			schedulePeriodList.add(ArrangeConstant.SchedulePeriod.FRIDAY);
		}
		if (schedule.getScheduleSaturday() != null && schedule.getScheduleSaturday() == ArrangeConstant.TureOrFalse.INT_TRUE) {
			schedulePeriodList.add(ArrangeConstant.SchedulePeriod.SATURDAY);
		}
		if (schedule.getScheduleSunday() != null && schedule.getScheduleSunday() == ArrangeConstant.TureOrFalse.INT_TRUE) {
			schedulePeriodList.add(ArrangeConstant.SchedulePeriod.SUNDAY);
		}
		return schedulePeriodList;
	}

	@Override
	public List<Integer> getSchedulePriods(Long scheduleId) {
		List<Integer> schedulePriods = new ArrayList<>();
		if (scheduleId.equals(ArrangeConstant.BREAK_SCHEDULE_ID)) {
			schedulePriods.add(0);
			schedulePriods.add(0);
			schedulePriods.add(0);
			schedulePriods.add(0);
			schedulePriods.add(0);
			schedulePriods.add(0);
			schedulePriods.add(0);
			return schedulePriods;
		}
		Schedule schedule = getById(scheduleId);
		if (schedule == null) {
			throw new ServiceException("没有该班次");
		}
		schedulePriods.add(schedule.getScheduleMonday());
		schedulePriods.add(schedule.getScheduleTuesday());
		schedulePriods.add(schedule.getScheduleWednesday());
		schedulePriods.add(schedule.getScheduleThursday());
		schedulePriods.add(schedule.getScheduleFriday());
		schedulePriods.add(schedule.getScheduleSaturday());
		schedulePriods.add(schedule.getScheduleSunday());
		return schedulePriods;
	}

	@Override
	public IPage<Schedule> page(ScheduleVO schedule, Query query) {
		QueryWrapper<Schedule> queryWrapper = generateQueryWrapper(schedule);
		return page(Condition.getPage(query), queryWrapper);
	}

	private QueryWrapper<Schedule> generateQueryWrapper(ScheduleVO schedule) {
		// 处理排班周期条件
		String schedulePeriod = schedule.getSchedulePeriod();
		if (StringUtils.isNotBlank(schedulePeriod)) {
			String[] schedulePeriods = schedulePeriod.split(",");
			if (schedulePeriods != null && schedulePeriods.length > 0) {
				schedule.setScheduleMonday(Integer.parseInt(schedulePeriods[0]));
			}
			if (schedulePeriods != null && schedulePeriods.length > 1) {
				schedule.setScheduleTuesday(Integer.parseInt(schedulePeriods[1]));
			}
			if (schedulePeriods != null && schedulePeriods.length > 2) {
				schedule.setScheduleWednesday(Integer.parseInt(schedulePeriods[2]));
			}
			if (schedulePeriods != null && schedulePeriods.length > 3) {
				schedule.setScheduleThursday(Integer.parseInt(schedulePeriods[3]));
			}
			if (schedulePeriods != null && schedulePeriods.length > 4) {
				schedule.setScheduleFriday(Integer.parseInt(schedulePeriods[4]));
			}
			if (schedulePeriods != null && schedulePeriods.length > 5) {
				schedule.setScheduleSaturday(Integer.parseInt(schedulePeriods[5]));
			}
			if (schedulePeriods != null && schedulePeriods.length > 6) {
				schedule.setScheduleSunday(Integer.parseInt(schedulePeriods[6]));
			}
		}
		String scheduleName = schedule.getScheduleName();
		schedule.setScheduleName(null);
		QueryWrapper<Schedule> queryWrapper = Condition.getQueryWrapper(schedule);
		if (StringUtils.isNotBlank(scheduleName)) {
			queryWrapper.like("schedule_name", scheduleName);
		}
		if (StringUtils.isNotBlank(schedule.getTenantId())) {
			queryWrapper.eq("tenant_id", schedule.getTenantId());
		} else {
			BladeUser user = AuthUtil.getUser();
    		if (user != null) {
    			queryWrapper.eq("tenant_id", user.getTenantId());
    		}
		}
		queryWrapper.orderByDesc("update_time");
		return queryWrapper;
	}

	@Override
	public List<Schedule> listAll(ScheduleVO schedule) {
		QueryWrapper<Schedule> queryWrapper = generateQueryWrapper(schedule);
		return list(queryWrapper);
	}

	@Override
	public String checkScheduleTime(Schedule schedule) {
		QueryWrapper<Schedule> queryWrapper = new QueryWrapper<>();
		if (schedule.getId() != null) {
			queryWrapper.notIn("id", schedule.getId());
		}
		queryWrapper.eq("schedule_monday", schedule.getScheduleMonday() != null && schedule.getScheduleMonday() == 1 ? 1 : 0);
		queryWrapper.eq("schedule_tuesday", schedule.getScheduleTuesday() != null && schedule.getScheduleTuesday() == 1 ? 1 : 0);
		queryWrapper.eq("schedule_wednesday", schedule.getScheduleWednesday() != null && schedule.getScheduleWednesday() == 1 ? 1 : 0);
		queryWrapper.eq("schedule_thursday", schedule.getScheduleThursday() != null && schedule.getScheduleThursday() == 1 ? 1 : 0);
		queryWrapper.eq("schedule_friday", schedule.getScheduleFriday() != null && schedule.getScheduleFriday() == 1 ? 1 : 0);
		queryWrapper.eq("schedule_saturday", schedule.getScheduleSaturday() != null && schedule.getScheduleSaturday() == 1 ? 1 : 0);
		queryWrapper.eq("schedule_sunday", schedule.getScheduleSunday() != null && schedule.getScheduleSunday() == 1 ? 1 : 0);
		queryWrapper.eq("schedule_begin_time", schedule.getScheduleBeginTime());
		queryWrapper.eq("schedule_end_time", schedule.getScheduleEndTime());
		if (schedule.getBreaksBeginTime() == null) {
			queryWrapper.isNull("breaks_begin_time");
		}
		if (schedule.getBreaksBeginTime() != null) {
			queryWrapper.eq("breaks_begin_time", schedule.getBreaksBeginTime());
		}
		if (schedule.getBreaksEndTime() == null) {
			queryWrapper.isNull("breaks_end_time");
		}
		if (schedule.getBreaksEndTime() != null) {
			queryWrapper.eq("breaks_end_time", schedule.getBreaksEndTime());
		}
		BladeUser user = AuthUtil.getUser();
		if (user != null) {
			queryWrapper.eq("tenant_id", user.getTenantId());
		}		
		List<Schedule> list = list(queryWrapper);
		if (list != null && list.size() > 0) {
			return list.get(0).getScheduleName();
		}
		return null;
	}

	@Override
	public String buildScheduleTime(Schedule schedule) {
		Date scheduleBeginTime = schedule.getScheduleBeginTime();
		Date scheduleEndTime = schedule.getScheduleEndTime();
		Date breaksBeginTime = schedule.getBreaksBeginTime();
		Date breaksEndTime = schedule.getBreaksEndTime();
		StringBuilder scheduleTime = new StringBuilder();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		if (scheduleBeginTime != null && scheduleEndTime != null) {
			scheduleTime.append(formatter.format(scheduleBeginTime) + ArrangeConstant.DATE_SEPARATION + formatter.format(scheduleEndTime));
		}
		if (breaksBeginTime != null && breaksEndTime != null) {
			scheduleTime.append("(休息时间:" + formatter.format(breaksBeginTime) + ArrangeConstant.DATE_SEPARATION + formatter.format(breaksEndTime) + ")");
		}
		return scheduleTime.toString();
	}

	@Override
	public boolean saveSchedule(Schedule schedule) {
		String scheduleName = checkScheduleTime(schedule);
		if (StringUtils.isNotBlank(scheduleName)) {
			throw new ServiceException(scheduleName + ", 已有相同班次周期和时间，不允许提交");
		}
		ScheduleCache.delSchedule(schedule.getId());
		boolean save = save(schedule);
		if (save){
			jobScheduleClient.addTodaySchedule(schedule);
		}
		return save;
	}

	@Override
	public Long syncSchedule(Schedule schedule) {
		QueryWrapper<Schedule> queryWrapper = new QueryWrapper<>();

		queryWrapper.eq("schedule_begin_time", schedule.getScheduleBeginTime());
		queryWrapper.eq("schedule_end_time", schedule.getScheduleEndTime());
		if (schedule.getBreaksBeginTime() == null) {
			queryWrapper.isNull("breaks_begin_time");
		}
		if (schedule.getBreaksBeginTime() != null) {
			queryWrapper.eq("breaks_begin_time", schedule.getBreaksBeginTime());
		}
		if (schedule.getBreaksEndTime() == null) {
			queryWrapper.isNull("breaks_end_time");
		}
		if (schedule.getBreaksEndTime() != null) {
			queryWrapper.eq("breaks_end_time", schedule.getBreaksEndTime());
		}
		BladeUser user = AuthUtil.getUser();
		if (user != null) {
			queryWrapper.eq("tenant_id", user.getTenantId());
		}
		List<Schedule> list = list(queryWrapper);
		if (list != null && list.size() > 0) {
			return list.get(0).getId();
		}
		ScheduleCache.delSchedule(schedule.getId());
		save(schedule);
		return schedule.getId();

	}

	@Override
	public boolean removeSchedule(List<Long> ids) {
		// 校验是否已设置考勤
		QueryWrapper<ScheduleObject> queryWrapper = new QueryWrapper<>();
		queryWrapper.in("schedule_id", ids);
		int count = scheduleObjectService.count(queryWrapper);
		if (count > 0) {
			throw new ServiceException("已设置考勤，不允许删除");
		}
		for (Long id : ids) {
			ScheduleCache.delSchedule(id);
			deleteLogic(Arrays.asList(id));
		}
		return true;		
	}

}
