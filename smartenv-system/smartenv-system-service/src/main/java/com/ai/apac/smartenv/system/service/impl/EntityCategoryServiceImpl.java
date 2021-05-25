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
package com.ai.apac.smartenv.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.vo.EntityCategoryVO;
import com.ai.apac.smartenv.system.mapper.EntityCategoryMapper;
import com.ai.apac.smartenv.system.service.IEntityCategoryService;
import com.ai.apac.smartenv.system.wrapper.EntityCategoryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.ArrayList;
import java.util.List;

/**
 * 车辆,设备,物资等实体的分类信息 服务实现类
 *
 * @author Blade
 * @since 2020-02-07
 */
@Service
public class EntityCategoryServiceImpl extends BaseServiceImpl<EntityCategoryMapper, EntityCategory> implements IEntityCategoryService {

	@Override
	public IPage<EntityCategoryVO> selectEntityCategoryPage(IPage<EntityCategoryVO> page, EntityCategoryVO entityCategory) {
		return page.setRecords(baseMapper.selectEntityCategoryPage(page, entityCategory));
	}

	@Override
	public List<Long> getAllChildIdByParentId(Long categoryId) {
		List<Long> ids = new ArrayList<Long>();
		List<EntityCategoryVO> parentVOS = new ArrayList<>();
		EntityCategory wrapper = new EntityCategory();
		wrapper.setParentCategoryId(categoryId);
		List<EntityCategory> parent = baseMapper.selectList(Condition.getQueryWrapper(wrapper));
		if (CollectionUtil.isNotEmpty(parent)) {
			parentVOS = EntityCategoryWrapper.build().listVO(parent);
			parentVOS.forEach(parentVO -> {
				ids.add(parentVO.getId());
				List<Long> children = this.getAllChildIdByParentId(parentVO.getId());
				ids.addAll(children);
			});
		}
		return ids;
	}

}
