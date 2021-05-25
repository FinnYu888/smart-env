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
package com.ai.apac.smartenv.event.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

import java.sql.Timestamp;

/**
 * 事件基本信息表实体类
 *
 * @author Blade
 * @since 2020-02-06
 */
@Data
@TableName("ai_event_medium")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EventMedium对象", description = "事件媒介信息表")
public class EventMedium extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 事件媒介信息表主键id
     */
    @ApiModelProperty(value = "事件媒介信息表主键id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;
    /**
     * 媒介类型
     */
    @ApiModelProperty(value = "媒介类型，1-图片，2-语音")
    private Integer mediumType;

    /**
     * 媒介分类
     */
    @ApiModelProperty(value = "媒介分类")
    private Integer mediumDetailType;
    /**
     * 存储路径
     */
    @ApiModelProperty(value = "存储路径")
    private String mediumUrl;

    /**
     * 事件id
     */
    @ApiModelProperty(value = "事件id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long eventInfoId;

    /**
     * 指派历史表id
     */
    @ApiModelProperty(value = "指派历史表id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long assignedId;

}
