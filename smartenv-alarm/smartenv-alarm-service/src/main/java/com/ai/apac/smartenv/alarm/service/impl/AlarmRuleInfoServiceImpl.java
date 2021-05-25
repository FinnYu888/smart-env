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

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.ai.apac.smartenv.alarm.constant.AlarmLevelEnum;
import com.ai.apac.smartenv.alarm.dto.AlarmRuleInfoDTO;
import com.ai.apac.smartenv.alarm.dto.VehicleTypeQueryDTO;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleExt;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleRel;
import com.ai.apac.smartenv.alarm.mapper.AlarmRuleInfoMapper;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleExtService;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleInfoService;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleRelService;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleExtVO;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleInfoVO;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleRelVO;
import com.ai.apac.smartenv.alarm.wrapper.AlarmRuleExtWrapper;
import com.ai.apac.smartenv.alarm.wrapper.AlarmRuleInfoWrapper;
import com.ai.apac.smartenv.alarm.wrapper.AlarmRuleRelWrapper;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCategoryCache;
import com.ai.apac.smartenv.vehicle.feign.IVehicleCategoryClient;
import com.ai.apac.smartenv.vehicle.vo.VehicleCategoryVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tenant.constant.TenantConstant;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 告警规则基本信息表 服务实现类
 *
 * @author Blade
 * @since 2020-02-15
 */
@Slf4j
@Service
@AllArgsConstructor
public class AlarmRuleInfoServiceImpl extends BaseServiceImpl<AlarmRuleInfoMapper, AlarmRuleInfo> implements IAlarmRuleInfoService {

	private IAlarmRuleExtService alarmRuleExtService;
	
	private IAlarmRuleRelService alarmRuleRelService;

	private IEntityCategoryClient entityCategoryClient;

	private IVehicleCategoryClient vehicleCategoryClient;
	
	@Override
	public IPage<AlarmRuleInfoVO> selectAlarmRuleInfoPage(IPage<AlarmRuleInfoVO> page, AlarmRuleInfoVO alarmRuleInfo) {
		return page.setRecords(baseMapper.selectAlarmRuleInfoPage(page, alarmRuleInfo));
	}

	/**
	 * 点击新增告警规则时组织告警信息及参数供前端展示
	 * @param alarmRuleInfoVO
	 * @return
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public AlarmRuleInfoVO constructNewAlarmRuleInfo(AlarmRuleInfoVO alarmRuleInfoVO) {
		// 告警规则规则类型ID必传
		Long entityCategoryId = alarmRuleInfoVO.getEntityCategoryId();
		if (entityCategoryId == null) {
			throw new ServiceException("告警规则类型Id为空");
		}
		String tenantId = alarmRuleInfoVO.getTenantId();
		QueryWrapper<AlarmRuleInfo> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(AlarmRuleInfo::getTenantId, TenantConstant.DEFAULT_TENANT_ID);
		queryWrapper.lambda().eq(AlarmRuleInfo::getEntityCategoryId, entityCategoryId);
		queryWrapper.lambda().ne(AlarmRuleInfo::getIsDeleted, BladeConstant.DB_IS_DELETED);
		AlarmRuleInfo oldRule = this.getOne(queryWrapper);
		if (oldRule != null) {
			Long oldRuleId = oldRule.getId();
			AlarmRuleInfo newRule = setAlarmRuleInfoDefaultValue(oldRule);
			newRule.setName(null); // 前端不要名字
			newRule.setStatus(AlarmConstant.Status.NO); // 默认规则停用
			newRule.setTenantId(tenantId);
			alarmRuleInfoVO = AlarmRuleInfoWrapper.build().entityVO(newRule);
			String entityCategoryName = entityCategoryClient.getCategoryName(alarmRuleInfoVO.getEntityCategoryId()).getData();
			alarmRuleInfoVO.setEntityCategoryName(entityCategoryName);
			List<AlarmRuleExt> alarmRuleExts = alarmRuleExtService.listByAlarmRuleId(oldRuleId);
			List<AlarmRuleExtVO> extParents = alarmRuleExtService.calculateRelationship(AlarmRuleExtWrapper.build().listVO(alarmRuleExts));
			List<AlarmRuleExtVO> newExtParents = new ArrayList<>();
			extParents.forEach(extParent -> {
				AlarmRuleExt newExtParent = setAlarmRuleInfoExtDefaultValue(null, extParent, null);
				newExtParent.setTenantId(tenantId);
				AlarmRuleExtVO newExtParentVO = AlarmRuleExtWrapper.build().entityVO(newExtParent);
				newExtParentVO.setAlarmLevelName(AlarmLevelEnum.getName(newExtParentVO.getAttrLevel()));
				List<AlarmRuleExtVO> extensionList = extParent.getExtensionList();
				if (CollectionUtils.isNotEmpty(extensionList)) {
					List<AlarmRuleExtVO> newExtensionList = new ArrayList<>();
					extensionList.forEach(oldExtChild ->{
						AlarmRuleExt newExtChild = setAlarmRuleInfoExtDefaultValue(null, oldExtChild, null);
						newExtChild.setTenantId(tenantId);
						newExtensionList.add(AlarmRuleExtWrapper.build().entityVO(newExtChild));
					});
					newExtParentVO.setExtensionList(newExtensionList);
				}
				newExtParents.add(newExtParentVO);
			});
			alarmRuleInfoVO.setAlarmRuleExtVOList(newExtParents);
			return alarmRuleInfoVO;
		}
		return null;
	}
	
	/**
	 * 新增或者更新告警规则信息
	 * @param alarmRuleInfoVO
	 * @return
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public boolean saveOrUpdateAlarmRuleInfo(AlarmRuleInfoVO alarmRuleInfoVO) {
		String optFlag = "";
		if (alarmRuleInfoVO.getId() == null || alarmRuleInfoVO.getId() <= 0) {
			optFlag = BigDataHttpClient.OptFlag.ADD;
			alarmRuleInfoVO.setId(null);
		} else {
			optFlag = BigDataHttpClient.OptFlag.EDIT;
		}
		String entityCategoryCode = StringUtils.isNotBlank(alarmRuleInfoVO.getEntityCategoryCode()) ? alarmRuleInfoVO.getEntityCategoryCode() : entityCategoryClient.getCategoryCode(alarmRuleInfoVO.getEntityCategoryId()).getData();
		alarmRuleInfoVO.setEntityCategoryCode(entityCategoryCode);
		// 校验参数配置
		validAlarmRuleConfig(entityCategoryCode, alarmRuleInfoVO);
		// 校验规则类型和绑定实体类型的匹配关系
		validateAlarmRuleRelationshipMatch(alarmRuleInfoVO);
		boolean success = this.saveOrUpdate(alarmRuleInfoVO);
		if (success) {
			// 根据扩展属性信息填充告警规则表
			alarmRuleInfoVO = fillAlarmRuleExtAttrs(entityCategoryCode, alarmRuleInfoVO);
			// 再更新(remark字段)
			this.updateById(alarmRuleInfoVO);

			// 实体类型与告警规则的绑定关系，除主动安全告警（驾驶异常）/人员手表SOS外都要同步大数据
			if (!AlarmConstant.VEHICLE_INITIATIVE_ALARM.equals(alarmRuleInfoVO.getEntityCategoryCode())
					&& !AlarmConstant.PERSON_WATCH_SOS_ALARM.equals(alarmRuleInfoVO.getEntityCategoryCode())) {
				alarmRuleInfoVO = fillAlarmRuleRelAttrs(alarmRuleInfoVO);
			}
			// 只有开启状态才同步大数据并且不是主动安全/人员SOS才同步大数据
			if (AlarmConstant.Status.YES == alarmRuleInfoVO.getStatus()
					&& !AlarmConstant.VEHICLE_INITIATIVE_ALARM.equals(alarmRuleInfoVO.getEntityCategoryCode())
					&& !AlarmConstant.PERSON_WATCH_SOS_ALARM.equals(alarmRuleInfoVO.getEntityCategoryCode())) {
				// 同步大数据
				postAlarmRuleDataToBigData(alarmRuleInfoVO, optFlag);
			}
		}
		return success;
	}


	/**
	 * 查询告警规则详情及其扩展信息并分页
	 * @param alarmRuleInfoDTO
	 * @param query
	 * @return
	 */
	@Override
	public IPage<AlarmRuleInfoVO> detailsAndDetailExts(AlarmRuleInfoDTO alarmRuleInfoDTO, Query query) {
		Long alarmRuleId = alarmRuleInfoDTO.getId();
		List<Long> relAlarmRuleIdList = new ArrayList<>();
		if (alarmRuleId == null || alarmRuleId <= 0) {
			String entityCategoryCode = alarmRuleInfoDTO.getEntityCategoryCode();
			// 告警类型前缀必传（VA/PA）
			if (StringUtils.isBlank(entityCategoryCode)
					|| (!entityCategoryCode.equals(AlarmConstant.VEHICLE_ALARM_PREFIX) && !entityCategoryCode.equals(AlarmConstant.PERSON_ALARM_PREFIX))){
				throw new ServiceException("需要传入告警类型编码前缀！");
			}
			// 如果传了告警规则的关联实体类型Id，则需要查告警规则关联表，关联表没查到数据，直接返回
			Long relEntityType = alarmRuleInfoDTO.getRelEntityType();
			if (relEntityType != null) {
				LambdaQueryWrapper<AlarmRuleRel> ruleRelQuery = new LambdaQueryWrapper<>();
				ruleRelQuery.eq(AlarmRuleRel::getEntityType, relEntityType);
				ruleRelQuery.eq(AlarmRuleRel::getTenantId, alarmRuleInfoDTO.getTenantId());
				ruleRelQuery.eq(AlarmRuleRel::getIsDeleted, BladeConstant.DB_NOT_DELETED);
				List<AlarmRuleRel> ruleRelList = alarmRuleRelService.list(ruleRelQuery);
				if (CollectionUtil.isEmpty(ruleRelList)) {
					return new Page<>(query.getCurrent(), query.getSize(), 0);
				} else {
					relAlarmRuleIdList = ruleRelList.stream().map(AlarmRuleRel::getAlarmRuleId).collect(Collectors.toList());
				}
			}
		}
		IPage<AlarmRuleInfo> pages = this.listAlarmRuleInfoByCondition(alarmRuleInfoDTO, relAlarmRuleIdList, query);
		IPage<AlarmRuleInfoVO> alarmRuleInfoVOIPage = AlarmRuleInfoWrapper.build().pageVO(pages);
		List<AlarmRuleInfoVO> alarmRuleInfoVOList = alarmRuleInfoVOIPage.getRecords();
		List<EntityCategory> entityCategoryList = entityCategoryClient.getCategoryByType(CommonConstant.ENTITY_TYPE.ALARM.toString()).getData();
		Map<Long, String> entityCategoryMap = new HashMap<>();
		entityCategoryList.forEach(entityCategory -> entityCategoryMap.put(entityCategory.getId(), entityCategory.getCategoryName()));
		if (CollectionUtil.isNotEmpty(alarmRuleInfoVOIPage.getRecords())) {
			alarmRuleInfoVOList.forEach(alarmRuleInfoVO -> {
				// 取实体规则名称
				alarmRuleInfoVO.setEntityCategoryName(entityCategoryMap.get(alarmRuleInfoVO.getEntityCategoryId()));
				// 规则扩展表
				AlarmRuleExt alarmRuleExtQuery = new AlarmRuleExt();
				alarmRuleExtQuery.setAlarmRuleId(alarmRuleInfoVO.getId());
				QueryWrapper<AlarmRuleExt> extQueryWrapper = new QueryWrapper<>();
				extQueryWrapper.lambda().eq(AlarmRuleExt::getAlarmRuleId, alarmRuleInfoVO.getId());
				extQueryWrapper.lambda().eq(AlarmRuleExt::getTenantId, alarmRuleInfoVO.getTenantId());
				extQueryWrapper.lambda().eq(AlarmRuleExt::getIsDeleted, BladeConstant.DB_NOT_DELETED);
				extQueryWrapper.lambda().orderByAsc(AlarmRuleExt::getAttrSeq, AlarmRuleExt::getAttrId);
				List<AlarmRuleExt> alarmRuleExtList = alarmRuleExtService.list(extQueryWrapper);
				if (CollectionUtil.isNotEmpty(alarmRuleExtList)) {
					List<AlarmRuleExtVO> alarmRuleExtVOS = AlarmRuleExtWrapper.build().listVO(alarmRuleExtList);
					// 计算父子关系
					List<AlarmRuleExtVO> relationshipExtList = alarmRuleExtService.calculateRelationship(alarmRuleExtVOS);
					// 等级名称
					relationshipExtList.forEach(alarmRuleExtVO -> alarmRuleExtVO.setAlarmLevelName(AlarmLevelEnum.getName(alarmRuleExtVO.getAttrLevel())));
					alarmRuleInfoVO.setAlarmRuleExtVOList(relationshipExtList);
				}
				// 规则关联表
				QueryWrapper<AlarmRuleRel> relQueryWrapper = new QueryWrapper<>();
				relQueryWrapper.lambda().eq(AlarmRuleRel::getAlarmRuleId, alarmRuleInfoVO.getId());
				relQueryWrapper.lambda().eq(AlarmRuleRel::getTenantId, alarmRuleInfoVO.getTenantId());
				relQueryWrapper.lambda().eq(AlarmRuleRel::getIsDeleted, BladeConstant.DB_NOT_DELETED);
				List<AlarmRuleRel> alarmRuleRelList = alarmRuleRelService.list(relQueryWrapper);
				if (CollectionUtil.isNotEmpty(alarmRuleRelList)) {
					alarmRuleInfoVO.setAlarmRuleRelVOList(AlarmRuleRelWrapper.build().listVO(alarmRuleRelList));
					Optional<String> typeNames = alarmRuleRelList.stream().map(AlarmRuleRel::getEntityTypeName).reduce((a, b) -> a.concat(StringPool.COMMA).concat(b));
					typeNames.ifPresent(alarmRuleInfoVO::setRelateEntityTypeNames);
				}
			});
		}
		return alarmRuleInfoVOIPage;
	}

