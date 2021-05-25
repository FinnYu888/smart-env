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
package com.ai.apac.smartenv.flow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 实体类
 *
 * @author Blade
 * @since 2020-08-26
 */
@Data
@TableName("ai_flow_task_pending")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FlowTaskPending对象", description = "FlowTaskPending对象")
public class FlowTaskPending extends TenantEntity {

	private static final long serialVersionUID = 1L;

	private Long id;
	/**
	* 流程名称
	*/
		@ApiModelProperty(value = "流程名称")
		private String flowName;
	/**
	* 任务节点
	*/
		@ApiModelProperty(value = "任务节点")
		private String taskNode;
	/**
	* 处理类型。1-人员，2-岗位，3-角色
	*/
		@ApiModelProperty(value = "处理类型。1-人员，2-岗位，3-角色")
		private String doneType;
	/**
	* 处理类型值
	*/
		@ApiModelProperty(value = "处理类型值")
		private String doneValue;
	/**
	* 待处理订单号
	*/
		@ApiModelProperty(value = "待处理订单号")
		private Long orderId;


}
