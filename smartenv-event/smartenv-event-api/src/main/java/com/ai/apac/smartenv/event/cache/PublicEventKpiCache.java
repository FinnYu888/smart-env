package com.ai.apac.smartenv.event.cache;

import com.ai.apac.smartenv.event.entity.PublicEventKpi;
import com.ai.apac.smartenv.event.feign.IPublicEventIKpiClient;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.common.cache.CacheNames.PUBLIC_EVENT_KPI_ID_MAP;
import static com.ai.apac.smartenv.common.cache.CacheNames.PUBLIC_EVENT_KPI_MAP;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/17 4:28 下午
 **/
public class PublicEventKpiCache {

    private static IPublicEventIKpiClient publicEventIKpiClient = null;

    private static BladeRedis bladeRedis = null;

    private static IPublicEventIKpiClient getPublicEventIKpiClient() {
        if (publicEventIKpiClient == null) {
            publicEventIKpiClient = SpringUtil.getBean(IPublicEventIKpiClient.class);
        }
        return publicEventIKpiClient;
    }

    private static BladeRedis getBladeRedis() {
        if (bladeRedis == null) {
            bladeRedis = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedis;
    }

    /**
     * 重载所有数据
     */
    public static void reload() {
        SmartCache.clear(PUBLIC_EVENT_KPI_MAP);
        R<List<PublicEventKpi>> result = getPublicEventIKpiClient().listAllKpi();
        if (result.isSuccess() && result.getData() != null) {
            List<PublicEventKpi> allData = result.getData();
            Map<Long, List<PublicEventKpi>> dataMap = allData.stream().collect(Collectors.groupingBy(PublicEventKpi::getCityId));
            dataMap.entrySet().stream().forEach(data -> {
                Long cityId = data.getKey();
                List<PublicEventKpi> dataList = data.getValue();
                SmartCache.hset(PUBLIC_EVENT_KPI_MAP, cityId, dataList);
            });
            Map<Long, PublicEventKpi> collect = allData.stream().collect(Collectors.toMap(PublicEventKpi::getId, publicEventKpi -> publicEventKpi));
            collect.entrySet().stream().forEach(data->{
                SmartCache.hset(PUBLIC_EVENT_KPI_ID_MAP, data.getKey(), data.getValue());
            });


        }

    }

    /**
     * 根据id获取KPI定义
     *
     * @param kpiId
     * @return
     */
    public static PublicEventKpi getKpiById(final Long kpiId) {
        return SmartCache.hget(PUBLIC_EVENT_KPI_ID_MAP, kpiId, () -> {
            R<PublicEventKpi> result = null;
            if (kpiId == null || kpiId <= 0L) {
                return null;
            } else {
                result = getPublicEventIKpiClient().getKpiById(kpiId);
            }
            if (result.isSuccess() && result.getData() != null) {
                return result.getData();
            }
            return null;
        });
    }


    /**
     * 根据城市获取KPI定义
     *
     * @param cityId
     * @return
     */
    public static List<PublicEventKpi> getKpiByCityId(final Long cityId) {
        return SmartCache.hget(PUBLIC_EVENT_KPI_MAP, cityId, () -> {
            R<List<PublicEventKpi>> result = null;
            if (cityId == null || cityId <= 0L) {
                result = getPublicEventIKpiClient().listKpiByCityId(100000L);
            } else {
                result = getPublicEventIKpiClient().listKpiByCityId(cityId);
            }

            if (result.isSuccess() && result.getData() != null) {
                return result.getData();
            }
            return null;
        });
    }
}
