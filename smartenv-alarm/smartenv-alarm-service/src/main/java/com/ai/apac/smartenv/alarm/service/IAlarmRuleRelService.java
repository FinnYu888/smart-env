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

import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleRel;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleInfoVO;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleRelVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 告警规则关联表 服务类
 *
 * @author Blade
 * @since 2020-02-07
 */
public interface IAlarmRuleRelService extends BaseService<AlarmRuleRel> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param alarmRuleRel
	 * @return
	 */
	IPage<AlarmRuleRelVO> selectAlarmRuleRelPage(IPage<AlarmRuleRelVO> page, AlarmRuleRelVO alarmRuleRel);

	/**
	 * 循环保存或者更新数据 
	 * @param alarmRuleRelList
	 */
	void saveOrUpdateAlarmRuleExtBatch(@NotEmpty List<AlarmRuleRel> alarmRuleRelList);

	/**
	 * 根据告警规则Id删除关联关系（逻辑删）
	 * @param alarmRuleInfoVO
	 */
	void removeAlarmRelByAlarmRuleId(AlarmRuleInfoVO alarmRuleInfoVO);
	
	/**
	 * 
	 * @Function: IAlarmRuleRelService::listForBinding
	 * @Description: 查询绑定告警规则
	 * @param alarmRuleInfo
	 * @param query
	 * @param vehicleId
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月11日 下午6:13:24 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<AlarmRuleRel> listForBinding(AlarmRuleInfo alarmRuleInfo, Query query, Long vehicleId);

	/**
	 * 告警关联实体类型同步大数据
	 * @param alarmRuleInfoVO
	 * @param addRelEntityTypeList
	 * @param deleteRelEntityTypeList
	 */
	void postAlarmRelationshipEntityInfoToBigData(AlarmRuleInfoVO alarmRuleInfoVO, List<Long> addRelEntityTypeList, List<Long> deleteRelEntityTypeList);
}
