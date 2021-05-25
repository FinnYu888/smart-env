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
package com.ai.apac.smartenv.workarea.service;

import com.ai.apac.smartenv.workarea.dto.RoadAreaDTO;
import com.ai.apac.smartenv.workarea.dto.SimpleWorkAreaInfoDTO;
import com.ai.apac.smartenv.workarea.entity.WorkareaRoadInfo;
import com.ai.apac.smartenv.workarea.vo.WorkareaRoadInfoVO;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 服务类
 *
 * @author Blade
 * @since 2021-01-08
 */
public interface IWorkareaRoadInfoService extends BaseService<WorkareaRoadInfo> {

    /**
     * 自定义分页
     *
     * @param page
     * @param workareaRoadInfo
     * @return
     */
    IPage<WorkareaRoadInfoVO> selectWorkareaRoadInfoPage(IPage<WorkareaRoadInfoVO> page, WorkareaRoadInfoVO workareaRoadInfo);

    /**
     * 保存工作区域信息和道路考核信息
     * @param simpleWorkAreaInfo
     * @param roadInfoList
     * @return
     */
    boolean saveWorkAreaRoadInfo(SimpleWorkAreaInfoDTO simpleWorkAreaInfo, List<WorkareaRoadInfo> roadInfoList);

    List<RoadAreaDTO> getRoadAreaByTenantId(String tenantId);

    /**
     * 获取项目的机动车道面积
     * @param projectCode
     * @param areaLevel
     * @return
     */
    Double getTotalMotorwayArea(String projectCode,Integer areaLevel);
}
