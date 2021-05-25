package com.ai.apac.smartenv.inventory.vo;

import com.ai.apac.smartenv.system.entity.Dict;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "物资申请详情", description = "物资申请详情")
public class ResApplyDetailVO {

    @ApiModelProperty(value = "申请订单ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
    @ApiModelProperty(value = "申请人")
    private String applyName;
    @ApiModelProperty(value = "申请人所在部门")
    private String applyDepartment;
    @ApiModelProperty(value = "申请时间")
    private Date applyTime;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "申请流程实例Id")
    private String processInstanceId;
    @ApiModelProperty(value = "申请物品明细")
    private List<ResOrderDtlVO> resOrderDtlVOList;
    @ApiModelProperty(value = "订单里程碑")
    List<ResOrderMilestoneVO> orderMilestoneVOList;
    @ApiModelProperty(value = "订单申请流程")
    List<Dict> resApplyFlows;

}
