package com.ai.apac.smartenv.vehicle.dto;

import com.ai.apac.smartenv.common.annotation.Latitude;
import com.ai.apac.smartenv.common.annotation.Longitude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author qianlong
 * @description 车辆基本信息DTO
 * @Date 2020/3/20 2:21 下午
 **/
@Data
@ApiModel(value = "车辆基本信息", description = "车辆基本信息")
public class BasicVehicleInfoDTO implements Serializable {

    private static final long serialVersionUID = 8870158777344186807L;

    @ApiModelProperty(value = "车辆ID")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @Indexed
    private Long id;

    /**
     * 车牌号
     */
    @ApiModelProperty(value = "车牌号")
    @Length(max = 20, message = "车牌号长度不能超过20")
    @NotBlank(message = "需要输入车牌号")
    @Indexed
    private String plateNumber;
    /**
     * 车辆大类
     */
    @ApiModelProperty(value = "车辆大类")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @NotNull(message = "需要输入车辆大类")
    @Indexed
    private Long kindCode;
    /**
     * 车辆类型
     */
    @ApiModelProperty(value = "车辆类型")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @NotNull(message = "需要输入车辆类型")
    @Indexed
    private Long entityCategoryId;

    @ApiModelProperty(value = "车辆类型名称")
    @Indexed
    private String vehicleTypeName;

    /**
     * 所属部门
     */
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @ApiModelProperty(value = "所属部门")
    @NotNull(message = "需要输入所属部门")
    @Indexed
    private Long deptId;

    @ApiModelProperty(value = "工作状态")
    @Indexed
    private Integer workStatus;

    @ApiModelProperty(value = "工作状态名称")
    private String workStatusName;



    @Indexed
    private Integer todayAlarmCount;


    private String lastAlarmContent;


    @Indexed
    private String tenantId;
    // 实时位置
    @Latitude
    private String lat;
    @Longitude
    private String lng;

    @Indexed
    private Long accDeviceId;
    @Indexed
    private String accDeviceCode;

    @Indexed
    private Long gpsDeviceId;
    @Indexed
    private String gpsDeviceCode;

    @Indexed
    private Long nvrDeviceId;
    @Indexed
    private String nvrDeviceCode;

    @Indexed
    private Long cvrDeviceId;
    @Indexed
    private String cvrDeviceCode;

    /**
     * 车辆工作所在片区
     */
    @ApiModelProperty(value = "车辆工作所在片区")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @Indexed
    private Long vehicleBelongRegion;


    /**
     * 车辆工作所在片区
     */
    @ApiModelProperty(value = "设备状态")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @Indexed
    private Long deviceStatus;

    @ApiModelProperty(value = "车辆工作类型编码")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @Indexed
    private String vehicleWorkTypeCode;

    @ApiModelProperty(value = "车辆工作类型名称")
    private String vehicleWorkTypeName;

    private Date updateTime;

    private Date createTime;

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        final BasicVehicleInfoDTO basicVehicleInfoDTO = (BasicVehicleInfoDTO) obj;
        if (this == basicVehicleInfoDTO) {
            return true;
        } else {
            return (this.id.equals(basicVehicleInfoDTO.id));
        }
    }
    @Override
    public int hashCode() {
        int hashno = 7;
        hashno = 13 * hashno + (id == null ? 0 : id.hashCode());
        return hashno;
    }
}
