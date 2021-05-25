package com.ai.apac.smartenv.omnic.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 每个租户的和人员相关的各种数据汇总(按天统计)
 * @Date 2020/10/29 5:39 下午
 **/
@Data
@ApiModel
public class SummaryDataForPerson implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("租户ID")
    private String tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("工作中数量")
    private Integer workingCount;

    @ApiModelProperty("静值中数量")
    private Integer onStandByCount;

    @ApiModelProperty("休息中数量")
    private Integer restingCount;

    @ApiModelProperty("休假中数量")
    private Integer vacationCount;

    @ApiModelProperty("未排班数量")
    private Integer unArrangeCount;

    @ApiModelProperty("所有未处理告警数量")
    private Integer totalUnHandleAlarmCount;
}
