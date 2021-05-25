package com.ai.apac.smartenv.address.feign;

import com.ai.apac.smartenv.address.vo.GisInfoVO;
import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/8/27 4:26 下午
 **/
@FeignClient(
        value = ApplicationConstant.APPLICATION_ADDR_NAME,
        fallback = AddressClientFallback.class
)
public interface IAddressClient {

    String client = "/client/address/";

    String GET_GIS_INFO = client + "getGisInfo";

    @GetMapping(GET_GIS_INFO)
    R<GisInfoVO> getGisInfo(@RequestParam String areaCode);
}
