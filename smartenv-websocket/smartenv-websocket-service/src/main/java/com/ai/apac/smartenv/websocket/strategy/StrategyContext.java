package com.ai.apac.smartenv.websocket.strategy;

import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.utils.SpringUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: StrategyContext
 * @Description: 一个策略管理上下文，用于处理
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/10/27
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/10/27  2020/10/27    panfeng          v1.0.0             修改原因
 */

@Component
public class StrategyContext {

    /**
     * EventType 对应的策略
     */
    private Map<String, BaseWebsocketPushStrategy> strategyMap;

    private boolean initEd = false;


    /**
     * 初始化策略列表
     */
    private void initStrategyContext() {
        if (initEd) {
            return;
        }
        Map<String, BaseWebsocketPushStrategy> beansOfType = SpringUtil.getContext().getBeansOfType(BaseWebsocketPushStrategy.class);
        strategyMap = new HashMap<>();

        for (BaseWebsocketPushStrategy baseWebsocketPushStrategy : beansOfType.values()) {
            if (strategyMap.containsKey(baseWebsocketPushStrategy.getSupportEventType())) {
                throw new ServiceException("重复的策略实现：" + baseWebsocketPushStrategy.getSupportEventType() + "   实现类：" +
                        baseWebsocketPushStrategy.getClass().getName() + " 和" + strategyMap.containsKey(baseWebsocketPushStrategy.getSupportEventType().getClass().getName()));
            }
            strategyMap.put(baseWebsocketPushStrategy.getSupportEventType(), baseWebsocketPushStrategy);
        }
        initEd = true;
    }

    /**
     * 寻找合适的策略执行事件对应的任务
     */
    public void doAction(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {
        initStrategyContext();
        //取到对应的策略
        BaseWebsocketPushStrategy baseWebsocketPushStrategy = strategyMap.get(baseWsMonitorEventDTO.getEventType());
        if (baseWebsocketPushStrategy == null) {
            throw new ServiceException("无法处理的任务类型：" + baseWsMonitorEventDTO.getEventType());

        }
        //应该要传入问题策略的。要不要异步
        baseWebsocketPushStrategy.strategy(baseWsMonitorEventDTO);
    }


}
