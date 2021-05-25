/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.system.service.impl;

import com.ai.apac.smartenv.system.entity.CityWeather;
import com.ai.apac.smartenv.system.mapper.CityWeatherMapper;
import com.ai.apac.smartenv.system.service.ICityWeatherService;
import com.ai.apac.smartenv.system.vo.CityInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务实现类
 *
 * @author BladeX
 * @since 2019-06-23
 */
@Service
public class CityWeatherServiceImpl extends BaseServiceImpl<CityWeatherMapper, CityWeather> implements ICityWeatherService {

    /**
     * 获取城市树结构列表
     *
     * @return
     */
    @Override
    public List<CityInfoVO> getCityTree() {
        //先获取所有城市
//        List<CityWeather> allCity = baseMapper.selectList(new QueryWrapper<CityWeather>());
//        List<CityWeather> provinceList = baseMapper.selectList(new QueryWrapper<CityWeather>().groupBy("province_en"));
//        List<CityInfoVO> cityTree = new ArrayList<CityInfoVO>();
//        if (provinceList != null && provinceList.size() > 0) {
//            provinceList.stream().forEach(province -> {
//                CityInfoVO provinceVO = new CityInfoVO();
//                provinceVO.setId(province.getId());
//                provinceVO.setParentId(0L);
//                provinceVO.setCityZh(province.getCityZh());
//                provinceVO.setCityEn(province.getCityEn());
//                provinceVO.setCityName(provinceVO.getCityZh());
//                provinceVO.setLat(province.getLat());
//                provinceVO.setLon(province.getLon());
//                //查询省份/直辖市下属城市或区域
//                List<CityInfoVO> cityList = new ArrayList<CityInfoVO>();
//                allCity.stream().forEach(city -> {
//                    if(city.getLeaderEn().equals(province.getCityEn())
//                            && !city.getId().equals(province.getId())){
//                        CityInfoVO cityInfoVO = new CityInfoVO();
//                        cityInfoVO.setId(city.getId());
//                        cityInfoVO.setParentId(province.getId());
//                        cityInfoVO.setCityZh(city.getCityZh());
//                        cityInfoVO.setCityEn(city.getCityEn());
//                        cityInfoVO.setCityName(cityInfoVO.getCityZh());
//                        cityInfoVO.setLat(city.getLat());
//                        cityInfoVO.setLon(city.getLon());
////                        cityVO.setChildren(null);
//                        cityList.add(cityInfoVO);
//                    }
//                });
//                if(cityList.size() > 0){
//                    provinceVO.setChildren(cityList);
//                }
//                cityTree.add(provinceVO);
//            });
//        }
//        return cityTree;
        return null;
    }
}
