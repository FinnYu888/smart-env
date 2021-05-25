package com.ai.apac.smartenv.omnic.strategy;

import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.utils.SpringUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 一个策略管理上下文，用于处理
 * @author qianlong
 */
@Component
public class DbStrategyContext {

    /**
     * EventType 对应的策略
     */
    private Map<String, BaseDbEventStrategy> strategyMap;

    private boolean initEd = false;

    /**
     * 初始化策略列表
     */
    private void initStrategyContext() {
        if (initEd) {
            return;
        }
        Map<String, BaseDbEventStrategy> beansOfType = SpringUtil.getContext().getBeansOfType(BaseDbEventStrategy.class);
        strategyMap = new HashMap<>();

        for (BaseDbEventStrategy dbEventStrategy : beansOfType.values()) {
            if (strategyMap.containsKey(dbEventStrategy.getSupportEventType())) {
                throw new ServiceException("重复的策略实现：" + dbEventStrategy.getSupportEventType() + "   实现类：" +
                        dbEventStrategy.getClass().getName() + " 和" + strategyMap.containsKey(dbEventStrategy.getSupportEventType().getClass().getName()));
            }
            strategyMap.put(dbEventStrategy.getSupportEventType(), dbEventStrategy);
        }
        initEd = true;
    }

    /**
     * 寻找合适的策略执行事件对应的任务
     */
    public void doAction(BaseDbEventDTO dbEventDTO) {
        initStrategyContext();
        //取到对应的策略
        BaseDbEventStrategy dbEventStrategy = strategyMap.get(dbEventDTO.getEventType());
        if (dbEventStrategy == null) {
            throw new ServiceException("无法处理的任务类型：" + dbEventDTO.getEventType());

        }
        //应该要传入问题策略的。要不要异步
        dbEventStrategy.strategy(dbEventDTO);
    }
}