	/**
	 * 根据条件查询告警规则表
	 * @param alarmRuleInfo
	 * @param alarmRuleIdList
	 * @param query
	 * @return
	 */
	@Override
	public IPage<AlarmRuleInfo> listAlarmRuleInfoByCondition(AlarmRuleInfoDTO alarmRuleInfo, List<Long> alarmRuleIdList, Query query) {
		// 告警改造之后，告警类型查询不涉及级联关系，直接根据前端传的告警类型进行查询 20210112 start
		/*alarmRuleInfo.setEntityCategoryId(null);
		QueryWrapper<AlarmRuleInfo> queryWrapper = Condition.getQueryWrapper(alarmRuleInfo);
		if (entityCategoryId != null) {
			List<EntityCategory> data = entityCategoryClient.getCategoryByType("1").getData();
			List<Long> ids = listEntityCategoryId(entityCategoryId, data);
			queryWrapper.in(CollectionUtil.isNotEmpty(ids), "entity_category_id", ids);
		}*/
		// 告警改造之后，告警类型查询不涉及级联关系，直接根据前端传的告警类型进行查询 20210112 end
		// entityCategoryCode = "VA" || "PA"
		String entityCategoryCode = alarmRuleInfo.getEntityCategoryCode();
		alarmRuleInfo.setEntityCategoryCode(null);
		QueryWrapper<AlarmRuleInfo> queryWrapper = Condition.getQueryWrapper(alarmRuleInfo);
		if (StringUtils.isNotBlank(entityCategoryCode)) {
			queryWrapper.lambda().likeRight(AlarmRuleInfo::getEntityCategoryCode, entityCategoryCode);
		}
		if (CollectionUtil.isNotEmpty(alarmRuleIdList)) {
			queryWrapper.lambda().in(AlarmRuleInfo::getId, alarmRuleIdList);
		}
		queryWrapper.lambda().eq(AlarmRuleInfo::getTenantId, AuthUtil.getTenantId());
		return this.page(Condition.getPage(query), queryWrapper);
	}

