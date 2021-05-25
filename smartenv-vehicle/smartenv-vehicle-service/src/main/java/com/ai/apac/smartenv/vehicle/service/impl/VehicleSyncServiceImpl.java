package com.ai.apac.smartenv.vehicle.service.impl;

import com.ai.apac.smartenv.vehicle.dto.VehicleSyncDto;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.service.IVehicleSyncService;
import org.springframework.stereotype.Service;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleSyncServiceImpl
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/11/25
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/11/25  19:14    panfeng          v1.0.0             修改原因
 */
@Service
public class VehicleSyncServiceImpl implements IVehicleSyncService {


    /**
     * 新增或者修改车辆信息
     *
     * @param vehicleSyncDto
     * @return
     */
    @Override
    public Boolean addOrUpdateVehicleInfo(VehicleSyncDto vehicleSyncDto) {


        return false;
    }

    @Override
    public Boolean deleteVehicleInfoByCode(String vehicleCode) {

        return false;

    }


}
