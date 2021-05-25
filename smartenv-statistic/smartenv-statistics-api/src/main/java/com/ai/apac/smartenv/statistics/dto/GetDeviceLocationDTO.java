package com.ai.apac.smartenv.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 查询设备位置请求对象
 * @Date 2021/1/6 12:50 下午
 **/
@Data
public class GetDeviceLocationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("项目编码列表")
    String projectCodes;

    @ApiModelProperty("设备状态列表")
    String deviceStatuss;

    @ApiModelProperty("工作状态列表")
    String workStatuss;

    @ApiModelProperty("车辆类型")
    String vehicleType;

    @ApiModelProperty("车辆类型名称")
    String vehicleTypeName;

    @ApiModelProperty("人员岗位")
    String personStation;

    @ApiModelProperty("人员岗位名称")
    String personStationName;

    @ApiModelProperty("查询实体类型 0-所有 1-车辆 2-人员")
    Integer searchObjType;
}
