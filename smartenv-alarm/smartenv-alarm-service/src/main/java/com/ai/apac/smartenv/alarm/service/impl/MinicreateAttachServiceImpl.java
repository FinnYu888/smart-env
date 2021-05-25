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

import com.ai.apac.smartenv.alarm.entity.MinicreateAttach;
import com.ai.apac.smartenv.alarm.mapper.MinicreateAttachMapper;
import com.ai.apac.smartenv.alarm.service.IMinicreateAttachService;
import com.ai.apac.smartenv.alarm.vo.MinicreateAttachVO;
import com.ai.apac.smartenv.alarm.wrapper.MinicreateAttachWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 点创主动告警附件表 服务实现类
 *
 * @author Blade
 * @since 2020-09-10
 */
@Service
public class MinicreateAttachServiceImpl extends BaseServiceImpl<MinicreateAttachMapper, MinicreateAttach> implements IMinicreateAttachService {

	@Override
	public IPage<MinicreateAttachVO> selectMinicreateAttachPage(IPage<MinicreateAttachVO> page, MinicreateAttachVO minicreateAttach) {
		return page.setRecords(baseMapper.selectMinicreateAttachPage(page, minicreateAttach));
	}

	@Override
	public List<MinicreateAttachVO> listAttachByCondition(MinicreateAttachVO attachVO) {
		if (attachVO == null) {
			return null;
		}
		LambdaQueryWrapper<MinicreateAttach> queryWrapper = new LambdaQueryWrapper<MinicreateAttach>();
		if (StringUtils.isNotBlank(attachVO.getUuid())) {
			queryWrapper.eq(MinicreateAttach::getUuid, attachVO.getUuid());
		}
		if (attachVO.getStatus() != null) {
			queryWrapper.eq(MinicreateAttach::getStatus, attachVO.getStatus());
		}
		List<MinicreateAttach> minicreateAttachList = this.list(queryWrapper);
		if (CollectionUtils.isNotEmpty(minicreateAttachList)) {
			return MinicreateAttachWrapper.build().listVO(minicreateAttachList);	
		}
		return null;
	} 

}
