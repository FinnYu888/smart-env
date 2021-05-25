package com.ai.apac.smartenv.address.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.common.dto.BaiduMapReverseGeoCodingResult;
import com.ai.apac.smartenv.common.dto.Coords;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: ReverseAddressClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/2
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/2  16:50    panfeng          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_ADDR_NAME,
        fallback = ReverseAddressfallBack.class
)
public interface IReverseAddressClient {

    public String client="/client";
    public String SAVE_ADDRESS="/save-address";
    public String GET_ADDRESS="/get-address";

    @PostMapping(SAVE_ADDRESS)
    R<String> saveAddress(@RequestBody BaiduMapReverseGeoCodingResult geoCodingResult);

    @PostMapping(GET_ADDRESS)
    R<BaiduMapReverseGeoCodingResult> getAddress(@RequestBody Coords coords);




}
