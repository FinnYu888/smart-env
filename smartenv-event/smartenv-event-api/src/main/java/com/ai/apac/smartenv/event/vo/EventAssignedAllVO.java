package com.ai.apac.smartenv.event.vo;

import com.ai.apac.smartenv.event.entity.EventAssignedHistory;
import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.event.entity.EventInfoKpiRel;
import com.ai.apac.smartenv.event.entity.EventMedium;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * 事件指派信息表视图实体类
 *
 * @author Blade
 * @since 2020-02-06
 */
@Data
//@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EventAssignedAllVO对象", description = "事件指派信息")
public class EventAssignedAllVO {

    private static final long serialVersionUID = 1L;
    private EventAssignedHistory eventAssignedHistory;
    private List<EventMedium> eventMediumList;

    private List<EventInfoKpiRel> eventInfoKpiRelList;// 指标
}
