package com.ai.apac.smartenv.omnic.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 每个租户的各种数据汇总(按天统计)
 * @Date 2020/10/29 5:39 下午
 **/
@Data
@ApiModel
public class SummaryAmount implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("租户ID")
    private String tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("车辆数量")
    private Integer vehicleCount;

    @ApiModelProperty("人员数量")
    private Integer personCount;

    @ApiModelProperty("中转站数量")
    private Integer facilityCount;

    @ApiModelProperty("垃圾桶数量")
    private Integer trashCount;

    @ApiModelProperty("公厕数量")
    private Integer toiletCount;

    @ApiModelProperty("事件数量")
    private Integer assessmentEventCount;

    @ApiModelProperty("所有未处理告警数量")
    private Integer totalUnHandleAlarmCount;
}
