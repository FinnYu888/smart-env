package com.ai.apac.smartenv.websocket.module.vehicle.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //车辆位置信息
 * @Date 2020/2/15 4:50 下午
 **/
@Data
public class VehicleInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("车辆绑定终端的ID")
    private String deviceId;

    @ApiModelProperty("车辆绑定终端的code")
    private String deviceCode;

    @ApiModelProperty("车辆ID")
    private String vehicleId;

    @ApiModelProperty("车牌号")
    private String vehicleNumber;

    @ApiModelProperty("当前车辆状态")
    private Integer status;

    @ApiModelProperty("当前车辆状态名称")
    private String statusName;

    @ApiModelProperty("显示图标")
    private String icon;

    @ApiModelProperty("是否显示")
    private Boolean showFlag = false;
}
