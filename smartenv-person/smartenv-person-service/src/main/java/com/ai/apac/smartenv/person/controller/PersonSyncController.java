package com.ai.apac.smartenv.person.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.person.service.IPersonAsyncService;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: PersonSyncController
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/11/26
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/11/26  9:48    panfeng          v1.0.0             修改原因
 */
@RestController
@RequestMapping("/sync")
@AllArgsConstructor
public class PersonSyncController {

    private IPersonAsyncService personAsyncService;

    /**
     * 第三方同步人员信息
     */
    @PostMapping("/personInfo")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "第三方同步人员信息", notes = "第三方同步人员信息")
    @ApiLog(value = "第三方同步人员信息")
    public R<Boolean> syncthirdPersonInfo(@RequestBody JSONObject obj) {
        List<List<String>> datasList = initializeData(obj);
        String tenantId = AuthUtil.getTenantId();
        return R.data(personAsyncService.thirdPersonInfoAsync(datasList,tenantId, "",false));
    }

    /**
     * 第三方删除人员信息
     */
    @DeleteMapping("/personInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "第三方删除人员信息", notes = "第三方删除人员信息")
    @ApiLog(value = "第三方删除人员信息")
    public R<Boolean> delthirdPersonInfo(@RequestBody JSONObject obj) {
        List<List<String>> datasList = initializeData(obj);
        String tenantId = AuthUtil.getTenantId();
        return R.data(personAsyncService.thirdPersonInfoAsync(datasList,tenantId,OmnicConstant.ACTION_TYPE.DELETE,false));
    }


    private List<List<String>> initializeData(JSONObject obj){
        List<List<String>> datasList = new ArrayList<List<String>>();
        List<String> data = new ArrayList<String>();
        if(ObjectUtil.isNotEmpty(obj.getStr("personCode"))){
            data.add(obj.getStr("personCode"));
        }else{
            throw new ServiceException("人员编号不能为空");
        }

        String personName = obj.getStr("personName")==null?"":obj.getStr("personName");
        data.add(personName);

        String gender = obj.getStr("gender")==null?"":obj.getStr("gender");
        data.add(gender);

        String age = obj.getStr("age")==null?"":obj.getStr("age");
        data.add(age);

        String idCard = obj.getStr("idCard")==null?"":obj.getStr("idCard");
        data.add(idCard);

        String mobileNumber = obj.getStr("mobileNumber")==null?"":obj.getStr("mobileNumber");
        data.add(mobileNumber);

        String email = obj.getStr("email")==null?"":obj.getStr("email");
        data.add(email);

        String personPosition = obj.getStr("personPosition")==null?"":obj.getStr("personPosition");
        data.add(personPosition);

        String deptCode = obj.getStr("deptCode")==null?"":obj.getStr("deptCode");
        data.add(deptCode);

        String deviceCode = obj.getStr("deviceCode")==null?"":obj.getStr("deviceCode");
        data.add(deviceCode);

        String entryTime = obj.getStr("entryTime")==null?"":obj.getStr("entryTime");
        data.add(entryTime);

        String leaveTime = obj.getStr("leaveTime")==null?"":obj.getStr("leaveTime");
        data.add(leaveTime);

        datasList.add(data);

        return datasList;
    }

    /**
     * 根据项目将人中最新状态同步刷新到MongoDB中
     * @param projectCode
     * @return
     */
    @PostMapping("/deviceStatus/{projectCode}")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "同步项目将人员设备状态到Mongo", notes = "根据项目将人员设备最新状态同步刷新到MongoDB中")
    public R syncVehicleDeviceStatusToMongo(@PathVariable String projectCode){
        personAsyncService.syncPersonDeviceStatusToMongo(projectCode);
        return R.status(true);
    }
}
