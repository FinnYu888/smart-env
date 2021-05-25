package com.ai.apac.smartenv.facility.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.facility.entity.FacilityInfo;
import com.ai.apac.smartenv.facility.vo.FacilityInfoExtVO;
import com.ai.apac.smartenv.facility.vo.LastDaysGarbageAmountVO;
import com.ai.apac.smartenv.facility.vo.LastDaysRegionGarbageAmountVO;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient( value = ApplicationConstant.APPLICATION_FACILITY_NAME,
        fallback = IFacilityClientFallBack.class
)
public interface IFacilityClient {

    String API_PREFIX = "/client";

    String API_GET_FACILITY_BY_ID = API_PREFIX + "/facility-by-id";

    String API_GET_UNREGION_FACILITY = API_PREFIX + "/unregion-facility";

    String API_POST_FACILITY_REGION = API_PREFIX + "/facility-region";

    String API_GET_LAST_DAYS_GARBAGE_AMOUNT = API_PREFIX + "/last_days_garbage_amount";

    String API_GET_LAST_DAYS_GARBAGE_AMOUNT_BY_REGION = API_PREFIX + "/last-days-garbage-amount-by-region";

    String API_GET_ALL_FACILITY = API_PREFIX + "/all-facility";

    String API_COUNT_ALL_FACILITY = API_PREFIX + "/count-all-facility";

    String FACILITY_INFO_ASYNC = API_PREFIX + "/facility-info-async";

    String API_GET_FACILITY_DETAIL_BY_ID = API_PREFIX + "/facility-detail-by-id";

    String COUNT_FACILITY_BY_TENANTID = API_PREFIX + "/count-facility-by-tenantId";

    @PostMapping(COUNT_FACILITY_BY_TENANTID)
    R<Integer> countFacilityByTenantId(@RequestBody String tenantId,@RequestParam("deviceStatus") String deviceStatus);


    @PostMapping(FACILITY_INFO_ASYNC)
    R<Boolean> facilityInfoAsync(@RequestBody List<List<String>> datasList,@RequestParam String tenantId, @RequestParam String actionType);

    /**
     * 根据facilityId找设施
     * @param facilityId
     * @return
     */
    @GetMapping(API_GET_FACILITY_BY_ID)
    R<FacilityInfo> getFacilityInfoById(@RequestParam("facilityId") Long facilityId);

    /**
     * 根据facilityId找设施及详情
     * @param facilityId
     * @return
     */
    @GetMapping(API_GET_FACILITY_DETAIL_BY_ID)
    R<FacilityInfoExtVO> getFacilityDetailById(@RequestParam("facilityId") Long facilityId);

    /**
     * 根据facilityId找设施
     * @return
     */
    @GetMapping(API_GET_UNREGION_FACILITY)
    R<List<FacilityInfo>> getUnRegionFacility();

    @PostMapping(API_POST_FACILITY_REGION)
    R updateFacilityInfo(@RequestBody FacilityInfo facilityInfo);

    @GetMapping(API_GET_LAST_DAYS_GARBAGE_AMOUNT)
    R<List<LastDaysGarbageAmountVO>> getLastDaysGarbageAmount(@RequestParam("days") Integer days, @RequestParam("tenantId") String tenantId);

    @GetMapping(API_GET_LAST_DAYS_GARBAGE_AMOUNT_BY_REGION)
    R<List<LastDaysRegionGarbageAmountVO>> getLastDaysGarbageAmountByRegion(@RequestParam("days") Integer days, @RequestParam("tenantId") String tenantId);

    @GetMapping(API_GET_ALL_FACILITY)
    R<List<FacilityInfo>> getAllFacility();

    @GetMapping(API_COUNT_ALL_FACILITY)
    R<Integer> countAllFacility(@RequestParam("tenantId") String tenantId);


}
