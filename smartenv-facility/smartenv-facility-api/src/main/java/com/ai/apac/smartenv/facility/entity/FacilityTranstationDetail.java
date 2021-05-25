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
package com.ai.apac.smartenv.facility.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 实体类
 *
 * @author Blade
 * @since 2020-02-14
 */
@Data
@TableName("ai_facility_transtation_detail")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FacilityTranstationDetail对象", description = "FacilityTranstationDetail对象")
public class FacilityTranstationDetail extends TenantEntity {

    private static final long serialVersionUID = 1L;

    @TableId("ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @TableField("FACILITY_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long facilityId;
    @TableField("TRANSFER_TIME")
    @DateTimeFormat(
            pattern = "yyyy/MM/dd HH:mm:ss"
    )
    @JsonFormat(
            pattern = "yyyy/MM/dd HH:mm:ss"
    )
    private Timestamp transferTime;
    @TableField("GARBAGE_TYPE")
    private String garbageType;
    @TableField("GARBAGE_WEIGHT")
    private String garbageWeight;
    @TableField("TRANSFER_TIMES")
    private Integer transferTimes;
    @TableField("DEVICE_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deviceId;
    @TableField("TENANT_ID")
    private String tenantId;
    @TableField("ODOR_LEVEL")
    private String odorLevel;
    @TableField("STATUS")
    private Integer status;
    @TableField("DONE_DATE")
    private Timestamp doneDate;
    @TableField("OP_ID")
    private String opId;
    @TableField("ORG_ID")
    private String orgId;
    @TableField("CREATE_USER")
    private Long createUser;
    @TableField("UPDATE_USER")
    private Long updateUser;



}
