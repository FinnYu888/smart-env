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

import org.apache.commons.collections4.CollectionUtils;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import com.ai.apac.smartenv.security.entity.TrainingObject;
import com.ai.apac.smartenv.security.vo.TrainingObjectVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author Blade
 * @since 2020-08-20
 */
public class TrainingObjectWrapper extends BaseEntityWrapper<TrainingObject, TrainingObjectVO>  {

	public static TrainingObjectWrapper build() {
		return new TrainingObjectWrapper();
 	}

	@Override
	public TrainingObjectVO entityVO(TrainingObject trainingObject) {
		TrainingObjectVO trainingObjectVO = BeanUtil.copy(trainingObject, TrainingObjectVO.class);

		return trainingObjectVO;
	}

	public TrainingObject voEntity(TrainingObjectVO trainingObjectVO) {
		TrainingObject trainingObject = BeanUtil.copy(trainingObjectVO, TrainingObject.class);

		return trainingObject;
	}

	public List<TrainingObject> listVOEntity(List<TrainingObjectVO> trainingObjectVOList) {
		List<TrainingObject> targetList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(trainingObjectVOList)) {
			trainingObjectVOList.forEach(trainingObjectVO -> {
				TrainingObject trainingObject = BeanUtil.copy(trainingObjectVO, TrainingObject.class);
				targetList.add(trainingObject);
			});
		}
		return targetList;
	}
}
