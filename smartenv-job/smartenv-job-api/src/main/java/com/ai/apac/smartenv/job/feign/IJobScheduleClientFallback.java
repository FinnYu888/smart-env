package com.ai.apac.smartenv.job.feign;

import com.ai.apac.smartenv.arrange.entity.Schedule;
import org.springblade.core.tool.api.R;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IScheduleClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  15:04    panfeng          v1.0.0             修改原因
 */
public class IJobScheduleClientFallback implements IJobScheduleClient {
    @Override
    public R reloadAllTodaySchedule() {
        return R.fail("接收数据失败");
    }

    @Override
    public R addTodaySchedule(Schedule schedule) {
        return R.fail("接收数据失败");
    }
}
