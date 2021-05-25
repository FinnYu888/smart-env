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
package com.ai.apac.smartenv.arrange.service.impl;

import com.ai.apac.smartenv.arrange.entity.ScheduleWork;
import com.ai.apac.smartenv.arrange.vo.ScheduleWorkVO;
import com.ai.apac.smartenv.arrange.mapper.ScheduleWorkMapper;
import com.ai.apac.smartenv.arrange.service.IScheduleWorkService;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 实际作业表 服务实现类
 *
 * @author Blade
 * @since 2020-03-17
 */
@Service
public class ScheduleWorkServiceImpl extends BaseServiceImpl<ScheduleWorkMapper, ScheduleWork> implements IScheduleWorkService {

	@Override
	public IPage<ScheduleWorkVO> selectScheduleWorkPage(IPage<ScheduleWorkVO> page, ScheduleWorkVO scheduleWork) {
		return page.setRecords(baseMapper.selectScheduleWorkPage(page, scheduleWork));
	}

}
