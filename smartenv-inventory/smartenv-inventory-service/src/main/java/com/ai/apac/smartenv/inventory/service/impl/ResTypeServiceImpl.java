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
package com.ai.apac.smartenv.inventory.service.impl;

import com.ai.apac.smartenv.inventory.entity.ResSpec;
import com.ai.apac.smartenv.inventory.entity.ResType;
import com.ai.apac.smartenv.inventory.vo.ResTypeSpecVO;
import com.ai.apac.smartenv.inventory.service.IResSpecService;
import com.ai.apac.smartenv.inventory.vo.ResSpecVO;
import com.ai.apac.smartenv.inventory.vo.ResTypeVO;
import com.ai.apac.smartenv.inventory.mapper.ResTypeMapper;
import com.ai.apac.smartenv.inventory.service.IResTypeService;
import com.ai.apac.smartenv.inventory.wrapper.ResSpecWrapper;
import com.ai.apac.smartenv.inventory.wrapper.ResTypeWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

import java.util.ArrayList;
import java.util.List;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-02-25
 */
@Slf4j
@Service
@AllArgsConstructor
public class ResTypeServiceImpl extends BaseServiceImpl<ResTypeMapper, ResType> implements IResTypeService {

	private IResSpecService resSpecService;
	
	@Override
	public IPage<ResTypeVO> selectResTypePage(IPage<ResTypeVO> page, ResTypeVO resType) {
		return page.setRecords(baseMapper.selectResTypePage(page, resType));
	}

	@Override
	public List<ResTypeSpecVO> selectResTypeSpec() {
		return baseMapper.selectResTypeSpec();
	}


	@Override
	public List<ResTypeVO> listResTypeByCond(ResType resType) {
		List<ResTypeVO> resTypeVOList = new ArrayList<>();
		List<ResType> resTypeList = this.list(new LambdaQueryWrapper<>(resType));
		if (CollectionUtils.isNotEmpty(resTypeList)) {
			resTypeVOList = ResTypeWrapper.build().listVO(resTypeList);
		}
		return resTypeVOList;
	}

	@Override
	public List<String> listResTypeResSpecNameIdStrings(String tenantId) {
		List<String> resTypeResSpecList = new ArrayList<>();
		LambdaQueryWrapper<ResType> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ResType::getTenantId, tenantId);
		queryWrapper.eq(ResType::getStatus, 1);
		List<ResType> resTypeList = this.list(queryWrapper);
		if (CollectionUtils.isNotEmpty(resTypeList)) {
			String resTypeResSpec;
			for (ResType resType : resTypeList) {
				ResSpec resSpec = new ResSpec();
				resSpec.setResType(resType.getId());
				resSpec.setTenantId(tenantId);
				resSpec.setStatus(1);
				List<ResSpecVO> resSpecVOList = resSpecService.listResSpecByCond(resSpec);
				if (CollectionUtils.isNotEmpty(resSpecVOList)) {
					for (ResSpecVO resSpecVO : resSpecVOList) {
						resTypeResSpec = resType.getTypeName().concat(StringPool.SLASH).concat(resSpecVO.getSpecName()).concat(StringPool.COLON).concat(resSpecVO.getId().toString());
						resTypeResSpecList.add(resTypeResSpec);
					}
				}
			}
		}
		return resTypeResSpecList;
	}
}
