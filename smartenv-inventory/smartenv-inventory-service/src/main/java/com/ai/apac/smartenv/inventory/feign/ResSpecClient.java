package com.ai.apac.smartenv.inventory.feign;

import com.ai.apac.smartenv.inventory.entity.ResSpec;
import com.ai.apac.smartenv.inventory.service.IResSpecService;
import com.ai.apac.smartenv.inventory.vo.ResSpecVO;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: ResSpecClient
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
public class ResSpecClient implements IResSpecClient{

    private IResSpecService resSpecService;
    
    @Override
    public R<List<ResSpecVO>> listSpecByTenant(String tenantId) {
        return R.data(resSpecService.listResSpecByTenantId(tenantId));
    }
}
