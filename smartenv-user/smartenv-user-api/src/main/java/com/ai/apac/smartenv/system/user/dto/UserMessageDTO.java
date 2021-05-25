package com.ai.apac.smartenv.system.user.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName UserMessage
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/2 15:35
 * @Version 1.0
 */
@Data
public class UserMessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;

    private String unReadAlarmCount;

    private String unReadEventCount;

    private String unReadAnnounCount;

    private String alarmCount;

    private String eventCount;

    private String announCount;

    private List<RelMessageDTO> alarmMessageList;

    private List<RelMessageDTO> eventMessageList;

    private List<RelMessageDTO> announMessageList;

}
