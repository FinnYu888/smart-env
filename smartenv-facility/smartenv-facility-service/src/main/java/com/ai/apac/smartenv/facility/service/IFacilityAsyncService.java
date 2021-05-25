package com.ai.apac.smartenv.facility.service;

import com.ai.apac.smartenv.facility.entity.ToiletInfo;

import java.util.List;

public interface IFacilityAsyncService {
    Boolean thirdFacilityInfoAsync(List<List<String>> datasList,String tenantId,String actionType,Boolean isAsyn) ;

}
