package com.ai.apac.smartenv.vehicle.vo;

import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleTrackVo
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/14  14:24    panfeng          v1.0.0             修改原因
 */

@Data
public class  VehicleTrackVO extends TrackPositionDto {

    @ApiModelProperty(value = "车牌号")
    private String plateNumber;

    /**
     * 排班名称
     */
    @ApiModelProperty(value = "排班名称")
    private String scheduleName;

    /**
     * 工作趟数
     */
    @ApiModelProperty(value = "工作趟数")
    private String workCount;

    /**
     * 总里程
     */
    @ApiModelProperty(value = "总里程")
    private String totalDistance;


    @ApiModelProperty(value = "作业里程")
    private String workDistance;

    @ApiModelProperty(value = "最高速度")
    private String maxSpeed;

    @ApiModelProperty(value = "平均速度")
    private String avgSpeed;

    @ApiModelProperty(value = "作业开始时间")
    private String workBeginTime;

    @ApiModelProperty(value = "作业总时间")
    private String timeOfDuration;

    @ApiModelProperty(value = "违规次数")
    private Long alarmCount;

    @ApiModelProperty(value = "最新告警内容")
    private String lastAlarmContent;

    @ApiModelProperty(value = "驾驶员")
    private String driver;

    @ApiModelProperty(value = "百公里油耗")
    private String avgOil100km;
    
    @ApiModelProperty(value = "总油耗")
    private String totalOil;
    
    @ApiModelProperty(value = "加油量")
    private String totalFillOil;

    @ApiModelProperty(value = "车辆图片")
    private String carIconPicture;
    /**
     * 设备使用的坐标系  1、WGS84
     */
    private String CSYS;

    private Integer total;

    /**
     * 车辆路线坐标 列表
     */
    private List<List<WorkareaNode>> roadNodes;

    /**
     *车辆区域坐标 列表
     */
    private List<List<WorkareaNode>> areaNodes;

    /**
     *车辆当前所属业务片区坐标 列表
     */
    private List<List<WorkareaNode>> regionNodes;

}
