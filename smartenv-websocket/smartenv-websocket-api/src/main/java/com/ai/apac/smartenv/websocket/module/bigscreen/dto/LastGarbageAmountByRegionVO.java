package com.ai.apac.smartenv.websocket.module.bigscreen.dto;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020/8/20 Asiainfo
 *
 * @ClassName: LastDaysRegionGarbageVO
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/20
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/20  10:59    zhanglei25          v1.0.0             修改原因
 */
@Data
public class LastGarbageAmountByRegionVO extends WebSocketDTO {

    List<LastDaysRegionGarbageAmountVO> lastDaysRegionGarbageAmountList;

}
