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
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 公众上报事件基本信息表实体类
 *
 * @author qianlong
 * @since 2020-12-17
 */
@Data
@TableName("ai_public_event_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PublicEventInfo对象", description = "公众上报事件基本信息表")
public class PublicEventInfo extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 事件基本信息表主键id
     */
    @ApiModelProperty(value = "事件信息表主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "关联的事件信息表主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long eventId;

    /**
     * 事件类型
     */
    @ApiModelProperty(value = "事件类型")
    private String eventType;

    /**
     * 事件涉及的工作区域
     */
    @ApiModelProperty(value = "事件涉及的工作区域")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workareaId;
    /**
     * 事件涉及的具体地址描述
     */
    @ApiModelProperty(value = "事件涉及的具体地址描述")
    private String eventAddress;
    /**
     * 事件上报人ID
     */
    @ApiModelProperty(value = "事件上报人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private String reportPersonId;
    /**
     * 事件上报人名称
     */
    @ApiModelProperty(value = "事件上报人名称")
    private String reportPersonName;

    /**
     * 事件上报人联系方式
     */
    @ApiModelProperty(value = "事件上报人联系方式")
    private String reportPersonPhone;

    /**
     * 事件处理人ID
     */
    @ApiModelProperty(value = "事件处理人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private String handlePersonId;
    /**
     * 事件处理人名称
     */
    @ApiModelProperty(value = "事件处理人名称")
    private String handlePersonName;

    /**
     * 事件描述
     */
    @ApiModelProperty(value = "事件描述")
    private String eventDesc;

    /**
     * 处理意见
     */
    @ApiModelProperty(value = "处理意见")
    private String handleAdvice;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private String longitude;

    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private String latitudinal;

    /**
     * 所属区域
     */
    @ApiModelProperty(value = "所属区域")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long belongArea;

    /**
     * 所属区域名称
     */
    @ApiModelProperty(value = "所属区域名称")
    private String belongAreaName;

    @ApiModelProperty(value = "所属城市ID")
    private Long cityId;

    @ApiModelProperty(value = "所属城市名称")
    private String cityName;


    /**
     * 区域代码参考民政部官网：http://www.mca.gov.cn/article/sj/xzqh
     */
    @ApiModelProperty(value = "所属区域编码")
    private String adcode;

}
