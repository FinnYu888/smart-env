package com.ai.apac.smartenv.statistics.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("bi_vehicle_distance_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "bi_vehicle_distance_info对象", description = "车辆实际作业里程")
public class VehicleDistanceInfo extends TenantEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long id;

    @ApiModelProperty(value = "车辆ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long vehicleId;

    @ApiModelProperty(value = "车牌号码")
    private String plateNumber;

    @ApiModelProperty(value = "工作路线ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long workareaId;

    @ApiModelProperty(value = "车辆工作类型")
    private String vehicleWorktype;

    @ApiModelProperty(value = "车辆实时里程")
    private String realDistance;

    @ApiModelProperty(value = "工作路线宽度")
    private String workareaWidth;

    @ApiModelProperty(value = "工作路线长度")
    private String workareaLength;

    @ApiModelProperty(value = "工作路线等级")
    private Integer workareaLevel;

    @ApiModelProperty(value = "统计开始时间")
    private String beginTime;

    @ApiModelProperty(value = "统计结束时间")
    private String endTime;

    @ApiModelProperty(value = "统计日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date statDate;

}
