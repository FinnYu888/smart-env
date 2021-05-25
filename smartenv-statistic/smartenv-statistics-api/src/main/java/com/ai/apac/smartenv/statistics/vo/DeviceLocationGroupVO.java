package com.ai.apac.smartenv.statistics.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.util.List;

/**
 * @author qianlong
 * @description 设备位置数据集合
 * @Date 2021/1/6 12:50 下午
 **/
@Data
public class DeviceLocationGroupVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("车辆信息列表")
    private List<DeviceLocationVO> vehicleList;

    @ApiModelProperty("人员信息列表")
    private List<DeviceLocationVO> personList;
}
