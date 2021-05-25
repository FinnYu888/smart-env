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
package com.ai.apac.smartenv.event.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 抄送人
 *
 * @author upf3
 * @since 2020-10-06
 */
@Data
@ApiModel(value = "CcPeopleVO对象", description = "事件基本信息表")
public class CcPeopleVO {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "人员名称")
	private String personName;

	@ApiModelProperty(value = "人员Id")
	private Long personId;


}
