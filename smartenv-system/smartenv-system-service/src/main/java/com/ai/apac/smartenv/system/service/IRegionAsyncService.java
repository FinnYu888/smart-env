package com.ai.apac.smartenv.system.service;

import java.util.List;

public interface IRegionAsyncService {

    Boolean thirdRegionInfoAsync(List<List<String>> datasList,String tenantId,String actionType,Boolean isAsyn) ;

}
