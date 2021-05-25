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
package com.ai.apac.smartenv.event.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.event.cache.EventCache;
import com.ai.apac.smartenv.event.dto.EventQueryDTO;
import com.ai.apac.smartenv.event.dto.mongo.EventInfoMongoDto;
import com.ai.apac.smartenv.event.dto.mongo.GreenScreenEventDTO;
import com.ai.apac.smartenv.event.dto.mongo.GreenScreenEventsDTO;
import com.ai.apac.smartenv.event.entity.*;
import com.ai.apac.smartenv.event.service.*;
import com.ai.apac.smartenv.event.vo.*;
import com.ai.apac.smartenv.event.mapper.EventInfoMapper;
import com.ai.apac.smartenv.event.wrapper.EventAssignedHistoryWrapper;
import com.ai.apac.smartenv.event.wrapper.EventInfoWrapper;
import com.ai.apac.smartenv.event.wrapper.EventMediumWrapper;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.feign.IPersonUserRelClient;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.pushc.dto.AssessEventDTO;
import com.ai.apac.smartenv.pushc.dto.EventInfoDTO;
import com.ai.apac.smartenv.pushc.feign.IPushcClient;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.entity.Role;
import com.ai.apac.smartenv.system.feign.IRegionClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.user.dto.RelMessageDTO;
import com.ai.apac.smartenv.system.user.dto.UserMessageDTO;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.ai.apac.smartenv.websocket.feign.IBigScreenDataClient;
import com.ai.apac.smartenv.websocket.feign.IHomeDataClient;
import com.ai.apac.smartenv.websocket.feign.INotificationClient;
import com.ai.apac.smartenv.websocket.module.notification.dto.NotificationInfo;
import com.ai.apac.smartenv.wechat.entity.WeChatUser;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaNodeClient;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 事件基本信息表 服务实现类
 *
 * @author Blade
 * @since 2020-02-06
 */
@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
public class EventInfoServiceImpl extends BaseServiceImpl<EventInfoMapper, EventInfo> implements IEventInfoService {
    private static Logger logger = LoggerFactory.getLogger(EventInfoServiceImpl.class);

    @Autowired
    private IPersonClient personClient;
    @Autowired
    private IEventMediumService eventMediumService;
    @Autowired
    private IWorkareaClient workareaClient;
    @Autowired
    private IEventAssignedHistoryService historyService;
    @Autowired
    private IUserClient userClient;
    @Autowired
    private ISysClient sysClient;
    @Autowired
    private IOssClient ossClient;
    @Autowired
    private BaiduMapUtils baiduMapUtils;
    @Autowired
    private IWorkareaNodeClient workareaNodeClient;
    @Autowired
    private CoordsTypeConvertUtil coordsTypeConvertUtil;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private INotificationClient notificationClient;
    @Autowired
    private IBigScreenDataClient bigScreenDataClient;
    @Autowired
    private IHomeDataClient homeDataClient;
    @Autowired
    private IRegionClient regionClient;
    @Autowired
    private IPersonUserRelClient personUserRelClient;
    @Autowired
    private IPushcClient pushcClient;
    @Autowired
    private IEventInfoKpiRelService eventInfoKpiRelService;
    @Autowired
    private IDataChangeEventClient dataChangeEventClient;
    @Lazy
    @Autowired
    private IPublicEventInfoService publicEventInfoService;


    @Override
    public IPage<EventInfoVO> selectEventInfoPage(IPage<EventInfoVO> page, EventInfoVO eventInfo) {
        List<EventInfoVO> eventInfoVOList = baseMapper.selectEventInfoPage(page, eventInfo);
        return page.setRecords(eventInfoVOList);
    }

    private Boolean checkRequestParam(EventAllInfoVO eventInfoVO) {
        Boolean result = false;
        if (eventInfoVO != null && eventInfoVO.getEventInfo() != null
//                && StringUtil.isNotBlank(eventInfoVO.getEventInfo().getEventType())
//                && StringUtil.isNotBlank(eventInfoVO.getEventInfo().getEventLevel())
                && eventInfoVO.getEventInfo().getReportPersonId() != null && eventInfoVO.getEventInfo().getReportPersonId() != 0L
                && StringUtil.isNotBlank(eventInfoVO.getEventInfo().getEventDesc())
//		&& !eventInfoVO.getEventInfo().getEventInspectType().isEmpty()
//		&& !eventInfoVO.getEventInfo().getHandleAdvice().isEmpty()
        ) {
            result = true;
        }
        return result;
    }

