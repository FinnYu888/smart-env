package com.ai.apac.smartenv.green.dto.mongo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName GreenScreenWorkingCountDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/22 17:45
 * @Version 1.0
 */
@Data
public class GreenScreenWorkingCountDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    String tenantId;
    String person;
    String vehicle;
}
