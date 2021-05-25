package com.ai.apac.smartenv.address.feign;

import com.ai.apac.smartenv.address.service.impl.AddressService;
import com.ai.apac.smartenv.address.vo.GisInfoVO;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/8/27 4:31 下午
 **/
@ApiIgnore
@RestController
@AllArgsConstructor
public class AddressClient implements IAddressClient {

    @Autowired
    private AddressService addressService;

    @Override
    @GetMapping(GET_GIS_INFO)
    public R<GisInfoVO> getGisInfo(String areaCode) {
        if (StringUtils.isEmpty(areaCode)) {
            return R.fail("获取不能为空");
        }
        return R.data(addressService.getGisInfoByAreaCode(areaCode));
    }
}
