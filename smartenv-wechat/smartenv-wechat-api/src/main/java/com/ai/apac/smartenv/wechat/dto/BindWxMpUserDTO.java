package com.ai.apac.smartenv.wechat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/9/1 10:50 上午
 **/
@ApiModel("绑定微信公众号用户信息")
@Data
public class BindWxMpUserDTO implements Serializable {

    @ApiModelProperty("登录帐号,支持帐号或手机号")
    @NotEmpty(message = "帐号不能为空")
    private String account;

    @ApiModelProperty("登录密码")
    @NotEmpty(message = "登录密码不能为空")
    private String password;

    @ApiModelProperty("微信公众号用户ID")
    @NotEmpty(message = "微信公众号用户ID不能为空")
    private String mpOpenId;
}
