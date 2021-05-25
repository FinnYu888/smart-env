package com.ai.apac.smartenv.websocket.module.vehicle.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author qianlong
 * @description //车辆实时监控DTO
 * @Date 2020/2/15 4:56 下午
 **/
@Data
public class VehicleMonitorVO extends WebSocketDTO implements Serializable {

    private static final long serialVersionUID = -8104129746535094492L;

    List<VehicleMonitorInfoVO> vehicleList;
}
