package com.ai.apac.smartenv.omnic.wrapper;

import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.omnic.vo.StatusCountVo;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: StatusCountWrapper
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/12  23:55    panfeng          v1.0.0             修改原因
 */
public class StatusCountWrapper  extends BaseEntityWrapper<StatusCount, StatusCountVo> {
    public static StatusCountWrapper build() {
        return new StatusCountWrapper();
    }

    @Override
    public StatusCountVo entityVO(StatusCount charSpecValue) {
        StatusCountVo statusCountVo = BeanUtil.copy(charSpecValue, StatusCountVo.class);

        return statusCountVo;
    }


}
