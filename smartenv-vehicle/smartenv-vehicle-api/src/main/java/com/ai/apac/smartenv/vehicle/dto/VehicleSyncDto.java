package com.ai.apac.smartenv.vehicle.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleSyncDto
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/11/25
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/11/25  19:09    panfeng          v1.0.0             修改原因
 */
@Data
public class VehicleSyncDto {
    @ApiModelProperty("车辆编码")
    private String vehicleCode;
    @ApiModelProperty("车牌号")
    private String plateNumber;
    @ApiModelProperty("车辆类型编码")
    private String categoryId;
    @ApiModelProperty("车辆品牌")
    private String brand;
    @ApiModelProperty("部门编码")
    private String deptCode;
    @ApiModelProperty("设备编码列表")
    private List<String> deviceCodes;
    @ApiModelProperty("人员编码")
    private String personCode;
    @ApiModelProperty("项目编码")
    private String projectCode;

}
