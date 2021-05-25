package com.ai.apac.smartenv.event.dto;

import com.ai.apac.smartenv.event.entity.EventKpiTplRel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventKpiTplRelDTO extends EventKpiTplRel {
    private static final long serialVersionUID = 1L;

    private Long eventKpiCatalog;
    /**
     * 考核指标名称
     */
    private String eventKpiName;

    private String eventKpiCatalogName;

    private Integer eventKpiCatalogLevel;

    private String eventKpiDescription;

    private String appraisalCriteria;

    private String handleLimitTime;

    private String handleLimitTimeDesc;
}
