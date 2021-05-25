package com.ai.apac.smartenv.alarm.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: MinicreateDsmAlarmVO
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
@ApiModel(value = "MinicreateDsmAlarmVO对象", description = "点创DSM告警对象")
public class MinicreateDsmAlarmVO implements Serializable {

    private static final long serialVersionUID = 286292310207057035L;
    
    private Integer eventType;
    private Integer fatigueDegree;
    private Integer level;
    
}
