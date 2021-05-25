package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.websocket.module.main.vo.ResOrder4HomeVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020/8/19 Asiainfo
 *
 * @ClassName: IResOrderService
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  17:19    zhanglei25          v1.0.0             修改原因
 */
public interface IResOrderService {

    /**
     * 推送首页最新任务列表
     * @param websocketTask
     */
    void pushLast6Order(WebsocketTask websocketTask);

    /**
     * 获取首页最新任务列表用于推送
     */
    List<ResOrder4HomeVO> getLast6Order(String tenantId,String userId);
}
