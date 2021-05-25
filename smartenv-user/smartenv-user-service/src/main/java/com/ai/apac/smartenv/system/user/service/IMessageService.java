package com.ai.apac.smartenv.system.user.service;

import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.system.user.dto.MessageInfoDTO;
import com.ai.apac.smartenv.system.user.dto.RelMessageDTO;
import com.ai.apac.smartenv.system.user.dto.UserMessageDTO;
import com.ai.apac.smartenv.system.user.vo.UserMessageVO;

import java.util.List;

/**
 * @ClassName IMessageService
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/2 14:58
 * @Version 1.0
 */
public interface IMessageService {


    UserMessageVO listMessage(String messageType, String isRead, Integer current, Integer size, String userId, String tenantId);

    String countMessage(String messageType,String isRead);

    UserMessageDTO getUserMessage();

    void updateMessage(String messageId,String messageType,String acceptChannelType);

    void cleanMessage(String messageType);
}
