package com.ai.apac.smartenv.inventory.feign;

import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: ResTypeClientFallBack
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/8/11
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/8/11     zhaidx           v1.0.0               修改原因
 */
@Component
public class IResTypeClientFallBack implements IResTypeClient{
    @Override
    public R<List<String>> listResTypeResSpecNameIdStrings(String tenantId) {
        return R.fail("获取物资类型/物资规格名称列表失败");
    }
}
