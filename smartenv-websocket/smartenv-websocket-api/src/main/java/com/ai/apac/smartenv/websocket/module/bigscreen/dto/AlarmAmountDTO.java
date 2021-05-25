package com.ai.apac.smartenv.websocket.module.bigscreen.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2020/8/20 Asiainfo
 *
 * @ClassName: AlarmAmountDTO
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/20
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/20  14:15    zhanglei25          v1.0.0             修改原因
 */
@Data
@ApiModel(value = "每日各种种类告警数量对象", description = "每日各种种类告警数量对象")
public class AlarmAmountDTO implements Serializable {
    private static final long serialVersionUID = -3564378328281623663L;

    @ApiModelProperty(value = "车辆超速告警级别")
    private Integer vehicleSpeedingAlarmCount;

    @ApiModelProperty(value = "车辆越界告警级别")
    private Integer vehicleOutOfAreaAlarmCount;

    @ApiModelProperty(value = "车辆违规告警级别")
    private Integer vehicleViolationAlarmCount;

    @ApiModelProperty(value = "人员违规告警级别")
    private Integer personViolationAlarmCount;

    @ApiModelProperty(value = "人员异常告警级别")
    private Integer personUnusualAlarmCount;





}
