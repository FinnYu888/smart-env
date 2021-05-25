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
package com.ai.apac.smartenv.inventory.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import com.ai.apac.smartenv.inventory.entity.ResType;
import com.ai.apac.smartenv.inventory.vo.ResTypeVO;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author Blade
 * @since 2020-02-25
 */
public class ResTypeWrapper extends BaseEntityWrapper<ResType, ResTypeVO>  {

	public static ResTypeWrapper build() {
		return new ResTypeWrapper();
 	}

	@Override
	public ResTypeVO entityVO(ResType resType) {
		ResTypeVO resTypeVO = BeanUtil.copy(resType, ResTypeVO.class);

		return resTypeVO;
	}

}
