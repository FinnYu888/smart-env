package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.vo.QScheduleObjectVO;
import org.springblade.core.tool.api.R;

import java.util.List;

/**
 *
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IArrangeClient.java
 * @Description: 该类的功能描述
 *
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020年11月30日 下午5:05:39
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 * 2020年11月30日     zhanglei25           v1.0.0               修改原因
 */
public class IMappingClientFallback implements IMappingClient{

    @Override
    public R<AiMapping> getSscpCodeByThirdCode(AiMapping mapping) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<Boolean> delMapping(String sscpCode, Integer codeType) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<Boolean> saveMappingCode(AiMapping mapping) {
        return R.fail("接收数据失败");
    }
}
