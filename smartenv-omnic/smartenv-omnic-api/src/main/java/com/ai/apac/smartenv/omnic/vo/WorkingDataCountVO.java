package com.ai.apac.smartenv.omnic.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MiniAppHomeDataCountVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/15 14:08
 * @Version 1.0
 */
@Data
@ApiModel(value = "大屏出勤统计数字VO对象", description = "大屏出勤统计数字VO对象")
public class WorkingDataCountVO implements Serializable {

    private Integer shouldWorkVehicleCount;

    private Integer shouldWorkPersonCount;


    private Integer eventCount;

    private Integer alarmCount;

    private Integer workingVehicleCount;

    private Integer workingPersonCount;
}

