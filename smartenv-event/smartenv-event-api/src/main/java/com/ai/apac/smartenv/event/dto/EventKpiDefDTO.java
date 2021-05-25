package com.ai.apac.smartenv.event.dto;

import com.ai.apac.smartenv.event.entity.EventKpiDef;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventKpiDefDTO extends EventKpiDef {
    private static final long serialVersionUID = 1L;

    private Long catalogLevel;

    private String catalogName;
}
