package com.ai.apac.flow.engine.service;

import io.swagger.models.auth.In;

/**
* 车辆维修申请任务监听
* @author 66578
*/
public interface IVehicleMaintApplyFlowService {
    /**
    * 审批完成更新车辆状态为正常状态
    * @author 66578
    */
    void updateVehicleStatus(Long vehicleId, Integer vehicleState);
}
