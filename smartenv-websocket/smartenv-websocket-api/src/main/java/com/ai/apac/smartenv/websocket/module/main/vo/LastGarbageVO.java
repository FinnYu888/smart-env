package com.ai.apac.smartenv.websocket.module.main.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020/8/17 Asiainfo
 *
 * @ClassName: Last30GarbageVO
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/17
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/17  11:06    zhanglei25          v1.0.0             修改原因
 */
@Data
public class LastGarbageVO extends WebSocketDTO {

    List<LastDaysGarbageAmountVO> lastDaysGarbageAmountVOList;

}
