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
package com.ai.apac.smartenv.person.vo;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.ai.apac.smartenv.arrange.vo.ScheduleObjectVO;
import com.ai.apac.smartenv.person.entity.Person;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 人员信息表视图实体类
 *
 * @author Blade
 * @since 2020-02-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PersonVO对象", description = "人员信息表")
public class PersonVO extends Person {
	private static final long serialVersionUID = 1L;

	private String personDeptName;
	private String imageLink;
	
	private String driverLicenseFirstName;// 驾驶证正页
	private String driverLicenseFirstLink;// 驾驶证正页
	private String driverLicenseSecondName;// 驾驶证副页
	private String driverLicenseSecondLink;// 驾驶证副页
	
	private String personPositionName;
	private String genderName;
	private String idCardTypeName;
	private String politicalKindName;
	private String maritalStatusName;
	private String isIncumbencyName;
	private String contractTypeName;
	private String educationName;
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long personUserRelId;
	private Boolean isPersonUserRel;
	private Boolean isImport;
	
	// 操作员信息
	@Length(max = 45, message = "操作员账号长度不能超过45")
	private String account;
	private String password;
	private String roleId;
	private String roleName;
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long userId;

	private String watchStatusId;

	private String watchStatus;
	
	private List<List<ScheduleObjectVO>> scheduleObjectList;

	private String isIncumbencys;

	private Integer isBindTerminal;


	private List<Long> personIdList;


	private String idCardFrontLink;

	private String idCardBackLink;

	private String bankCardFrontLink;

	private String bankCardBackLink;
	
}
