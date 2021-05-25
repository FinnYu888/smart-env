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

import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.ai.apac.smartenv.alarm.dto.AlarmInfoCountDTO;
import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.event.dto.EventQueryDTO;
import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.event.vo.EventInfoVO;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.constant.RegionConstant;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.vo.BigScreenInfoVO;
import com.ai.apac.smartenv.system.vo.BusiRegionTreeVO;
import com.ai.apac.smartenv.system.vo.BusiRegionVO;
import com.ai.apac.smartenv.system.vo.RegionVO;
import com.ai.apac.smartenv.system.mapper.RegionMapper;
import com.ai.apac.smartenv.system.service.IRegionService;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaNodeClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 服务实现类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
public class RegionServiceImpl extends BaseServiceImpl<RegionMapper, Region> implements IRegionService {
    private IWorkareaNodeClient workareaNodeClient;
    private IWorkareaClient workareaClient;
    private IPersonClient personClient;
    private CoordsTypeConvertUtil coordsTypeConvertUtil;
    private MongoTemplate mongoTemplate;
    private IEventInfoClient eventInfoClient;
    private IAlarmInfoClient alarmInfoClient;
    @Override
    public IPage<RegionVO> selectRegionPage(IPage<RegionVO> page, RegionVO region) {
        return page.setRecords(baseMapper.selectRegionPage(page, region));
    }

