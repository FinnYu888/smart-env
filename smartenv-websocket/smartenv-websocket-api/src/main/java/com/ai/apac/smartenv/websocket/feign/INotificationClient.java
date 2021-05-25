package com.ai.apac.smartenv.websocket.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.websocket.module.notification.dto.NotificationInfo;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author qianlong
 * @description 消息通知接口
 * @Date 2020/3/11 5:48 下午
 **/
@FeignClient(
        value = ApplicationConstant.APPLICATION_WEBSOCKET_NAME,
        fallback = INotificationClientFallback.class
)
public interface INotificationClient {

    String API_PREFIX = "/client";
    String PUSH_NOTIFICATION = API_PREFIX + "/push-notification";

    @PostMapping(PUSH_NOTIFICATION)
    @ResponseBody
    R pushNotification(@RequestBody NotificationInfo notificationInfo);
}
