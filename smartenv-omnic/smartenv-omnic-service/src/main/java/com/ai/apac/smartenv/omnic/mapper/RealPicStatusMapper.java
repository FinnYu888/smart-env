package com.ai.apac.smartenv.omnic.mapper;

import com.ai.apac.smartenv.omnic.entity.PicStatus;
import org.apache.ibatis.annotations.Param;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: RealPicStatusMapper
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/17
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/17  19:17    panfeng          v1.0.0             修改原因
 */

public interface RealPicStatusMapper {

    PicStatus  selectVehiclePicStatusById(@Param("vehicleId") String vehicleId);



    PicStatus  selectPersonPicStatusById(@Param("personId") String vehicleId);


}
