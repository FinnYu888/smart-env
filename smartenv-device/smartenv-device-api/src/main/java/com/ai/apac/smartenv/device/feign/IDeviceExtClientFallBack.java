package com.ai.apac.smartenv.device.feign;

import com.ai.apac.smartenv.device.entity.DeviceExt;
import org.springblade.core.tool.api.R;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IDeviceExtClientFallBack
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/7
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/7  1:33    panfeng          v1.0.0             修改原因
 */
public class IDeviceExtClientFallBack implements IDeviceExtClient {
    @Override
    public R<DeviceExt> getByAttrId(Long deviceId, Long attrId) {
        return R.fail("接收数据失败");
    }
}
