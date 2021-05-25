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

import com.ai.apac.smartenv.assessment.cache.AssessmentCache;
import com.ai.apac.smartenv.assessment.entity.KpiDef;
import com.ai.apac.smartenv.assessment.vo.KpiDefVO;
import com.ai.apac.smartenv.assessment.mapper.KpiDefMapper;
import com.ai.apac.smartenv.assessment.service.IKpiDefService;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 考核指标定义表 服务实现类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Service
public class KpiDefServiceImpl extends BaseServiceImpl<KpiDefMapper, KpiDef> implements IKpiDefService {

	@Override
	public IPage<KpiDefVO> selectKpiDefPage(IPage<KpiDefVO> page, KpiDefVO kpiDef) {
		return page.setRecords(baseMapper.selectKpiDefPage(page, kpiDef));
	}

	@Override
	public boolean saveKpiDef(KpiDef kpiDef) {
		checkKpiDefName(kpiDef);
		AssessmentCache.delKpiDef(kpiDef.getId());
		return save(kpiDef);
	}

	private boolean checkKpiDefName(KpiDef kpiDef) {
		QueryWrapper<KpiDef> queryWrapper = new QueryWrapper<>();
		if (kpiDef.getId() != null) {
			queryWrapper.notIn("id", kpiDef.getId());
		}
		queryWrapper.eq("kpi_name", kpiDef.getKpiName());
		BladeUser user = AuthUtil.getUser();
		if (user != null && StringUtils.isNotBlank(user.getTenantId())) {
			queryWrapper.eq("tenant_id", user.getTenantId());
		}
		List<KpiDef> list = list(queryWrapper);
		if (list != null && list.size() > 0) {
			throw new ServiceException("该考核指标名称已存在");
		}
		return true;		
	}

	@Override
	public boolean updateKpiDefById(KpiDef kpiDef) {
		checkKpiDefName(kpiDef);
		AssessmentCache.delKpiDef(kpiDef.getId());
		return updateById(kpiDef);
	}

	@Override
	public IPage<KpiDef> page(KpiDef kpiDef, Query query) {
		QueryWrapper<KpiDef> queryWrapper = generateQueryWrapper(kpiDef);
		return page(Condition.getPage(query), queryWrapper);
	}

	@Override
	public List<KpiDef> listAll(KpiDef kpiDef) {
		QueryWrapper<KpiDef> queryWrapper = generateQueryWrapper(kpiDef);
		return list(queryWrapper);
	}

	private QueryWrapper<KpiDef> generateQueryWrapper(KpiDef kpiDef) {
		QueryWrapper<KpiDef> queryWrapper = new QueryWrapper<>();
		if (StringUtils.isNotBlank(kpiDef.getKpiName())) {
			queryWrapper.like("kpi_name", kpiDef.getKpiName());
		}
		if (kpiDef.getKpiCatalog() != null) {
			queryWrapper.eq("kpi_catalog", kpiDef.getKpiCatalog());
		}
		if (StringUtils.isNotBlank(kpiDef.getTenantId())) {
			queryWrapper.eq("tenant_id", kpiDef.getTenantId());
		} else {
			BladeUser user = AuthUtil.getUser();
    		if (user != null) {
    			queryWrapper.eq("tenant_id", user.getTenantId());
    		}
		}
		return queryWrapper;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean removeKpiDef(List<Long> idList) {
		idList.forEach(id -> {
			AssessmentCache.delKpiDef(id);
			deleteLogic(Arrays.asList(id));
		});
		return true;
	}

	@Override
	public Integer countKpiDef(KpiDef kpiDef) {
		QueryWrapper<KpiDef> kpiDefQueryWrapper = new QueryWrapper<KpiDef>();
		if(ObjectUtil.isNotEmpty(kpiDef.getKpiCatalog())){
			kpiDefQueryWrapper.lambda().eq(KpiDef::getKpiCatalog,kpiDef.getKpiCatalog());
		}
		return this.count(kpiDefQueryWrapper);
	}

}
