package com.ai.apac.smartenv.system.user.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @ClassName UserMessageCountVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/21 10:05
 * @Version 1.0
 */
@Data
@ApiModel(value = "UserMessageCountVO对象", description = "UserMessageCountVO对象")
public class UserMessageCountVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;

    private String unReadAlarmCount;

    private String unReadEventCount;

    private String unReadAnnounCount;

    private String alarmCount;

    private String eventCount;

    private String announCount;

}
