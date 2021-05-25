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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleRel;
import com.ai.apac.smartenv.alarm.mapper.AlarmRuleRelMapper;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleRelService;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleInfoVO;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleRelVO;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 告警规则关联表 服务实现类
 *
 * @author Blade
 * @since 2020-02-07
 */
@Slf4j
@Service
@AllArgsConstructor
public class AlarmRuleRelServiceImpl extends BaseServiceImpl<AlarmRuleRelMapper, AlarmRuleRel> implements IAlarmRuleRelService {

	@Override
	public IPage<AlarmRuleRelVO> selectAlarmRuleRelPage(IPage<AlarmRuleRelVO> page, AlarmRuleRelVO alarmRuleRel) {
		return page.setRecords(baseMapper.selectAlarmRuleRelPage(page, alarmRuleRel));
	}

	@Override
	public void saveOrUpdateAlarmRuleExtBatch(@NonNull List<AlarmRuleRel> alarmRuleRelList) {
		alarmRuleRelList.forEach(alarmRuleRel -> {
			if (BeanUtil.isEmpty(alarmRuleRel.getId())) {
				this.save(alarmRuleRel);
			} else {
				this.updateById(alarmRuleRel);
			}
		});
	}

	@Override
	public void removeAlarmRelByAlarmRuleId(AlarmRuleInfoVO alarmRuleInfoVO) {
		LambdaQueryWrapper<AlarmRuleRel> alarmRuleRelQueryWrapper = new LambdaQueryWrapper<>();
		alarmRuleRelQueryWrapper.eq(AlarmRuleRel::getAlarmRuleId, alarmRuleInfoVO.getId());
		List<AlarmRuleRel> alarmRuleRelList = baseMapper.selectList(alarmRuleRelQueryWrapper);
		List<Long> deleteRelList = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(alarmRuleRelList)) {
			deleteRelList = alarmRuleRelList.stream().map(AlarmRuleRel::getEntityType).collect(Collectors.toList());
			this.remove(alarmRuleRelQueryWrapper);
		}
		// 关联关系同步大数据
		postAlarmRelationshipEntityInfoToBigData(alarmRuleInfoVO, null, deleteRelList);
	}
	
	@Override
	public List<AlarmRuleRel> listForBinding(AlarmRuleInfo alarmRuleInfo, Query query, Long vehicleId) {
		Integer current = query.getCurrent();
		Integer size = query.getSize();
		if (current == null) {
			current = 0;
		}
		if (size == null) {
			size = 0;
		}
		return baseMapper.listForBinding(alarmRuleInfo, (current - 1) * size, size, vehicleId);
	}

	/**
	 * 告警规则关联实体类型同步大数据
	 * @param alarmRuleInfoVO
	 * @param addRelEntityTypeList
	 * @param deleteRelEntityTypeList
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public void postAlarmRelationshipEntityInfoToBigData(AlarmRuleInfoVO alarmRuleInfoVO, List<Long> addRelEntityTypeList, List<Long> deleteRelEntityTypeList) {
		Long alarmId = alarmRuleInfoVO.getId();
		// 没有应用实体类型绑定关系的同步样式为：000000_VA010101，有绑定关系的同步样式为，最后一个为实体类型Id：000000_VA010101_12341231512341
		String prefix = alarmRuleInfoVO.getTenantId().concat(StringPool.UNDERSCORE).concat(alarmRuleInfoVO.getEntityCategoryCode());
		JSONArray jsonArray = new JSONArray();
		if ((AlarmConstant.VEHICLE_OVERSPEED_ALARM.equals(alarmRuleInfoVO.getEntityCategoryCode())
				|| AlarmConstant.VEHICLE_OUT_OF_AREA_ALARM.equals(alarmRuleInfoVO.getEntityCategoryCode())
				|| AlarmConstant.VEHICLE_STAY_ALARM.equals(alarmRuleInfoVO.getEntityCategoryCode()))) {
			if (CollectionUtil.isNotEmpty(addRelEntityTypeList)) {
				addRelEntityTypeList.forEach(entityType -> {
					JSONObject relObj = new JSONObject();
					relObj.put("relKey", prefix.concat(StringPool.UNDERSCORE) + entityType);
					relObj.put("alarmId", alarmId);
					// 超速告警2个等级，其他告警1个等级
					relObj.put("levelNum", alarmRuleInfoVO.getEntityCategoryCode().startsWith(AlarmConstant.VEHICLE_OVERSPEED_ALARM_TYPE) ? 2 : 1);
					relObj.put("optFlag", BigDataHttpClient.OptFlag.ADD);
					jsonArray.put(relObj);
				});
			}
			if (CollectionUtil.isNotEmpty(deleteRelEntityTypeList)) {
				deleteRelEntityTypeList.forEach(entityType -> {
					JSONObject relObj = new JSONObject();
					relObj.put("relKey", prefix.concat(StringPool.UNDERSCORE) + entityType);
					relObj.put("alarmId", alarmId);
					// 超速告警2个等级，其他告警1个等级
					relObj.put("levelNum", alarmRuleInfoVO.getEntityCategoryCode().startsWith(AlarmConstant.VEHICLE_OVERSPEED_ALARM_TYPE) ? 2 : 1);
					relObj.put("optFlag", BigDataHttpClient.OptFlag.REMOVE);
					jsonArray.put(relObj);
				});
			}
		} else {
			// 除车辆超速/车辆滞留/车辆越界，其他只同步一条关联关系给大数据
			JSONObject relObj = new JSONObject();
			relObj.put("relKey", prefix);
			relObj.put("alarmId", alarmId);
			relObj.put("levelNum", 1);
			if (BladeConstant.DB_IS_DELETED == alarmRuleInfoVO.getIsDeleted()) {
				relObj.put("optFlag", BigDataHttpClient.OptFlag.REMOVE);
			} else {
				relObj.put("optFlag", BigDataHttpClient.OptFlag.EDIT);
			}
			jsonArray.put(relObj);
		}
		try {
			if (!jsonArray.isEmpty()) {
				BigDataHttpClient.postDataToBigData(BigDataHttpClient.postAlarmRuleRelateEntity, jsonArray.toString());
			}
		} catch (IOException e) {
			// 记录失败日志
			log.error(StrUtil.format("告警规则[{}], id[{}]关联实体信息同步大数据接口失败，异常报文：{}", alarmRuleInfoVO.getName(), alarmId, e));
			throw new ServiceException("关联实体信息同步大数据接口失败!");
		}
	}
}
