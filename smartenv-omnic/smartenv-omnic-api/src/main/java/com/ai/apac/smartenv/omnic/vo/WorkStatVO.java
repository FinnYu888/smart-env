package com.ai.apac.smartenv.omnic.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/8/26 9:15 上午
 **/
@Data
@ApiModel(value = "大屏作业统计VO对象", description = "大屏作业统计VO对象")
public class WorkStatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("车辆出勤率")
    public Double vehicleWorkRate;

    @ApiModelProperty("人员出勤率")
    public Double personWorkRate;

    @ApiModelProperty("总体作业完成率")
    public Double overviewWorkRate;
}
