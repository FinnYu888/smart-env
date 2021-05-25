package com.ai.apac.smartenv.event.dto;

import com.ai.apac.smartenv.event.entity.EventInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;
import java.util.List;

/**
 * @ClassName EventQueryDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/11 16:45
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EventQueryDTO  extends EventInfo {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "告警条数")
    Integer eventNum;

    @ApiModelProperty(value = "开始时间")
    Long startTime;

    @ApiModelProperty(value = "结束时间")
    Long endTime;

    @ApiModelProperty(value = "事件等级List")
    private List<Long> eventLevels;

    @ApiModelProperty(value = "事件状态List")
    private List<Long> eventStatuses;

    @ApiModelProperty(value = "事件类型名称")
    private String eventTypeName;

}
