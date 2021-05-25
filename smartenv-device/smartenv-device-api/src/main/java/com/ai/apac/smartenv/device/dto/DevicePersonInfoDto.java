package com.ai.apac.smartenv.device.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class DevicePersonInfoDto implements Serializable {


    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 设备编码
     */
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;
    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;
    /**
     * 设备型号
     */
    @ApiModelProperty(value = "设备型号")
    private String deviceType;
    /**
     * 设备厂家
     */
    @ApiModelProperty(value = "设备厂家")
    private String deviceFactory;
    /**
     * 实体分类
     */
    @ApiModelProperty(value = "实体分类")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long entityCategoryId;

    /**
     * 实体分类
     */
    @ApiModelProperty(value = "设备实时状态")
    private Long deviceStatus;

    @ApiModelProperty(value = "设备实时坐标")
    private String deviceLocation;

    @ApiModelProperty(value = "设备实时位置")
    private String deviceLocationName;
    /**
     * 实体ID
     */
    @ApiModelProperty(value = "实体ID")
    private Long entityId;
}
