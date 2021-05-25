package com.ai.apac.smartenv.address.service;

import com.ai.apac.smartenv.address.dto.CoordsAllSystem;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: GisInfoCacheService
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/6/1
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/6/1 10:12    panfeng          v1.0.0             修改原因
 */

public interface IGisInfoCacheService {


    /**
     * 保存或者更新 坐标值
     * @param from 保存的源坐标系
     * @param coordsAllSystemList
     * @return
     */
    Boolean saveOrupdateCoordsAllSystemList(BaiduMapUtils.CoordsSystem from, List<CoordsAllSystem> coordsAllSystemList);
    /**
     * 保存或者更新一个 坐标值
     * @param from 保存的源坐标系
     * @param coordsAllSystem
     * @return
     */
    Boolean saveOrUpdate(BaiduMapUtils.CoordsSystem from, CoordsAllSystem coordsAllSystem);
    /**
     * 获取一个坐标
     * @param coordsSystem 传入坐标的坐标系
     * @param coords 传入的坐标，
     * @return
     */
    CoordsAllSystem getCoordsAllSystem(BaiduMapUtils.CoordsSystem coordsSystem, Coords coords);


    /**
     * 获取一个坐标
     * @param from 原始坐标的坐标系
     * @param target 要获取的坐标系
     * @param coords 原始坐标
     * @return
     */
    Coords getCoords(BaiduMapUtils.CoordsSystem from, BaiduMapUtils.CoordsSystem target, Coords coords);
}
