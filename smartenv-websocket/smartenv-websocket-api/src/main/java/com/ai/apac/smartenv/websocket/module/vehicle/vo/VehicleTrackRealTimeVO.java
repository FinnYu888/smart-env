package com.ai.apac.smartenv.websocket.module.vehicle.vo;

import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

import java.util.List;

/**
 * @author qianlong
 * @description 车辆轨迹VO
 * @Date 2020/2/18 8:52 下午
 **/
@Data
public class VehicleTrackRealTimeVO extends WebSocketDTO {

    private static final long serialVersionUID = 1L;

    private List<PositionDTO> positionList;
}
