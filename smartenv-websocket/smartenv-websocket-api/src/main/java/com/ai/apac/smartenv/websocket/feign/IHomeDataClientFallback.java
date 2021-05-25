package com.ai.apac.smartenv.websocket.feign;

import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

/**
 * Copyright: Copyright (c) 2020/8/19 Asiainfo
 *
 * @ClassName: IHomeDataClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  10:09    zhanglei25          v1.0.0             修改原因
 */
@Component
public class IHomeDataClientFallback implements IHomeDataClient{

    @Override
    public R<Boolean> updateHomeCountRedis(String tenantId) {
        return R.fail("首页统计数据更新失败");
    }

    @Override
    public R<Boolean> updateHomeAlarmListRedis(String tenantId) {
        return R.fail("首页告警列表数据更新失败");
    }

    @Override
    public R<Boolean> updateHomeEventListRedis(String tenantId) {
        return R.fail("首页事件列表数据更新失败");
    }

    @Override
    public R<Boolean> updateHomeOrderListRedis(String tenantId,String userId) {
        return R.fail("首页任务列表数据更新失败");
    }

    @Override
    public R<Boolean> updateHomeGarbageAmountRedis(String tenantId) {
        return R.fail("首页垃圾统计数据更新失败");
    }
}
