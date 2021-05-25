package com.ai.apac.smartenv.websocket.feign;

import com.ai.apac.smartenv.websocket.module.person.vo.PersonDetailVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleDetailVO;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

/**
 * Copyright: Copyright (c) 2020/8/20 Asiainfo
 *
 * @ClassName: IBigScreenDataClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/20
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/20  15:44    zhanglei25          v1.0.0             修改原因
 */
@Component
public class IBigScreenDataClientFallback implements IBigScreenDataClient {

    @Override
    public R<Boolean> updateBigscreenCountRedis(String tenantId) {
        return R.fail("大屏统计数据更新失败");
    }

    @Override
    public R<Boolean> updateBigscreenGarbageAmountByRegionRedis(String tenantId) {
        return R.fail("大屏片区垃圾数据更新失败");
    }

    @Override
    public R<Boolean> updateBigscreenGarbageAmountDailyRedis(String tenantId) {
        return R.fail("大屏垃圾趋势数据更新失败");
    }

    @Override
    public R<Boolean> updateBigscreenAlarmAmountRedis(String tenantId) {
        return R.fail("大屏今日告警数量统计更新失败");
    }

    @Override
    public R<Boolean> updateBigscreenAlarmListRedis(String tenantId) {
        return R.fail("大屏实时告警列表更新失败");
    }

    @Override
    public R<Boolean> updateBigscreenEventCountByTypeRedis(String tenantId) {
        return R.fail("大屏事件统计数据更新失败");
    }

    /**
     * 查询人员定位详细信息
     *
     * @param personId
     * @return
     */
    @Override
    public R<PersonDetailVO> getPersonLocationDetail(Long personId, String tenantId) {
        return R.fail("查询人员定位详细信息失败");
    }

    /**
     * 查询车辆定位详细信息
     *
     * @param vehicleId
     * @param coordsSystemType
     * @return
     */
    @Override
    public R<VehicleDetailVO> getVehicleLocationDetail(Long vehicleId, String tenantId, String coordsSystemType) {
        return R.fail("查询车辆定位详细信息失败");
    }
}
