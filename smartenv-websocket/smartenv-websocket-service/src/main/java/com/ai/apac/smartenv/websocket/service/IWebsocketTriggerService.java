package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.websocket.module.bigscreen.dto.GetBigScreenDto;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WebsocketTriggerService
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2021/1/4
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2021/1/4  15:27    panfeng          v1.0.0             修改原因
 */
public interface IWebsocketTriggerService {
    Boolean cangZScreenPosition(GetBigScreenDto bigScreenDto);
}
