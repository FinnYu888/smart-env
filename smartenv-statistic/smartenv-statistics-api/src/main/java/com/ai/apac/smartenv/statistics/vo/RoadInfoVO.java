package com.ai.apac.smartenv.statistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2021/1/11 3:33 下午
 **/
@Data
public class RoadInfoVO implements Serializable {

    @ApiModelProperty("道路等级")
    private String roadLevel;

    @ApiModelProperty("道路总面积")
    private String totalArea;

    @ApiModelProperty("道路工作完成情况")
    private List<WorkInfoVO> workInfoList;
}
