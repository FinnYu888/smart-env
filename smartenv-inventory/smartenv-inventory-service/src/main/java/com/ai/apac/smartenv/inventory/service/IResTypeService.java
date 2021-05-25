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
package com.ai.apac.smartenv.inventory.service;

import com.ai.apac.smartenv.inventory.entity.ResType;
import com.ai.apac.smartenv.inventory.vo.ResTypeSpecVO;
import com.ai.apac.smartenv.inventory.vo.ResTypeVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 *  服务类
 *
 * @author Blade
 * @since 2020-02-25
 */
public interface IResTypeService extends BaseService<ResType> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param resType
	 * @return
	 */
	IPage<ResTypeVO> selectResTypePage(IPage<ResTypeVO> page, ResTypeVO resType);

	/**
	* 按照层级查询资源类型和资源规格
	* @author 66578
	*/
	List<ResTypeSpecVO> selectResTypeSpec();

	/**
	 * 根据条件查询物资类型
	 * @param resType
	 * @return
	 */
	List<ResTypeVO> listResTypeByCond(ResType resType);

	/**
	 * 根据租户获取物资类型/物资规格名称组合
	 * @param tenantId
	 * @return
	 */
    List<String> listResTypeResSpecNameIdStrings(String tenantId);
}
