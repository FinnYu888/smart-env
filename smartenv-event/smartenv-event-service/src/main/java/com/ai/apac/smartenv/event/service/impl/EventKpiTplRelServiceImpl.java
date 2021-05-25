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
import com.ai.apac.smartenv.event.entity.EventKpiDef;
import com.ai.apac.smartenv.event.entity.EventKpiTplRel;
import com.ai.apac.smartenv.event.mapper.EventKpiDefMapper;
import com.ai.apac.smartenv.event.mapper.EventKpiTplRelMapper;
import com.ai.apac.smartenv.event.service.IEventKpiCatalogService;
import com.ai.apac.smartenv.event.service.IEventKpiDefService;
import com.ai.apac.smartenv.event.service.IEventKpiTplRelService;
import com.ai.apac.smartenv.event.vo.EventKpiDefVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 考核指标定义表 服务实现类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Service
public class EventKpiTplRelServiceImpl extends BaseServiceImpl<EventKpiTplRelMapper, EventKpiTplRel> implements IEventKpiTplRelService {


	@Override
	public boolean saveEventKpiTplRelList(List<EventKpiTplRel> eventKpiTplRelList) {
		return false;
	}

	@Override
	public List<EventKpiTplRel> listAll(EventKpiTplRel eventKpiTplRel) {
		return null;
	}

	@Override
	public boolean removeEventKpiTplRelList(List<Long> longList) {
		return false;
	}
}
