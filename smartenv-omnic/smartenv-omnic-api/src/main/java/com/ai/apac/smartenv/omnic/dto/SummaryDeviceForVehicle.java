package com.ai.apac.smartenv.omnic.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 车辆设备数据汇总
 * @Date 2020/10/29 5:59 下午
 **/
@Data
@ApiModel
public class SummaryDeviceForVehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("在线中数量")
    private Integer onlineCount;

    @ApiModelProperty("离线中数量")
    private Integer offlineCount;

    @ApiModelProperty("未绑定车辆数量")
    private Integer unBindCount;
}
