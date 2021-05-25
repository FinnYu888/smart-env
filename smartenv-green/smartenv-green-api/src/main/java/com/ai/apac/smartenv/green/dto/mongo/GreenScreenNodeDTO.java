package com.ai.apac.smartenv.green.dto.mongo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName GreenScreenNodeDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/22 17:50
 * @Version 1.0
 */
@Data
public class GreenScreenNodeDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    String lng;
    String lat;
}
