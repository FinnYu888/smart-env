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
package com.ai.apac.smartenv.address.service;

import com.ai.apac.smartenv.address.entity.TrackExportTask;
import com.ai.apac.smartenv.address.vo.TrackExportTaskVO;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.secure.BladeUser;

/**
 * 历史轨迹导出任务表 服务类
 *
 * @author Blade
 * @since 2020-03-03
 */
public interface ITrackExportTaskService extends BaseService<TrackExportTask> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param trackExportTask
	 * @return
	 */
	IPage<TrackExportTaskVO> selectTrackExportTaskPage(IPage<TrackExportTaskVO> page, TrackExportTaskVO trackExportTask);

    boolean addTrackExportTask(TrackExportTask exportTask, BladeUser user);

}
