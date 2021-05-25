package com.ai.apac.smartenv.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

@Data
@ApiModel(value = "对象", description = "记录设备信息")
public class DevicePersonInfo extends TenantEntity {
    /**
     * 设备ID
     */
    @ApiModelProperty(value = "设备ID")
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
