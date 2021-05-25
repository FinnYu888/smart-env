package com.ai.apac.smartenv.person.vo;

import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: PersonTrackVo
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/17
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/17  10:07    panfeng          v1.0.0             修改原因
 */
@Data
public class PersonTrackVo  extends TrackPositionDto {

    /**
     * 工号
     */
    @ApiModelProperty(value = "工号")
    private String jobNumber;
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String personName;




    /**
     * 排班名称
     */
    @ApiModelProperty(value = "排班名称")
    private String scheduleName;

    /**
     * 排班名称
     */
    @ApiModelProperty(value = "工作趟数")
    private String workCount;

    /**
     * 总里程
     */
    @ApiModelProperty(value = "总里程")
    private String totalDistance;
    /**
     * 总里程
     */
    @ApiModelProperty(value = "作业里程")
    private String workDistance;

    @ApiModelProperty(value = "违规次数")
    private Long gotOfCount;

    @ApiModelProperty(value = "总数量")
    private Integer total;
    @ApiModelProperty(value = "告警数量")
    private Long alarmCount;

    @ApiModelProperty(value = "作业总时间")
    private String timeOfDuration;


    @ApiModelProperty(value = "工作开始时间")
    private String workBeginTime;

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
