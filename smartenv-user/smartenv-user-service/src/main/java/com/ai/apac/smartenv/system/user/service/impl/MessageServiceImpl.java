package com.ai.apac.smartenv.system.user.service.impl;

import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.common.constant.MessageConstant;
import com.ai.apac.smartenv.system.user.dto.MessageInfoDTO;
import com.ai.apac.smartenv.system.user.dto.RelMessageDTO;
import com.ai.apac.smartenv.system.user.dto.UserMessageDTO;
import com.ai.apac.smartenv.system.user.service.IMessageService;
import com.ai.apac.smartenv.system.user.service.IUserService;
import com.ai.apac.smartenv.system.user.vo.UserMessageVO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.AllArgsConstructor;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName MessageServiceImpl
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/2 14:59
 * @Version 1.0
 */
@Service
@AllArgsConstructor
public class MessageServiceImpl implements IMessageService {

    private MongoTemplate mongoTemplate;

    @Override
    public UserMessageVO listMessage(String messageType, String isRead, Integer current, Integer size, String userId, String tenantId) {
        String messageTypeStr = "announMessageList";
        if(MessageConstant.MessageType.ALARM_MESSAGE.equals(messageType)){
            messageTypeStr = "alarmMessageList";
        }else if(MessageConstant.MessageType.EVENT_MESSAGE.equals(messageType)){
            messageTypeStr = "eventMessageList";
        }

        if(!ObjectUtil.isNotEmpty(userId)){
            userId = AuthUtil.getUserId().toString();
        }

        if(!ObjectUtil.isNotEmpty(tenantId)){
            tenantId = AuthUtil.getTenantId();
        }

        UserMessageDTO res = new UserMessageDTO();
        List<AggregationOperation> operations = new ArrayList<AggregationOperation>();

        AggregationOperation match = Aggregation.match(Criteria.where("userId").is(userId));
        AggregationOperation unwind = Aggregation.unwind(messageTypeStr);
        AggregationOperation sort = Aggregation.sort(Sort.Direction.DESC, messageTypeStr+".pushTime");
        AggregationOperation skip = Aggregation.skip((current - 1) * size);
        AggregationOperation limit = Aggregation.limit(size);
        AggregationOperation group = Aggregation.group("userId", "unReadAlarmCount","unReadEventCount","unReadAnnounCount").push(messageTypeStr).as(messageTypeStr);
        operations.add(match);
        if (ObjectUtil.isNotEmpty(isRead) && "0".equals(isRead)) {
            ComparisonOperators.Eq isReadEq = ComparisonOperators.Eq.valueOf("$"+messageTypeStr+".isRead").equalToValue(false);
            operations.add(Aggregation.project("userId", "unReadAlarmCount","unReadEventCount","unReadAnnounCount", messageTypeStr).and(messageTypeStr).filter(messageTypeStr, isReadEq).as(messageTypeStr));
        }
        ComparisonOperators.Eq isDeletedEq = ComparisonOperators.Eq.valueOf("$"+messageTypeStr+".isDeleted").equalToValue("0");
        operations.add(Aggregation.project("userId", "unReadAlarmCount","unReadEventCount","unReadAnnounCount", messageTypeStr).and(messageTypeStr).filter(messageTypeStr, isDeletedEq).as(messageTypeStr));

        operations.add(unwind);
        operations.add(sort);
        operations.add(skip);
        operations.add(limit);
        operations.add(group);
        Aggregation aggregation = Aggregation.newAggregation(operations);

        List<UserMessageDTO> userMessageDTOList = mongoTemplate.aggregate(aggregation, "userMessage_" + tenantId, UserMessageDTO.class).getMappedResults();

        if (userMessageDTOList.size() > 0) {
            res = userMessageDTOList.get(0);
            List<RelMessageDTO> relMessageDTOList = res.getAnnounMessageList();
            if(MessageConstant.MessageType.ALARM_MESSAGE.equals(messageType)){
                relMessageDTOList = res.getAlarmMessageList();
            }else if(MessageConstant.MessageType.EVENT_MESSAGE.equals(messageType)){
                relMessageDTOList = res.getEventMessageList();
            }
            for(RelMessageDTO relMessageDTO:relMessageDTOList){
                org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
                query.addCriteria(Criteria.where("messageId").is(relMessageDTO.getMessageId()));
                MessageInfoDTO messageInfoDTO = mongoTemplate.findOne(query, MessageInfoDTO.class, "messageInfo_" + tenantId);
                relMessageDTO.setMessageTitle(messageInfoDTO.getMessageTitle());
                relMessageDTO.setMessageContent(messageInfoDTO.getMessageContent());
                relMessageDTO.setMessageKind(messageInfoDTO.getMessageKind());
            };
        }

        UserMessageVO userMessageVO = BeanUtil.copy(res, UserMessageVO.class);
        if(MessageConstant.MessageType.ALARM_MESSAGE.equals(messageType)){
            userMessageVO.setMessageList(res.getAlarmMessageList());
        }else if(MessageConstant.MessageType.EVENT_MESSAGE.equals(messageType)){
            userMessageVO.setMessageList(res.getEventMessageList());
        }else{
            userMessageVO.setMessageList(res.getAnnounMessageList());
        }
        return userMessageVO;
    }

