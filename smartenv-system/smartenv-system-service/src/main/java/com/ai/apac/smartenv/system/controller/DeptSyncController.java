package com.ai.apac.smartenv.system.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.system.service.IDeptAsyncService;
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
public class DeptSyncController {

    private IDeptAsyncService deptAsyncService;

    /**
     * 第三方同步人员信息
     */
    @PostMapping("/deptInfo")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "第三方同步部门信息", notes = "第三方同步部门信息")
    @ApiLog(value = "第三方同步部门信息")
    public R<Boolean> syncthirdDeptInfo(@RequestBody JSONObject obj) {
        List<List<String>> datasList = initializeData(obj);
        String tenantId = AuthUtil.getTenantId();
        return R.data(deptAsyncService.thirdDeptInfoAsync(datasList,tenantId, "",false));
    }

    /**
     * 第三方删除人员信息
     */
    @DeleteMapping("/deptInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "第三方删除部门信息", notes = "第三方删除部门信息")
    @ApiLog(value = "第三方删除部门信息")
    public R<Boolean> delthirdDeptInfo(@RequestBody JSONObject obj) {
        List<List<String>> datasList = initializeData(obj);
        String tenantId = AuthUtil.getTenantId();
        return R.data(deptAsyncService.thirdDeptInfoAsync(datasList,tenantId, OmnicConstant.ACTION_TYPE.DELETE,false));
    }


    private List<List<String>> initializeData(JSONObject obj){
        List<List<String>> datasList = new ArrayList<List<String>>();
        List<String> data = new ArrayList<String>();
        if(ObjectUtil.isNotEmpty(obj.getStr("deptCode"))){
            data.add(obj.getStr("deptCode"));
        }else{
            throw new ServiceException("人员编号不能为空");
        }

        String deptName = obj.getStr("deptName")==null?"":obj.getStr("deptName");
        data.add(deptName);

        String parentCode = obj.getStr("parentCode")==null?"":obj.getStr("parentCode");
        data.add(parentCode);

        datasList.add(data);
        return datasList;
    }
}
