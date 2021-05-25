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

import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.ai.apac.smartenv.alarm.entity.AlarmInform;
import com.ai.apac.smartenv.alarm.mapper.AlarmInformMapper;
import com.ai.apac.smartenv.alarm.service.IAlarmInformService;
import com.ai.apac.smartenv.alarm.vo.AlarmInformVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 告警通知方式配置表 服务实现类
 *
 * @author Blade
 * @since 2020-12-28
 */
@Service
public class AlarmInformServiceImpl extends BaseServiceImpl<AlarmInformMapper, AlarmInform> implements IAlarmInformService {

	@Override
	public IPage<AlarmInformVO> selectAlarmInformPage(IPage<AlarmInformVO> page, AlarmInformVO alarmInform) {
		return page.setRecords(baseMapper.selectAlarmInformPage(page, alarmInform));
	}

	/**
	 * 新增或更新告警通知方式
	 * 操作前对必要参数进行校验
	 * @param alarmInform
	 * @return
	 */
	@Override
	public boolean saveOrUpdateAlarmInform(AlarmInform alarmInform) {
		// 校验参数
		validateRuleInform(alarmInform);
		return super.saveOrUpdate(alarmInform);
	}

	/**
	 * 校验通知类型参数
	 * @param alarmInform
	 */
	private void validateRuleInform(AlarmInform alarmInform) {
		// 告警级别
		Integer alarmLevel = alarmInform.getAlarmLevel();
		if (alarmLevel == null || alarmLevel <= 0) {
			throw new ServiceException("告警级别不能为空！");
		}
		// 告警通知类型
		String informType = alarmInform.getInformType();
		if (StringUtils.isBlank(informType) || informType.split("\\|").length == 0) {
			throw new ServiceException("告警通知类型不能为空！");
		}
		// 告警通知应用到的实体类型
		Long entityType = alarmInform.getEntityType();
		if (entityType == null || entityType <= 0) {
			throw new ServiceException("通知应用实体类型不能为空！");
		}
		// 抄送领导的话，需要选择抄送方式
		Integer ccToLeader = alarmInform.getCcToLeader();
		if (ccToLeader != null && ccToLeader == AlarmConstant.CCToLeader.YES) {
			String ccToLeaderInformType = alarmInform.getCcToLeaderInformType();
			if (StringUtils.isBlank(ccToLeaderInformType) || ccToLeaderInformType.split("\\|").length == 0) {
				throw new ServiceException("告警抄送领导的通知类型不能为空！");
			}
		}
	}

}
