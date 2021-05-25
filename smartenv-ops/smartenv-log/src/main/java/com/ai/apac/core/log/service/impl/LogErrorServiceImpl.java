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

import com.ai.apac.core.log.dto.LogErrorQueryDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ai.apac.core.log.mapper.LogErrorMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.model.LogError;
import com.ai.apac.core.log.service.ILogErrorService;
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
public class LogErrorServiceImpl extends ServiceImpl<LogErrorMapper, LogError> implements ILogErrorService {

    private LogErrorMapper logErrorMapper;
    
    @Override
    public Integer countLogErrorByCondition(LogErrorQueryDTO logErrorQueryDTO) {
        return logErrorMapper.countLogErrorByCondition(logErrorQueryDTO);
    }

    @Override
    public List<LogError> listLogErrorByCondition(LogErrorQueryDTO logErrorQueryDTO) {
        if (logErrorQueryDTO.getCurrent() != null && logErrorQueryDTO.getCurrent() > 0 && logErrorQueryDTO.getSize() != null) {
            Integer current = logErrorQueryDTO.getCurrent();
            Integer accCur = (current - 1) * logErrorQueryDTO.getSize();
            logErrorQueryDTO.setCurrent(accCur);
        }
        return logErrorMapper.listLogErrorByCondition(logErrorQueryDTO);
    }

}
