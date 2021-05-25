package com.ai.apac.smartenv.event.dto.mongo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName GreenScreenEventDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/22 16:25
 * @Version 1.0
 */
@Data
public class GreenScreenEventDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    String eventId;

    String eventName;

    String eventStatus;

    String eventTime;
}

