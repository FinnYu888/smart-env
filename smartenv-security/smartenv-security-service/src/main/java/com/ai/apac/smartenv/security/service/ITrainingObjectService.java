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
package com.ai.apac.smartenv.security.service;

import com.ai.apac.smartenv.security.dto.TrainingObjectDTO;
import com.ai.apac.smartenv.security.entity.TrainingObject;
import com.ai.apac.smartenv.security.vo.TrainingObjectVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;

import java.util.List;

/**
 *  服务类
 *
 * @author Blade
 * @since 2020-08-20
 */
public interface ITrainingObjectService extends BaseService<TrainingObject> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param trainingObject
	 * @return
	 */
	IPage<TrainingObjectVO> selectTrainingObjectPage(IPage<TrainingObjectVO> page, TrainingObjectVO trainingObject);

	/**
	 * 根据条件查询培训对象
	 * @param trainingObjectDTO
	 * @return
	 * @throws Exception
	 */
	List<TrainingObjectVO> listTrainingObjectVOList(TrainingObjectDTO trainingObjectDTO) throws Exception;

	/**
	 * 根据培训记录id查培训对象
	 * @param trainingRecordId
	 * @return
	 * @throws Exception
	 */
	List<TrainingObjectVO> listTrainingObjectByTrainingRecordId(Long trainingRecordId) throws Exception;

	/**
	 * 根据培训记录Id删除数据
	 * @param trainingRecordId
	 * @return
	 */
    boolean removeByTrainingRecordId(Long trainingRecordId);
}
