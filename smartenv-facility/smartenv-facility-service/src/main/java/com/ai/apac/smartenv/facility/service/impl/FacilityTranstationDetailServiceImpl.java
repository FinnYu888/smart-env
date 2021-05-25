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
package com.ai.apac.smartenv.facility.service.impl;

import com.ai.apac.smartenv.common.constant.DbEventConstant;
import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.facility.entity.FacilityTranstationDetail;
import com.ai.apac.smartenv.facility.entity.GarbageAmountDaily;
import com.ai.apac.smartenv.facility.entity.TranstationEveryDay;
import com.ai.apac.smartenv.facility.mapper.FacilityTranstationDetailMapper;
import com.ai.apac.smartenv.facility.service.IFacilityTranstationDetailService;
import com.ai.apac.smartenv.facility.vo.FacilityTranstationDetailVO;
import com.ai.apac.smartenv.facility.vo.LastDaysRegionGarbageAmountVO;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.ai.apac.smartenv.websocket.feign.IBigScreenDataClient;
import com.ai.apac.smartenv.websocket.feign.IHomeDataClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-02-14
 */
@Service
@AllArgsConstructor
public class FacilityTranstationDetailServiceImpl extends BaseServiceImpl<FacilityTranstationDetailMapper, FacilityTranstationDetail> implements IFacilityTranstationDetailService {

    private IBigScreenDataClient bigScreenDataClient;

    private IHomeDataClient homeDataClient;

    private IDataChangeEventClient dataChangeEventClient;


    @Override
    public IPage<FacilityTranstationDetailVO> selectFacilityTranstationDetailPage(IPage<FacilityTranstationDetailVO> page, FacilityTranstationDetailVO facilityTranstationDetail) {
        return page.setRecords(baseMapper.selectFacilityTranstationDetailPage(page, facilityTranstationDetail));
    }

    @Override
    public FacilityTranstationDetail getCurrentDayWorkInfo(Long facilityId) {
        return baseMapper.getCurrentDayWorkInfo(facilityId);
    }

    @Override
    public FacilityTranstationDetail statisticalEveryDate( Long facilityId, String startDate, String endDate) {
        QueryWrapper totalWrapper = new QueryWrapper();
        totalWrapper.select(	"  SUM(garbage_weight) garbage_weight","SUM(transfer_times) transfer_times");
        if (StringUtil.isNotBlank(startDate)) totalWrapper.ge("transfer_time",startDate);
        if (StringUtil.isNotBlank(endDate))  totalWrapper.le("transfer_time",endDate);

        totalWrapper.eq("facility_Id",facilityId);
        FacilityTranstationDetail  detailTotal = this.getOne(totalWrapper);
        return detailTotal;
    }

    @Override
    public IPage<TranstationEveryDay> staticsTranstationEveryDay(IPage page, Long facilityId, String startDate, String endDate) {
        return page.setRecords(baseMapper.staticsTranstationEveryDay(page, facilityId,startDate,endDate));
    }

    @Override
    public IPage<FacilityTranstationDetail> listfacilityTranstationDetail(IPage page, Long facilityId, String startDate, String endDate, String garbageType) {
       // List<FacilityTranstationDetailVO> list= baseMapper.listfacilityTranstationDetail(page,facilityId,startDate,endDate,garbageType);
        QueryWrapper<FacilityTranstationDetail> queryWrapper  = new QueryWrapper<>();
        /*queryWrapper.between("TRANSFER_TIME",startDate,endDate);*/
        //queryWrapper.apply("TRANSFER_TIME >= '"+startDate+"' AND TRANSFER_TIME <= '"+endDate+"'");
        if (StringUtil.isNotBlank(startDate)) {
            queryWrapper.gt("TRANSFER_TIME",startDate);
        }
        if (StringUtil.isNotBlank(endDate)) {
            queryWrapper.lt("TRANSFER_TIME",endDate);
        }
        if (StringUtil.isNotBlank(garbageType)) {
            queryWrapper.eq("GARBAGE_TYPE",garbageType);
        }

        queryWrapper.eq("FACILITY_ID",facilityId);
        queryWrapper.orderByDesc("TRANSFER_TIME");
        IPage<FacilityTranstationDetail> iPage = baseMapper.selectPage(page,queryWrapper);
        return iPage;
    }

    @Override
    public List<GarbageAmountDaily> lastDaysGarbageAmount(String garbageType, String startDate,
                                                        String endDate,String tenantId) {
        return baseMapper.lastDaysGarbageAmount(garbageType,startDate,endDate,tenantId);
    }

    @Override
    public List<LastDaysRegionGarbageAmountVO> lastDaysGarbageAmountGroupByRegion(String startDate, String endDate,String tenantId) {
        return baseMapper.lastDaysGarbageAmountGroupByRegion(startDate,endDate,tenantId);
    }

    @Override
    public Boolean saveDetail(FacilityTranstationDetail facilityTranstationDetail) {
        Boolean status =  this.save(facilityTranstationDetail);
        if(status){
            String tenantId = facilityTranstationDetail.getTenantId();
            dataChangeEventClient.doWebsocketEvent(new BaseWsMonitorEventDTO<String>(WsMonitorEventConstant.EventType.GARBAGE_EVENT, tenantId, tenantId,null));
        }
        return status;
    }


}
