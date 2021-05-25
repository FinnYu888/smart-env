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
package com.ai.apac.smartenv.assessment.service;

import com.ai.apac.smartenv.assessment.entity.KpiTplDetail;
import com.ai.apac.smartenv.assessment.vo.KpiTplDetailVO;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 考核模板明细 服务类
 *
 * @author Blade
 * @since 2020-02-08
 */
public interface IKpiTplDetailService extends BaseService<KpiTplDetail> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param kpiTplDetail
	 * @return
	 */
	IPage<KpiTplDetailVO> selectKpiTplDetailPage(IPage<KpiTplDetailVO> page, KpiTplDetailVO kpiTplDetail);

	List<KpiTplDetail> getKpiTplDetailList(KpiTplDetail kpiTplDetail);

}
