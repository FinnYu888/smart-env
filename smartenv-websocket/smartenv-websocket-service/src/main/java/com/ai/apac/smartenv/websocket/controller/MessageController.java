package com.ai.apac.smartenv.websocket.controller;

import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.common.BaseWebSocketResp;
import com.ai.apac.smartenv.websocket.module.main.vo.HomePageDataCountVO;
import com.ai.apac.smartenv.websocket.module.message.vo.UnReadMessageCountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.IEventService;
import com.ai.apac.smartenv.websocket.service.IMessageService;
import com.ai.apac.smartenv.websocket.util.WebSocketUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.boot.ctrl.BladeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName MessageController
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/3 10:31
 * @Version 1.0
 */
@RestController
@Api("Message Websocket")
@Slf4j
public class MessageController extends BladeController {

    @Autowired
    private IMessageService messageService;

    public static final String GET_UNREAD_MESSAGE_COUNT = "message.getUnReadMessageCount";


    @MessageMapping(GET_UNREAD_MESSAGE_COUNT)
    @SendTo(WebSocketConsts.PUSH_MESSAGE)
    public BaseWebSocketResp<UnReadMessageCountVO> getUnReadMessageCount(SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_UNREAD_MESSAGE_COUNT, WebSocketConsts.PUSH_MESSAGE,
                "0/5 * * * * ?", null);
        messageService.pushUnReadMessage(task);
        UnReadMessageCountVO unReadMessageCountVO = new UnReadMessageCountVO();
        unReadMessageCountVO.setTopicName(WebSocketConsts.PUSH_MESSAGE);
        unReadMessageCountVO.setActionName(GET_UNREAD_MESSAGE_COUNT);

        BaseWebSocketResp<UnReadMessageCountVO> result = BaseWebSocketResp.data(unReadMessageCountVO);
        return result;
    }
}
