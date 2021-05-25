package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.arrange.entity.ScheduleAttendance;
import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.omnic.dto.AttendanceDetailDTO;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IAttendanceClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/5/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/5/14 15:06     panfeng          v1.0.0             修改原因
 */
@FeignClient(value = ApplicationConstant.APPLICATION_OMNIC_NAME, fallback = IAttendanceClientFallback.class)
public interface IAttendanceClient {

    String client = "/client";
    String GET_VEHICLE_DETAIL = client + "/getVehicleDetail";

    @PostMapping(GET_VEHICLE_DETAIL)
    R<AttendanceDetailDTO> getAttendanceDetail(@RequestBody  ScheduleAttendance attendance);
}
