package com.ai.apac.smartenv.pushc.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 告警提醒信息对象
 * @Date 2020/10/15 8:53 下午
 **/
@Data
public class AlarmInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("告警ID")
    private Long alarmId;

    @ApiModelProperty("告警标题")
    private String alarmTitle;

    @ApiModelProperty("告警名称")
    private String alarmName;

    @ApiModelProperty("告警对象,人名或车牌号")
    private String objectName;

    @ApiModelProperty("告警发生地址")
    private String address;

    @ApiModelProperty("告警发生时间")
    private String alarmTime;

    @ApiModelProperty("告警级别")
    private String alarmLevel;
}
