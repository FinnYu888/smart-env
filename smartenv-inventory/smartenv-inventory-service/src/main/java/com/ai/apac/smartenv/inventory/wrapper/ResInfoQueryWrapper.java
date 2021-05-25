package com.ai.apac.smartenv.inventory.wrapper;

import com.ai.apac.smartenv.inventory.entity.ResInfo;
import com.ai.apac.smartenv.inventory.entity.ResInfoQuery;
import com.ai.apac.smartenv.inventory.vo.ResInfoQueryVO;
import com.ai.apac.smartenv.inventory.vo.ResInfoVO;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;

public class ResInfoQueryWrapper extends BaseEntityWrapper<ResInfoQuery, ResInfoQueryVO> {

    public static ResInfoQueryWrapper build() {
        return new ResInfoQueryWrapper();
    }

    @Override
    public ResInfoQueryVO entityVO(ResInfoQuery resInfoQuery) {
        ResInfoQueryVO resInfoQueryVO = BeanUtil.copy(resInfoQuery, ResInfoQueryVO.class);

        return resInfoQueryVO;
    }
}
