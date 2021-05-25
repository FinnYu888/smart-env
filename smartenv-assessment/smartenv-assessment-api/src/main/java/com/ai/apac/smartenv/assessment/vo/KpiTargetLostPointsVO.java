package com.ai.apac.smartenv.assessment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @ClassName KpiTargetLostPointsVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/21 14:10
 * @Version 1.0
 */
@Data
@ApiModel(value = "考核指标扣分明细对象", description = "考核指标扣分明细对象")
public class KpiTargetLostPointsVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "考核指标ID")
    private Long KpiId;
    /**
     * 考核指标名称
     */
    @ApiModelProperty(value = "考核指标名称")
    private String KpiName;

    @ApiModelProperty(value = "考核指标扣分值")
    private Double lostPoints;

}
