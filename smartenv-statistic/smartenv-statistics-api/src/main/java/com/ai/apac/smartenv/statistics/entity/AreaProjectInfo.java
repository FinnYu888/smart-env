package com.ai.apac.smartenv.statistics.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author qianlong
 * @description 区域项目汇总数据DTO
 * @Date 2020/12/8 7:15 下午
 **/
@Data
@ApiModel("区域项目汇总数据DTO")
@Document(collection = "area_project_info")
public class AreaProjectInfo {

    @Id
    @Field("id")
    private String id;

    @Field("area_code")
    @ApiModelProperty("区域编码")
    private String areaCode;

    @Field("project_num")
    @ApiModelProperty("项目数量")
    private Integer projectNum;

    @Field("person_num")
    @ApiModelProperty("人员数量")
    private Integer personNum;

    @Field("vehicle_num")
    @ApiModelProperty("车辆数量")
    private Integer vehicleNum;

    @Field("device_num")
    @ApiModelProperty("设备数量")
    private Integer deviceNum;

    @Field("stat_date")
    @ApiModelProperty("统计日期")
    private String statDate;

    public AreaProjectInfo(String id, String areaCode, Integer projectNum, Integer personNum, Integer vehicleNum, Integer deviceNum, String statDate) {
        this.id = id;
        this.areaCode = areaCode;
        this.projectNum = projectNum;
        this.personNum = personNum;
        this.vehicleNum = vehicleNum;
        this.deviceNum = deviceNum;
        this.statDate = statDate;
    }

    public AreaProjectInfo(){
        this.id = null;
        this.areaCode = null;
        this.projectNum = 0;
        this.personNum = 0;
        this.vehicleNum = 0;
        this.deviceNum = 0;
        this.statDate = "";
    }
}
