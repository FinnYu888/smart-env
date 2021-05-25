package com.ai.apac.smartenv.websocket.module.person.vo;

import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleInfoVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/15 6:19 下午
 **/
@Data
public class PersonMonitorInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("人员信息")
    private PersonInfoVO personInfo;

    @ApiModelProperty("人员实时位置")
    private PositionDTO position;
}
