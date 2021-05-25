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
package com.ai.apac.smartenv.alarm.mapper;

import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleRel;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleRelVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 告警规则关联表 Mapper 接口
 *
 * @author Blade
 * @since 2020-02-07
 */
public interface AlarmRuleRelMapper extends BaseMapper<AlarmRuleRel> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param alarmRuleRel
	 * @return
	 */
	List<AlarmRuleRelVO> selectAlarmRuleRelPage(IPage page, AlarmRuleRelVO alarmRuleRel);

	/**
	 * 
	 * @Function: AlarmRuleRelMapper::listForBinding
	 * @Description: 查询绑定告警规则
	 * @param alarmRuleInfo
	 * @param start
	 * @param size
	 * @param vehicleId
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月11日 下午6:15:10 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<AlarmRuleRel> listForBinding(AlarmRuleInfo alarmRuleInfo, int start, Integer size, Long vehicleId);

}
