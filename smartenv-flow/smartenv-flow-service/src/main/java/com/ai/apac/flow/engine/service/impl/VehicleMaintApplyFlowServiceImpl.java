package com.ai.apac.flow.engine.service.impl;

import com.ai.apac.flow.engine.service.IVehicleMaintApplyFlowService;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("vehicleMaintApplyFlowService")
public class VehicleMaintApplyFlowServiceImpl implements IVehicleMaintApplyFlowService {
    @Autowired
    IVehicleClient vehicleClient;
    @Override
    public void updateVehicleStatus(Long vehicleId,Integer vehicleState) {
        vehicleClient.updateVehicleStateById(vehicleState,vehicleId);
    }
}
