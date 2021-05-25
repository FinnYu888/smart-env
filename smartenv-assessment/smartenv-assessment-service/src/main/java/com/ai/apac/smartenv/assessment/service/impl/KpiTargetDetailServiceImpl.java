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
package com.ai.apac.smartenv.assessment.service.impl;

import com.ai.apac.smartenv.assessment.entity.KpiTargetDetail;
import com.ai.apac.smartenv.assessment.vo.KpiTargetDetailVO;
import com.ai.apac.smartenv.assessment.mapper.KpiTargetDetailMapper;
import com.ai.apac.smartenv.assessment.service.IKpiTargetDetailService;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-03-02
 */
@Service
public class KpiTargetDetailServiceImpl extends BaseServiceImpl<KpiTargetDetailMapper, KpiTargetDetail> implements IKpiTargetDetailService {

	@Override
	public IPage<KpiTargetDetailVO> selectKpiTargetDetailPage(IPage<KpiTargetDetailVO> page, KpiTargetDetailVO kpiTargetDetail) {
		return page.setRecords(baseMapper.selectKpiTargetDetailPage(page, kpiTargetDetail));
	}

	@Override
	public boolean saveBatchKpiTargetDetail(List<KpiTargetDetail> kpiTargetDetailList) {
		kpiTargetDetailList.forEach(kpiTargetDetail -> {
			save(kpiTargetDetail);
		});
		return true;
	}


}
