package com.ai.apac.smartenv.event.feign;

import com.ai.apac.smartenv.event.entity.EventMedium;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WorkareaClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: yupf3
 * @date: 2020/2/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/14  18:12    yupf3          v1.0.0             修改原因
 */
public class EventMediumClientFallback implements IEventMediumClient {

    @Override
    public R<List<EventMedium>> listEventMediumById(@RequestParam("eventId") Long eventId,Integer mediumDetailType) {
        return R.fail("接收数据失败");
    }
    @Override
    public R<List<EventMedium>> listEventMediumByAssignedId(@RequestParam("assignedId") Long assignedId) {
        return R.fail("接收数据失败");
    }
}
