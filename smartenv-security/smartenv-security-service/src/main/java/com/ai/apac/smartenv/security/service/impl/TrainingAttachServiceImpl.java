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
package com.ai.apac.smartenv.security.service.impl;

import com.ai.apac.smartenv.security.dto.TrainingAttachDTO;
import com.ai.apac.smartenv.security.entity.TrainingAttach;
import com.ai.apac.smartenv.security.entity.TrainingAttach;
import com.ai.apac.smartenv.security.vo.TrainingAttachVO;
import com.ai.apac.smartenv.security.mapper.TrainingAttachMapper;
import com.ai.apac.smartenv.security.service.ITrainingAttachService;
import com.ai.apac.smartenv.security.vo.TrainingAttachVO;
import com.ai.apac.smartenv.security.wrapper.TrainingAttachWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 培训记录附件表 服务实现类
 *
 * @author Blade
 * @since 2020-08-20
 */
@Service
public class TrainingAttachServiceImpl extends BaseServiceImpl<TrainingAttachMapper, TrainingAttach> implements ITrainingAttachService {

	@Override
	public IPage<TrainingAttachVO> selectTrainingAttachPage(IPage<TrainingAttachVO> page, TrainingAttachVO trainingAttach) {
		return page.setRecords(baseMapper.selectTrainingAttachPage(page, trainingAttach));
	}

	@Override
	public List<TrainingAttachVO> listTrainingAttachVOList(TrainingAttachDTO trainingAttachDTO) throws Exception {
		LambdaQueryWrapper<TrainingAttach> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(trainingAttachDTO.getTrainingRecordId() != null, TrainingAttach::getTrainingRecordId, trainingAttachDTO.getTrainingRecordId());
		List<TrainingAttach> trainingAttachList = this.list(queryWrapper);
		return TrainingAttachWrapper.build().listVO(trainingAttachList);
	}

	@Override
	public List<TrainingAttachVO> listTrainingAttachByTrainingRecordId(Long trainingRecordId) throws Exception {
		List<TrainingAttachVO> results = new ArrayList<>();
		if (trainingRecordId == null) {
			return results;
		}
		TrainingAttachDTO query = new TrainingAttachDTO();
		query.setTrainingRecordId(trainingRecordId);
		return this.listTrainingAttachVOList(query);
	}

	@Override
	public boolean removeByTrainingRecordId(Long trainingRecordId) {
		LambdaQueryWrapper<TrainingAttach> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(TrainingAttach::getTrainingRecordId, trainingRecordId);
		return this.remove(queryWrapper);
	}
}
