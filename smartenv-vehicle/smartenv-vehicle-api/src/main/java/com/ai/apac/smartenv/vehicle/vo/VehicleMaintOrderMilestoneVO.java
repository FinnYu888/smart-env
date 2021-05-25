package com.ai.apac.smartenv.vehicle.vo;

import com.ai.apac.smartenv.vehicle.entity.VehicleMaintOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleMaintOrderMilestoneVO对象", description = "VehicleMaintOrderMilestoneVO对象")
public class VehicleMaintOrderMilestoneVO extends VehicleMaintOrder {
    @ApiModelProperty(value = "申请类型")
    private String applyTypeName;

    @ApiModelProperty(value = "维修类型名称")
    private String maintTypeName;
    @ApiModelProperty(value = "车辆大类名称")
    private String vehicleKindName;
    @ApiModelProperty(value = "车辆类型名称")
    private String vehicleTypeName;
    @ApiModelProperty(value = "申请人部门名称")
    private String applyPersonDeptName;

    private List<VehicleMaintOrderMilestoneVO> vehicleMaintMilestones;
}
