package com.ai.apac.smartenv.vehicle.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data

@ApiModel(value = "VehicleMaintOrderApproveVO对象", description = "VehicleMaintOrderApproveVO对象")
public class VehicleMaintOrderApproveVO implements Serializable {
    @ApiModelProperty(value = "申请单号")
    private Long orderId;
    @ApiModelProperty(value = "审批结果,车队长通过2，经理通过4，拒绝都是6")
    private Integer doneResult;
    @ApiModelProperty(value = "审批意见")
    private String doneRemark;

}
