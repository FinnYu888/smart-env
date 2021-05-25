package com.ai.apac.smartenv.omnic.mapper;

import com.ai.apac.smartenv.omnic.entity.OmnicPersonInfo;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.omnic.entity.OmnicVehicleInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: RealStatusMapper
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/12  17:10    panfeng          v1.0.0             修改原因
 */

public interface RealStatusMapper {

    StatusCount selectAllVehicleDeviceStatusCount(@Param("tenantId") String tenantId);

    List<OmnicVehicleInfo> selectVehicleByStatus(@Param("status") Integer status, @Param("tenantId") String tenantId);

    List<OmnicVehicleInfo> selectAlarmVehicle(@Param("tenantId") String tenantId);







    StatusCount selectAllPersonDeviceStatusCount(@Param("tenantId") String tenantId);

    List<OmnicPersonInfo> selectPersonByStatus(@Param("status") Integer status, @Param("tenantId") String tenantId);

    List<OmnicPersonInfo> selectAlarmPerson(@Param("tenantId") String tenantId);




}
