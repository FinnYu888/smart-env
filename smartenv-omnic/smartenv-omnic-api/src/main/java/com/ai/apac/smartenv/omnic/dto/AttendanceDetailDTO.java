package com.ai.apac.smartenv.omnic.dto;

import com.ai.apac.smartenv.omnic.entity.OmnicPersonInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleAttendanceDetailDTO
 * @Description: 车辆考勤详情信息
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/5/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/5/12 16:50     panfeng          v1.0.0             修改原因
 */
@Data
@ToString
public class AttendanceDetailDTO {

    private String key;

    @ApiModelProperty(value = "打卡ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long attendanceId;


    /**
     * 工号
     */
    @ApiModelProperty(value = "工号")
    @Length(max = 20, message = "人员工号长度不能超过20")
    @NotBlank(message = "需要输入人员工号")
    @Indexed
    private String jobNumber;

    /**
     * 员工所属职位
     */
    @ApiModelProperty(value = "员工所属职位")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @NotNull(message = "需要输入职位")
    @Indexed
    private Long personPositionId;

    @ApiModelProperty(value = "员工所属职位名称")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @Indexed
    private String personPositionName;



    @ApiModelProperty(value = "人员ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long entityId;
    @ApiModelProperty(value = "车牌号")
    private String entityName;
    @ApiModelProperty(value = "实体类型")
    private Long entityType;



    @ApiModelProperty(value = "考勤对象分类")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long entityCategoryId;
    @ApiModelProperty(value = "考勤对象分类名称")
    private String entityCategoryName;

    @ApiModelProperty(value = "人员所在部门ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private String deptId;

    @ApiModelProperty(value = "人员所在部门名称")
    private String deptName;
    @ApiModelProperty(value = "人员所属片区")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private String regionId;

    @ApiModelProperty(value = "人员所属片区名称")
    private String regionName;

    @ApiModelProperty(value = "工作区域ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long workareaId;

    @ApiModelProperty(value = "工作区域名称")
    private String workareaName;
    /**
     * 敏捷排班ID
     */
    @ApiModelProperty(value = "敏捷排班ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long scheduleObjectId;

    /**
     * 排班日期
     */
    @ApiModelProperty(value = "排班日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;

    /**
     * 排班ID
     */
    @ApiModelProperty(value = "排班ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long scheduleId;

    /**
     * 排班名称
     */
    @ApiModelProperty(value = "排班名称")
    @Length(max = 64, message = "班次名称长度不能超过64")
    @NotBlank(message = "需要输入班次名称")
    private String scheduleName;
    /**
     * 排班类型,1是周期性、2是弹性
     */
    @ApiModelProperty(value = "排班类型,1是周期性、2是弹性")
    @NotBlank(message = "需要输入班次类型")
    private String scheduleType;




    @ApiModelProperty(value = "工作开始时间点和坐标")
    private TrackPositionDto.Position workBeginPosition;
    @ApiModelProperty(value = "工作结束时间点和坐标")
    private TrackPositionDto.Position workEndPosition;





    /**
     * 班次起始时间
     */
    @ApiModelProperty(value = "班次起始时间")
    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    @NotNull(message = "需要输入工作开始时间")
    private Date scheduleBeginTime;
    /**
     * 班次结束时间
     */
    @ApiModelProperty(value = "班次结束时间")
    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    @NotNull(message = "需要输入工作结束时间")
    private Date scheduleEndTime;
    /**
     * 休息起始时间
     */
    @ApiModelProperty(value = "休息起始时间")
    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date breaksBeginTime;
    /**
     * 休息结束时间
     */
    @ApiModelProperty(value = "休息结束时间")
    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date breaksEndTime;

    /**
     * 作业路线图路径
     */
    @ApiModelProperty(value = "人员作业路线图路径")
    private String personLineImagePath;

    @ApiModelProperty(value = "作业图片信息")
    private List<OmnicScheduleAttendanceImageDTO> scheduleAttendanceDetails;

    /**
     * 途径位置
     */
    private List<TrackPositionDto.Position> positions;

//    /**
//     * 驾驶员列表
//     */
//    private List<OmnicPersonInfo> personList;

    /**
     * 途径线路格式化字符串
     */
    private String pathWay;

    //当前记录的生成时间
    private Long lastGeneralTime;






}
