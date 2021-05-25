package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.vo.BusiRegionTreeVO;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IRegionClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/9/15
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/9/15  2020/9/15    panfeng          v1.0.0             修改原因
 */
public class IRegionClientFallback  implements IRegionClient{


    @Override
    public R<Boolean> regionInfoAsync(@RequestBody List<List<String>> datasList, @RequestParam String tenantId, @RequestParam String actionType) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<BusiRegionTreeVO> queryChildBusiRegionList(Long regionId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<Region> getRegionById(Long regionId) {
        return R.fail("接收数据失败");
    }
}
