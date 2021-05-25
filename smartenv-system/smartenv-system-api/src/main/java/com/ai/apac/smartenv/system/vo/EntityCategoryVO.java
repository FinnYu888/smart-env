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
package com.ai.apac.smartenv.system.vo;

import com.ai.apac.smartenv.system.entity.EntityCategory;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 车辆,设备,物资等实体的分类信息视图实体类
 *
 * @author Blade
 * @since 2020-02-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EntityCategoryVO对象", description = "车辆,设备,物资等实体的分类信息")
public class EntityCategoryVO extends EntityCategory {
	private static final long serialVersionUID = 1L;
	/**
	 * 下级分类
	 */
	private List<EntityCategoryVO> childEntityCategoryVOS;

}
