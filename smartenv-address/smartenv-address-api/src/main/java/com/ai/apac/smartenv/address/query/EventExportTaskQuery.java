package com.ai.apac.smartenv.address.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.mp.support.Query;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: TrackExportTaskQuery
 * @Description:
 * @version: v1.0.0
 * @author: yupf3
 * @date: 2020/3/5
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/5  16:28    yupf3          v1.0.0             修改原因
 */
@Data
public class EventExportTaskQuery extends Query {
    /**
     * 查询开始时间
     */
    @ApiModelProperty(value = "查询开始时间")
    private String conditionBeginTime;
    /**
     * 查询结束时间
     */
    @ApiModelProperty(value = "查询结束时间")
    private String conditionEndTime;

    /**
     * 所属片区
     */
    @ApiModelProperty(value = "所属片区")
    private String belongArea;


//    CommonConstant.ENTITY_TYPE


    /**
     * 导出状态 1：进行中  2 已完成
     */
    @ApiModelProperty(value = "导出状态 1：进行中  2 已完成")
    private Integer exportStatus;



}
