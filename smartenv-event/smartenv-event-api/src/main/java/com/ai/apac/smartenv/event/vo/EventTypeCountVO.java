package com.ai.apac.smartenv.event.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

/**
 * @ClassName TypeEventCountVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/19 10:11
 * @Version 1.0
 */
@Data
@ApiModel(value = "指定类型事件VO对象", description = "指定类型事件VO对象")
public class EventTypeCountVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventType;

    private String eventTypeName;

    private String eventTypeCount;

}
