package com.ai.apac.smartenv.system.service.impl;

import cn.hutool.http.HttpUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.system.cache.CityCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.dto.CityDTO;
import com.ai.apac.smartenv.system.dto.WeatherDTO;
import com.ai.apac.smartenv.system.dto.WeatherInfoDTO;
import com.ai.apac.smartenv.system.entity.City;
import com.ai.apac.smartenv.system.entity.CityWeather;
import com.ai.apac.smartenv.system.mapper.CityMapper;
import com.ai.apac.smartenv.system.mapper.CityWeatherMapper;
import com.ai.apac.smartenv.system.service.IWeatherService;
import com.ai.apac.smartenv.system.vo.CityVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/9 8:43 上午
 **/
@Service
@Slf4j
public class WeatherServiceImpl extends BaseServiceImpl<CityWeatherMapper, CityWeather> implements IWeatherService {

    //请求连接地址
    final static String SOJSON_WEATHER_URL = "http://t.weather.sojson.com/api/weather/city/{1}";
    final static String TIANQI_WEATHER_URL = "https://www.tianqiapi.com/api/?version=v6&appid={1}&appsecret={2}&city={3}";

    private static List<CityDTO> allCityList = null;

    public static HashMap<String, CityDTO> cityNameMap = null;

    private static final String CITY_CODE = "cityCode:";
    private static final String CITY_NAME = "cityName:";

    @Value("${weather.tianqiapi.appId}")
    private String appId;

    @Value("${weather.tianqiapi.appSecret}")
    private String appSecret;

    @Autowired
    private BladeRedisCache bladeRedisCache;

    @Autowired
    private CityWeatherMapper cityWeatherMapper;

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private IOssClient ossClient;

    /**
     * 根据城市编码或城市名称获取天气情况
     *
     * @param cityCode
     * @param cityName
     * @return
     */
    @Override
    public R<WeatherDTO> getWeather(String cityCode, String cityName) {
        WeatherDTO weatherDTO = null;
        if (StringUtils.isBlank(cityCode) && StringUtils.isBlank(cityName)) {
            return R.fail("请输入城市编码或城市名称");
        }
        //默认先从缓存中读取,否则再调用API获取天气情况
        if (StringUtils.isNotBlank(cityCode)) {
            weatherDTO = this.getByCode(cityCode);
        } else if (StringUtils.isNotBlank(cityName)) {
            List<CityDTO> cityList = this.getCityByName(cityName);
            if (cityList != null && cityList.size() != 1) {
                return R.fail("根据城市名称获取到多个地区的天气,请重新查询");
            }
            weatherDTO = this.getByCode(cityList.get(0).getCityCode());
        }
        if (weatherDTO == null) {
            return R.fail("没有获取到该地区的天气信息");
        }
        return R.data(weatherDTO);
    }

    /**
     * 根据cityId获取天气信息
     *
     * @param cityCode
     * @return
     */
    @Override
    public WeatherDTO getByCode(String cityCode) {
        log.info("WeatherService#getById: cityCode={}", cityCode);
        if (StringUtils.isBlank(cityCode)) {
            return null;
        }
        try {
            //默认先从缓存中读取,否则再调用API获取天气情况
            String key = CacheNames.WEATHER + ":" + CITY_CODE + cityCode;
            Object weatherObj = bladeRedisCache.get(key);
            if (weatherObj != null) {
                return (WeatherDTO) weatherObj;
            }

            RestTemplate restTemplate = new RestTemplate();
            WeatherDTO dto = restTemplate.getForObject(SOJSON_WEATHER_URL, WeatherDTO.class, cityCode);

            if (dto != null && dto.isSuccess()) {
                //将天气情况放入缓存中,过期时间为1小时
                bladeRedisCache.setEx(key, dto, 3600L);
                return dto;
            } else {
                log.error("获取天气数据返回错误：{}", dto);
            }
        } catch (RestClientException e) {
            log.error("获取天气数据返回错误，出现异常.", e);
        }
        return null;
    }

