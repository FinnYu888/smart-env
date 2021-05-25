package com.ai.apac.smartenv.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 告警通知方式接收对象
 */
@Data
public class AlarmInformArrayDTO implements Serializable {
    private static final long serialVersionUID = 5865617623386087257L;

    /**
     * 告警通知方式数组
     */
    @ApiModelProperty(value = "告警通知方式数组")
    private List<AlarmInformDTO> informTypeList;
}
