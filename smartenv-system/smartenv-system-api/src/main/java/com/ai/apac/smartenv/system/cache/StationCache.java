package com.ai.apac.smartenv.system.cache;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.system.entity.Station;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ai.apac.smartenv.common.cache.CacheNames.STATION_MAP;

/**
 * @author qianlong
 * @description //岗位缓存
 * @Date 2020/4/27 11:48 上午
 **/
public class StationCache {

    private static ISysClient sysClient;

    private static ISysClient getSysClient() {
        if (sysClient == null) {
            sysClient = SpringUtil.getBean(ISysClient.class);
        }
        return sysClient;
    }

    public static void reload() {
        List<Tenant> tenantList = TenantCache.getAllTenant();
        tenantList.stream().forEach(tenant -> {
            reload(tenant.getTenantId());
        });
    }

    public static void reload(String tenantId) {
        SmartCache.clear(STATION_MAP);
        R<List<Station>> result = getSysClient().getStationByTenant(tenantId);
        if (result != null && result.getData() != null) {
            List<Station> stationList = result.getData();
            stationList.stream().forEach(station -> {
                SmartCache.hset(STATION_MAP, station.getId(), station);
            });
        }
    }

    /**
     * 新增/更新岗位信息
     *
     * @param station
     */
    public static void saveOrUpdateStation(Station station) {
        SmartCache.hset(STATION_MAP, station.getId(), station);
    }

    /**
     * 从缓存中删除岗位
     *
     * @param stationId
     */
    public static void deleteStation(Long stationId) {
        SmartCache.hdel(STATION_MAP, stationId);
    }

    /**
     * 根据主键查询岗位
     *
     * @param stationId
     * @return
     */
    public static Station getStation(Long stationId) {
        return SmartCache.hget(STATION_MAP, stationId, () -> {
            R<Station> result = getSysClient().getStationById(stationId);
            return result.getData();
        });
    }

    /**
     * 根据主键查询岗位名称
     *
     * @param stationId
     * @return
     */
    public static String getStationName(Long stationId) {
        Station station = getStation(stationId);
        if (station == null) {
            return null;
        }
        return station.getStationName();
    }
    /**
    * 获取所有岗位信息
    */
    public static Map<Long,Station> getStation(String tenantId) {
    Map<Long,Station> stationMap = new HashMap<>();
    try {
        List<Station> resultList = getSysClient().getStationByTenant(tenantId).getData();
        resultList.forEach(result->{
            stationMap.put(result.getId(),result);
        });
    }catch (Exception e) {

    }

        return stationMap;
    }
}
