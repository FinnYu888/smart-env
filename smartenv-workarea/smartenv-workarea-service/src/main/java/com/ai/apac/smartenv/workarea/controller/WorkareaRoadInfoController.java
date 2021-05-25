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

import com.ai.apac.smartenv.workarea.dto.SimpleWorkAreaInfoDTO;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.event.SyncReadListener;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.workarea.entity.WorkareaRoadInfo;
import com.ai.apac.smartenv.workarea.vo.WorkareaRoadInfoVO;
import com.ai.apac.smartenv.workarea.wrapper.WorkareaRoadInfoWrapper;
import com.ai.apac.smartenv.workarea.service.IWorkareaRoadInfoService;
import org.springblade.core.boot.ctrl.BladeController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 控制器
 *
 * @author Blade
 * @since 2021-01-08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/workarearoadinfo")
@Api(value = "工作区域道路信息", tags = "工作区域道路信息")
@Slf4j
public class WorkareaRoadInfoController extends BladeController {

    private IWorkareaRoadInfoService workareaRoadInfoService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入workareaRoadInfo")
    public R<WorkareaRoadInfoVO> detail(WorkareaRoadInfo workareaRoadInfo) {
        WorkareaRoadInfo detail = workareaRoadInfoService.getOne(Condition.getQueryWrapper(workareaRoadInfo));
        return R.data(WorkareaRoadInfoWrapper.build().entityVO(detail));
    }

