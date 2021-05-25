package com.ai.apac.core.log.wrapper;

import com.ai.apac.core.log.vo.LogApiVO;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import org.springblade.core.log.model.LogApi;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;

/**
 * @author qianlong
 * @description 视图包装类
 * @Date 2020/3/30 9:16 下午
 **/
public class LogApiWrapper extends BaseEntityWrapper<LogApi, LogApiVO> {

    public static LogApiWrapper build() {
        return new LogApiWrapper();
    }

    @Override
    public LogApiVO entityVO(LogApi entity) {
        LogApiVO logApiVO = BeanUtil.copy(entity, LogApiVO.class);
        logApiVO.setTenantName(TenantCache.getTenantName(entity.getTenantId()));
        logApiVO.setCreatorName(UserCache.getUserName(entity.getCreateBy()));
        return logApiVO;
    }
}
