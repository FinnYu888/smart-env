package com.ai.apac.smartenv.green.dto.mongo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName GreenScreenTaskDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/22 17:15
 * @Version 1.0
 */
@Data
public class GreenScreenTasksDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    String tenantId;
    List<GreenScreenTaskDTO> lastDaysTaskCount;

}
