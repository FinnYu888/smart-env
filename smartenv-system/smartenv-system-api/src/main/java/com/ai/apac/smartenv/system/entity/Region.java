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
package com.ai.apac.smartenv.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@TableName("ai_region")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Region对象", description = "Region对象")
public class Region extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 区域ID
     */
    @ApiModelProperty(value = "区域ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;
    /**
     * 区域名称
     */
    @ApiModelProperty(value = "区域名称")
    private String regionName;
    /**
     * 父区域ID
     */
    @ApiModelProperty(value = "父区域ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long parentRegionId;
    /**
     * 行政级别：
     * 1：国家
     * 2：省/直辖市
     * 3：市
     * 4：区/县
     * 5：镇/乡
     */
    @ApiModelProperty(value = "行政级别： 1：国家 2：省/直辖市 3：市 4：区/县 5：镇/乡")
    private Long regionLevel;

    /**
     * 区域类型：
     * 1：行政区域
     * 2：业务区域
     */
    @ApiModelProperty(value = "区域类型：1：行政区域 2：业务区域")
    private Long regionType;

    /**
     * 区域主管
     */
    @ApiModelProperty(value = "区域主管")
    private String regionManager;

    /**
     * 区域主管
     */
    @ApiModelProperty(value = "区域主管名称")
    private String regionManagerName;

    /**
     * 区域面积
     */
    @ApiModelProperty(value = "区域面积")
    private String regionArea;
    /**
     * 备用字段
     */
    @ApiModelProperty(value = "备用字段")
    private String ext1;
}
