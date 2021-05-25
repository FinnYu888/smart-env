package com.ai.apac.smartenv.system.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MessageInfoDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/3 11:30
 * @Version 1.0
 */
@Data
public class MessageInfoDTO  implements Serializable {
    private static final long serialVersionUID = 1L;

    private String messageKind;

    private String messageTitle;

    private String messageContent;
}
