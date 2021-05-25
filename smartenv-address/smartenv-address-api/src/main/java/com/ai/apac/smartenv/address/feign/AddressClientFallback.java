package com.ai.apac.smartenv.address.feign;

import com.ai.apac.smartenv.address.vo.GisInfoVO;
import org.springblade.core.tool.api.R;

public class AddressClientFallback implements IAddressClient{

    @Override
    public R<GisInfoVO> getGisInfo(String areaCode) {
        return R.fail("接收数据失败");
    }
}
