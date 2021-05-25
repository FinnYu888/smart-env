package com.ai.apac.smartenv.address.service;

import com.ai.apac.smartenv.address.entity.AttendanceExportTask;
import com.ai.apac.smartenv.address.entity.EventinfoExportTask;
import com.ai.apac.smartenv.address.entity.TrackExportTask;
import org.springblade.core.secure.BladeUser;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: AddressAsyncService
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/4
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/4  9:54    panfeng          v1.0.0             修改原因
 */
public interface IAddressAsyncService {
    @Async("trackThreadPool")
    void exportExcelToOss(TrackExportTask exportTask, BladeUser user);



    @Async("trackThreadPool")
    void exportAttendance(AttendanceExportTask exportTask, BladeUser user);

    @Async("trackThreadPool")
    void exportEventinfo(EventinfoExportTask exportTask, List<Long> eventIds);
}
