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

import com.ai.apac.smartenv.security.dto.TrainingObjectDTO;
import com.ai.apac.smartenv.security.entity.TrainingAttach;
import com.ai.apac.smartenv.security.entity.TrainingObject;
import com.ai.apac.smartenv.security.mapper.TrainingObjectMapper;
import com.ai.apac.smartenv.security.service.ITrainingObjectService;
import com.ai.apac.smartenv.security.vo.TrainingObjectVO;
import com.ai.apac.smartenv.security.wrapper.TrainingObjectWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-08-20
 */
@Service
public class TrainingObjectServiceImpl extends BaseServiceImpl<TrainingObjectMapper, TrainingObject> implements ITrainingObjectService {

	@Override
	public IPage<TrainingObjectVO> selectTrainingObjectPage(IPage<TrainingObjectVO> page, TrainingObjectVO trainingObject) {
		return page.setRecords(baseMapper.selectTrainingObjectPage(page, trainingObject));
	}

	@Override
	public List<TrainingObjectVO> listTrainingObjectVOList(TrainingObjectDTO trainingObjectDTO) throws Exception {
		LambdaQueryWrapper<TrainingObject> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(trainingObjectDTO.getTrainingRecordId() != null, TrainingObject::getTrainingRecordId, trainingObjectDTO.getTrainingRecordId());
		List<TrainingObject> trainingObjectList = this.list(queryWrapper);
		return TrainingObjectWrapper.build().listVO(trainingObjectList);
	}

	@Override
	public List<TrainingObjectVO> listTrainingObjectByTrainingRecordId(Long trainingRecordId) throws Exception {
		List<TrainingObjectVO> results = new ArrayList<>();
		if (trainingRecordId == null) {
			return results;
		}
		TrainingObjectDTO query = new TrainingObjectDTO();
		query.setTrainingRecordId(trainingRecordId);
		return this.listTrainingObjectVOList(query);
	}

	@Override
	public boolean removeByTrainingRecordId(Long trainingRecordId) {
		LambdaQueryWrapper<TrainingObject> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(TrainingObject::getTrainingRecordId, trainingRecordId);
		return this.remove(queryWrapper);
	}
}
