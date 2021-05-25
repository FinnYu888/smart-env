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

import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2021-01-08
 */
@Data
@TableName("ai_workarea_road_info")
@EqualsAndHashCode(callSuper = true)

@ApiModel(value = "WorkareaRoadInfo对象", description = "WorkareaRoadInfo对象")
public class WorkareaRoadInfo extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;
    /**
     * 关联工作区域ID
     */
    @ApiModelProperty(value = "关联工作区域ID")
    private Long workareaId;
    /**
     * 起止
     */
    @ApiModelProperty(value = "起止")
    private String startAndEnd;
    /**
     * 机动车道长度
     */
    @ApiModelProperty(value = "机动车道长度")
    private String motorwayLength;
    /**
     * 机动车道宽度
     */
    @ApiModelProperty(value = "机动车道宽度")
    private String motorwayWight;
    /**
     * 非机动车道长度
     */
    @ApiModelProperty(value = "非机动车道长度")
    private String nonMotorizedLength;
    /**
     * 非机动车道宽度
     */
    @ApiModelProperty(value = "非机动车道宽度")
    private String nonMotorizedWeight;
    /**
     * 人行道长度
     */
    @ApiModelProperty(value = "人行道长度")
    private String sidewalkLength;
    /**
     * 人行道宽度
     */
    @ApiModelProperty(value = "人行道宽度")
    private String sidewalkWight;
    /**
     * 绿化带长度
     */
    @ApiModelProperty(value = "绿化带长度")
    private String greenbeltLength;
    /**
     * 绿化带宽度
     */
    @ApiModelProperty(value = "绿化带宽度")
    private String greenbeltWeight;

    @ApiModelProperty(value = "绿化带面积")
    private String greenbeltArea;

    /**
     * 门前道路长度
     */
    @ApiModelProperty(value = "门前道路长度")
    private String frontRoadLength;
    /**
     * 门前道路宽度
     */
    @ApiModelProperty(value = "门前道路宽度")
    private String frontRoadWeigth;
    /**
     * 围栏长度
     */
    @ApiModelProperty(value = "围栏长度")
    private String fenceLength;
    /**
     * 总面积
     */
    @ApiModelProperty(value = "总面积")
    private String area;
    /**
     * 定额面积
     */
    @ApiModelProperty(value = "定额面积")
    private String workArea;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 道路级别：1:一级道路，2:二级道路，3:三级道路，4:四级道路
     */
    @ApiModelProperty(value = "道路级别：1:一级道路，2:二级道路，3:三级道路，4:四级道路")
    private Integer roadLevel;
}
