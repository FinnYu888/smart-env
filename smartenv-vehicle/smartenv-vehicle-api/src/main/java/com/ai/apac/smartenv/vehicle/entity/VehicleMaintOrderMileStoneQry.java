package com.ai.apac.smartenv.vehicle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleMaintOrderMilestone查询对象", description = "VehicleMaintOrderMilestone查询对象")
public class VehicleMaintOrderMileStoneQry extends VehicleMaintMilestone {
    @DateTimeFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @ApiModelProperty("完成时间")
    private Date doneTime;
}