	/**
	 * 从类型数据中找出所有当前节点和子节点的id
	 * @param entityCategoryId
	 * @param data
	 */
	@Override
	public List<Long> listEntityCategoryId(Long entityCategoryId, List<EntityCategory> data) {
		List<Long> ids = new ArrayList<>();
		ids.add(entityCategoryId);
		data.forEach(entityCategory -> {
			if (entityCategory.getParentCategoryId() != null && entityCategory.getParentCategoryId().equals(entityCategoryId)) {
				List<Long> tempids = listEntityCategoryId(entityCategory.getId(), data);
				if (CollectionUtil.isNotEmpty(tempids)) {
					ids.addAll(tempids);
				}
			}
		});
		return ids;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public AlarmRuleInfoVO fillAlarmRuleExtAttrs(String entityCategoryCode, AlarmRuleInfoVO alarmRuleInfoVO) {
		Long alarmRuleId = alarmRuleInfoVO.getId();
		List<AlarmRuleExtVO> alarmRuleExtVOList = alarmRuleInfoVO.getAlarmRuleExtVOList();
		alarmRuleInfoVO.setRemark(""); // 清空之前的备注
		String paramMessage = ""; 
		if (CollectionUtil.isNotEmpty(alarmRuleExtVOList)) {
			// 根据扩展信息拼接remark
			for (AlarmRuleExtVO alarmRuleExtVO : alarmRuleExtVOList) {
				List<AlarmRuleExtVO> extensionTempList = alarmRuleExtVO.getExtensionList();
				// 如果是勾选框，并且已勾选，则继续拼接信息
				if ((alarmRuleExtVO.getInputType().equals((long) AlarmConstant.AttrInputType.CHECK_BOX)
						&& AlarmConstant.AttrInputType.CheckBoxValue.SELECTED.equals(alarmRuleExtVO.getInputValue()))) {
					paramMessage = concatMessage(paramMessage, alarmRuleExtVO);
					for (AlarmRuleExtVO extension : extensionTempList) {
						paramMessage = concatMessage(paramMessage, extension);
					}
				} else {
					paramMessage = concatMessage(paramMessage, alarmRuleExtVO);
				}
			}
			if (!"".equals(paramMessage) && paramMessage.length() > 0) {
				paramMessage = paramMessage.substring(0, paramMessage.length() - 1);
				alarmRuleInfoVO.setRemark(paramMessage);
			}
			List<AlarmRuleExtVO> extensionVOList = new ArrayList<>();
			// 保存扩展数据信息，并保留属性间父子关系
			alarmRuleExtVOList.forEach(alarmRuleExtVO -> {
				// 框架在返回给前端的时候空字段会填充-1
				if (alarmRuleExtVO.getId() != null && alarmRuleExtVO.getId() <= 0) {
					alarmRuleExtVO.setId(null);
				}
				alarmRuleExtVO.setAlarmRuleId(alarmRuleId);
				AlarmRuleExt alarmRuleExt = AlarmRuleExtWrapper.build().voEntity(alarmRuleExtVO);
				alarmRuleExtService.saveOrUpdate(alarmRuleExt);
				Long parentId = alarmRuleExt.getId();
				List<AlarmRuleExtVO> extensionTempList = alarmRuleExtVO.getExtensionList();
				extensionTempList.forEach(extension -> {
					// 框架在返回给前端的时候空字段会填充-1
					if (extension.getId() != null && extension.getId() <= 0) {
						extension.setId(null);
					}
					extension.setAlarmRuleId(alarmRuleId);
					extension.setParentId(parentId);
				});
				// 扩展属性的子扩展属性和属性定义信息
				extensionVOList.addAll(extensionTempList);
			});
			if (extensionVOList.size() > 0) {
				// 新增或者更新扩展信息
				List<AlarmRuleExt> extensionList = AlarmRuleExtWrapper.build().listEntity(extensionVOList);
				alarmRuleExtService.saveOrUpdateAlarmRuleExtBatch(extensionList);
			}
        }
		return alarmRuleInfoVO;
	}

	/**
	 * 校验相同规则不同等级的规则是否有配置重复
	 * @param entityCategoryCode
	 * @param alarmRuleInfoVO
	 */
	@Override
	public void validAlarmRuleConfig(String entityCategoryCode, AlarmRuleInfoVO alarmRuleInfoVO) {
		String ruleName = alarmRuleInfoVO.getName();
		if (StringUtils.isBlank(ruleName)) {
			throw new ServiceException("告警规则名称不能为空！");
		}
		List<AlarmRuleExtVO> originExts = alarmRuleInfoVO.getAlarmRuleExtVOList();
		if (CollectionUtils.isEmpty(originExts)) {
			return;
		}
//		QueryWrapper<AlarmRuleExt> extQueryWrapper = new QueryWrapper<>();
		// 按属性值校验
		if (entityCategoryCode.equals(AlarmConstant.VEHICLE_OVERSPEED_ALARM)) {
			Map<Integer, String> originSpeedMap = originExts.stream()
					.filter(extVO -> AlarmConstant.OverSpeedAlarmAttr.SPEED.equals(extVO.getAttrCode()))
					.collect(Collectors.toMap(AlarmRuleExtVO::getAttrLevel, AlarmRuleExtVO::getInputValue));
			if (!originSpeedMap.isEmpty()) {
				originSpeedMap.forEach((level, value) -> strToNumber(value, AlarmLevelEnum.getName(level) + ",速度参数必须是正整数"));
				String level1Speed = originSpeedMap.get(AlarmConstant.AlarmLevel.NORMAL);
				String level2Speed = originSpeedMap.get(AlarmConstant.AlarmLevel.EMERGENCY);
				if (StringUtils.isNotBlank(level1Speed) && StringUtils.isNotBlank(level1Speed) && Integer.parseInt(level2Speed) <= Integer.parseInt(level1Speed)) {
					throw new ServiceException("紧急告警速度需要大于一般告警速度！");
				}
			} else {
				throw new ServiceException(alarmRuleInfoVO.getName() + "告警规则，需要配置速度属性值");
			}
		}
//		else if (entityCategoryCode.startsWith(AlarmConstant.VEHICLE_AREA_ALARM_TYPE) || entityCategoryCode.startsWith(AlarmConstant.PERSON_VIOLATION_ALARM_TYPE)) {
//			List<String> outOfAreaDurations = originExts.stream()
//					.filter(extVO -> AlarmConstant.OutOfAreaAlarm.DURATION.equals(extVO.getAttrCode()))
//					.map(AlarmRuleExtVO::getInputValue)
//					.collect(Collectors.toList());
//			if (CollectionUtils.isNotEmpty(outOfAreaDurations)) {
//				strToNumber(outOfAreaDurations.get(0), alarmRuleInfoVO.getName() + "，持续时间参数必须是正整数");
//			} else {
//				throw new ServiceException(alarmRuleInfoVO.getName() + "告警规则，需要配置持续时间属性值");
//			}
//			extQueryWrapper.lambda().eq(AlarmRuleExt::getAttrCode, AlarmConstant.OutOfAreaAlarm.DURATION);
//			extQueryWrapper.lambda().eq(AlarmRuleExt::getInputValue, outOfAreaDurations.get(0));
//			int count = alarmRuleExtService.count(extQueryWrapper);
//			if (count > 0) {
//				throw new ServiceException(StrUtil.format(alarmRuleInfoVO.getName() + "告警规则的持续时间参数【{}】，与同类型其他规则的持续时间参数相同，请修改！", outOfAreaDurations.get(0)));
//			}
//		} else if (entityCategoryCode.startsWith(AlarmConstant.VEHICLE_NON_DESIGNATED_PLACE_ALARM)) {
//			Map<String, String> collect = originExts.stream().collect(Collectors.toMap(AlarmRuleExtVO::getAttrCode, AlarmRuleExtVO::getInputValue));
//			List<AlarmRuleExt> alarmRuleExts = alarmRuleExtService.list(extQueryWrapper);
//			Collection<List<AlarmRuleExt>> values = alarmRuleExts.stream().collect(Collectors.groupingBy(AlarmRuleExt::getAlarmRuleId)).values();
//			List<Boolean> flagList = new ArrayList<>();
//			Iterator<List<AlarmRuleExt>> iterator = values.iterator();
//			int count = 0;
//			while (iterator.hasNext()) {
//				flagList.add(true); // 默认true
//				List<AlarmRuleExt> next = iterator.next();
//				for (AlarmRuleExt alarmRuleExt : next) {
//					if (StringUtils.isBlank(alarmRuleExt.getInputValue())
//							|| StringUtils.isBlank(collect.get(alarmRuleExt.getAttrCode()))
//							|| !alarmRuleExt.getInputValue().equals(collect.get(alarmRuleExt.getAttrCode()))) {
//						flagList.set(count, false); // false 用来记录不同
//						break;
//					}
//				}
//				count++;
//			}
//			flagList.forEach(aBoolean -> {
//				if (aBoolean) {
//					throw new ServiceException("与同类型其他规则的参数配置相同，请修改！");
//				}
//			});
//		} else if (entityCategoryCode.startsWith(AlarmConstant.VEHICLE_IRREGULAR_OPERATION_ALARM)) {
//			// 选中的告警属性及其持续时间参数的键值对<"NOT_TURN_OFF_WHEN_PARKING", "10">
//			Map<String, String> attrValueMap = originExts.stream()
//					.filter(alarmRuleExtVO -> AlarmConstant.AttrInputType.CheckBoxValue.SELECTED.equals(alarmRuleExtVO.getInputValue()))
//					.collect(Collectors.toMap(AlarmRuleExtVO::getAttrCode, alarmRuleExtVO -> alarmRuleExtVO.getExtensionList().get(0).getInputValue()));
//			Map<String, String> codeNameMap = originExts.stream()
//					.filter(alarmRuleExtVO -> AlarmConstant.AttrInputType.CheckBoxValue.SELECTED.equals(alarmRuleExtVO.getInputValue()))
//					.collect(Collectors.toMap(AlarmRuleExtVO::getAttrCode, AlarmRuleExt::getAttrName));
//			// 校验属性值
//			attrValueMap.forEach((k, v) ->{
//				strToNumber(v, codeNameMap.get(k) + "的持续时间属性必须是正整数！");
//			});
//			List<AlarmRuleExt> alarmRuleExtList = alarmRuleExtService.list(extQueryWrapper);
//			List<AlarmRuleExtVO> alarmRuleExtVOS = AlarmRuleExtWrapper.build().listVO(alarmRuleExtList);
//			// 计算父子关系
//			List<AlarmRuleExtVO> relationshipExtList = alarmRuleExtService.calculateRelationship(alarmRuleExtVOS);
//			attrValueMap.keySet().forEach(key -> {
//				relationshipExtList.forEach(record -> {
//					if (AlarmConstant.AttrInputType.CheckBoxValue.SELECTED.equals(record.getInputValue())
//                            && key.equals(record.getAttrCode())) {
//						if (attrValueMap.get(key).equals(record.getExtensionList().get(0).getInputValue())) {
//							throw new ServiceException(codeNameMap.get(key) + "属性值的持续时间与已有规则相同，请修改！");
//						}
//					}
//				});
//			});
//		} else if (entityCategoryCode.startsWith(AlarmConstant.PERSON_LOWEST_BATTERY_ALARM)) {
//			List<String> lowestBatterys = originExts.stream()
//					.filter(extVO -> AlarmConstant.LOWEST_BATTERY_VALUE.equals(extVO.getAttrCode()))
//					.map(AlarmRuleExtVO::getInputValue)
//					.collect(Collectors.toList());
//			if (CollectionUtils.isNotEmpty(lowestBatterys)) {
//				strToNumber(lowestBatterys.get(0), alarmRuleInfoVO.getName() + "，电量最低值参数必须是正整数");
//			} else {
//				throw new ServiceException(alarmRuleInfoVO.getName() + "告警规则，需要配置电量最低值属性值");
//			}
//			extQueryWrapper.lambda().eq(AlarmRuleExt::getAttrCode, AlarmConstant.LOWEST_BATTERY_VALUE);
//			extQueryWrapper.lambda().eq(AlarmRuleExt::getInputValue, lowestBatterys.get(0));
//			int count = alarmRuleExtService.count(extQueryWrapper);
//			if (count > 0) {
//				throw new ServiceException(StrUtil.format(alarmRuleInfoVO.getName() + "告警规则的电量最低值参数【{}】，与同类型其他规则的电量最低值参数相同，请修改！", lowestBatterys.get(0)));
//			}
//		} else if (entityCategoryCode.startsWith(AlarmConstant.PERSON_BLOOD_PRESSURE_ALARM)) {
//			Integer minBloodPressure = null, maxBloodPressure = null, duration = null;
//			for (AlarmRuleExtVO originExt : originExts) {
//				if (AlarmConstant.BloodPressureAlarm.MIN_BLOOD_PRESSURE.equals(originExt.getAttrCode())) {
//					String exceptionMsg = alarmRuleInfoVO.getName() + "，血压最低值参数必须是正整数";
//					minBloodPressure = strToNumber(originExt.getInputValue(), exceptionMsg);
//				} else if (AlarmConstant.BloodPressureAlarm.MAX_BLOOD_PRESSURE.equals(originExt.getAttrCode())) {
//					String exceptionMsg = alarmRuleInfoVO.getName() + "，血压最高值参数必须是正整数";
//					maxBloodPressure = strToNumber(originExt.getInputValue(), exceptionMsg);
//				} else if (AlarmConstant.BloodPressureAlarm.DURATION.equals(originExt.getAttrCode())) {
//					String exceptionMsg = alarmRuleInfoVO.getName() + "，持续时间参数必须是正整数";
//					duration = strToNumber(originExt.getInputValue(), exceptionMsg);
//				}
//			}
//			if (minBloodPressure == null || maxBloodPressure == null || duration == null) {
//				throw new ServiceException("血压最低值、血压最高值和持续时间都必填");
//			}
//			List<AlarmRuleExt> alarmRuleExts = alarmRuleExtService.list(extQueryWrapper);
//			Map<Long, List<AlarmRuleExt>> collect = alarmRuleExts.stream().collect(Collectors.groupingBy(AlarmRuleExt::getAlarmRuleId));
//			Integer finalMinBloodPressure = minBloodPressure;
//			Integer finalMaxBloodPressure = maxBloodPressure;
//			Integer finalDuration = duration;
//			collect.values().forEach(values -> {
//				Integer minValue = null, maxValue = null, durationValue = null;
//				for (AlarmRuleExt value : values) {
//					if (AlarmConstant.BloodPressureAlarm.MIN_BLOOD_PRESSURE.equals(value.getAttrCode())) {
//						minValue = value.getInputValue() == null ? null :Integer.valueOf(value.getInputValue());
//					} else if (AlarmConstant.BloodPressureAlarm.MAX_BLOOD_PRESSURE.equals(value.getAttrCode())) {
//						maxValue = value.getInputValue() == null ? null :Integer.valueOf(value.getInputValue());
//					} else if (AlarmConstant.BloodPressureAlarm.DURATION.equals(value.getAttrCode())) {
//						durationValue = value.getInputValue() == null ? null :Integer.valueOf(value.getInputValue());
//					}
//				}
//				if (finalMinBloodPressure.equals(minValue) && finalMaxBloodPressure.equals(maxValue) && finalDuration.equals(durationValue)) {
//					Long alarmRuleId = values.get(0).getAlarmRuleId();
//					AlarmRuleInfo alarmRuleInfo = this.getById(alarmRuleId);
////					throw new ServiceException(StrUtil.format("参数配置与告警级别为【{}】的完全一致，请修改！", AlarmLevelEnum.getName(alarmRuleInfo.getAlarmLevel())));
//				}
//			});
//		} else if (entityCategoryCode.startsWith(AlarmConstant.PERSON_HERAT_RATE_ALARM)) {
//			Integer minHeartRate = null, maxHeartRate = null, duration = null;
//			for (AlarmRuleExtVO originExt : originExts) {
//				if (AlarmConstant.HeartRateAlarm.HEART_RATE_MIN.equals(originExt.getAttrCode())) {
//					String exceptionMsg = alarmRuleInfoVO.getName() + "，心率最低值参数必须是正整数";
//					minHeartRate = strToNumber(originExt.getInputValue(), exceptionMsg);
//				} else if (AlarmConstant.HeartRateAlarm.HEART_RATE_MAX.equals(originExt.getAttrCode())) {
//					String exceptionMsg = alarmRuleInfoVO.getName() + "，心率最高值参数必须是正整数";
//					maxHeartRate = strToNumber(originExt.getInputValue(), exceptionMsg);
//				} else if (AlarmConstant.HeartRateAlarm.DURATION.equals(originExt.getAttrCode())) {
//					String exceptionMsg = alarmRuleInfoVO.getName() + "，持续时间参数必须是正整数";
//					duration = strToNumber(originExt.getInputValue(), exceptionMsg);
//				}
//			}
//			if (minHeartRate == null || maxHeartRate == null || duration == null) {
//				throw new ServiceException("心率最低值、心率最高值和持续时间都必填");
//			}
//			List<AlarmRuleExt> alarmRuleExts = alarmRuleExtService.list(extQueryWrapper);
//			Map<Long, List<AlarmRuleExt>> collect = alarmRuleExts.stream().collect(Collectors.groupingBy(AlarmRuleExt::getAlarmRuleId));
//			Integer finalMinHeartRate = minHeartRate;
//			Integer finalMaxHeartRate = maxHeartRate;
//			Integer finalDuration = duration;
//			collect.values().forEach(values -> {
//				Integer minValue = null, maxValue = null, durationValue = null;
//				for (AlarmRuleExt value : values) {
//					if (AlarmConstant.HeartRateAlarm.HEART_RATE_MIN.equals(value.getAttrCode())) {
//						minValue = value.getInputValue() == null ? null :Integer.valueOf(value.getInputValue());
//					} else if (AlarmConstant.HeartRateAlarm.HEART_RATE_MAX.equals(value.getAttrCode())) {
//						maxValue = value.getInputValue() == null ? null :Integer.valueOf(value.getInputValue());
//					} else if (AlarmConstant.HeartRateAlarm.DURATION.equals(value.getAttrCode())) {
//						durationValue = value.getInputValue() == null ? null :Integer.valueOf(value.getInputValue());
//					}
//				}
//				if (finalMinHeartRate.equals(minValue) && finalMaxHeartRate.equals(maxValue) && finalDuration.equals(durationValue)) {
//					Long alarmRuleId = values.get(0).getAlarmRuleId();
//					AlarmRuleInfo alarmRuleInfo = this.getById(alarmRuleId);
////					throw new ServiceException(StrUtil.format("参数配置与告警级别为【{}】的完全一致，请修改！", AlarmLevelEnum.getName(alarmRuleInfo.getAlarmLevel())));
//				}
//			});
//		}
	}
	
	/**
	 * 字符串转数字 >= 0
	 * @param string
	 * @return
	 */
	private Integer strToNumber(String string, String exceptionMsg) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		if (StringUtils.isNotBlank(string) && pattern.matcher(string).matches() && (Integer.parseInt(string) > 0)) {
			return Integer.parseInt(string);
		}
		throw new ServiceException(exceptionMsg);
	}
	
