package com.ai.apac.smartenv.omnic.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.omnic.service.ISyncService;
import com.ai.apac.smartenv.omnic.service.IViewService;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: OmnicSyncController
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/12  15:53    zhanglei25          v1.0.0             修改原因
 */
@RestController
@RequestMapping("/sync/info")
@AllArgsConstructor
@Slf4j
@Api(value = "基础台帐数据同步", tags = "基础台帐数据同步")
public class OmnicSyncController {

    private ISyncService syncService;


    @PostMapping("")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "基础台帐数据同步", notes = "基础台帐数据同步")
    public R<Boolean> syncInfo(@RequestParam(required = true) String optType,@RequestParam("file") MultipartFile excel,@RequestParam(required = true) String actionType) throws IOException {
        if(OmnicConstant.ACTION_TYPE.NEW.equals(actionType) || OmnicConstant.ACTION_TYPE.UPDATE.equals(actionType) || OmnicConstant.ACTION_TYPE.DELETE.equals(actionType)){
            String tenantId = AuthUtil.getTenantId();
            InputStream inputStream1 = new BufferedInputStream(excel.getInputStream());
            List<Object> datas = EasyExcelFactory.read(inputStream1, new Sheet(1, 1));
            syncService.syncInfo(datas,optType,actionType,AuthUtil.getTenantId());
            return R.data(true);
        }else{
            return R.data(false,"ActionType不支持");
        }
    }



}
