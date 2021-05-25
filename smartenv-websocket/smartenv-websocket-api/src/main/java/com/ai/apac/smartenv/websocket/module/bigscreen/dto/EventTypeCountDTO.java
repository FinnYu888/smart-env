package com.ai.apac.smartenv.websocket.module.bigscreen.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2020/8/20 Asiainfo
 *
 * @ClassName: EventTypeCountDTO
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/20
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/20  14:56    zhanglei25          v1.0.0             修改原因
 */
@Data
@ApiModel(value = "指定类型事件VO对象", description = "指定类型事件VO对象")
public class EventTypeCountDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventType;

    private String eventTypeName;

    private String eventTypeCount;

}
