package com.ai.apac.smartenv.system.cache;

import cn.hutool.core.collection.CollUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.system.entity.AdministrativeCity;
import com.ai.apac.smartenv.system.entity.City;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.vo.AdministrativeCityVO;
import com.ai.apac.smartenv.system.vo.CityVO;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

/**
 * @author qianlong
 * @description 行政区域城市信息cache
 * @Date 2020/12/16 21:24 上午
 **/
public class AdminCityCache {

    public static ISysClient sysClient = null;

    private static BladeRedis bladeRedisCache = null;

    private static ISysClient getSysClient() {
        if (sysClient == null) {
            sysClient = SpringUtil.getBean(ISysClient.class);
        }
        return sysClient;
    }

    private static BladeRedis getBladeRedisCache() {
        if (bladeRedisCache == null) {
            bladeRedisCache = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedisCache;
    }

    public static void reload() {
        getBladeRedisCache().del(ADMIN_CITY_MAP, ADMIN_CITY_NAME_MAP, ADMIN_CITY_TREE);
        R<List<AdministrativeCity>> result = getSysClient().getAllAdministrativeCity();
        if (result != null && result.getData() != null) {
            List<AdministrativeCity> cityList = result.getData();
            cityList.stream().forEach(city -> {
                SmartCache.hset(ADMIN_CITY_MAP, city.getId(), city);
                SmartCache.hset(ADMIN_CITY_NAME_MAP, city.getId(), city.getCityName());
            });
            reloadCityTree();
        }
    }

    /**
     * 重新加载城市列表
     */
    public static void reloadCityTree() {
        List<AdministrativeCity> allCity = null;
        R<List<AdministrativeCity>> result = getSysClient().getAllAdministrativeCity();
        if (result.isSuccess() && CollUtil.isNotEmpty(result.getData())) {
            allCity = result.getData();
            allCity = allCity.stream().sorted(Comparator.comparing(AdministrativeCity::getId)).collect(Collectors.toList());
            List<AdministrativeCityVO> cityVOList = new ArrayList<AdministrativeCityVO>(allCity.size());
            allCity.stream().forEach(city -> {
                AdministrativeCityVO cityVO = entityVO(city);
                cityVOList.add(cityVO);
            });
            List<AdministrativeCityVO> cityTree = ForestNodeMerger.merge(cityVOList);
            getBladeRedisCache().set(CacheNames.ADMIN_CITY_TREE, cityTree);
        }
    }

    /**
     * 获取城市树
     *
     * @return
     */
    public static List<AdministrativeCityVO> getCityTree() {
        List<AdministrativeCityVO> cityInfoVOList = getBladeRedisCache().get(CacheNames.ADMIN_CITY_TREE);
        if (CollUtil.isEmpty(cityInfoVOList)) {
            reloadCityTree();
        }
        cityInfoVOList = getBladeRedisCache().get(CacheNames.ADMIN_CITY_TREE);
        return cityInfoVOList;
    }

    /**
     * 根据城市名称模糊搜索城市
     *
     * @param cityName
     * @return
     */
    public static List<AdministrativeCityVO> getCityByName(String cityName) {
        if (StringUtil.isBlank(cityName)) {
            return null;
        }
        List<AdministrativeCityVO> cityInfoVOList = new ArrayList<>();
        List<AdministrativeCity> allCity = getBladeRedisCache().hVals(ADMIN_CITY_MAP);
        if (allCity == null || allCity.size() == 0) {
            reload();
            allCity = getBladeRedisCache().hVals(ADMIN_CITY_MAP);
        }
        allCity.forEach(city -> {
            if (city.getCityName() != null && city.getCityName().indexOf(cityName) >= 0) {
                AdministrativeCityVO cityInfoVO = entityVO(city);
                cityInfoVOList.add(cityInfoVO);
            }
        });
        if (cityInfoVOList == null || cityInfoVOList.size() == 0) {
            R<List<AdministrativeCity>> result = getSysClient().getAdminCityByName(cityName);
            if (result != null && result.getData() != null) {
                for (AdministrativeCity city : result.getData()) {
                    SmartCache.hset(ADMIN_CITY_MAP, city.getId(), city);
                    SmartCache.hset(ADMIN_CITY_NAME_MAP, city.getId(), city.getCityName());
                    AdministrativeCityVO cityInfoVO = entityVO(city);
                    cityInfoVOList.add(cityInfoVO);
                }
            }
        }
        return cityInfoVOList;
    }

    private static AdministrativeCityVO entityVO(AdministrativeCity city) {
        if (city == null) {
            return null;
        }
        AdministrativeCityVO cityVO = BeanUtil.copy(city, AdministrativeCityVO.class);
        String parentName = AdminCityCache.getCityNameById(city.getParentId());
        cityVO.setParentName(parentName);
        return cityVO;
    }

    /**
     * 根据城市ID查询城市名称
     *
     * @param cityId
     * @return
     */
    public static String getCityNameById(Long cityId) {
        if (cityId == null) {
            return null;
        }
        if (cityId == 0L || cityId == 100000L || cityId == 000000L) {
            return "中国";
        }
        return SmartCache.hget(ADMIN_CITY_NAME_MAP, cityId, () -> {
            AdministrativeCity city = getCity(cityId);
            if (city != null) {
                return city.getCityName();
            }
            return null;
        });
    }

    /**
     * 根据行政区域ID获取城市名称
     *
     * @param cityId
     * @return
     */
    public static String getAdminCityNameById(Long cityId) {
        if (cityId == null) {
            return null;
        }
        if (cityId == 0L) {
            return "中国";
        }
        return SmartCache.hget(CITY_NAME_MAP, cityId, () -> {
            AdministrativeCity city = getCity(cityId);
            if (city != null) {
                return city.getCityName();
            }
            return null;
        });
    }

    /**
     * 根据城市ID查询城市对象
     *
     * @param cityId
     * @return
     */
    public static AdministrativeCity getCity(Long cityId) {
        if (cityId == null) {
            return null;
        }
        return SmartCache.hget(ADMIN_CITY_MAP, cityId, () -> {
            R<AdministrativeCity> city = getSysClient().getAdminCityById(cityId);
            return city.getData();
        });
    }

    /**
     * 根据ID查询上级城市
     *
     * @param cityId
     * @return
     */
    public static AdministrativeCity getParentCity(Long cityId) {
        if (cityId == null) {
            return null;
        }
        AdministrativeCity currentCity = getCity(cityId);
        if (currentCity == null) {
            return null;
        }
        //根据名称查询上级城市信息
        AdministrativeCity parentCity = getCity(currentCity.getParentId());
        if (parentCity == null) {
            return null;
        }
        return parentCity;
    }
}
