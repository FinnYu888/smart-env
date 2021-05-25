package com.ai.apac.smartenv.inventory.service.impl;

import com.ai.apac.smartenv.common.constant.InventoryConstant;
import com.ai.apac.smartenv.inventory.service.IResManageService;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ResManageServiceImpl implements IResManageService {
    @Override
    public String getExceptionMsg(String key) {
        String msg = DictBizCache.getValue(InventoryConstant.ExceptionMsg.CODE, key);
        if (StringUtils.isBlank(msg)) {
            msg = key;
        }
        return msg;
    }
}
