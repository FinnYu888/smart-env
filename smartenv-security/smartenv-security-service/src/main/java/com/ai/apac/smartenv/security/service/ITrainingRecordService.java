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

import com.ai.apac.smartenv.security.dto.TrainingRecordDTO;
import com.ai.apac.smartenv.security.entity.TrainingRecord;
import com.ai.apac.smartenv.security.vo.TrainingRecordVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;

/**
 * 培训记录表 服务类
 *
 * @author Blade
 * @since 2020-08-20
 */
public interface ITrainingRecordService extends BaseService<TrainingRecord> {


	/**
	 * 根据主键查询详情
	 * @param id
	 * @return
	 * @throws Exception
	 */
	TrainingRecordVO getRecord(Long id) throws Exception;
	
	/**
	 * 自定义分页
	 *
	 * @param query
	 * @param trainingRecord
	 * @return
	 */
	IPage<TrainingRecordVO> selectTrainingRecordPage(Query query, TrainingRecordDTO trainingRecord) throws Exception;

	/**
	 * 保存培训记录
	 * @param trainingRecordVO
	 * @throws Exception
	 * @return
	 */
    boolean saveTrainingRecord(TrainingRecordVO trainingRecordVO) throws Exception;

	/**
	 * 更新培训记录
	 * 记录关联的图片/附件/培训对象人员更新时都是全删除再新增
	 * @param trainingRecordVO
	 * @return
	 * @throws Exception
	 */
	boolean updateTrainingRecord(TrainingRecordVO trainingRecordVO) throws Exception;

	/**
	 * 删除培训记录
	 * @param recordId
	 * @return
	 * @throws Exception
	 */
	boolean removeTrainingRecord(Long recordId);
}
