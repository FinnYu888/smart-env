package com.ai.apac.smartenv.omnic.service;

import com.ai.apac.smartenv.omnic.vo.WorkingDataCountVO;

import java.util.List;

/**
 * @ClassName ISyncService
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/13 14:55
 * @Version 1.0
 */
public interface ISyncService {

    /**
     * 同步第三方的基础台帐数据
     */
    void syncInfo(List<Object> datas,String optType,String actionType,String tenantId);
}
