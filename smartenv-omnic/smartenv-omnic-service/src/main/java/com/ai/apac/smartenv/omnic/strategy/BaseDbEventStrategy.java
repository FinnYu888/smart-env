package com.ai.apac.smartenv.omnic.strategy;

import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;

/**
 * 数据库事件策略
 * @author qianlong
 * @param <T>
 */
public abstract class BaseDbEventStrategy<T> {

    /**
     * 当前策略支持的任务
     * @return
     */
    public abstract String getSupportEventType();

    /**
     * 具体的策略实现
     * @param baseDbEventDTO
     */
    public abstract void strategy(BaseDbEventDTO<T> baseDbEventDTO);

}
