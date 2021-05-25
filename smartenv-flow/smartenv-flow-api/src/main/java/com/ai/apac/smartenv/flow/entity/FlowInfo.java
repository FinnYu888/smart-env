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
 * @since 2020-09-07
 */
@Data
@TableName("ai_flow_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FlowInfo对象", description = "FlowInfo对象")
public class FlowInfo extends TenantEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "流程key")
	private String flowCode;
	@ApiModelProperty(value = "流程名称")
	private String flowName;
	@ApiModelProperty(value = "备注")
	private String remark;
	@ApiModelProperty(value = "流程定义图片")
	private String image;
	@ApiModelProperty(value = "是否配置标识")
	private Integer configFlag;


}
