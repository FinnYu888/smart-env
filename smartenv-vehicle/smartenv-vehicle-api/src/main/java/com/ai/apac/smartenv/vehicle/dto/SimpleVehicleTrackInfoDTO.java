package com.ai.apac.smartenv.vehicle.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 车辆轨迹信息
 * @Date 2021/1/16 10:16 下午
 **/
@Data
@Document(collection = "simple_vehicle_track_info")
public class SimpleVehicleTrackInfoDTO implements Serializable {

    public static final String COLLECTION_NAME = "simple_vehicle_track_info";

    @ApiModelProperty(value = "主键")
    @Indexed
    private String id;

    @ApiModelProperty(value = "车辆ID")
    @Indexed
    private String vehicleId;

    @ApiModelProperty("车牌号")
    @Indexed
    private String plateNumber;

    @ApiModelProperty("车辆定位设备号")
    @Indexed
    private String gpsDeviceCode;

    @ApiModelProperty("项目编号")
    @Indexed
    private String projectCode;

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("开始时间")
    private String beginTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("行驶总里程")
    private String totalDistance;

    @ApiModelProperty("作业总里程")
    private String totalWorkDistance;

    @ApiModelProperty("平均速度")
    private String avgSpeed;

    @ApiModelProperty("最高速度")
    private String maxSpeed;

    @ApiModelProperty("总上报轨迹次数")
    private String totalCount;

    @ApiModelProperty("统计日期")
    @Indexed
    private String statDate;

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        final SimpleVehicleTrackInfoDTO simpleVehicleTrackInfoDTO = (SimpleVehicleTrackInfoDTO) obj;
        if (this == simpleVehicleTrackInfoDTO) {
            return true;
        } else {
            return (this.id.equals(simpleVehicleTrackInfoDTO.id));
        }
    }
    @Override
    public int hashCode() {
        int hashno = 7;
        hashno = 13 * hashno + (id == null ? 0 : id.hashCode());
        return hashno;
    }
}
