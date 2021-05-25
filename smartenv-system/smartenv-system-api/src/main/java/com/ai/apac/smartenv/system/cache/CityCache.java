package com.ai.apac.smartenv.system.cache;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.system.entity.City;
import com.ai.apac.smartenv.system.entity.CityWeather;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.vo.CityInfoVO;
import com.ai.apac.smartenv.system.vo.CityVO;
import com.ai.smartenv.cache.util.SmartCache;
import com.google.common.collect.Lists;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.redis.cache.BladeRedisCache;
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
 * @description 城市信息cache
 * @Date 2020/2/28 8:24 上午
 **/
public class CityCache {

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
        getBladeRedisCache().del(CITY_MAP, CITY_NAME_MAP, CITY_TREE);
        R<List<City>> result = getSysClient().getAllCity();
        if (result != null && result.getData() != null) {
            List<City> cityList = result.getData();
            cityList.stream().forEach(city -> {
                SmartCache.hset(CITY_MAP, city.getId(), city);
                SmartCache.hset(CITY_NAME_MAP, city.getId(), city.getCityName());
            });
            reloadCityTree(cityList);
        }
    }

    /**
     * 重新加载城市列表
     *
     * @param allCity
     */
    public static void reloadCityTree(List<City> allCity) {
        if (allCity == null || allCity.size() == 0) {
            R<List<City>> result = getSysClient().getAllCity();
            allCity = result.getData();
        }
        allCity = allCity.stream().sorted(Comparator.comparing(City::getId)).collect(Collectors.toList());
        List<CityVO> cityVOList = new ArrayList<CityVO>(allCity.size());
        allCity.stream().forEach(city -> {
            CityVO cityVO = entityVO(city);
            cityVOList.add(cityVO);
        });
        List<CityVO> cityTree = ForestNodeMerger.merge(cityVOList);
        getBladeRedisCache().set(CacheNames.CITY_TREE, cityTree);

    }

    /**
     * 获取城市树
     *
     * @return
     */
    public static List<CityVO> getCityTree() {
        List<CityVO> cityInfoVOList = getBladeRedisCache().get(CacheNames.CITY_TREE);
        if (cityInfoVOList == null || cityInfoVOList.size() == 0) {
            reloadCityTree(null);
        }
        cityInfoVOList = getBladeRedisCache().get(CacheNames.CITY_TREE);
        return cityInfoVOList;
    }

    /**
     * 根据城市名称模糊搜索城市
     *
     * @param cityName
     * @return
     */
    public static List<CityVO> getCityByName(String cityName) {
        if (StringUtil.isBlank(cityName)) {
            return null;
        }
        List<CityVO> cityInfoVOList = new ArrayList<>();
        List<City> allCity = getBladeRedisCache().hVals(CITY_MAP);
        if (allCity == null || allCity.size() == 0) {
            reload();
            allCity = getBladeRedisCache().hVals(CITY_MAP);
        }
        allCity.forEach(city -> {
            if (city.getCityName() != null && city.getCityName().indexOf(cityName) >= 0) {
                CityVO cityInfoVO = entityVO(city);
                cityInfoVOList.add(cityInfoVO);
            }
        });
        if (cityInfoVOList == null || cityInfoVOList.size() == 0) {
            R<List<City>> result = getSysClient().getCityByName(cityName);
            if (result != null && result.getData() != null) {
                for (City city : result.getData()) {
                    SmartCache.hset(CITY_MAP, city.getId(), city);
                    SmartCache.hset(CITY_NAME_MAP, city.getId(), city.getCityName());
                    CityVO cityInfoVO = entityVO(city);
                    cityInfoVOList.add(cityInfoVO);
                }
            }
        }
        return cityInfoVOList;
    }

    private static CityVO entityVO(City city) {
        if (city == null) {
            return null;
        }
        CityVO cityVO = BeanUtil.copy(city, CityVO.class);
        String parentName = CityCache.getCityNameById(city.getParentId());
        cityVO.setParentName(parentName);
//        cityVO.setCityZh(city.getCityName());
//        cityVO.setCityEn(city.getCityName());
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
        if (cityId == 0L) {
            return "中国";
        }
        return SmartCache.hget(CITY_NAME_MAP, cityId, () -> {
            City city = getCity(cityId);
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
    public static City getCity(Long cityId) {
        if (cityId == null) {
            return null;
        }
        return SmartCache.hget(CITY_MAP, cityId, () -> {
            R<City> city = getSysClient().getCityById(cityId);
            return city.getData();
        });
    }

    /**
     * 根据ID查询上级城市
     *
     * @param cityId
     * @return
     */
    public static City getParentCity(Long cityId) {
        if (cityId == null) {
            return null;
        }
        City currentCity = getCity(cityId);
        if (currentCity == null) {
            return null;
        }
        //根据名称查询上级城市信息
        City parentCity = getCity(currentCity.getParentId());
        if (parentCity == null) {
            return null;
        }
        return parentCity;
    }

    public static void isReload(){
        List<City> allCity = getBladeRedisCache().hVals(CITY_MAP);
        if(allCity == null || allCity.size() == 0){
            reload();
        }
    }
}
