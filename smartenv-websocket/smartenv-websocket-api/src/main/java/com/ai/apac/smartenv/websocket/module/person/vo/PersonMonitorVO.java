package com.ai.apac.smartenv.websocket.module.person.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorInfoVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author qianlong
 * @description //人员实时监控DTO
 * @Date 2020/2/15 4:56 下午
 **/
@Data
public class PersonMonitorVO extends WebSocketDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    List<PersonMonitorInfoVO> personList;
}
