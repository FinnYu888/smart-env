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
package com.ai.apac.smartenv.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 培训记录附件表实体类
 *
 * @author Blade
 * @since 2020-08-20
 */
@Data
@TableName("ai_training_attach")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TrainingAttach对象", description = "培训记录附件表")
public class TrainingAttach extends TenantEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "培训记录附件表Id")
	@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
	private Long id;
	/**
	* 培训记录表Id
	*/
		@ApiModelProperty(value = "培训记录表Id")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long trainingRecordId;
	/**
	* 附件类型：图片-1， 文件-2
	*/
		@ApiModelProperty(value = "附件类型：图片-1， 文件-2")
		private Integer attachType;
	/**
	* 附件存储地址
	*/
		@ApiModelProperty(value = "附件存储地址")
		private String attachUrl;

}
