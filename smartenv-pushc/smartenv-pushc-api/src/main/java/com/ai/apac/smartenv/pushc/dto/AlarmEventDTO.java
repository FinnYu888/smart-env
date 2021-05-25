package com.ai.apac.smartenv.pushc.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 告警通知DTO对象
 * @Date 2020/10/14 9:15 上午
 **/
@Data
public class AlarmEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("微信用户唯一身份ID")
    private String unionId;

    @ApiModelProperty("微信公众号身份ID")
    private String mpOpenId;

    @ApiModelProperty("告警信息对象")
    private AlarmInfoDTO alarmInfoDTO;
}
