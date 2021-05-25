package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.arrange.entity.ScheduleAttendance;
import com.ai.apac.smartenv.omnic.dto.AttendanceDetailDTO;
import com.ai.apac.smartenv.omnic.service.VehicleAttendanceDetailService;
import lombok.RequiredArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: AttendanceClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/5/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/5/14 15:07     panfeng          v1.0.0             修改原因
 */
@ApiIgnore
@RestController
@RequiredArgsConstructor
public class AttendanceClient implements IAttendanceClient{

    @Autowired
    private VehicleAttendanceDetailService attendanceDetailService;


    /**
     * 获取导出对象
     * @param attendance
     * @return
     */
    @Override
    @PostMapping(GET_VEHICLE_DETAIL)
    public R<AttendanceDetailDTO> getAttendanceDetail(@RequestBody ScheduleAttendance attendance) {
        try {
            AttendanceDetailDTO vehicleAttendanceDetail = attendanceDetailService.getAttendanceDetail(attendance);
            return R.data(vehicleAttendanceDetail);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

}
