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
package com.ai.apac.smartenv.workarea.controller;

import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.feign.IRegionClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.vo.DictVO;
import com.ai.apac.smartenv.workarea.entity.WorkareaDetail;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.service.IWorkareaNodeService;
import com.ai.apac.smartenv.workarea.service.IWorkareaRelService;
import com.ai.apac.smartenv.workarea.vo.WorkareaInfoRefuelVO;
import com.ai.smartenv.cache.util.SmartCache;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.IdentifierHelper;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tenant.constant.TenantConstant;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.vo.WorkareaInfoVO;
import com.ai.apac.smartenv.workarea.wrapper.WorkareaInfoWrapper;
import com.ai.apac.smartenv.workarea.service.IWorkareaInfoService;
import org.springblade.core.boot.ctrl.BladeController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 工作区域信息 控制器
 *
 * @author Blade
 * @since 2020-01-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/workareainfo")
@Api(value = "工作区域信息", tags = "工作区域信息接口")
public class WorkareaInfoController extends BladeController {
    private IWorkareaRelService workareaRelService;
    private IWorkareaInfoService workareaInfoService;
    private IWorkareaNodeService workareaNodeService;
    private ISysClient sysClient;
    private BaiduMapUtils baiduMapUtils;
    private IPersonClient personClient;
    private IDeviceRelClient deviceRelClient;
    private IRegionClient regionClient;

    private CoordsTypeConvertUtil coordsTypeConvertUtil;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入workareaInfo")
    @ApiLog("查详情")
    public R<WorkareaInfoVO> detail(WorkareaInfo workareaInfo) {
        WorkareaInfo detail = workareaInfoService.getOne(Condition.getQueryWrapper(workareaInfo));
        return R.data(WorkareaInfoWrapper.build().entityVO(detail));
    }

