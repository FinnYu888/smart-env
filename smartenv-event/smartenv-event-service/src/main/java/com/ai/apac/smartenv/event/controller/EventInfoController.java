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
package com.ai.apac.smartenv.event.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DbEventConstant;
import com.ai.apac.smartenv.common.constant.EventConstant;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.event.cache.EventCache;
import com.ai.apac.smartenv.event.dto.EventQueryDTO;
import com.ai.apac.smartenv.event.dto.mongo.EventInfoMongoDto;
import com.ai.apac.smartenv.event.dto.mongo.GreenScreenEventsDTO;
import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.event.service.IEventInfoService;
import com.ai.apac.smartenv.event.vo.*;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.feign.IPersonUserRelClient;
import com.ai.apac.smartenv.omnic.feign.ITrackClient;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.ai.apac.smartenv.system.vo.RegionVO;
import com.ai.apac.smartenv.websocket.feign.IBigScreenDataClient;
import com.ai.apac.smartenv.websocket.feign.IHomeDataClient;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;


/**
 * 事件基本信息表 控制器
 *
 * @author Blade
 * @since 2020-02-06
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/eventinfo")
@Api(value = "事件基本信息表", tags = "事件基本信息表接口")
public class EventInfoController extends BladeController {

    private IEventInfoService eventInfoService;

    private BaiduMapUtils baiduMapUtils;
    private MongoTemplate mongoTemplate;
    private IPersonUserRelClient personUserRelClient;
    private ITrackClient trackClient;
    private ISysClient sysClient;
    private IUserClient userClient;

    private IBigScreenDataClient bigScreenDataClient;

    private IHomeDataClient homeDataClient;
    private IDataChangeEventClient dataChangeEventClient;

    /**
     * 事件详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "eventInfoId")
    @ApiLog("查详情")
    public R<EventInfoVO> detail(@ApiParam(value = "主键", required = true) @RequestParam String eventInfoId, @RequestHeader(CommonConstant.COORDS_HEADER_NAME) Integer coordsType, BladeUser user) throws IOException {
        EventInfoVO eventInfoVO = eventInfoService.getComplexEventDetail(eventInfoId, coordsType, user);
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("eventInfoId").is(Long.valueOf(eventInfoId)));

        List<EventInfoMongoDto> mongoDtos = mongoTemplate.find(query, EventInfoMongoDto.class, EventConstant.EVENT_MONGO_NAME + AuthUtil.getTenantId());
        if (mongoDtos.size() > 0) { // 理论上是一条数据
            eventInfoVO.setCcPeopleVOS(mongoDtos.get(0).getCcPeopleVOS());
        }
        return R.data(eventInfoVO);
    }

    /**
     * 分页 事件基本信息表
     */
    @PostMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入eventInfo")
    @ApiLog("分页查询")
    public R<IPage<EventInfoVO>> list(@Valid @RequestBody EventInfoVO eventInfo, Query query, BladeUser user, @RequestHeader(CommonConstant.COORDS_HEADER_NAME) Integer coordsType) throws IOException {
        PersonUserRel personUserRel = personUserRelClient.getRelByUserId(user.getUserId()).getData();
        Long personId = 0L;
        if (personUserRel != null && personUserRel.getId() != null) {
            personId = personUserRel.getPersonId();
        }
        IPage<EventInfoVO> pages = eventInfoService.selectEventInfList(eventInfo, query, user, personId);
        if (CollectionUtil.isNotEmpty(pages.getRecords())) {
            List<Coords> coordsList = new ArrayList<>();
            pages.getRecords().forEach(record -> {
                Coords coords = new Coords();
                coords.setLongitude(record.getLongitude());
                coords.setLatitude(record.getLatitudinal());
                coordsList.add(coords);
            });

            if (coordsType != null && !BaiduMapUtils.CoordsSystem.GC02.equals(coordsType) && eventInfo.getLatitudinal() != null && eventInfo.getLongitude() != null) {
                List<Coords> resultList = baiduMapUtils.coordsToBaiduMapllAll(BaiduMapUtils.CoordsSystem.GC02, coordsList);
                for (int i = 0; i < resultList.size(); i++) {
                    Coords coords = resultList.get(i);
                    EventInfoVO eventInfoVO = pages.getRecords().get(i);
                    eventInfoVO.setLongitude(coords.getLongitude());
                    eventInfoVO.setLatitudinal(coords.getLatitude());
                }
            }
        }


        return R.data(pages);
    }


    /**
     * 自定义分页 事件基本信息表
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "分页", notes = "传入eventInfo")
    @ApiLog("默认分页查询")
    public R<IPage<EventInfoVO>> page(EventInfoVO eventInfo, Query query) {
        IPage<EventInfoVO> pages = eventInfoService.selectEventInfoPage(Condition.getPage(query), eventInfo);
        return R.data(pages);
    }

    /**
     * 新增 事件基本信息表
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入eventInfo")
    @ApiLog("默认保存方法")
    public R save(@Valid @RequestBody EventInfo eventInfo) {
        return R.status(eventInfoService.save(eventInfo));
    }


    /**
     * 新增事件基本信息和媒介信息
     */
    @PostMapping("/saveEventinfo")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增事件基本信息和媒介信息", notes = "传入eventInfoVO")
    @ApiLog("默认保存方法")
    public R saveEventInfo(@Valid @RequestBody EventAllInfoVO eventInfoVO, @RequestHeader(CommonConstant.COORDS_HEADER_NAME) Integer coordsType, BladeUser user) {
        // 获取租户管理员
        Tenant tenant = TenantCache.getTenantById(user.getTenantId());
        eventInfoVO.getEventInfo().setTenantId(user.getTenantId());
        PersonUserRel personUserRel = personUserRelClient.getRelByUserId(userClient.userByAcct(tenant.getAdminAccount()).getData().getId()).getData();
        Long personId = 0L;
        if (personUserRel != null && personUserRel.getId() != null) {
            personId = personUserRel.getPersonId();
        }
        Boolean flag = eventInfoService.saveEventInfo(eventInfoVO, coordsType, personId);

        if (flag) {
            //首页统计数据redis更新
            homeDataClient.updateHomeCountRedis(AuthUtil.getTenantId());
            bigScreenDataClient.updateBigscreenCountRedis(AuthUtil.getTenantId());
            bigScreenDataClient.updateBigscreenEventCountByTypeRedis(AuthUtil.getTenantId());
            //首页告警列表redis数据更新
            if (EventConstant.Event_LEVEL.LEVEL_1.equals(eventInfoVO.getEventInfo().getEventLevel())) {
                homeDataClient.updateHomeEventListRedis(AuthUtil.getTenantId());
            }
        }

        try {
            EventInfoMongoDto mongoDto = new EventInfoMongoDto();
            BeanUtil.copyProperties(eventInfoVO.getEventInfo(), mongoDto);
            mongoDto.setEventInfoId(eventInfoVO.getEventInfo().getId());
            mongoDto.setCcPeopleVOS(eventInfoVO.getCcPeopleVOS());
            mongoTemplate.save(mongoDto, EventConstant.EVENT_MONGO_NAME + AuthUtil.getUser().getTenantId());
            //Boolean isEmergency = EventConstant.Event_LEVEL.LEVEL_1.equals(eventInfoVO.getEventInfo().getEventLevel())?true:false;
            //trackClient.trackAddEvent(eventInfoVO.getEventInfo().getId(),AuthUtil.getTenantId(),isEmergency);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return R.status(flag);
    }

    /**
     * 修改事件基本信息和媒介信息
     */
    @PostMapping("/updateEventinfo")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "修改事件基本信息和媒介信息", notes = "传入eventInfo,eventMediumList")
    @ApiLog("更新事件信息")
    public R updateEventInfo(@Valid @RequestBody EventAllInfoVO eventInfoVO, @RequestHeader(CommonConstant.COORDS_HEADER_NAME) Integer coordsType) {
        eventInfoService.updateEventInfo(eventInfoVO, coordsType);
        //更新mongo数据
        EventInfoMongoDto mongoDto = new EventInfoMongoDto();
        BeanUtil.copyProperties(eventInfoVO.getEventInfo(), mongoDto);
        mongoDto.setEventInfoId(eventInfoVO.getEventInfo().getId());
        mongoDto.setCcPeopleVOS(eventInfoVO.getCcPeopleVOS());

        eventInfoService.updateEventInfoMongoDate(mongoDto);

        return R.status(Boolean.TRUE);
    }

    /**
     * 更新事件狀態
     */
    @PostMapping("/updateEventInfoStatus")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "更新事件狀態", notes = "eventInfo")
    @ApiLog("更新事件狀態")
    public R updateEventInfoStatus(EventInfo eventInfo) {
        eventInfoService.updateEventInfoStatus(eventInfo);
        //更新mongo数据
        EventInfoMongoDto mongoDto = new EventInfoMongoDto();
        BeanUtil.copyProperties(eventInfo, mongoDto);
        mongoDto.setEventInfoId(eventInfo.getId());
        eventInfoService.updateEventInfoMongoDate(mongoDto);
        return R.status(Boolean.TRUE);
    }

    /**
     * 刪除事件基本信息和媒介信息
     */
    @PostMapping("/removeEventInfo")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除事件信息", notes = "传入ids")
    @ApiLog("逻辑删除事件信息")
    public R removeEventInfo(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        eventInfoService.removeEventInfo(Func.toLongList(ids));

        try {
            org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
            query.addCriteria(Criteria.where("eventInfoId").is(ids));
            long count = mongoTemplate.remove(query, EventConstant.EVENT_MONGO_NAME + AuthUtil.getUser().getTenantId()).getDeletedCount();
            log.debug("delete count={},id={}", count, ids);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return R.status(Boolean.TRUE);
    }

    /**
     * 修改 事件基本信息表
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入eventInfo")
    @ApiLog("修改事件基本信息")
    public R update(@Valid @RequestBody EventInfo eventInfo) {
        return R.status(eventInfoService.updateById(eventInfo));
    }

    /**
     * 新增或修改 事件基本信息表
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入eventInfo")
    @ApiLog("默认submit方法")
    public R submit(@Valid @RequestBody EventInfo eventInfo) {
        return R.status(eventInfoService.saveOrUpdate(eventInfo));
    }


    /**
     * 删除 事件基本信息表
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    @ApiLog("默认删除方法")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(eventInfoService.deleteLogic(Func.toLongList(ids)));
    }


    @PostMapping("/countEventGroupByType")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "最近N天各种类型事件个数", notes = "传入天数")
    @ApiLog("查询最近N天各种类型事件个数")
    public R<List<EventTypeCountVO>> countEventGroupByType(@ApiParam(value = "天数", required = true) @RequestParam Integer days) {
        List<EventTypeCountVO> eventTypeCountVOList = eventInfoService.countEventGroupByType(days, AuthUtil.getTenantId());
        eventTypeCountVOList.forEach(eventTypeCountVO -> {
            eventTypeCountVO.setEventTypeName(DictCache.getValue("event_type", eventTypeCountVO.getEventType()));
        });
        return R.data(eventTypeCountVOList);
    }

    @PostMapping("/testEventInfo")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "测试mongo中事件信息")
    @ApiLog("测试mongo中事件信息")
    public R<GreenScreenEventsDTO> testEventInfo() {

        return R.data(eventInfoService.queryEventInfos(AuthUtil.getUser().getTenantId()));
    }

    @PostMapping("/lastEventsDaily")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "今日最新N条紧急事件", notes = "传入条数")
    @ApiLog("查询今日最新N条紧急事件")
    public R<List<HomeEventVO>> lastEvents(@ApiParam(value = "条数", required = true) @RequestParam Integer num) {
        List<HomeEventVO> eventVOList = new ArrayList<HomeEventVO>();
        Date startTime = DateUtil.beginOfDay(Calendar.getInstance().getTime());
        Date endTime = DateTime.now();
        EventQueryDTO eventQueryDTO = new EventQueryDTO();
        eventQueryDTO.setStartTime(startTime.getTime());
        eventQueryDTO.setEndTime(endTime.getTime());
        eventQueryDTO.setStatus(EventConstant.Event_Status.HANDLE_1);
        eventQueryDTO.setEventNum(num);
        eventQueryDTO.setEventLevel(EventConstant.Event_LEVEL.LEVEL_1);
        eventQueryDTO.setTenantId(AuthUtil.getTenantId());
        List<EventInfoVO> eventInfoVOList = eventInfoService.listEventInfoByParam(eventQueryDTO);
        if (ObjectUtil.isNotEmpty(eventInfoVOList) && eventInfoVOList.size() > 0) {
            eventInfoVOList.forEach(eventInfoVO_ -> {
                HomeEventVO eventVO = new HomeEventVO();
                eventVO.setId(eventInfoVO_.getId().toString());
                eventVO.setEventType(eventInfoVO_.getEventType());
                eventVO.setEventMessage(eventInfoVO_.getEventDesc());
                eventVO.setEventDate(TimeUtil.formDateToTimestamp(eventInfoVO_.getCreateTime()));
                eventVOList.add(eventVO);
            });
        }
        return R.data(eventVOList);
    }

    /**
     * 根据地址查询地址所在片区信息
     */
    @PostMapping("/getRegionByAddress")
    @ApiOperationSupport(order = 20)
    @ApiOperation(value = "根据地址查询地址所在片区信息", notes = "传入eventInfo")
    @ApiLog("根据地址查询地址所在片区信息")
    public R<Region> getRegionByAddress(@Valid @RequestBody EventInfo eventInfo) {
        Region region = eventInfoService.getRegionByAddress(eventInfo.getLatitudinal(), eventInfo.getLongitude(), AuthUtil.getTenantId());
        return R.data(region);
    }

}
