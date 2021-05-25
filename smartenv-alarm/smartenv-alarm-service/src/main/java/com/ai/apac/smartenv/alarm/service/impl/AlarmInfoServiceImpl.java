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
package com.ai.apac.smartenv.alarm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.ai.apac.smartenv.alarm.constant.AlarmHandledName;
import com.ai.apac.smartenv.alarm.constant.AlarmLevelEnum;
import com.ai.apac.smartenv.alarm.constant.InformTypeEnum;
import com.ai.apac.smartenv.alarm.constant.MinicreateADASAlarm;
import com.ai.apac.smartenv.alarm.constant.MinicreateDSMAlarm;
import com.ai.apac.smartenv.alarm.dto.AlarmInfoCountDTO;
import com.ai.apac.smartenv.alarm.dto.AlarmInfoQueryDTO;
import com.ai.apac.smartenv.alarm.dto.PersonAlarmInfoExcelModel;
import com.ai.apac.smartenv.alarm.dto.VehicleAlarmInfoExcelModel;
import com.ai.apac.smartenv.alarm.entity.AlarmInfo;
import com.ai.apac.smartenv.alarm.entity.AlarmInform;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleExt;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import com.ai.apac.smartenv.alarm.entity.MinicreateAttach;
import com.ai.apac.smartenv.alarm.mapper.AlarmInfoMapper;
import com.ai.apac.smartenv.alarm.mq.AlarmProductSource;
import com.ai.apac.smartenv.alarm.service.IAlarmInfoService;
import com.ai.apac.smartenv.alarm.service.IAlarmInformService;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleExtService;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleInfoService;
import com.ai.apac.smartenv.alarm.service.IMinicreateAttachService;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleResultVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoMongoDBVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoScreenViewVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoVO;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleExtVO;
import com.ai.apac.smartenv.alarm.vo.MinicreatAdasAlarmVO;
import com.ai.apac.smartenv.alarm.vo.MinicreateAttachVO;
import com.ai.apac.smartenv.alarm.vo.MinicreateDsmAlarmVO;
import com.ai.apac.smartenv.alarm.wrapper.AlarmInfoWrapper;
import com.ai.apac.smartenv.alarm.wrapper.AlarmRuleExtWrapper;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.dto.BaiduMapReverseGeoCodingResult;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.entity.SimRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceExtClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.device.feign.ISimClient;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.feign.IPersonVehicleRelClient;
import com.ai.apac.smartenv.pushc.dto.EmailDTO;
import com.ai.apac.smartenv.pushc.feign.IPushcClient;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.entity.Role;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.user.dto.RelMessageDTO;
import com.ai.apac.smartenv.system.user.dto.UserMessageDTO;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.websocket.feign.IBigScreenDataClient;
import com.ai.apac.smartenv.websocket.feign.IHomeDataClient;
import com.ai.apac.smartenv.websocket.feign.INotificationClient;
import com.ai.apac.smartenv.websocket.feign.IPolymerizationDataClient;
import com.ai.apac.smartenv.websocket.module.notification.dto.NotificationInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 告警基本信息表 服务实现类
 *
 * @author Blade
 * @since 2020-02-18
 */
@Slf4j
@Service
@AllArgsConstructor
public class AlarmInfoServiceImpl extends BaseServiceImpl<AlarmInfoMapper, AlarmInfo> implements IAlarmInfoService {
    private static Logger logger = LoggerFactory.getLogger(AlarmInfoServiceImpl.class);

    private final IAlarmRuleInfoService alarmRuleInfoService;

    private final IAlarmRuleExtService alarmRuleExtService;

    private final IEntityCategoryClient entityCategoryClient;

    private final IDeviceRelClient deviceRelClient;

    private final IWorkareaRelClient workareaRelClient;

    private final IWorkareaClient workareaClient;

    private final IDeviceClient deviceClient;

    private final ISysClient sysClient;

    private final IUserClient userClient;

    private final IVehicleClient vehicleClient;

    private final IPersonClient personClient;

    private final IPersonVehicleRelClient personVehicleRelClient;

    private final INotificationClient notificationClient;

    private final MongoTemplate mongoTemplate;

    private final IDeviceExtClient deviceExtClient;

    private final IPushcClient pushcClient;

    private final ISimClient simClient;

    private final IBigScreenDataClient bigScreenDataClient;

    private final IHomeDataClient homeDataClient;

    private final IMinicreateAttachService minicreateAttachService;

    private AlarmProductSource alarmProductSource;

    private IPolymerizationDataClient polymerizationDataClient;

    private IDataChangeEventClient dataChangeEventClient;

    private IAlarmInformService alarmInformService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean pushDataToMongodb() {
        List<AlarmInfo> alarmInfos = this.list(new LambdaQueryWrapper<>(new AlarmInfo()));
        // 删除mongo中alarmInfo信息
        mongoTemplate.dropCollection(AlarmConstant.MONGODB_ALARM_INFO);
        List<Long> errorInfo = new ArrayList<>();
        alarmInfos.forEach(alarmInfo -> {
            try {
                // 设置租户id
//				AuthUtil.getUser().setTenantId(alarmInfo.getTenantId());
                // 构造存mongodb的详细信息
                AlarmInfoHandleInfoVO alarmInfoHandleInfoVO = handleAlarmInfoVO(alarmInfo);
                getDept(alarmInfo, alarmInfoHandleInfoVO);
                saveAlarmInfoToMongoDB(alarmInfo, alarmInfoHandleInfoVO);
            } catch (Exception e) {
                errorInfo.add(alarmInfo.getId());
                e.printStackTrace();
            }
        });
        if (errorInfo.size() > 0) {
            log.error(StrUtil.format("alarmInfo[{}] push to mongodb error", errorInfo.stream().map(Object::toString).reduce((a, b) -> a + "," + b)));
        }
        return true;
    }

    /**
     * 更新确认信息到mongodb
     *
     * @param alarmInfo
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public void updateMongoDBByAlarmId(AlarmInfo alarmInfo) {
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("alarmId").is(alarmInfo.getId()));
        AlarmInfoMongoDBVO oneRecod = mongoTemplate.findOne(query, AlarmInfoMongoDBVO.class, AlarmConstant.MONGODB_ALARM_INFO);
        Optional.ofNullable(oneRecod).ifPresent(record -> {
            Update update = new Update();
            update.set("isHandle", alarmInfo.getIsHandle());
            update.set("isHandleName", Optional.ofNullable(alarmInfo.getIsHandle()).map(AlarmHandledName::getName).orElse(""));
            update.set("alarmCheck", alarmInfo.getAlarmCheck());
            update.set("checkRemark", alarmInfo.getCheckRemark());
            update.set("informType", alarmInfo.getInformType());
            update.set("updateUser", alarmInfo.getUpdateUser());
            update.set("updateTime", alarmInfo.getUpdateTime().getTime());
            mongoTemplate.findAndModify(query, update, AlarmInfoMongoDBVO.class, AlarmConstant.MONGODB_ALARM_INFO);
        });
    }

    /**
     * 更新主动告警data到mongodb
     *
     * @param alarmInfo
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public void updateInitiativeAlarmToMongo(AlarmInfo alarmInfo) {
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("alarmId").is(alarmInfo.getId()));
        AlarmInfoMongoDBVO oneRecod = mongoTemplate.findOne(query, AlarmInfoMongoDBVO.class, AlarmConstant.MONGODB_ALARM_INFO);
        Optional.ofNullable(oneRecod).ifPresent(record -> {
            Update update = new Update();
            update.set("updateTime", alarmInfo.getUpdateTime().getTime());
            update.set("data", alarmInfo.getData());
            mongoTemplate.findAndModify(query, update, AlarmInfoMongoDBVO.class, AlarmConstant.MONGODB_ALARM_INFO);
        });
    }


    /**
     * @param alarmInfo
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean handleBigDataAlarmInfo(AlarmInfo alarmInfo) throws Exception {
        // 完善告警信息
        populateAlarmInfoField(alarmInfo);
        boolean success = this.save(alarmInfo);
        // 构造存mongodb的详细信息
        AlarmInfoHandleInfoVO alarmInfoHandleInfoVO = handleAlarmInfoVO(alarmInfo);
        // 处理部门
        getDept(alarmInfo, alarmInfoHandleInfoVO);
        // 存mongodb
        AlarmInfoMongoDBVO mongoDBVO = saveAlarmInfoToMongoDB(alarmInfo, alarmInfoHandleInfoVO);

        String tenantId = alarmInfo.getTenantId();
		/*
			把告警message缓存mongo
		 */
        if (success) {
            Long now = TimeUtil.getSysDate().getTime();
            JSONObject message = new JSONObject();
            String messageId = MessageConstant.MessageType.ALARM_MESSAGE + "_" + alarmInfo.getId() + "_" + now;
            message.put("messageId", messageId);
            message.put("messageType", MessageConstant.MessageType.ALARM_MESSAGE);
            message.put("messageKind", "2");
            if (mongoDBVO.getEntityType().equals(CommonConstant.ENTITY_TYPE.PERSON)) {
                message.put("messageKind", "1");
            }
            String entityTypeName = mongoDBVO.getEntityType().equals(CommonConstant.ENTITY_TYPE.PERSON) ? "人员" : "车辆";
            message.put("messageTitle", StrUtil.format(entityTypeName + " - {}", alarmInfo.getRuleName()));
            message.put("messageContent", StrUtil.format(entityTypeName + ":{},{}", alarmInfo.getEntityName(), alarmInfo.getAlarmMessage()));
            message.put("messageData", mongoDBVO);
            mongoTemplate.save(message, "messageInfo_" + tenantId);


            //本人
            List<String> userIds = new ArrayList<String>();
            Set<String> userEmails = new HashSet<>();
            boolean sendMail = false;
            if (StrUtil.isNotBlank(alarmInfo.getInformType()) &&
                    alarmInfo.getInformType().contains(String.valueOf(InformTypeEnum.EMAIL.getIndex()))) {
                sendMail = true;
            }

            if (mongoDBVO.getEntityType().equals(CommonConstant.ENTITY_TYPE.VEHICLE)) {
                //如果是车，那么就要查发生告警的时候驾驶员
                Person person = personVehicleRelClient.getCurrentDriver(alarmInfo.getEntityId()).getData();
                if (ObjectUtil.isNotEmpty(person) && ObjectUtil.isNotEmpty(person.getId())) {
                    userIds.add(person.getId().toString());
                    if (sendMail && StrUtil.isNotBlank(person.getEmail())) {
                        userEmails.add(person.getEmail());
                    }
                }
            } else {
                userIds.add(alarmInfo.getEntityId().toString());
                Person person = personClient.getPerson(alarmInfo.getEntityId()).getData();
                if (person != null) {
                    if (sendMail && StrUtil.isNotBlank(person.getEmail())) {
                        userEmails.add(person.getEmail());
                    }
                }
            }

            //系统管理员
            Role adminRole = sysClient.getTenantAdminRole(tenantId).getData();
            if (ObjectUtil.isNotEmpty(adminRole) && ObjectUtil.isNotEmpty(adminRole.getId())) {
                List<User> userList = userClient.getRoleUser(adminRole.getId().toString(), tenantId).getData();
                userList.forEach(user -> {
                    userIds.add(user.getId().toString());
                    if (StrUtil.isNotBlank(user.getEmail())) {
                        userEmails.add(user.getEmail());
                    }
                });
            }
            //责任人的领导.暂时放一放。


            // 发邮件提醒
            if (sendMail) {
                EmailDTO emailDTO = new EmailDTO();
                if (CommonConstant.ENTITY_TYPE.PERSON.equals(alarmInfoHandleInfoVO.getEntityType())) {
                    /*
                     * 紧急告警:欧梓航（000005）
                     * 人员终端工作时间信号丢失告警：持续时间:(25)分钟
                     */
                    emailDTO.setSubject(StrUtil.format("{}告警：{}({})", alarmInfoHandleInfoVO.getAlarmLevelName(), alarmInfoHandleInfoVO.getPersonName(), alarmInfoHandleInfoVO.getJobNumber()));
                } else if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(alarmInfoHandleInfoVO.getEntityType())) {
                    /*
                     * 紧急告警:洒水车（苏A12345）
                     * 洒水车作业超速告警：车速:20公里/小时，持续：5分钟
                     */
                    emailDTO.setSubject(StrUtil.format("{}告警：{}({})", alarmInfoHandleInfoVO.getAlarmLevelName(), alarmInfoHandleInfoVO.getVehicleCategoryName(), alarmInfoHandleInfoVO.getPlateNumber()));
                }
                emailDTO.setContent(StrUtil.format("{}:{}", alarmInfoHandleInfoVO.getRuleName(), alarmInfoHandleInfoVO.getAlarmMessage()));
                userEmails.forEach(emailAddress -> {
                    emailDTO.setReceiver(emailAddress);
                    pushcClient.sendEmail(emailDTO);
                });
            }

