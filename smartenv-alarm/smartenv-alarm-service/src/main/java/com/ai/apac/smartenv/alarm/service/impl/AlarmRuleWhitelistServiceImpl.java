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
package com.ai.apac.smartenv.alarm.service.impl;

import com.ai.apac.smartenv.alarm.entity.AlarmRuleWhitelist;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleWhitelistVO;
import com.ai.apac.smartenv.alarm.mapper.AlarmRuleWhitelistMapper;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleWhitelistService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 告警白名单表 服务实现类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Slf4j
@Service
@AllArgsConstructor
public class AlarmRuleWhitelistServiceImpl extends BaseServiceImpl<AlarmRuleWhitelistMapper, AlarmRuleWhitelist> implements IAlarmRuleWhitelistService {

	@Override
	public IPage<AlarmRuleWhitelistVO> selectAlarmRuleWhitelistPage(IPage<AlarmRuleWhitelistVO> page, AlarmRuleWhitelistVO alarmRuleWhitelist) {
		return page.setRecords(baseMapper.selectAlarmRuleWhitelistPage(page, alarmRuleWhitelist));
	}

}
