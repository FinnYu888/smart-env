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

import com.ai.apac.smartenv.event.entity.EventAssignedHistory;
import com.ai.apac.smartenv.event.mapper.EventAssignedHistoryMapper;
import com.ai.apac.smartenv.event.service.IEventAssignedHistoryService;
import com.ai.apac.smartenv.event.vo.EventAssignedHistoryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-03-03
 */
@Service
public class EventAssignedHistoryServiceImpl extends BaseServiceImpl<EventAssignedHistoryMapper, EventAssignedHistory> implements IEventAssignedHistoryService {

	@Override
	public IPage<EventAssignedHistoryVO> selectEventAssignedHistoryPage(IPage<EventAssignedHistoryVO> page, EventAssignedHistoryVO eventAssignedHistory) {
		return page.setRecords(baseMapper.selectEventAssignedHistoryPage(page, eventAssignedHistory));
	}

}
