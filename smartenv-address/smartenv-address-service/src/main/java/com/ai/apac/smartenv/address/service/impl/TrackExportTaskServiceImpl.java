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
package com.ai.apac.smartenv.address.service.impl;

import com.ai.apac.smartenv.address.entity.TrackExportTask;
import com.ai.apac.smartenv.address.service.IAddressAsyncService;
import com.ai.apac.smartenv.address.vo.TrackExportTaskVO;
import com.ai.apac.smartenv.address.mapper.TrackExportTaskMapper;
import com.ai.apac.smartenv.address.service.ITrackExportTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.BladeUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 历史轨迹导出任务表 服务实现类
 *
 * @author Blade
 * @since 2020-03-03
 */
@Service
public class TrackExportTaskServiceImpl extends BaseServiceImpl<TrackExportTaskMapper, TrackExportTask> implements ITrackExportTaskService {

	private static Logger logger= LoggerFactory.getLogger(AddressAsyncService.class);


	@Autowired
	@Lazy
	private IAddressAsyncService addressAsyncService;

	@Override
	public IPage<TrackExportTaskVO> selectTrackExportTaskPage(IPage<TrackExportTaskVO> page, TrackExportTaskVO trackExportTask) {
		return page.setRecords(baseMapper.selectTrackExportTaskPage(page, trackExportTask));
	}

	@Override
	public boolean addTrackExportTask(TrackExportTask exportTask, BladeUser user){
		boolean save = this.save(exportTask);
		logger.info(Thread.currentThread().getName()+"----exportExcelToOss------");
		addressAsyncService.exportExcelToOss(exportTask,user);
		return save;
	}




}
