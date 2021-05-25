package com.ai.apac.smartenv.system.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //修改密码视图类
 * @Date 2020/1/15 3:40 下午
 **/
@Data
@ApiModel(value = "UpdatePasswordVO对象", description = "UserVO对象")
public class UpdatePasswordVO implements Serializable {

    @ApiModelProperty(value = "旧密码")
    private String oldPassword;

    @ApiModelProperty(value = "新密码")
    private String newPassword;

    @ApiModelProperty(value = "确认密码")
    private String newPassword1;
}
