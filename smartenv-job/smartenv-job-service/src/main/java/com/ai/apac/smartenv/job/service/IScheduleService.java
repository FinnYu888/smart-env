package com.ai.apac.smartenv.job.service;

import com.ai.apac.smartenv.arrange.entity.Schedule;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IScheduleService
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  14:47    panfeng          v1.0.0             修改原因
 */
public interface IScheduleService {

    Boolean addTodaySchedule(Schedule schedule);
}
