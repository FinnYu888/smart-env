package com.ai.apac.smartenv.pushc.feign;

import com.ai.apac.smartenv.pushc.dto.AlarmEventDTO;
import com.ai.apac.smartenv.pushc.dto.AssessEventDTO;
import com.ai.apac.smartenv.pushc.dto.EmailDTO;
import com.ai.apac.smartenv.pushc.mq.PushcProducerSource;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/5/25 9:01 下午
 **/
@ApiIgnore
@RestController
@RequiredArgsConstructor
@Slf4j
public class PushcClient implements IPushcClient {

    @Autowired
    private PushcProducerSource pushcProducerSource;

    /**
     * 发送邮件
     *
     * @param emailDTO 邮件对象
     * @return Menu
     */
    @Override
    @PostMapping(SEND_EMAIL)
    @ApiOperation("发送邮件")
    public R sendEmail(EmailDTO emailDTO) {
        //创建 Spring Message 对象
        Message<EmailDTO> springMessage = MessageBuilder.withPayload(emailDTO)
                .build();
        // <3> 发送消息
        boolean result = pushcProducerSource.emailChannel().send(springMessage);
        log.info("向消息队列推送邮件成功:{}", JSON.toJSONString(emailDTO));
        return R.status(result);
    }

    @Override
    @PostMapping(SEND_ASSESS_EVENT_BY_MP)
    @ApiOperation("微信公众号发送考核事件通知")
    public R sendAssessEventByMP(AssessEventDTO assessEventDTO) {
        //创建 Spring Message 对象
        Message<AssessEventDTO> springMessage = MessageBuilder.withPayload(assessEventDTO)
                .build();
        // <3> 发送消息
        boolean result = pushcProducerSource.accessEventChannel().send(springMessage);
        log.info("向消息队列推送事件提醒成功:{}", JSON.toJSONString(assessEventDTO));
        return R.status(result);
    }

    /**
     * 通过微信公众号发送告警通知
     *
     * @param alarmEventDTO
     * @return
     */
    @Override
    @PostMapping(SEND_ALARM_BY_MP)
    @ApiOperation("微信公众号发送告警通知")
    public R sendAlarmByMP(AlarmEventDTO alarmEventDTO) {
        //创建 Spring Message 对象
        Message<AlarmEventDTO> springMessage = MessageBuilder.withPayload(alarmEventDTO)
                .build();
        // <3> 发送消息
        boolean result = pushcProducerSource.alarmEventChannel().send(springMessage);
        log.info("向消息队列推送事件提醒成功:{}", JSON.toJSONString(alarmEventDTO));
        return R.status(result);
    }
}
