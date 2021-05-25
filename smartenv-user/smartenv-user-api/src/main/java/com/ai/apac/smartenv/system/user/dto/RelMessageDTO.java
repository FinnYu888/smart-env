package com.ai.apac.smartenv.system.user.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @ClassName RelMessageDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/2 15:49
 * @Version 1.0
 */
@Data
public class RelMessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String messageId;

    private String messageType;

    private String messageKind;

    private String messageTitle;

    private String messageContent;

    private boolean isRead;

    private String isDeleted;

    private Long pushTime;

    private String readChannel;
}
