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

import cn.hutool.core.util.StrUtil;
import com.ai.apac.smartenv.assessment.entity.KpiTplDef;
import com.ai.apac.smartenv.assessment.entity.KpiTplDetail;
import com.ai.apac.smartenv.assessment.service.IKpiTplDetailService;
import com.ai.apac.smartenv.assessment.vo.KpiTplDefVO;
import com.ai.apac.smartenv.assessment.mapper.KpiTplDefMapper;
import com.ai.apac.smartenv.assessment.service.IKpiTplDefService;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 考核模板定义表 服务实现类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Service
@AllArgsConstructor
public class KpiTplDefServiceImpl extends BaseServiceImpl<KpiTplDefMapper, KpiTplDef> implements IKpiTplDefService {


	private IKpiTplDetailService kpiTplDetailService;

	@Override
	public IPage<KpiTplDefVO> selectKpiTplDefPage(IPage<KpiTplDefVO> page, KpiTplDefVO kpiTplDef) {
		return page.setRecords(baseMapper.selectKpiTplDefPage(page, kpiTplDef));
	}

	@Override
	public boolean verifyKpiIsUsed(List<Long> idList) {
		QueryWrapper<KpiTplDetail> wrapper = new QueryWrapper<KpiTplDetail>();
		wrapper.lambda().in(KpiTplDetail::getKpiId,idList);
		List<KpiTplDetail> kpiTplDetails = kpiTplDetailService.list(wrapper);
		if(kpiTplDetails.size() > 0 && !ObjectUtils.isEmpty(kpiTplDetails.get(0).getKpiName())){
			throw new ServiceException(StrUtil.format("指标[{}]已被使用，不能删除", kpiTplDetails.get(0).getKpiName()));
		}
		return true;
	}

}
