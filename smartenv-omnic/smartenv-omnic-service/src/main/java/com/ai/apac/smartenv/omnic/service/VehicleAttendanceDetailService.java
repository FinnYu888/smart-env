package com.ai.apac.smartenv.omnic.service;

import com.ai.apac.smartenv.arrange.entity.ScheduleAttendance;
import com.ai.apac.smartenv.omnic.dto.AttendanceDetailDTO;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleAttendanceDetailDTO
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/5/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/5/12 19:29     panfeng          v1.0.0             修改原因
 */
public interface VehicleAttendanceDetailService {
    AttendanceDetailDTO getAttendanceDetail(ScheduleAttendance attendance) throws Exception;
}
