package com.ai.apac.smartenv.websocket.module.main.vo;

import com.ai.apac.smartenv.facility.vo.LastDaysGarbageAmountVO;
import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020/8/19 Asiainfo
 *
 * @ClassName: Last6OrderVO
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  17:08    zhanglei25          v1.0.0             修改原因
 */
@Data
public class Last6OrderVO extends WebSocketDTO {

    List<ResOrder4HomeVO> resOrder4HomeVOList;

}