    @Override
    public boolean saveEventInfo(EventAllInfoVO eventInfoVO, Integer coordsType, Long personId) throws ServiceException {
        boolean status = false;
        // 校验必填字段
        if (checkRequestParam(eventInfoVO)) {

            String personNames = "";
            if (StringUtils.isBlank(eventInfoVO.getEventInfo().getHandlePersonId())) {  // 没有片区主管则指派给租户管理员
                eventInfoVO.getEventInfo().setExt1(String.valueOf(personId)); // 扩展字段作为租户管理员对应的人员
            } else {
                String[] ids = eventInfoVO.getEventInfo().getHandlePersonId().split(",");
                if (ids.length > 0) {
                    for (String id : ids) {  // 改造需求6-所有人员显示姓名，工号去掉
//					String personName = personClient.getPerson(Long.valueOf(id)).getData().getPersonName();
                        String personName = PersonCache.getPersonById(null, Long.valueOf(id)).getPersonName();
                        String nameAndNumber = personName;
//							+"("+ personClient.getPerson(Long.valueOf(id)).getData().getJobNumber() +")";
                        personNames = "".equals(personNames) ? nameAndNumber : (personNames + "," + nameAndNumber);
                    }
                }
            }


            eventInfoVO.getEventInfo().setHandlePersonName(personNames);
            eventInfoVO.getEventInfo().setReportPersonName(personClient.getPerson(eventInfoVO.getEventInfo().getReportPersonId()).getData().getPersonName());
//					                                       +"("+ personClient.getPerson(eventInfoVO.getEventInfo().getReportPersonId()).getData().getJobNumber() +")");
            if (eventInfoVO.getEventInfo().getBelongArea() != null) {
                Region region = sysClient.getRegion(eventInfoVO.getEventInfo().getBelongArea()).getData();
                if (region.getRegionManager() != null && StringUtils.isNotBlank(region.getRegionManager())) {
                    eventInfoVO.getEventInfo().setBelongAreaName(region.getRegionName()
                            + "(" + personClient.getPerson(Long.valueOf(region.getRegionManager())).getData().getPersonName() + ")");
                }
            }


            if (coordsType != null && !BaiduMapUtils.CoordsSystem.GC02.equals(coordsType) && eventInfoVO.getEventInfo().getLatitudinal() != null && eventInfoVO.getEventInfo().getLongitude() != null) {
                List<Coords> coordsList = new ArrayList<>();
                Coords coords = new Coords();
                coords.setLatitude(eventInfoVO.getEventInfo().getLatitudinal());
                coords.setLongitude(eventInfoVO.getEventInfo().getLongitude());
                coordsList.add(coords);
                if (BaiduMapUtils.CoordsSystem.getCoordsSystem(coordsType).equals(BaiduMapUtils.CoordsSystem.BD09LL)) {
                    List<Coords> resultList = null;
                    resultList = baiduMapUtils.baiduMapllToGC02All(coordsList);
                    if (CollectionUtil.isNotEmpty(resultList)) {
                        Coords coords1 = resultList.get(0);
                        eventInfoVO.getEventInfo().setLongitude(coords1.getLongitude());
                        eventInfoVO.getEventInfo().setLatitudinal(coords1.getLatitude());
                    }
                }
            }

            this.save(eventInfoVO.getEventInfo());
            List<CcPeopleVO> ccPeopleVOS = eventInfoVO.getCcPeopleVOS();
            List<Long> personIdList = new ArrayList<>();
            if (ccPeopleVOS != null && ccPeopleVOS.size() > 0) {
                for (CcPeopleVO ccPeopleVO : ccPeopleVOS) {
                    personIdList.add(ccPeopleVO.getPersonId());
                }
            }

            EventInfo eventInfo = eventInfoVO.getEventInfo();

            if (eventInfoVO.getEventMediumList() != null && eventInfoVO.getEventMediumList().size() > 0) {
                for (EventMedium eventMedium : eventInfoVO.getEventMediumList()) {
                    eventMedium.setEventInfoId(eventInfoVO.getEventInfo().getId());
                    eventMedium.setMediumDetailType(EventConstant.MediumDetailType.PRE_CHECK);// 整改前
                    eventMediumService.save(eventMedium);
                }
            }
            //保存历史指派记录
            EventAssignedHistory history = new EventAssignedHistory();
            if (StringUtils.isNotBlank(eventInfo.getHandlePersonId())) {
                history.setAssignedPersonId(eventInfo.getHandlePersonId());
                history.setAssignedPersonName(eventInfo.getHandlePersonName());
            } else {
                if (StringUtils.isNotBlank(eventInfo.getExt1())) {
                    history.setAssignedPersonId(eventInfo.getExt1());
                    history.setAssignedPersonName(PersonCache.getPersonById(null, Long.valueOf(eventInfo.getExt1())).getPersonName());
                }

            }


            history.setEventInfoId(eventInfo.getId());
//			history.setHandleAdvice(eventInfoVO.getEventInfo().getHandleAdvice());
            history.setType(EventConstant.Type.ASSIGN); //1-指派，2-检查
            historyService.save(history);

            List<EventInfoKpiRel> eventInfoKpiRelList = eventInfoVO.getEventInfoKpiRelList();

            if (CollectionUtil.isNotEmpty(eventInfoKpiRelList)) {
                eventInfoKpiRelList.forEach(eventInfoKpiRel -> {
                    eventInfoKpiRel.setEventInfoId(eventInfoVO.getEventInfo().getId());
                    eventInfoKpiRel.setTenantId(eventInfoVO.getEventInfo().getTenantId());
                });
                eventInfoKpiRelService.saveBatch(eventInfoKpiRelList);
            }
            //生产插入不进去

            List<String> userIdList = new ArrayList<String>();
            if (StringUtils.isNotBlank(eventInfoVO.getEventInfo().getHandlePersonId())) {
                PersonUserRel handlePersonUserRel = PersonUserRelCache.getRelByPersonId(Long.valueOf(eventInfoVO.getEventInfo().getHandlePersonId()));
                if (handlePersonUserRel != null && handlePersonUserRel.getUserId() != null) {
                    userIdList.add(handlePersonUserRel.getUserId().toString());
                }
                personIdList.add(Long.valueOf(eventInfoVO.getEventInfo().getHandlePersonId()));
            }
            if (eventInfoVO.getEventInfo().getReportPersonId() != null) {
                PersonUserRel reportPersonUserRel = PersonUserRelCache.getRelByPersonId(Long.valueOf(eventInfoVO.getEventInfo().getReportPersonId()));
                if (reportPersonUserRel != null && reportPersonUserRel.getUserId() != null) {
                    userIdList.add(reportPersonUserRel.getUserId().toString());
                }
                personIdList.add(Long.valueOf(eventInfoVO.getEventInfo().getReportPersonId()));
            }
            if (StringUtils.isNotBlank(eventInfoVO.getEventInfo().getExt1())) {//带上租户管理员
                PersonUserRel adminUserRel = PersonUserRelCache.getRelByPersonId(Long.valueOf(eventInfoVO.getEventInfo().getExt1()));
                if (adminUserRel != null && adminUserRel.getUserId() != null) {
                    userIdList.add(adminUserRel.getUserId().toString());
                }
                personIdList.add(Long.valueOf(eventInfoVO.getEventInfo().getExt1()));
            }
            String userIds = ArrayUtil.toString(userIdList).replace("[", "").replaceAll("]", "");

            status = true;
            String tenantId = AuthUtil.getTenantId();

            if (status) {
                //把事件message保存mongo
                eventMessage2Mongo(eventInfo);

                ThreadUtil.execute(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 发送页面通知
                        sendNotice(eventInfo, userIds);
                    }
                }));

                ThreadUtil.execute(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //发送微信公众号消息
                        sendWechatMessage(personIdList, eventInfo);
                    }
                }));

                ThreadUtil.execute(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //发送数据库变更通知消息
                        BaseDbEventDTO dbeventDto = new BaseDbEventDTO();
                        dbeventDto.setTenantId(tenantId);
                        dbeventDto.setEventType(DbEventConstant.EventType.INSPECT_EVENT);
                        dbeventDto.setEventObject(eventInfo.getId());
                        dataChangeEventClient.doDbEvent(dbeventDto);
                    }
                }));
            }

