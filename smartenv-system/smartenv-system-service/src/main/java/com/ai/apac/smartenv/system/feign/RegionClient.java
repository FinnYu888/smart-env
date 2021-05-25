package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.service.IRegionAsyncService;
import com.ai.apac.smartenv.system.service.IRegionService;
import com.ai.apac.smartenv.system.vo.BusiRegionTreeVO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: RegionClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/9/15
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/9/15  2020/9/15    panfeng          v1.0.0             修改原因
 */
//@ApiIgnore
@RestController
@AllArgsConstructor
public class RegionClient implements IRegionClient {


    private IRegionService regionService;

    private IRegionAsyncService regionAsyncService;


    @Override
    public R<Boolean> regionInfoAsync(@RequestBody List<List<String>> datasList, @RequestParam String tenantId, @RequestParam String actionType) {
        return R.data(regionAsyncService.thirdRegionInfoAsync(datasList,tenantId,actionType,true));
    }

    @GetMapping(QUERY_CHILD_BUSI_REGION_LIST)
    @Override
    public R<BusiRegionTreeVO> queryChildBusiRegionList(@RequestParam Long regionId) {
        return R.data(regionService.queryChildBusiRegionList(regionId));
    }


    @GetMapping(GET_REGION_BY_ID)
    @Override
    public R<Region> getRegionById(@RequestParam("id") Long id) {
        return R.data(regionService.getById(id));
    }

}
