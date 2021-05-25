package com.ai.apac.smartenv.websocket.module.main.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName Last10EventVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/11 17:15
 * @Version 1.0
 */
@Data
public class Last10EventVO extends WebSocketDTO {

    List<EventVO> eventVOList;

}

