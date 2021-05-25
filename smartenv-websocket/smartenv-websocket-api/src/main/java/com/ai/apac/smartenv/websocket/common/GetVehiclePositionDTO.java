package com.ai.apac.smartenv.websocket.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author qianlong
 * @description 获取车辆位置请求
 * @Date 2020/2/17 3:58 下午
 **/
@Data
public class GetVehiclePositionDTO implements Serializable {

    private static final long serialVersionUID = -3513145283505396994L;

    @ApiModelProperty("车辆ID")
    private String vehicleIds;

    @ApiModelProperty("车辆状态")
    private Integer status;

    @ApiModelProperty("工作区域id列表")
    private String workareaIds;

    @ApiModelProperty("区域ID")
    private String regionId;
    @ApiModelProperty("车辆类型")
    private String categoryId;

    /**
     * 车牌号
     */
    @ApiModelProperty(value = "车牌号")
    private String plateNumber;


}
