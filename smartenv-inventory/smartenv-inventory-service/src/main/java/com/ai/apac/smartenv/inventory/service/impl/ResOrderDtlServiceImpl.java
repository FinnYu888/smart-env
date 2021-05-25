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

import com.ai.apac.smartenv.inventory.entity.ResOrderDtl;
import com.ai.apac.smartenv.inventory.vo.ResOrderDtlVO;
import com.ai.apac.smartenv.inventory.mapper.ResOrderDtlMapper;
import com.ai.apac.smartenv.inventory.service.IResOrderDtlService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-02-25
 */
@Service
public class ResOrderDtlServiceImpl extends BaseServiceImpl<ResOrderDtlMapper, ResOrderDtl> implements IResOrderDtlService {

	@Override
	public IPage<ResOrderDtlVO> selectResOrderDtlPage(IPage<ResOrderDtlVO> page, ResOrderDtlVO resOrderDtl) {
		return page.setRecords(baseMapper.selectResOrderDtlPage(page, resOrderDtl));
	}

	@Override
	public List<ResOrderDtlVO> selectResOrderDtlInfoPage(QueryWrapper queryWrapper) {
		queryWrapper.eq("dtl.is_deleted",0);
		return baseMapper.selectResOrderDtlInfoPage(queryWrapper);
	}

}
