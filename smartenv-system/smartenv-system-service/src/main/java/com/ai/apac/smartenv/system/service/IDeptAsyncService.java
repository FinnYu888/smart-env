package com.ai.apac.smartenv.system.service;

import java.util.List;

public interface IDeptAsyncService {

    Boolean thirdDeptInfoAsync(List<List<String>> datasList,String tenantId,String actionType,Boolean isAsyn) ;

}
