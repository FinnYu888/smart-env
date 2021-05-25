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
package com.ai.apac.core.log.service.impl;

import com.ai.apac.core.log.dto.LogApiQueryDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ai.apac.core.log.mapper.LogApiMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.model.LogApi;
import com.ai.apac.core.log.service.ILogApiService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务实现类
 *
 * @author Chill
 */
@Slf4j
@Service
@AllArgsConstructor
public class LogApiServiceImpl extends ServiceImpl<LogApiMapper, LogApi> implements ILogApiService {

    private LogApiMapper logApiMapper;

    @Override
    public Integer countLogApiByCondition(LogApiQueryDTO logApiQueryDTO) {
        return logApiMapper.countLogApiByCondition(logApiQueryDTO);
    }

    @Override
    public List<LogApi> listLogApiByCondition(LogApiQueryDTO logApiQueryDTO) {
        if (logApiQueryDTO.getCurrent() != null && logApiQueryDTO.getCurrent() > 0 && logApiQueryDTO.getSize() != null) {
            Integer current = logApiQueryDTO.getCurrent();
            Integer accCur = (current - 1) * logApiQueryDTO.getSize();
            logApiQueryDTO.setCurrent(accCur);
        }
        return logApiMapper.listLogApiByCondition(logApiQueryDTO);
    }
}
