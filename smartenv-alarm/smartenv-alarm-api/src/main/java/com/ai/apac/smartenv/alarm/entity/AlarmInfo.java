/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.alarm.entity;

import com.ai.apac.smartenv.common.annotation.Latitude;
import com.ai.apac.smartenv.common.annotation.Longitude;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.sql.Timestamp;

/**
 * 告警基本信息表实体类
 *
 * @author Blade
 * @since 2020-03-05
 */
@Data
@TableName("ai_alarm_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AlarmInfo对象", description = "AlarmInfo对象")
public class AlarmInfo extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 事件基本信息表主键id
     */
    @ApiModelProperty(value = "事件基本信息表主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 告警规则名称
     */
    @ApiModelProperty(value = "点创告警消息关联Id")
    private String uuid;
    /**
     * 设备编码
     */
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;
    /**
     * 设备关联的实体Id，可以是人员Id或者车辆Id
     */
    @ApiModelProperty(value = "设备关联的实体Id，可以是人员Id或者车辆Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long entityId;
    /**
     * 设备关联的实体类型，人员=5，车辆=2
     */
    @ApiModelProperty(value = "设备关联的实体类型，人员=5，车辆=2")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long entityType;
    /**
     * 设备关联的实体名称，车牌或者人员名称
     */
    @ApiModelProperty(value = "设备关联的实体名称，车牌或者人员名称")
    private String entityName;
    /**
     * 设备关联的实体定义，车辆类型：洒水车，清扫车等；人员：工号
     */
    @ApiModelProperty(value = "设备关联的实体定义，车辆类型：洒水车，清扫车等；人员：工号")
    private String entityDefine;
    /**
     * 告警规则信息
     */
    @ApiModelProperty(value = "告警规则信息")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ruleId;
    /**
     * 告警规则名称
     */
    @ApiModelProperty(value = "告警规则名称")
    private String ruleName;
    /**
     * 告警级别
     */
    @ApiModelProperty(value = "告警级别")
    private Integer ruleAlarmLevel;
    /**
     * 规则关联的实体类型Id
     */
    @ApiModelProperty(value = "规则关联的实体类型Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ruleCategoryId;
    /**
     * 规则关联的实体类型编码
     */
    @ApiModelProperty(value = "规则关联的实体类型编码")
    private String ruleCategoryCode;
    /**
     * 规则关联的父实体类型Id
     */
    @ApiModelProperty(value = "规则关联的父实体类型Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentRuleCategoryId;
    /**
     * 告警时间
     */
    @ApiModelProperty(value = "告警时间")
    private Timestamp alarmTime;
    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    @Longitude
    private String longitude;
    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    @Latitude
    private String latitudinal;

    @ApiModelProperty(value = "片区")
    private String regionId;

    @ApiModelProperty(value = "片区的上级行政区域")
    private String parentRegionId;
    /**
     * 告警数据
     */
    @ApiModelProperty(value = "告警数据")
    private String data;
    /**
     * 是否已经处理0:未处理，1：已处理
     */
    @ApiModelProperty(value = "是否已经处理0:未处理，1：已处理")
    private Integer isHandle;
    /**
     * 告警信息备注
     */
    @ApiModelProperty(value = "告警信息备注")
    private String alarmMessage;
    /**
     * 告警校对，正常告警：1， 错误告警：2，其他：3
     */
    @ApiModelProperty(value = "告警校对，正常告警：1， 错误告警：2，其他：3")
    private Integer alarmCheck;
    /**
     * 告警校对备注
     */
    @ApiModelProperty(value = "告警校对备注")
    private String checkRemark;
    /**
     * 告警信息发送方式，邮件：1，微信：2，后台通知：3，手表：4
     */
    @ApiModelProperty(value = "告警信息发送方式，邮件：1，微信：2，后台通知：3，手表：4")
    private String informType;


}
