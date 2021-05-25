package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.omnic.entity.OmnicPersonInfo;
import com.ai.apac.smartenv.omnic.entity.PicStatus;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.omnic.entity.OmnicVehicleInfo;
import org.springblade.core.tool.api.R;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IRealTimeStatusClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/17
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/17  15:30    panfeng          v1.0.0             修改原因
 */
public class IRealTimeStatusClientFallback implements IRealTimeStatusClient{
    @Override
    public R<List<OmnicVehicleInfo>> getVehicleByStatus(Integer status, String tenentId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<PicStatus> getPicStatusByVehicleId(String vehicleId) {
        return R.fail("接收数据失败");
    }


    @Override
    public R<StatusCount> getAllVehicleStatusCount(String tenantId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<OmnicPersonInfo>> getPersonByStatus(Integer status, String tenantId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<PicStatus> getPicStatusByPersonId(String personId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<StatusCount> getAllPersonStatusCount(String tenantId) {
        return R.fail("接收数据失败");
    }
}
