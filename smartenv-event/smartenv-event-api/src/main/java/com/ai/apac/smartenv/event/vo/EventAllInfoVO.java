package com.ai.apac.smartenv.event.vo;

import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.event.entity.EventInfoKpiRel;
import com.ai.apac.smartenv.event.entity.EventMedium;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * 事件基本信息表视图实体类
 *
 * @author Blade
 * @since 2020-02-06
 */
@Data
//@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EventAllInfoVO对象", description = "事件全量信息表")
public class EventAllInfoVO {

    private static final long serialVersionUID = 1L;
    private EventInfo eventInfo;
    private List<EventMedium> eventMediumList;
    /**
     * 抄送人员
     */
    private List<CcPeopleVO> ccPeopleVOS;

    private List<EventInfoKpiRel> eventInfoKpiRelList;
}
