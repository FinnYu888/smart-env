package com.ai.apac.smartenv.alarm.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class AlarmInfoQueryDTO implements Serializable {
    private static final long serialVersionUID = 3835620289691070180L;

    @ApiModelProperty(value = "告警实体类型Id")
    @JsonSerialize(using = ToStringSerializer.class)
    Long entityCategoryId;

    @ApiModelProperty(value = "告警实体类型Id")
    Integer isHandle;

    @ApiModelProperty(value = "开始时间")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    Long startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    Long endTime;

    @ApiModelProperty(value = "人员Id")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    Long personId;
    
    @ApiModelProperty(value = "人员名称")
    String personName;

    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @ApiModelProperty(value = "车辆Id")
    Long vehicleId;

    @ApiModelProperty(value = "车牌号")
    String vehiclePlateNumber;
    
    @ApiModelProperty(value = "车辆大类Id")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    Long vehicleKindCode;

    @ApiModelProperty(value = "车辆小类Id")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    Long vehicleCategoryId;
    
    @ApiModelProperty(value = "告警级别")
    Integer alarmLevel;

    @ApiModelProperty(value = "告警条数")
    Integer alarmNum;

    @ApiModelProperty(value = "租户ID")
    String tenantId;

    @ApiModelProperty(value = "实体类型（车辆2人员5）")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    Long entityType;
}
