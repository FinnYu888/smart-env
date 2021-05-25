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
package com.ai.apac.smartenv.system.wrapper;

import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.entity.Station;
import com.ai.apac.smartenv.system.vo.StationVO;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.node.INode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author qianlong
 * @since 2020-04-27
 */
public class StationWrapper extends BaseEntityWrapper<Station, StationVO> {

    public static StationWrapper build() {
        return new StationWrapper();
    }

    @Override
    public StationVO entityVO(Station station) {
        StationVO stationVO = BeanUtil.copy(station, StationVO.class);
        String parentStationName = StationCache.getStationName(station.getParentId());
        stationVO.setParentStationName(parentStationName);
		String stationLevelName = DictCache.getValue("station_level", station.getStationLevel());
		stationVO.setStationLevelName(stationLevelName);
        String statusName = DictCache.getValue("station_status", station.getStatus());
        stationVO.setStatusName(statusName);
        return stationVO;
    }

    public List<INode> listNodeVO(List<Station> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            List<INode> collect = list.stream().map(station -> {
                return entityVO(station);
            }).collect(Collectors.toList());
            return ForestNodeMerger.merge(collect);
        }
        return null;
    }
}
