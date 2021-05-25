package com.ai.apac.smartenv.websocket.module.person.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 人员信息
 * @Date 2020/2/24 9:17 上午
 **/
@Data
public class PersonInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("人员姓名")
    private String personName;

    @ApiModelProperty("人员绑定终端的ID")
    private String deviceId;

    @ApiModelProperty("人员绑定终端的code")
    private String deviceCode;

    @ApiModelProperty("人员ID")
    private String personId;

    @ApiModelProperty("当前车辆状态")
    private Integer status;

    @ApiModelProperty("当前车辆状态名称")
    private String statusName;

    @ApiModelProperty("显示图标")
    private String icon;

    @ApiModelProperty("是否显示")
    private Boolean showFlag = false;
}
