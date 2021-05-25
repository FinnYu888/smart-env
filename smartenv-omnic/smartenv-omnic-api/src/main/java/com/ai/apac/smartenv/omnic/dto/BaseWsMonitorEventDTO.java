package com.ai.apac.smartenv.omnic.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description Websocket监听任务事件对象
 * @Date 2020/10/27 4:32 下午
 **/
@Data
@ApiModel
public class BaseWsMonitorEventDTO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("任务类型")
    private String eventType;

    @ApiModelProperty("租户ID")
    private String tenantId;

    @ApiModelProperty("系统的登录用户ID")
    private String userId;

    @ApiModelProperty("任务对象")
    private T eventObject;

    public BaseWsMonitorEventDTO() {
    }

    public BaseWsMonitorEventDTO(String eventType, String tenantId, String userId, T eventObject) {
        this.eventType = eventType;
        this.tenantId = tenantId;
        this.userId = userId;
        this.eventObject = eventObject;
    }
}
