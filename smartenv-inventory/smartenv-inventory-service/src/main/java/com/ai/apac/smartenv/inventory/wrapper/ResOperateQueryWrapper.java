package com.ai.apac.smartenv.inventory.wrapper;

import com.ai.apac.smartenv.inventory.entity.ResInfo;
import com.ai.apac.smartenv.inventory.entity.ResOperate;
import com.ai.apac.smartenv.inventory.entity.ResOperateQuery;
import com.ai.apac.smartenv.inventory.vo.ResInfoVO;
import com.ai.apac.smartenv.inventory.vo.ResOperateQueryVO;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;

public class ResOperateQueryWrapper extends BaseEntityWrapper<ResOperateQuery, ResOperateQueryVO> {

    public static ResOperateQueryWrapper build() {
        return new ResOperateQueryWrapper();
    }

    @Override
    public ResOperateQueryVO entityVO(ResOperateQuery resOperateQuery) {
        ResOperateQueryVO resOperateQueryVO = BeanUtil.copy(resOperateQuery, ResOperateQueryVO.class);

        return resOperateQueryVO;
    }
}
