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
package com.ai.apac.smartenv.person.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 人员信息表实体类
 *
 * @author Blade
 * @since 2020-02-14
 */
@Data
@TableName("ai_person")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Person对象", description = "人员信息表")
public class Person extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 主键
	*/
	@ApiModelProperty(value = "主键")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long id;
	/**
	* 工号
	*/
	@ApiModelProperty(value = "工号")
	@Length(max = 20, message = "人员工号长度不能超过20")
//	@NotBlank(message = "需要输入人员工号")
	private String jobNumber;
	/**
	* 姓名
	*/
	@ApiModelProperty(value = "姓名")
	@Length(max = 24, message = "人员姓名长度不能超过24")
	@NotBlank(message = "需要输入人员姓名")
	private String personName;
	/**
	* 员工所属部门
	*/
	@ApiModelProperty(value = "员工所属部门")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	@NotNull(message = "需要输入所属部门")
	private Long personDeptId;
	/**
	* 员工所属职位
	*/
	@ApiModelProperty(value = "员工所属职位")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long personPositionId;
	/**
	* 手机号
	*/
	@ApiModelProperty(value = "手机号")
//	@NotNull(message = "需要输入手机号码")
	@Length(max = 20, message = "手机号长度不能超过20")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private String mobileNumber;
	/**
	* 微信ID
	*/
	@ApiModelProperty(value = "微信ID")
	private String wechatId;
	/**
	* 电子邮箱
	*/
	@ApiModelProperty(value = "电子邮箱")
	private String email;
	/**
	* 入职时间
	*/
	@ApiModelProperty(value = "入职时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
//	@NotNull(message = "需要输入入职日期")
	private Date entryTime;
	/**
	* 离职时间
	*/
	@ApiModelProperty(value = "离职时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date leaveTime;
	/**
	* 工作年限
	*/
	@ApiModelProperty(value = "工作年限")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long workYear;
	/**
	* 人员头像
	*/
	@ApiModelProperty(value = "人员头像")
	private String image;
	/**
	* 性别
	*/
	@ApiModelProperty(value = "性别")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Integer gender;
	/**
	* 实体分类
	*/
	@ApiModelProperty(value = "实体分类")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long entityCategoryId;
	/**
	* 身份证号
	*/
	@ApiModelProperty(value = "身份证号")
	@Length(max = 20, message = "证件号码长度不能超过20")
//	@NotBlank(message = "需要输入证件号码")
	private String idCard;
	/**
	 * 证件类型
	 */
	@ApiModelProperty(value = "证件类型")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
//	@NotNull(message = "需要输入证件类型")
	private Integer idCardType;
	/**
	* 出生日期
	*/
	@ApiModelProperty(value = "出生日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date birthday;
	/**
	* 学历
	*/
	@ApiModelProperty(value = "学历")
	private String education;
	/**
	* 婚姻状况
	*/
	@ApiModelProperty(value = "婚姻状况")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Integer maritalStatus;
	/**
	* 是否在职
	*/
	@ApiModelProperty(value = "是否在职")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	@NotNull(message = "需要输入在职状态")
	private Integer isIncumbency;
	/**
	* 劳动合同
	*/
	@ApiModelProperty(value = "劳动合同")
	private String laborContract;
	/**
	* 合同类型
	*/
	@ApiModelProperty(value = "合同类型")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Integer contractType;
	/**
	* 合同开始日期
	*/
	@ApiModelProperty(value = "合同开始日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date contractStart;
	/**
	* 合同结束日期
	*/
	@ApiModelProperty(value = "合同结束日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date contractEnd;
	/**
	* 社会保险(0:未缴纳，1:缴纳)
	*/
	@ApiModelProperty(value = "社会保险(0:未缴纳，1:缴纳)")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Integer socialInsurance;
	/**
	* 社保编号
	*/
	@ApiModelProperty(value = "社保编号")
	private String socialInsuranceNumber;
	/**
	* 住房公积金(0:未缴纳，1:缴纳)
	*/
	@ApiModelProperty(value = "住房公积金(0:未缴纳，1:缴纳)")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Integer providentFund;
	/**
	* 公积金编号
	*/
	@ApiModelProperty(value = "公积金编号")
	private String providentFundNumber;
	/**
	* 政治面貌
	*/
	@ApiModelProperty(value = "政治面貌")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Integer politicalKind;
	/**
	 * 是否允许登录系统
	 */
	@ApiModelProperty(value = "是否允许登录系统")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Integer isUser;

	@ApiModelProperty(value = "绑定的手表实时状态")
	private Long watchDeviceStatus;
	
	@ApiModelProperty(value = "身份证正面")
	private String idCardFront;
	@ApiModelProperty(value = "身份证背面")
	private String idCardBack;
	@ApiModelProperty(value = "银行卡正面")
	private String bankCardFront;
	@ApiModelProperty(value = "银行卡背面")
	private String bankCardBack;

}
