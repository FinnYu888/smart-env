package com.ai.apac.smartenv.alarm.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: MinicreatAdasAlarmVO
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/9/10
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/9/10     zhaidx           v1.0.0               修改原因
 */
@Data
@EqualsAndHashCode
@ApiModel(value = "MinicreatAdasAlarmVO对象", description = "点创Adas告警对象")
public class MinicreatAdasAlarmVO implements Serializable {
    private static final long serialVersionUID = 1722578832387102315L;

    private Integer level;
    private Integer eventType;
    private String eventTypeName;
    private Integer frontCarSpeed;
    private Integer forwardDistance;
    private Integer ldwType;
    private String ldwTypeName;
    private Integer trafficSignType;
    private String trafficSignTypeName;
    private Integer trafficSignData;
}
