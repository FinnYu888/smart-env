package com.ai.apac.smartenv.device.service;

import com.ai.apac.smartenv.omnic.dto.ThirdSyncImportResultModel;

import java.util.List;

public interface IDeviceAsyncService {

    Boolean thirdDeviceInfoAsync(List<List<String>> datasList, String tenantId,String actionType,Boolean isAsyn) ;

}