    /**
     * 删除区域
     *
     * @param regionIds
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean removeRegion(String regionIds) {
    	if(StringUtils.isBlank(regionIds)){
    		throw new ServiceException("请选择需要删除的区域");
		}
    	List<Long> regionIdList = Func.toLongList(regionIds);
        LambdaQueryWrapper<Region> queryWrapper = Wrappers.<Region>query().lambda()
                .in(Region::getParentRegionId, regionIdList);
        Integer cnt = baseMapper.selectCount(queryWrapper);
        if (cnt > 0) {
            throw new ServiceException("请先删除下级区域!");
        }

        for (Long aLong : regionIdList) {  // 删除业务区域网格坐标
            //删除区域时校验该区域是否规划了作业区域
            List<WorkareaInfo> workareaInfoList = workareaClient.getWorkareaInfoByRegion(aLong).getData();
            if (workareaInfoList != null && workareaInfoList.size() > 0) {
                throw new ServiceException("请先删除下级工作区域或路线!");
            }
            workareaNodeClient.deleteWorkAreaNodes(aLong);
        }

		boolean result = removeByIds(regionIdList);
		return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean savaOrUpdateRegionNew(BusiRegionVO busiRegionVO) {
        boolean result = false;
        //保存区域对象
        Region region = busiRegionVO.getRegion();
        if(StringUtils.isNotBlank(region.getRegionManager())) {
            region.setRegionManagerName(personClient.getPerson(Long.valueOf(region.getRegionManager())).getData().getPersonName());
        }
        if(region.getId() != null && region.getId() != 0L){ //edit
            this.updateById(region);
            //同步更新事件中对应片区主管
            EventQueryDTO eventQueryDTO = new EventQueryDTO();
            eventQueryDTO.setTenantId(AuthUtil.getTenantId());
            eventQueryDTO.setBelongArea(region.getId());
            List<EventInfoVO> eventInfos = eventInfoClient.listEventInfoByParam(eventQueryDTO).getData();
            if (eventInfos != null && eventInfos.size() > 0 && eventInfos.get(0).getId() != null) {
                for (EventInfoVO eventInfo : eventInfos) {
                    eventInfo.setHandlePersonId(region.getRegionManager());
                    eventInfo.setHandlePersonName(region.getRegionManagerName());
                    eventInfoClient.updateEventInfo(eventInfo);
                }
            }

            // 同步更新区域、路线中对应的片区主管
            List<WorkareaInfo> workareaInfoList = workareaClient.getWorkareaInfoByRegion(region.getId()).getData();
            if (workareaInfoList != null && workareaInfoList.size() > 0 && workareaInfoList.get(0).getId() != null) {
                for (WorkareaInfo workareaInfo : workareaInfoList) {
                    workareaInfo.setAreaHead(Long.valueOf(region.getRegionManager()));
                    workareaClient.updateWorkareaInfo(workareaInfo);
                }
            }
            // 每次编辑调用前先删除原有的坐标点
            if(region.getRegionType().intValue() == RegionConstant.REGION_TYPE.BUSI_REGION) {
                workareaNodeClient.deleteWorkAreaNodes(region.getId());
            }
        }else {
            this.save(region);
        }

        WorkareaNode[] workareaNodes = busiRegionVO.getWorkareaNodes();
        if (workareaNodes != null && workareaNodes.length > 0) {
            for (WorkareaNode workareaNode: workareaNodes ) {
                workareaNode.setRegionId(region.getId());
            }
            if (workareaNodes.length > 0) {
                coordsTypeConvertUtil.fromWebConvert(Arrays.asList(workareaNodes));
                for (WorkareaNode workareaNode : workareaNodes) {
                    workareaNodeClient.saveWorkAreaNode(workareaNode).getData();
                }
            }
        }


        result = true;
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public BusiRegionVO queryBusiRegionList(Long regionId) {
        BusiRegionVO busiRegionVO = new BusiRegionVO();
        Region region = this.getById(regionId);
        busiRegionVO.setRegion(region);
        R<List<WorkareaNode>> listR = workareaNodeClient.queryRegionNodesList(regionId);
        if (listR.getData() != null && listR.getData().size() > 0) {
            coordsTypeConvertUtil.toWebConvert(listR.getData());
            busiRegionVO.setWorkareaNodes(listR.getData().toArray(new WorkareaNode[listR.getData().size()]));
        }
        return busiRegionVO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public BigScreenInfoVO queryBigScreenInfoCountByRegion(Long regionId,String tenantId) {
        BigScreenInfoVO bigScreenInfoVO = new BigScreenInfoVO();
        Region region = this.getById(regionId);
        bigScreenInfoVO.setRegionName(region.getRegionName());

        //人员数量、过滤休假数据
        Query query = Query.query(Criteria.where("tenantId").is(tenantId).and("personBelongRegion").is(regionId).and("watchDeviceCode").ne(null));
        List<BasicPersonDTO> basicPersonDTOList = mongoTemplate.find(query, BasicPersonDTO.class);
        bigScreenInfoVO.setPersonCount(basicPersonDTOList.size());
        //车辆数量 过滤休假数据
        Query query1 = Query.query(Criteria.where("tenantId").is(tenantId).and("vehicleBelongRegion").is(regionId).and("gpsDeviceCode").ne(null));
        List<BasicVehicleInfoDTO> basicVehicleInfoDTOS = mongoTemplate.find(query1, BasicVehicleInfoDTO.class);
        bigScreenInfoVO.setVehicleCount(basicVehicleInfoDTOS.size());

        //事件数量
        Integer eventCount =  eventInfoClient.countEventDailyByParam(tenantId,regionId).getData();
        bigScreenInfoVO.setEventCount(eventCount);
        //告警数量

        // 1-人员告警数量
        Integer personAlarmCount = 0;
        if (basicPersonDTOList.size() > 0) {

            List<Long> entityIds = new ArrayList<>();
            for (BasicPersonDTO basicPersonDTO : basicPersonDTOList) {
                entityIds.add(basicPersonDTO.getId());
            }
            AlarmInfoCountDTO alarmInfoCountDTO = new AlarmInfoCountDTO();
            alarmInfoCountDTO.setTenantId(tenantId);
            alarmInfoCountDTO.setEntityIds(entityIds);
            alarmInfoCountDTO.setIsHandle(AlarmConstant.IsHandle.HANDLED_NO);
            personAlarmCount = alarmInfoClient.countAlarmInfoAmountByEntityIds(alarmInfoCountDTO).getData();
        }

        // 1-车辆告警数量
        Integer vehicleAlarmCount = 0;

        if (basicVehicleInfoDTOS.size() > 0) {
            List<Long> entityIds = new ArrayList<>();
            for (BasicVehicleInfoDTO basicVehicleInfoDTO : basicVehicleInfoDTOS) {
                entityIds.add(basicVehicleInfoDTO.getId());
            }
            AlarmInfoCountDTO alarmInfoCountDTO = new AlarmInfoCountDTO();
            alarmInfoCountDTO.setTenantId(tenantId);
            alarmInfoCountDTO.setEntityIds(entityIds);
            alarmInfoCountDTO.setIsHandle(AlarmConstant.IsHandle.HANDLED_NO);
            vehicleAlarmCount = alarmInfoClient.countAlarmInfoAmountByEntityIds(alarmInfoCountDTO).getData();
        }
        bigScreenInfoVO.setAlarmCount(personAlarmCount + vehicleAlarmCount);
        return bigScreenInfoVO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public BigScreenInfoVO queryBigScreenInfoCountByAllRegion(String tenantId) {
        BigScreenInfoVO bigScreenInfoVO = new BigScreenInfoVO();
        bigScreenInfoVO.setRegionName("全部");

        //人员数量、过滤休假人员
        Query query = Query.query(Criteria.where("tenantId").is(tenantId).and("personBelongRegion").ne(null).and("watchDeviceCode").ne(null));
        List<BasicPersonDTO> basicPersonDTOList = mongoTemplate.find(query, BasicPersonDTO.class);
        bigScreenInfoVO.setPersonCount(basicPersonDTOList.size());
        //车辆数量 ,过滤休假车辆
        Query query1 = Query.query(Criteria.where("tenantId").is(tenantId).and("vehicleBelongRegion").ne(null).and("gpsDeviceCode").ne(null));
        List<BasicVehicleInfoDTO> basicVehicleInfoDTOS = mongoTemplate.find(query1, BasicVehicleInfoDTO.class);
        bigScreenInfoVO.setVehicleCount(basicVehicleInfoDTOS.size());

        //事件数量
        Integer eventCount =  eventInfoClient.countEventDaily(tenantId).getData();
        bigScreenInfoVO.setEventCount(eventCount);
        //告警数量

        // 1-人员告警数量
        Integer personAlarmCount = 0;
        AlarmInfoCountDTO alarmInfoCountDTO = new AlarmInfoCountDTO();
        alarmInfoCountDTO.setTenantId(tenantId);
        alarmInfoCountDTO.setIsHandle(AlarmConstant.IsHandle.HANDLED_NO);
        personAlarmCount = alarmInfoClient.countAlarmInfoAmountByEntityIds(alarmInfoCountDTO).getData();

        bigScreenInfoVO.setAlarmCount(personAlarmCount);
        return bigScreenInfoVO;
    }

    @Override
    public BusiRegionTreeVO queryChildBusiRegionList(Long regionId) {
        BusiRegionTreeVO treeVO = new BusiRegionTreeVO();
        List<BusiRegionVO> busiRegionVOS = new ArrayList<>();
        //当前区域
        BusiRegionVO regionVO = queryBusiRegionList(regionId);
        if (regionVO != null && regionVO.getRegion() != null) {
            treeVO.setBusiRegionVO(regionVO);
        }

        List<Region> regions = this.list(new QueryWrapper<Region>().eq("parent_region_id",regionId));
        if (regions != null && regions.size() > 0) {
            for (Region region : regions) {
                BusiRegionVO busiRegionVO = new BusiRegionVO();
                busiRegionVO.setRegion(region);
                R<List<WorkareaNode>> listR = workareaNodeClient.queryRegionNodesList(region.getId());
                if (listR.getData() != null && listR.getData().size() > 0) {
                    coordsTypeConvertUtil.toWebConvert(listR.getData());
                    busiRegionVO.setWorkareaNodes(listR.getData().toArray(new WorkareaNode[listR.getData().size()]));
                }
                busiRegionVOS.add(busiRegionVO);
            }
        }
        treeVO.setChildBusiRegionVOList(busiRegionVOS);

        return treeVO;
    }

    @Override
    public List<BusiRegionVO> queryAllBusiRegionAndNodes(String regionType,String tenantId) {

        List<BusiRegionVO> busiRegionVOList = new ArrayList<>();

        String regionType_ = RegionConstant.REGION_TYPE.BUSI_REGION +","+ RegionConstant.REGION_TYPE.GREEN_REGION; // 两个都是业务区域
        if(ObjectUtil.isNotEmpty(regionType)){
            regionType_ = regionType;
        }
        List<Region> regionList = this.list(new QueryWrapper<Region>().in("region_type",Func.toIntList(regionType_)).eq("tenant_id",tenantId));
        if (regionList != null && regionList.size() > 0) {
            for (Region region : regionList) {
                BusiRegionVO busiRegionVO = new BusiRegionVO();
                if (region.getRegionManager() != null && !"".equals(region.getRegionManager())) {
                    Person persion = personClient.getPerson(Long.valueOf(region.getRegionManager())).getData();
                    String name = region.getRegionName() + "("+persion.getPersonName()+")";
                    region.setExt1(name);
                }
                busiRegionVO.setRegion(region);
                R<List<WorkareaNode>> listR = workareaNodeClient.queryRegionNodesList(region.getId());
                if (listR.getData() != null && listR.getData().size() > 0) {
                    coordsTypeConvertUtil.toWebConvert(listR.getData());
                    busiRegionVO.setWorkareaNodes(listR.getData().toArray(new WorkareaNode[listR.getData().size()]));
                }
                busiRegionVOList.add(busiRegionVO);
            }
        }

        return busiRegionVOList;
    }

    /**
     * 查询所有上级是行政区域的业务区域
     */
    @Override
    public List<Region> queryAllBusiRegionList(String regionType,String tenantId) {


        String regionType_ = RegionConstant.REGION_TYPE.BUSI_REGION +","+ RegionConstant.REGION_TYPE.GREEN_REGION; // 两个都是业务区域
        if(ObjectUtil.isNotEmpty(regionType)){
            regionType_ = regionType;
        }
        List<Region> regionList = this.list(new QueryWrapper<Region>().in("region_type",Func.toIntList(regionType_)).eq("tenant_id",tenantId));
        if (regionList != null && regionList.size() > 0) {
            for (Region region : regionList) {
                if (region.getRegionManager() != null && !"".equals(region.getRegionManager())) {
                    Person persion = personClient.getPerson(Long.valueOf(region.getRegionManager())).getData();
                    String name = region.getRegionName() + "("+persion.getPersonName()+")";
                    region.setExt1(name);
                }
            }
        }

        return regionList;
    }

    @Override
    public List<Region> queryBusiRegionListForBS(String regionType,String tenantId) {
        QueryWrapper<VehicleInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("a.region_type", regionType);
        queryWrapper.eq("a.tenant_Id", tenantId);
        List<Region> regionList  = baseMapper.selectRegionListForBS(queryWrapper);
        return regionList;
    }
}
