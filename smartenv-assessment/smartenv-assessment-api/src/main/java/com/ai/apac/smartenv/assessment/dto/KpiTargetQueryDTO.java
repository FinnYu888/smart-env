package com.ai.apac.smartenv.assessment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName KpiTargetQueryDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/17 11:04
 * @Version 1.0
 */
@Data
public class KpiTargetQueryDTO implements Serializable {
    private static final long serialVersionUID = 3835620289691070180L;

    @ApiModelProperty(value = "考核开始时间")
    Long startTime;

    @ApiModelProperty(value = "考核结束时间")
    Long endTime;

    @ApiModelProperty(value = "考核状态")
    Integer status;

    @ApiModelProperty(value = "考核岗位")
    String stationId;

    @ApiModelProperty(value = "考核名称")
    String name;



}
