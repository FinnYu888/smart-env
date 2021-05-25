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
package com.ai.apac.smartenv.inventory.vo;

import com.ai.apac.smartenv.inventory.entity.ResOrderDtl;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视图实体类
 *
 * @author Blade
 * @since 2020-02-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ResOrderDtlVO对象", description = "ResOrderDtlVO对象")
public class ResOrderDtlVO extends ResOrderDtl {
	private static final long serialVersionUID = 1L;
	private String specName;
	private String typeName;
	private String unit;
	@ApiModelProperty(value = "库存数量")
	private Integer inventoryCount = 0;

}
