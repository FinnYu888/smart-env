package com.ai.apac.smartenv.pushc.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.pushc.dto.AlarmEventDTO;
import com.ai.apac.smartenv.pushc.dto.AssessEventDTO;
import com.ai.apac.smartenv.pushc.dto.EmailDTO;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/5/25 8:53 下午
 **/
@FeignClient(
        value = ApplicationConstant.APPLICATION_PUSHC_NAME,
        fallback = IPushcFallbackClient.class
)
public interface IPushcClient {

    String API_PREFIX = "/client";
    String SEND_EMAIL = API_PREFIX + "/send-email";
    String SEND_ASSESS_EVENT_BY_MP = API_PREFIX + "/send-assessEvent-by-MP";
    String SEND_ALARM_BY_MP = API_PREFIX + "/send-alarm-by-MP";

    /**
     * 发送邮件
     *
     * @param emailDTO 邮件对象
     * @return Menu
     */
    @PostMapping(SEND_EMAIL)
    R sendEmail(@RequestBody EmailDTO emailDTO);

    /**
     * 通过微信公众号发送事件通知
     * @param assessEventDTO
     * @return
     */
    @PostMapping(SEND_ASSESS_EVENT_BY_MP)
    R sendAssessEventByMP(@RequestBody AssessEventDTO assessEventDTO);

    /**
     * 通过微信公众号发送告警通知
     * @param alarmEventDTO
     * @return
     */
    @PostMapping(SEND_ALARM_BY_MP)
    R sendAlarmByMP(@RequestBody AlarmEventDTO alarmEventDTO);

}
