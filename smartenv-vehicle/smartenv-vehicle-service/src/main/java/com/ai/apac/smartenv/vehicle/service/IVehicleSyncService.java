package com.ai.apac.smartenv.vehicle.service;

import com.ai.apac.smartenv.vehicle.dto.VehicleSyncDto;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleSyncService
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/11/25
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/11/25  19:13    panfeng          v1.0.0             修改原因
 */
public interface IVehicleSyncService {

    Boolean addOrUpdateVehicleInfo(VehicleSyncDto vehicleSyncDto);

    Boolean deleteVehicleInfoByCode(String vehicleCode);
}
