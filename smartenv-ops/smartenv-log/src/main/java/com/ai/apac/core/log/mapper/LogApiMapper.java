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
package com.ai.apac.core.log.mapper;

import com.ai.apac.core.log.dto.LogApiQueryDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springblade.core.log.model.LogApi;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author Chill
 */
public interface LogApiMapper extends BaseMapper<LogApi> {

    Integer countLogApiByCondition(@Param("query") LogApiQueryDTO logApiQueryDTO);

    List<LogApi> listLogApiByCondition(@Param("query") LogApiQueryDTO logApiQueryDTO);

}
