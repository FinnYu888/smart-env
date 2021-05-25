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

import com.ai.apac.smartenv.common.constant.EventConstant;
import com.ai.apac.smartenv.event.cache.EventCache;
import com.ai.apac.smartenv.event.entity.*;
import com.ai.apac.smartenv.event.mapper.EventKpiCatalogMapper;
import com.ai.apac.smartenv.event.mapper.EventKpiDefMapper;
import com.ai.apac.smartenv.event.service.*;
import com.ai.apac.smartenv.event.vo.EventKpiCatalogVO;
import com.ai.apac.smartenv.event.vo.EventKpiDefVO;
import com.ai.apac.smartenv.event.wrapper.EventKpiCatalogWrapper;
import com.ai.apac.smartenv.event.wrapper.EventKpiDefWrapper;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.ldap.PagedResultsControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考核指标定义表 服务实现类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Service
public class EventKpiDefServiceImpl extends BaseServiceImpl<EventKpiDefMapper, EventKpiDef> implements IEventKpiDefService {


	@Autowired
	@Lazy
	private IEventKpiCatalogService eventKpiCatalogService;

	@Autowired
	@Lazy
	private IEventKpiTplDefService kpiTplDefService;

	@Autowired
	private IEventKpiTplRelService eventKpiTplRelService;

	@Autowired
	private IEventInfoKpiRelService eventInfoKpiRelService;


	@Override
	public IPage<EventKpiDefVO> selectEventKpiDefPage(IPage<EventKpiDefVO> page, EventKpiDefVO eventKpiDefVO) {
		return page.setRecords(baseMapper.selectEventKpiDefPage(page, eventKpiDefVO));
	}

	@Override
	public boolean saveEventKpiDef(EventKpiDef eventKpiDef) {
		checkEventKpiDefName(eventKpiDef);
		EventCache.delEventKpiDef(eventKpiDef.getId());
		return save(eventKpiDef);
	}

	private boolean checkEventKpiDefName(EventKpiDef eventKpiDef) {
		QueryWrapper<EventKpiDef> queryWrapper = new QueryWrapper<>();
		if (eventKpiDef.getId() != null) {
			queryWrapper.notIn("id", eventKpiDef.getId());
		}
		queryWrapper.eq("event_kpi_name", eventKpiDef.getEventKpiName());
		BladeUser user = AuthUtil.getUser();
		if (user != null && StringUtils.isNotBlank(user.getTenantId())) {
			queryWrapper.eq("tenant_id", user.getTenantId());
		}
		List<EventKpiDef> list = list(queryWrapper);
		if (list != null && list.size() > 0) {
			throw new ServiceException("该考核指标名称已存在");
		}
		return true;
	}

	@Override
	public boolean updateEventKpiDefById(EventKpiDef eventKpiDef) {
		checkEventKpiDefName(eventKpiDef);
		EventCache.delEventKpiDef(eventKpiDef.getId());
		return updateById(eventKpiDef);
	}

	@Override
	public IPage<EventKpiDef> page(EventKpiDef eventKpiDef, Query query) {
		QueryWrapper<EventKpiDef> queryWrapper = generateQueryWrapper(eventKpiDef);
		return page(Condition.getPage(query), queryWrapper);
	}

	@Override
	public EventKpiDefVO getEventKpiDef(Long id) {
		EventKpiDef detail = EventCache.getEventKpiDefById(id);
		EventKpiDefVO eventKpiDefVO = EventKpiDefWrapper.build().entityVO(detail);
		return getEventKpiDefAllInfoByVO(eventKpiDefVO);
	}

	private EventKpiDefVO getEventKpiDefAllInfoByVO(EventKpiDefVO eventKpiDefVO) {
		Long kpiCatalogId = eventKpiDefVO.getEventKpiCatalog();
		EventKpiCatalog eventKpiCatalog = EventCache.getEventKpiCatalogById(kpiCatalogId);
		if (eventKpiCatalog != null && StringUtils.isNotBlank(eventKpiCatalog.getCatalogName())) {
			eventKpiDefVO.setEventKpiCatalogName(eventKpiCatalog.getCatalogName());
			eventKpiDefVO.setEventKpiCatalogLevel(eventKpiCatalog.getCatalogLevel());
		}

		if(ObjectUtil.isNotEmpty(eventKpiDefVO.getHandleLimitTime())) {
			eventKpiDefVO.setHandleLimitTimeDesc(DictBizCache.getValue(AuthUtil.getTenantId(),"event_handle_time", eventKpiDefVO.getHandleLimitTime()));
		}
		return eventKpiDefVO;
	}

