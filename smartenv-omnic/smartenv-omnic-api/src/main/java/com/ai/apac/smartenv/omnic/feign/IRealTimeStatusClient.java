package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.omnic.entity.OmnicPersonInfo;
import com.ai.apac.smartenv.omnic.entity.PicStatus;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.omnic.entity.OmnicVehicleInfo;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IRealTimeStatusClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/17
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/17  15:30    panfeng          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_OMNIC_NAME,
        fallback = IRealTimeStatusClientFallback.class

)
public interface IRealTimeStatusClient {
    String client = "/client";
    String GET_ALL_STATUS_COUNT = client + "/get-all-status-count";
    String GET_PIC_STATUS = client + "/get-pic-status";
    String GET_VEHICLE_STATUS = client + "/get-vehicle-status";



    String GET_ALL_PERSON_STATUS_COUNT = client + "/get-all-person-status-count";
    String GET_PERSON_PIC_STATUS = client + "/get-person-pic-status";
    String GET_PERSON_STATUS = client + "/get-person-status";


    @GetMapping(GET_VEHICLE_STATUS)
    R<List<OmnicVehicleInfo>> getVehicleByStatus(@RequestParam Integer status,@RequestParam String tenantId);

    @GetMapping(GET_PIC_STATUS)
    R<PicStatus> getPicStatusByVehicleId(@RequestParam String vehicleId);

    @GetMapping(GET_ALL_STATUS_COUNT)
    R<StatusCount> getAllVehicleStatusCount(@RequestParam String tenantId);
//---------------------------------------------------人员实时状态-----------------------------------------------------


    @GetMapping(GET_PERSON_STATUS)
    R<List<OmnicPersonInfo>> getPersonByStatus(@RequestParam Integer status, @RequestParam String tenantId);

    @GetMapping(GET_PERSON_PIC_STATUS)
    R<PicStatus> getPicStatusByPersonId(@RequestParam String personId);

    @GetMapping(GET_ALL_PERSON_STATUS_COUNT)
    R<StatusCount> getAllPersonStatusCount(@RequestParam String tenantId);
}
