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
package com.ai.apac.smartenv.workarea.entity;

import com.ai.apac.smartenv.common.annotation.Latitude;
import com.ai.apac.smartenv.common.annotation.Longitude;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 工作区域节点信息实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@TableName("ai_workarea_node")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "WorkareaNode对象", description = "工作区域节点信息")
public class WorkareaNode extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    @ApiModelProperty(value = "节点ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;
    @ApiModelProperty(value = "规划区域或路线ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long workareaId;
    /**
     * 节点排序
     */
    @ApiModelProperty(value = "节点排序")
    private Long nodeSeq;
    /**
     * 由于精度问题，直接存储字符串
     */
    @Longitude
    @ApiModelProperty(value = "由于精度问题，直接存储字符串")
    private String longitude;
    /**
     * 由于精度问题，直接存储字符串
     */
    @Latitude
    @ApiModelProperty(value = "由于精度问题，直接存储字符串")
    private String latitudinal;

    @ApiModelProperty(value = "片区区域ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long regionId;

}
