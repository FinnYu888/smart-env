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
package com.ai.apac.smartenv.workarea.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.common.dto.AmapDrvierResult;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.system.cache.ProjectCache;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.workarea.dto.RoadAreaDTO;
import com.ai.apac.smartenv.workarea.dto.SimpleWorkAreaInfoDTO;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.entity.WorkareaRoadInfo;
import com.ai.apac.smartenv.workarea.service.IWorkareaInfoService;
import com.ai.apac.smartenv.workarea.service.IWorkareaNodeService;
import com.ai.apac.smartenv.workarea.vo.WorkareaRoadInfoVO;
import com.ai.apac.smartenv.workarea.mapper.WorkareaRoadInfoMapper;
import com.ai.apac.smartenv.workarea.service.IWorkareaRoadInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务实现类
 *
 * @author Blade
 * @since 2021-01-08
 */
@Service
public class WorkareaRoadInfoServiceImpl extends BaseServiceImpl<WorkareaRoadInfoMapper, WorkareaRoadInfo> implements IWorkareaRoadInfoService {

    @Autowired
    private IWorkareaInfoService workAreaInfoService;

    @Autowired
    private IWorkareaNodeService workAreaNodeService;

    @Override
    public IPage<WorkareaRoadInfoVO> selectWorkareaRoadInfoPage(IPage<WorkareaRoadInfoVO> page, WorkareaRoadInfoVO workareaRoadInfo) {
        return page.setRecords(baseMapper.selectWorkareaRoadInfoPage(page, workareaRoadInfo));
    }

