package com.ai.apac.smartenv.workarea.feign;

import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WorkareaClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/14  18:12    panfeng          v1.0.0             修改原因
 */
public class WorkareaClientFallback implements IWorkareaClient {


    @Override
    public R<Boolean> workareaInfoAsync(@RequestBody List<List<String>> datasList, @RequestParam String tenantId, @RequestParam String actionType) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<WorkareaInfo> getWorkInfoById(Long id) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<WorkareaInfo>> getWorkareaInfoByRegion(Long regionId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<Boolean> updateWorkareaInfo(WorkareaInfo workareaInfo) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<WorkareaInfo>> getWorkareaInfoByTenantId(String tenantId) {
    	return R.fail("接收数据失败");
    }

    @Override
    public R<Map<Long,String>> getWorkInfoByIds(List<Long> ids){
        return R.fail("接收数据失败");
    }
}