    /**
     * 分页
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入workareaRoadInfo")
    public R<IPage<WorkareaRoadInfoVO>> list(WorkareaRoadInfo workareaRoadInfo, Query query) {
        IPage<WorkareaRoadInfo> pages = workareaRoadInfoService.page(Condition.getPage(query), Condition.getQueryWrapper(workareaRoadInfo));
        return R.data(WorkareaRoadInfoWrapper.build().pageVO(pages));
    }


    /**
     * 自定义分页
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "分页", notes = "传入workareaRoadInfo")
    public R<IPage<WorkareaRoadInfoVO>> page(WorkareaRoadInfoVO workareaRoadInfo, Query query) {
        IPage<WorkareaRoadInfoVO> pages = workareaRoadInfoService.selectWorkareaRoadInfoPage(Condition.getPage(query), workareaRoadInfo);
        return R.data(pages);
    }

    /**
     * 新增
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入workareaRoadInfo")
    public R save(@Valid @RequestBody WorkareaRoadInfo workareaRoadInfo) {
        return R.status(workareaRoadInfoService.save(workareaRoadInfo));
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入workareaRoadInfo")
    public R update(@Valid @RequestBody WorkareaRoadInfo workareaRoadInfo) {
        return R.status(workareaRoadInfoService.updateById(workareaRoadInfo));
    }

    /**
     * 新增或修改
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入workareaRoadInfo")
    public R submit(@Valid @RequestBody WorkareaRoadInfo workareaRoadInfo) {
        return R.status(workareaRoadInfoService.saveOrUpdate(workareaRoadInfo));
    }


    /**
     * 删除
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(workareaRoadInfoService.deleteLogic(Func.toLongList(ids)));
    }

    @PostMapping("/importWorkRoadInfo")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "导入工作区域道路信息", notes = "传入文件")
    public R importWorkRoadInfo(@RequestParam("file") MultipartFile excel, SimpleWorkAreaInfoDTO simpleWorkAreaInfoDTO) {
        BufferedInputStream inputStream;
        try {
            inputStream = new BufferedInputStream(excel.getInputStream());
//			List<Object> objects = EasyExcelFactory.read(inputStream).sheet(1).doReadSync();
            SyncReadListener listener = new SyncReadListener();
            ExcelReader excelReader = EasyExcel.read(inputStream, listener).build();
            List<ReadSheet> readSheets = excelReader.excelExecutor().sheetList();
            Map<String, ReadSheet> collect = readSheets.stream().collect(Collectors.toMap(ReadSheet::getSheetName, readSheet -> readSheet));
            ReadSheet readSheet1 = collect.get("1");
            ReadSheet readSheet2 = collect.get("2");
            ReadSheet readSheet3 = collect.get("3");
            ReadSheet readSheet4 = collect.get("4");

            excelReader.read(readSheet1);
            List<Object> list1 = listener.getList();
            log.info("");
            listener.setList(new ArrayList<>());
            excelReader.read(readSheet2);
            List<Object> list2 = listener.getList();
            log.info("");
            listener.setList(new ArrayList<>());
            excelReader.read(readSheet3);
            List<Object> list3 = listener.getList();
            log.info("");
            listener.setList(new ArrayList<>());
            excelReader.read(readSheet4);

            List<Object> list4 = listener.getList();
            List<WorkareaRoadInfo> workareaRoadInfos1 = convertToWorkareaRoadInfoGreeenArea(list1, 1);
            List<WorkareaRoadInfo> workareaRoadInfos2 = convertToWorkareaRoadInfoGreeenArea(list2, 2);
            List<WorkareaRoadInfo> workareaRoadInfos3 = convertToWorkareaRoadInfoGreeenArea(list3, 3);
            List<WorkareaRoadInfo> workareaRoadInfos4 = convertToWorkareaRoadInfoGreeenStrip(list4, 4);

            List<WorkareaRoadInfo> allWorkRoadInfoList = new ArrayList<WorkareaRoadInfo>();
            allWorkRoadInfoList.addAll(workareaRoadInfos1);
            allWorkRoadInfoList.addAll(workareaRoadInfos2);
            allWorkRoadInfoList.addAll(workareaRoadInfos3);
            allWorkRoadInfoList.addAll(workareaRoadInfos4);
            workareaRoadInfoService.saveWorkAreaRoadInfo(simpleWorkAreaInfoDTO, allWorkRoadInfoList);

//            workareaRoadInfoService.saveBatch(workareaRoadInfos1);
//            workareaRoadInfoService.saveBatch(workareaRoadInfos2);
//            workareaRoadInfoService.saveBatch(workareaRoadInfos3);
//            workareaRoadInfoService.saveBatch(workareaRoadInfos4);
            log.info("");
        } catch (Exception e) {
            log.error("导入失败", e);
        }

        return R.data("导入成功");
    }

    public List<WorkareaRoadInfo> convertToWorkareaRoadInfoGreeenArea(List<Object> list, Integer roadLevel) {
        List<WorkareaRoadInfo> collect = list.stream().map(o -> (Map<Integer, String>) o).map(stringObjectMap -> {
            WorkareaRoadInfo workareaRoadInfo = new WorkareaRoadInfo();
            workareaRoadInfo.setStartAndEnd(stringObjectMap.get(1) == null ? stringObjectMap.get(0) : stringObjectMap.get(1));
            workareaRoadInfo.setMotorwayLength(stringObjectMap.get(2));
            workareaRoadInfo.setMotorwayWight(stringObjectMap.get(3));
            workareaRoadInfo.setNonMotorizedLength(stringObjectMap.get(4));
            workareaRoadInfo.setNonMotorizedWeight(stringObjectMap.get(5));
            workareaRoadInfo.setSidewalkLength(stringObjectMap.get(6));
            workareaRoadInfo.setSidewalkWight(stringObjectMap.get(7));
            workareaRoadInfo.setFrontRoadLength(stringObjectMap.get(8));
            workareaRoadInfo.setFrontRoadWeigth(stringObjectMap.get(9));
            workareaRoadInfo.setGreenbeltLength(stringObjectMap.get(10));
            workareaRoadInfo.setGreenbeltWeight(stringObjectMap.get(11));
            workareaRoadInfo.setFenceLength(stringObjectMap.get(12));
            workareaRoadInfo.setArea(stringObjectMap.get(13));
            workareaRoadInfo.setWorkArea(stringObjectMap.get(14));
            workareaRoadInfo.setRoadLevel(roadLevel);
            return workareaRoadInfo;
        }).collect(Collectors.toList());
        return collect;
    }

    public List<WorkareaRoadInfo> convertToWorkareaRoadInfoGreeenStrip(List<Object> list, Integer roadLevel) {
        List<WorkareaRoadInfo> collect = list.stream().map(o -> (Map<Integer, String>) o).map(stringObjectMap -> {
            WorkareaRoadInfo workareaRoadInfo = new WorkareaRoadInfo();
            workareaRoadInfo.setStartAndEnd(stringObjectMap.get(1) == null ? stringObjectMap.get(0) : stringObjectMap.get(1));
            workareaRoadInfo.setMotorwayLength(stringObjectMap.get(2));
            workareaRoadInfo.setMotorwayWight(stringObjectMap.get(3));
            workareaRoadInfo.setNonMotorizedLength(stringObjectMap.get(4));
            workareaRoadInfo.setNonMotorizedWeight(stringObjectMap.get(5));
            workareaRoadInfo.setSidewalkLength(stringObjectMap.get(6));
            workareaRoadInfo.setSidewalkWight(stringObjectMap.get(7));
            workareaRoadInfo.setFrontRoadLength(stringObjectMap.get(8));
            workareaRoadInfo.setFrontRoadWeigth(stringObjectMap.get(9));
            workareaRoadInfo.setGreenbeltArea(stringObjectMap.get(10));
            workareaRoadInfo.setFenceLength(stringObjectMap.get(11));
            workareaRoadInfo.setArea(stringObjectMap.get(12));
            workareaRoadInfo.setWorkArea(stringObjectMap.get(13));
            workareaRoadInfo.setRoadLevel(roadLevel);
            return workareaRoadInfo;
        }).collect(Collectors.toList());
        return collect;
    }


}
