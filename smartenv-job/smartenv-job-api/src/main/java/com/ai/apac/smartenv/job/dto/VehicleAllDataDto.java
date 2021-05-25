package com.ai.apac.smartenv.job.dto;

import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleAllDataDto
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/5
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/5  11:11    panfeng          v1.0.0             修改原因
 */
public class VehicleAllDataDto {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("主键id")
    private Long id;
    /**
     * 车牌号
     */
    @ApiModelProperty(value = "车牌号")
    @Length(max = 20, message = "车牌号长度不能超过20")
    @NotBlank(message = "需要输入车牌号")
    private String plateNumber;
    /**
     * 车辆大类
     */
    @ApiModelProperty(value = "车辆大类")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @NotNull(message = "需要输入车辆大类")
    private Long kindCode;
    /**
     * 车辆类型
     */
    @ApiModelProperty(value = "车辆类型")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @NotNull(message = "需要输入车辆类型")
    private Long entityCategoryId;

    private String entityCategoryName;

    /**
     * 所属部门
     */
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @ApiModelProperty(value = "所属部门")
    @NotNull(message = "需要输入所属部门")
    private Long deptId;

    private String deptName;



    /**
     * 排班ID
     */
    @ApiModelProperty(value = "排班ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long scheduleId;
    /**
     * 排班名称
     */
    @ApiModelProperty(value = "排班名称")
    private String scheduleName;
    /**
     * 排班类型,1是周期性、2是弹性
     */
    @ApiModelProperty(value = "排班类型,1是周期性、2是弹性")
    private String scheduleType;

    /**
     * 班次起始时间
     */
    @ApiModelProperty(value = "班次起始时间")
    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date scheduleBeginTime;
    /**
     * 班次结束时间
     */
    @ApiModelProperty(value = "班次结束时间")
    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date scheduleEndTime;
    /**
     * 休息起始时间
     */
    @ApiModelProperty(value = "休息起始时间")
    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date breaksBeginTime;
    /**
     * 休息结束时间
     */
    @ApiModelProperty(value = "休息结束时间")
    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date breaksEndTime;

    @ApiModelProperty(value = "绑定的工作区域")
    private List<WorkareaInfo> workareaInfoList;
    @ApiModelProperty(value = "人员状态")
    private Long personStatus;
    @ApiModelProperty(value = "人员状态名称")
    private String personStatusName;

}
