package com.ai.apac.smartenv.websocket.feign;

import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

/**
 * Copyright: Copyright (c) 2020/9/22 Asiainfo
 *
 * @ClassName: IPolymerizationDataClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/9/22
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/9/22  16:53    zhanglei25          v1.0.0             修改原因
 */
@Component
public class IPolymerizationDataClientFallback  implements IPolymerizationDataClient{
    @Override
    public R<Boolean> updatePolymerizationCountRedis(String tenantId,String entityType) {
        return R.fail("首页统计数据更新失败");
    }
}
