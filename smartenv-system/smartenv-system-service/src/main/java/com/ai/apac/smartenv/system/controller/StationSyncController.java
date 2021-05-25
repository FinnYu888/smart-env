package com.ai.apac.smartenv.system.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.system.service.IDeptAsyncService;
import com.ai.apac.smartenv.system.service.IStationAsyncService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sync")
@AllArgsConstructor
public class StationSyncController {

    private IStationAsyncService stationAsyncService;

    /**
     * 第三方同步人员信息
     */
    @PostMapping("/station")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "第三方同步岗位信息", notes = "第三方同步岗位信息")
    @ApiLog(value = "第三方同步岗位信息")
    public R<Boolean> syncthirdDeptInfo(@RequestBody JSONObject obj) {
        List<List<String>> datasList = initializeData(obj);
        String tenantId = AuthUtil.getTenantId();
        return R.data(stationAsyncService.thirdStationInfoAsync(datasList,tenantId, "",false));
    }

    /**
     * 第三方删除人员信息
     */
    @DeleteMapping("/station")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "第三方删除岗位信息", notes = "第三方删除岗位信息")
    @ApiLog(value = "第三方删除岗位信息")
    public R<Boolean> delthirdDeptInfo(@RequestBody JSONObject obj) {
        List<List<String>> datasList = initializeData(obj);
        String tenantId = AuthUtil.getTenantId();
        return R.data(stationAsyncService.thirdStationInfoAsync(datasList,tenantId, OmnicConstant.ACTION_TYPE.DELETE,false));
    }


    private List<List<String>> initializeData(JSONObject obj){
        List<List<String>> datasList = new ArrayList<List<String>>();
        List<String> data = new ArrayList<String>();
        if(ObjectUtil.isNotEmpty(obj.getStr("stationCode"))){
            data.add(obj.getStr("stationCode"));
        }else{
            throw new ServiceException("岗位编号不能为空");
        }

        String stationName = obj.getStr("stationName")==null?"":obj.getStr("stationName");
        data.add(stationName);

        String parentCode = obj.getStr("parentCode")==null?"":obj.getStr("parentCode");
        data.add(parentCode);

        datasList.add(data);
        return datasList;
    }
}
