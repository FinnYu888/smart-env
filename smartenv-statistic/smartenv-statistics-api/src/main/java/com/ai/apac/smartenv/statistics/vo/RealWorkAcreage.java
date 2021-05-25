package com.ai.apac.smartenv.statistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 实际工作面积
 * @Date 2021/1/12 7:26 下午
 **/
@Data
public class RealWorkAcreage implements Serializable {

    @ApiModelProperty(value = "工作路线等级")
    private Long workAreaLevel;

    @ApiModelProperty(value = "实际工作总面积")
    private Double totalRealWorkAcreage;
}
