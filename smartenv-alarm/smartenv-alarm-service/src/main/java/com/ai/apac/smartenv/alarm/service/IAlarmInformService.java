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
package com.ai.apac.smartenv.alarm.service;

import com.ai.apac.smartenv.alarm.entity.AlarmInform;
import com.ai.apac.smartenv.alarm.vo.AlarmInformVO;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 告警通知方式配置表 服务类
 *
 * @author Blade
 * @since 2020-12-28
 */
public interface IAlarmInformService extends BaseService<AlarmInform> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param alarmInform
	 * @return
	 */
	IPage<AlarmInformVO> selectAlarmInformPage(IPage<AlarmInformVO> page, AlarmInformVO alarmInform);

	/**
	 * 新增或者更新通知方式
	 * @param alarmInform
	 * @return
	 */
	boolean saveOrUpdateAlarmInform(AlarmInform alarmInform);
	
}
