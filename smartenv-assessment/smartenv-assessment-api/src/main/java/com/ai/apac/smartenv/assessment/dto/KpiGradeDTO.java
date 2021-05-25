package com.ai.apac.smartenv.assessment.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @ClassName KpigradeDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/24 19:49
 * @Version 1.0
 */
@Data
public class KpiGradeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String kpiTargetId;

    private String scoreType;

    private String score;
}
