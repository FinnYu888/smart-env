package com.ai.apac.smartenv.alarm.vo;

import com.ai.apac.smartenv.alarm.entity.AlarmInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: AlarmInfoHandleInfoVO
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/2/11
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/2/11     zhaidx           v1.0.0               修改原因
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AlarmInfoHandleInfoVO", description = "告警信息查询结果对象")
public class AlarmInfoHandleInfoVO extends AlarmInfo {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "事件基本信息表主键id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    Long alarmId;
    
    @ApiModelProperty(value = "告警级别")
    private Integer alarmLevel;
    
    @ApiModelProperty(value = "告警级别的展示名称")
    private String alarmLevelName;

    @ApiModelProperty(value = "处理状态名称")
    private String isHandleName;

    @ApiModelProperty(value = "告警名称")
    private String alarmName;

    @ApiModelProperty(value = "告警类型")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long alarmType;

    @ApiModelProperty(value = "告警类型名称")
    private String alarmTypeName;

    @ApiModelProperty(value = "告警大类名称")
    private String alarmCatalogName;

    @ApiModelProperty(value = "车辆Id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long vehicleId;
    
    @ApiModelProperty(value = "车牌号")
    private String plateNumber;

    @ApiModelProperty(value = "车辆大类")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long kindCode;

    @ApiModelProperty(value = "车辆大类名称")
    private String vehicleTypeName;

    @ApiModelProperty(value = "车辆小类Id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long vehicleCategoryId;

    @ApiModelProperty(value = "车辆小类名称")
    private String vehicleCategoryName;

    @ApiModelProperty(value = "人员Id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long personId;  
    
    @ApiModelProperty(value = "人员姓名")
    private String personName;

    @ApiModelProperty(value = "工号")
    private String jobNumber;
    
    @ApiModelProperty(value = "人员岗位")
    private String personPositionName;

    @ApiModelProperty(value = "人员岗位Id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long personPositionId;

    @ApiModelProperty(value = "告警信息")
    private String alarmMessage;

    @ApiModelProperty(value = "告警地址")
    private String alarmLocation;

    @ApiModelProperty(value = "所属部门Id")
    private Long deptId;

    @ApiModelProperty(value = "所属部门")
    private String department;
    
    @ApiModelProperty(value = "主动告警图片链接")
    private List<String> initiativeAlarmPics;

}
