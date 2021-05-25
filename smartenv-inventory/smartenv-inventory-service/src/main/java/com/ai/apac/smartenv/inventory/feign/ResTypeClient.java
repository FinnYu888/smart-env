package com.ai.apac.smartenv.inventory.feign;

import com.ai.apac.smartenv.inventory.service.IResTypeService;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: ResTypeClient
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/8/11
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/8/11     zhaidx           v1.0.0               修改原因
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class ResTypeClient implements IResTypeClient {

    private IResTypeService resTypeService;

    @Override
    public R<List<String>> listResTypeResSpecNameIdStrings(String tenantId) {
        return R.data(resTypeService.listResTypeResSpecNameIdStrings(tenantId));
    }
}
