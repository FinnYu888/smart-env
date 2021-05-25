package com.ai.apac.smartenv.omnic.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 数据库表变更事件对象
 * @Date 2020/11/5 4:32 下午
 **/
@Data
@ApiModel
public class BaseDbEventDTO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("任务类型")
    private String eventType;

    @ApiModelProperty("租户ID")
    private String tenantId;

    @ApiModelProperty("任务对象")
    private T eventObject;

    public BaseDbEventDTO() {

    }

    public BaseDbEventDTO(String eventType, String tenantId, T eventObject) {
        this.eventType = eventType;
        this.tenantId = tenantId;
        this.eventObject = eventObject;
    }
}
