package com.ai.apac.smartenv.system.controller;

import com.ai.apac.smartenv.pushc.dto.AlarmEventDTO;
import com.ai.apac.smartenv.pushc.dto.AssessEventDTO;
import com.ai.apac.smartenv.pushc.dto.EmailDTO;
import com.ai.apac.smartenv.pushc.feign.IPushcClient;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/5/24 10:00 下午
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/mq")
@Api(value = "RabbitMQ测试", tags = "RabbitMQ测试")
@Slf4j
public class TestMqController {


    @Autowired
    private IPushcClient pushcClient;

    @PostMapping("/testSendSimpleEmail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "测试发送简单Email消息", notes = "测试发送简单Email消息")
    public boolean testSendSimpleEmail(@RequestBody EmailDTO message) {
        pushcClient.sendEmail(message);
        return true;
        // <1> 创建 Message
//        message.setSubject("登录密码");
//        message.setReceiver("qianlong@asiainf.sg");
//        message.setContent("您的登录密码是123456");
        // <2> 创建 Spring Message 对象
//        Message<EmailDTO> springMessage = MessageBuilder.withPayload(message)
//                .build();
//        // <3> 发送消息
//        boolean result = pushcProducerSource.emailChannel().send(springMessage);
//        log.info("[testSendSimpleEmail][发送编号：[{}] 发送成功]", message.getSubject());
//        return result;
    }

    @PostMapping("/testSendAssessEvent")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "测试通过公众号发送事件提醒", notes = "测试通过公众号发送事件提醒")
    public boolean testSendEventByMP(@RequestBody AssessEventDTO message) {
        pushcClient.sendAssessEventByMP(message);
//
//        // <2> 创建 Spring Message 对象
//        Message<AssessEventDTO> springMessage = MessageBuilder.withPayload(message)
//                .build();
//        // <3> 发送消息
//        boolean result = pushcProducerSource.accessEventChannel().send(springMessage);
//        log.info("[testSendSimpleEmail][发送编号：[{}] 发送成功]", message.getUnionId());
        return true;
    }

    /**
     * 通过微信公众号发送告警通知
     *
     * @param alarmEventDTO
     * @return
     */
    @PostMapping("/testSendAlarmEvent")
    @ApiOperation("微信公众号发送告警通知")
    public R testSendAlarmByMP(@RequestBody AlarmEventDTO alarmEventDTO) {
        pushcClient.sendAlarmByMP(alarmEventDTO);
//        //创建 Spring Message 对象
//        Message<AlarmEventDTO> springMessage = MessageBuilder.withPayload(alarmEventDTO)
//                .build();
//        // <3> 发送消息
//        boolean result = pushcProducerSource.alarmEventChannel().send(springMessage);
//        log.info("向消息队列推送事件提醒成功:{}", JSON.toJSONString(alarmEventDTO));
        return R.status(true);
    }
}
