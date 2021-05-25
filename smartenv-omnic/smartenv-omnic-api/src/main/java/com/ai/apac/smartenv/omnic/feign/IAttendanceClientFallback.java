package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.arrange.entity.ScheduleAttendance;
import com.ai.apac.smartenv.omnic.dto.AttendanceDetailDTO;
import org.springblade.core.tool.api.R;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IAttendanceClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/5/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/5/14 15:06     panfeng          v1.0.0             修改原因
 */
public class IAttendanceClientFallback implements IAttendanceClient{
    @Override
    public R<AttendanceDetailDTO> getAttendanceDetail(ScheduleAttendance attendance) {
        return R.fail("获取数据失败");
    }
}
