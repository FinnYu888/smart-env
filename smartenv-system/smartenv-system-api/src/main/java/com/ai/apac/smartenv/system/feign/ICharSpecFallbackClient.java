package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.system.entity.CharSpec;
import com.ai.apac.smartenv.system.entity.CharSpecValue;
import com.ai.apac.smartenv.system.vo.CharSpecVO;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: ICharSpecFallbackClient
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/2/7
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/2/7     zhaidx           v1.0.0               修改原因
 */
@Component
public class ICharSpecFallbackClient implements ICharSpecClient {
    
    @Override
    public R<CharSpec> getCharSpecById(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<CharSpecValue> getCharSpecValue(Long charSpecId,String value) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<CharSpecVO>> listCharSpecByEntityCategoryId(@NotEmpty String entityCategoryId) {
        return R.fail("获取数据失败");
    }
}
