package com.ai.apac.smartenv.address.feign;

import com.ai.apac.smartenv.address.service.IAddressService;
import com.ai.apac.smartenv.common.dto.BaiduMapReverseGeoCodingResult;
import com.ai.apac.smartenv.common.dto.Coords;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

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
 * 2020/3/2  17:12    panfeng          v1.0.0             修改原因
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class ReverseAddressClient implements IReverseAddressClient {


    @Autowired
    private IAddressService addressService;

    @Override
    @PostMapping(SAVE_ADDRESS)
    public R<String> saveAddress(@RequestBody BaiduMapReverseGeoCodingResult geoCodingResult) {
        addressService.saveAddress(geoCodingResult);

        return R.data("success");
    }

    @Override
    @PostMapping(GET_ADDRESS)
    public R<BaiduMapReverseGeoCodingResult> getAddress(@RequestBody Coords coords) {
        return R.data(addressService.getAddress(coords));
    }
}
