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
@TableName("bi_vehicle_workstat_result")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "bi_vehicle_workstat_result对象", description = "车辆作业统计结果")
public class VehicleWorkStatResult extends TenantEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long id;

    @ApiModelProperty(value = "工作路线等级")
    private Integer workareaLevel;

    @ApiModelProperty(value = "规划工作路线面积")
    private Double workareaAcreage;

    @ApiModelProperty(value = "车辆工作类型")
    private String vehicleWorktype;

    @ApiModelProperty(value = "实时作业面积")
    private Double realWorkAcreage;

    @ApiModelProperty(value = "实时作业完成率")
    private String realWorkPerc;

    @ApiModelProperty(value = "统计开始时间")
    private String beginTime;

    @ApiModelProperty(value = "统计结束时间")
    private String endTime;

    @ApiModelProperty(value = "统计日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date statDate;

    public VehicleWorkStatResult(Long id, Integer workareaLevel, Double workareaAcreage, String vehicleWorktype, Double realWorkAcreage, String realWorkPerc, String beginTime, String endTime, Date statDate) {
        this.id = id;
        this.workareaLevel = workareaLevel;
        this.workareaAcreage = workareaAcreage;
        this.vehicleWorktype = vehicleWorktype;
        this.realWorkAcreage = realWorkAcreage;
        this.realWorkPerc = realWorkPerc;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.statDate = statDate;
    }

    public VehicleWorkStatResult() {
    }
}
