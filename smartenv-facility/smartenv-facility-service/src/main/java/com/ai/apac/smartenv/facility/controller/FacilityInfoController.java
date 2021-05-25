/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.facility.controller;

import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DbEventConstant;
import com.ai.apac.smartenv.common.constant.FacilityConstant;
import com.ai.apac.smartenv.common.constant.ResultCodeConstant;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.facility.entity.FacilityExt;
import com.ai.apac.smartenv.facility.entity.FacilityInfo;
import com.ai.apac.smartenv.facility.service.IFacilityExtService;
import com.ai.apac.smartenv.facility.service.IFacilityInfoService;
import com.ai.apac.smartenv.facility.service.IFacilityTranstationDetailService;
import com.ai.apac.smartenv.facility.vo.FacilityExtVO;
import com.ai.apac.smartenv.facility.vo.FacilityInfoExtVO;
import com.ai.apac.smartenv.facility.vo.FacilityInfoVO;
import com.ai.apac.smartenv.facility.wrapper.FacilityInfoWrapper;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.ai.apac.smartenv.omnic.feign.IPolymerizationClient;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 控制器
 *
 * @author Blade
 * @since 2020-02-11
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("facilityinfo")
@Api(value = "设施信息接口", tags = "设施信息接口")
public class FacilityInfoController extends BladeController {

    private IFacilityInfoService facilityInfoService;
    private IFacilityExtService facilityExtService;
    private IFacilityTranstationDetailService facilityTranstationDetailService;
    private IDictClient dictClient;
    private IDeviceRelClient deviceRelClient;
    private IDeviceClient deviceClient;
    private IPolymerizationClient iPolymerizationClient;

    private CoordsTypeConvertUtil coordsTypeConvertUtil;
    private IDataChangeEventClient dataChangeEventClient;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入facilityInfo")
    public R<FacilityInfoVO> detail(FacilityInfo facilityInfo) {
        FacilityInfo detail = facilityInfoService.getOne(Condition.getQueryWrapper(facilityInfo));
        return R.data(FacilityInfoWrapper.build().entityVO(detail));
    }

