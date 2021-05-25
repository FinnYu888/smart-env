package com.ai.apac.smartenv.websocket.feign;

import com.ai.apac.smartenv.websocket.module.notification.dto.NotificationInfo;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/11 5:50 下午
 **/
@Component
public class INotificationClientFallback implements INotificationClient {

    @Override
    public R pushNotification(NotificationInfo notificationInfo) {
        return R.fail("推送通知失败");
    }
}
