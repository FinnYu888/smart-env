package com.ai.apac.smartenv.alarm.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: AlarmSendMessageController
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/1/24
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/1/24  13:04    panfeng          v1.0.0             修改原因
 */
@RestController
@AllArgsConstructor
@RequestMapping("/alarmMessageSend")
@Api(value = "告警白名单表", tags = "告警白名单表接口")
public class AlarmSendMessageController {

    @Autowired
    private AlarmWebsocketController controller;


    @PostMapping("/send")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入alarmRuleWhitelist")
    public void sendMessageToClient(@RequestBody String message) throws IOException {


        controller.sendTextMessageToWebSocket("",message);


    }

}
