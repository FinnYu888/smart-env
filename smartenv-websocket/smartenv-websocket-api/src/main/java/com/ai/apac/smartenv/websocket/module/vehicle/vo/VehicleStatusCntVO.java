package com.ai.apac.smartenv.websocket.module.vehicle.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/15 11:58 上午
 **/
@Data
public class VehicleStatusCntVO extends WebSocketDTO {

    /**
     * 在岗数量
     */
    private Long workingCnt;

    /**
     * 脱岗数量
     */
    private Long unWorkingCnt;

    /**
     * 休息中数量
     */
    private Long restCnt;

    /**
     * 告警数量
     */
    private Long alarmCnt;

    /**
     * 加水数量
     */
    private Long waterCnt;

    /**
     * 加油数量
     */
    private Long oilCnt;

    /**
     * 休假数量
     */
    private Long vacationCnt;

    //未排班
    private Long unArrangeCnt;
}
