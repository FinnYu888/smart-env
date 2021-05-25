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
package com.ai.apac.smartenv.omnic.mapper;

import com.ai.apac.smartenv.omnic.entity.QScheduleObject;
import com.ai.apac.smartenv.omnic.vo.QScheduleObjectVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface QScheduleObjectMapper extends BaseMapper<QScheduleObject> {

	List<QScheduleObject> listArrange(@Param("param1") QScheduleObjectVO qScheduleObject, @Param("param2") int start,
			@Param("param3") int size);

	List<QScheduleObject> listHistoryArrange(@Param("param1") QScheduleObjectVO qScheduleObject, @Param("param2") int start,
			@Param("param3") int size);

	Integer countArrange(@Param("param1") QScheduleObjectVO qScheduleObject);
	
	Integer countHistoryArrange(@Param("param1") QScheduleObjectVO qScheduleObject);

}
