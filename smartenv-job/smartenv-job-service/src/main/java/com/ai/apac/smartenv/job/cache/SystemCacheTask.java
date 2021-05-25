package com.ai.apac.smartenv.job.cache;

import com.ai.apac.smartenv.system.cache.CityCache;
import com.ai.apac.smartenv.system.cache.MenuCache;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author qianlong
 * @description 系统缓存定时刷新
 * @Date 2020/4/6 9:29 上午
 **/
@Component
@AllArgsConstructor
public class SystemCacheTask {

    /**
     * 每隔10秒检查是否需要刷新
     */
    @Scheduled(fixedRate = 10000)
    public void reloadMenu() {
        MenuCache.isReload();
    }

    /**
     * 每隔10秒检查是否需要刷新f
     */
    @Scheduled(fixedRate = 10000)
    public void reloadCity() {
        CityCache.isReload();
    }
}
