package com.ai.apac.smartenv.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName GreenDeviceVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/27 10:19
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleDeviceVO对象", description = "车辆设备视图&编辑对象")
public class GreenDeviceVO extends DeviceInfoVO {
    @ApiModelProperty(value = "厂家名称")
    private String deviceFactoryName;

    @ApiModelProperty(value = "设备实时坐标")
    private String deviceLocation;
}
