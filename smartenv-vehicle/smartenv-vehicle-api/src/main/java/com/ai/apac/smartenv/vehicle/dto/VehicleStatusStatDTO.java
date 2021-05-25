package com.ai.apac.smartenv.vehicle.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author qianlong
 * @description 车辆状态统计DTO
 * @Date 2020/3/20 9:04 上午
 **/
@Data
public class VehicleStatusStatDTO implements Serializable {

    private static final long serialVersionUID = -4690152936663578596L;

    /**
     * 工作中车辆
     */
    @ApiModelProperty(value = "工作中车辆")
    private List<BasicVehicleInfoDTO> workingList;

    /**
     * 脱岗车辆
     */
    @ApiModelProperty(value = "脱岗车辆")
    private List<BasicVehicleInfoDTO> departureList;

    /**
     * 休息中车辆
     */
    @ApiModelProperty(value = "休息中车辆")
    private List<BasicVehicleInfoDTO> sitBackList;
}
