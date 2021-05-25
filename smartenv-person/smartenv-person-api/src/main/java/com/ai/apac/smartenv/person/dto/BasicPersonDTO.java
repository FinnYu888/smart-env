package com.ai.apac.smartenv.person.dto;

import com.ai.apac.smartenv.common.annotation.Latitude;
import com.ai.apac.smartenv.common.annotation.Longitude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author qianlong
 * @description 人员基本信息DTO
 * @Date 2020/3/21 07:21 上午
 **/
@Data
@ApiModel(value = "人员基本信息", description = "人员基本信息")
@EqualsAndHashCode
public class BasicPersonDTO implements Serializable {

    private static final long serialVersionUID = -8400328240837856787L;

    @ApiModelProperty(value = "人员ID")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @Indexed
    private Long id;

    /**
     * 工号
     */
    @ApiModelProperty(value = "工号")
    @Length(max = 20, message = "人员工号长度不能超过20")
    @NotBlank(message = "需要输入人员工号")
    @Indexed
    private String jobNumber;
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @Length(max = 24, message = "人员姓名长度不能超过24")
    @NotBlank(message = "需要输入人员姓名")
    @Indexed
    private String personName;
    /**
     * 员工所属部门
     */
    @ApiModelProperty(value = "员工所属部门")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @NotNull(message = "需要输入所属部门")
    @Indexed
    private Long personDeptId;
    /**
     * 员工所属职位
     */
    @ApiModelProperty(value = "员工所属职位")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @NotNull(message = "需要输入职位")
    @Indexed
    private Long personPositionId;

    @ApiModelProperty(value = "员工所属职位名称")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @Indexed
    private String personPositionName;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    @NotNull(message = "需要输入手机号码")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @Indexed
    private Long mobileNumber;
    /**
     * 微信ID
     */
    @ApiModelProperty(value = "微信ID")
    @Indexed
    private String wechatId;
    /**
     * 电子邮箱
     */
    @ApiModelProperty(value = "电子邮箱")
    @Length(max = 50, message = "邮箱地址长度不能超过50")
    @NotBlank(message = "需要输入邮箱地址")
    @Email(message = "需要输入正确的邮箱地址")
    @Indexed
    private String email;

    @ApiModelProperty(value = "工作状态")
    @Indexed
    private Integer workStatus;

    @ApiModelProperty(value = "工作状态名称")
    private String workStatusName;


    @Indexed
    private Integer todayAlarmCount;

    private String lastAlarmContent;


    @Indexed
    private String tenantId;

    // 实时位置
    @Latitude
    private String lat;
    @Longitude
    private String lng;

    //手表设备ID
    @Indexed
    private Long watchDeviceId;

    //手表设备编码
    @Indexed
    private String watchDeviceCode;

    /**
     * 员工工作所在片区
     */
    @ApiModelProperty(value = "员工工作所在片区")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @NotNull(message = "员工工作所在片区")
    @Indexed
    private Long personBelongRegion;


    /**
     * 手表电量百分比
     */
    private String watchBattery;

    /**
     * 车辆工作所在片区
     */
    @ApiModelProperty(value = "设备状态")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @Indexed
    private Long deviceStatus;

    private Date updateTime;

    private Date createTime;
}
