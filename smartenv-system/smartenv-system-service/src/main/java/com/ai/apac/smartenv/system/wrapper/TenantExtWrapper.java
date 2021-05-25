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
package com.ai.apac.smartenv.system.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import com.ai.apac.smartenv.system.entity.TenantExt;
import com.ai.apac.smartenv.system.vo.TenantExtVO;

/**
 * 租户扩展表包装类,返回视图层所需的字段
 *
 * @author Blade
 * @since 2020-07-05
 */
public class TenantExtWrapper extends BaseEntityWrapper<TenantExt, TenantExtVO>  {

	public static TenantExtWrapper build() {
		return new TenantExtWrapper();
 	}

	@Override
	public TenantExtVO entityVO(TenantExt tenantExt) {
		TenantExtVO tenantExtVO = BeanUtil.copy(tenantExt, TenantExtVO.class);

		return tenantExtVO;
	}

}
