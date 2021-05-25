package com.ai.apac.smartenv.system.service;

import java.util.List;

public interface IStationAsyncService {

    Boolean thirdStationInfoAsync(List<List<String>> datasList,String tenantId,String actionType,Boolean isAsyn) ;

}
