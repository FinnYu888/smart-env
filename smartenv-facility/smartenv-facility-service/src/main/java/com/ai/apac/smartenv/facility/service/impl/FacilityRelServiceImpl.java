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
package com.ai.apac.smartenv.facility.service.impl;

import com.ai.apac.smartenv.facility.entity.FacilityRel;
import com.ai.apac.smartenv.facility.vo.FacilityRelVO;
import com.ai.apac.smartenv.facility.mapper.FacilityRelMapper;
import com.ai.apac.smartenv.facility.service.IFacilityRelService;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Service
public class FacilityRelServiceImpl extends BaseServiceImpl<FacilityRelMapper, FacilityRel> implements IFacilityRelService {

	@Override
	public IPage<FacilityRelVO> selectFacilityRelPage(IPage<FacilityRelVO> page, FacilityRelVO facilityRel) {
		return page.setRecords(baseMapper.selectFacilityRelPage(page, facilityRel));
	}

}
