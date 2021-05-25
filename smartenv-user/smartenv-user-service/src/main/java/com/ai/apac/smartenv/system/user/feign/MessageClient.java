package com.ai.apac.smartenv.system.user.feign;

import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.common.constant.MessageConstant;
import com.ai.apac.smartenv.system.user.dto.UserMessageDTO;
import com.ai.apac.smartenv.system.user.service.IMessageService;
import com.ai.apac.smartenv.system.user.service.IUserService;
import com.ai.apac.smartenv.system.user.vo.UserMessageVO;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName MessageClient
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/3 11:07
 * @Version 1.0
 */
@RestController
@AllArgsConstructor
public class MessageClient implements IMessageClient {

    private IMessageService messageService;

    @Override
    public R<UserMessageDTO> unReadMessage(String tenantId, String userId) {
        UserMessageVO userMessageVO1 = messageService.listMessage(MessageConstant.MessageType.ALARM_MESSAGE,"0",1,10,userId,tenantId);
        UserMessageDTO userMessageDTO = BeanUtil.copy(userMessageVO1, UserMessageDTO.class);
        userMessageDTO.setAlarmMessageList(userMessageVO1.getMessageList());
        UserMessageVO userMessageVO2 = messageService.listMessage(MessageConstant.MessageType.EVENT_MESSAGE,"0",1,10,userId,tenantId);
        userMessageDTO.setEventMessageList(userMessageVO2.getMessageList());
        UserMessageVO userMessageVO3 = messageService.listMessage(MessageConstant.MessageType.ANNOUN_MESSAGE,"0",1,10,userId,tenantId);
        userMessageDTO.setAnnounMessageList(userMessageVO3.getMessageList());
        return R.data(userMessageDTO);
    }
}
