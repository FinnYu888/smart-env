package com.ai.apac.smartenv.device.feign;

import com.ai.apac.smartenv.device.entity.SimInfo;
import com.ai.apac.smartenv.device.entity.SimRel;
import org.springblade.core.tool.api.R;

import java.util.List;

/**
 * @ClassName ISimClientFallBack
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/28 20:08
 * @Version 1.0
 */
public class ISimClientFallBack implements ISimClient {
    @Override
    public R<SimInfo> getSimByDeviceId(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<SimRel> getSimRelBySimCode2(String simCode2) {
        return R.fail("根据simCode2获取simRel数据失败");
    }

    @Override
    public R<SimInfo> getSimBySimCode(String simCode) {
        return R.fail("根据simCode获取数据失败");
    }

    @Override
    public R<SimInfo> getSimBySimNumber(String simNumber) {
        return R.fail("根据simNumber获取数据失败");
    }

}
