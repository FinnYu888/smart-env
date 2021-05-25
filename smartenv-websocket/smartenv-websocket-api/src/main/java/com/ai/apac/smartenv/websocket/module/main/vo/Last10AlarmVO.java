package com.ai.apac.smartenv.websocket.module.main.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName Last10Alarm
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/5 20:07
 * @Version 1.0
 */
@Data
public class Last10AlarmVO extends WebSocketDTO {

    List<AlarmVO> alarmVOList;

}
