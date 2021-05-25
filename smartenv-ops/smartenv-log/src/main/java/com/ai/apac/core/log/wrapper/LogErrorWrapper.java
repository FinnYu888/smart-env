package com.ai.apac.core.log.wrapper;

import com.ai.apac.core.log.vo.LogErrorVO;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import org.springblade.core.log.model.LogError;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;

/**
 * @author qianlong
 * @description 视图包装类
 * @Date 2020/3/30 9:16 下午
 **/
public class LogErrorWrapper extends BaseEntityWrapper<LogError, LogErrorVO> {

    public static LogErrorWrapper build() {
        return new LogErrorWrapper();
    }

    @Override
    public LogErrorVO entityVO(LogError entity) {
        LogErrorVO logErrorVO = BeanUtil.copy(entity, LogErrorVO.class);
        logErrorVO.setTenantName(TenantCache.getTenantName(entity.getTenantId()));
        logErrorVO.setCreatorName(UserCache.getUserName(entity.getCreateBy()));
        return logErrorVO;
    }
}