            userIds.forEach(userId -> {
                org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
                query.addCriteria(Criteria.where("userId").is(userId));
                UserMessageDTO userMessageDTO = mongoTemplate.findOne(query, UserMessageDTO.class, "userMessage_" + tenantId);
                RelMessageDTO relMessageDTO = new RelMessageDTO();
                relMessageDTO.setMessageId(messageId);
                relMessageDTO.setMessageType(MessageConstant.MessageType.ALARM_MESSAGE);
                relMessageDTO.setRead(false);
                relMessageDTO.setIsDeleted("0");
                relMessageDTO.setReadChannel("");
                relMessageDTO.setPushTime(now);
                List<RelMessageDTO> relMessageDTOList = new ArrayList<RelMessageDTO>();
                if (ObjectUtil.isNotEmpty(userMessageDTO)) {
                    if (userMessageDTO.getAlarmMessageList().size() > 0) {
                        relMessageDTOList = userMessageDTO.getAlarmMessageList();
                    }
                    relMessageDTOList.add(relMessageDTO);
                    Update update = new Update();
                    update.set("unReadAlarmCount", Long.parseLong(userMessageDTO.getUnReadAlarmCount()) + 1 + "");
                    update.set("alarmCount", Long.parseLong(userMessageDTO.getAlarmCount()) + 1 + "");
                    update.set("alarmMessageList", relMessageDTOList);
                    mongoTemplate.upsert(query, update, "userMessage_" + tenantId);
                } else {
                    userMessageDTO = new UserMessageDTO();
                    relMessageDTOList.add(relMessageDTO);
                    userMessageDTO.setUserId(userId);
                    userMessageDTO.setUnReadAlarmCount("1");
                    userMessageDTO.setAlarmCount("1");
                    userMessageDTO.setUnReadEventCount("0");
                    userMessageDTO.setUnReadAnnounCount("0");
                    userMessageDTO.setAnnounCount("0");
                    userMessageDTO.setEventCount("0");
                    userMessageDTO.setAlarmMessageList(relMessageDTOList);
                    userMessageDTO.setAnnounMessageList(new ArrayList<RelMessageDTO>());
                    userMessageDTO.setEventMessageList(new ArrayList<RelMessageDTO>());
                    mongoTemplate.save(userMessageDTO, "userMessage_" + tenantId);
                }
            });
        }

        // 发送页面通知
        if (success) {
            NotificationInfo notificationInfo = new NotificationInfo();
            if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(alarmInfoHandleInfoVO.getEntityType())) {
                /*
                 * 紧急告警
                 * 洒水车（苏A12345）：洒水车作业超速告警
                 * 车速:20公里/小时，持续：5分钟
                 */
                notificationInfo.setTitle(StrUtil.format("{}告警", alarmInfoHandleInfoVO.getAlarmLevelName())); // 紧急告警
                notificationInfo.setContent(StrUtil.format("{}:{}({})<br/ >{}",
                        alarmInfoHandleInfoVO.getVehicleCategoryName(), alarmInfoHandleInfoVO.getPlateNumber(), alarmInfoHandleInfoVO.getRuleName(), alarmInfoHandleInfoVO.getAlarmMessage()));
            } else if (CommonConstant.ENTITY_TYPE.PERSON.equals(alarmInfoHandleInfoVO.getEntityType())) {
                /*
                 * 紧急告警
                 * 欧梓航（000005）：人员终端工作时间信号丢失告警
                 * 持续时间:(25)分钟
                 */
                notificationInfo.setTitle(StrUtil.format("{}告警", alarmInfoHandleInfoVO.getAlarmLevelName())); // 紧急告警
                if (AlarmConstant.PERSON_WATCH_SOS_ALARM.equals(mongoDBVO.getRuleCategoryCode())) {
                    notificationInfo.setContent(mongoDBVO.getAlarmMessage());
                } else {
                    notificationInfo.setContent(StrUtil.format("{}:{}({})<br/ >{}",
                            alarmInfoHandleInfoVO.getPersonName(), alarmInfoHandleInfoVO.getJobNumber(), alarmInfoHandleInfoVO.getRuleName(), alarmInfoHandleInfoVO.getAlarmMessage()));
                }
            }
            notificationInfo.setCategory(WebSocketConsts.NotificationCategory.ALARM);
            notificationInfo.setId(String.valueOf(alarmInfoHandleInfoVO.getId()));
            notificationInfo.setPath(WebSocketConsts.NotificationPath.ALARM_INFO);
            notificationInfo.setPathType(WebSocketConsts.NotificationPathType.INNER_LINK);
            notificationInfo.setLevel(WebSocketConsts.NotificationLevel.WARNING);
            notificationInfo.setUserId(null);
            notificationInfo.setTenantId(alarmInfoHandleInfoVO.getTenantId()); // 告警传给租户所有人
            notificationInfo.setBroadCast(true);
            try {
                notificationClient.pushNotification(notificationInfo);
            } catch (Exception e) {
                log.error(StrUtil.format("告警消息推送前端出错，告警内容[{};{}]", notificationInfo.getTitle(), notificationInfo.getContent()));
            }
        }

        if (success) {
//            首页统计数据redis更新
//            homeDataClient.updateHomeCountRedis(tenantId);
//            bigScreenDataClient.updateBigscreenCountRedis(tenantId);
//            bigScreenDataClient.updateBigscreenAlarmAmountRedis(tenantId);
//            bigScreenDataClient.updateBigscreenAlarmListRedis(tenantId);
//            //首页告警列表redis数据更新
//            if (AlarmConstant.AlarmLevel.EMERGENCY == alarmInfoHandleInfoVO.getAlarmLevel()) {
//                homeDataClient.updateHomeAlarmListRedis(tenantId);
//            }
//            //综合页面数据redis更新
//            polymerizationDataClient.updatePolymerizationCountRedis(tenantId, "-1");

            //触发数据库变更事件
            dataChangeEventClient.doDbEvent(new BaseDbEventDTO<String>(DbEventConstant.EventType.ALARM_EVENT, tenantId, String.valueOf(alarmInfo.getId())));
        }
        return success;
    }

    // 缓存mongo
    private AlarmInfoMongoDBVO saveAlarmInfoToMongoDB(AlarmInfo alarmInfo, AlarmInfoHandleInfoVO alarmInfoHandleInfoVO) {
        AlarmInfoMongoDBVO mongoDBVO = new AlarmInfoMongoDBVO();
        BeanUtil.copyProperties(alarmInfoHandleInfoVO, mongoDBVO, "alarmTime", "createTime", "updateTime");
        mongoDBVO.setAlarmTime(alarmInfo.getAlarmTime().getTime());
        mongoDBVO.setCreateTime(alarmInfo.getCreateTime().getTime());
        mongoDBVO.setUpdateTime(alarmInfo.getUpdateTime().getTime());
        mongoDBVO.setAlarmId(alarmInfo.getId());
        //处理主动告警信息
        if (AlarmConstant.VEHICLE_INITIATIVE_ALARM.equals(mongoDBVO.getRuleCategoryCode())) {
            initiativeAlarmHandler(mongoDBVO);
        } else if (AlarmConstant.PERSON_WATCH_SOS_ALARM.equals(mongoDBVO.getRuleCategoryCode())) {
            try {
                String alarmMessage = "{}在{}发出SOS求助告警";
                Coords coords = new Coords();
                coords.setLatitude(mongoDBVO.getLatitudinal());
                coords.setLongitude(mongoDBVO.getLongitude());
                BaiduMapReverseGeoCodingResult reverseGeoCoding = BaiduMapUtils.getReverseGeoCoding(coords);
                String addressName = reverseGeoCoding.getResult().getFormatted_address();
                mongoDBVO.setAlarmMessage(StrFormatter.format(alarmMessage, mongoDBVO.getEntityName(), addressName));
            } catch (IOException e) {
                log.error("SOS地址转换异常:[{}]", e.getMessage());
            }
        }
        mongoTemplate.save(mongoDBVO);


//        if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(mongoDBVO.getEntityType())) {
//            org.springframework.data.mongodb.core.query.Query query = org.springframework.data.mongodb.core.query.Query.query(Criteria.where("id").is(mongoDBVO.getEntityId()));
//            BasicVehicleInfoDTO one = mongoTemplate.findOne(query, BasicVehicleInfoDTO.class);
//            Update update = Update.update("todayAlarmCount", one.getTodayAlarmCount() == null ? 1L : one.getTodayAlarmCount() + 1);
//            update.set("lastAlarmContent", mongoDBVO.getAlarmMessage());
//            mongoTemplate.findAndModify(query, update, BasicVehicleInfoDTO.class);
//            List<Long> vehicleIdList = new ArrayList<>();
//            vehicleIdList.add(mongoDBVO.getEntityId());
//            sendVehicleChangeMessageToMQ(vehicleIdList);
//        } else if (CommonConstant.ENTITY_TYPE.PERSON.equals(mongoDBVO.getEntityType())) {
//            org.springframework.data.mongodb.core.query.Query query = org.springframework.data.mongodb.core.query.Query.query(Criteria.where("id").is(mongoDBVO.getEntityId()));
//            BasicPersonDTO one = mongoTemplate.findOne(query, BasicPersonDTO.class);
//            Update update = Update.update("todayAlarmCount", one.getTodayAlarmCount() == null ? 1L : one.getTodayAlarmCount() + 1);
//            update.set("lastAlarmContent", mongoDBVO.getAlarmMessage());
//            mongoTemplate.findAndModify(query, update, BasicPersonDTO.class);
//            List<Long> personIdList = new ArrayList<>();
//            personIdList.add(mongoDBVO.getEntityId());
//            sendPersonChangeMessageToMQ(personIdList);
//        }


        return mongoDBVO;
    }

    /**
     * 主动告警信息处理
     *
     * @param mongoDBVO
     */
    private void initiativeAlarmHandler(AlarmInfoMongoDBVO mongoDBVO) {
        String data = mongoDBVO.getData();
        JSONObject dsmAlarmInfo = JSONObject.parseObject(data).getJSONObject("dsm_alarm_info");
        JSONObject adasAlarmInfo = JSONObject.parseObject(data).getJSONObject("adas_alarm_info");
        String alarmMessage = "";
        if (dsmAlarmInfo != null) {
            mongoDBVO.setInitiativeAlarmType(AlarmConstant.MinicreatAlarmType.DSM_ALARM);
            MinicreateDsmAlarmVO dsmAlarm = new MinicreateDsmAlarmVO();
            Integer eventType = dsmAlarmInfo.getInteger("event_type");
            dsmAlarm.setEventType(eventType);
            if (eventType != null) {
                alarmMessage = MinicreateDSMAlarm.getName(eventType).concat(StringPool.COLON).concat(StringPool.LEFT_SQ_BRACKET);
                if (eventType == 1) {
                    Integer fatigueDegree = dsmAlarmInfo.getInteger("fatigue_degree");
                    dsmAlarm.setFatigueDegree(fatigueDegree);
                    alarmMessage = alarmMessage.concat("疲劳等级：").concat(fatigueDegree.toString()).concat(StringPool.COMMA);
                }
            }
            Integer level = dsmAlarmInfo.getInteger("level");
            dsmAlarm.setLevel(level);
            mongoDBVO.setDsmAlarmVO(dsmAlarm);
        } else if (adasAlarmInfo != null) {
            mongoDBVO.setInitiativeAlarmType(AlarmConstant.MinicreatAlarmType.ADAS_ALARM);
            MinicreatAdasAlarmVO adasAlarm = new MinicreatAdasAlarmVO();
            adasAlarm.setLevel(adasAlarmInfo.getInteger("level"));
            Integer eventType = adasAlarmInfo.getInteger("event_type");
            adasAlarm.setEventType(eventType);
            if (eventType != null) {
                alarmMessage = MinicreateADASAlarm.getName(eventType).concat(StringPool.COLON).concat(StringPool.LEFT_SQ_BRACKET);
                adasAlarm.setEventTypeName(MinicreateADASAlarm.getName(eventType));

                Integer frontCarSpeed = adasAlarmInfo.getInteger("front_car_speed");
                if (frontCarSpeed != null) {
                    if (eventType == 1) {
                        adasAlarm.setFrontCarSpeed(frontCarSpeed);
                        alarmMessage = alarmMessage.concat("前车车速:").concat(frontCarSpeed.toString()).concat("公里/小时,");
                    }
                }
                Integer forwardDistance = adasAlarmInfo.getInteger("forward_distance");
                if (forwardDistance != null) {
                    if (eventType == 1 || eventType == 4) {
                        adasAlarm.setForwardDistance(forwardDistance);
                        alarmMessage = alarmMessage.concat("前车或行人距离:").concat(forwardDistance.toString()).concat("米,");
                    }
                }
                Integer ldwType = adasAlarmInfo.getInteger("ldw_type");
                if (ldwType != null && eventType == 2) {
                    adasAlarm.setLdwType(ldwType);
                    if (ldwType == 1) {
                        adasAlarm.setLdwTypeName("左侧偏移");
                        alarmMessage = alarmMessage.concat("左侧偏移:").concat(ldwType.toString()).concat("米,");
                    }
                    if (ldwType == 2) {
                        adasAlarm.setLdwTypeName("右侧偏移");
                        alarmMessage = alarmMessage.concat("右侧偏移:").concat(ldwType.toString()).concat("米,");
                    }
                }
                Integer trafficSignType = adasAlarmInfo.getInteger("traffic_sign_type");
                Integer tarfficSignData = adasAlarmInfo.getInteger("tarffic_sign_data");
                if (trafficSignType != null) {
                    if (eventType == 6) {
                        if (trafficSignType == 1) {
                            adasAlarm.setTrafficSignTypeName("限速标志");
                            alarmMessage = alarmMessage.concat("限速标志,限速").concat(tarfficSignData.toString()).concat("公里/小时,");
                        }
                        if (trafficSignType == 2) {
                            adasAlarm.setTrafficSignTypeName("限高标志");
                            alarmMessage = alarmMessage.concat("限高标志,限高").concat(tarfficSignData.toString()).concat("米,");
                        }
                        if (trafficSignType == 3) {
                            adasAlarm.setTrafficSignTypeName("限重标志");
                            alarmMessage = alarmMessage.concat("限重标志,限重").concat(tarfficSignData.toString()).concat("吨,");
                        }
                        adasAlarm.setTrafficSignData(tarfficSignData);
                    }
                }
            }
            mongoDBVO.setAdasAlarmVO(adasAlarm);
        }
        if (alarmMessage.endsWith(StringPool.LEFT_SQ_BRACKET)) {
            alarmMessage = alarmMessage.substring(0, alarmMessage.length() - 2);
        } else if (alarmMessage.endsWith(StringPool.COMMA)) {
            alarmMessage = alarmMessage.substring(0, alarmMessage.length() - 1).concat(StringPool.RIGHT_SQ_BRACKET);
        }
        mongoDBVO.setAlarmMessage(alarmMessage);
    }


    private void sendVehicleChangeMessageToMQ(List<Long> vehicleIdList) {
        try {
            MessageChannel messageChannel = alarmProductSource.polymerizationVehicleChangeOutput();
            Message<List<Long>> message = MessageBuilder.withPayload(vehicleIdList).build();
            messageChannel.send(message);
        } catch (Exception e) {
            log.warn("发布车辆聚合数据更改信息数据失败", e);
        }
    }


    private void sendPersonChangeMessageToMQ(List<Long> personIdList) {
        try {
            MessageChannel messageChannel = alarmProductSource.polymerizationVehicleChangeOutput();
            Message<List<Long>> message = MessageBuilder.withPayload(personIdList).build();
            messageChannel.send(message);
        } catch (Exception e) {
            log.warn("发布人员聚合数据更改信息数据失败", e);
        }
    }


    /**
     * 填充表字段值
     *
     * @param alarmInfo
     */
    private void populateAlarmInfoField(AlarmInfo alarmInfo) {
        @NotEmpty(message = "设备编码不能为空") String deviceCode = alarmInfo.getDeviceCode();
        @NotNull(message = "告警规则id不能为空") Long ruleId = alarmInfo.getRuleId();
        // 规则
        AlarmRuleInfo alarmRuleInfo = alarmRuleInfoService.getById(ruleId);
        if (alarmRuleInfo == null) {
            throw new ServiceException(StrUtil.format("告警规则[{}]不存在", ruleId));
        }
		/*// 大数据同步数据过来用的是admin账号，设置当前租户与ruleId租户一致
		AuthUtil.getUser().setTenantId(alarmRuleInfo.getTenantId());*/
        // 设置当前租户与ruleId租户一致
        alarmInfo.setTenantId(alarmRuleInfo.getTenantId());

        if (ObjectUtil.isNotEmpty(alarmRuleInfo.getEntityCategoryId())) {
            alarmInfo.setRuleId(ruleId);
            alarmInfo.setRuleName(alarmRuleInfo.getName());
            alarmInfo.setRuleCategoryId(alarmRuleInfo.getEntityCategoryId());
            alarmInfo.setRuleCategoryCode(alarmRuleInfo.getEntityCategoryCode());

            EntityCategory entityCategory = entityCategoryClient.getCategory(alarmRuleInfo.getEntityCategoryId()).getData();
            if (Objects.nonNull(entityCategory)) {
                alarmInfo.setParentRuleCategoryId(entityCategory.getParentCategoryId());
            }
        }
        DeviceInfo deviceByCode = DeviceCache.getDeviceByCode(alarmRuleInfo.getTenantId(), deviceCode); // 使用告警规则的租户查询
        if (deviceByCode == null) {
            throw new ServiceException(StrUtil.format("设备编码【{}】不存在", deviceByCode));
        }
        // 点创设备只接收监控告警，其他过滤
        if (DeviceConstant.DeviceFactory.MINICREATE.equals(deviceByCode.getDeviceFactory())) {
            if (!DeviceConstant.DeviceCategory.VEHICLE_CVR_MONITOR_DEVICE.equals(deviceByCode.getEntityCategoryId())) {
                throw new ServiceException("点创设备只接收监控告警,非监控告警一律丢弃");
            }
        }
        DeviceRel deviceRel = deviceRelClient.getDeviceRelByDeviceId(deviceByCode.getId()).getData();
        if (deviceRel == null || deviceRel.getId() == null) {
            throw new ServiceException(StrUtil.format("设备编码【{}】没有绑定到车辆/人员，或者绑定的车辆/人员不存在", deviceByCode));
        }
        if (CommonConstant.ENTITY_TYPE.PERSON.toString().equals(deviceRel.getEntityType())) {
            alarmInfo.setEntityId(deviceRel.getEntityId());
            alarmInfo.setEntityType(CommonConstant.ENTITY_TYPE.PERSON);
            Person person = personClient.getPerson(deviceRel.getEntityId()).getData();
            if (person != null) {
                alarmInfo.setEntityName(person.getPersonName()); // 人名
                alarmInfo.setEntityDefine(person.getJobNumber()); // 工号
            } else {
                throw new ServiceException(StrUtil.format("设备【{}】绑定的人员不存在", deviceByCode));
            }
        } else if (CommonConstant.ENTITY_TYPE.VEHICLE.toString().equals(deviceRel.getEntityType())) {
            alarmInfo.setEntityId(deviceRel.getEntityId());
            alarmInfo.setEntityType(CommonConstant.ENTITY_TYPE.VEHICLE);
            VehicleInfo vehicleInfo = vehicleClient.vehicleInfoById(deviceRel.getEntityId()).getData();
            if (vehicleInfo != null) {
                alarmInfo.setEntityName(vehicleInfo.getPlateNumber()); // 车牌号
                EntityCategory vehicleEntityCategory = entityCategoryClient.getCategory(vehicleInfo.getEntityCategoryId()).getData();
                alarmInfo.setEntityDefine(vehicleEntityCategory.getCategoryName()); // 车辆类型名称，如洒水车
            } else {
                throw new ServiceException(StrUtil.format("设备【{}】绑定的车辆不存在", deviceByCode));
            }
        }

        // 查询配置的通知方式
        LambdaQueryWrapper<AlarmInform> alarmInformQuery = new LambdaQueryWrapper<>();
        // 告警等级
        Integer ruleAlarmLevel = alarmInfo.getRuleAlarmLevel();
        // 通知类型改造
        alarmInformQuery.eq(AlarmInform::getEntityType, alarmInfo.getEntityType());
        alarmInformQuery.eq(AlarmInform::getAlarmLevel, ruleAlarmLevel);
        alarmInformQuery.eq(AlarmInform::getTenantId, alarmRuleInfo.getTenantId());
        alarmInformQuery.eq(AlarmInform::getIsDeleted, BladeConstant.DB_NOT_DELETED);
        List<AlarmInform> alarmInformList = alarmInformService.list(alarmInformQuery);
        if (CollectionUtil.isNotEmpty(alarmInformList)) {
            alarmInfo.setInformType(alarmInformList.get(0).getInformType());
        }

//        DeviceExt deviceExt = deviceExtClient.getByAttrId(deviceByCode.getId(), CommonConstant.VEHICLE_WARCH_COORDS_CATEGORY_ID).getData();

//        //获取坐标系。默认为国测局坐标系
//        BaiduMapUtils.CoordsSystem coordsSystem = BaiduMapUtils.CoordsSystem.GC02;
//        if (deviceExt == null) {
//            String attrValue = deviceExt.getAttrValue();
//            coordsSystem = BaiduMapUtils.CoordsSystem.getCoordsSystem(Integer.parseInt(attrValue));
//        }
        String longitude = JSONUtil.parseObj(alarmInfo.getData()).getStr("longitude");
        String latitude = JSONUtil.parseObj(alarmInfo.getData()).getStr("latitude");

//        if (longitude != null && latitude != null) {
//            if (coordsSystem.equals(BaiduMapUtils.CoordsSystem.BD09LL)) {
//                List<Coords> coordsList = new ArrayList<>();
//                Coords coords = new Coords();
//                coords.setLat(latitude);
//                coords.setLongitude(longitude);
//                coordsList.add(coords);
//                List<Coords> coords1 = baiduMapUtils.baiduMapllToGC02All(coordsList);
//                if (CollectionUtil.isNotEmpty(coords1)) {
//                    Coords coords2 = coords1.get(0);
//                    longitude = coords2.getLongitude();
//                    latitude = coords2.getLatitude();
//                }
//            }
//        }
        alarmInfo.setLongitude(longitude);
        alarmInfo.setLatitudinal(latitude);

        String eventTime = JSONObject.parseObject(alarmInfo.getData()).getString("eventTime");
        if (StringUtils.isNotBlank(eventTime)) {
            try {
                DateTime alarmTime = DateUtil.parse(eventTime);
                alarmInfo.setAlarmTime(new Timestamp(alarmTime.getTime()));
            } catch (Exception e) {
                log.error("alarmInfo取告警时间错误， 报文：[{}], eventTime:[{}]", alarmInfo.getData(), eventTime);
            }
        } else {
            alarmInfo.setAlarmTime(TimeUtil.getSysDate());
        }
        alarmInfo.setIsHandle(AlarmConstant.IsHandle.HANDLED_NO); // 默认为未处理
        String dataCopy = alarmInfo.getData();
        // 把告警级别放在data里面
        dataCopy = Objects.requireNonNull(JSONUtil.parseObj(dataCopy).put("alarmLevel", alarmInfo.getRuleAlarmLevel())).toString();
        alarmInfo.setAlarmMessage(constructAlarmMessage(dataCopy.toLowerCase(), ruleId)); // 拼接告警信息备注

        //获取该实体工作区域的业务区域

        List<WorkareaRel> workareaRelList = workareaRelClient.getByEntityIdAndType(deviceRel.getEntityId(), Long.parseLong(deviceRel.getEntityType())).getData();
        if (ObjectUtil.isNotEmpty(workareaRelList) && workareaRelList.size() > 0) {
            WorkareaInfo workareaInfo = workareaClient.getWorkInfoById(workareaRelList.get(0).getWorkareaId()).getData();
            if (ObjectUtil.isNotEmpty(workareaInfo)) {
                alarmInfo.setRegionId(workareaInfo.getRegionId().toString());

            }
        }

    }

    /**
     * 取车或者人的岗位，部门
     *
     * @param alarmInfo
     * @param alarmInfoHandleInfoVO
     */
    private void getDept(AlarmInfo alarmInfo, AlarmInfoHandleInfoVO alarmInfoHandleInfoVO) {
        Long deptId = null;
        if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(alarmInfoHandleInfoVO.getEntityType())) {
            VehicleInfo vehicleInfo = VehicleCache.getVehicleById(alarmInfo.getTenantId(), alarmInfo.getEntityId());
            if (vehicleInfo != null) {
                deptId = vehicleInfo.getDeptId();
            }
        } else if (CommonConstant.ENTITY_TYPE.PERSON.equals(alarmInfoHandleInfoVO.getEntityType())) {
            Person person = PersonCache.getPersonById(alarmInfo.getTenantId(), alarmInfo.getEntityId());
            if (person != null) {
                deptId = person.getPersonDeptId();
                // 查岗位
                if (person.getPersonPositionId() != null) {
                    alarmInfoHandleInfoVO.setPersonPositionId(person.getPersonPositionId());
                    String stationName = StationCache.getStationName(person.getPersonPositionId());
                    alarmInfoHandleInfoVO.setPersonPositionName(stationName);
                }
            }
        }
        // 取部门名称
        if (Objects.nonNull(deptId)) {
            Dept dept = sysClient.getDept(deptId).getData();
            alarmInfoHandleInfoVO.setDeptId(deptId);
            alarmInfoHandleInfoVO.setDepartment(dept.getFullName());
        }
    }

    /**
     * 拼接告警信息
     *
     * @param data
     * @param ruleId
     * @return
     */
    private String constructAlarmMessage(String data, Long ruleId) {
        JSONObject alarmParam = JSONObject.parseObject(data);
        String alarmMesssage = "";
        List<AlarmRuleExt> alarmRuleExts = alarmRuleExtService.listByAlarmRuleId(ruleId);
        List<AlarmRuleExtVO> alarmRuleExtVOS = AlarmRuleExtWrapper.build().listVO(alarmRuleExts);
        List<AlarmRuleExtVO> extRelationShipList = alarmRuleExtService.calculateRelationship(alarmRuleExtVOS);
        for (AlarmRuleExtVO alarmRuleExtVO : extRelationShipList) {
            // 拼接remark
            // 如果是勾选框，并且已勾选，则继续判断
            boolean ruleSelect = alarmRuleExtVO.getInputType().equals((long) AlarmConstant.AttrInputType.CHECK_BOX)
                    && AlarmConstant.AttrInputType.CheckBoxValue.SELECTED.equals(alarmRuleExtVO.getInputValue());
            if (ruleSelect) {
                // 并且大数据也同步了这个属性值过来则拼接
                boolean alarmSynchronized = ObjectUtil.isNotEmpty(alarmParam.get(alarmRuleExtVO.getAttrCode().toLowerCase()))
                        && JSONUtil.parseObj(alarmParam.get(alarmRuleExtVO.getAttrCode().toLowerCase())).get("value").equals("1");
                if (alarmSynchronized) {
                    alarmMesssage = concatMessage(alarmMesssage, alarmRuleExtVO, alarmParam);
                    List<AlarmRuleExtVO> extensions = alarmRuleExtVO.getExtensionList();
                    if (CollectionUtil.isNotEmpty(extensions)) {
                        for (AlarmRuleExtVO extension : extensions) {
                            alarmMesssage = concatMessage(alarmMesssage, extension, (JSONObject) alarmParam.get(alarmRuleExtVO.getAttrCode().toLowerCase()));
                        }
                    }
                }
            } else {
                Integer alarmLevel = alarmParam.getInteger("alarmlevel");
                // 只拼同等级的告警信息
                if (alarmLevel != null && alarmLevel.equals(alarmRuleExtVO.getAttrLevel())) {
                    alarmMesssage = concatMessage(alarmMesssage, alarmRuleExtVO, alarmParam);
                }
            }
        }
        if (alarmMesssage != null && !"".equals(alarmMesssage) && alarmMesssage.length() > 1) {
            alarmMesssage = alarmMesssage.substring(0, alarmMesssage.length() - 1);
        }
        return alarmMesssage;
    }

    /**
     * 通过attrCode填充告警信息
     *
     * @param message
     * @param alarmRuleExtVO
     * @param alarmParam
     * @return
     */
    private String concatMessage(String message, AlarmRuleExtVO alarmRuleExtVO, JSONObject alarmParam) {
        // 拼参数信息
        if (alarmRuleExtVO.getInputValue() != null && !"".equals(alarmRuleExtVO.getInputValue())) {
            if (alarmRuleExtVO.getInputType().equals((long) AlarmConstant.AttrInputType.TEXT_INPUT)) {
                String attrValue = "";
                if (alarmRuleExtVO.getAttrCode() != null) {
                    attrValue = String.valueOf(alarmParam.get(alarmRuleExtVO.getAttrCode().toLowerCase()));
                    if (StringUtils.isBlank(attrValue) || attrValue.equals("") || attrValue.equals("null")) {
                        return message;
                    }
                    if (alarmRuleExtVO.getAttrCode().equals(AlarmConstant.OutOfAreaAlarm.DURATION)) {
                        attrValue = String.valueOf(Long.parseLong(attrValue) / 1000 / 60); // 持续时间大数据过来是毫秒
                    } else if (alarmRuleExtVO.getAttrCode().equals(AlarmConstant.OutOfAreaAlarm.OUT_OF_AREA_DEVIATION_VALUE)) {
                        if (attrValue.contains(".")) {
                            attrValue = attrValue.substring(0, attrValue.indexOf(".") + 2); // 距离取2位小数
                        }
                    }
                }
                message = message.concat(alarmRuleExtVO.getAttrName()).concat(":").concat(attrValue != null && !"".equals(attrValue) ? attrValue : "")
                        .concat(alarmRuleExtVO.getMeasurementUnitName()).concat(",");
            } else if (alarmRuleExtVO.getInputType().equals((long) AlarmConstant.AttrInputType.CHECK_BOX)
                    && AlarmConstant.AttrInputType.CheckBoxValue.SELECTED.equals(alarmRuleExtVO.getInputValue())) {
                message = message.concat(alarmRuleExtVO.getAttrName()).concat(",");
            }
        }
        return message;
    }

    /**
     * 先从monggo取数据，没有再从数据库查
     *
     * @param id
     * @return
     */
    @Override
    public AlarmInfoHandleInfoVO detailByIdFromMongo(Long id) {
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("alarmId").is(id));
        AlarmInfoMongoDBVO alarmInfoMongoDBVO = mongoTemplate.findOne(query, AlarmInfoMongoDBVO.class, AlarmConstant.MONGODB_ALARM_INFO);
        return Optional.ofNullable(alarmInfoMongoDBVO)
                .map(u -> {
                    AlarmInfoHandleInfoVO targetVO = new AlarmInfoHandleInfoVO();
                    BeanUtils.copyProperties(alarmInfoMongoDBVO, targetVO, "alarmTime", "createTime", "updateTime");
                    targetVO.setAlarmTime(alarmInfoMongoDBVO.getAlarmTime() != null ? new Timestamp(alarmInfoMongoDBVO.getAlarmTime()) : null);
                    targetVO.setCreateTime(alarmInfoMongoDBVO.getCreateTime() != null ? new Date(alarmInfoMongoDBVO.getCreateTime()) : null);
                    targetVO.setUpdateTime(alarmInfoMongoDBVO.getUpdateTime() != null ? new Date(alarmInfoMongoDBVO.getUpdateTime()) : null);
                    targetVO.setId(alarmInfoMongoDBVO.getAlarmId());
                    if (AlarmConstant.VEHICLE_INITIATIVE_ALARM.equals(alarmInfoMongoDBVO.getRuleCategoryCode())) {
                        MinicreateAttachVO attachQuery = new MinicreateAttachVO();
                        attachQuery.setUuid(alarmInfoMongoDBVO.getUuid());
                        attachQuery.setStatus(AlarmConstant.Status.YES);
                        List<MinicreateAttachVO> minicreateAttachVOList = minicreateAttachService.listAttachByCondition(attachQuery);
                        if (cn.hutool.core.collection.CollectionUtil.isNotEmpty(minicreateAttachVOList)) {
                            List<String> initiativeAlarmPics = minicreateAttachVOList.stream().map(MinicreateAttach::getFileUrl).collect(Collectors.toList());
                            targetVO.setInitiativeAlarmPics(initiativeAlarmPics);
                        }
                    }
                    return targetVO;
                })
                .orElseGet(() -> detailById(id));
    }

    /**
     * 根据主键查告警信息
     *
     * @param id
     * @return
     */
    @Override
    public AlarmInfoHandleInfoVO detailById(Long id) {
        AlarmInfoHandleInfoVO alarmInfoHandleInfoVO = null;
        AlarmInfo alarmInfo = baseMapper.selectById(id);
        if (Objects.nonNull(alarmInfo) && Objects.nonNull(alarmInfo.getId())) {
            alarmInfoHandleInfoVO = handleAlarmInfoVO(alarmInfo);
            getDept(alarmInfo, alarmInfoHandleInfoVO);
        }
        return alarmInfoHandleInfoVO;
    }

    @Override
    public IPage<AlarmInfoVO> selectAlarmInfoPage(IPage<AlarmInfoVO> page, AlarmInfoVO alarmInfo) {
        return page.setRecords(baseMapper.selectAlarmInfoPage(page, alarmInfo));
    }

    @Override
    public boolean insertNewAlarmInfo(AlarmInfo alarmInfo) {
        return baseMapper.insertNewAlarmInfo(alarmInfo);
    }


    /**
     * 构造mongodb查询条件
     *
     * @param alarmInfoQueryDTO
     * @return
     */
    private org.springframework.data.mongodb.core.query.Query constructQueryWrapper4MongoDB(AlarmInfoQueryDTO alarmInfoQueryDTO) {
        Long personId = alarmInfoQueryDTO.getPersonId();
        Long vehicleId = alarmInfoQueryDTO.getVehicleId();
        Long startTime = alarmInfoQueryDTO.getStartTime();
        Long endTime = alarmInfoQueryDTO.getEndTime();
        Long entityCategoryId = alarmInfoQueryDTO.getEntityCategoryId();
        Integer alarmLevel = alarmInfoQueryDTO.getAlarmLevel();
        Integer isHandle = alarmInfoQueryDTO.getIsHandle();
        String tenantId = alarmInfoQueryDTO.getTenantId();
        Long entityType = alarmInfoQueryDTO.getEntityType();
        String vehiclePlateNumber = alarmInfoQueryDTO.getVehiclePlateNumber();
        String personName = alarmInfoQueryDTO.getPersonName();
        Long vehicleKindCode = alarmInfoQueryDTO.getVehicleKindCode();
        Long vehicleCategoryId = alarmInfoQueryDTO.getVehicleCategoryId();
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        if (Objects.nonNull(personId)) {
            query.addCriteria(Criteria.where("personId").is(personId));
        }
        if (Objects.nonNull(vehicleId)) {
            query.addCriteria(Criteria.where("vehicleId").is(vehicleId));
        }
        if (Objects.nonNull(entityCategoryId)) {
            // 小程序会传车辆告警/人员告警的entityCategoryId，直接查一层子id
            List<EntityCategory> categories = entityCategoryClient.getCategoryByParentCategoryId(entityCategoryId).getData();
            List<Long> collect = categories.stream().map(EntityCategory::getId).collect(Collectors.toList());
            collect.add(entityCategoryId);
            query.addCriteria(new Criteria().orOperator(Criteria.where("ruleCategoryId").in(collect),
                    Criteria.where("parentRuleCategoryId").in(collect)));
        }
        if (Objects.nonNull(alarmLevel)) {
            query.addCriteria(Criteria.where("ruleAlarmLevel").is(alarmLevel));
        }
        if (Objects.nonNull(isHandle)) {
            query.addCriteria(Criteria.where("isHandle").is(isHandle));
        }
        if (Objects.nonNull(entityType)) {
            query.addCriteria(Criteria.where("entityType").is(entityType));
        }
        if (Objects.nonNull(startTime) && Objects.nonNull(endTime)) {
            query.addCriteria(Criteria.where("alarmTime").gte(startTime).lte(endTime));
        }
        if (StringUtils.isNotBlank(tenantId)) {
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
        } else {
            if (StringUtils.isNotBlank(AuthUtil.getTenantId())) {
                query.addCriteria(Criteria.where("tenantId").is(AuthUtil.getTenantId()));
            }
        }
        if (StringUtils.isNotBlank(vehiclePlateNumber)) {
            query.addCriteria(Criteria.where("entityName").regex(vehiclePlateNumber, "i"));
        } else if (StringUtils.isNotBlank(personName)) {
            query.addCriteria(Criteria.where("entityName").regex(personName, "i"));
        }
        if (Objects.nonNull(vehicleKindCode)) {
            query.addCriteria(Criteria.where("kindCode").is(vehicleKindCode));
        }
        if (Objects.nonNull(vehicleCategoryId)) {
            query.addCriteria(Criteria.where("vehicleCategoryId").is(vehicleCategoryId));
        }
        return query;
    }

    /**
     * 拼接查询条件
     *
     * @param alarmInfoQueryDTO
     * @return
     */
    private QueryWrapper<AlarmInfo> constructQueryWrapperNew(AlarmInfoQueryDTO alarmInfoQueryDTO) {
        Long personId = alarmInfoQueryDTO.getPersonId();
        Long vehicleId = alarmInfoQueryDTO.getVehicleId();
        Long startTime = alarmInfoQueryDTO.getStartTime();
        Long endTime = alarmInfoQueryDTO.getEndTime();
        Long entityCategoryId = alarmInfoQueryDTO.getEntityCategoryId();
        Integer alarmLevel = alarmInfoQueryDTO.getAlarmLevel();
        Integer alarmNum = alarmInfoQueryDTO.getAlarmNum();
        String tenantId = alarmInfoQueryDTO.getTenantId();
        QueryWrapper<AlarmInfo> queryWrapper = new QueryWrapper<>();
        // person vehicle二选一
        queryWrapper.lambda().eq(Objects.nonNull(personId), AlarmInfo::getEntityId, personId).eq(Objects.nonNull(personId), AlarmInfo::getEntityType, CommonConstant.ENTITY_TYPE.PERSON);
        queryWrapper.lambda().eq(Objects.nonNull(vehicleId), AlarmInfo::getEntityId, vehicleId).eq(Objects.nonNull(vehicleId), AlarmInfo::getEntityType, CommonConstant.ENTITY_TYPE.VEHICLE);
        // 时间范围
        if (Objects.nonNull(startTime)) {
            queryWrapper.lambda().ge(AlarmInfo::getAlarmTime, new Timestamp(startTime));
        }
        if (Objects.nonNull(endTime)) {
            queryWrapper.lambda().le(AlarmInfo::getAlarmTime, new Timestamp(endTime));
        }
        // 是否已处理
        queryWrapper.lambda().eq(Objects.nonNull(alarmInfoQueryDTO.getIsHandle()), AlarmInfo::getIsHandle, alarmInfoQueryDTO.getIsHandle());
        // 告警类型，大类或者具体某个规则实体
        queryWrapper.lambda().and(Objects.nonNull(entityCategoryId),
                i -> i.eq(AlarmInfo::getRuleCategoryId, entityCategoryId).or().eq(AlarmInfo::getParentRuleCategoryId, entityCategoryId));
        // 告警级别
        queryWrapper.lambda().eq(Objects.nonNull(alarmLevel), AlarmInfo::getRuleAlarmLevel, alarmLevel);
        queryWrapper.lambda().orderByDesc(AlarmInfo::getAlarmTime); // 默认倒序

        if (Objects.nonNull(tenantId)) {
            queryWrapper.lambda().eq(AlarmInfo::getTenantId, tenantId);
        }

        if (Objects.nonNull(alarmNum)) {
            queryWrapper.last("limit 0 , " + alarmNum);
        }

        return queryWrapper;
    }


    /**
     * 数据转换
     *
     * @param alarmInfoList
     * @return
     */
    private List<AlarmInfoHandleInfoVO> transferAlarmInfoNew(List<AlarmInfo> alarmInfoList) {
        List<AlarmInfoHandleInfoVO> alarmInfoHandleInfoVOS = new ArrayList<>();
        alarmInfoList.forEach(alarmInfo -> {
            alarmInfoHandleInfoVOS.add(handleAlarmInfoVO(alarmInfo));
        });
        return alarmInfoHandleInfoVOS;
    }

    /**
     * alarmInfo转AlarmInfoHandleInfoVO
     *
     * @param alarmInfo
     * @return
     */
    private AlarmInfoHandleInfoVO handleAlarmInfoVO(AlarmInfo alarmInfo) {
        AlarmInfoHandleInfoVO alarmInfoHandleInfoVO = AlarmInfoWrapper.build().copyAlarmInfo4AlarmHandleInfoVO(alarmInfo);
        if (CommonConstant.ENTITY_TYPE.PERSON.equals(alarmInfo.getEntityType())) {
            alarmInfoHandleInfoVO.setPersonId(alarmInfo.getEntityId());
            alarmInfoHandleInfoVO.setPersonName(alarmInfo.getEntityName());
            alarmInfoHandleInfoVO.setJobNumber(alarmInfo.getEntityDefine());
        } else if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(alarmInfo.getEntityType())) {
            alarmInfoHandleInfoVO.setVehicleId(alarmInfo.getEntityId());
            alarmInfoHandleInfoVO.setPlateNumber(alarmInfo.getEntityName());
            VehicleInfo vehicleInfo = vehicleClient.vehicleInfoById(alarmInfo.getEntityId()).getData();
            if (vehicleInfo != null) {
                alarmInfoHandleInfoVO.setKindCode(vehicleInfo.getKindCode()); // 车辆大类Id
                EntityCategory vehicleType = entityCategoryClient.getCategory(vehicleInfo.getKindCode()).getData();
                if (vehicleType != null) {
                    alarmInfoHandleInfoVO.setVehicleTypeName(vehicleType.getCategoryName()); // 车辆大类名称
                }
                alarmInfoHandleInfoVO.setVehicleCategoryId(vehicleInfo.getEntityCategoryId()); // 车辆小类
                alarmInfoHandleInfoVO.setVehicleCategoryName(alarmInfo.getEntityDefine()); // 车辆小类名称
                // 人员主动安全告警取的是车辆上的设备，找当时排了班的驾驶员
                if (AlarmConstant.PERSON_VIOLATION_ALARM_TYPE.equals(alarmInfo.getRuleCategoryCode())) {
                    // 取关联的在岗驾驶员信息
                    Person person = personVehicleRelClient.getCurrentDriver(vehicleInfo.getId()).getData();
                    alarmInfoHandleInfoVO.setPersonId(person.getId());
                    alarmInfoHandleInfoVO.setPersonName(person.getPersonName());
                    alarmInfoHandleInfoVO.setJobNumber(person.getJobNumber());
                }
            }
        }
        // 告警信息主键Id
        alarmInfoHandleInfoVO.setAlarmId(alarmInfo.getId());
        // 告警名称
        alarmInfoHandleInfoVO.setAlarmName(alarmInfo.getRuleName());
        // 告警类型Id和名称
        alarmInfoHandleInfoVO.setAlarmType(alarmInfo.getRuleCategoryId());
        EntityCategory ruleEntityCategory = entityCategoryClient.getCategory(alarmInfo.getRuleCategoryId()).getData(); // TODO 等缓存改造好从缓存取
        alarmInfoHandleInfoVO.setAlarmTypeName(Objects.nonNull(ruleEntityCategory) ? ruleEntityCategory.getCategoryName() : null);
        // 告警级别和名称
        alarmInfoHandleInfoVO.setAlarmLevel(alarmInfo.getRuleAlarmLevel());
        alarmInfoHandleInfoVO.setAlarmLevelName(AlarmLevelEnum.getName(alarmInfo.getRuleAlarmLevel()));
        // 告警大类名称
        EntityCategory parentEntityCategory = entityCategoryClient.getCategory(alarmInfo.getParentRuleCategoryId()).getData(); // TODO 等缓存改造好从缓存取
        alarmInfoHandleInfoVO.setAlarmCatalogName(parentEntityCategory.getCategoryName());
        // 告警信息
        alarmInfoHandleInfoVO.setAlarmMessage(alarmInfo.getAlarmMessage());
        // 处理状态名称
        alarmInfoHandleInfoVO.setIsHandleName(AlarmHandledName.getName(alarmInfo.getIsHandle()));
        return alarmInfoHandleInfoVO;
    }

    /**
     * 告警信息查询，不分页
     *
     * @param alarmInfoQueryDTO
     * @return
     */
    @Override
    public List<AlarmInfoHandleInfoVO> listAlarmHandleInfoNoPage(AlarmInfoQueryDTO alarmInfoQueryDTO) {
        logger.info("alarmInfoQueryDTO：" + alarmInfoQueryDTO.toString());
        org.springframework.data.mongodb.core.query.Query query = constructQueryWrapper4MongoDB(alarmInfoQueryDTO);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "alarmTime"))); // 默认时间倒序
        // 限制查询结果数量
        List<AlarmInfoMongoDBVO> mongoAlarmList = Optional.ofNullable(alarmInfoQueryDTO.getAlarmNum())
                .map(a -> mongoTemplate.find(query.limit(alarmInfoQueryDTO.getAlarmNum()), AlarmInfoMongoDBVO.class, AlarmConstant.MONGODB_ALARM_INFO))
                .orElseGet(() -> mongoTemplate.find(query, AlarmInfoMongoDBVO.class, AlarmConstant.MONGODB_ALARM_INFO));
        List<AlarmInfoHandleInfoVO> targetList = new ArrayList<>();
        mongoAlarmList.forEach(source -> {
            AlarmInfoHandleInfoVO targetVO = new AlarmInfoHandleInfoVO();
            BeanUtil.copyProperties(source, targetVO, "alarmTime", "createTime", "updateTime");
            targetVO.setAlarmTime(source.getAlarmTime() != null ? new Timestamp(source.getAlarmTime()) : null);
            targetVO.setCreateTime(source.getCreateTime() != null ? new Date(source.getCreateTime()) : null);
            targetVO.setUpdateTime(source.getUpdateTime() != null ? new Date(source.getUpdateTime()) : null);
            targetVO.setId(source.getAlarmId());
            targetList.add(targetVO);
        });
        return targetList;
        // mongo没查到不去表里查
		/*QueryWrapper<AlarmInfo> queryWrapper = constructQueryWrapperNew(alarmInfoQueryDTO);
		if (queryWrapper.getExpression().getNormal().size() > 0) {
			List<AlarmInfo> alarmInfoList  = this.list(queryWrapper);
			return transferAlarmInfoNew(alarmInfoList);
		}
		return Collections.emptyList();*/
    }

    /**
     * 告警信息查询，分页
     *
     * @param alarmInfoQueryDTO
     * @param page
     * @return
     */
    @Override
    public IPage<AlarmInfoHandleInfoVO> listAlarmHandleInfoPage(AlarmInfoQueryDTO alarmInfoQueryDTO, Query page) {
        org.springframework.data.mongodb.core.query.Query query = constructQueryWrapper4MongoDB(alarmInfoQueryDTO);
        Integer current = ObjectUtil.isNotEmpty(page.getCurrent()) ? page.getCurrent() : 1;
        Integer size = ObjectUtil.isNotEmpty(page.getSize()) ? page.getSize() : 10;
        query.skip(size * (current - 1)).limit(size);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "alarmTime"))); // 默认时间倒序
        List<AlarmInfoMongoDBVO> alarmInfoMongoDBVOS = mongoTemplate.find(query, AlarmInfoMongoDBVO.class, AlarmConstant.MONGODB_ALARM_INFO);
        long count = mongoTemplate.count(query, AlarmInfoMongoDBVO.class, AlarmConstant.MONGODB_ALARM_INFO);
        List<AlarmInfoHandleInfoVO> targetList = new ArrayList<>();
        alarmInfoMongoDBVOS.forEach(source -> {
            AlarmInfoHandleInfoVO targetVO = new AlarmInfoHandleInfoVO();
            BeanUtil.copyProperties(source, targetVO, "alarmTime", "createTime", "updateTime");
            targetVO.setAlarmTime(source.getAlarmTime() != null ? new Timestamp(source.getAlarmTime()) : null);
            targetVO.setCreateTime(source.getCreateTime() != null ? new Date(source.getCreateTime()) : null);
            targetVO.setUpdateTime(source.getUpdateTime() != null ? new Date(source.getUpdateTime()) : null);
            targetVO.setId(source.getAlarmId());
            targetList.add(targetVO);
        });
        IPage<AlarmInfoHandleInfoVO> alarmInfoHandlePages;
		/*QueryWrapper<AlarmInfo> queryWrapper = constructQueryWrapperNew(alarmInfoQueryDTO);
		IPage<AlarmInfo> alarmInfoPages;
		if (queryWrapper.getExpression().getNormal().size() > 0) {
			alarmInfoPages = this.page(Condition.getPage(page), queryWrapper);
			List<AlarmInfoHandleInfoVO> alarmInfoHandleInfoVOList = transferAlarmInfoNew(alarmInfoPages.getRecords());
			alarmInfoHandlePages = new Page<>(alarmInfoPages.getCurrent(), alarmInfoPages.getSize(), alarmInfoPages.getTotal());
			alarmInfoHandlePages.setRecords(alarmInfoHandleInfoVOList);
			return alarmInfoHandlePages;
		}*/
        alarmInfoHandlePages = new Page<>(current, size, count);
        alarmInfoHandlePages.setRecords(targetList);
        return alarmInfoHandlePages;
    }


    /**
     * 按条件统计告警数量
     *
     * @param alarmInfoQueryDTO
     * @return
     */
    @Override
    public Long countAlarmInfoByCondition(AlarmInfoQueryDTO alarmInfoQueryDTO) {
        org.springframework.data.mongodb.core.query.Query query = constructQueryWrapper4MongoDB(alarmInfoQueryDTO);
        return mongoTemplate.count(query, AlarmInfoMongoDBVO.class, AlarmConstant.MONGODB_ALARM_INFO);
    }


    /**
     * 导出车辆或者人员告警信息
     *
     * @param alarmInfoQueryDTO
     */
    @Override
    public void exportAlarmInfo(AlarmInfoQueryDTO alarmInfoQueryDTO) {
        Long entityCategoryId = alarmInfoQueryDTO.getEntityCategoryId();
        Long vehicleId = alarmInfoQueryDTO.getVehicleId();
        Long personId = alarmInfoQueryDTO.getPersonId();
        String entityCategoryCode = null;
        if (Objects.nonNull(vehicleId)) {
            entityCategoryCode = "VA";
        } else if (Objects.nonNull(personId)) {
            entityCategoryCode = "PA";
        } else if (Objects.nonNull(entityCategoryId)) {
            entityCategoryCode = entityCategoryClient.getCategoryCode(Long.valueOf(entityCategoryId)).getData();
        }
        if (entityCategoryCode == null || "".equals(entityCategoryCode)) {
            throw new ServiceException("告警信息（车辆或者人员）类型不确定！");
        }
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        List modelList = null;
        List<AlarmInfoHandleInfoVO> alarmInfoHandleInfoVOS = listAlarmHandleInfoNoPage(alarmInfoQueryDTO);
        if (entityCategoryCode.startsWith("VA")) {
            modelList = new ArrayList<VehicleAlarmInfoExcelModel>();
            for (AlarmInfoHandleInfoVO alarmInfoHandleInfoVO : alarmInfoHandleInfoVOS) {
                VehicleAlarmInfoExcelModel model = new VehicleAlarmInfoExcelModel();
                try {
                    String alarmTime = TimeUtil.getFormattedDate(alarmInfoHandleInfoVO.getAlarmTime(), TimeUtil.YYYY_MM_DD_HH_MM_SS);
                    model.setAlarmTime(alarmTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String vehicle = alarmInfoHandleInfoVO.getPlateNumber() + "(" + alarmInfoHandleInfoVO.getVehicleCategoryName() + ")";
                model.setVehicle(vehicle);
                model.setAlarmName(alarmInfoHandleInfoVO.getAlarmName());
                model.setAlarmType(alarmInfoHandleInfoVO.getAlarmTypeName());
                model.setAlarmLevel(alarmInfoHandleInfoVO.getAlarmLevelName());
                model.setAlarmMessage(alarmInfoHandleInfoVO.getAlarmMessage());
                model.setAlarmStatus(alarmInfoHandleInfoVO.getIsHandleName());
                modelList.add(model);
            }
            ;
        } else if (entityCategoryCode.startsWith("PA")) {
            modelList = new ArrayList<PersonAlarmInfoExcelModel>();
            for (AlarmInfoHandleInfoVO alarmInfoHandleInfoVO : alarmInfoHandleInfoVOS) {
                PersonAlarmInfoExcelModel model = new PersonAlarmInfoExcelModel();
                try {
                    String alarmTime = TimeUtil.getFormattedDate(alarmInfoHandleInfoVO.getAlarmTime(), TimeUtil.YYYY_MM_DD_HH_MM_SS);
                    model.setAlarmTime(alarmTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String person = alarmInfoHandleInfoVO.getPersonName() + "(" + alarmInfoHandleInfoVO.getJobNumber() + ")";
                model.setPerson(person);
                model.setAlarmName(alarmInfoHandleInfoVO.getAlarmName());
                model.setAlarmType(alarmInfoHandleInfoVO.getAlarmTypeName());
                model.setAlarmLevel(alarmInfoHandleInfoVO.getAlarmLevelName());
                model.setAlarmMessage(alarmInfoHandleInfoVO.getAlarmMessage());
                model.setAlarmStatus(alarmInfoHandleInfoVO.getIsHandleName());
                modelList.add(model);
            }
            ;
        }
        OutputStream out = null;
        try {
            response.reset(); // 清除buffer缓存
            // 指定下载的文件名
            String fileName = "";
            Sheet sheet1;
            if (entityCategoryCode.startsWith("VA")) {
                sheet1 = new Sheet(1, 0, VehicleAlarmInfoExcelModel.class);
                fileName = "VehicleInfoList.xlsx";
            } else {
                sheet1 = new Sheet(1, 0, PersonAlarmInfoExcelModel.class);
                fileName = "PersonInfoList.xlsx";
            }
            out = response.getOutputStream();
            response.setContentType("application/x-msdownload;charset=utf-8");
            response.setHeader("Content-disposition", "attachment;filename= " + URLEncoder.encode(fileName, "UTF-8"));
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            sheet1.setSheetName("sheet1");
            writer.write(modelList, sheet1);
            writer.finish();

        } catch (IOException e) {
//            logger.error(e.getMessage(), e);
            throw new ServiceException("导出告警信息异常");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
//            	logger.error(e.getMessage(), e);
                throw new ServiceException("导出告警信息异常");
            }
        }
    }

    /**
     * 查询当天告警信息总数
     *
     * @return
     */
    @Override
    public Integer countAlarmInfoAmount(AlarmInfo alarmInfo) {
//        QueryWrapper<AlarmInfo> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().between(AlarmInfo::getAlarmTime, TimeUtil.getStartTime(new Date()), TimeUtil.getSysDate()).eq(AlarmInfo::getTenantId, tenantId);
//        return this.count(queryWrapper);
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        if (StringUtils.isNotBlank(alarmInfo.getTenantId())) {
            query.addCriteria(Criteria.where("tenantId").is(alarmInfo.getTenantId()));
        } else {
            query.addCriteria(Criteria.where("tenantId").is(AuthUtil.getTenantId()));
        }

        if (ObjectUtil.isNotEmpty(alarmInfo.getParentRuleCategoryId())) {
            query.addCriteria(Criteria.where("parentRuleCategoryId").is(alarmInfo.getParentRuleCategoryId()));
        }

        if (ObjectUtil.isNotEmpty(alarmInfo.getIsHandle())) {
            query.addCriteria(Criteria.where("isHandle").is(alarmInfo.getIsHandle()));
        }

        query.addCriteria(Criteria.where("alarmTime").gte(TimeUtil.getStartTime(new Date()).getTime()));
        long count = mongoTemplate.count(query, AlarmInfoMongoDBVO.class, AlarmConstant.MONGODB_ALARM_INFO);
        return Integer.valueOf(String.valueOf(count));
    }

    @Override
    public Integer countAlarmInfoAmountByEntityIds(AlarmInfoCountDTO alarmInfoCountDTO) {
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("tenantId").is(alarmInfoCountDTO.getTenantId()));

        query.addCriteria(Criteria.where("alarmTime").gte(TimeUtil.getStartTime(new Date()).getTime()));

        if (ObjectUtil.isNotEmpty(alarmInfoCountDTO.getEntityIds())) {
            query.addCriteria(Criteria.where("entityId").in(alarmInfoCountDTO.getEntityIds()));
        }

        if (ObjectUtil.isNotEmpty(alarmInfoCountDTO.getIsHandle())) {
            query.addCriteria(Criteria.where("isHandle").is(alarmInfoCountDTO.getIsHandle()));
        }
        long count = mongoTemplate.count(query, AlarmInfoMongoDBVO.class, AlarmConstant.MONGODB_ALARM_INFO);
        return (int) count;
    }

    @Override
    public List<AlarmInfoScreenViewVO> getLastAlarmInfosDaily(Long alarmNum, String tenantId) {
        List<AlarmInfoScreenViewVO> alarmInfoScreenViewVOList = new ArrayList<AlarmInfoScreenViewVO>();
//
//        QueryWrapper<AlarmInfo> wrapper = new QueryWrapper<AlarmInfo>();
//        wrapper.lambda().ge(AlarmInfo::getAlarmTime, TimeUtil.getStartTime(new Date()).getTime());
//        wrapper.lambda().eq(AlarmInfo::getTenantId, tenantId);
//        wrapper.lambda().eq(AlarmInfo::getIsHandle,AlarmConstant.IsHandle.HANDLED_NO);
//        wrapper.lambda().orderByDesc(AlarmInfo::getAlarmTime);
//        if (Objects.nonNull(alarmNum)) {
//            wrapper.last("limit 0 , " + alarmNum);
//        }
//        List<AlarmInfo> alarmInfoList = baseMapper.selectList(wrapper);

        AlarmInfoQueryDTO alarmInfoQueryDTO = new AlarmInfoQueryDTO();
        Long startTime = TimeUtil.getStartTime(new Date()).getTime();
        Long endTime = TimeUtil.getSysDate().getTime();
        alarmInfoQueryDTO.setStartTime(startTime);
        alarmInfoQueryDTO.setEndTime(endTime);
        alarmInfoQueryDTO.setIsHandle(AlarmConstant.IsHandle.HANDLED_NO);

        alarmInfoQueryDTO.setAlarmNum(alarmNum.intValue());
        alarmInfoQueryDTO.setTenantId(tenantId);
        logger.info("alarmInfoQueryDTO：" + alarmInfoQueryDTO.toString());

        List<AlarmInfoHandleInfoVO> alarmInfoHandleInfoVOList = this.listAlarmHandleInfoNoPage(alarmInfoQueryDTO);

        alarmInfoHandleInfoVOList.forEach(alarmInfo -> {
            AlarmInfoScreenViewVO alarmInfoScreenViewVO = new AlarmInfoScreenViewVO();
            alarmInfoScreenViewVO.setAlarmTime(TimeUtil.getYYYY_MM_DD_HH_MM_SS(alarmInfo.getAlarmTime()));
            if (alarmInfo.getEntityType().equals(CommonConstant.ENTITY_TYPE.PERSON)) {
                alarmInfoScreenViewVO.setAlarmEntity(alarmInfo.getEntityName() + "(" + alarmInfo.getEntityDefine() + ")");
            } else {
                alarmInfoScreenViewVO.setAlarmEntity(alarmInfo.getEntityName());
            }
            if (ObjectUtil.isNotEmpty(alarmInfo.getRegionId())) {
                alarmInfoScreenViewVO.setAlarmRegion(alarmInfo.getRegionId());
                alarmInfoScreenViewVO.setAlarmRegionName(sysClient.getRegion(Long.parseLong(alarmInfo.getRegionId())).getData().getRegionName());

            }
            alarmInfoScreenViewVO.setAlarmMessage(alarmInfo.getRuleName() + ":" + alarmInfo.getAlarmMessage());
            String ruleCategoryName = entityCategoryClient.getCategoryName(alarmInfo.getRuleCategoryId()).getData();
            alarmInfoScreenViewVO.setAlarmTypeName(ruleCategoryName);
            alarmInfoScreenViewVOList.add(alarmInfoScreenViewVO);
        });
        return alarmInfoScreenViewVOList;
    }

    @Override
    public void batchHandle(AlarmInfoHandleResultVO alarmInfoHandleResultVO) {
        @NotEmpty(message = "告警信息id不能为空") String alarmInfoIds = alarmInfoHandleResultVO.getAlarmInfoIds();
        List<Long> alarmInfoIdList = Func.toLongList(alarmInfoIds);
        alarmInfoIdList.forEach(alarmInfoId -> {
            AlarmInfo alarmInfo = new AlarmInfo();
            alarmInfo.setId(alarmInfoId);
            alarmInfo.setIsHandle(AlarmConstant.IsHandle.HANDLED_YES);
            alarmInfo.setAlarmCheck(alarmInfoHandleResultVO.getAlarmCheck());
            alarmInfo.setCheckRemark(alarmInfoHandleResultVO.getCheckRemark());
            alarmInfo.setInformType(alarmInfoHandleResultVO.getInformTypes());
            this.updateById(alarmInfo);
            this.updateMongoDBByAlarmId(alarmInfo);
        });
        //bigScreenDataClient.updateBigscreenAlarmListRedis(AuthUtil.getTenantId());
        //bigScreenDataClient.updateBigscreenAlarmAmountRedis(AuthUtil.getTenantId());
        //homeDataClient.updateHomeAlarmListRedis(AuthUtil.getTenantId());
        //homeDataClient.updateHomeCountRedis(AuthUtil.getTenantId());
        String informTypes = alarmInfoHandleResultVO.getInformTypes();
        if (StrUtil.isNotBlank(informTypes) && informTypes.contains(String.valueOf(InformTypeEnum.EMAIL.getIndex()))) {
            if (!CollectionUtils.isEmpty(alarmInfoIdList)) {
                org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
                query.addCriteria(Criteria.where("alarmId").in(alarmInfoIdList));
                List<AlarmInfoMongoDBVO> alarmInfoMongoDBVOS = mongoTemplate.find(query, AlarmInfoMongoDBVO.class, AlarmConstant.MONGODB_ALARM_INFO);
                alarmInfoMongoDBVOS.forEach(alarmInfoMongoDBVO -> {
                    Set<String> userEmails = new HashSet<>();
                    if (alarmInfoMongoDBVO.getEntityType().equals(CommonConstant.ENTITY_TYPE.VEHICLE)) {
                        //如果是车，那么就要查发生告警的时候驾驶员
                        Person person = personVehicleRelClient.getCurrentDriver(alarmInfoMongoDBVO.getEntityId()).getData();
                        if (ObjectUtil.isNotEmpty(person) && ObjectUtil.isNotEmpty(person.getId())) {
                            if (StrUtil.isNotBlank(person.getEmail())) {
                                userEmails.add(person.getEmail());
                            }
                        }
                    } else {
                        Person person = personClient.getPerson(alarmInfoMongoDBVO.getEntityId()).getData();
                        if (person != null) {
                            if (StrUtil.isNotBlank(person.getEmail())) {
                                userEmails.add(person.getEmail());
                            }
                        }
                    }
                    //系统管理员
                    Role adminRole = sysClient.getTenantAdminRole(alarmInfoMongoDBVO.getTenantId()).getData();
                    if (ObjectUtil.isNotEmpty(adminRole) && ObjectUtil.isNotEmpty(adminRole.getId())) {
                        List<User> userList = userClient.getRoleUser(adminRole.getId().toString(), alarmInfoMongoDBVO.getTenantId()).getData();
                        userList.forEach(user -> {
                            if (StrUtil.isNotBlank(user.getEmail())) {
                                userEmails.add(user.getEmail());
                            }
                        });
                    }
                    // 发邮件提醒
                    EmailDTO emailDTO = new EmailDTO();
                    if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(alarmInfoMongoDBVO.getEntityType())) {
                        emailDTO.setSubject(StrUtil.format("告警：{}[{}]", alarmInfoMongoDBVO.getEntityDefine(), alarmInfoMongoDBVO.getEntityName())); // 告警：洒水车[苏A12345]
                    } else if (CommonConstant.ENTITY_TYPE.PERSON.equals(alarmInfoMongoDBVO.getEntityType())) {
                        emailDTO.setSubject(StrUtil.format("告警：{}[工号：{}]", alarmInfoMongoDBVO.getEntityName(), alarmInfoMongoDBVO.getEntityDefine())); // 告警：人名[工号：12345]
                    }
                    emailDTO.setContent(alarmInfoMongoDBVO.getRuleName() + "，" + alarmInfoMongoDBVO.getAlarmMessage());
                    userEmails.forEach(emailAddress -> {
                        emailDTO.setReceiver(emailAddress);
                        pushcClient.sendEmail(emailDTO);
                    });
                });
            }
        }
    }

    /**
     * --bootstrap-server 47.99.209.234:9092 --topic lc_ipsfas
     * 测试用
     */
    @Deprecated
    @Override
    public void consumeInitiativeAlarm() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "47.99.209.234:9092");
        properties.put("group.id", "lc-ipsfas-group");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "latest");
        properties.put("session.timeout.ms", "30000");
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties, new StringDeserializer(), new StringDeserializer());
        kafkaConsumer.subscribe(Collections.singletonList("lc_ipsfas"), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {

            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                Map<TopicPartition, Long> beginningOffset = kafkaConsumer.beginningOffsets(collection);
                //读取历史数据 --from-beginning
                for (Map.Entry<TopicPartition, Long> entry : beginningOffset.entrySet()) {
                    // 基于seekToBeginning方法
                    kafkaConsumer.seekToBeginning(collection);
                }
            }
        });
        try {
            Duration duration = Duration.ofMillis(100);
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(duration);
                long count = 0;
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        System.out.println(record.value());
//						this.handlerInitiativeAlarm(JSONUtil.parseObj(record.value()));
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    kafkaConsumer.commitAsync();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                kafkaConsumer.commitSync();
            } finally {
                kafkaConsumer.close();
            }
        }
    }

    /**
     * @param initiativeAlarmInfo
     */
    @Override
    public void handlerInitiativeAlarm(JSONObject initiativeAlarmInfo) throws Exception {
        String simCode2 = initiativeAlarmInfo.getString("phone");
        if (StrUtil.isBlank(simCode2)) {
            return;
        }
        SimRel simRel = simClient.getSimRelBySimCode2(simCode2).getData();
        if (simRel == null || StrUtil.isBlank(simRel.getDeviceId())) {
            log.error(StrUtil.format("主动安全告警信息中根据simCode2[{}]没有找到关联sim卡Id或设备Id", simCode2));
            return;
        }
        DeviceInfo deviceInfo = deviceClient.getDeviceById(simRel.getDeviceId()).getData();
        if (deviceInfo == null || StrUtil.isBlank(deviceInfo.getDeviceCode())) {
            log.error(StrUtil.format("主动安全告警中根据设备Id[{}]没有找到关联设备信息", simRel.getDeviceId()));
            return;
        }
        String deviceCode = deviceInfo.getDeviceCode();
        String tenantId = deviceInfo.getTenantId();
        LambdaQueryWrapper<AlarmRuleInfo> ruleInfoQueryWrapper = new LambdaQueryWrapper<>();
        ruleInfoQueryWrapper.eq(AlarmRuleInfo::getTenantId, tenantId);
        ruleInfoQueryWrapper.eq(AlarmRuleInfo::getEntityCategoryCode, AlarmConstant.VEHICLE_INITIATIVE_ALARM);
        ruleInfoQueryWrapper.eq(AlarmRuleInfo::getStatus, AlarmConstant.Status.YES);
        AlarmRuleInfo initiativeAlarmRule = alarmRuleInfoService.getOne(ruleInfoQueryWrapper);
        if (initiativeAlarmRule == null || initiativeAlarmRule.getId() == null) {
            log.error(StrUtil.format("租户[{}]下没有配置主动安全告警规则或规则未启用", tenantId));
            return;
        }
        // 构造数据复用原有的告警信息处理方法
        String uuid = initiativeAlarmInfo.getString("id");
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setUuid(uuid);
        alarmInfo.setDeviceCode(deviceCode);
        alarmInfo.setRuleId(initiativeAlarmRule.getId());
        alarmInfo.setData(JSONUtil.toJsonStr(initiativeAlarmInfo));
        this.handleBigDataAlarmInfo(alarmInfo);
    }
}