    /**
     * 访问tianqiapi网站来获取实时天气情况
     *
     * @param cityName
     * @return
     */
    @Override
    public R<WeatherInfoDTO> getWeatherByName(String cityName) {
        log.info("WeatherService#getWeatherByName: cityName={}", cityName);
        if (StringUtils.isBlank(cityName)) {
            return null;
        }
        try {
            //先从数据库中查询到对应的城市编码
            Long cityCode = null;
//            QueryWrapper<City> queryWrapper = new QueryWrapper<City>();
//            queryWrapper.like("city_name", cityName);
            List<CityVO> cityList = CityCache.getCityByName(cityName);
            if (cityList == null || cityList.size() == 0) {
                return R.fail("根据城市名称无法获取到该地区的天气,请重新查询");
            } else if (cityList != null && cityList.size() > 1) {
                return R.fail("根据城市名称获取到多个地区的天气,请重新查询");
            } else if (cityList != null && cityList.size() == 1) {
                cityCode = cityList.get(0).getId();
            }
            //默认先从缓存中读取,否则再调用API获取天气情况
            String key = CacheNames.WEATHER + ":" + CITY_CODE + cityCode;
            Object weatherObj = bladeRedisCache.get(key);
            if (weatherObj != null) {
                return R.data((WeatherInfoDTO) weatherObj);
            }

            RestTemplate restTemplate = new RestTemplate();
            WeatherInfoDTO dto = restTemplate.getForObject(TIANQI_WEATHER_URL, WeatherInfoDTO.class, appId, appSecret, cityName);


            if (dto != null && cityName.indexOf(dto.getCityName()) >= 0) {
                dto = this.addWeaImgPath(dto);
                //将天气情况放入缓存中,过期时间为1小时
                bladeRedisCache.setEx(key, dto, 3600L);
                return R.data(dto);
            } else {
                //可能城市名称中包含了市/县/区,去掉后重新查询,如果还没有则直接返回错误
                if (cityName.indexOf("市") > 0 || cityName.indexOf("县") > 0 || cityName.indexOf("区") > 0) {
                    cityName = cityName.substring(0, cityName.length() - 1);
                }
                dto = restTemplate.getForObject(TIANQI_WEATHER_URL, WeatherInfoDTO.class, appId, appSecret, cityName);
                if (dto != null) {
                    dto = this.addWeaImgPath(dto);
                    //将天气情况放入缓存中,过期时间为1小时
                    bladeRedisCache.setEx(key, dto, 3600L);
                    return R.data(dto);
                }
                log.error("获取天气数据返回错误：{}", dto);
            }
        } catch (RestClientException e) {
            log.error("获取天气数据返回错误，出现异常.", e);
        }
        return R.fail("没有获取到天气信息");
    }

    private WeatherInfoDTO addWeaImgPath(WeatherInfoDTO dto) {
        if (ObjectUtil.isNotEmpty(dto.getWeaImg())) {
            String value = DictCache.getValue("wea_img_path", dto.getWeaImg());
            if (!ObjectUtil.isNotEmpty(value)) {
                value = DictCache.getValue("wea_img_path", "yin");
            }
            try{
                String weaImgPath = ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, value).getData();
                dto.setWeaImgPath(weaImgPath);
            }catch (Exception ex){
                log.error("获取天气图片异常:OSS域异常");
            }
        }
        return dto;
    }

    @Override
    public List<CityDTO> getAllCity() {
        if (allCityList != null && allCityList.size() > 0) {
            return allCityList;
        }

        allCityList = new ArrayList<CityDTO>();
        String jsonContent = "";
        BufferedReader reader = null;
        try {
            Resource resource = new ClassPathResource("citycode.json");
            reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                jsonContent += tempString;
            }
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        allCityList = JSON.parseArray(jsonContent, CityDTO.class);
        if (allCityList != null && allCityList.size() > 0) {
            cityNameMap = new HashMap<String, CityDTO>();
            allCityList.stream().forEach(cityDTO -> {
                cityNameMap.put(cityDTO.getCityName(), cityDTO);
            });
        }
        return allCityList;
    }

    /**
     * 根据城市名称获取城市信息
     *
     * @param cityName
     * @return
     */
    @Override
    public List<CityDTO> getCityByName(String cityName) {
        if (allCityList == null || allCityList.size() == 0) {
            getAllCity();
        }
        List<CityDTO> cityList = allCityList.stream()
                .filter(cityDTO -> cityDTO.getCityName().contains(cityName))
                .collect(Collectors.toList());
        return cityList;
    }

    /**
     * 对Map内所有value作utf8编码，拼接返回结果
     *
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     */
    public String toQueryString(Map<?, ?> data)
            throws UnsupportedEncodingException {
        StringBuffer queryString = new StringBuffer();
        for (Map.Entry<?, ?> pair : data.entrySet()) {
            queryString.append(pair.getKey() + "=");
            queryString.append(URLEncoder.encode((String) pair.getValue(),
                    "UTF-8") + "&");
        }
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }
        return queryString.toString();
    }

    /**
     * 来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
     *
     * @param md5
     * @return
     */
    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
}
