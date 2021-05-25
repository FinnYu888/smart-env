package com.ai.apac.smartenv.statistics.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author qianlong
 * @description 区域工作完成率汇总数据
 * @Date 2020/12/8 7:15 下午
 **/
@Data
@ApiModel("区域工作完成率汇总数据")
@Document(collection = "area_work_info")
public class AreaWorkInfo {

    @Id
    @Field("id")
    private String id;

    @Field("area_code")
    @ApiModelProperty("区域编码")
    private String areaCode;

    @Field("stat_date")
    @ApiModelProperty("统计日期")
    private String statDate;

    @Field("total_for_vehicle")
    @ApiModelProperty("车辆机扫总里程")
    private Double totalWorkAreaForVehicle;

    @Field("completed_for_vehicle")
    @ApiModelProperty("车辆机扫已完成里程")
    private Double completedWorkAreaForVehicle;

    @Field("total_for_person")
    @ApiModelProperty("人员清扫总面积")
    private Double totalWorkAreaForPerson;

    @Field("completed_for_person")
    @ApiModelProperty("人员清扫已完成面积")
    private Double completedWorkAreaForPerson;

    public AreaWorkInfo(String id, String areaCode, String statDate, Double totalWorkAreaForVehicle, Double completedWorkAreaForVehicle, Double totalWorkAreaForPerson, Double completedWorkAreaForPerson) {
        this.id = id;
        this.areaCode = areaCode;
        this.statDate = statDate;
        this.totalWorkAreaForVehicle = totalWorkAreaForVehicle;
        this.completedWorkAreaForVehicle = completedWorkAreaForVehicle;
        this.totalWorkAreaForPerson = totalWorkAreaForPerson;
        this.completedWorkAreaForPerson = completedWorkAreaForPerson;
    }

    public AreaWorkInfo() {
        this.id = null;
        this.areaCode = null;
        this.statDate = null;
        this.totalWorkAreaForVehicle = 0.0;
        this.completedWorkAreaForVehicle = 0.0;
        this.totalWorkAreaForPerson = 0.0;
        this.completedWorkAreaForPerson = 0.0;
    }
}
