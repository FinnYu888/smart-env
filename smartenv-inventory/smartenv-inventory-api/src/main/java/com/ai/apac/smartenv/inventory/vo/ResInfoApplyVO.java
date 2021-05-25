package com.ai.apac.smartenv.inventory.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.tenant.mp.TenantEntity;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "物资申请", description = "物资申请")
public class ResInfoApplyVO extends TenantEntity {
    @ApiModelProperty(value = "申请人")
    private String custName;
    @ApiModelProperty(value = "申请人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long custId;
    private String applyDescription;
    private String tenantId;
    private List<ResOrderDtlVO> orderDtlList;
}
