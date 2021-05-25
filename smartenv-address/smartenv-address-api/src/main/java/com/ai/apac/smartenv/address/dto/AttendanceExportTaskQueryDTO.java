package com.ai.apac.smartenv.address.dto;

import com.ai.apac.smartenv.address.entity.AttendanceExportTask;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: AttendanceExportTaskQueryDTO
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/5/21
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/5/21 9:34     panfeng          v1.0.0             修改原因
 */
@Data
public class AttendanceExportTaskQueryDTO extends AttendanceExportTask {
    @ApiModelProperty(value = "分类ID")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    Long categoryId;
    @ApiModelProperty(value = "部门ID")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    Long deptId;
    @ApiModelProperty(value = "区域ID")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    Long regionId;

    @ApiModelProperty(value = "查询开始时间")
    Long queryBeginTime;
    @ApiModelProperty(value = "查询结束时间")
    Long queryEndTime;



}
