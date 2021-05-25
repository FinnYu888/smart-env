package com.ai.apac.smartenv.omnic.service;

import com.ai.apac.smartenv.omnic.entity.PicStatus;
import org.springframework.stereotype.Service;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: RealPicStatusService
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/17
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/17  22:50    panfeng          v1.0.0             修改原因
 */

public interface RealPicStatusService {

    PicStatus selectVehiclePicStatusById(String vehicle);

    PicStatus selectPersonPicStatusById(String vehicle);
}
