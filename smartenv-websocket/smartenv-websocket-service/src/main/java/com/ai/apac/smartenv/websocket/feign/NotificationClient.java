package com.ai.apac.smartenv.websocket.feign;

import com.ai.apac.smartenv.websocket.module.notification.dto.NotificationInfo;
import com.ai.apac.smartenv.websocket.service.INotificationService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/11 6:04 下午
 **/
//@ApiIgnore
@RestController
@AllArgsConstructor
public class NotificationClient implements INotificationClient {

    @Autowired
    private INotificationService notificationService;

    @Override
    @PostMapping(PUSH_NOTIFICATION)
    @ApiOperation(value = "发送通知", notes = "发送通知")
    @ResponseBody
    public R pushNotification(@RequestBody NotificationInfo notificationInfo) {
        return R.status(notificationService.pushNotification(notificationInfo));
    }
}
