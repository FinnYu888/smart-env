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

import com.ai.apac.smartenv.event.entity.EventMedium;
import com.ai.apac.smartenv.event.mapper.EventMediumMapper;
import com.ai.apac.smartenv.event.service.IEventMediumService;
import com.ai.apac.smartenv.event.vo.EventMediumVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 事件基本信息表 服务实现类
 *
 * @author Blade
 * @since 2020-02-06
 */
@Service
public class EventMediumServiceImpl extends BaseServiceImpl<EventMediumMapper, EventMedium> implements IEventMediumService {

	@Override
	public IPage<EventMediumVO> selectEventMediumPage(IPage<EventMediumVO> page, EventMediumVO eventInfo) {
		return page.setRecords(baseMapper.selectEventMediumPage(page, eventInfo));
	}
}
