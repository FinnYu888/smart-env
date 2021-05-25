/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.vehicle.vo;

import java.util.List;

import com.ai.apac.smartenv.arrange.vo.ScheduleObjectVO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 车辆基本信息表视图实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleInfoVO对象", description = "车辆基本信息表")
public class VehicleInfoVO extends VehicleInfo {
	
	private static final long serialVersionUID = 1L;

	private String vehiclePicName;
	
	private String vehiclePicLink;
	
	private String drivingPicFirstName;

	private String drivingPicFirstLink;

	private String drivingPicSecondName;
	
	private String drivingPicSecondLink;

	private String kindCodeName;

	private String entityCategoryName;

	private String deptName;

	private String fuelTypeName;
	
	private String rozName;

	//实时位置ICON
	private String flagPic;
	//是否显示，给前端用的。默认为false
	private Boolean showFlag;



	/**
	 * ACC状态
	 */
	private String accStatusId;

	private String accStatus;

	private Integer isBindTerminal;

	/**
	 * 是否有告警
	 */
	private Integer haveAlarm;


	private List<VehicleDriverVO> vehicleDriverVO;
	
	private String vehicleStateName;
	
	private List<List<ScheduleObjectVO>> scheduleObjectList;
	
	private String isUseds;
	
}
