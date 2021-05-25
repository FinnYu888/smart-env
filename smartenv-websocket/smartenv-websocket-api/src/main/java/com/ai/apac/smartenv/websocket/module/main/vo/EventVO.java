package com.ai.apac.smartenv.websocket.module.main.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.mp.base.BaseEntity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @ClassName EventVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/11 17:15
 * @Version 1.0
 */
@Data
public class EventVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @ApiModelProperty("事件类型")
    private String eventType;

    @ApiModelProperty("事件信息")
    private String eventMessage;

    @ApiModelProperty("事件时间")
    private String eventDate;

}
