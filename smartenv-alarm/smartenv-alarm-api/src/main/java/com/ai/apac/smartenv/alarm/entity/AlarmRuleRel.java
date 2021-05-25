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

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 告警规则关联表实体类
 *
 * @author Blade
 * @since 2020-02-07
 */
@Data
@TableName("ai_alarm_rule_rel")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AlarmRuleRel对象", description = "告警规则关联表")
public class AlarmRuleRel extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 告警规则关联表主键id
     */
    @ApiModelProperty(value = "告警规则关联表主键id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;
    /**
     * 报警规则id
     */
    @ApiModelProperty(value = "报警规则id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long alarmRuleId;
    /**
     * 实体id
     */
    @ApiModelProperty(value = "实体id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long entityId;
    /**
     * 实体名称
     */
    @ApiModelProperty(value = "实体名称")
    private String entityName;
    /**
     * 实体类型
     */
    @ApiModelProperty(value = "实体类型")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long entityType;
    /**
     * 实体类型名称
     */
    @ApiModelProperty(value = "实体类型名称")
    private String entityTypeName;
    /**
     * 关联实体分类
     */
    @ApiModelProperty(value = "关联实体分类")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long entityCategoryId;
    /**
     * 关联实体分类名称
     */
    @ApiModelProperty(value = "关联实体分类名称")
    private String entityCategoryName;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

}
