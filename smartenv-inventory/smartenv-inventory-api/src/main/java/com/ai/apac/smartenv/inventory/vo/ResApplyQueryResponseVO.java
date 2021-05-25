package com.ai.apac.smartenv.inventory.vo;

import com.ai.apac.smartenv.inventory.entity.ResOrderDtl;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "物资申请单查询响应对象", description = "物资申请单查询响应对象")
public class ResApplyQueryResponseVO implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "订单id")
    private Long id;
    private Date createTime;
    private Integer amount;
    @ApiModelProperty(value = "申请人")
    private String custName;
    @ApiModelProperty(value = "申请人id")
    private String custId;
    @ApiModelProperty(value = "订单状态")
    private String orderStatus;
    @ApiModelProperty(value = "翻译订单状态")
    private String orderStatusName;
    private String businessType;
    @ApiModelProperty(value = "审批人")
    private String approverName;
    @ApiModelProperty(value = "审批结果")
    private String approveResult;
    private List<ResOrderDtlVO> orderDtlList;


}
