package com.ai.apac.smartenv.vehicle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 车辆设备状态数据统计
 * @Date 2020/11/9 11:13 上午
 **/
@Data
@ApiModel("车辆设备状态数据统计")
public class VehicleDeviceStatusCountDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("车辆总数")
    Integer vehicleCount;

    @ApiModelProperty("在线数量")
    Integer onVehicleCount;

    @ApiModelProperty("离线数量")
    Integer offVehicleCount;

    @ApiModelProperty("未绑定设备数量")
    Integer nodVehicleCount;

    @ApiModelProperty("项目编码")
    private String projectCode;

    public VehicleDeviceStatusCountDTO() {
        this.vehicleCount = 0;
        this.onVehicleCount = 0;
        this.offVehicleCount = 0;
        this.nodVehicleCount = 0;
    }

    public VehicleDeviceStatusCountDTO(Integer vehicleCount, Integer onVehicleCount, Integer offVehicleCount, Integer nodVehicleCount,String projectCode) {
        this.vehicleCount = vehicleCount;
        this.onVehicleCount = onVehicleCount;
        this.offVehicleCount = offVehicleCount;
        this.nodVehicleCount = nodVehicleCount;
        this.projectCode = projectCode;
    }
}
