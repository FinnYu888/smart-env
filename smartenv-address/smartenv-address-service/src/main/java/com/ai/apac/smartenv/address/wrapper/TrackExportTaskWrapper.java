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
package com.ai.apac.smartenv.address.wrapper;

import com.ai.apac.smartenv.system.feign.IDictClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import com.ai.apac.smartenv.address.entity.TrackExportTask;
import com.ai.apac.smartenv.address.vo.TrackExportTaskVO;

import java.sql.Timestamp;

/**
 * 历史轨迹导出任务表包装类,返回视图层所需的字段
 *
 * @author Blade
 * @since 2020-03-03
 */
public class TrackExportTaskWrapper extends BaseEntityWrapper<TrackExportTask, TrackExportTaskVO>  {




	public static TrackExportTaskWrapper build() {
		return new TrackExportTaskWrapper();
 	}

	@Override
	public TrackExportTaskVO entityVO(TrackExportTask trackExportTask) {
		TrackExportTaskVO trackExportTaskVO = BeanUtil.copy(trackExportTask, TrackExportTaskVO.class);




		return trackExportTaskVO;
	}

}
