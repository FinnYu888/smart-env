package com.ai.apac.smartenv.system.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.system.service.IRegionAsyncService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleSyncController
 * @Description: 区域同步接口
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/11/25
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/11/25  19:07    panfeng          v1.0.0             修改原因
 */
@RestController
@RequestMapping("/sync")
@AllArgsConstructor
public class RegionSyncController {


    private IRegionAsyncService regionAsyncService;

    /**
     * 导入人员信息表
     */
    @PostMapping("/regionInfo")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "第三方同步区域信息", notes = "第三方同步区域信息")
    @ApiLog(value = "第三方同步区域信息")
    public R<Boolean> syncRegionInfo(@RequestBody JSONObject obj) {
        List<List<String>> datasList = initializeData(obj);
        String tenantId = AuthUtil.getTenantId();
        return R.data(regionAsyncService.thirdRegionInfoAsync(datasList,tenantId, OmnicConstant.ACTION_TYPE.NEW,false));
    }

    /**
     * 导入人员信息表
     */
    @DeleteMapping("/regionInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "第三方删除区域信息", notes = "第三方删除区域信息")
    @ApiLog(value = "第三方删除区域信息")
    public R<Boolean> delRegionInfo(@RequestBody JSONObject obj) {
        List<List<String>> datasList = initializeData(obj);
        String tenantId = AuthUtil.getTenantId();
        return R.data(regionAsyncService.thirdRegionInfoAsync(datasList,tenantId,OmnicConstant.ACTION_TYPE.DELETE,false));
    }


    private List<List<String>> initializeData(JSONObject obj){
        List<List<String>> datasList = new ArrayList<List<String>>();
        List<String> data = new ArrayList<String>();
        if(ObjectUtil.isNotEmpty(obj.getStr("regionCode"))){
            data.add(obj.getStr("regionCode"));
        }else{
            throw new ServiceException("区域编号不能为空");
        }

        String regionName = obj.getStr("regionName")==null?"":obj.getStr("regionName");
        data.add(regionName);

        String regionType = obj.getStr("regionType")==null?"":obj.getStr("regionType");
        data.add(regionType);

        JSONArray nodes = obj.getJSONArray("nodes");
        if(ObjectUtil.isNotEmpty(nodes) && nodes.size() > 0){
            StringBuilder nodeStr = new StringBuilder();
            Object[] nodeSet = nodes.toArray();
            for(Object node:nodeSet){
                JSONObject nodeObj = (JSONObject)node;
                nodeStr.append(nodeObj.getStr("lat")).append(",").append(nodeObj.getStr("lng")).append("|");
            }
            data.add(nodeStr.substring(0,nodeStr.length()-1));
        }else{
            data.add("");
        }

        datasList.add(data);

        return datasList;
    }
}

