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
package com.ai.apac.smartenv.device.service.impl;

import com.ai.apac.smartenv.device.entity.SimRel;
import com.ai.apac.smartenv.device.vo.SimRelVO;
import com.ai.apac.smartenv.device.mapper.SimRelMapper;
import com.ai.apac.smartenv.device.service.ISimRelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-05-08
 */
@Service
public class SimRelServiceImpl extends BaseServiceImpl<SimRelMapper, SimRel> implements ISimRelService {

	@Override
	public IPage<SimRelVO> selectSimRelPage(IPage<SimRelVO> page, SimRelVO simRel) {
		return page.setRecords(baseMapper.selectSimRelPage(page, simRel));
	}

	@Override
	public SimRel selectSimRelBySimId(Long simId) {
		QueryWrapper<SimRel> queryWrapper = new QueryWrapper<SimRel>();
		queryWrapper.lambda().eq(SimRel::getSimId, simId);
		List<SimRel> list =  this.list(queryWrapper);
		if(list.size() > 0){
			return list.get(0);
		}
		return null;
	}

}