    /**
     * 获取数据字典
     */
    @GetMapping("/listBladeDict")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "获取数据字典", notes = "")
    @ApiLog("查字典数据")
    public R listBladeDict(Dict dict) {
        List<Dict> dicts = DictCache.getList(dict.getCode());
        List<DictVO> dictVOs = new ArrayList<>();
        for (Dict dict1 : dicts) {
            DictVO vo = BeanUtil.copy(dict1, DictVO.class);
            if (dict.getIsSealed() != null && dict.getIsSealed().equals(dict1.getIsSealed())) {
                dictVOs.add(vo);
            }

        };
        return R.data(dictVOs);
    }

    /**
     * 分页 工作区域信息
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入workareaInfo,entityId")
    @ApiLog("分页查询列表信息")
    public R<IPage<WorkareaInfoVO>> list(@ApiParam(value = "关联实体id") String entityId,
                                         WorkareaInfo workareaInfo, Query query, BladeUser user) throws IOException {
        String tanetId = user.getTenantId();
        QueryWrapper<WorkareaInfo> queryWrapper = new QueryWrapper<>();
        if (workareaInfo.getDivision() != null && workareaInfo.getDivision() != 0L) {
            queryWrapper.eq("division", workareaInfo.getDivision());
        }
        if (workareaInfo.getAreaType() != null && workareaInfo.getAreaType() != 0L) {
            queryWrapper.eq("area_type", workareaInfo.getAreaType());
        }
        if (workareaInfo.getBindType() != null && workareaInfo.getBindType() != 0L) {
            queryWrapper.eq("bind_type", workareaInfo.getBindType());
        }
        if (workareaInfo.getWorkAreaType() != null && workareaInfo.getWorkAreaType() != 0L) {
            queryWrapper.eq("work_area_type", workareaInfo.getWorkAreaType());
        }
        if (workareaInfo.getAreaName() != null) {
            queryWrapper.like("area_name", workareaInfo.getAreaName());
        }
        if (tanetId != null) {
            queryWrapper.eq("tenant_id", tanetId);
        }
        queryWrapper.orderByDesc("update_time");
        List<WorkareaInfo> workareaInfoList = workareaInfoService.list(queryWrapper);
        List<WorkareaInfo> workareaInfos = new ArrayList<>();
        if (workareaInfoList != null && workareaInfoList.size() > 0) {
            for (WorkareaInfo info : workareaInfoList) {
                // 查询列表时，实体id不传就查不到，会走到else里面
                List<WorkareaRel> workareaRelList = workareaRelService.list(new QueryWrapper<WorkareaRel>().eq("entity_id", entityId)
                        .eq("workarea_id", info.getId()));
                if (workareaRelList != null && workareaRelList.size() > 0) {

                } else {
                    workareaInfos.add(info);
                }
            }
        }

        int count = workareaInfos.size();
        List<WorkareaInfoVO> workareaInfoVOList = WorkareaInfoWrapper.build().listVO(workareaInfos);
        IPage<WorkareaInfoVO> page = Condition.getPage(query);
        Double pages = Math.ceil((double) page.getTotal() / (double) page.getSize());
        page.setPages(pages.longValue());
        page.setTotal(count);
        int start = ((int) page.getCurrent() - 1) * (int) page.getSize();
        page.setRecords(workareaInfoVOList.subList(start, count - start > page.getSize() ? start + (int) page.getSize() : count));
        for (WorkareaInfoVO record : page.getRecords()) {
            if (record.getAreaHead() != null && record.getAreaHead() != 0L) {
                record.setAreaHeadName(personClient.getPerson(record.getAreaHead()).getData().getPersonName());
            }
            if (record.getDivision()!=null){
                record.setDivisionName(sysClient.getRegion(record.getDivision()).getData().getRegionName());
            }
            if (record.getAreaType() == 1L) {
                record.setWorkAreaName(DictCache.getValue("road_type", String.valueOf(record.getWorkAreaType())));
                record.setRoadLevelName(DictBizCache.getValue(AuthUtil.getTenantId(),"road_level", String.valueOf(record.getAreaLevel())));
            } else if (record.getAreaType() == 2L) {
                record.setWorkAreaName(DictCache.getValue("area_type", String.valueOf(record.getWorkAreaType())));
                record.setAreaLevelName(DictBizCache.getValue(AuthUtil.getTenantId(),"area_level", String.valueOf(record.getAreaLevel())));
            }
            record.setNodes(workareaNodeService.list(new QueryWrapper<WorkareaNode>().eq("workarea_id", record.getId())));
        }

        page.getRecords().forEach(record -> {
            coordsTypeConvertUtil.toWebConvert(record.getNodes());
        });


        return R.data(page);
    }


    /**
     * 分页查询工作区域信息，不带坐标
     */
    @GetMapping("/listPage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入workareaInfo,entityId")
    @ApiLog("分页查询列表信息")
    public R<IPage<WorkareaInfoVO>> listPage(@ApiParam(value = "关联实体id") String entityId,
                                         WorkareaInfo workareaInfo, Query query, BladeUser user) throws IOException {
        String tanetId = user.getTenantId();
        QueryWrapper<WorkareaInfo> queryWrapper = new QueryWrapper<>();
        if (workareaInfo.getDivision() != null && workareaInfo.getDivision() != 0L) {
            queryWrapper.eq("division", workareaInfo.getDivision());
        }
        if (workareaInfo.getAreaType() != null && workareaInfo.getAreaType() != 0L) {
            queryWrapper.eq("area_type", workareaInfo.getAreaType());
        }
        if (workareaInfo.getBindType() != null && workareaInfo.getBindType() != 0L) {
            queryWrapper.eq("bind_type", workareaInfo.getBindType());
        }
        if (workareaInfo.getWorkAreaType() != null && workareaInfo.getWorkAreaType() != 0L) {
            queryWrapper.eq("work_area_type", workareaInfo.getWorkAreaType());
        }
        if (workareaInfo.getAreaName() != null) {
            queryWrapper.like("area_name", workareaInfo.getAreaName());
        }
        if (tanetId != null) {
            queryWrapper.eq("tenant_id", tanetId);
        }
        queryWrapper.orderByDesc("update_time");
        List<WorkareaInfo> workareaInfoList = workareaInfoService.list(queryWrapper);
        List<WorkareaInfo> workareaInfos = new ArrayList<>();
        if (workareaInfoList != null && workareaInfoList.size() > 0) {
            for (WorkareaInfo info : workareaInfoList) {
                // 查询列表时，实体id不传就查不到，会走到else里面
                List<WorkareaRel> workareaRelList = workareaRelService.list(new QueryWrapper<WorkareaRel>().eq("entity_id", entityId)
                        .eq("workarea_id", info.getId()));
                if (workareaRelList != null && workareaRelList.size() > 0) {

                } else {
                    workareaInfos.add(info);
                }
            }
        }

        int count = workareaInfos.size();
        List<WorkareaInfoVO> workareaInfoVOList = WorkareaInfoWrapper.build().listVO(workareaInfos);
        IPage<WorkareaInfoVO> page = Condition.getPage(query);
        Double pages = Math.ceil((double) page.getTotal() / (double) page.getSize());
        page.setPages(pages.longValue());
        page.setTotal(count);
        int start = ((int) page.getCurrent() - 1) * (int) page.getSize();
        page.setRecords(workareaInfoVOList.subList(start, count - start > page.getSize() ? start + (int) page.getSize() : count));
        for (WorkareaInfoVO record : page.getRecords()) {
            if (record.getAreaHead() != null && record.getAreaHead() != 0L) {
                record.setAreaHeadName(personClient.getPerson(record.getAreaHead()).getData().getPersonName());
            }
            if (record.getDivision()!=null){
                record.setDivisionName(sysClient.getRegion(record.getDivision()).getData().getRegionName());
            }
            if (record.getAreaType() == 1L) {
                record.setWorkAreaName(DictCache.getValue("road_type", String.valueOf(record.getWorkAreaType())));
                record.setRoadLevelName(DictBizCache.getValue(AuthUtil.getTenantId(),"road_level", String.valueOf(record.getAreaLevel())));
            } else if (record.getAreaType() == 2L) {
                record.setWorkAreaName(DictCache.getValue("area_type", String.valueOf(record.getWorkAreaType())));
                record.setAreaLevelName(DictBizCache.getValue(AuthUtil.getTenantId(),"area_level", String.valueOf(record.getAreaLevel())));
            }
        }


        return R.data(page);
    }




    /**
     * 事件上报页面查询工作区域信息
     */
    @GetMapping("/areaListByName")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "联想查询区域", notes = "传入name")
    @ApiLog("根据名称模糊查询列表信息")
    public R<List<WorkareaInfoVO>> areaListByName(@ApiParam(value = "区域名称name") String name, @ApiParam(value = "区域类型") @RequestParam int bindType, @ApiParam(value = "所属区域belongArea") String belongArea, BladeUser user) {
        String tanetId = user.getTenantId();
        QueryWrapper<WorkareaInfo> queryWrapper = new QueryWrapper<>();

        if (name != null) {
            queryWrapper.like("area_name", name);
        }
        if (tanetId != null) {
            queryWrapper.eq("tenant_id", tanetId);
        }
        if (belongArea != null) {
            queryWrapper.eq("region_id", Func.toLong(belongArea));
        }
        if (bindType != 0) {
            queryWrapper.eq("bind_type", bindType);
        }
        queryWrapper.orderByDesc("update_time");
        List<WorkareaInfo> workareaInfoList = workareaInfoService.list(queryWrapper);


        List<WorkareaInfoVO> workareaInfoVOList = WorkareaInfoWrapper.build().listVO(workareaInfoList);
        for (WorkareaInfoVO record : workareaInfoVOList) {

            record.setDivisionName(sysClient.getRegion(record.getDivision()).getData().getRegionName());
            if (record.getAreaType() == 1L) {

                record.setWorkAreaName(DictCache.getValue("road_type", String.valueOf(record.getWorkAreaType())));
                record.setRoadLevelName(DictBizCache.getValue(AuthUtil.getTenantId(),"road_level", String.valueOf(record.getAreaLevel())));
            } else if (record.getAreaType() == 2L) {

                record.setWorkAreaName(DictCache.getValue("area_type", String.valueOf(record.getWorkAreaType())));
                record.setAreaLevelName(DictBizCache.getValue(AuthUtil.getTenantId(),"area_level", String.valueOf(record.getAreaLevel())));
            }

        }
        return R.data(workareaInfoVOList);
    }

    /**
     * 垃圾桶所属路线区域
     */
    @GetMapping("/listAreaForAshcan")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "垃圾桶所属路线区域", notes = "传入name")
    @ApiLog("根据名称模糊查询列表信息")
    public R<List<WorkareaInfoVO>> listAreaForAshcan(@ApiParam(value = "区域名称") String name,
                                                     @ApiParam(value = "所属区域") String regionId, BladeUser user) {
        String tanetId = user.getTenantId();
        QueryWrapper<WorkareaInfo> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like("area_name", name);
        }
        if (tanetId != null) {
            queryWrapper.eq("tenant_id", tanetId);
        }
        if (StringUtils.isNotBlank(regionId)) {
            queryWrapper.eq("region_id", Func.toLong(regionId));
        }
        List<Integer> keys = new ArrayList<>();
        keys.add(1); // 人员工作路线或区域
        keys.add(8);  // 车辆工作路线或区域
        queryWrapper.in("work_area_type", keys);
        queryWrapper.orderByAsc("area_name");
        List<WorkareaInfo> workareaInfoList = workareaInfoService.list(queryWrapper);
        List<WorkareaInfoVO> workareaInfoVOList = WorkareaInfoWrapper.build().listVO(workareaInfoList);
        return R.data(workareaInfoVOList);
    }


    /**
     * 自定义分页 工作区域信息
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "分页", notes = "传入workareaInfo")
    @ApiLog("分页查询")
    public R<IPage<WorkareaInfoVO>> page(WorkareaInfoVO workareaInfo, Query query) {
        IPage<WorkareaInfoVO> pages = workareaInfoService.selectWorkareaInfoPage(Condition.getPage(query), workareaInfo);
        return R.data(pages);
    }

    /**
     * 新增 工作区域信息
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入workareaInfo")
    @ApiLog("默认保存方法")
    public R save(@Valid @RequestBody WorkareaInfo workareaInfo) {
        return R.status(workareaInfoService.save(workareaInfo));
    }

    /**
     * 修改 工作区域信息
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入workareaInfo")
    @ApiLog("默认更新方法")
    public R update(@Valid @RequestBody WorkareaInfo workareaInfo) {
        return R.status(workareaInfoService.updateById(workareaInfo));
    }

    /**
     * 新增或修改 工作区域信息
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入workareaInfo")
    @ApiLog("默认submit方法")
    public R submit(@Valid @RequestBody WorkareaInfo workareaInfo, List<WorkareaNode> nodes) {
        return R.status(workareaInfoService.saveOrUpdate(workareaInfo));
    }

    @PostMapping("/saveDetail")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "全量新增工作区域", notes = "传入工作区域")
    @ApiLog("全量新增工作区域")
    public R saveDetail(@RequestBody WorkareaDetail workareaDetail) throws ServiceException {
        return R.status(workareaInfoService.saveOrUpdateDetail(workareaDetail));
    }

    @PostMapping("/workAreaInfo2BigData")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "工作区域全量同步给大数据，当大数据与环卫因不可控因素导致数据不一致时调用", notes = "当大数据与环卫因不可控因素导致数据不一致时调用")
    @ApiLog("工作区域全量同步给大数据")
    public R workAreaInfo2BigData() throws ServiceException, IOException {
        // 按租户同步区域信息给大数据
        List<Tenant> allTenant = TenantCache.getAllTenant();
        for (Tenant tenant : allTenant) {
            workareaInfoService.workAreaInfo2BigData(tenant.getTenantId());
        }
        return R.status(true);
    }


    /**
     * 删除 工作区域信息
     * 改造：工作区域基本信息删除，对应的关系、nodes也需要删除
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    @ApiLog("默认删除方法")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) throws ServiceException {
        return R.status(workareaInfoService.removeAllInfo(Func.toLongList(ids)));
    }


    /**
     * 根据租户信息查询加油所属区域
     */
    @GetMapping("/listAreaForRefuel")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "加油所属区域", notes = "传入tenantId")
    @ApiLog("根据租户信息查询加油所属区域")
    public R<List<WorkareaInfoRefuelVO>> listAreaForAshcan(String tenantId) {
        if (StringUtil.isBlank(tenantId)) {
            BladeUser user = AuthUtil.getUser();
            if (user != null) {
                tenantId = user.getTenantId();
            }
        }
        QueryWrapper<WorkareaInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", tenantId);
        queryWrapper.eq("work_area_type", 2);
        queryWrapper.eq("area_type", 2);
        queryWrapper.orderByAsc("area_name");
        List<WorkareaInfo> workareaInfoList = workareaInfoService.list(queryWrapper);
//		List<WorkareaInfoRefuelVO> arealist = new ArrayList<>();
        List<WorkareaInfoRefuelVO> arealist = BeanUtil.copyProperties(workareaInfoList, WorkareaInfoRefuelVO.class);
//		BeanUtils.copyProperties(workareaInfoList,arealist);
        return R.data(arealist);
    }


    @PostMapping("/addWorkareaInfoByTrack")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "根据历史轨迹添加工作区域", notes = "传入历史轨迹查询条件")
    @ApiLog("根据历史轨迹添加工作区域")
    public R<Boolean> addWorkareaInfoByTrack(@RequestBody WorkareaDetail workareaDetail) throws Exception {

        workareaDetail.getWorkareaInfo().setTenantId(getUser().getTenantId());

        Boolean resu = workareaInfoService.addWorkareaInfoByTrack(workareaDetail.getWorkareaInfo(), workareaDetail.getEntityType(),
                workareaDetail.getEntityId(), workareaDetail.getBeginTime(), workareaDetail.getEndTime()
        );
        return R.data(resu);
    }


    @PostMapping("/realTimeWorkingArea")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "查询车辆或者人员的实时作业面积", notes = "查询车辆或者人员的实时作业面积")
    @ApiLog("查询车辆或者人员的实时作业面积")
    public R<String> getRealTimeWorkingArea(@ApiParam(value = "实体ID", required = true) @RequestParam Long entityId,
                                            @ApiParam(value = "实体类型", required = true) @RequestParam Long entityType) throws Exception {

        List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(entityId,entityType).getData();

        if(ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0){

        }

        return R.data(null);

    }

    @PostMapping("/regionNode2areaNode")
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "将业务区域坐标导入工作区域呢", notes = "将业务区域坐标导入工作区域呢")
    @ApiLog("将业务区域坐标导入工作区域呢")
    public R<Boolean> addWorkareaInfoByTrack(@ApiParam(value = "业务区域ID", required = true) @RequestParam String regionid,
                                             @ApiParam(value = "工作区域ID", required = true) @RequestParam String areaId) throws Exception {
        QueryWrapper<WorkareaNode> wrapper = new QueryWrapper<WorkareaNode>();
        wrapper.lambda().eq(WorkareaNode::getRegionId,regionid);
        List<WorkareaNode> workareaNodeList = workareaNodeService.list(wrapper);
        workareaNodeList.forEach(workareaNode -> {
            WorkareaNode node = new WorkareaNode();
            node.setLatitudinal(workareaNode.getLatitudinal());
            node.setLongitude(workareaNode.getLongitude());
            node.setWorkareaId(Long.parseLong(areaId));
            workareaNodeService.save(node);
        });
        return R.data(true);
    }


    /**
     * 批量变更作业区域所属的业务区域
     */
    @PostMapping("/batchChangeRegion4WorkArea")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "批量变更作业区域所属的业务区域", notes = "传入targetRegionId，areaIds")
    @ApiLog("批量变更作业区域所属的业务区域")
    public R batchChangeRegion4WorkArea(@RequestParam List<Long> areaIds, Long targetRegionId) {
        Region region = regionClient.getRegionById(targetRegionId).getData();
        return R.status(workareaInfoService.batchChangeRegion4WorkArea(areaIds,targetRegionId,region.getRegionManager()));
    }





}