    /**
     * 保存工作区域信息和道路考核信息
     *
     * @param simpleWorkAreaInfo
     * @param roadInfoList
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean saveWorkAreaRoadInfo(SimpleWorkAreaInfoDTO simpleWorkAreaInfo, List<WorkareaRoadInfo> roadInfoList) {
        if (CollUtil.isEmpty(roadInfoList)) {
            throw new ServiceException("道路考核数据不能为空");
        }
        Project project = ProjectCache.getProjectByCode(AuthUtil.getTenantId());
        Long adcode = project.getAdcode();
//        Long cityCode = Long.valueOf(adcode.toString().substring(0, 4).concat("00"));
        Long cityCode = 130900L;

        roadInfoList.stream().forEach(workAreaRoadInfo -> {
            String optFlag = BigDataHttpClient.OptFlag.ADD;
            WorkareaInfo workareaInfo = BeanUtil.copy(simpleWorkAreaInfo, WorkareaInfo.class);
            workareaInfo.setAreaName(workAreaRoadInfo.getStartAndEnd());
            workareaInfo.setAreaLevel(workAreaRoadInfo.getRoadLevel());
            workareaInfo.setRegionId(simpleWorkAreaInfo.getDivision());
            workareaInfo.setVehicleCount(0L);
            workareaInfo.setPersonCount(0L);
            workareaInfo.setWidth(workAreaRoadInfo.getMotorwayWight());

            //工作区域ID
            Long workAreaId = 0L;
            //先根据名称查询是否有相同的数据,如果有则更新
            List<WorkareaInfo> workAreaInfoList = workAreaInfoService.list(new LambdaQueryWrapper<WorkareaInfo>()
                    .eq(WorkareaInfo::getAreaName, workAreaRoadInfo.getStartAndEnd())
                    .eq(WorkareaInfo::getAreaLevel, workAreaRoadInfo.getRoadLevel()));
            if (CollUtil.isNotEmpty(workAreaInfoList) && workAreaInfoList.size() > 0) {
                optFlag = BigDataHttpClient.OptFlag.EDIT;
                WorkareaInfo workAreaInfoTmp = workAreaInfoList.get(0);
                BeanUtil.copy(workareaInfo, workAreaInfoTmp);
                workAreaId = workAreaInfoTmp.getId();
                workAreaInfoService.updateById(workareaInfo);
            } else {
                workAreaInfoService.save(workareaInfo);
                workAreaId = workareaInfo.getId();
            }

            //TODO 如果能根据起止地点获取到路线规划坐标,则直接保存
//            JSONArray nodes = new JSONArray();
//            String startAndEnd = workAreaRoadInfo.getStartAndEnd();
//            if (startAndEnd.indexOf(StringPool.DASH) > 0) {
//                String[] split = startAndEnd.split(StringPool.DASH);
//                if (split.length == 2) {
//                    String str_start = split[0];
//                    String str_end = split[1];
//                    Coords start = BaiduMapUtils.amapGeo(str_start, cityCode.toString());
//                    Coords end = BaiduMapUtils.amapGeo(str_end, cityCode.toString());
//
//                    if (start != null && end != null) {
//
//                        AmapDrvierResult amapDrvierResult = BaiduMapUtils.directionDriving(Collections.singletonList(start), end);
//                        if (amapDrvierResult != null && CollectionUtil.isNotEmpty(amapDrvierResult.getCoords())) {
//                            List<Coords> coords = amapDrvierResult.getCoords();
//                            // 每次先删除已有的nodes
//                            workAreaNodeService.remove(new QueryWrapper<WorkareaNode>().eq("workarea_id", workareaInfo.getId()));
//
//                            List<WorkareaNode> workareaNodes = coords.stream().map(coord -> {
//                                WorkareaNode workareaNode = new WorkareaNode();
//                                workareaNode.setLatitudinal(coord.getLatitude());
//                                workareaNode.setLongitude(coord.getLongitude());
//                                workareaNode.setWorkareaId(workareaInfo.getId());
//                                workareaNode.setRegionId(workareaInfo.getRegionId());
//                                return workareaNode;
//                            }).collect(Collectors.toList());
//                            // 删除完再新增
//                            for (WorkareaNode workareaNode : workareaNodes) {
////                            workareaNode.setWorkareaId(workareaInfo.getId()); // 取基本信息表主键
//                                workAreaNodeService.save(workareaNode);
//                                JSONObject node = new JSONObject();
//                                node.put("isDeleted", workareaNode.getIsDeleted());
//                                node.put("latitudinal", workareaNode.getLatitudinal());
//                                node.put("longitude", workareaNode.getLongitude());
//                                node.put("nodeId", workareaNode.getId()); // 取node信息表主键
//                                node.put("nodeSequence", workareaNode.getNodeSeq());
//                                node.put("status", workareaNode.getStatus());
//                                nodes.add(node);
//                            }
//
//                        }
//
//                    }
//                }
//            }

            //保存道路考核数据
            baseMapper.delete(new LambdaQueryWrapper<WorkareaRoadInfo>()
                    .eq(WorkareaRoadInfo::getStartAndEnd, workAreaRoadInfo.getStartAndEnd())
                    .eq(WorkareaRoadInfo::getRoadLevel, workAreaRoadInfo.getRoadLevel()));
            workAreaRoadInfo.setWorkareaId(workAreaId);
            baseMapper.insert(workAreaRoadInfo);

            try {
                //调用大数据
                JSONObject param = new JSONObject();
                param.put("optFlag", optFlag);
                param.put("areaAddress", workareaInfo.getAreaAddress());
                param.put("areaId", workareaInfo.getId());// 取基本信息表主键
                param.put("areaName", workareaInfo.getAreaName());
                param.put("areaType", workareaInfo.getWorkAreaType());
                param.put("isDeleted", workareaInfo.getIsDeleted());
                param.put("regionId", workareaInfo.getRegionId());
                param.put("tenant_id", workareaInfo.getTenantId());
//                param.put("nodes", nodes);
//                BigDataHttpClient.postDataToBigData("/smartenv-api/sync/region", param.toString());
            } catch (Exception ex) {
                throw new ServiceException("工作区域同步大数据失败");
            }
        });
        return true;
    }

    @Override
    public List<RoadAreaDTO> getRoadAreaByTenantId(String tenantId) {
        return baseMapper.getRoadAreaByTenantId(tenantId);
    }

    /**
     * 获取项目的机动车道面积
     *
     * @param projectCode
     * @param areaLevel
     * @return
     */
    @Override
    public Double getTotalMotorwayArea(String projectCode, Integer areaLevel) {
        LambdaQueryWrapper<WorkareaRoadInfo> queryWrapper = new LambdaQueryWrapper<WorkareaRoadInfo>();
        if (StringUtils.isNotEmpty(projectCode)) {
            queryWrapper.in(WorkareaRoadInfo::getTenantId, projectCode);
        }
        if (areaLevel != null && areaLevel != 0) {
            queryWrapper.in(WorkareaRoadInfo::getRoadLevel, areaLevel);
        }
        List<WorkareaRoadInfo> list = baseMapper.selectList(queryWrapper);
        Double totalArea = 0.0;
        for (WorkareaRoadInfo workareaRoadInfo : list) {
            totalArea = totalArea + Double.valueOf(workareaRoadInfo.getMotorwayLength()) * Double.valueOf(workareaRoadInfo.getMotorwayWight());
        }
        totalArea = Double.valueOf(NumberUtil.formatPercent(totalArea,2));
        return totalArea;
    }
}