//			if(status){
//				//首页统计数据redis更新
//				homeDataClient.updateHomeCountRedis(AuthUtil.getTenantId());
//				bigScreenDataClient.updateBigscreenCountRedis(AuthUtil.getTenantId());
//				bigScreenDataClient.updateBigscreenEventCountByTypeRedis(AuthUtil.getTenantId());
//				//首页告警列表redis数据更新
//				if(EventConstant.Event_LEVEL.LEVEL_1.equals(eventInfo.getEventLevel())){
//					homeDataClient.updateHomeEventListRedis(AuthUtil.getTenantId());
//				}
//			}
        }
        return status;
    }

    /**
     * 发送页面通知
     *
     * @param eventInfo
     * @param userIds
     */
    @Override
    public void sendNotice(EventInfo eventInfo, String userIds) {
        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.setTitle(DictCache.getValue("event_type", eventInfo.getEventType()));
        notificationInfo.setContent(eventInfo.getEventDesc());
        notificationInfo.setCategory(WebSocketConsts.NotificationCategory.EVENT);
        notificationInfo.setId(String.valueOf(eventInfo.getId()));
        notificationInfo.setPath(WebSocketConsts.NotificationPath.EVENT_INFO);
        notificationInfo.setPathType(WebSocketConsts.NotificationPathType.INNER_LINK);
        notificationInfo.setLevel(WebSocketConsts.NotificationLevel.INFO);
        notificationInfo.setTenantId(eventInfo.getTenantId());
        notificationInfo.setUserId(userIds);
        notificationInfo.setBroadCast(true);
        notificationClient.pushNotification(notificationInfo);
    }

    /**
     * 发送微信公众号消息
     *
     * @param personIdList
     * @param eventInfo
     */
    @Override
    public void sendWechatMessage(List<Long> personIdList, EventInfo eventInfo) {
        EventInfo event=null;

        if (eventInfo!=null&&eventInfo.getId()!=null){
            event = getById(eventInfo.getId());
        }else {
            event=eventInfo;
        }

        if (CollectionUtil.isNotEmpty(personIdList)) {
            for (Long id : personIdList) {
                WeChatUser weChatUser = personClient.getWechatUserByPersonId(id).getData();
                if (weChatUser != null && StringUtils.isNotBlank(weChatUser.getMpOpenId())) {
                    AssessEventDTO assessEventDTO = new AssessEventDTO();
                    EventInfoDTO eventInfoDTO = new EventInfoDTO();
                    eventInfoDTO.setId(event.getId());
                    eventInfoDTO.setEventTitle(DictCache.getValue("event_type", event.getEventType()));
                    eventInfoDTO.setPrincipal(event.getHandlePersonName());
                    eventInfoDTO.setDeadline(DictCache.getValue("event_level", String.valueOf(event.getEventLevel())));
                    eventInfoDTO.setReporter(event.getReportPersonName());
                    eventInfoDTO.setReportTime(event.getCreateTime().toString());
                    eventInfoDTO.setAddress(event.getEventAddress());
                    eventInfoDTO.setStatusName(DictCache.getValue("handle_status", String.valueOf(event.getStatus())));
                    assessEventDTO.setMpOpenId(weChatUser.getMpOpenId());
                    assessEventDTO.setEventInfoDTO(eventInfoDTO);
                    assessEventDTO.setUnionId(weChatUser.getUnionId());
                    assessEventDTO.setMpOpenId(weChatUser.getMpOpenId());
                    pushcClient.sendAssessEventByMP(assessEventDTO);
                }
            }
        }
    }

    @Override
    public void eventMessage2Mongo(EventInfo eventInfo) {
        String tenantId = AuthUtil.getTenantId();
        Long now = TimeUtil.getSysDate().getTime();
        JSONObject message = new JSONObject();
        String messageId = MessageConstant.MessageType.EVENT_MESSAGE + "_" + eventInfo.getId() + "_" + now;
        message.put("messageId", messageId);
        message.put("messageType", MessageConstant.MessageType.EVENT_MESSAGE);
        String entityTypeName = "人员";
        message.put("messageKind", "1");
        if ("1".equals(eventInfo.getEventType()) || "2".equals(eventInfo.getEventType()) || "5".equals(eventInfo.getEventType())) {
            entityTypeName = "车辆";
            message.put("messageKind", "2");
        }

        message.put("messageTitle", StrUtil.format(entityTypeName + " - {}", eventInfo.getEventDesc()));
        if (StringUtils.isBlank(eventInfo.getExt1())) {

        } else {
            message.put("messageContent", StrUtil.format("{},负责人:{}", eventInfo.getEventDesc(), StringUtils.isBlank(eventInfo.getHandlePersonName()) ? personClient.getPerson(Long.valueOf(eventInfo.getExt1())).getData().getPersonName() : eventInfo.getHandlePersonName()));
        }

        message.put("messageData", eventInfo);
        mongoTemplate.save(message, "messageInfo_" + tenantId);


        //责任人
        List<String> userIds = new ArrayList<String>();
        if (StringUtils.isNotBlank(eventInfo.getHandlePersonId())) {
            Long userId_ = PersonUserRelCache.getRelByPersonId(Long.valueOf(eventInfo.getHandlePersonId())).getUserId();
            userIds.add(String.valueOf(userId_));
        }

        //上报人
        Long reportPersionUserId = PersonUserRelCache.getRelByPersonId(eventInfo.getReportPersonId()).getUserId();
        userIds.add(String.valueOf(reportPersionUserId));
        //系统管理员
        Role adminRole = sysClient.getTenantAdminRole(tenantId).getData();
        if (ObjectUtil.isNotEmpty(adminRole) && ObjectUtil.isNotEmpty(adminRole.getId())) {
            List<User> userList = userClient.getRoleUser(adminRole.getId().toString(), tenantId).getData();
            userList.forEach(user -> {
                userIds.add(user.getId().toString());
            });
        }
        //区域主管
        if (eventInfo.getBelongArea() != null && eventInfo.getBelongArea() > 0) {
            String areaHead = sysClient.getRegion(eventInfo.getBelongArea()).getData().getRegionManager();
            if (StringUtils.isNotBlank(areaHead)) {
                Long areaHeadUserId = PersonUserRelCache.getRelByPersonId(Long.valueOf(areaHead)).getUserId();
                if (ObjectUtil.isNotEmpty(eventInfo) && areaHeadUserId != null) {
                    userIds.add(String.valueOf(areaHeadUserId));
                }
            }
        }

        userIds.forEach(userId -> {
            org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
            query.addCriteria(Criteria.where("userId").is(userId));
            UserMessageDTO userMessageDTO = mongoTemplate.findOne(query, UserMessageDTO.class, "userMessage_" + tenantId);
            RelMessageDTO relMessageDTO = new RelMessageDTO();
            relMessageDTO.setMessageId(messageId);
            relMessageDTO.setMessageType(MessageConstant.MessageType.EVENT_MESSAGE);
            relMessageDTO.setRead(false);
            relMessageDTO.setIsDeleted("0");
            relMessageDTO.setReadChannel("");
            relMessageDTO.setPushTime(now);
            List<RelMessageDTO> relMessageDTOList = new ArrayList<RelMessageDTO>();
            if (ObjectUtil.isNotEmpty(userMessageDTO)) {
                if (userMessageDTO.getEventMessageList().size() > 0) {
                    relMessageDTOList = userMessageDTO.getEventMessageList();
                }
                relMessageDTOList.add(relMessageDTO);
                Update update = new Update();
                update.set("unReadEventCount", Long.parseLong(userMessageDTO.getUnReadEventCount()) + 1 + "");
                update.set("eventCount", Long.parseLong(userMessageDTO.getEventCount()) + 1 + "");
                update.set("eventMessageList", relMessageDTOList);
                mongoTemplate.upsert(query, update, "userMessage_" + tenantId);
            } else {
                userMessageDTO = new UserMessageDTO();
                relMessageDTOList.add(relMessageDTO);
                userMessageDTO.setUserId(userId);
                userMessageDTO.setUnReadAlarmCount("0");
                userMessageDTO.setAlarmCount("0");
                userMessageDTO.setUnReadEventCount("1");
                userMessageDTO.setUnReadAnnounCount("0");
                userMessageDTO.setAnnounCount("0");
                userMessageDTO.setEventCount("1");
                userMessageDTO.setEventMessageList(relMessageDTOList);
                userMessageDTO.setAnnounMessageList(new ArrayList<RelMessageDTO>());
                userMessageDTO.setAlarmMessageList(new ArrayList<RelMessageDTO>());
                mongoTemplate.save(userMessageDTO, "userMessage_" + tenantId);
            }
        });
    }

    @Override
    public boolean updateEventInfo(EventAllInfoVO eventInfoVO, Integer coordsType) throws ServiceException {
        boolean status = false;
        if (eventInfoVO.getEventInfo() != null && eventInfoVO.getEventInfo().getId() != null && eventInfoVO.getEventInfo().getId() != 0) {
            String personNames = "";
            if (StringUtils.isNotBlank(eventInfoVO.getEventInfo().getHandlePersonId())) {
                String[] ids = eventInfoVO.getEventInfo().getHandlePersonId().split(",");
                if (ids.length > 0) {
                    for (String id : ids) {
//					String personName = personClient.getPerson(Long.valueOf(id)).getData().getPersonName();
                        String personName = PersonCache.getPersonById(null, Long.valueOf(id)).getPersonName();
                        String nameAndNumber = personName;
//							+"("+ personClient.getPerson(Long.valueOf(id)).getData().getJobNumber() +")";
                        personNames = "".equals(personNames) ? nameAndNumber : (personNames + "," + nameAndNumber);
                    }
                }
            }

            if (coordsType != null && !BaiduMapUtils.CoordsSystem.GC02.equals(coordsType) && eventInfoVO.getEventInfo().getLatitudinal() != null && eventInfoVO.getEventInfo().getLongitude() != null) {
                List<Coords> coordsList = new ArrayList<>();
                Coords coords = new Coords();
                coords.setLatitude(eventInfoVO.getEventInfo().getLatitudinal());
                coords.setLongitude(eventInfoVO.getEventInfo().getLongitude());
                coordsList.add(coords);
                if (BaiduMapUtils.CoordsSystem.getCoordsSystem(coordsType).equals(BaiduMapUtils.CoordsSystem.BD09LL)) {
                    List<Coords> resultList = null;
                    resultList = baiduMapUtils.baiduMapllToGC02All(coordsList);
                    if (CollectionUtil.isNotEmpty(resultList)) {
                        Coords coords1 = resultList.get(0);
                        eventInfoVO.getEventInfo().setLongitude(coords1.getLongitude());
                        eventInfoVO.getEventInfo().setLatitudinal(coords1.getLatitude());
                    }
                }
            }


            eventInfoVO.getEventInfo().setHandlePersonName(personNames);
            eventInfoVO.getEventInfo().setReportPersonName(personClient.getPerson(eventInfoVO.getEventInfo().getReportPersonId()).getData().getPersonName());
//					+"("+ personClient.getPerson(eventInfoVO.getEventInfo().getReportPersonId()).getData().getJobNumber() +")");
            this.updateById(eventInfoVO.getEventInfo());

            eventMessage2Mongo(eventInfoVO.getEventInfo());
            //先刪除
            eventMediumService.remove(new QueryWrapper<EventMedium>().eq("event_info_id", eventInfoVO.getEventInfo().getId()));

            if (eventInfoVO.getEventMediumList() != null && eventInfoVO.getEventMediumList().size() > 0) {
                for (EventMedium eventMedium : eventInfoVO.getEventMediumList()) {
                    //再保存
                    eventMedium.setEventInfoId(eventInfoVO.getEventInfo().getId());
                    eventMediumService.save(eventMedium);
                }
            }


            List<EventInfoKpiRel> eventInfoKpiRelList = eventInfoVO.getEventInfoKpiRelList();

            if (CollectionUtil.isNotEmpty(eventInfoKpiRelList)) {
                eventInfoKpiRelList.forEach(eventInfoKpiRel -> {
                    eventInfoKpiRel.setEventInfoId(eventInfoVO.getEventInfo().getId());
                    eventInfoKpiRel.setTenantId(eventInfoVO.getEventInfo().getTenantId());
                });
                eventInfoKpiRelService.saveBatch(eventInfoKpiRelList);
            }

            // 发送微信通知
            List<Long> personIds = new ArrayList<>();
            personIds.add(eventInfoVO.getEventInfo().getReportPersonId());
            if (StringUtils.isNotBlank(eventInfoVO.getEventInfo().getHandlePersonId())) {
                personIds.add(Long.valueOf(eventInfoVO.getEventInfo().getHandlePersonId()));
            }
            if (StringUtils.isNotBlank(eventInfoVO.getEventInfo().getExt1())) {
                personIds.add(Long.valueOf(eventInfoVO.getEventInfo().getExt1()));
            }

            sendWechatMessage(personIds, eventInfoVO.getEventInfo());


            //发送数据库变更通知消息
            BaseDbEventDTO dbeventDto = new BaseDbEventDTO();
            dbeventDto.setTenantId(AuthUtil.getTenantId());
            dbeventDto.setEventType(DbEventConstant.EventType.INSPECT_EVENT);
            dbeventDto.setEventObject(eventInfoVO.getEventInfo().getId());
            dataChangeEventClient.doDbEvent(dbeventDto);


            status = true;
        }
        return status;
    }

    @Override
    public boolean updateEventInfoStatus(EventInfo eventInfo) throws ServiceException {
        boolean status = false;
        if (eventInfo != null && eventInfo.getId() != null && eventInfo.getId() != 0) {
            this.updateById(eventInfo);

            List<EventMedium> mediumList = eventMediumService.list(new QueryWrapper<EventMedium>().eq("event_info_id", eventInfo.getId()));
            if (mediumList != null && mediumList.size() > 0) {
                for (EventMedium medium : mediumList) {
                    medium.setStatus(eventInfo.getStatus());
                    eventMediumService.updateById(medium);
                }
            }

            // 发送微信通知
            List<Long> personIds = new ArrayList<>();
            personIds.add(eventInfo.getReportPersonId());
            if (StringUtils.isNotBlank(eventInfo.getHandlePersonId())) {
                personIds.add(Long.valueOf(eventInfo.getHandlePersonId()));
            }
            if (StringUtils.isNotBlank(eventInfo.getExt1())) {
                personIds.add(Long.valueOf(eventInfo.getExt1()));
            }

            sendWechatMessage(personIds, eventInfo);


            //发送数据库变更通知消息
            BaseDbEventDTO dbeventDto = new BaseDbEventDTO();
            dbeventDto.setTenantId(AuthUtil.getTenantId());
            dbeventDto.setEventType(DbEventConstant.EventType.INSPECT_EVENT);
            dbeventDto.setEventObject(eventInfo.getId());
            dataChangeEventClient.doDbEvent(dbeventDto);


            status = true;
        }
        return status;
    }

    @Override
    public boolean removeEventInfo(List<Long> ids) throws ServiceException {
        boolean status = false;
        if (ids != null && ids.size() > 0) {
            this.deleteLogic(ids);
            for (Long id : ids) {
                //先刪除
                eventMediumService.remove(new QueryWrapper<EventMedium>().eq("event_info_id", id));

            }
            status = true;
        }


        return status;
    }

    private QueryWrapper<EventInfo> getCondition(EventInfoVO eventInfo, String tenantId) {
        QueryWrapper<EventInfo> queryWrapper = new QueryWrapper<>();
        if (eventInfo.getEventType() != null && !eventInfo.getEventType().equals("")) {
            queryWrapper.eq("event_type", eventInfo.getEventType());
        }
        if (eventInfo.getBelongArea() != null && eventInfo.getBelongArea() != 0L) {
            queryWrapper.eq("belong_area", eventInfo.getBelongArea());
        }
        if (eventInfo.getStatus() != null && eventInfo.getStatus() != 0L) {
            queryWrapper.eq("status", eventInfo.getStatus());
        }
        if (eventInfo.getEventInspectType() != null && !eventInfo.getEventInspectType().equals("")) {
            queryWrapper.eq("event_inspect_type", eventInfo.getEventInspectType());
        }
        if (eventInfo.getEventLevel() != null && !eventInfo.getEventLevel().equals("")) {
            queryWrapper.eq("event_level", eventInfo.getEventLevel());
        }
        if (eventInfo.getEventDesc() != null && !eventInfo.getEventDesc().equals("")) {
            queryWrapper.like("event_desc", eventInfo.getEventDesc());
        }
        if (eventInfo.getStartTime() != null && eventInfo.getEndTime() != null) {
            queryWrapper.between("create_time", eventInfo.getStartTime(), eventInfo.getEndTime());
        }
        if (tenantId != null) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        queryWrapper.orderByDesc("update_time");
        return queryWrapper;
    }

    @Override
    public IPage<EventInfoVO> selectEventInfList(EventInfoVO eventInfo, Query query, BladeUser user, Long personId) {
        String tenantId = user.getTenantId();
        QueryWrapper<EventInfo> queryWrapper = getCondition(eventInfo, tenantId);
        //当前事件只有片区主管或被指派的管理员能看到 -------此段逻辑又被客户自己推翻了，相当于最初的版本了
//		if("administrator".equals(user.getRoleGroup()) || "admin".equals(user.getRoleGroup())) { //超级管理员可查看全部
//
//		}else {
//			if(personId != null) { // 上报人、片区主管、租户管理员
//				queryWrapper.and(i -> i.eq("report_person_id",personId).or().eq("handle_person_id",personId).or().eq("ext1",personId));
//			}
//		}

        List<EventInfo> eventInfos = this.list(queryWrapper);


        int count = eventInfos.size();
        List<EventInfoVO> workareaInfoVOList = EventInfoWrapper.build().listVO(eventInfos);
        IPage<EventInfoVO> page = Condition.getPage(query);
        Double pages = Math.ceil((double) page.getTotal() / (double) page.getSize());
        page.setPages(pages.longValue());
        page.setTotal(count);
        int start = ((int) page.getCurrent() - 1) * (int) page.getSize();
        page.setRecords(workareaInfoVOList.subList(start, count - start > page.getSize() ? start + (int) page.getSize() : count));

        for (EventInfoVO eventInfoVO : page.getRecords()) {
            List<EventInfoKpiRel> eventInfoRelByEventId = EventCache.getEventInfoRelByEventId(eventInfoVO.getId());
            if (CollectionUtil.isEmpty(eventInfoRelByEventId)) {
                eventInfoVO.setEventKpiName("普通事件");
                continue;
            }
            List<String> collect = eventInfoRelByEventId.stream()
                    .map(eventInfoKpiRel -> EventCache.getEventKpiDefById(eventInfoKpiRel.getKpiId()))
                    .filter(eventKpiDef -> eventKpiDef != null)
                    .map(eventKpiDef -> {
                        EventKpiCatalog eventKpiCatalog = EventCache.getEventKpiCatalogById(eventKpiDef.getEventKpiCatalog());
                        while (!Integer.valueOf(EventConstant.Event_LEVEL.LEVEL_1).equals(eventKpiCatalog.getCatalogLevel())) {
                            eventKpiCatalog = EventCache.getEventKpiCatalogById(eventKpiCatalog.getParentId());
                        }
                        return eventKpiCatalog.getCatalogName();
                    })
                    .filter(eventKpiDef -> eventKpiDef != null)
                    .distinct()
                    .collect(Collectors.toList());
            eventInfoVO.setEventKpiName(Func.join(collect));
        }
        for (EventInfoVO record : page.getRecords()) {
//			record.setWorkareaName(workareaClient.getWorkInfoById(record.getWorkareaId()).getData().getAreaName());
            record.setEventTypeName(DictCache.getValue("event_type", String.valueOf(record.getEventType())));
            record.setStatusName(DictCache.getValue("handle_status", String.valueOf(record.getStatus())));
            record.setEventInspectTypeName(DictCache.getValue("check_type", String.valueOf(record.getEventInspectType())));
//            record.setEventLevelName(DictCache.getValue("event_level", String.valueOf(record.getEventLevel())));
        }
        return page;
    }

    @Override
    public List<EventInfoVO> listEventInfoByParam(EventQueryDTO eventQueryDTO) {

        QueryWrapper<EventInfo> queryWrapper = new QueryWrapper<>();
        // 时间范围
        if (Objects.nonNull(eventQueryDTO.getStartTime()) && Objects.nonNull(eventQueryDTO.getEndTime())) {
            queryWrapper.lambda().between(EventInfo::getCreateTime, new Timestamp(eventQueryDTO.getStartTime()), new Timestamp(eventQueryDTO.getEndTime()));
        }

        if (Objects.nonNull(eventQueryDTO.getStatus())) {
            queryWrapper.lambda().eq(EventInfo::getStatus, eventQueryDTO.getStatus());
        }

        if (Objects.nonNull(eventQueryDTO.getEventLevel())) {
            queryWrapper.lambda().eq(EventInfo::getEventLevel, eventQueryDTO.getEventLevel());
        }
        if (Objects.nonNull(eventQueryDTO.getEventType())) {
            queryWrapper.lambda().eq(EventInfo::getEventType, eventQueryDTO.getEventType());
        }
        if (Objects.nonNull(eventQueryDTO.getEventInspectType())) {
            queryWrapper.lambda().eq(EventInfo::getEventInspectType, eventQueryDTO.getEventInspectType());
        }
        if (Objects.nonNull(eventQueryDTO.getTenantId())) {
            queryWrapper.lambda().eq(EventInfo::getTenantId, eventQueryDTO.getTenantId());
        }
        if (Objects.nonNull(eventQueryDTO.getBelongArea())) {
            queryWrapper.lambda().eq(EventInfo::getBelongArea, eventQueryDTO.getBelongArea());
        }
        if (Objects.nonNull(eventQueryDTO.getEventNum())) {
            queryWrapper.last("limit 0 , " + eventQueryDTO.getEventNum());
        }

        if (queryWrapper.getExpression().getNormal().size() > 0) {
            List<EventInfo> eventInfoList = this.list(queryWrapper);
            return EventInfoWrapper.build().listVO(eventInfoList);
        }
        return Collections.emptyList();
    }

    @Override
    public List<EventInfoVO> listEventInfoByCondition(EventQueryDTO eventQueryDTO) {
        LambdaQueryWrapper<EventInfo> queryWrapper = new LambdaQueryWrapper<>();
        String tenantId = eventQueryDTO.getTenantId();
        if (StringUtils.isNotBlank(tenantId)) {
            queryWrapper.eq(EventInfo::getTenantId, tenantId);
        }
        if (CollectionUtil.isNotEmpty(eventQueryDTO.getEventLevels())) {
            queryWrapper.in(EventInfo::getEventLevel, eventQueryDTO.getEventLevels());
        }
        if (CollectionUtil.isNotEmpty(eventQueryDTO.getEventStatuses())) {
            queryWrapper.in(EventInfo::getStatus, eventQueryDTO.getEventStatuses());
        }
        if (Objects.nonNull(eventQueryDTO.getStartTime()) && Objects.nonNull(eventQueryDTO.getEndTime())) {
            queryWrapper.between(EventInfo::getCreateTime, new Timestamp(eventQueryDTO.getStartTime()), new Timestamp(eventQueryDTO.getEndTime()));
        }
        String eventTypeName = eventQueryDTO.getEventTypeName();
        if (StringUtils.isNotBlank(eventTypeName)) {
            List<Dict> eventTypes = DictCache.getList("event_type");
            List<String> typeIds = eventTypes.stream().filter(dict -> dict.getDictValue().contains(eventTypeName)).map(Dict::getDictKey).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(typeIds)) {
                queryWrapper.in(EventInfo::getEventType, typeIds);
            }
        }
        List<EventInfo> eventInfoList = this.list(queryWrapper);
        if (CollectionUtil.isEmpty(eventInfoList)) {
            return null;
        }
        List<EventInfoVO> eventInfoVOList = new ArrayList<>();
        eventInfoList.forEach(eventInfo -> {
            EventInfoVO simpleDetail = getSimpleDetail(eventInfo);
            eventInfoVOList.add(simpleDetail);
        });
        return eventInfoVOList;
    }

    private EventInfoVO getSimpleDetail(EventInfo eventInfo) {
        EventInfoVO eventInfoVO = EventInfoWrapper.build().entityVO(eventInfo);
        eventInfoVO.setEventTypeName(DictCache.getValue("event_type", String.valueOf(eventInfoVO.getEventType())));
        eventInfoVO.setStatusName(DictCache.getValue("handle_status", String.valueOf(eventInfoVO.getStatus())));
        eventInfoVO.setEventInspectTypeName(DictCache.getValue("check_type", String.valueOf(eventInfoVO.getEventInspectType())));
        eventInfoVO.setEventLevelName(DictCache.getValue("event_level", String.valueOf(eventInfoVO.getEventLevel())));
        Long belongArea = eventInfo.getBelongArea();
        if (belongArea != null) {
            Region region = regionClient.getRegionById(belongArea).getData();
            if (region != null) {
                eventInfoVO.setWorkAreaManageName(region.getRegionManagerName());
            }
        }
        return eventInfoVO;
    }


    @Override
    public EventInfoVO getComplexEventDetail(String eventInfoId, Integer coordsType, BladeUser user) throws IOException {
        PersonUserRel personUserRel = personUserRelClient.getRelByUserId(user.getUserId()).getData();
        Long personId = 0L;
        if (personUserRel != null && personUserRel.getId() != null) {
            personId = personUserRel.getPersonId();
        }
        List<ButtonsVO> buttonsVOS = new ArrayList<>();
        EventInfoVO detail = this.getDetail(eventInfoId);
        // 根据当前登录人设置可操作的按钮权限
        if ((StringUtils.isNotBlank(detail.getHandlePersonId()) && Long.valueOf(detail.getHandlePersonId()).equals(personId))
                || (StringUtils.isNotBlank(detail.getExt1()) && Long.valueOf(detail.getExt1()).equals(personId))) {
            // 已经有处理人 且当前登录人为片区主管
            if (StringUtils.isNotBlank(detail.getHandlePersonId()) && (StringUtils.isNotBlank(detail.getExt1()) && Long.valueOf(detail.getExt1()).equals(personId))) {
                ButtonsVO buttonsVO1 = new ButtonsVO();
                buttonsVO1.setLabel("重新指派");
                buttonsVO1.setValue(EventConstant.BUTTONS.BUTTON_1);
                buttonsVOS.add(buttonsVO1);
            } else {
                // 当前登录人为租户管理员或者片区主管
                if (detail.getStatus().equals(EventConstant.Event_Status.HANDLE_1)) { // 事件处理中
                    ButtonsVO buttonsVO1 = new ButtonsVO();
                    ButtonsVO buttonsVO2 = new ButtonsVO();
                    buttonsVO1.setLabel("重新指派");
                    buttonsVO1.setValue(EventConstant.BUTTONS.BUTTON_1);
                    buttonsVO2.setLabel("整改");
                    buttonsVO2.setValue(EventConstant.BUTTONS.BUTTON_2);
                    buttonsVOS.add(buttonsVO1);
                    buttonsVOS.add(buttonsVO2);
                } else if (detail.getStatus().equals(EventConstant.Event_Status.HANDLE_2)) { // 待检查
                    // TODO 无操作
                } else if (detail.getStatus().equals(EventConstant.Event_Status.HANDLE_3)) { // 已检查

                    // 检查结果为合格，无操作；检查结果不合格，操作：重新指派，整改
                    List<EventAssignedHistoryVO> eventAssignedHistoryVOS = detail.getAssignedHistoryVOS();
                    if (eventAssignedHistoryVOS != null && eventAssignedHistoryVOS.size() > 0) {
                        if (EventConstant.Event_Result.SUCCESS.equals(eventAssignedHistoryVOS.get(0).getCheckResult())) { //排过序，取第一条即为最后一条处理结果
                            // TODO 无操作
                        } else if (EventConstant.Event_Result.FAILED.equals(eventAssignedHistoryVOS.get(0).getCheckResult())) {
                            ButtonsVO buttonsVO1 = new ButtonsVO();
                            ButtonsVO buttonsVO2 = new ButtonsVO();
                            buttonsVO1.setLabel("重新指派");
                            buttonsVO1.setValue(EventConstant.BUTTONS.BUTTON_1);
                            buttonsVO2.setLabel("整改");
                            buttonsVO2.setValue(EventConstant.BUTTONS.BUTTON_2);
                            buttonsVOS.add(buttonsVO1);
                            buttonsVOS.add(buttonsVO2);
                        }
                    }
                }
            }
        }
        if (detail.getReportPersonId() != null && detail.getReportPersonId().equals(personId)) {
            // 当前登录人为上报人
            buttonsVOS.clear(); // 清空
            if (detail.getStatus().equals(EventConstant.Event_Status.HANDLE_1)) { // 事件处理中
                ButtonsVO buttonsVO1 = new ButtonsVO();
                ButtonsVO buttonsVO2 = new ButtonsVO();
                buttonsVO1.setLabel("重新指派");
                buttonsVO1.setValue(EventConstant.BUTTONS.BUTTON_1);
                buttonsVO2.setLabel("编辑");
                buttonsVO2.setValue(EventConstant.BUTTONS.BUTTON_3);
                buttonsVOS.add(buttonsVO1);
                buttonsVOS.add(buttonsVO2);
                if ((StringUtils.isNotBlank(detail.getHandlePersonId()) && Long.valueOf(detail.getHandlePersonId()).equals(personId))
                        || ((StringUtils.isNotBlank(detail.getExt1()) && Long.valueOf(detail.getExt1()).equals(personId)))) { // 同时也是片区主管 或 租户管理员
                    ButtonsVO buttonsVO3 = new ButtonsVO();
                    buttonsVO3.setLabel("整改");
                    buttonsVO3.setValue(EventConstant.BUTTONS.BUTTON_2);
                    buttonsVOS.add(buttonsVO3);
                }
            } else if (detail.getStatus().equals(EventConstant.Event_Status.HANDLE_2)) { // 待检查
                ButtonsVO buttonsVO1 = new ButtonsVO();
                ButtonsVO buttonsVO2 = new ButtonsVO();
                buttonsVO1.setLabel("重新指派");
                buttonsVO1.setValue(EventConstant.BUTTONS.BUTTON_1);
                buttonsVO2.setLabel("检查");
                buttonsVO2.setValue(EventConstant.BUTTONS.BUTTON_4);
                buttonsVOS.add(buttonsVO1);
                buttonsVOS.add(buttonsVO2);
            } else if (detail.getStatus().equals(EventConstant.Event_Status.HANDLE_3)) { // 已检查

                // 检查结果为合格，无操作；检查结果不合格，操作：重新指派，整改
                List<EventAssignedHistoryVO> eventAssignedHistoryVOS = detail.getAssignedHistoryVOS();
                if (eventAssignedHistoryVOS != null && eventAssignedHistoryVOS.size() > 0) {
                    if (EventConstant.Event_Result.SUCCESS.equals(eventAssignedHistoryVOS.get(0).getCheckResult())) { //排过序，取第一条即为最后一条处理结果
                        // TODO 无操作
                    } else if (EventConstant.Event_Result.FAILED.equals(eventAssignedHistoryVOS.get(0).getCheckResult())) {

                        if ((StringUtils.isNotBlank(detail.getHandlePersonId()) && Long.valueOf(detail.getHandlePersonId()).equals(personId))) { // 同时也是片区主管
                            ButtonsVO buttonsVO1 = new ButtonsVO();
                            buttonsVO1.setLabel("重新指派");
                            buttonsVO1.setValue(EventConstant.BUTTONS.BUTTON_1);
                            buttonsVOS.add(buttonsVO1);

                            ButtonsVO buttonsVO3 = new ButtonsVO();
                            buttonsVO3.setLabel("整改");
                            buttonsVO3.setValue(EventConstant.BUTTONS.BUTTON_2);
                            buttonsVOS.add(buttonsVO3);

                        }

                        ButtonsVO buttonsVO = new ButtonsVO();
                        buttonsVO.setLabel("检查");
                        buttonsVO.setValue(EventConstant.BUTTONS.BUTTON_4);
                        buttonsVOS.add(buttonsVO);
                    }
                }
            }


        }
        detail.setButtons(buttonsVOS);
        if (coordsType != null && !BaiduMapUtils.CoordsSystem.GC02.equals(coordsType) && detail.getLatitudinal() != null && detail.getLongitude() != null) {
            List<Coords> coordsList = new ArrayList<>();
            Coords coords = new Coords();
            coords.setLatitude(detail.getLatitudinal());
            coords.setLongitude(detail.getLongitude());
            coordsList.add(coords);
            List<Coords> resultList = baiduMapUtils.coordsToBaiduMapllAll(BaiduMapUtils.CoordsSystem.GC02, coordsList);
            if (CollectionUtil.isNotEmpty(resultList)) {
                Coords coords1 = resultList.get(0);
                detail.setLatitudinal(coords1.getLatitude());
                detail.setLongitude(coords1.getLongitude());
            }
        }
        return detail;
    }

    @Override
    public EventInfoVO getDetail(String eventInfoId) throws ServiceException {
        EventInfo eventInfo = this.getById(eventInfoId);
        EventInfoVO eventInfoVO = new EventInfoVO();
        if (eventInfo != null) {
            eventInfoVO = EventInfoWrapper.build().entityVO(eventInfo);
//			eventInfoVO.setWorkareaName(workareaClient.getWorkInfoById(eventInfoVO.getWorkareaId()).getData().getAreaName());
            eventInfoVO.setEventTypeName(DictCache.getValue("event_type", String.valueOf(eventInfoVO.getEventType())));
            eventInfoVO.setStatusName(DictCache.getValue("handle_status", String.valueOf(eventInfoVO.getStatus())));
            eventInfoVO.setEventInspectTypeName(DictCache.getValue("check_type", String.valueOf(eventInfoVO.getEventInspectType())));
            eventInfoVO.setEventLevelName(DictCache.getValue("event_level", String.valueOf(eventInfoVO.getEventLevel())));
            List<EventMediumVO> eventMediumVOS1 = EventMediumWrapper.build().listVO(eventMediumService.list(new QueryWrapper<EventMedium>().eq("event_info_id", eventInfoId).eq("medium_detail_type", 1)));
            eventInfoVO.setPreEventMediumList(eventMediumVOS1); //整改前照片
            List<EventMediumVO> eventMediumVOS2 = EventMediumWrapper.build().listVO(eventMediumService.list(new QueryWrapper<EventMedium>().eq("event_info_id", eventInfoId).eq("medium_detail_type", 2)));
            eventInfoVO.setAfterEventMediumList(eventMediumVOS2); // 此处先把整改的塞进去，如果下面有检查的用检查的覆盖就好了
            List<EventAssignedHistoryVO> histories = EventAssignedHistoryWrapper.build().listVO(historyService.list(new QueryWrapper<EventAssignedHistory>().eq("event_info_id", eventInfoId).orderByDesc("create_time")));
            if (histories != null && histories.size() > 0) {
                // 整改后照片,如果有检查节点，取检查节点的照片，没有检查节点则取整改上传的照片
                if (histories.get(0).getType() == 2) {
                    List<EventMediumVO> afterEventMediumList = EventMediumWrapper.build().listVO(eventMediumService.list(new QueryWrapper<EventMedium>()
                            .eq("assigned_id", histories.get(0).getId()).eq("medium_detail_type", 2)));
                    if (afterEventMediumList != null && afterEventMediumList.size() > 0 && afterEventMediumList.get(0).getId() != null) {
                        eventInfoVO.setAfterEventMediumList(afterEventMediumList);// 检查上传照片
                    } else {
                        eventInfoVO.setAfterEventMediumList(eventMediumVOS2);// 整改上传照片
                    }
                }

                for (EventAssignedHistoryVO history : histories) {
                    history.setCheckResultName(DictCache.getValue("check_result", String.valueOf(history.getCheckResult())));
                    if (history.getType() == 2) { // 检查有检查图片
                        List<EventMediumVO> checkEventMediumList = EventMediumWrapper.build().listVO(eventMediumService.list(new QueryWrapper<EventMedium>().eq("assigned_id", history.getId())));
                        history.setEventMediumVOList(checkEventMediumList);

                    }
                }

                eventInfoVO.setAssignedHistoryVOS(histories);
            }
        }
        return eventInfoVO;
    }

    @Override
    public boolean reassign(EventAssignedHistory eventAssignedHistory) throws ServiceException {
        boolean status = false;
        eventAssignedHistory.setType(1);
        if (eventAssignedHistory.getAssignedPersonName() != null && !"".equals(eventAssignedHistory.getAssignedPersonName())) {
            eventAssignedHistory.setAssignedPersonName(eventAssignedHistory.getAssignedPersonName());
        } else {
            String personNames = "";
            String[] ids = eventAssignedHistory.getAssignedPersonId().split(",");
            if (ids.length > 0) {
                for (String id : ids) {
//					String personName = personClient.getPerson(Long.valueOf(id)).getData().getPersonName();
                    String personName = PersonCache.getPersonById(null, Long.valueOf(id)).getPersonName();
                    String nameAndNumber = personName;
//							+"("+ personClient.getPerson(Long.valueOf(id)).getData().getJobNumber() +")";
                    personNames = "".equals(personNames) ? nameAndNumber : (personNames + "," + nameAndNumber);
                }
            }
            eventAssignedHistory.setAssignedPersonName(personNames);
        }
        historyService.save(eventAssignedHistory);
        EventInfo eventInfo = this.getById(eventAssignedHistory.getEventInfoId());
        if (eventInfo != null) {
            // 无处理人则说明默认挂在租户管理员下，由租户管理员指派给片区主管
            // 已有处理人则是转给其他片区主管
            eventInfo.setHandlePersonId(eventAssignedHistory.getAssignedPersonId());
            eventInfo.setHandlePersonName(eventAssignedHistory.getAssignedPersonName());
            eventInfo.setHandleAdvice(eventAssignedHistory.getHandleAdvice());
            eventInfo.setStatus(EventConstant.Event_Status.HANDLE_1);// 重新指派后状态变为1-处理中
            this.updateById(eventInfo);

            // 发送微信通知
            List<Long> personIds = new ArrayList<>();
            personIds.add(eventInfo.getReportPersonId());
            if (StringUtils.isNotBlank(eventInfo.getHandlePersonId())) {
                personIds.add(Long.valueOf(eventInfo.getHandlePersonId()));
            }
            if (StringUtils.isNotBlank(eventInfo.getExt1())) {
                personIds.add(Long.valueOf(eventInfo.getExt1()));
            }

            sendWechatMessage(personIds, eventInfo);

            status = true;
        }
        return status;
    }

    @Override
    public boolean eventCheck(EventAssignedAllVO eventAssignedHistory) throws ServiceException {
        boolean status = false;
        eventAssignedHistory.getEventAssignedHistory().setType(EventConstant.Type.CHECK);
        if (eventAssignedHistory.getEventAssignedHistory().getAssignedPersonName() != null && !"".equals(eventAssignedHistory.getEventAssignedHistory().getAssignedPersonName())) {
            eventAssignedHistory.getEventAssignedHistory().setAssignedPersonName(eventAssignedHistory.getEventAssignedHistory().getAssignedPersonName());
        } else {

            String personNames = "";
            if (StringUtils.isNotBlank(eventAssignedHistory.getEventAssignedHistory().getAssignedPersonId())) {
                String[] ids = eventAssignedHistory.getEventAssignedHistory().getAssignedPersonId().split(",");
                if (ids.length > 0) {
                    for (String id : ids) {
//					String personName = personClient.getPerson(Long.valueOf(id)).getData().getPersonName();
                        String personName = PersonCache.getPersonById(null, Long.valueOf(id)).getPersonName();
                        String nameAndNumber = personName;
//							+"("+ personClient.getPerson(Long.valueOf(id)).getData().getJobNumber() +")";
                        personNames = "".equals(personNames) ? nameAndNumber : (personNames + "," + nameAndNumber);
                    }
                }
            }


            eventAssignedHistory.getEventAssignedHistory().setAssignedPersonName(personNames);
        }

        historyService.save(eventAssignedHistory.getEventAssignedHistory());
        EventInfo eventInfo = this.getById(eventAssignedHistory.getEventAssignedHistory().getEventInfoId());
        if (eventInfo != null) {
            eventInfo.setHandlePersonName(eventAssignedHistory.getEventAssignedHistory().getAssignedPersonName());
//			eventInfo.setHandlePersonId(eventAssignedHistory.getEventAssignedHistory().getAssignedPersonId()); // 处理人为片区主管，不需要更新
            eventInfo.setHandleAdvice(eventAssignedHistory.getEventAssignedHistory().getHandleAdvice());
            eventInfo.setStatus(EventConstant.Event_Status.HANDLE_3); //无论合格不合格都是已检查
            this.updateById(eventInfo);
            if (eventAssignedHistory.getEventMediumList() != null && eventAssignedHistory.getEventMediumList().size() > 0) {
                for (EventMedium eventMedium : eventAssignedHistory.getEventMediumList()) {
                    eventMedium.setAssignedId(eventAssignedHistory.getEventAssignedHistory().getId());
                    eventMedium.setMediumDetailType(EventConstant.MediumDetailType.AFTER_CHECK);// 检查后
                    eventMediumService.save(eventMedium);
                }
            }

            status = true;
            //更新mongo中数据状态

            EventInfoMongoDto mongoDto = new EventInfoMongoDto();
            BeanUtil.copyProperties(eventInfo, mongoDto);
            mongoDto.setEventInfoId(eventAssignedHistory.getEventAssignedHistory().getEventInfoId());
            mongoDto.setStatus(eventAssignedHistory.getEventAssignedHistory().getCheckResult());
            updateEventInfoMongoDate(mongoDto);

            List<EventInfoKpiRel> eventInfoKpiRelList = eventAssignedHistory.getEventInfoKpiRelList();
//            List<Long> oldKpiIdList = eventInfoKpiRelList.stream().map(EventInfoKpiRel::getKpiId).collect(Collectors.toList());

            QueryWrapper<EventInfoKpiRel> query = new QueryWrapper<>();
            query.eq("event_info_id", eventAssignedHistory.getEventAssignedHistory().getEventInfoId());
            eventInfoKpiRelService.remove(query);


            if (CollectionUtil.isNotEmpty(eventInfoKpiRelList)) {
//
//                if (CollectionUtil.isNotEmpty(kpiRels)){
//                    Map<Long, EventInfoKpiRel> eventInfoKpiRelMap = kpiRels.stream().collect(Collectors.toMap(EventInfoKpiRel::getKpiId, eventInfoKpiRel -> eventInfoKpiRel));
//                    eventInfoKpiRelList.forEach(eventInfoKpiRel -> {
//                        EventInfoKpiRel eventInfoKpiRel1 = eventInfoKpiRelMap.get(eventInfoKpiRel.getKpiId());
//                        eventInfoKpiRel.setId(eventInfoKpiRel1.getId());
//                    });
//                }

                Long eventInfoId = eventAssignedHistory.getEventAssignedHistory().getEventInfoId();
                eventInfoKpiRelList.forEach(eventInfoKpiRel -> eventInfoKpiRel.setEventInfoId(eventInfoId));
                eventInfoKpiRelService.saveBatch(eventInfoKpiRelList);
            }
            // 发送微信通知
            List<Long> personIds = new ArrayList<>();
            personIds.add(eventInfo.getReportPersonId());
            if (StringUtils.isNotBlank(eventInfo.getHandlePersonId())) {
                personIds.add(Long.valueOf(eventInfo.getHandlePersonId()));
            }
            if (StringUtils.isNotBlank(eventInfo.getExt1())) {
                personIds.add(Long.valueOf(eventInfo.getExt1()));
            }
            if (EventConstant.Event_Result.SUCCESS.equals(eventAssignedHistory.getEventAssignedHistory().getCheckResult())){
                // TODO  查询是否是公众事件，如果是公众事件，更新公众事件处理状态
                PublicEventInfo publicEntity=new PublicEventInfo();
                publicEntity.setStatus(EventConstant.PublicEventStatus.HANDLE_2);
                QueryWrapper<PublicEventInfo> publicWrapper=new QueryWrapper<>();
                publicWrapper.eq("event_id",eventInfo.getId());
                publicEventInfoService.update(publicEntity,publicWrapper);

            }
            sendWechatMessage(personIds, eventInfo);

            //发送数据库变更通知消息
            BaseDbEventDTO dbeventDto = new BaseDbEventDTO();
            dbeventDto.setTenantId(AuthUtil.getTenantId());
            dbeventDto.setEventType(DbEventConstant.EventType.INSPECT_EVENT);
            dbeventDto.setEventObject(eventInfo.getId());
            dataChangeEventClient.doDbEvent(dbeventDto);


        }
        return status;
    }

    /**
     * 整改
     *
     * @param eventAssignedHistory
     * @return
     * @throws ServiceException
     */
    @Override
    public boolean rectification(EventAssignedAllVO eventAssignedHistory) {
        boolean status = false;
        EventInfo eventInfo = this.getById(eventAssignedHistory.getEventAssignedHistory().getEventInfoId());
        if (eventInfo != null) {
            if (eventAssignedHistory.getEventMediumList() != null && eventAssignedHistory.getEventMediumList().size() > 0) {
                for (EventMedium eventMedium : eventAssignedHistory.getEventMediumList()) {
                    eventMedium.setEventInfoId(eventAssignedHistory.getEventAssignedHistory().getEventInfoId());
                    eventMedium.setMediumDetailType(EventConstant.MediumDetailType.AFTER_CHECK);// 整改后
                    eventMediumService.save(eventMedium);
                }
            }
            eventInfo.setStatus(EventConstant.Event_Status.HANDLE_2);
            this.updateById(eventInfo);

            // 发送微信通知
            List<Long> personIds = new ArrayList<>();
            personIds.add(eventInfo.getReportPersonId());
            if (StringUtils.isNotBlank(eventInfo.getHandlePersonId())) {
                personIds.add(Long.valueOf(eventInfo.getHandlePersonId()));
            }

            if (StringUtils.isNotBlank(eventInfo.getExt1())) {
                personIds.add(Long.valueOf(eventInfo.getExt1()));
            }
            sendWechatMessage(personIds, eventInfo);

            // TODO 发送公众号消息，更新公众事件状态
            //发送数据库变更通知消息
            BaseDbEventDTO dbeventDto = new BaseDbEventDTO();
            dbeventDto.setTenantId(AuthUtil.getTenantId());
            dbeventDto.setEventType(DbEventConstant.EventType.INSPECT_EVENT);
            dbeventDto.setEventObject(eventInfo.getId());
            dataChangeEventClient.doDbEvent(dbeventDto);


            status = true;
        }
        return status;
    }

    @Override
    public Integer countEventDaily(EventInfo eventInfo) throws ServiceException {
        QueryWrapper<EventInfo> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(eventInfo.getTenantId())) {
            queryWrapper.lambda().eq(EventInfo::getTenantId, eventInfo.getTenantId());
        }
        if (ObjectUtil.isNotEmpty(eventInfo.getBelongArea())) { // 按所属片区
            queryWrapper.lambda().eq(EventInfo::getBelongArea, eventInfo.getBelongArea());
        }
        queryWrapper.lambda().ge(EventInfo::getCreateTime, TimeUtil.getStartTime(new Date()));
        return this.count(queryWrapper);
    }

    @Override
    public List<EventTypeCountVO> countEventGroupByType(Integer days, String tenantId) throws ServiceException {
        Timestamp createTime = TimeUtil.getStartTime(TimeUtil.addOrMinusDays(TimeUtil.getSysDate().getTime(), -days + 1));
        return baseMapper.countEventGroupByType(createTime, tenantId);
    }

    @Override
    public GreenScreenEventsDTO queryEventInfos(String tenantId) {

        GreenScreenEventsDTO greenScreenEventsDTO = new GreenScreenEventsDTO();
        int days = 7;
        try {
            String daystr = DictCache.getValue("query_last_event_days", "0");
            if (null != daystr) {
                days = Integer.valueOf(daystr);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        List<String> eventTypeIdList = new ArrayList<>();
        try {
            List<Dict> dicts = DictCache.getList("query_last_events");
            if (null != dicts) {
                dicts.forEach(dict -> {
                    eventTypeIdList.add(dict.getDictValue());
                });
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        greenScreenEventsDTO.setTenantId(tenantId);
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        Date times = TimeUtil.addOrMinusDays(TimeUtil.getSysDate().getTime(), -days);
        query.addCriteria(Criteria.where("createTime").gte(times));
        if (null != eventTypeIdList && eventTypeIdList.size() > 0) {
            query.addCriteria(Criteria.where("eventType").in(eventTypeIdList));
        }
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        List<EventInfoMongoDto> mongoDtos = mongoTemplate.find(query, EventInfoMongoDto.class, EventConstant.EVENT_MONGO_NAME + tenantId);
        if (null != mongoDtos && mongoDtos.size() > 0) {
            List<GreenScreenEventDTO> mongoDtoList = new ArrayList<>();
            for (EventInfoMongoDto mongoDto : mongoDtos) {
                GreenScreenEventDTO greenScreenEventDTO = new GreenScreenEventDTO();
                greenScreenEventDTO.setEventId(mongoDto.getEventInfoId().toString());
                greenScreenEventDTO.setEventName(mongoDto.getEventDesc());
                greenScreenEventDTO.setEventStatus(DictCache.getValue("handle_status", String.valueOf(mongoDto.getStatus())));
                greenScreenEventDTO.setEventTime(TimeUtil.getDateString(mongoDto.getCreateTime()));
                mongoDtoList.add(greenScreenEventDTO);
            }
            greenScreenEventsDTO.setLastDaysEvents(mongoDtoList);
        }
        return greenScreenEventsDTO;
    }

    @Override
    public void updateEventInfoMongoDate(EventInfoMongoDto eventInfoVO) {
        try {
            org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
            query.addCriteria(Criteria.where("eventInfoId").is(eventInfoVO.getEventInfoId()));
            Update update = new Update();
            if (StringUtil.isNotBlank(eventInfoVO.getEventDesc())) {
                update.set("eventDesc", eventInfoVO.getEventDesc());
            }
            if (null != eventInfoVO.getStatus()) {
                update.set("status", eventInfoVO.getStatus());
            }
            if (null != eventInfoVO.getCcPeopleVOS()) {
                update.set("ccPeopleVOS", eventInfoVO.getCcPeopleVOS());
            }

            long result = mongoTemplate.updateFirst(query, update, EventConstant.EVENT_MONGO_NAME + AuthUtil.getUser().getTenantId()).getMatchedCount();
            log.debug("update count={},id={}", result, eventInfoVO.getEventInfoId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Region getRegionByAddress(String lat, String lng, String tenantId) {
//		Long regionId = 1265093908719734786L;
//		Region region = sysClient.getRegion(regionId).getData();
        Region targetRegion = null;
        Point2D.Double point = new Point2D.Double();
        point.x = Double.parseDouble(lng);
        point.y = Double.parseDouble(lat);
        //查询所有业务区域
        List<Region> regionList = sysClient.getRegionByType(SystemConstant.RegionType.TYPE_2, tenantId).getData();
        if (regionList != null && regionList.size() > 0) {
            for (Region region : regionList) {
                R<List<WorkareaNode>> listR = workareaNodeClient.queryRegionNodesList(region.getId());
                if (listR.getData() != null && listR.getData().size() > 0) {
                    coordsTypeConvertUtil.toWebConvert(listR.getData());
                    List<Point2D.Double> areapoints = new ArrayList<>();
                    for (WorkareaNode datum : listR.getData()) {
                        Point2D.Double node = new Point2D.Double();
                        node.x = Double.parseDouble(datum.getLongitude());
                        node.y = Double.parseDouble(datum.getLatitudinal());
                        areapoints.add(node);
                    }
                    boolean result = baiduMapUtils.pointInArea(point, areapoints);
                    if (result) {
                        targetRegion = region;
                        break;
                    }
                }
            }
        }


        return targetRegion;
    }
}
