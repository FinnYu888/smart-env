package com.ai.apac.smartenv.vehicle.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName VehicleLiveVideoVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/6/4 10:20
 * @Version 1.0
 */
@Data
public class VehicleVideoVO implements Serializable {
    private String verhicleId;
    private List<VehicleVideoUrlVO> vehicleVideoUrlVOList;
}
