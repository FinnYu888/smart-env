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
package com.ai.apac.smartenv.system.service;

import com.ai.apac.smartenv.system.entity.City;
import com.ai.apac.smartenv.system.vo.CityVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *  服务类
 *
 * @author qianlong
 * @since 2020-02-09
 */
public interface ICityService extends IService<City> {

    /**
     * 获取所有城市
     * @return
     */
    List<City> getAllCity();

    /**
     * 获取城市树结构
     * @return
     */
    List<CityVO> getCityTree();

    /**
     * 根据主键查询城市信息
     * @param cityId
     * @return
     */
    City getCityById(Long cityId);

    /**
     * 根据主键查询城市名称
     * @param cityId
     * @return
     */
    String getCityNameById(Long cityId);
}