    /**
     * 分页
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入facilityInfo")
    public R<IPage<FacilityInfoVO>> list(FacilityInfo facilityInfo, Query query) {
        IPage<FacilityInfo> pages = facilityInfoService.page(Condition.getPage(query), Condition.getQueryWrapper(facilityInfo));

        List<FacilityInfo> facilityInfos = coordsTypeConvertUtil.toWebConvert(pages.getRecords());
        pages.setRecords(facilityInfos);
        return R.data(FacilityInfoWrapper.build().pageVO(pages));
    }

    /**
     * 查询列表，不分页
     */
    @ApiLog(value = "查询列表，不分页")
    @GetMapping("/facilityInfolist")
    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "查询列表，不分页", notes = "传入facilityInfo")
    public R<List<FacilityInfoVO>> list(FacilityInfo facilityInfo, @RequestParam(name = "statusFlag", required = false) boolean statusFlag) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (StringUtil.isNotBlank(facilityInfo.getFacilityName()))
            queryWrapper.like("facility_name", facilityInfo.getFacilityName());
        if (statusFlag)
            queryWrapper.notIn("status", new ArrayList<String>(Arrays.asList(FacilityConstant.TranStationStatus.PLANNING, FacilityConstant.TranStationStatus.DROP)));
        List<FacilityInfo> infoList = facilityInfoService.list(queryWrapper);

        List<FacilityInfo> facilityInfos = coordsTypeConvertUtil.toWebConvert(infoList);


        return R.data(FacilityInfoWrapper.build().listVO(facilityInfos));
    }


    /**
     * 自定义分页
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "分页", notes = "传入facilityInfo")
    public R<IPage<FacilityInfoVO>> page(FacilityInfoVO facilityInfo, Query query) {
        IPage<FacilityInfoVO> pages = facilityInfoService.selectFacilityInfoPage(Condition.getPage(query), facilityInfo);
        return R.data(pages);
    }

    /**
     * 新增
     */
    @PostMapping
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入facilityInfo")
    public R save(@Valid @RequestBody FacilityInfo facilityInfo) {
        return R.status(facilityInfoService.saveOrUpdateFacilityInfo(facilityInfo));
    }

    /**
     * 修改
     */
    @PutMapping
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入facilityInfo")
    public R update(@Valid @RequestBody FacilityInfo facilityInfo) {
        return R.status(facilityInfoService.saveOrUpdateFacilityInfo(facilityInfo));
    }


    /**
     * 删除
     */
    @ApiLog(value = "删除中转站")
    @DeleteMapping
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids, BladeUser bladeUser) {
        R<List<DeviceRel>> deviceRelVOR = deviceRelClient.getEntityRels(Func.toLong(ids), CommonConstant.ENTITY_TYPE.FACILITY);
        if (null != deviceRelVOR && ResultCodeConstant.ResponseCode.SUCCESS == deviceRelVOR.getCode()) {
            if (null != deviceRelVOR.getData() && CollectionUtil.isNotEmpty(deviceRelVOR.getData()))
                throw new ServiceException(getExceptionMsg(FacilityConstant.ExceptionMsg.KEY_TRANSFER_STATUS_DEL));
        } else {
            throw new ServiceException(getExceptionMsg(FacilityConstant.ExceptionMsg.KEY_TRANSFER__DEL));
        }
        Boolean ruslt = facilityInfoService.deleteLogic(Func.toLongList(ids));
        //删除相应的mongodb
//        iPolymerizationClient.removeFacilityList(ids);
        dataChangeEventClient.doDbEvent(new BaseDbEventDTO<String>(DbEventConstant.EventType.REMOVE_TRANS_STATION_EVENT, bladeUser.getTenantId(), ids));
        return R.status(ruslt);
    }

    /**
     * 新增中转站
     */
    @ApiLog(value = "新增中转站")
    @PostMapping("/saveFacility")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "新增中转站信息", notes = "传入facilityInfoExt")
    public R saveFacility(@Valid @RequestBody FacilityInfoExtVO facilityInfoExtVO, BladeUser user) throws IOException {
        //唯一性校验
        this.checkAddUnionTranstation(facilityInfoExtVO.getProjectNo(), facilityInfoExtVO.getFacilityName(), user);

        FacilityInfo facilityInfo = new FacilityInfo();
        BeanUtil.copy(facilityInfoExtVO, facilityInfo);
//        //坐标系转换，转为GC02
        List<FacilityInfo> facilityInfos = new ArrayList<>();
        facilityInfos.add(facilityInfo);
        List<FacilityInfo> facilityInfosResu = coordsTypeConvertUtil.fromWebConvert(facilityInfos);
        if (CollectionUtil.isNotEmpty(facilityInfosResu)) {
            facilityInfo = facilityInfos.get(0);
        }

        //if (StringUtil.isBlank(facilityInfo.getTenantId())) facilityInfo.setTenantId("000000");
        facilityInfoService.save(facilityInfo);
        Long facilityId = facilityInfo.getId();
        List<FacilityExtVO> facilityExtVoList = facilityInfoExtVO.getFacilityExtVOList();
        if (null != facilityExtVoList) {
            facilityExtVoList.forEach(facilityExtVO -> {
                facilityExtVO.setFacilityId(facilityId);
                facilityExtService.save(facilityExtVO);
            });
        }
        //更新mongoDB
//        iPolymerizationClient.addOrUpdateFacility(facilityId.toString(), Integer.valueOf(FacilityConstant.FacilityType.TRANSFER_STATION));
        dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_TRANS_STATION_EVENT, user.getTenantId(), facilityId));
        return R.status(true);
    }

    /**
     * 获取中转站及中转站扩展信息、工作信息
     */
    @ApiLog(value = "获取中转站及中转站扩展信息、工作信息")
    @PostMapping("/getFacilityDetail")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "详情", notes = "传入facilityInfo")
    public R<FacilityInfoExtVO> getFacilityDetail(@RequestBody FacilityInfo facilityInfo) {
        return R.data(facilityInfoService.getFacilityDetail(facilityInfo));
    }

    /**
     * 修改中转站及中转站扩展信息
     */
    @ApiLog(value = "修改中转站及中转站扩展信息")
    @PutMapping("/modifyFacilityDetail")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "详情", notes = "传入facilityInfo")
    public R modifyFacilityDetail(@Valid @RequestBody FacilityInfoExtVO facilityInfoExt, BladeUser user) throws IOException {
        //唯一性校验
        this.checkModifyTranstation(facilityInfoExt.getId(), facilityInfoExt.getProjectNo(), facilityInfoExt.getFacilityName(), facilityInfoExt.getStatus(), user);
        FacilityInfo facilityInfo = new FacilityInfo();
        BeanUtil.copy(facilityInfoExt, facilityInfo);

        //坐标系转换，转为GC02
        if (facilityInfo.getLat() != null && facilityInfo.getLng() != null) {
            List<FacilityInfo> facilityInfos = new ArrayList<>();
            facilityInfos.add(facilityInfo);
            List<FacilityInfo> facilityInfosResu = coordsTypeConvertUtil.fromWebConvert(facilityInfos);
            if (CollectionUtil.isNotEmpty(facilityInfosResu)) {
                facilityInfo = facilityInfos.get(0);
            }
        }

        facilityInfoService.updateById(facilityInfo);
        List<FacilityExtVO> facilityExtVOs = facilityInfoExt.getFacilityExtVOList();
        if (null != facilityExtVOs) {
            for (FacilityExtVO facilityExtVO : facilityExtVOs) {
                FacilityExt facilityExt = new FacilityExt();
                BeanUtil.copy(facilityExtVO, facilityExt);
                facilityExtService.updateById(facilityExt);
            }
        }
        //更新mongoDB
//        iPolymerizationClient.addOrUpdateFacility(facilityInfo.getId().toString(), Integer.valueOf(FacilityConstant.FacilityType.TRANSFER_STATION));
        dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_TRANS_STATION_EVENT, user.getTenantId(), facilityInfo.getId()));
        return R.status(true);

    }

    private String getExceptionMsg(String key) {
        String msg = DictBizCache.getValue(FacilityConstant.ExceptionMsg.CODE, key);
        if (StringUtils.isBlank(msg)) {
            msg = key;
        }
        return msg;
    }

    /*
     *校验新增中转站信息
     */
    private void checkAddUnionTranstation(String projectNo, String facilityName, BladeUser user) {
        if (StringUtil.isNotBlank(projectNo)) {
            FacilityInfo info = new FacilityInfo();
            info.setProjectNo(projectNo);
            info.setTenantId(user.getTenantId());
            FacilityInfo facilityInfo = facilityInfoService.getOne(Condition.getQueryWrapper(info), false);
            if (null != facilityInfo) {
                throw new ServiceException(getExceptionMsg(FacilityConstant.ExceptionMsg.KEY_PROJECT_NO_EXIST));
            }
        }
        if (StringUtil.isNotBlank(facilityName)) {
            FacilityInfo info = new FacilityInfo();
            info.setFacilityName(facilityName);
            info.setTenantId(user.getTenantId());
            FacilityInfo facilityInfo = facilityInfoService.getOne(Condition.getQueryWrapper(info), false);
            if (null != facilityInfo) {
                throw new ServiceException(getExceptionMsg(FacilityConstant.ExceptionMsg.KEY_TRANSFER_NAME_EXIST));
            }
        }
    }

    /**
     * 校验修改中转站信息
     */
    private void checkModifyTranstation(Long id, String projectNo, String facilityName, Integer status, BladeUser user) {
        //弃用状态不能修改
        FacilityInfo info = facilityInfoService.getById(id);
        if (null != info && FacilityConstant.TranStationStatus.DROP.equals(Func.toStr(info.getStatus()))) {
            throw new ServiceException(getExceptionMsg(FacilityConstant.ExceptionMsg.KEY_TRANSFER__DROP));
        }
        //已绑定的改为弃用，要解除绑定
        if (FacilityConstant.TranStationStatus.DROP.equals(Func.toStr(status))) {
            R<List<DeviceRel>> relListReturn = deviceRelClient.getEntityRels(id, CommonConstant.ENTITY_TYPE.FACILITY);
            if (ResultCodeConstant.ResponseCode.SUCCESS == relListReturn.getCode()) {
                if (null != relListReturn.getData() && relListReturn.getData().size() > 0)
                    deviceClient.unbindDevice(id, CommonConstant.ENTITY_TYPE.FACILITY);
            } else {
                throw new ServiceException(relListReturn.getMsg());
            }
        }

        //校验修改的工程编码不能重复
        if (StringUtil.isNotBlank(projectNo)) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("project_no", projectNo);
            queryWrapper.ne("id", id);
            queryWrapper.eq("tenant_id", user.getTenantId());
            FacilityInfo facilityInfo = facilityInfoService.getOne(queryWrapper, false);
            if (null != facilityInfo) {
                throw new ServiceException(getExceptionMsg(FacilityConstant.ExceptionMsg.KEY_PROJECT_NO_EXIST));
            }
        }
        //校验修改的中转站名称不能重复
        if (StringUtil.isNotBlank(facilityName)) {
            QueryWrapper nameWrapper = new QueryWrapper();
            nameWrapper.eq("facility_name", facilityName);
            nameWrapper.ne("id", id);
            nameWrapper.eq("tenant_id", user.getTenantId());
            FacilityInfo facilityInfo = facilityInfoService.getOne(nameWrapper, false);
            if (null != facilityInfo) {
                throw new ServiceException(getExceptionMsg(FacilityConstant.ExceptionMsg.KEY_TRANSFER_NAME_EXIST));
            }
        }
        //工作中和故障中、弃用的的不能修改为规划中；
        List<String> statusList = new ArrayList<>();
        statusList.add(FacilityConstant.TranStationStatus.WORKING);
        statusList.add(FacilityConstant.TranStationStatus.STOPPING);
        statusList.add(FacilityConstant.TranStationStatus.DROP);
        FacilityInfo detail = facilityInfoService.getById(id);
        if (null != status && FacilityConstant.TranStationStatus.PLANNING.equals(status.toString())) {
            if (null != detail.getStatus() && statusList.contains(detail.getStatus().toString())) {
                throw new ServiceException(getExceptionMsg(FacilityConstant.ExceptionMsg.KEY_TRANSFER_STATUS_MODIFY));
            }
        }
    }


}
