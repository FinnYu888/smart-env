package com.ai.apac.smartenv.websocket.module.polymerization.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: AshcanPolymerizationDetailDTO
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/9/15
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/9/15     zhaidx           v1.0.0               修改原因
 */
@Data
public class AshcanPolyDetailDTO extends BasicPolymerizationDetailDTO {

    private static final long serialVersionUID = 6584698547689088535L;
    @ApiModelProperty(value = "垃圾桶编码")
    private String ashcanCode;
    @ApiModelProperty(value = "垃圾桶类型")
    private String ashcanType;
    @ApiModelProperty(value = "垃圾桶容量，单位L")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long capacity;
    @ApiModelProperty(value = "垃圾桶是否支持安装终端")
    private String supportDevice;
    @ApiModelProperty(value = "所属部门")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;
    @ApiModelProperty(value = "所属路线/区域")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workareaId;
    @ApiModelProperty(value = "所属片区")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long regionId;
    @ApiModelProperty(value = "经度")
    private String lng;
    @ApiModelProperty(value = "纬度")
    private String lat;
    @ApiModelProperty(value = "垃圾桶地址")
    private String location;
    @ApiModelProperty(value = "垃圾桶详细地址")
    private String detailLocation;
    @ApiModelProperty(value = "垃圾桶厂家")
    private String companyCode;
    @ApiModelProperty(value = "垃圾桶状态：正常，损坏")
    private String ashcanStatus;
    @ApiModelProperty(value = "工作状态：正常，溢满，无信号")
    private String workStatus;
    @ApiModelProperty(value = "垃圾桶二维码")
    private String ashcanQrCode;
    @ApiModelProperty(value = "传感器上传经度")
    private String deviceLng;
    @ApiModelProperty(value = "传感器上传纬度")
    private String deviceLat;
    @ApiModelProperty(value = "设备")
    private String deviceId;
    @ApiModelProperty(value = "部门名称")
    private String deptName;
    @ApiModelProperty(value = "垃圾桶类别名称")
    private String ashcanTypeName;
    @ApiModelProperty(value = "垃圾桶状态名称")
    private String ashcanStatusName;
    @ApiModelProperty(value = "垃圾桶工作状态名称")
    private String workStatusName;
    @ApiModelProperty(value = "支持设备名称")
    private String supportDeviceName;
    @ApiModelProperty(value = "工作区域/路线名称")
    private String workareaName;
    @ApiModelProperty(value = "片区名称")
    private String regionName;
    @ApiModelProperty(value = "设备名称")
    private String deviceName;
    @ApiModelProperty(value = "展示经度")
    private String showLng;
    @ApiModelProperty(value = "展示纬度")
    private String showLat;
    @ApiModelProperty(value = "垃圾桶图片")
    private String ashcanPicture;
}