	/**
	 * 拼接remark
	 * @param message
	 * @param alarmRuleExtVO
	 * @return
	 */
	private String concatMessage(String message, AlarmRuleExtVO alarmRuleExtVO) {
		// 拼参数信息
		if (alarmRuleExtVO.getInputValue() != null && !"".equals(alarmRuleExtVO.getInputValue())) {
			if (alarmRuleExtVO.getInputType().equals((long)AlarmConstant.AttrInputType.TEXT_INPUT)) {
				String alarmLevel = alarmRuleExtVO.getAttrLevel() != null ? AlarmLevelEnum.getName(alarmRuleExtVO.getAttrLevel()).concat(":") : "";
				message = message.concat(alarmLevel).concat(alarmRuleExtVO.getAttrName()).concat(":(").concat(alarmRuleExtVO.getInputValue()).concat(")")
						.concat(alarmRuleExtVO.getMeasurementUnitName()).concat(",");
			} else if (alarmRuleExtVO.getInputType().equals((long)AlarmConstant.AttrInputType.CHECK_BOX)
					&& AlarmConstant.AttrInputType.CheckBoxValue.SELECTED.equals(alarmRuleExtVO.getInputValue())) {
				message = message.concat(alarmRuleExtVO.getAttrName()).concat(",");
			}
		}
		return message;
	}
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public AlarmRuleInfoVO fillAlarmRuleRelAttrs(AlarmRuleInfoVO alarmRuleInfoVO) {
		// 前端传入的绑定关系
		List<AlarmRuleRelVO> relationshipVOList = alarmRuleInfoVO.getAlarmRuleRelVOList();
		// 车辆越界/车辆滞留/车辆超速才有绑定实体类型
		if (CollectionUtil.isNotEmpty(relationshipVOList)
				&& (AlarmConstant.VEHICLE_OVERSPEED_ALARM.equals(alarmRuleInfoVO.getEntityCategoryCode())
				|| AlarmConstant.VEHICLE_OUT_OF_AREA_ALARM.equals(alarmRuleInfoVO.getEntityCategoryCode())
				|| AlarmConstant.VEHICLE_STAY_ALARM.equals(alarmRuleInfoVO.getEntityCategoryCode()))) {
			// 先查询已存在的关联关系
			LambdaQueryWrapper<AlarmRuleRel> relQuery = new LambdaQueryWrapper<>();
			relQuery.eq(AlarmRuleRel::getAlarmRuleId, alarmRuleInfoVO.getId());
			relQuery.eq(AlarmRuleRel::getTenantId, alarmRuleInfoVO.getTenantId());
			relQuery.eq(AlarmRuleRel::getIsDeleted, BladeConstant.DB_NOT_DELETED);
			List<AlarmRuleRel> existRelList = alarmRuleRelService.list(relQuery);
			Map<Long, Long> existVehicleTypeList = new HashMap<>();
			if (CollectionUtil.isNotEmpty(existRelList)) {
				existVehicleTypeList = existRelList.stream().collect(Collectors.toMap(AlarmRuleRel::getEntityType, AlarmRuleRel::getId));
			}
			// 检查关联关系是新增(前端传入有，数据库没有)/删除(前端没有，数据库有)/还是不变(前端和数据库都有)
			// List倒序检查
			for (int i = relationshipVOList.size() - 1; i >= 0; i--) {
				// 不变，前端和数据库去掉交集
				if (existVehicleTypeList.containsKey(relationshipVOList.get(i).getEntityType())) {
					// remove后剩余的就是需要删除的关联关系
					existVehicleTypeList.remove(relationshipVOList.get(i).getEntityType());
					// remove后剩余的就是需要新增的关联关系
					relationshipVOList.remove(i);
				}
			}
			// 新增或者更新
			if (CollectionUtil.isNotEmpty(relationshipVOList)) {
				relationshipVOList.forEach(relVO -> relVO.setAlarmRuleId(alarmRuleInfoVO.getId()));
				alarmRuleRelService.saveOrUpdateAlarmRuleExtBatch(AlarmRuleRelWrapper.build().listEntity(relationshipVOList));
			}
			// 删除
			if (!existVehicleTypeList.isEmpty()) {
				alarmRuleRelService.deleteLogic(new ArrayList<>(existVehicleTypeList.values()));
			}
			// 只有规则时开启状态才同步大数据
			if (AlarmConstant.Status.YES == alarmRuleInfoVO.getStatus()) {
				// 关联关系同步大数据
				alarmRuleRelService.postAlarmRelationshipEntityInfoToBigData(alarmRuleInfoVO,
						relationshipVOList.stream().map(AlarmRuleRelVO::getEntityType).collect(Collectors.toList()),
						new ArrayList<>(existVehicleTypeList.keySet()));
			}
		} else {
			//
		}
		return alarmRuleInfoVO;
	}

