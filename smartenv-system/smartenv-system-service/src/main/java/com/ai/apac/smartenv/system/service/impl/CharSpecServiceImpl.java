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

import com.ai.apac.smartenv.system.entity.CharSpec;
import com.ai.apac.smartenv.system.mapper.CharSpecMapper;
import com.ai.apac.smartenv.system.service.ICharSpecService;
import com.ai.apac.smartenv.system.vo.CharSpecVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 扩展属性表 服务实现类
 *
 * @author Blade
 * @since 2020-02-07
 */
@Service
public class CharSpecServiceImpl extends BaseServiceImpl<CharSpecMapper, CharSpec> implements ICharSpecService {

	@Override
	public IPage<CharSpecVO> selectCharSpecPage(IPage<CharSpecVO> page, CharSpecVO charSpec) {
		return page.setRecords(baseMapper.selectCharSpecPage(page, charSpec));
	}

	@Override
	public List<CharSpecVO> listCharSpecsByEntityCategoryId(String entityCategoryId) {
		return baseMapper.listCharSpecsByEntityCategoryId(entityCategoryId);
	}
}
