package com.ai.apac.smartenv.statistics.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@ApiModel("车辆作业信息")
@Document("VehicleWorkInfoData")
public class VehicleWorkSynthInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String vehicleId;//车辆ID

    private String vehicleCode;//车辆编码

    private String workDate;//作业时期

    private String statDate;//统计截至日期

    private String workareaLength;//规划里程

    private String workareaWidth;//规划宽度

    private String workareaArea;//规划面积

    private String distance;//实际里程

    private String distanceArea;//实际里程面积

    private String operationRate;//工作完成率

    private String updateTime;//更新时间

    private String projectCode;//项目编码

    private String projectName;//项目编码

    private String companyId;//公司ID

    private String areaId;//地区ID
}
