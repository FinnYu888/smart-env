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
import com.ai.apac.smartenv.assessment.entity.KpiCatalog;
import com.ai.apac.smartenv.assessment.entity.KpiDef;
import com.ai.apac.smartenv.assessment.vo.KpiCatalogVO;
import com.ai.apac.smartenv.assessment.mapper.KpiCatalogMapper;
import com.ai.apac.smartenv.assessment.service.IKpiCatalogService;
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
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 考核指标分类 服务实现类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Service
public class KpiCatalogServiceImpl extends BaseServiceImpl<KpiCatalogMapper, KpiCatalog> implements IKpiCatalogService {

	@Autowired
	private IKpiDefService kpiDefService;
	
	@Override
	public IPage<KpiCatalogVO> selectKpiCatalogPage(IPage<KpiCatalogVO> page, KpiCatalogVO kpiCatalog) {
		return page.setRecords(baseMapper.selectKpiCatalogPage(page, kpiCatalog));
	}

	@Override
	public boolean checkKpiCatalogName(KpiCatalog kpiCatalog) {
		QueryWrapper<KpiCatalog> queryWrapper = new QueryWrapper<>();
		if (kpiCatalog.getId() != null) {
			queryWrapper.notIn("id", kpiCatalog.getId());
		}
		queryWrapper.eq("catalog_name", kpiCatalog.getCatalogName());
		BladeUser user = AuthUtil.getUser();
		if (user != null && StringUtils.isNotBlank(user.getTenantId())) {
			queryWrapper.eq("tenant_id", user.getTenantId());
		}
		List<KpiCatalog> list = list(queryWrapper);
		if (list != null && list.size() > 0) {
			throw new ServiceException("该考核指标分类名称已存在");
		}
		return true;
	}

	@Override
	public IPage<KpiCatalog> page(KpiCatalog kpiCatalog, Query query) {
		QueryWrapper<KpiCatalog> queryWrapper = generateQueryWrapper(kpiCatalog);
		return page(Condition.getPage(query), queryWrapper);
	}

	private QueryWrapper<KpiCatalog> generateQueryWrapper(KpiCatalog kpiCatalog) {
		QueryWrapper<KpiCatalog> queryWrapper = new QueryWrapper<>();
		if (StringUtils.isNotBlank(kpiCatalog.getCatalogName())) {
			queryWrapper.like("catalog_name", kpiCatalog.getCatalogName());
		}
		if (StringUtils.isNotBlank(kpiCatalog.getTenantId())) {
			queryWrapper.eq("tenant_id", kpiCatalog.getTenantId());
		} else {
			BladeUser user = AuthUtil.getUser();
    		if (user != null) {
    			queryWrapper.eq("tenant_id", user.getTenantId());
    		}
		}
		return queryWrapper;
	}

	@Override
	public boolean saveKpiCatalog(KpiCatalog kpiCatalog) {
		checkKpiCatalogName(kpiCatalog);
		AssessmentCache.delKpiCatalog(kpiCatalog.getId());
		return save(kpiCatalog);
	}

	@Override
	public boolean updateKpiCatalogById(KpiCatalog kpiCatalog) {
		checkKpiCatalogName(kpiCatalog);
		AssessmentCache.delKpiCatalog(kpiCatalog.getId());
		return updateById(kpiCatalog);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeKpiCatalog(List<Long> idList) {
		idList.forEach(id -> {
			KpiDef kpiDef = new KpiDef();
			kpiDef.setKpiCatalog(id);
			List<KpiDef> kpiDefList = kpiDefService.listAll(kpiDef);
			if (kpiDefList != null && !kpiDefList.isEmpty()) {
				throw new ServiceException("该考核指标分类已用考核指标，不允许删除");
			}
			AssessmentCache.delKpiCatalog(id);
			deleteLogic(Arrays.asList(id));
		});
	}

	@Override
	public List<KpiCatalog> listAll(KpiCatalog kpiCatalog) {
		QueryWrapper<KpiCatalog> queryWrapper = generateQueryWrapper(kpiCatalog);
		return list(queryWrapper);
	}

	@Override
	public List<KpiCatalogVO> tree() {
		return ForestNodeMerger.merge(baseMapper.tree());
	}

}
