package com.ai.apac.smartenv.websocket.module.message.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName UnReadMessageCountVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/3 10:39
 * @Version 1.0
 */
@Data
public class UnReadMessageCountVO extends WebSocketDTO {

    private static final long serialVersionUID = 1L;

    private String userId;

    private String unReadAlarmCount;

    private String unReadEventCount;

    private String unReadAnnounCount;

    private List<RelMessageVO> unReadAlarmMessageList;

    private List<RelMessageVO> unReadEventMessageList;

    private List<RelMessageVO> unReadAnnounMessageList;
}
