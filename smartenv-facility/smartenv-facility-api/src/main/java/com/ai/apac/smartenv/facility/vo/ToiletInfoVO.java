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
package com.ai.apac.smartenv.facility.vo;

import com.ai.apac.smartenv.facility.entity.ToiletInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

/**
 * 视图实体类
 *
 * @author Blade
 * @since 2020-09-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ToiletInfoVO对象", description = "ToiletInfoVO对象")
public class ToiletInfoVO extends ToiletInfo {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "公厕负责人")
	private String chargePersonName;

	@ApiModelProperty(value = "公厕级别名称")
	private String toiletLevelName;

	@ApiModelProperty(value = "公厕状态名称")
	private String workStatusName;

	@ApiModelProperty(value = "所属单位名称")
	private String companyName;

	@ApiModelProperty(value = "所属部门名称")
	private String deptName;

	@ApiModelProperty(value = "所属业务区域名称")
	private String regionName;

	@ApiModelProperty(value = "男厕便池数")
	private int manAQuotaCount;

	@ApiModelProperty(value = "男厕坑位数")
	private int manBQuotaCount;

	@ApiModelProperty(value = "女厕坑位数")
	private int womanBQuotaCount;

	@ApiModelProperty(value = "母婴厕位数")
	private int momQuotaCount;

	@ApiModelProperty(value = "无障碍厕位")
	private int barrierFreeQuotaCount;

}