	/**
	 * 校验规则类型和绑定实体类型的匹配关系
	 * @param alarmRuleInfoVO
	 */
	private void validateAlarmRuleRelationshipMatch(AlarmRuleInfoVO alarmRuleInfoVO) {
		String entityCategoryCode = alarmRuleInfoVO.getEntityCategoryCode();
		List<AlarmRuleRelVO> relationshipVOList = new ArrayList<>();
		if (AlarmConstant.VEHICLE_OVERSPEED_ALARM.equals(entityCategoryCode)
				|| AlarmConstant.VEHICLE_OUT_OF_AREA_ALARM.equals(entityCategoryCode)
				|| AlarmConstant.VEHICLE_STAY_ALARM.equals(entityCategoryCode)) {
			relationshipVOList = alarmRuleInfoVO.getAlarmRuleRelVOList();
			if (CollectionUtil.isEmpty(relationshipVOList)) {
				throw new ServiceException("告警关联的实体类型不能为空");
			}
		}
		// 校验车辆类型是否已经绑定到其他超速告警规则上
		Long entityCategoryId = alarmRuleInfoVO.getEntityCategoryId();
		LambdaQueryWrapper<AlarmRuleInfo> ruleQuery = new LambdaQueryWrapper<>();
		ruleQuery.eq(AlarmRuleInfo::getEntityCategoryId, entityCategoryId);
		ruleQuery.eq(AlarmRuleInfo::getTenantId, alarmRuleInfoVO.getTenantId());
		ruleQuery.eq(AlarmRuleInfo::getIsDeleted, BladeConstant.DB_NOT_DELETED);
		// 查询当前租户下需要配置的对应类型的告警规则数据
		List<AlarmRuleInfo> existRuleList = this.list(ruleQuery);
		// 只判断车辆
		if (entityCategoryCode.startsWith(AlarmConstant.VEHICLE_ALARM_RULE)
				&& !AlarmConstant.VEHICLE_LOSE_SIGNAL.equals(entityCategoryCode)
				&& !AlarmConstant.VEHICLE_INITIATIVE_ALARM.equals(entityCategoryCode)) {
			List<VehicleCategoryVO> vehicleCategoryList = VehicleCategoryCache.listCategoryByTenantId(alarmRuleInfoVO.getTenantId());
			if (CollectionUtil.isEmpty(vehicleCategoryList)) {
				throw new ServiceException("该租户下没有配置车辆类型！");
			}
			// 校验车辆类型在租户下是否存在
			List<Long> vehicleTypes = vehicleCategoryList.stream().map(vo -> Long.parseLong(vo.getCategoryCode())).collect(Collectors.toList());
			relationshipVOList.forEach(inputType -> {
				if (!vehicleTypes.contains(inputType.getEntityType())) {
					throw new ServiceException(StrUtil.format("租户下没有[{}]类型", inputType.getEntityTypeName()));
				}
			});
			if (CollectionUtil.isNotEmpty(existRuleList)) {
				Map<Long, AlarmRuleInfo> ruleIdRuleMap = existRuleList.stream().collect(Collectors.toMap(AlarmRuleInfo::getId, Function.identity()));
				LambdaQueryWrapper<AlarmRuleRel> relQuery = new LambdaQueryWrapper<>();
				relQuery.in(AlarmRuleRel::getAlarmRuleId, ruleIdRuleMap.keySet());
				relQuery.eq(AlarmRuleRel::getTenantId, alarmRuleInfoVO.getTenantId());
				relQuery.eq(AlarmRuleRel::getIsDeleted, BladeConstant.DB_NOT_DELETED);
				// 查出所有超速告警关联的车辆类型
				List<AlarmRuleRel> existRelList = alarmRuleRelService.list(relQuery);
				if (CollectionUtil.isNotEmpty(existRelList)) {
					StringBuilder sb = new StringBuilder();
					Map<Long, Long> entityTypeRuleRelMap = existRelList.stream().collect(Collectors.toMap(AlarmRuleRel::getEntityType, AlarmRuleRel::getAlarmRuleId));
					relationshipVOList.forEach(inputType -> {
						if (entityTypeRuleRelMap.containsKey(inputType.getAlarmRuleId())) {
							sb.append("车辆类型：[").append(inputType.getEntityName()).append("]已经与告警规则：[").append(ruleIdRuleMap.get(inputType.getAlarmRuleId()).getName()).append("]绑定；");
						}
					});
					if (StringUtils.isNotBlank(sb)) {
						throw new ServiceException(sb.toString());
					}
				}
			}
		} else if (entityCategoryCode.startsWith(AlarmConstant.PERSON_ALARM_RULE)
				|| AlarmConstant.VEHICLE_LOSE_SIGNAL.equals(entityCategoryCode)
				|| AlarmConstant.VEHICLE_INITIATIVE_ALARM.equals(entityCategoryCode)) {
			// 车辆主动安全告警/车辆信号丢失/人员告警规则一个类型只能配一条
			if (CollectionUtil.isNotEmpty(existRuleList) && existRuleList.size() > 1) {
				throw new ServiceException(StrUtil.format("{}只能配置一条，请检查已有规则！", alarmRuleInfoVO.getEntityCategoryName()));
			}
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public void postAlarmRuleDataToBigData(AlarmRuleInfoVO alarmRuleInfoVO, String optFlag) {
		JSONObject param = new JSONObject();
		if (BigDataHttpClient.OptFlag.ADD.equals(optFlag) || BigDataHttpClient.OptFlag.EDIT.equals(optFlag)) {
			param.put("optFlag", BigDataHttpClient.OptFlag.ADD);
			param.put("alarmId", alarmRuleInfoVO.getId());
			param.put("alarmName", alarmRuleInfoVO.getName());
			param.put("alarmDescription", alarmRuleInfoVO.getRemark());
			param.put("relEntityCategoryId", alarmRuleInfoVO.getEntityCategoryId());
			param.put("tenantId", alarmRuleInfoVO.getTenantId());
			// 默认规则等级为1，有其他的下面代码会覆盖
			param.put("alarmLevel", AlarmConstant.AlarmLevel.NORMAL);
			if (!AlarmConstant.VEHICLE_LOSE_SIGNAL.equals(alarmRuleInfoVO.getEntityCategoryCode())) {
				// 扩展属性表
				List<AlarmRuleExtVO> extVOList = alarmRuleInfoVO.getAlarmRuleExtVOList();
				if (CollectionUtil.isNotEmpty(extVOList)) {
					JSONArray attrs = new JSONArray();
					extVOList.forEach(alarmRuleExtVO -> {
						JSONObject attr = new JSONObject();
						// 输入框
						if (alarmRuleExtVO.getInputType().equals((long) AlarmConstant.AttrInputType.TEXT_INPUT) &&
								(alarmRuleExtVO.getParentId() == null || alarmRuleExtVO.getParentId() < 0)) { // IAlarmRuleInfoService.fillAlarmRuleExtAttrs这里对子扩展做了处理，会有重复数据，这边过滤掉
							/**
							 * 等级配置在参数上，为了保持原来的报文格式不变，减少大数据大规模改代码，
							 * 依旧每个等级拼接一条报文同步给大数据，
							 * 当前只有超速告警有2个等级，其他告警规则都是一个等级，对超速告警单独判断即可
							 */
							param.put("alarmLevel", alarmRuleExtVO.getAttrLevel());

							attr.put("attrId", alarmRuleExtVO.getId());
							attr.put("attrName", alarmRuleExtVO.getAttrName());
							attr.put("attrType", alarmRuleExtVO.getAttrId());
							attr.put("attrValue", alarmRuleExtVO.getInputValue());
							attr.put("attrCode", alarmRuleExtVO.getAttrCode());
							attrs.put(attr);
							// 超速告警拼完报文直接同步
							if (alarmRuleInfoVO.getEntityCategoryCode().startsWith(AlarmConstant.VEHICLE_OVERSPEED_ALARM_TYPE)) {
								param.put("attributes", new JSONArray().put(attr));
								// 同步大数据告警规则
								sendAlarmRuleToBigData(param, optFlag);
							}
						}
						// 勾选框 
						else if (alarmRuleExtVO.getInputType().equals((long) AlarmConstant.AttrInputType.CHECK_BOX)
								&& AlarmConstant.AttrInputType.CheckBoxValue.SELECTED.equals(alarmRuleExtVO.getInputValue())) {
							attr.put("attrId", alarmRuleExtVO.getId());
							attr.put("attrName", alarmRuleExtVO.getAttrName());
							attr.put("attrType", alarmRuleExtVO.getAttrId());
							attr.put("attrValue", alarmRuleExtVO.getInputValue());
							attr.put("attrCode", alarmRuleExtVO.getAttrCode());
							attrs.put(attr);
							List<AlarmRuleExtVO> extensionList = alarmRuleExtVO.getExtensionList();
							if (CollectionUtil.isNotEmpty(extensionList)) {
								extensionList.forEach(extension -> {
									JSONObject childAttr = new JSONObject();
									childAttr.put("parentAttrId", alarmRuleExtVO.getId());
									childAttr.put("attrId", extension.getId());
									childAttr.put("attrName", extension.getAttrName());
									childAttr.put("attrType", extension.getAttrId());
									childAttr.put("attrValue", extension.getInputValue());
									childAttr.put("attrCode", extension.getAttrCode());
									attrs.put(childAttr);
								});
							}
						}
					});
					param.put("attributes", attrs);
				}
			}
		} else if (BigDataHttpClient.OptFlag.REMOVE.equals(optFlag)) {
			param.put("alarmId", alarmRuleInfoVO.getId());
			param.put("optFlag", optFlag);
			param.put("alarmLevel", AlarmConstant.AlarmLevel.NORMAL);
			if (alarmRuleInfoVO.getEntityCategoryCode().equals(AlarmConstant.VEHICLE_OVERSPEED_ALARM)) {
				// 超速告警删除调2遍
				sendAlarmRuleToBigData(param, optFlag);
				param.put("alarmLevel", AlarmConstant.AlarmLevel.EMERGENCY);
				sendAlarmRuleToBigData(param, optFlag);
			}
		}
		// 超速告警同步过了，直接返回
		if (alarmRuleInfoVO.getEntityCategoryCode().startsWith(AlarmConstant.VEHICLE_OVERSPEED_ALARM_TYPE)) {
			return;
		}
		if (!param.isEmpty()) {
			// 同步大数据告警规则
			sendAlarmRuleToBigData(param, optFlag);
		}
	}

	/**
	 * 告警规则同步大数据
	 * @param param
	 * @param optFlag
	 */
	private void sendAlarmRuleToBigData(JSONObject param, String optFlag) {
		try {
			BigDataHttpClient.postDataToBigData(BigDataHttpClient.postAlarmRule, param.toString());
		} catch (IOException e) {
			// 记录失败日志
			log.error((BigDataHttpClient.OptFlag.ADD.equals(optFlag) ? "新增" : "删除").concat("告警规则同步大数据接口失败，异常报文：{}"), e);
			throw new ServiceException("同步大数据接口失败!");
		}
	}

	/**
	 * 停用启用告警规则
	 * 停用时同步给大数据删除
	 * 启用时同步给大数据新增
	 * @param alarmRuleId
	 * @param newStatus
	 * @return
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public boolean enableOrDisableRule(Long alarmRuleId, Integer newStatus) {
		AlarmRuleInfo alarmRuleInfo = this.getById(alarmRuleId);
		if(alarmRuleInfo == null){
			throw new ServiceException(StrFormatter.format("告警规则[{}]不存在", alarmRuleId));
		}
		boolean initiativeAlarm = false;
		// 主动安全告警不需要配置参数，也不需要同步大数据
		if (AlarmConstant.VEHICLE_INITIATIVE_ALARM.equals(alarmRuleInfo.getEntityCategoryCode())) {
			initiativeAlarm = true;
		}
		boolean watchSOSAlarm = false;
		// 手表SOS告警不需要配置参数，也不需要同步大数据
		if (AlarmConstant.PERSON_WATCH_SOS_ALARM.equals(alarmRuleInfo.getEntityCategoryCode())) {
			watchSOSAlarm = true;
		}
		boolean vehicleLoseSignal = false;
		// 车辆丢失信号不配置参数，同步大数据
		if (AlarmConstant.VEHICLE_LOSE_SIGNAL.equals(alarmRuleInfo.getEntityCategoryCode())) {
			vehicleLoseSignal = true;
		}
		boolean personLoseSignal = false;
		// 人员丢失信号不配置参数，同步大数据
		if (AlarmConstant.PERSON_LOSE_SIGNAL.equals(alarmRuleInfo.getEntityCategoryCode())) {
			personLoseSignal = true;
		}
		// 启用规则时校验参数是否配置
		if (StrUtil.isBlank(alarmRuleInfo.getRemark()) && AlarmConstant.Status.YES == newStatus 
				&& !initiativeAlarm && !watchSOSAlarm && !vehicleLoseSignal && !personLoseSignal) {
			throw new ServiceException(StrFormatter.format("告警规则[{}]参数未配置！", alarmRuleInfo.getName()));
		}
		alarmRuleInfo.setStatus(newStatus);
		boolean success = this.updateById(alarmRuleInfo);
		if (!initiativeAlarm && !watchSOSAlarm) {
			AlarmRuleInfoDTO query = new AlarmRuleInfoDTO();
			query.setId(alarmRuleId);
			List<AlarmRuleInfoVO> records = this.detailsAndDetailExts(query, new Query()).getRecords();
			if (CollectionUtil.isNotEmpty(records)) {
				AlarmRuleInfoVO record = records.get(0);
				if (newStatus.equals(AlarmConstant.Status.YES)) {
					postAlarmRuleDataToBigData(record, BigDataHttpClient.OptFlag.ADD);
					// 车辆告警规则，新增关联实体类型数据同步大数据
					alarmRuleRelService.postAlarmRelationshipEntityInfoToBigData(record,
							CollectionUtil.isNotEmpty(record.getAlarmRuleRelVOList()) ? record.getAlarmRuleRelVOList().stream().map(AlarmRuleRelVO::getEntityType).collect(Collectors.toList()) : null,
							null);
				} else if (newStatus.equals(AlarmConstant.Status.NO)) {
					postAlarmRuleDataToBigData(records.get(0), BigDataHttpClient.OptFlag.REMOVE);
					// 车辆告警规则，删除关联实体类型数据同步大数据
					alarmRuleRelService.postAlarmRelationshipEntityInfoToBigData(record,
							null,
							CollectionUtil.isNotEmpty(record.getAlarmRuleRelVOList()) ? record.getAlarmRuleRelVOList().stream().map(AlarmRuleRelVO::getEntityType).collect(Collectors.toList()) : null);
				}
			}
		}
		return success;
	}


	/**
	 * 根据车辆或者人员，返回给前端支持新增操作的告警规则类型
	 * @param tenantId
	 * @param alarmTypeCode
	 * @param isSearch
	 * @return
	 */
	@Override
	public List<Dict> listAvailableAlarmTypes(String tenantId, String alarmTypeCode, Boolean isSearch) {
		if (StrUtil.isBlank(tenantId) || StrUtil.isBlank(alarmTypeCode)) {
			return null;
		}
		List<Dict> alarmTypeList = DictCache.listDictByCode(alarmTypeCode);
		if (CollectionUtil.isNotEmpty(alarmTypeList)) {
			// 做为查询条件时不做过滤
			if (isSearch) {
				return alarmTypeList;
			}
			Map<Long, Dict> alarmTypeMap = new HashMap<>();
			alarmTypeList.forEach(dict -> alarmTypeMap.put(Long.parseLong(dict.getDictKey()), dict));
			QueryWrapper<AlarmRuleInfo> queryWrapper = new QueryWrapper<>();
			queryWrapper.lambda().eq(AlarmRuleInfo::getTenantId, tenantId);
			queryWrapper.lambda().eq(AlarmRuleInfo::getIsDeleted, BladeConstant.DB_NOT_DELETED);
			if (AlarmConstant.AlarmTypeCode.VEHICLE_ALARM_TYPE.equals(alarmTypeCode)) {
				queryWrapper.lambda().eq(AlarmRuleInfo::getEntityCategoryId, AlarmConstant.AlarmTypeCode.VEHILCE_ALARM_LOSE_SIGNAL);
				List<AlarmRuleInfo> loseSignalRuleList = this.list(queryWrapper);
				if (CollectionUtil.isNotEmpty(loseSignalRuleList)) {
					// 车辆信号丢失只能配一条
					alarmTypeMap.remove(AlarmConstant.AlarmTypeCode.VEHILCE_ALARM_LOSE_SIGNAL);
				}
				queryWrapper.lambda().eq(AlarmRuleInfo::getEntityCategoryId, AlarmConstant.AlarmTypeCode.VEHILCE_ALARM_INITIATIVE_ALARM);
				List<AlarmRuleInfo> initiativeAlarmRuleList = this.list(queryWrapper);
				if (CollectionUtil.isNotEmpty(initiativeAlarmRuleList)) {
					// 主动安全告警只能配一条
					alarmTypeMap.remove(AlarmConstant.AlarmTypeCode.VEHILCE_ALARM_INITIATIVE_ALARM);
				}
				return new ArrayList<>(alarmTypeMap.values());
			} else if (AlarmConstant.AlarmTypeCode.PERSON_ALARM_TYPE.equals(alarmTypeCode)) {
				// 人员告警规则每个只能配一条
				queryWrapper.lambda().in(AlarmRuleInfo::getEntityCategoryId, alarmTypeMap.keySet());
				List<AlarmRuleInfo> existPersonAlarmList = this.list(queryWrapper);
				if (CollectionUtil.isNotEmpty(existPersonAlarmList)) {
					alarmTypeMap.keySet().removeAll(existPersonAlarmList.stream().map(AlarmRuleInfo::getEntityCategoryId).collect(Collectors.toSet()));
				}
				return new ArrayList<>(alarmTypeMap.values());
			}
			return null;
		}
		return null;
	}
	
	/**
	 * 复制告警规则
	 * @param tenantId
	 * @return
	 */
	@Deprecated
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public boolean copyDefaultAlarmRule4SpecifiedTenantOrRuleId(String tenantId, Long ruleId) {
		QueryWrapper<AlarmRuleInfo> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(AlarmRuleInfo::getTenantId, TenantConstant.DEFAULT_TENANT_ID);
		queryWrapper.lambda().ne(AlarmRuleInfo::getIsDeleted, BladeConstant.DB_IS_DELETED);
		queryWrapper.lambda().eq(Objects.nonNull(ruleId), AlarmRuleInfo::getId, ruleId);
		List<AlarmRuleInfo> alarmRuleInfos = this.list(queryWrapper);
		alarmRuleInfos.forEach(oldRule -> {
			Long oldRuleId = oldRule.getId();
			AlarmRuleInfo newRule = setAlarmRuleInfoDefaultValue(oldRule);
			newRule.setStatus(AlarmConstant.Status.NO); // 默认规则停用
			newRule.setTenantId(tenantId);
			// 保存取新Id
			this.save(newRule);
//			AlarmRuleInfoVO newRuleVO = AlarmRuleInfoWrapper.build().entityVO(newRule);
			Long newRuleId = newRule.getId();
			List<AlarmRuleExt> alarmRuleExts = alarmRuleExtService.listByAlarmRuleId(oldRuleId);
			List<AlarmRuleExtVO> extParents = alarmRuleExtService.calculateRelationship(AlarmRuleExtWrapper.build().listVO(alarmRuleExts));
//			List<AlarmRuleExtVO> newExtParents = new ArrayList<>();
			extParents.forEach(extParent -> {
				boolean isOverSpeedRule = false; // 是否为超速告警规则
				if (oldRule.getEntityCategoryCode().startsWith(AlarmConstant.VEHICLE_OVERSPEED_ALARM_TYPE)) {
					isOverSpeedRule = true;
				}
				AlarmRuleExt newExtParent = setAlarmRuleInfoExtDefaultValue(newRuleId, extParent, null);
				newExtParent.setTenantId(tenantId);
				if (alarmRuleExtService.save(newExtParent)) {
//					AlarmRuleExtVO newExtParentVO = AlarmRuleExtWrapper.build().entityVO(newExtParent);
					List<AlarmRuleExtVO> extensionList = extParent.getExtensionList();
					if (CollectionUtils.isNotEmpty(extensionList)) {
//						List<AlarmRuleExtVO> newExtensionList = new ArrayList<>();
						boolean finalIsOverSpeedRule = isOverSpeedRule;
						extensionList.forEach(oldExtChild ->{
							AlarmRuleExt newExtChild = setAlarmRuleInfoExtDefaultValue(newRuleId, oldExtChild, newExtParent.getId());
							newExtChild.setTenantId(tenantId);
							alarmRuleExtService.save(newExtChild);
//							newExtensionList.add(AlarmRuleExtWrapper.build().entityVO(newExtChild));
						});
//						newExtParentVO.setExtensionList(newExtensionList);
					}
//					newExtParents.add(newExtParentVO);
				}
			});
//			newRuleVO.setAlarmRuleExtVOList(newExtParents);
			// 创建组合时候复制告警规则，状态为停用，不同步大数据，等前台编辑告警规则并启用再同步
//			postDataToBigData(newRuleVO, BigDataHttpClient.OptFlag.ADD);
		});
		return true;
	}

	/**
	 * 复制告警规则时设置默认值
	 * @param alarmRuleInfo
	 * @return
	 */
	private AlarmRuleInfo setAlarmRuleInfoDefaultValue(AlarmRuleInfo alarmRuleInfo) {
		AlarmRuleInfo aNew = new AlarmRuleInfo();
		aNew.setName(alarmRuleInfo.getName());
		aNew.setEntityCategoryId(alarmRuleInfo.getEntityCategoryId());
		aNew.setEntityCategoryCode(alarmRuleInfo.getEntityCategoryCode());
//		aNew.setAlarmLevel(alarmRuleInfo.getAlarmLevel()); // 跟原值相同
//		aNew.setAlarmScope(alarmRuleInfo.getAlarmScope()); // 跟原值相同
		aNew.setAlarmStartTime(alarmRuleInfo.getAlarmStartTime()); // 跟原值相同
		aNew.setAlarmEndTime(alarmRuleInfo.getAlarmEndTime()); // 跟原值相同
		aNew.setIsDeleted(alarmRuleInfo.getIsDeleted()); // 跟原值相同
		return aNew;
	}

	/**
	 * 复制告警规则扩展表时设置默认值
	 * @param ruleId
	 * @param alarmRuleExt
	 * @param parentExtId
	 * @return
	 */
	private AlarmRuleExt setAlarmRuleInfoExtDefaultValue(Long ruleId, AlarmRuleExt alarmRuleExt, Long parentExtId) {
		AlarmRuleExt aNew = new AlarmRuleExt();
		aNew.setParentId(parentExtId);
		aNew.setAlarmRuleId(ruleId);
		aNew.setAttrId(alarmRuleExt.getAttrId()); // 跟原值相同
		aNew.setAttrName(alarmRuleExt.getAttrName()); // 跟原值相同
		aNew.setAttrCode(alarmRuleExt.getAttrCode()); // 跟原值相同
		aNew.setAttrSeq(alarmRuleExt.getAttrSeq()); // 跟原值相同
		aNew.setInputType(alarmRuleExt.getInputType()); // 跟原值相同
		aNew.setAttrLevel(alarmRuleExt.getAttrLevel()); // 跟原值相同
		aNew.setInputValue(null);
		aNew.setMeasurementUnitCode(alarmRuleExt.getMeasurementUnitCode()); // 跟原值相同
		aNew.setMeasurementUnitName(alarmRuleExt.getMeasurementUnitName()); // 跟原值相同
        aNew.setStatus(alarmRuleExt.getStatus()); // 跟原值相同
        aNew.setEditable(alarmRuleExt.getEditable()); // 跟原值相同
		aNew.setIsDeleted(alarmRuleExt.getIsDeleted()); // 跟原值相同
		return aNew;
	}

	/**
	 * 获取可配置关联的车辆类型
	 *
	 * @param vehicleTypeQueryDTO @return
	 */
	@Override
	public List<VehicleCategoryVO> listAvailableVehicleCategory(VehicleTypeQueryDTO vehicleTypeQueryDTO) {
		String tenantId = vehicleTypeQueryDTO.getTenantId();
		Long alarmEntityCategoryId = vehicleTypeQueryDTO.getAlarmEntityCategoryId();
		Long alarmRuleId = vehicleTypeQueryDTO.getAlarmRuleId();
		Boolean isSearch = vehicleTypeQueryDTO.getIsSearch();
		if (StringUtils.isBlank(tenantId)) {
			return null;
		}
		// 从车辆缓存查询租户下车辆类型配置
		List<VehicleCategoryVO> vehicleCategoryList = VehicleCategoryCache.listCategoryByTenantId(tenantId);
		if (CollectionUtil.isEmpty(vehicleCategoryList)) {
			return null;
		}
		Map<Long, VehicleCategoryVO> vehicleCategoryMap = vehicleCategoryList.stream().collect(Collectors.toMap(vo -> Long.parseLong(vo.getCategoryCode()), t -> t));
		// 返回全量数据做为前端搜索框数据
		if (Boolean.TRUE.equals(isSearch)) {
			return calculateRelationship(vehicleCategoryMap.values().stream().collect(Collectors.toMap(VehicleCategoryVO::getId, Function.identity())), new AtomicReference<>());
		}
		// 没传是哪种告警类型直接返回空
		if (alarmEntityCategoryId == null) {
			return null;
		}
		LambdaQueryWrapper<AlarmRuleInfo> ruleQuery = new LambdaQueryWrapper<>();
		ruleQuery.eq(AlarmRuleInfo::getEntityCategoryId, alarmEntityCategoryId);
		ruleQuery.eq(AlarmRuleInfo::getTenantId, tenantId);
		ruleQuery.eq(AlarmRuleInfo::getIsDeleted, BladeConstant.DB_NOT_DELETED);
		List<AlarmRuleInfo> ruleList = this.list(ruleQuery);
		AtomicReference<Set<Long>> bindedTypeList = new AtomicReference<>(new HashSet<>());
		if (CollectionUtil.isNotEmpty(ruleList)) {
			LambdaQueryWrapper<AlarmRuleRel> relQuery = new LambdaQueryWrapper<>();
			relQuery.eq(AlarmRuleRel::getTenantId, tenantId);
			relQuery.eq(AlarmRuleRel::getIsDeleted, BladeConstant.DB_NOT_DELETED);
			// 查询同类型下所有的告警规则（排除自己）
			List<Long> ruleIdList = ruleList.stream().map(AlarmRuleInfo::getId).filter(id -> !id.equals(alarmRuleId)).collect(Collectors.toList());
			if (CollectionUtil.isNotEmpty(ruleIdList)) {
				relQuery.in(AlarmRuleRel::getAlarmRuleId, ruleIdList);
				List<AlarmRuleRel> relList = alarmRuleRelService.list(relQuery);
				if (CollectionUtil.isNotEmpty(relList)) {
					// 移除已绑定的车辆类型（自己已绑定的车辆类型移除）
					vehicleCategoryMap.keySet().removeAll(relList.stream().map(AlarmRuleRel::getEntityType).collect(Collectors.toList()));
				}
			}
			// 自己已绑定的车辆类型
			if (alarmRuleId != null && alarmRuleId > 0) {
				relQuery.eq(AlarmRuleRel::getAlarmRuleId, alarmRuleId);
				List<AlarmRuleRel> bindedRelList = alarmRuleRelService.list(relQuery);
				bindedTypeList.set(bindedRelList.stream().map(AlarmRuleRel::getEntityType).collect(Collectors.toSet()));
			}
		}
		return calculateRelationship(vehicleCategoryMap.values().stream().collect(Collectors.toMap(VehicleCategoryVO::getId, Function.identity())), bindedTypeList);
	}

	/**
	 * 计算车辆类型的级联关系
	 * @param vehicleCategoryMap
	 * @param bindedTypeList
	 * @return
	 */
	private List<VehicleCategoryVO> calculateRelationship(Map<Long, VehicleCategoryVO> vehicleCategoryMap, AtomicReference<Set<Long>> bindedTypeList) {
		// 父节点
		Map<Long, VehicleCategoryVO> parentMap = vehicleCategoryMap.values().stream()
				.filter(vehicleCategory -> vehicleCategory.getParentCategoryId() == null || vehicleCategory.getParentCategoryId() == 0L)
				.collect(Collectors.toMap(VehicleCategoryVO::getId, Function.identity()));
		// 子节点
		vehicleCategoryMap.values().stream()
				.filter(vehicleCategory -> vehicleCategory.getParentCategoryId() != null && vehicleCategory.getParentCategoryId() >= 0)
				.forEach(vehicleCategoryVO -> {
					VehicleCategoryVO parent = parentMap.get(vehicleCategoryVO.getParentCategoryId());
					if (parent != null) {
						List<VehicleCategoryVO> children = parent.getChildVehicleCategoryVOS();
						// 标记已选中
						if (CollectionUtil.isNotEmpty(bindedTypeList.get()) && bindedTypeList.get().contains(Long.parseLong(vehicleCategoryVO.getCategoryCode()))) {
							vehicleCategoryVO.setIsSelected(1);
						}
						if (CollectionUtil.isNotEmpty(children)) {
							children.add(vehicleCategoryVO);
						} else {
							children = new ArrayList<>();
							children.add(vehicleCategoryVO);
						}
						parent.setChildVehicleCategoryVOS(children);
					}
				});
		return parentMap.values().stream().filter(parentVO -> CollectionUtil.isNotEmpty(parentVO.getChildVehicleCategoryVOS())).collect(Collectors.toList());
	}
}
