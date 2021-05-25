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
package com.ai.apac.smartenv.address.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

import java.sql.Timestamp;

/**
 * 历史轨迹导出任务表实体类
 *
 * @author Blade
 * @since 2020-03-03
 */
@Data
@TableName("ai_track_export_task")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TrackExportTask对象", description = "历史轨迹导出任务表")
public class TrackExportTask extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    @JsonSerialize(
            using = ToStringSerializer.class
    )
    private Long id;
    /**
     * 导出时间
     */
    @ApiModelProperty(value = "导出时间")
    private Timestamp exportTime;
    /**
     * 导出对象id
     */
    @ApiModelProperty(value = "导出对象id")
    private Long entityId;
    /**
     * 导出对象名称
     */
    @ApiModelProperty(value = "导出对象名称")
    private String entityName;
    /**
     * 导出对象类型1：车辆  2：人员
     */
    @ApiModelProperty(value = "导出对象类型1：车辆  2：人员 ")
    private Integer entityType;


    /**
     * 导出条件
     */
    @ApiModelProperty(value = "条件")
    private String exportCondition;
    /**
     * 导出状态 1：进行中  2 已完成
     */
    @ApiModelProperty(value = "导出状态 1：进行中  2 已完成")
    private Integer exportStatus;
    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径")
    private String filePath;


}
