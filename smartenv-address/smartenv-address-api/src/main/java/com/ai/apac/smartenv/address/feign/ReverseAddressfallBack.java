package com.ai.apac.smartenv.address.feign;

import com.ai.apac.smartenv.common.dto.BaiduMapReverseGeoCodingResult;
import com.ai.apac.smartenv.common.dto.Coords;
import org.springblade.core.tool.api.R;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: ReverseAddressfallBack
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/2
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/2  17:03    panfeng          v1.0.0             修改原因
 */
public class ReverseAddressfallBack implements IReverseAddressClient {

    @Override
    public R<String> saveAddress(BaiduMapReverseGeoCodingResult geoCodingResult) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<BaiduMapReverseGeoCodingResult> getAddress(Coords coords) {
        return R.fail("接收数据失败");
    }
}
