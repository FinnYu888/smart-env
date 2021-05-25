package com.ai.apac.smartenv.omnic.vo;

import java.time.LocalDate;
import java.util.List;

import com.ai.apac.smartenv.omnic.entity.QScheduleObject;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "QScheduleObjectVO对象", description = "设置考勤查询")
public class QScheduleObjectVO extends QScheduleObject {
	
	private static final long serialVersionUID = 1L;
	
	private String arrangeDate;
	
	private Long deptId;
	private String deptName;
	private String name;// 人员姓名或车辆车牌
	private String schedulePeriod;
	private String scheduleTime;
	private List<Long> personDeptIds;
	private List<Long> vehicleDeptIds;
	
	private Long newScheduleId;
	
	private LocalDate newScheduleBeginDate;
	
	private LocalDate newScheduleEndDate;
	
	private int start;
	private int size;
	private boolean historyFlag;
}
