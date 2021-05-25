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
package com.ai.apac.smartenv.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springblade.core.mp.base.BaseEntity;

import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 培训记录表实体类
 *
 * @author Blade
 * @since 2020-08-20
 */
@Data
@TableName("ai_training_record")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TrainingRecord对象", description = "培训记录表")
public class TrainingRecord extends TenantEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "培训记录Id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;
    /**
     * 培训主题
     */
    @ApiModelProperty(value = "培训主题")
    private String trainingTopic;
    /**
     * 培训内容（富文本）
     */
    @ApiModelProperty(value = "培训内容（富文本）")
    private String trainingContent;
    /**
     * 组织者
     */
    @ApiModelProperty(value = "组织者")
    private String organizer;
    /**
     * 主讲人
     */
    @ApiModelProperty(value = "主讲人")
    private String speaker;
    /**
     * 培训开始时间
     */
    @ApiModelProperty(value = "培训开始时间")
    private Timestamp trainingStartTime;
    /**
     * 培训结束时间
     */
    @ApiModelProperty(value = "培训结束时间")
    private Timestamp trainingEndTime;
    /**
     * 培训地点
     */
    @ApiModelProperty(value = "培训地点")
    private String trainingLocation;
    /**
     * 培训类型Id
     */
    @ApiModelProperty(value = "培训类型Id")
    private Long trainingTypeId;
    /**
     * 培训类型名称
     */
    @ApiModelProperty(value = "培训类型名称")
    private String trainingTypeName;


}
