package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.omnic.entity.OmnicPersonInfo;
import com.ai.apac.smartenv.omnic.entity.OmnicVehicleInfo;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IPolymerizationClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/12  10:47    panfeng          v1.0.0             修改原因
 */
public class IPolymerizationClientFallback implements IPolymerizationClient{
    @Override
    public R initTreeDataToMongoDB() {
        return R.fail("接收数据失败");
    }

    @Override
    public R reloadTreeDataToMongoDb() {
        return R.fail("接收数据失败");
    }

//    @Override
//    public R addOrUpdateVehicleList(List<OmnicVehicleInfo> vehicleInfoList) {
//        return R.fail("接收数据失败");
//    }
//
//    @Override
//    public R addOrUpdatePersonList(List<OmnicPersonInfo> personList) {
//        return R.fail("接收数据失败");
//    }

    @Override
    public R addOrUpdateFacility(String facilityListId,Integer facilityMainType){
        return R.fail("接收数据失败");
    }

    @Override
    public R removePersonList(List<Long> personList) {
        return R.fail("接收数据失败");
    }

    @Override
    public R removeFacilityList(String facilityIds){
        return R.fail("接收数据失败");
    }

    @Override
    public R removeVehicleList(List<Long> personList) {
        return R.fail("接收数据失败");
    }

    @Override
    public R reloadVehicleInfo(List<Long> vehicleIds) {
        return R.fail("接收数据失败");
    }

    @Override
    public R reloadPersonInfo(List<Long> personIds) {
        return R.fail("接收数据失败");
    }
}
