package com.ai.apac.smartenv.websocket.module.person.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/24 08:58 上午
 **/
@Data
public class PersonStatusCntVO extends WebSocketDTO {

    private static final long serialVersionUID = 1L;

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
     * 休假
     */
    private Long vacationCnt;
    /**
     * 未排班
     */
    private Long unArrangeCnt;


}
