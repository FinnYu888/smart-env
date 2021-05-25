package com.ai.apac.smartenv.websocket.module.main.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName WorkingOffEntityVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/28 17:02
 * @Version 1.0
 */
@Data
public class WorkingOffEntityVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("实体名称")
    private String entityName;

    @ApiModelProperty("实体类型")
    private String entityType;

    @ApiModelProperty("地区")
    private String area;
}
