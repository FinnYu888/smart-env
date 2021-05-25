package com.ai.apac.smartenv.inventory.feign;

import cn.hutool.core.util.StrUtil;
import com.ai.apac.smartenv.inventory.vo.ResSpecVO;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: IResSpecClientFallBack
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
public class IResSpecClientFallBack implements IResSpecClient {

    @Override
    public R<List<ResSpecVO>> listSpecByTenant(String tenantId) {
        return R.fail(StrUtil.format("根据租户Id[{}]没有取到物资规格", tenantId));
    }
}
