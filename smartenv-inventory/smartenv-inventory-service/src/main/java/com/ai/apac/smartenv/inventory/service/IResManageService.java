package com.ai.apac.smartenv.inventory.service;

import com.ai.apac.smartenv.common.constant.InventoryConstant;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import org.apache.commons.lang.StringUtils;

public interface IResManageService {
    /**
    *错误信息
    */
     String getExceptionMsg(String key);
}
