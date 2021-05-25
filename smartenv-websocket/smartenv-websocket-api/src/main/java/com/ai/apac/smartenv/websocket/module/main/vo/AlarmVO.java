package com.ai.apac.smartenv.websocket.module.main.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.mp.base.BaseEntity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @ClassName AlarmVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/5 20:11
 * @Version 1.0
 */
@Data
public class AlarmVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @ApiModelProperty("实体类型")
    private String entityType;

    @ApiModelProperty("告警类型")
    private String alarmType;

    @ApiModelProperty("告警信息")
    private String alarmMessage;

    @ApiModelProperty("告警时间")
    private String alarmDate;

}
