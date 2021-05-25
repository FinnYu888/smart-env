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
package com.ai.apac.smartenv.assessment.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import com.ai.apac.smartenv.assessment.entity.KpiTplDetail;
import com.ai.apac.smartenv.assessment.vo.KpiTplDetailVO;

/**
 * 考核模板明细包装类,返回视图层所需的字段
 *
 * @author Blade
 * @since 2020-02-08
 */
public class KpiTplDetailWrapper extends BaseEntityWrapper<KpiTplDetail, KpiTplDetailVO>  {

	public static KpiTplDetailWrapper build() {
		return new KpiTplDetailWrapper();
 	}

	@Override
	public KpiTplDetailVO entityVO(KpiTplDetail kpiTplDetail) {
		KpiTplDetailVO kpiTplDetailVO = BeanUtil.copy(kpiTplDetail, KpiTplDetailVO.class);

		return kpiTplDetailVO;
	}

}
