package com.ai.apac.smartenv.workarea.feign;

import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.service.IWorkareaAsyncService;
import com.ai.apac.smartenv.workarea.service.IWorkareaInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WorkareaClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/14  18:24    panfeng          v1.0.0             修改原因
 */
@ApiIgnore
@RestController
@RequiredArgsConstructor
public class WorkareaClient implements IWorkareaClient {
    @Autowired
    private IWorkareaInfoService workareaInfoService;

    @Autowired
    private IWorkareaAsyncService workareaAsyncService;

    @Override
    public R<Boolean> workareaInfoAsync(@RequestBody List<List<String>> datasList,@RequestParam String tenantId, @RequestParam String actionType) {
        return R.data(workareaAsyncService.thirdWorkareaInfoAsync(datasList,tenantId,actionType,true));
    }

    @Override
    @GetMapping(GET_BY_ID)
    public R<WorkareaInfo> getWorkInfoById(@RequestParam("id") Long id) {
        WorkareaInfo workareaInfo=new WorkareaInfo();
        workareaInfo.setId(id);
        return R.data(workareaInfoService.getOne(Condition.getQueryWrapper(workareaInfo)));
    }

    @Override
    @PostMapping(UPDATE_WORKAREA_INFO)
    public R<Boolean> updateWorkareaInfo(@RequestBody WorkareaInfo workareaInfo) {
        return R.data(workareaInfoService.updateById(workareaInfo));
    }

    @Override
    @GetMapping(GET_BY_REGION_ID)
    public R<List<WorkareaInfo>> getWorkareaInfoByRegion(@RequestParam("regionId") Long regionId){
        return R.data(workareaInfoService.list(new QueryWrapper<WorkareaInfo>().eq("region_id",regionId)));
    }
    
    @Override
    @GetMapping(GET_BY_TENANT_ID)
    public R<List<WorkareaInfo>> getWorkareaInfoByTenantId(@RequestParam("tenantId") String tenantId){
    	return R.data(workareaInfoService.list(new QueryWrapper<WorkareaInfo>().eq("tenant_id",tenantId)));
    }

    @Override
    @PostMapping(GET_NAME_BY_IDS)
    public R<Map<Long,String>> getWorkInfoByIds(@RequestBody List<Long> ids) {
        List<WorkareaInfo> workareaInfoList = workareaInfoService.list(new QueryWrapper<WorkareaInfo>().in("id",ids));
        Map<Long,String> reMap = new HashMap<>();
        if(null!=workareaInfoList){
            for (WorkareaInfo workareaInfo : workareaInfoList) {
                reMap.put(workareaInfo.getId(),workareaInfo.getAreaName());
            }
        }
        return R.data(reMap);
    }

}
