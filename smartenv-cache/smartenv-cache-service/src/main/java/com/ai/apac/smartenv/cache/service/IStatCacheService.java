package com.ai.apac.smartenv.cache.service;

import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/11/4 2:36 下午
 **/
public interface IStatCacheService {

    /**
     * 重新刷首页四个数字的统计
     * @param tenantId
     */
    void reloadHomeSummaryAmount(String tenantId);

    /**
     * 重新刷新综合数字统计
     * @param tenantId
     */
    void reloadSummaryAmount(String tenantId);
}
