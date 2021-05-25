package com.ai.apac.smartenv.event.dto.mongo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName GreenScreenEventDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/22 16:25
 * @Version 1.0
 */
@Data
public class GreenScreenEventsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    String tenantId;

    List<GreenScreenEventDTO> lastDaysEvents;
}

