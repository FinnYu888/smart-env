package com.ai.apac.smartenv.system.user.vo;

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
@ApiModel(value = "小程序首页统计数字VO对象", description = "小程序首页统计数字VO对象")
public class MiniAppHomeDataCountVO  implements Serializable {

    private Long shouldWorkVehicleCount;//应出勤车辆

    private Long shouldWorkPersonCount;//应出勤人员

    private Long restPersonCount;//人员（休息）

    private Long workingPersonCount;//人员（正常）

    private Long staticPersonCount;//人员（静值）

    private Long restVehicleCount;//车辆（休息）

    private Long workingVehicleCount;//车辆（正常）

    private Long staticVehicleCount;//车辆（静值）

    private Long wateringVehicleCount;//车辆（加水中）

    private Long oilingVehicleCount;//车辆（加油中）

    private Integer eventCount;//今日事件总数

    private Integer alarmCount;//今日告警总数
}
