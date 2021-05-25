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
package com.ai.apac.smartenv.green.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import com.ai.apac.smartenv.green.entity.GreenareaInfo;
import com.ai.apac.smartenv.green.vo.GreenareaInfoVO;

/**
 * 绿化养护信息包装类,返回视图层所需的字段
 *
 * @author Blade
 * @since 2020-07-22
 */
public class GreenareaInfoWrapper extends BaseEntityWrapper<GreenareaInfo, GreenareaInfoVO>  {

	public static GreenareaInfoWrapper build() {
		return new GreenareaInfoWrapper();
 	}

	@Override
	public GreenareaInfoVO entityVO(GreenareaInfo greenareaInfo) {
		GreenareaInfoVO greenareaInfoVO = BeanUtil.copy(greenareaInfo, GreenareaInfoVO.class);

		return greenareaInfoVO;
	}

}
