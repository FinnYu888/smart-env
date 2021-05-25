package com.ai.apac.smartenv.device.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.SimInfo;
import com.ai.apac.smartenv.device.entity.SimRel;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @ClassName ISimClient
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/28 20:06
 * @Version 1.0
 */
@FeignClient( value = ApplicationConstant.APPLICATION_DEVICE_NAME,
        fallback = DeviceClientFallBack.class
)
public interface ISimClient {
    String API_PREFIX = "/client";
    String API_GET_SIM_BY_DEVICEID = API_PREFIX + "/getSimByDeviceId";
    String API_GET_SIM_REL_BY_SIMCODE2 = API_PREFIX + "/getSimRelBySimCode2";
    String API_GET_SIM_BY_SIMCODE = API_PREFIX + "/getSimBySimCode";
    String API_GET_SIM_BY_SIMNUMBER = API_PREFIX + "/getSimBySimNumber";


    @GetMapping(value = API_GET_SIM_BY_DEVICEID)
    R<SimInfo> getSimByDeviceId(@RequestParam("id") Long id);

    @GetMapping(value = API_GET_SIM_REL_BY_SIMCODE2)
    R<SimRel> getSimRelBySimCode2(@RequestParam("simCode2") String simCode2);

    @GetMapping(value = API_GET_SIM_BY_SIMCODE)
    R<SimInfo> getSimBySimCode(@RequestParam("simCode") String simCode);

    @GetMapping(value = API_GET_SIM_BY_SIMNUMBER)
    R<SimInfo> getSimBySimNumber(@RequestParam("simNumber") String simNumber);
}
