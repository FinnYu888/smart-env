package com.ai.apac.smartenv.vehicle.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleMaintOrderMilestone对象", description = "VehicleMaintOrderMilestone对象")
public class VehicleMaintOrderMilestone extends VehicleMaintOrder {
    private List<VehicleMaintOrderMileStoneQry> vehicleMaintMilestones;
}
