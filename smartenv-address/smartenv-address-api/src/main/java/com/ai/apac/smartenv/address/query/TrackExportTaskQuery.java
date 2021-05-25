package com.ai.apac.smartenv.address.query;

import com.ai.apac.smartenv.address.entity.TrackExportTask;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.utils.StringTimestampConvert;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.mp.support.Query;

import java.sql.Timestamp;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: TrackExportTaskQuery
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/5
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/5  16:28    panfeng          v1.0.0             修改原因
 */
@Data
public class TrackExportTaskQuery extends Query {
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
     * 导出对象名称
     */
    @ApiModelProperty(value = "导出对象名称")
    private String entityName;


//    CommonConstant.ENTITY_TYPE

    /**
     * 导出对象类型1：车辆  2：人员
     */
    @ApiModelProperty(value = "导出对象类型4：车辆  5：人员 ")
    private Integer entityType;

    /**
     * 导出状态 1：进行中  2 已完成
     */
    @ApiModelProperty(value = "导出状态 1：进行中  2 已完成")
    private Integer exportStatus;



}
