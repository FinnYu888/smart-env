package com.ai.apac.smartenv.system.service;

import com.ai.apac.smartenv.system.dto.CityDTO;
import com.ai.apac.smartenv.system.dto.WeatherDTO;
import com.ai.apac.smartenv.system.dto.WeatherInfoDTO;
import com.ai.apac.smartenv.system.entity.CityWeather;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.core.tool.api.R;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/9 8:42 上午
 **/
public interface IWeatherService extends IService<CityWeather> {

    /**
     * 根据城市编码或城市名称获取天气情况
     * @param cityCode
     * @param cityName
     * @return
     */
    R<WeatherDTO> getWeather(String cityCode, String cityName);

    /**
     * 根据cityId获取天气信息
     * @param cityCode
     * @return
     */
    WeatherDTO getByCode(String cityCode);

    /**
     * 从配置文件中获取所有城市信息
     * @return
     */
    List<CityDTO> getAllCity();

    /**
     * 根据城市名称获取城市信息
     * @param cityName
     * @return
     */
    List<CityDTO> getCityByName(String cityName);

    /**
     * 根据城市名称获取城市实时天气
     * @param cityName
     * @return
     */
    R<WeatherInfoDTO> getWeatherByName(String cityName);

}
