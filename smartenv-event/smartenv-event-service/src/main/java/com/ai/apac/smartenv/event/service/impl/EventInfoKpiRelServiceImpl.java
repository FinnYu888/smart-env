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

import com.ai.apac.smartenv.event.entity.EventInfoKpiRel;
import com.ai.apac.smartenv.event.vo.EventInfoKpiRelVO;
import com.ai.apac.smartenv.event.mapper.EventInfoKpiRelMapper;
import com.ai.apac.smartenv.event.service.IEventInfoKpiRelService;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 事件指标与事件信息关联表 服务实现类
 *
 * @author Blade
 * @since 2020-12-16
 */
@Service
public class EventInfoKpiRelServiceImpl extends BaseServiceImpl<EventInfoKpiRelMapper, EventInfoKpiRel> implements IEventInfoKpiRelService {

	@Override
	public IPage<EventInfoKpiRelVO> selectEventInfoKpiRelPage(IPage<EventInfoKpiRelVO> page, EventInfoKpiRelVO eventInfoKpiRel) {
		return page.setRecords(baseMapper.selectEventInfoKpiRelPage(page, eventInfoKpiRel));
	}

}
