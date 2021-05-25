package com.ai.apac.smartenv.omnic.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //设备工作状态DTO
 * @Date 2020/11/4 4:50 下午
 **/
@ApiModel("设备工作状态DTO")
@Data
public class DeviceWorkAreaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("设备编号")
    private String deviceCode;

    @ApiModelProperty("工作区域状态")
    private Integer workAreaType;
}
