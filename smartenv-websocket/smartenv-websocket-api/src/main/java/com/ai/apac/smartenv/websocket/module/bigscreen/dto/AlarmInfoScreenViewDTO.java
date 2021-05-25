package com.ai.apac.smartenv.websocket.module.bigscreen.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Copyright: Copyright (c) 2020/8/20 Asiainfo
 *
 * @ClassName: AlarmInfoScreenViewDTO
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/20
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/20  14:33    zhanglei25          v1.0.0             修改原因
 */
@Data
@ApiModel(value = "大屏实时告警信息监控VO对象", description = "大屏实时告警信息监控VO对象")
public class AlarmInfoScreenViewDTO implements Serializable {
    private static final long serialVersionUID = -3564378328281623663L;

    @ApiModelProperty(value = "告警时间")
    private String alarmTime;

    @ApiModelProperty(value = "告警对象")
    private String alarmEntity;

    @ApiModelProperty(value = "告警信息")
    private String alarmMessage;

    @ApiModelProperty(value = "告警区域ID")
    private String alarmRegion;

    @ApiModelProperty(value = "告警区域")
    private String alarmRegionName;

    @ApiModelProperty(value = "告警类型名称")
    private String alarmTypeName;

}