    @Override
    public String countMessage(String messageType, String isRead) {
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("userId").is(AuthUtil.getUserId().toString()));
        UserMessageDTO userMessageDTO = mongoTemplate.findOne(query, UserMessageDTO.class, "userMessage_" + AuthUtil.getTenantId());
        if (ObjectUtil.isNotEmpty(userMessageDTO)) {
            if (MessageConstant.MessageType.ALARM_MESSAGE.equals(messageType)) {
                return "0".equals(isRead) ? userMessageDTO.getUnReadAlarmCount() : userMessageDTO.getAlarmCount();
            }
            if (MessageConstant.MessageType.EVENT_MESSAGE.equals(messageType)) {
                return "0".equals(isRead) ? userMessageDTO.getUnReadEventCount() : userMessageDTO.getEventCount();
            }
            if (MessageConstant.MessageType.ANNOUN_MESSAGE.equals(messageType)) {
                return "0".equals(isRead) ? userMessageDTO.getUnReadAnnounCount() : userMessageDTO.getAnnounCount();
            }
        }
        return "0";
    }

    @Override
    public UserMessageDTO getUserMessage() {
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("userId").is(AuthUtil.getUserId().toString()));
        return mongoTemplate.findOne(query, UserMessageDTO.class, "userMessage_" + AuthUtil.getTenantId());
    }

    @Override
    public void updateMessage(String messageId, String messageType,String acceptChannelType) {
        String messageTypeStr = "announMessageList";
        if(MessageConstant.MessageType.ALARM_MESSAGE.equals(messageType)){
            messageTypeStr = "alarmMessageList";
        }else if(MessageConstant.MessageType.EVENT_MESSAGE.equals(messageType)){
            messageTypeStr = "eventMessageList";
        }

        Update update = new Update();
        Query query = new Query(Criteria.where("userId").is(AuthUtil.getUserId().toString()));
        UserMessageDTO userMessageDTO = mongoTemplate.findOne(query, UserMessageDTO.class, "userMessage_" + AuthUtil.getTenantId());
        if (!"-1".equals(messageId)) {
            query.addCriteria(Criteria.where(messageTypeStr+".messageId").is(messageId));
            update.set(messageTypeStr+".$.isRead", true);
            update.set(messageTypeStr+".$.readChannel", acceptChannelType);
        }else{
            update.set(messageTypeStr+".$[].isRead", true);
            update.set(messageTypeStr+".$[].readChannel", acceptChannelType);
        }
        if (MessageConstant.MessageType.ALARM_MESSAGE.equals(messageType)) {
            if (!"-1".equals(messageId)) {
                update.set("unReadAlarmCount", Long.parseLong(userMessageDTO.getUnReadAlarmCount()) - 1 + "");
            } else {
                update.set("unReadAlarmCount", "0");
            }
        }
        if (MessageConstant.MessageType.EVENT_MESSAGE.equals(messageType)) {
            if (!"-1".equals(messageId)) {
                update.set("unReadEventCount", Long.parseLong(userMessageDTO.getUnReadEventCount()) - 1 + "");
            } else {
                update.set("unReadEventCount", "0");
            }

        }

        mongoTemplate.updateMulti(query, update, "userMessage_" + AuthUtil.getTenantId());

    }

    @Override
    public void cleanMessage(String messageType) {
        String messageTypeStr = "announMessageList";
        if(MessageConstant.MessageType.ALARM_MESSAGE.equals(messageType)){
            messageTypeStr = "alarmMessageList";
        }else if(MessageConstant.MessageType.EVENT_MESSAGE.equals(messageType)){
            messageTypeStr = "eventMessageList";
        }

        Update update = new Update();
        Query query = new Query(Criteria.where("userId").is(AuthUtil.getUserId().toString()));
        update.set(messageTypeStr+".$[].isDeleted", "1");

        if (MessageConstant.MessageType.ALARM_MESSAGE.equals(messageType)) {
            update.set("unReadAlarmCount", "0");
            update.set("alarmCount", "0");
        }
        if (MessageConstant.MessageType.EVENT_MESSAGE.equals(messageType)) {
            update.set("unReadEventCount", "0");
            update.set("eventCount", "0");
        }
        mongoTemplate.updateMulti(query, update, "userMessage_" + AuthUtil.getTenantId());
    }

}
