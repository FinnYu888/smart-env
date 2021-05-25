package com.ai.apac.smartenv.assessment.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * @ClassName KpiTargetLostPointsStaVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/21 20:37
 * @Version 1.0
 */
@Data
@ApiModel(value = "考核指标扣分统计对象", description = "考核指标扣分统计对象")
public class KpiTargetLostPointsStaVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tenantId;

    private Integer days;

    private String staTime;

    private List<KpiTargetLostPointsVO> kpiTargetLostPointsVOList;

}
