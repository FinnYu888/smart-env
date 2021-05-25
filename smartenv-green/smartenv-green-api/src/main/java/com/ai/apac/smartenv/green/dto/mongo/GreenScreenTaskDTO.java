package com.ai.apac.smartenv.green.dto.mongo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName GreenScreenTaskDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/22 17:15
 * @Version 1.0
 */
@Data
public class GreenScreenTaskDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    String taskId;
    String taskName;
    String count;
    String lastTaskTime;

}
