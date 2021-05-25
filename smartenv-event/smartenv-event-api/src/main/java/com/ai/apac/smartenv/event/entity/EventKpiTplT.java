package com.ai.apac.smartenv.event.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "指标预览对象", description = "指标预览对象")
public class EventKpiTplT  extends TenantEntity {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    private String catalogName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long eventKpiCatalog;

    private String  eventKpiName;

    private String appraisalCriteria;

    private double threshold;
}