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
import com.ai.apac.smartenv.alarm.entity.AlarmRuleExt;
import com.ai.apac.smartenv.alarm.mapper.AlarmRuleExtMapper;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleExtService;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleExtVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 告警规则参数表 服务实现类
 *
 * @author Blade
 * @since 2020-02-07
 */
@Slf4j
@Service
@AllArgsConstructor
public class AlarmRuleExtServiceImpl extends BaseServiceImpl<AlarmRuleExtMapper, AlarmRuleExt> implements IAlarmRuleExtService {

	@Override
	public IPage<AlarmRuleExtVO> selectAlarmRuleExtPage(IPage<AlarmRuleExtVO> page, AlarmRuleExtVO alarmRuleExt) {
		return page.setRecords(baseMapper.selectAlarmRuleExtPage(page, alarmRuleExt));
	}

	@Override
	public void removeByAlarmRuleId(@NonNull Long alarmRuleId) {
		LambdaQueryWrapper<AlarmRuleExt> alarmRuleExtQueryWrapper = new LambdaQueryWrapper<>();
		alarmRuleExtQueryWrapper.eq(AlarmRuleExt::getAlarmRuleId, alarmRuleId);
		this.remove(alarmRuleExtQueryWrapper);
	}

	@Override
	public List<AlarmRuleExt> listByAlarmRuleId(@NonNull Long alarmRuleId) {
		LambdaQueryWrapper<AlarmRuleExt> alarmRuleExtQueryWrapper = new LambdaQueryWrapper<>();
		alarmRuleExtQueryWrapper.eq(AlarmRuleExt::getAlarmRuleId, alarmRuleId);
		 return this.list(alarmRuleExtQueryWrapper);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public void saveOrUpdateAlarmRuleExtBatch(List<AlarmRuleExt> alarmRuleExtList) {
		alarmRuleExtList.forEach(alarmRuleExt -> {
			if (BeanUtil.isEmpty(alarmRuleExt.getId())) {
				this.save(alarmRuleExt);
			} else {
				this.updateById(alarmRuleExt);
			}
		});
	}

	/**
	 * 计算父子关系, 父属性按attrValueSeq升序排列
	 * @param alarmRuleExtVOS
	 * @return
	 */
	@Override
	public List<AlarmRuleExtVO> calculateRelationship(List<AlarmRuleExtVO> alarmRuleExtVOS) {
		Map<Long, AlarmRuleExtVO> parent = new HashMap<>();
		List<AlarmRuleExtVO> children = new ArrayList<>();
		alarmRuleExtVOS.forEach(alarmRuleExtVO -> {
			if (alarmRuleExtVO.getParentId() != null && alarmRuleExtVO.getParentId() > -1) {
				children.add(alarmRuleExtVO);
			} else {
				parent.put(alarmRuleExtVO.getId(), alarmRuleExtVO);
			}
		});
		children.forEach(alarmRuleExtVO -> {
			AlarmRuleExtVO aParent = parent.get(alarmRuleExtVO.getParentId());
			if (CollectionUtil.isNotEmpty(aParent.getExtensionList())) {
				aParent.getExtensionList().add(alarmRuleExtVO);
			} else {
				List<AlarmRuleExtVO> extensionList = new ArrayList<>();
				extensionList.add(alarmRuleExtVO);
				aParent.setExtensionList(extensionList);
			}
		});
		// 比较器
		Comparator<AlarmRuleExtVO> alarmRuleExtVOComparator = (o1, o2) -> {
			if (o1.getAttrSeq() == null && o2.getAttrSeq() == null) {
				return 0;
			} else if (o1.getAttrSeq() != null && o2.getAttrSeq() == null) {
				return 1;
			} else if (o1.getAttrSeq() == null && o2.getAttrSeq() != null) {
				return -1;
			} else {
				return o1.getAttrSeq() - o2.getAttrSeq();
			}
		};
		// 只比较父属性，目前子属性只配一条，暂不排序
		List<AlarmRuleExtVO> parentValues = new ArrayList<>(parent.values());
		parentValues.sort(alarmRuleExtVOComparator);
		return parentValues;
	}
}
