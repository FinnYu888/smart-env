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
package com.ai.apac.smartenv.security.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import com.ai.apac.smartenv.security.entity.TrainingAttach;
import com.ai.apac.smartenv.security.vo.TrainingAttachVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 培训记录附件表包装类,返回视图层所需的字段
 *
 * @author Blade
 * @since 2020-08-20
 */
public class TrainingAttachWrapper extends BaseEntityWrapper<TrainingAttach, TrainingAttachVO>  {

	public static TrainingAttachWrapper build() {
		return new TrainingAttachWrapper();
 	}

	@Override
	public TrainingAttachVO entityVO(TrainingAttach trainingAttach) {
		TrainingAttachVO trainingAttachVO = BeanUtil.copy(trainingAttach, TrainingAttachVO.class);

		return trainingAttachVO;
	}

	public TrainingAttach voEntity(TrainingAttachVO trainingAttachVO) {
		TrainingAttach trainingAttach = BeanUtil.copy(trainingAttachVO, TrainingAttach.class);

		return trainingAttach;
	}

	public List<TrainingAttach> listVOEntity(List<TrainingAttachVO> trainingAttachVOList) {
		List<TrainingAttach> targetList = new ArrayList<>();
		trainingAttachVOList.forEach(trainingAttachVO -> {
			TrainingAttach trainingAttach = BeanUtil.copy(trainingAttachVO, TrainingAttach.class);
			targetList.add(trainingAttach);
		});
		return targetList;
	}

}
