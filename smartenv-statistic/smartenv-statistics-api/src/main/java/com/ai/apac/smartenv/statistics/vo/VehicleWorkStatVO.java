package com.ai.apac.smartenv.statistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author qianlong
 * @description 车辆工作完成情况统计
 * @Date 2021/1/11 3:21 下午
 **/
@Data
public class VehicleWorkStatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("统计日期")
    private String statDate;

    @ApiModelProperty("道路清扫明细数据")
    private List<RoadInfoVO> roadInfoList;

    @ApiModelProperty("项目编码")
    private String projectCode;

    @ApiModelProperty("项目名称")
    private String projectName;

//    @ApiModelProperty("一级道路工作情况")
//    private RoadInfoVO firstLevelRoadInfo;
//
//    @ApiModelProperty("二级道路工作情况")
//    private RoadInfoVO secondLevelRoadInfo;
//
//    @ApiModelProperty("三级道路工作情况")
//    private RoadInfoVO threeLevelRoadInfo;
//
//    @ApiModelProperty("四级道路工作情况")
//    private RoadInfoVO fourLevelRoadInfo;
}
