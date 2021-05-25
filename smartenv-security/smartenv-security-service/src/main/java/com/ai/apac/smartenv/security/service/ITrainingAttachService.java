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

import com.ai.apac.smartenv.security.dto.TrainingAttachDTO;
import com.ai.apac.smartenv.security.entity.TrainingAttach;
import com.ai.apac.smartenv.security.vo.TrainingAttachVO;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 培训记录附件表 服务类
 *
 * @author Blade
 * @since 2020-08-20
 */
public interface ITrainingAttachService extends BaseService<TrainingAttach> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param trainingAttach
	 * @return
	 */
	IPage<TrainingAttachVO> selectTrainingAttachPage(IPage<TrainingAttachVO> page, TrainingAttachVO trainingAttach);

	/**
	 * 根据条件查附件信息
	 * @param trainingAttachDTO
	 * @return
	 * @throws Exception
	 */
    List<TrainingAttachVO> listTrainingAttachVOList(TrainingAttachDTO trainingAttachDTO) throws Exception;

	/**
	 * 根据培训Id查附件信息
	 * @param trainingRecordId
	 * @return
	 * @throws Exception
	 */
	List<TrainingAttachVO> listTrainingAttachByTrainingRecordId(Long trainingRecordId) throws Exception;

	/**
	 * 根据培训记录Id删除数据
	 * @param trainingRecordId
	 * @return
	 */
	boolean removeByTrainingRecordId(Long trainingRecordId);
}
