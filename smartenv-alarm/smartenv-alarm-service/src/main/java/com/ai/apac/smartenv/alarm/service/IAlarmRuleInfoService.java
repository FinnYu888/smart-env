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

import com.ai.apac.smartenv.alarm.dto.AlarmRuleInfoDTO;
import com.ai.apac.smartenv.alarm.dto.VehicleTypeQueryDTO;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleInfoVO;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.vehicle.vo.VehicleCategoryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 告警规则基本信息表 服务类
 *
 * @author Blade
 * @since 2020-02-15
 */
public interface IAlarmRuleInfoService extends BaseService<AlarmRuleInfo> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param alarmRuleInfo
	 * @return
	 */
	IPage<AlarmRuleInfoVO> selectAlarmRuleInfoPage(IPage<AlarmRuleInfoVO> page, AlarmRuleInfoVO alarmRuleInfo);


	/**
	 * 点击新增告警规则时组织告警信息及参数供前端展示
	 * @param alarmRuleInfoVO
	 * @return
	 */

	AlarmRuleInfoVO constructNewAlarmRuleInfo(AlarmRuleInfoVO alarmRuleInfoVO);

    /**
	 * 更新告警规则信息
	 * @param alarmRuleInfoVO
	 * @return
	 */
	boolean saveOrUpdateAlarmRuleInfo(AlarmRuleInfoVO alarmRuleInfoVO);

	/**
	 * 查询告警规则详情及其扩展信息并分页
	 * @param alarmRuleInfoDTO
	 * @param query
	 * @return
	 */
	IPage<AlarmRuleInfoVO> detailsAndDetailExts(AlarmRuleInfoDTO alarmRuleInfoDTO, Query query);
	
	/**
	 * 根据条件查规则信息
	 * @param alarmRuleInfo
	 * @param alarmRuleIdList
	 * @param query
	 * @return
	 */
	IPage<AlarmRuleInfo> listAlarmRuleInfoByCondition(AlarmRuleInfoDTO alarmRuleInfo, List<Long> alarmRuleIdList, Query query);

	/**
	 * 从类型数据中找出所有当前节点和子节点的id
	 * @param entityCategoryId
	 * @param data
	 */
	List<Long> listEntityCategoryId(Long entityCategoryId, List<EntityCategory> data);

    /**
	 * 告警规则表属性填充
	 * @param entityCategoryCode
	 * @param alarmRuleInfoVO
	 * @return
	 */
	AlarmRuleInfoVO fillAlarmRuleExtAttrs(String entityCategoryCode, AlarmRuleInfoVO alarmRuleInfoVO);

	/**
	 * 告警规则入参校验
	 * @param entityCategoryCode
	 * @param alarmRuleInfoVO
	 */
	void validAlarmRuleConfig(String entityCategoryCode, AlarmRuleInfoVO alarmRuleInfoVO);

	/**
	 * 告警规则关联传感器
	 * @param alarmRuleInfoVO
	 * @return
	 */
	AlarmRuleInfoVO fillAlarmRuleRelAttrs(AlarmRuleInfoVO alarmRuleInfoVO);

	/**
	 * 告警规则新增或修改或删除同步给大数据
	 * @param alarmRuleInfoVO
	 * @param optFlag
	 */
	void postAlarmRuleDataToBigData(AlarmRuleInfoVO alarmRuleInfoVO, String optFlag);

	/**
	 * 停用或者启用告警规则
	 * @param alarmRuleId
	 * @param newStatus
	 * @return
	 */
	boolean enableOrDisableRule(@NotNull(message = "告警规则Id不能为空") Long alarmRuleId, @NotNull(message = "启用/停用状态不能为空") Integer newStatus);

	/**
	 * 根据车辆或者人员，返回给前端支持新增操作的告警规则类型
	 * @param tenantId
	 * @param alarmTypeCode
	 * @param isSearch
	 * @return
	 */
	List<Dict> listAvailableAlarmTypes(String tenantId, String alarmTypeCode, Boolean isSearch);

	/**
	 * 复制告警规则
	 * @param tenantId
	 * @return
	 */
    @Deprecated
	boolean copyDefaultAlarmRule4SpecifiedTenantOrRuleId(@NotEmpty String tenantId, Long ruleId);

	/**
	 * 获取可配置关联的车辆类型
	 *
	 * @param vehicleTypeQueryDTO @return
	 */
	List<VehicleCategoryVO> listAvailableVehicleCategory(VehicleTypeQueryDTO vehicleTypeQueryDTO);
}
