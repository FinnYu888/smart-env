package com.ai.apac.smartenv.statistics.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 工作完成率情况
 * @Date 2021/1/12 2:15 下午
 **/
@Data
@ApiModel("工作完成率情况")
public class WorkCompleteRateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("工作时间段")
    private String workTimePeriod;

    @ApiModelProperty(value = "规划工作路线面积")
    private String workAreaAcreage;

    @ApiModelProperty(value = "实时作业面积")
    private String realWorkAcreage;

    @ApiModelProperty("面积单位")
    private String areaUnit;

    @ApiModelProperty("完成率")
    private String completeRate;

    @ApiModelProperty("作业类型")
    private String workType;

    @ApiModelProperty("道路等级")
    private String roadLevel;

    @ApiModelProperty("项目编码")
    private String projectCode;

    @ApiModelProperty("项目名称")
    private String projectName;

    public WorkCompleteRateVO() {
    }

    public WorkCompleteRateVO(String workTimePeriod, String workAreaAcreage, String realWorkAcreage, String areaUnit, String completeRate, String workType, String roadLevel) {
        this.workTimePeriod = workTimePeriod;
        this.workAreaAcreage = workAreaAcreage;
        this.realWorkAcreage = realWorkAcreage;
        this.areaUnit = areaUnit;
        this.completeRate = completeRate;
        this.workType = workType;
        this.roadLevel = roadLevel;
    }
}
