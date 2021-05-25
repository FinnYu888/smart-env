package com.ai.apac.smartenv.facility.feign;

import com.ai.apac.smartenv.facility.entity.FacilityInfo;
import com.ai.apac.smartenv.facility.vo.FacilityInfoExtVO;
import com.ai.apac.smartenv.facility.vo.LastDaysGarbageAmountVO;
import com.ai.apac.smartenv.facility.vo.LastDaysRegionGarbageAmountVO;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
public class IFacilityClientFallBack implements IFacilityClient {

    @Override
    public R<Integer> countFacilityByTenantId(String tenantId, String deviceStatus) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Boolean> facilityInfoAsync(@RequestBody List<List<String>> datasList, @RequestParam String tenantId, @RequestParam String actionType) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<FacilityInfo> getFacilityInfoById(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<FacilityInfoExtVO> getFacilityDetailById(Long facilityId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<FacilityInfo>> getUnRegionFacility() {
        return R.fail("获取数据失败");
    }

    @Override
    public R updateFacilityInfo(FacilityInfo facilityInfo) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<LastDaysGarbageAmountVO>> getLastDaysGarbageAmount(Integer days, String tenantId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<LastDaysRegionGarbageAmountVO>> getLastDaysGarbageAmountByRegion(Integer days, String tenantId) {
        return R.fail("获取数据失败");
    }

    @GetMapping(API_GET_ALL_FACILITY)
    public R<List<FacilityInfo>> getAllFacility(){
        return R.fail("获取数据失败");
    }

    @Override
    public R<Integer> countAllFacility(String tenantId) {
        return R.fail("获取数据失败");
    }
}
