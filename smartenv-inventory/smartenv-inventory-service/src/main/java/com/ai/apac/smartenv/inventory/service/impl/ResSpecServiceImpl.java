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
import com.ai.apac.smartenv.inventory.vo.ResSpecVO;
import com.ai.apac.smartenv.inventory.mapper.ResSpecMapper;
import com.ai.apac.smartenv.inventory.service.IResSpecService;
import com.ai.apac.smartenv.inventory.wrapper.ResSpecWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-02-25
 */
@Service
public class ResSpecServiceImpl extends BaseServiceImpl<ResSpecMapper, ResSpec> implements IResSpecService {

	@Override
	public IPage<ResSpecVO> selectResSpecPage(IPage<ResSpecVO> page, ResSpecVO resSpec) {
		return page.setRecords(baseMapper.selectResSpecPage(page, resSpec));
	}

	@Override
	public IPage<ResSpecVO> selectResSpecInfoPage(IPage page, QueryWrapper queryWrapper) {
		queryWrapper.eq("spec.is_deleted",0);
		return page.setRecords(baseMapper.selectResSpecInfoPage(page, queryWrapper));
	}

	@Override
	public List<ResSpecVO> listResSpecByCond(ResSpec resSpec) {
		List<ResSpecVO> resSpecVOList = new ArrayList<>();
		List<ResSpec> resSpecList = this.list(new LambdaQueryWrapper<>(resSpec));
		if (CollectionUtils.isNotEmpty(resSpecList)) {
			 resSpecVOList = ResSpecWrapper.build().listVO(resSpecList);
		}
		return resSpecVOList;
	}

	@Override
	public List<ResSpecVO> listResSpecByTenantId(String tenantId) {
		if (StringUtils.isBlank(tenantId)) {
			return Collections.emptyList();
		}
		LambdaQueryWrapper<ResSpec> queryWrapper = new LambdaQueryWrapper<>(ResSpec.class);
		queryWrapper.eq(ResSpec::getTenantId, tenantId);
		queryWrapper.eq(ResSpec::getStatus, 1);
		List<ResSpec> list = this.list(queryWrapper);
		return ResSpecWrapper.build().listVO(list);
	}
}
