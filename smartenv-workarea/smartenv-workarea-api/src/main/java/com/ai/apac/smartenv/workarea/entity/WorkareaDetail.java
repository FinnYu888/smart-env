package com.ai.apac.smartenv.workarea.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WorkareaDetail
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/5
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/5  17:56    panfeng          v1.0.0             修改原因
 * 2020/2/6  11:45    yupf3          v1.0.1              引入参数对象
 */
@Data
@ApiModel(value = "WorkareaDetail对象", description = "工作区域全量信息")
public class WorkareaDetail {
    /**
     * 工作区域基本信息
     */
    @ApiModelProperty(value = "工作区域基本信息")
    private WorkareaInfo workareaInfo;
    /**
     * 工作区域节点列表
     */
    @ApiModelProperty(value = "工作区域节点列表")
    private WorkareaNode[] workareaNodes;
    @ApiModelProperty(value = "工作区域途径信息列表")
    private List<WorkareaPathway> workareaPathways;


    //历史轨迹查询条件
    @ApiModelProperty(value = "历史轨迹查询条件：实体ID")
    private Long entityId;

    //历史轨迹查询条件
    @ApiModelProperty(value = "历史轨迹查询条件：实体类型 车=2   人=5")
    private Long entityType;

    //历史轨迹查询条件
    @ApiModelProperty(value = "历史轨迹查询条件：开始时间  时间戳")
    private Long beginTime;
    //历史轨迹查询条件
    @ApiModelProperty(value = "历史轨迹查询条件：结束时间  时间戳")
    private Long endTime;

    @ApiModelProperty(value = "坐标类型")
    private Integer coordsType;


}
