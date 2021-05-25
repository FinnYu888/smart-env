package com.ai.apac.smartenv.websocket.module.main.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

/**
 * @ClassName MainDataCountVO
 * @Desc 出勤车辆数，应出勤人员数，事件数，告警数的实时数据DTO
 * @Author ZHANGLEI25
 * @Date 2020/3/3 13:57
 * @Version 1.0
 */
@Data
public class HomePageDataCountVO extends WebSocketDTO {

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
