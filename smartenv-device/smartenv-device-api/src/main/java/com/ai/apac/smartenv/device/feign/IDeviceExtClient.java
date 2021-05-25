package com.ai.apac.smartenv.device.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.device.entity.DeviceExt;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IDeviceExtClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/7
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/7  1:32    panfeng          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_DEVICE_NAME,
        fallback = IDeviceExtClientFallBack.class
)
public interface IDeviceExtClient {
    String API_PREFIX = "/client";
    String API_GET_BY_ATTR = API_PREFIX + "/getByAttr";

    @GetMapping(API_GET_BY_ATTR)
    R<DeviceExt> getByAttrId(@RequestParam Long deviceId,@RequestParam Long attrId);


}
