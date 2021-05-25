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

import com.ai.apac.smartenv.address.entity.AttendanceExportTask;
import com.ai.apac.smartenv.address.service.IAddressAsyncService;
import com.ai.apac.smartenv.address.vo.AttendanceExportTaskVO;
import com.ai.apac.smartenv.address.mapper.AttendanceExportTaskMapper;
import com.ai.apac.smartenv.address.service.IAttendanceExportTaskService;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.AddressConstant;
import com.ai.apac.smartenv.omnic.feign.IAttendanceClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.BladeUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.*;

/**
 * 考勤记录导出任务表 服务实现类
 *
 * @author Blade
 * @since 2020-05-12
 */
@Service
public class AttendanceExportTaskServiceImpl extends BaseServiceImpl<AttendanceExportTaskMapper, AttendanceExportTask> implements IAttendanceExportTaskService {

    private static Logger logger = LoggerFactory.getLogger(AddressAsyncService.class);

    @Autowired
    private IVehicleClient vehicleClient;
    @Autowired
    private IWorkareaRelClient workareaRelClient;
    @Autowired
    private IWorkareaClient workareaClient;
    @Autowired
    private IScheduleClient scheduleClient;
    @Autowired
    private IAttendanceClient attendanceClient;
    @Autowired
    private IOssClient ossClient;

    @Autowired
    @Lazy
    private IAddressAsyncService addressAsyncService;

    @Override
    public IPage<AttendanceExportTaskVO> selectAttendanceExportTaskPage(IPage<AttendanceExportTaskVO> page, AttendanceExportTaskVO attendanceExportTask) {
        return page.setRecords(baseMapper.selectAttendanceExportTaskPage(page, attendanceExportTask));
    }


    @Override
    public boolean addAttendanceExport(AttendanceExportTask exportTask, BladeUser user) {
        boolean save = this.save(exportTask);
        logger.info(Thread.currentThread().getName() + "----exportWordToOss1------------------");
        addressAsyncService.exportAttendance(exportTask,user);
        return save;
    }

    @Override
    public boolean reExport(Long id,BladeUser user) {
        AttendanceExportTask exportTask = this.getById(id);
        AttendanceExportTask update=new AttendanceExportTask();
        update.setId(id);
        update.setExportTime(new Date());
        update.setExportStatus(AddressConstant.ExportStatus.EXPORTING);
        boolean b = this.updateById(update);
        logger.info(Thread.currentThread().getName() + "----exportWordToOss1------------------");
        addressAsyncService.exportAttendance(exportTask,user);
        return b ;
    }


}

