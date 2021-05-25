package com.ai.apac.smartenv.websocket.module.polymerization.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.PolymerizationDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020/9/22 Asiainfo
 *
 * @ClassName: PolymerizationCountVO
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/9/22
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/9/22  14:51    zhanglei25          v1.0.0             修改原因
 */

@Data
public class PolymerizationCountVO  extends WebSocketDTO implements Serializable {

    /**
     * 人员数量
     */
    Long personCount;

    Long onPersonCount;

    Long offPersonCount;

    Long nodPersonCount;

    /**
     * 车辆数量
     */
    Long vehicleCount;

    Long onVehicleCount;

    Long offVehicleCount;

    Long nodVehicleCount;

    /**
     * 今日事件数量
     */
    Long todayEventCount;

    /**
     * 今日告警数量
     */
    Long todayAlarmCount;

    /**
     * 中转站数量
     */
    Long facilityCount;

    /**
     * 垃圾桶数量
     */
    Long ashcanCount;

    /**
     * 公厕数量
     */
    Long toiletCount;

}
