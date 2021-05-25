package com.ai.apac.smartenv.omnic.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: AlarmInfoHandleInfoVO
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/2/11
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/2/11     zhaidx           v1.0.0               修改原因
 */
@Data
@ApiModel(value = "每日各种种类告警数量对象", description = "每日各种种类告警数量对象")
public class AlarmAmountInfoVO implements Serializable {
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
