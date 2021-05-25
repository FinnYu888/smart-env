package com.ai.apac.smartenv.statistics.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author qianlong
 * @description 工作完成情况
 * @Date 2021/1/11 3:22 下午
 **/
@Data
@ApiModel("作业完成情况")
public class WorkInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("道路等级")
    private String roadLevel;

    @ApiModelProperty("作业类型")
    private String workType;

    @ApiModelProperty("作业类型名称")
    private String workTypeName;

    @ApiModelProperty(value = "规划作业面积")
    private Double workAreaAcreage;

    @ApiModelProperty("工作完成率")
    private List<WorkCompleteRateVO> workCompleteRateList;
}
