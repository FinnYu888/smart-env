package com.ai.apac.smartenv.system.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/4/16 4:46 下午
 **/
@Data
public class ContactPersonVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "员工ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 工号
     */
    @ApiModelProperty(value = "工号")
    @Length(max = 20, message = "人员工号长度不能超过20")
    private String jobNumber;
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @Length(max = 24, message = "人员姓名长度不能超过24")
    private String personName;
    /**
     * 员工所属部门
     */
    @ApiModelProperty(value = "员工所属部门ID")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long personDeptId;

    @ApiModelProperty(value = "员工呢称")
    private String nickName;

    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "手机号")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private String mobileNumber;

    @ApiModelProperty(value = "岗位名称")
    private String stationName;

    /**
     * 电子邮箱
     */
    @ApiModelProperty(value = "电子邮箱")
    @Email(message = "需要输入正确的邮箱地址")
    private String email;

    /**
     * 是否在职
     */
    @ApiModelProperty(value = "是否在职")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Integer isIncumbency;

    @ApiModelProperty(value = "在职状态名称")
    private String isIncumbencyName;
}
