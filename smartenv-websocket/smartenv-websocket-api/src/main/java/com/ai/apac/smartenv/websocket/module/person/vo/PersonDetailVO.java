package com.ai.apac.smartenv.websocket.module.person.vo;

import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/18 4:05 下午
 **/
@Data
public class PersonDetailVO extends WebSocketDTO implements Serializable {
    /**
     * 人员ID
     */
    private String id;
    /**
     * 工号
     */
    @ApiModelProperty(value = "工号")
    private String jobNumber;

    @ApiModelProperty(value = "岗位")
    private String stationId;

    @ApiModelProperty(value = "岗位名称")
    private String stationName;


    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String personName;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String mobile;

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

    @ApiModelProperty(value = "最高速度")
    private String maxSpeed;

    @ApiModelProperty(value = "平均速度")
    private String avgSpeed;

    @ApiModelProperty(value = "实时速度")
    private String speed;

    @ApiModelProperty(value = "作业开始时间")
    private Long workBeginTime;

    @ApiModelProperty(value = "作业总时间")
    private Long timeOfDuration;

    @ApiModelProperty(value = "违规次数")
    private Integer alarmCount;

    @ApiModelProperty(value = "最新告警内容")
    private String lastAlarmContent;

    @ApiModelProperty(value = "驾驶员")
    private String driver;

    @ApiModelProperty(value = "位置信息")
    private PositionDTO position;

    @ApiModelProperty(value = "ACC状态,0 无信息,1 开启,2 正常关闭,3 异常关闭")
    private Long deviceStatus;

    @ApiModelProperty(value = "ACC状态名称")
    private String deviceStatusName;

    @ApiModelProperty(value = "设备剩余电量")
    private String batteryPercent = "80";

    @ApiModelProperty(value = "出勤状态")
    private Integer workStatus;

    @ApiModelProperty(value = "出勤状态名称")
    private String workStatusName;
    @ApiModelProperty(value = "公司ID")
    private String companyId;
    @ApiModelProperty(value = "公司名称")
    private String companyName;
    @ApiModelProperty(value = "项目ID")
    private String projectId;
    @ApiModelProperty(value = "项目名称")
    private String projectName;

}
