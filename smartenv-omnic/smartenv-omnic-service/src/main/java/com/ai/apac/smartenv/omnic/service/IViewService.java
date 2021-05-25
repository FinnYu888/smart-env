package com.ai.apac.smartenv.omnic.service;

import com.ai.apac.smartenv.omnic.vo.TenantDetailsVO;
import com.ai.apac.smartenv.omnic.vo.WorkAreaDetailVO;
import com.ai.apac.smartenv.omnic.vo.WorkareaInfoBigScreenVO;
import com.ai.apac.smartenv.omnic.vo.WorkingDataCountVO;

/**
 * @ClassName IBigScreenViewService
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/13 14:55
 * @Version 1.0
 */
public interface IViewService {

    WorkingDataCountVO getWorkingDataCount();


    WorkAreaDetailVO getWorkAreaDetails();

    TenantDetailsVO getTenantDetails(String tenantId);

    WorkareaInfoBigScreenVO getWorkareaInfoBigScreen(String tenantId);

}
