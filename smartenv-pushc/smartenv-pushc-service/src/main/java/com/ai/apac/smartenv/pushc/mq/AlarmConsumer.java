package com.ai.apac.smartenv.pushc.mq;

import com.ai.apac.smartenv.common.constant.PushcConstant;
import com.ai.apac.smartenv.pushc.dto.AlarmEventDTO;
import com.ai.apac.smartenv.pushc.dto.AlarmInfoDTO;
import com.ai.apac.smartenv.pushc.dto.AssessEventDTO;
import com.ai.apac.smartenv.pushc.entity.NoticeInfo;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author qianlong
 * @description 告警消息费者
 * @Date 2020/10/14 08:19 下午
 **/
@Component
@Slf4j
@EnableBinding(PushcConsumerSource.class)
public class AlarmConsumer {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PushcProducerSource pushcProducerSource;

    @StreamListener(PushcConsumerSource.ALARM_EVENT_CHANNEL_INPUT)
    public void onMessage(@Payload AlarmEventDTO alarmEventDTO) {
        log.info("[AlarmConsumer.onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), JSON.toJSONString(alarmEventDTO));

        //向微信公众号平台发送消息,通知对方发送消息
        Message<AlarmEventDTO> springMessage = MessageBuilder.withPayload(alarmEventDTO)
                .build();
        pushcProducerSource.mpAlarmEventChannel().send(springMessage);
        log.info("向微信公众平平台消息队列推送告警提醒成功:{}", JSON.toJSONString(alarmEventDTO));

        NoticeInfo noticeInfo = new NoticeInfo();
        noticeInfo.setContent(JSON.toJSONString(alarmEventDTO.getAlarmInfoDTO()));
        noticeInfo.setSubject(alarmEventDTO.getAlarmInfoDTO().getAlarmName());
        noticeInfo.setReceiver(alarmEventDTO.getUnionId());
        Long noticeId = IdWorker.getId();
        noticeInfo.setNoticeChannel(PushcConstant.NoticeChannel.WECHAT_MP);
        noticeInfo.setNoticeId(noticeId);
        noticeInfo.setStatus(PushcConstant.SendStatus.SUCCESS);
        noticeInfo.setCreateTime(new Date());
        mongoTemplate.save(noticeInfo);
    }
}
