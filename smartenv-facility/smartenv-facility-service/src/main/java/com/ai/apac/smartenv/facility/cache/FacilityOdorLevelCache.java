package com.ai.apac.smartenv.facility.cache;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.smartenv.cache.util.SmartCache;

import java.util.Map;

public class FacilityOdorLevelCache {
    //获取租户下中转站的臭味级别
    public static Float getFacilityOdorLevel(String facilityId) {
        Float odorLevel = SmartCache.hget(CacheNames.FACILITY_ODOR,facilityId);
       return odorLevel;
    }
    /**
    * 更新臭味级别
    * @author 66578
    */
    public static void putFacilityOdorLevel(String facilityId, Float facilityOdoyValue) {
        SmartCache.hset(CacheNames.FACILITY_ODOR,facilityId,facilityOdoyValue);
    }
}
