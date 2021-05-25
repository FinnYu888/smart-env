package com.ai.apac.smartenv.job.dto;

import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: PersonAllDataDto
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/5
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/5  09:48    panfeng          v1.0.0             修改原因
 */
public class PersonAllDataDto {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long id;
    /**
     * 工号
     */
    @ApiModelProperty(value = "工号")
    @Length(max = 20, message = "人员工号长度不能超过20")
    @NotBlank(message = "需要输入人员工号")
    private String jobNumber;
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @Length(max = 24, message = "人员姓名长度不能超过24")
    @NotBlank(message = "需要输入人员姓名")
    private String personName;
    /**
     * 员工所属部门
     */
    @ApiModelProperty(value = "员工所属部门")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @NotNull(message = "需要输入所属部门")
    private Long personDeptId;
    /**
     * 员工所属职位
     */
    @ApiModelProperty(value = "员工所属职位")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long personPositionId;

    /**
     * 人员头像
     */
    @ApiModelProperty(value = "人员头像")
    private String image;

    /**
     * 实体分类
     */
    @ApiModelProperty(value = "实体分类")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long entityCategoryId;

    /**
     * 是否在职
     */
    @ApiModelProperty(value = "是否在职")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @NotNull(message = "需要输入在职状态")
    private Integer isIncumbency;
    /**
     * 劳动合同
     */
    @ApiModelProperty(value = "劳动合同")
    private String laborContract;
    /**
     * 合同类型
     */
    @ApiModelProperty(value = "合同类型")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Integer contractType;
    /**
     * 是否允许登录系统
     */
    @ApiModelProperty(value = "是否允许登录系统")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Integer isUser;

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
