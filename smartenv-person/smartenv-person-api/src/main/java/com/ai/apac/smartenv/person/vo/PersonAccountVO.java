package com.ai.apac.smartenv.person.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author qianlong
 * @description 用户员工信息
 * @Date 2020/12/10 9:12 上午
 **/
@Data
public class PersonAccountVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private String jobNumber;
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @NotBlank(message = "需要输入人员姓名")
    private String personName;
    /**
     * 员工所属部门
     */
    @ApiModelProperty(value = "员工所属部门")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long personDeptId;
    /**
     * 员工所属职位
     */
    @ApiModelProperty(value = "员工所属职位")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long personPositionId;
    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private String mobileNumber;
    /**
     * 微信ID
     */
    @ApiModelProperty(value = "微信ID")
    private String wechatId;
    /**
     * 电子邮箱
     */
    @ApiModelProperty(value = "电子邮箱")
    private String email;

    @ApiModelProperty(value = "登录帐号")
    private String account;

    @ApiModelProperty(value = "登录帐号ID")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long accountId;

    @ApiModelProperty("租户ID")
    private String tenantId;
}
