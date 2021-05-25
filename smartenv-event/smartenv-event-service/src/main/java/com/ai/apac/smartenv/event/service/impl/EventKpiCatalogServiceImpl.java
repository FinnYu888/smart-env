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
package com.ai.apac.smartenv.event.service.impl;

import com.ai.apac.smartenv.event.cache.EventCache;
import com.ai.apac.smartenv.event.entity.EventKpiCatalog;
import com.ai.apac.smartenv.event.entity.EventKpiDef;
import com.ai.apac.smartenv.event.mapper.EventKpiCatalogMapper;
import com.ai.apac.smartenv.event.service.IEventKpiCatalogService;
import com.ai.apac.smartenv.event.service.IEventKpiDefService;
import com.ai.apac.smartenv.event.vo.EventKpiCatalogVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 考核指标分类 服务实现类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Service
public class EventKpiCatalogServiceImpl extends BaseServiceImpl<EventKpiCatalogMapper, EventKpiCatalog> implements IEventKpiCatalogService {

	@Autowired
	@Lazy
	private IEventKpiDefService eventKpiDefService;

	@Override
	public IPage<EventKpiCatalogVO> selectEventKpiCatalogPage(IPage<EventKpiCatalogVO> page, EventKpiCatalogVO eventKpiCatalogVO) {
		return page.setRecords(baseMapper.selectEventKpiCatalogPage(page, eventKpiCatalogVO));
	}

	@Override
	public boolean checkEventKpiCatalogName(EventKpiCatalog eventKpiCatalog) {
		QueryWrapper<EventKpiCatalog> queryWrapper = new QueryWrapper<>();
		if (eventKpiCatalog.getId() != null) {
			queryWrapper.notIn("id", eventKpiCatalog.getId());
		}
		queryWrapper.eq("catalog_name", eventKpiCatalog.getCatalogName());
		BladeUser user = AuthUtil.getUser();
		if (user != null && StringUtils.isNotBlank(user.getTenantId())) {
			queryWrapper.eq("tenant_id", user.getTenantId());
		}
		List<EventKpiCatalog> list = list(queryWrapper);
		if (list != null && list.size() > 0) {
			throw new ServiceException("该考核指标分类名称已存在");
		}
		return true;
	}

	@Override
	public IPage<EventKpiCatalog> page(EventKpiCatalog eventKpiCatalog, Query query) {
		QueryWrapper<EventKpiCatalog> queryWrapper = generateQueryWrapper(eventKpiCatalog);
		return page(Condition.getPage(query), queryWrapper);
	}

	private QueryWrapper<EventKpiCatalog> generateQueryWrapper(EventKpiCatalog eventKpiCatalog) {
		QueryWrapper<EventKpiCatalog> queryWrapper = new QueryWrapper<>();
		if (StringUtils.isNotBlank(eventKpiCatalog.getCatalogName())) {
			queryWrapper.like("catalog_name", eventKpiCatalog.getCatalogName());
		}
		if (ObjectUtil.isNotEmpty(eventKpiCatalog.getParentId())) {
			queryWrapper.eq("parent_id", eventKpiCatalog.getParentId());
		}
		return queryWrapper;
	}

	@Override
	public boolean saveEventKpiCatalog(EventKpiCatalog eventKpiCatalog) {
		checkEventKpiCatalogName(eventKpiCatalog);
		EventCache.delEventKpiCatalog(eventKpiCatalog.getId());
		return save(eventKpiCatalog);
	}

	@Override
	public boolean updateEventKpiCatalogById(EventKpiCatalog eventKpiCatalog) {
		checkEventKpiCatalogName(eventKpiCatalog);
		// 若是 有下层级别时，不能换到 其他一级层级下
		checkExistChildCatalog(eventKpiCatalog);
		checkExistDef(eventKpiCatalog);
		EventCache.delEventKpiCatalog(eventKpiCatalog.getId());
		return updateById(eventKpiCatalog);
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeEventKpiCatalog(List<Long> idList) {
		idList.forEach(id -> {
			EventCache.delEventKpiCatalog(id);
			deleteLogic(Arrays.asList(id));
		});
	}

	@Override
	public List<EventKpiCatalog> listAll(EventKpiCatalog eventKpiCatalog) {
		QueryWrapper<EventKpiCatalog> queryWrapper = generateQueryWrapper(eventKpiCatalog);
		return list(queryWrapper);
	}

	@Override
	public List<EventKpiCatalogVO> tree(Long eventKpiTplId) {
		return ForestNodeMerger.merge(baseMapper.tree(eventKpiTplId));
	}


	@Override
	public List<EventKpiCatalogVO> treeNoMergerd(Long eventKpiTplId) {
		return baseMapper.tree(eventKpiTplId);
	}



	@Override
	public void getChildCatalogIdList(Long pId,List<Long> catalogIdList){
		List<EventKpiCatalog> eventKpiCatalogList = this.list(new QueryWrapper<EventKpiCatalog>().lambda().eq(EventKpiCatalog::getParentId,pId));
		if(ObjectUtil.isNotEmpty(eventKpiCatalogList) && eventKpiCatalogList.size() > 0){
			for(EventKpiCatalog eventKpiCatalog:eventKpiCatalogList){
				catalogIdList.add(eventKpiCatalog.getId());
				getChildCatalogIdList(eventKpiCatalog.getId(),catalogIdList);
			}
		}
	}

	/*
	 * 若是 有下层级别时，不能换到 其他一级层级下
	 */
	private boolean checkExistChildCatalog(EventKpiCatalog eventKpiCatalog) {
		Long newParentId = eventKpiCatalog.getParentId();
		Long oldParentId = EventCache.getEventKpiCatalogById(eventKpiCatalog.getId()).getParentId();
		if (!newParentId.equals(oldParentId)) {
			List<EventKpiCatalog> eventKpiCatalogList = list(new QueryWrapper<EventKpiCatalog>().lambda().eq(EventKpiCatalog::getParentId, eventKpiCatalog.getId()));
			if (eventKpiCatalogList != null && !eventKpiCatalogList.isEmpty()) {
				throw new ServiceException("当前指标分类存在下级分类，不能修改上级分类");
			}
		}
		return true;
	}
	/*
	 * 不能移到已有指标的层级下
	 */
	private boolean checkExistDef(EventKpiCatalog eventKpiCatalog) {
		Long newParentId = eventKpiCatalog.getParentId();
		Long oldParentId = EventCache.getEventKpiCatalogById(eventKpiCatalog.getId()).getParentId();
		if (!newParentId.equals(oldParentId)) {
			List<EventKpiDef> eventKpiDefList = eventKpiDefService.list(new QueryWrapper<EventKpiDef>().lambda().eq(EventKpiDef::getEventKpiCatalog, newParentId));
			if (eventKpiDefList != null && !eventKpiDefList.isEmpty()) {
				EventKpiCatalog catalog = EventCache.getEventKpiCatalogById(newParentId);
				throw new ServiceException("上级分类(" + catalog.getCatalogName() + ")下已有指标，上级分类不能修改成" + catalog.getCatalogName());
			}
		}
		return true;
	}
}
