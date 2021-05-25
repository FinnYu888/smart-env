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
package com.ai.apac.smartenv.system.controller;

import com.ai.apac.smartenv.system.dto.DictDTO;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.service.IDictService;
import com.ai.apac.smartenv.system.vo.DictVO;
import com.ai.apac.smartenv.system.wrapper.DictWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.node.INode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.springblade.core.cache.constant.CacheConstant.DICT_CACHE;


/**
 * 控制器
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dict")
@Api(value = "字典", tags = "字典")
public class DictController extends BladeController {

    private IDictService dictService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入dict")
    @ApiLog
    public R<DictVO> detail(Dict dict) {
        Dict detail = dictService.getOne(Condition.getQueryWrapper(dict));
        return R.data(DictWrapper.build().entityVO(detail));
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "字典编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "dictValue", value = "字典名称", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "列表", notes = "传入dict")
    public R<List<INode>> list(@ApiIgnore @RequestParam Map<String, Object> dict) {
        QueryWrapper<Dict> condition = new QueryWrapper<Dict>();
        if (dict.get("code") != null && StringUtils.isNotBlank(dict.get("code").toString())) {
            condition.eq("code", dict.get("code"));
        }
        if (dict.get("dictValue") != null && StringUtils.isNotBlank(dict.get("dictValue").toString())) {
            condition.eq("dict_value", dict.get("dictValue"));
        }
        if (dict.get("isSealed") != null && StringUtils.isNotBlank(dict.get("isSealed").toString())) {
            condition.eq("is_sealed", dict.get("isSealed"));
        }
        List<Dict> list = dictService.list(condition);
        DictWrapper dictWrapper = new DictWrapper();
        return R.data(dictWrapper.listNodeVO(list));
    }

    /**
     * 顶级列表
     */
    @GetMapping("/parent-list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "字典编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "dictValue", value = "字典名称", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "列表", notes = "传入dict")
    public R<IPage<DictVO>> parentList(@ApiIgnore @RequestParam Map<String, Object> dict, Query query) {
        return R.data(dictService.parentList(dict, query));
    }

    /**
     * 子列表
     */
    @GetMapping("/child-list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "字典编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "dictValue", value = "字典名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "parentId", value = "字典名称", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "列表", notes = "传入dict")
    public R<IPage<DictVO>> childList(@ApiIgnore @RequestParam Map<String, Object> dict, @RequestParam(required = false, defaultValue = "-1") Long parentId, Query query) {
        return R.data(dictService.childList(dict, parentId, query));
    }

    /**
     * 获取字典树形结构
     *
     * @return
     */
    @GetMapping("/tree")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "树形结构", notes = "树形结构")
    public R<List<DictVO>> tree() {
        List<DictVO> tree = dictService.tree();
        return R.data(tree);
    }

    /**
     * 获取字典树形结构
     *
     * @return
     */
    @GetMapping("/parent-tree")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "树形结构", notes = "树形结构")
    public R<List<DictVO>> parentTree() {
        List<DictVO> tree = dictService.parentTree();
        return R.data(tree);
    }

    /**
     * 新增或修改
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入dict")
    @CacheEvict(cacheNames = {DICT_CACHE}, allEntries = true)
    public R submit(@Valid @RequestBody Dict dict) {
        return R.status(dictService.submit(dict));
    }


    /**
     * 删除
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @CacheEvict(cacheNames = {DICT_CACHE}, allEntries = true)
    @ApiOperation(value = "删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(dictService.removeDict(ids));
    }

    /**
     * 获取字典
     *
     * @return
     */
    @GetMapping("/dictionary")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "获取字典", notes = "获取字典")
    public R<List<Dict>> dictionary(String code) {
        List<Dict> tree = dictService.getList(code);
        return R.data(tree);
    }

    /**
     * 获取报表链接
     *
     * @return
     */
    @GetMapping("/viewReport")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "获取报表链接", notes = "获取报表链接")
    public R<String> viewReport(Dict dict, BladeUser user, String entityId) {
        Dict detail = dictService.getOne(Condition.getQueryWrapper(dict));
        StringBuilder url = new StringBuilder("");
        if (detail != null) {
            url.append(detail.getDictValue());
            if (user != null) {
                url.append("&tenantId=").append(user.getTenantId());
            }
            if (StringUtils.isNotBlank(entityId)) {
                url.append("&entityId=").append(entityId);
            }
        }
        return R.data(url.toString());
    }


    //---------------------------------------------------字典管理---------------------------------------------

    @GetMapping("/managementList")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "字典管理页面列表", notes = "字典管理页面列表")
    public R<IPage<DictVO>> managementList(Dict dict, Query query) {

        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotBlank(dict.getCode())) {
            queryWrapper.like("code", dict.getCode());
        }
        if (StringUtil.isNotBlank(dict.getDictValue())) {
            queryWrapper.like("dict_value", dict.getDictValue());
        }
        if (dict.getIsSealed() != null) {
            queryWrapper.eq("is_sealed", dict.getIsSealed());
        }
        queryWrapper.eq("parent_id", 0);
        IPage<Dict> page = dictService.page(Condition.getPage(query), queryWrapper);
        IPage<DictVO> dictVOIPage = DictWrapper.build().pageVO(page);
        dictVOIPage.getRecords().forEach(records -> {
            Dict dict1 = new Dict();
            dict1.setParentId(records.getId());
            int count = dictService.count(Condition.getQueryWrapper(dict1));
            if (records.getIsSealed().intValue()==0){
                records.setStatusName("正常");
            }else {
                records.setStatusName("停用");

            }

            records.setChildrenCount(count);
        });
        return R.data(dictVOIPage);
    }

    @GetMapping("/managementDetail")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "字典管理详情页面", notes = "字典管理详情页面")
    public R<DictVO> managementDetail(String id) {
        Dict dict = new Dict();
        dict.setId(Long.parseLong(id));
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>(dict);
        Dict one = dictService.getOne(queryWrapper);
        if (one==null){
            return R.data(null);
        }
        DictVO dictVO = DictWrapper.build().entityVO(one);
        Dict dict1 = new Dict();
        dict1.setParentId(one.getId());
        List<Dict> list = dictService.list(Condition.getQueryWrapper(dict1));
        List<INode> iNodes = DictWrapper.build().listNodeVO(list);
        dictVO.setChildren(iNodes);
        return R.data(dictVO);
    }

    @PostMapping("/managementModifyDict")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "字典管理字典修改", notes = "字典管理字典修改")
    public R<Boolean> addDict(@RequestBody DictDTO dict) {
        return R.data(dictService.managementModifyDict(dict));
    }



}