	@Override
	public List<EventKpiDef> listAll(EventKpiDef eventKpiDef) {
		QueryWrapper<EventKpiDef> queryWrapper = generateQueryWrapper(eventKpiDef);
		return list(queryWrapper);
	}

	private QueryWrapper<EventKpiDef> generateQueryWrapper(EventKpiDef eventKpiDef) {
		QueryWrapper<EventKpiDef> queryWrapper = new QueryWrapper<>();
		if (StringUtils.isNotBlank(eventKpiDef.getEventKpiName())) {
			queryWrapper.lambda().like(EventKpiDef::getEventKpiName, eventKpiDef.getEventKpiName());
		}
		if (eventKpiDef.getEventKpiCatalog() != null) {
			List<Long> ids = new ArrayList<Long>();
			ids.add(eventKpiDef.getEventKpiCatalog());
			eventKpiCatalogService.getChildCatalogIdList(eventKpiDef.getEventKpiCatalog(), ids);
			queryWrapper.lambda().in(EventKpiDef::getEventKpiCatalog,ids);
		}
		return queryWrapper;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean removeEventKpiDef(List<Long> idList) {
		idList.forEach(id -> {
			EventCache.delEventKpiDef(id);
			deleteLogic(Arrays.asList(id));
		});
		return true;
	}

	@Override
	public Integer countEventKpiDef(EventKpiDef eventKpiDef) {
		QueryWrapper<EventKpiDef> eventKpiDefQueryWrapper = new QueryWrapper<EventKpiDef>();
		if(ObjectUtil.isNotEmpty(eventKpiDef.getEventKpiCatalog())){
			eventKpiDefQueryWrapper.lambda().eq(EventKpiDef::getEventKpiCatalog,eventKpiDef.getEventKpiCatalog());
		}
		return this.count(eventKpiDefQueryWrapper);
	}

	/**
	 * 根据人员ID获取指标树
	 * @param personId
	 * @return
	 */
	@Override
	public  List<EventKpiCatalogVO> treeKpiDefByPersonId(Long personId){
		Person personById = PersonCache.getPersonById(AuthUtil.getTenantId(), personId);
		Long personPositionId = personById.getPersonPositionId();

		QueryWrapper<EventKpiTplDef> eventKpiTplDefQueryWrapper=new QueryWrapper<>();
		eventKpiTplDefQueryWrapper.eq("position_id",personPositionId);// 岗位
		eventKpiTplDefQueryWrapper.eq("status", EventConstant.Event_Kpi_Tpl_Status.LIVE);// 状态
		EventKpiTplDef one = kpiTplDefService.getOne(eventKpiTplDefQueryWrapper);// 根据岗位查询
		if (one==null){
			return null;
		}
		QueryWrapper<EventKpiTplRel> eventKpiTplRelQueryWrapper=new QueryWrapper<>();
		eventKpiTplRelQueryWrapper.eq("event_kpi_tpl_id",one.getId());
		List<EventKpiTplRel> list = eventKpiTplRelService.list(eventKpiTplRelQueryWrapper);// 查询所有指标列表
		List<Long> tplIdList = list.stream().filter(eventKpiTplRel -> eventKpiTplRel != null).map(EventKpiTplRel::getEventKpiId).collect(Collectors.toList());
		Map<Long, EventKpiTplRel> tplRelMap = list.stream().collect(Collectors.toMap(EventKpiTplRel::getEventKpiId, eventKpiTplRel -> eventKpiTplRel));


		QueryWrapper<EventKpiDef> eventKpiDefQueryWrapper=new QueryWrapper<>();
		eventKpiDefQueryWrapper.in("id",tplIdList);
		List<EventKpiDef> kpiDefList = list(eventKpiDefQueryWrapper);
//		List<Long> categoryIdList = kpiDefList.stream().filter(eventKpiDef -> eventKpiDef != null && eventKpiDef.getEventKpiCatalog() != null).map(eventKpiDef -> eventKpiDef.getEventKpiCatalog()).collect(Collectors.toList());

//		QueryWrapper<EventKpiCatalog> eventKpiCatalogQueryWrapper=new QueryWrapper<>();
//		 eventKpiCatalogQueryWrapper.in("id", categoryIdList);
//		List<EventKpiCatalog> catalogs = eventKpiCatalogService.list(eventKpiCatalogQueryWrapper);
		List<EventKpiCatalog> catalogs = eventKpiCatalogService.list();
		List<EventKpiCatalogVO> tree = catalogs.stream().map(eventKpiCatalog -> EventKpiCatalogWrapper.build().entityVO(eventKpiCatalog)).collect(Collectors.toList());
		Map<Long, EventKpiCatalogVO> eventKpiCatalogVOMap = tree.stream().collect(Collectors.toMap(EventKpiCatalogVO::getId, eventKpiCatalogVO -> eventKpiCatalogVO));
		kpiDefList.forEach(eventKpiDef ->{
			EventKpiCatalogVO eventKpiCatalogVO = eventKpiCatalogVOMap.get(eventKpiDef.getEventKpiCatalog());
			List<EventKpiDefVO> eventKpiDefVOList =eventKpiCatalogVO.getEventKpiDefVOList();
			if (CollectionUtil.isEmpty(eventKpiCatalogVO.getEventKpiDefVOList())){
				eventKpiDefVOList=new ArrayList<>();
				eventKpiCatalogVO.setEventKpiDefVOList(eventKpiDefVOList);
			}
			EventKpiTplRel eventKpiTplRel = tplRelMap.get(eventKpiDef.getId());
			EventKpiDefVO eventKpiDefVO = EventKpiDefWrapper.build().entityVO(eventKpiDef);
			eventKpiDefVO.setThreshold(eventKpiTplRel.getThreshold());
			eventKpiDefVOList.add(eventKpiDefVO);
		});
		List<EventKpiCatalogVO> merge = ForestNodeMerger.merge(tree);
		return merge;
	}

	/**
	 * 根据事件ID获取指标树
	 * @param eventId
	 * @return
	 */
	@Override
	public List<EventKpiCatalogVO> getKpiDefByEventId(Long eventId) {

		QueryWrapper<EventInfoKpiRel> eventInfoKpiRelQueryWrapper=new QueryWrapper<>();
		eventInfoKpiRelQueryWrapper.eq("event_info_id",eventId);
		List<EventInfoKpiRel> eventInfoKpiRels = eventInfoKpiRelService.list(eventInfoKpiRelQueryWrapper);
		List<Long> kpiIdlist = eventInfoKpiRels.stream().filter(eventInfoKpiRel -> eventInfoKpiRel.getKpiId() != null).map(EventInfoKpiRel::getKpiId).collect(Collectors.toList());
		QueryWrapper<EventKpiDef> eventKpiDefQueryWrapper=new QueryWrapper<>();
		eventKpiDefQueryWrapper.in("id",kpiIdlist);
		List<EventKpiDef> kpiDefList = list(eventKpiDefQueryWrapper);


		List<Long> kpiCatalogIds = kpiDefList.stream().map(eventKpiDef -> eventKpiDef.getEventKpiCatalog()).collect(Collectors.toList());

		QueryWrapper<EventKpiCatalog> queryWrapper=new QueryWrapper<>();
//		queryWrapper.in("id",kpiCatalogIds);
		List<EventKpiCatalog> list = eventKpiCatalogService.list(queryWrapper);
		List<EventKpiCatalogVO> tree = list.stream().map(eventKpiCatalog -> EventKpiCatalogWrapper.build().entityVO(eventKpiCatalog)).collect(Collectors.toList());
		Map<Long, EventKpiCatalogVO> eventKpiCatalogVOMap = tree.stream().collect(Collectors.toMap(EventKpiCatalogVO::getId, eventKpiCatalogVO -> eventKpiCatalogVO));
		kpiDefList.forEach(eventKpiDef ->{
			EventKpiCatalogVO eventKpiCatalogVO = eventKpiCatalogVOMap.get(eventKpiDef.getEventKpiCatalog());
			List<EventKpiDefVO> eventKpiDefVOList =eventKpiCatalogVO.getEventKpiDefVOList();
			if (CollectionUtil.isEmpty(eventKpiCatalogVO.getEventKpiDefVOList())){
				eventKpiDefVOList=new ArrayList<>();
				eventKpiCatalogVO.setEventKpiDefVOList(eventKpiDefVOList);
			}



			EventKpiDefVO eventKpiDefVO = EventKpiDefWrapper.build().entityVO(eventKpiDef);
			eventKpiDefVO.setThreshold(0D);


			eventKpiDefVOList.add(eventKpiDefVO);
		});
		List<EventKpiCatalogVO> merge = ForestNodeMerger.merge(tree);

		return merge;
	}


}
