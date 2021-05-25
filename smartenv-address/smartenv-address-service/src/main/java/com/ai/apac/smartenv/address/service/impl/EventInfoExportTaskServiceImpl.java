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

import com.ai.apac.smartenv.address.entity.EventinfoExportTask;
import com.ai.apac.smartenv.address.mapper.EventinfoExportTaskMapper;
import com.ai.apac.smartenv.address.service.IAddressAsyncService;
import com.ai.apac.smartenv.address.service.IEventinfoExportTaskService;
import com.ai.apac.smartenv.address.vo.EventinfoExportTaskVO;
import com.ai.apac.smartenv.common.constant.AddressConstant;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 考勤记录导出任务表 服务实现类
 *
 * @author Blade
 * @since 2020-05-12
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
public class EventInfoExportTaskServiceImpl extends BaseServiceImpl<EventinfoExportTaskMapper, EventinfoExportTask> implements IEventinfoExportTaskService {

    @Autowired
    @Lazy
    private IAddressAsyncService addressAsyncService;

    @Override
    public IPage<EventinfoExportTaskVO> selectEventinfoExportTaskPage(IPage<EventinfoExportTaskVO> page, EventinfoExportTaskVO EventinfoExportTask) {
        return page.setRecords(baseMapper.selectEventinfoExportTaskPage(page, EventinfoExportTask));
    }


    @Override
    public boolean addEventinfoExport(EventinfoExportTask exportTask,List<Long> eventIds) {
        exportTask.setExportStatus(AddressConstant.ExportStatus.EXPORTING);
        boolean save =false;
        if (exportTask.getId() != null && exportTask.getId() != 0L) {
            save = this.updateById(exportTask);
        }else {
            save= this.save(exportTask);
        }

        addressAsyncService.exportEventinfo(exportTask,eventIds);
        return save;
    }
}

