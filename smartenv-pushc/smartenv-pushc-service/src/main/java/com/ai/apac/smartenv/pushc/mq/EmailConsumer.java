package com.ai.apac.smartenv.pushc.mq;

import com.ai.apac.smartenv.common.constant.PushcConstant;
import com.ai.apac.smartenv.pushc.PushcApplication;
import com.ai.apac.smartenv.pushc.dto.EmailDTO;
import com.ai.apac.smartenv.pushc.entity.NoticeInfo;
import com.ai.apac.smartenv.pushc.service.IMailService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/5/24 10:19 下午
 **/
@Component
@Slf4j
@EnableBinding(PushcConsumerSource.class)
public class EmailConsumer {

    @Autowired
    private IMailService mailService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @StreamListener(PushcConsumerSource.EMAIL_CHANNEL_INPUT)
    public void onMessage(@Payload EmailDTO emailDTO) {
        log.info("[EmailConsumer.onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), JSON.toJSONString(emailDTO));
        NoticeInfo noticeInfo = new NoticeInfo();
        BeanUtils.copyProperties(emailDTO, noticeInfo);
        Long noticeId = IdWorker.getId();
        noticeInfo.setNoticeChannel(PushcConstant.NoticeChannel.EMAIL);
        noticeInfo.setNoticeId(noticeId);
        noticeInfo.setStatus(PushcConstant.SendStatus.PENDING);
        noticeInfo.setCreateTime(new Date());
        mongoTemplate.save(noticeInfo);

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(noticeId));
        Update update = new Update();

        try {
            mailService.sendHtmlMail(emailDTO.getReceiver(), emailDTO.getSubject(), emailDTO.getContent());
            update.set("status", PushcConstant.SendStatus.SUCCESS);
        } catch (Exception ex) {
            update.set("update_time", new Date());
            update.set("status", PushcConstant.SendStatus.FAIL);
        } finally {
            update.set("send_time", new Date());
            update.set("update_time", new Date());
            mongoTemplate.findAndModify(query, update, NoticeInfo.class, PushcConstant.MONGODB_NOTICE_INFO);
        }
    }
}
