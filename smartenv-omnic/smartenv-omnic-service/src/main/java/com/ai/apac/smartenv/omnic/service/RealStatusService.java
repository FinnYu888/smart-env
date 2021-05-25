package com.ai.apac.smartenv.omnic.service;

import com.ai.apac.smartenv.omnic.entity.OmnicPersonInfo;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.omnic.entity.OmnicVehicleInfo;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: RealStatusService
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/12  23:51    panfeng          v1.0.0             修改原因
 */
public interface RealStatusService {

    StatusCount selectAllVehicleDeviceStatusCount(String tenantId);

    StatusCount selectAllPersonDeviceStatusCount(String tenantId);

    List<OmnicVehicleInfo> getVehicleInfoByStatus(Integer status, String tenantId);

    List<OmnicPersonInfo> getPersonInfoByStatus(Integer status, String tenantId);
}
