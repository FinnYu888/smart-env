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

import com.ai.apac.smartenv.common.constant.DbEventConstant;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.ai.apac.smartenv.omnic.feign.IPolymerizationClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.ai.apac.smartenv.common.constant.FacilityConstant;
import com.ai.apac.smartenv.common.utils.ZXingCodeUtil;
import com.ai.apac.smartenv.facility.cache.AshcanCache;
import com.ai.apac.smartenv.facility.dto.AshcanImportResultModel;
import com.ai.apac.smartenv.facility.entity.AshcanInfo;
import com.ai.apac.smartenv.facility.vo.AshcanImportResultVO;
import com.ai.apac.smartenv.facility.vo.AshcanInfoVO;
import com.ai.apac.smartenv.facility.wrapper.AshcanInfoWrapper;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.ai.apac.smartenv.facility.service.IAshcanInfoService;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;

/**
 * 控制器
 *
 * @author Blade
 * @since 2020-07-20
 */
@RestController
@AllArgsConstructor
@RequestMapping("ashcaninfo")
@Api(value = "", tags = "垃圾桶接口")
public class AshcanInfoController extends BladeController {

    private IAshcanInfoService ashcanInfoService;
    private IWorkareaClient workareaClient;
    private ISysClient sysClient;
    private IOssClient ossClient;
    private IDeviceClient deviceClient;
    private BaiduMapUtils baiduMapUtils;
    private IPolymerizationClient iPolymerizationClient;
    private IDataChangeEventClient dataChangeEventClient;

    /**
     * 详情
     *
     * @throws IOException
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入ashcanInfo")
    @ApiLog(value = "查询垃圾桶详情")
    public R<AshcanInfoVO> detail(AshcanInfo ashcanInfo) throws IOException {
        AshcanInfo detail = AshcanCache.getAshcanById(ashcanInfo.getId());
        AshcanInfoVO ashcanInfoVO = AshcanInfoWrapper.build().entityVO(detail);
        ashcanInfoVO = ashcanInfoService.getAshcanAllInfoByVO(ashcanInfoVO);
        return R.data(ashcanInfoVO);
    }

    /**
     * 分页
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入ashcanInfo")
    @ApiLog(value = "查询垃圾桶列表")
    public R<IPage<AshcanInfoVO>> list(AshcanInfo ashcanInfo, Query query) {
        IPage<AshcanInfo> pages = ashcanInfoService.page(ashcanInfo, query);
        IPage<AshcanInfoVO> pageVO = AshcanInfoWrapper.build().pageVO(pages);
        List<AshcanInfoVO> records = pageVO.getRecords();
        records.forEach(record -> {
            record = ashcanInfoService.getAshcanAllInfoByVO(record);
        });
        return R.data(pageVO);
    }

    /**
     * 新增
     *
     * @throws IOException
     */
    @PostMapping("")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入ashcanInfo")
    @ApiLog(value = "新增垃圾桶")
    public R save(@RequestBody AshcanInfoVO ashcanInfoVO, BladeUser bladeUser) throws IOException {
        ashcanInfoVO.setTenantId(bladeUser.getTenantId());
        Boolean result = ashcanInfoService.saveAshcanInfo(ashcanInfoVO);
        //更新mongoDB
//        iPolymerizationClient.addOrUpdateFacility(ashcanInfoVO.getId().toString(), Integer.valueOf(FacilityConstant.FacilityType.ASHCAN));
        dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_TRASH_INFO_EVENT, bladeUser.getTenantId(), ashcanInfoVO.getId()));
        return R.status(result);
    }

    /**
     * 修改
     */
    @PutMapping("")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入ashcanInfo")
    @ApiLog(value = "修改垃圾桶")
    public R update(@RequestBody AshcanInfoVO ashcanInfo, BladeUser bladeUser) {
        Boolean result = ashcanInfoService.updateAshcan(ashcanInfo);
        //更新mongoDB
//        iPolymerizationClient.addOrUpdateFacility(ashcanInfo.getId().toString(), Integer.valueOf(FacilityConstant.FacilityType.ASHCAN));
        dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_TRASH_INFO_EVENT, bladeUser.getTenantId(), ashcanInfo.getId()));
        return R.status(result);
    }

    /**
     * 删除
     */
    @DeleteMapping("")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    @ApiLog(value = "删除垃圾桶")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids, BladeUser bladeUser) {
        Boolean result = ashcanInfoService.removeAshcan(Func.toLongList(ids));
        //更新mongoDB
//        iPolymerizationClient.removeFacilityList(ids);
        dataChangeEventClient.doDbEvent(new BaseDbEventDTO<String>(DbEventConstant.EventType.REMOVE_TRASH_EVENT, bladeUser.getTenantId(), ids));
        return R.status(result);
    }

    @PostMapping("/importAshcan")
    @ApiOperationSupport(order = 9)
    @ApiLog(value = "导入垃圾桶信息")
    @ApiOperation(value = "导入excel", notes = "传入excel")
    public R<AshcanImportResultVO> importAshcan(@RequestParam("file") MultipartFile excel, BladeUser bladeUser) throws Exception {
        String tenantId = bladeUser.getTenantId();
        AshcanImportResultVO result = new AshcanImportResultVO();
        int successCount = 0;
        int failCount = 0;
        List<AshcanImportResultModel> failRecords = new ArrayList<>();
        List<AshcanImportResultModel> allRecords = new ArrayList<>();
        InputStream inputStream = null;
        AshcanImportResultModel currentModel = new AshcanImportResultModel();
        try {
            inputStream = new BufferedInputStream(excel.getInputStream());
            List<Object> datas = EasyExcelFactory.read(inputStream, new Sheet(1, 1));
            if (datas == null || datas.isEmpty()) {
                throw new ServiceException("没有数据");
            }
            HashMap<String, Long> workareaMap = getWorkareaMap(tenantId);
            for (Object object : datas) {
                try {
                    // 获取每行数据
                    List<String> params = new ArrayList<>();
                    for (Object o : (List<?>) object) {
                        params.add(String.class.cast(o));
                    }
                    // 导入结果对象
                    currentModel = new AshcanImportResultModel();
                    if (params.size() > FacilityConstant.ExcelImportIndex.CODE) {
                        currentModel.setAshcanCode(params.get(FacilityConstant.ExcelImportIndex.CODE));
                    }
                    if (params.size() > FacilityConstant.ExcelImportIndex.TYPE) {
                        currentModel.setAshcanType(params.get(FacilityConstant.ExcelImportIndex.TYPE));
                    }
                    if (params.size() > FacilityConstant.ExcelImportIndex.CAPACITY) {
                        currentModel.setCapacity(params.get(FacilityConstant.ExcelImportIndex.CAPACITY));
                    }
                    if (params.size() > FacilityConstant.ExcelImportIndex.SUPPORT_DEVICE) {
                        currentModel.setSupportDevice(params.get(FacilityConstant.ExcelImportIndex.SUPPORT_DEVICE));
                    }
                    if (params.size() > FacilityConstant.ExcelImportIndex.WORKAREA) {
                        currentModel.setWorkarea(params.get(FacilityConstant.ExcelImportIndex.WORKAREA));
                    }
                    if (params.size() > FacilityConstant.ExcelImportIndex.ADDRESS) {
                        currentModel.setAddress(params.get(FacilityConstant.ExcelImportIndex.ADDRESS));
                    }
                    if (params.size() > FacilityConstant.ExcelImportIndex.DEPT) {
                        currentModel.setDept(params.get(FacilityConstant.ExcelImportIndex.DEPT));
                    }
                    // 校验数据
                    verifyParamForImport(currentModel, workareaMap);
                    // 保存
                    Map<String, Double> address = baiduMapUtils.getLatAndLngByAddress(currentModel.getAddress());
                    Double lng = address.get("lng");
                    Double lat = address.get("lat");
                    AshcanInfoVO ashcanInfoVO = new AshcanInfoVO();
                    ashcanInfoVO.setAshcanCode(currentModel.getAshcanCode());
                    ashcanInfoVO.setAshcanType(currentModel.getAshcanType());
                    ashcanInfoVO.setCapacity(Long.parseLong(currentModel.getCapacity()));
                    ashcanInfoVO.setSupportDevice(currentModel.getSupportDevice());
                    ashcanInfoVO.setWorkareaId(workareaMap.get(currentModel.getWorkarea()));
                    ashcanInfoVO.setLocation(currentModel.getAddress());
                    ashcanInfoVO.setLng(String.valueOf(lng));
                    ashcanInfoVO.setLat(String.valueOf(lat));
                    if (StringUtils.isNotBlank(currentModel.getDept())) {
                        ashcanInfoVO.setDeptId(Long.valueOf(currentModel.getDept()));
                    }

                    ashcanInfoService.saveAshcanInfo(ashcanInfoVO);
                    // 保存成功
                    successCount++;
                    currentModel.setStatus("成功");
                    allRecords.add(currentModel);
                } catch (Exception e) {
                    failCount++;
                    currentModel.setStatus("失败");
                    currentModel.setReason(e.getMessage());
                    failRecords.add(currentModel);
                    allRecords.add(currentModel);
                }
            }
        } catch (Exception e) {
//            logger.error("Excel操作异常" + e.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            result.setSuccessCount(successCount);
            result.setFailCount(failCount);
            result.setFailRecords(failRecords);

            if (failCount > 0) {
                AshcanCache.saveImportRecords(allRecords);
            }
        }
        return R.data(result);
    }

    /*
     * 获取该租户所有区域
     */
    private HashMap<String, Long> getWorkareaMap(String tenantId) {
        List<WorkareaInfo> workareaInfoList = workareaClient.getWorkareaInfoByTenantId(tenantId).getData();
        HashMap<String, Long> workareaMap = new HashMap<>();
        if (workareaInfoList != null) {
            for (WorkareaInfo workareaInfo : workareaInfoList) {
                workareaMap.put(workareaInfo.getAreaName(), workareaInfo.getId());
            }
        }
        return workareaMap;
    }

    private void verifyParamForImport(AshcanImportResultModel currentModel, HashMap<String, Long> stationMap) {
        // 垃圾桶编码
        if (StringUtils.isBlank(currentModel.getAshcanCode())) {
            throw new ServiceException("需要输入垃圾桶编码");
        }
        // 垃圾桶类型
        if (StringUtils.isBlank(currentModel.getAshcanType())) {
            throw new ServiceException("需要输入垃圾桶类型");
        } else {
            try {
                Integer.parseInt(currentModel.getAshcanType());
            } catch (Exception e) {
                throw new ServiceException("垃圾桶类型格式不正确");
            }
            String supportDevice = DictCache.getValue(FacilityConstant.DictCode.ASHCAN_TYPE, currentModel.getAshcanType());
            if (supportDevice == null || "".equals(supportDevice)) {
                throw new ServiceException("垃圾桶类型格式不正确");
            }
        }
        // 垃圾桶大小
        if (StringUtils.isBlank(currentModel.getCapacity())) {
            throw new ServiceException("需要输入垃圾桶大小");
        } else {
            try {
                Long.parseLong(currentModel.getCapacity());
            } catch (Exception e) {
                throw new ServiceException("垃圾桶大小格式不正确");
            }
            String capacity = DictCache.getValue(FacilityConstant.DictCode.ASHCAN_CAPACITY, currentModel.getCapacity());
            if (capacity == null || "".equals(capacity)) {
                throw new ServiceException("垃圾桶大小格式不正确");
            }
        }
        // 是否支持终端
        if (StringUtils.isBlank(currentModel.getSupportDevice())) {
            throw new ServiceException("需要输入是否支持终端");
        } else {
            try {
                Integer.parseInt(currentModel.getSupportDevice());
            } catch (Exception e) {
                throw new ServiceException("是否支持终端格式不正确");
            }
            String supportDevice = DictCache.getValue(FacilityConstant.DictCode.YES_NO, currentModel.getSupportDevice());
            if (supportDevice == null || "".equals(supportDevice)) {
                throw new ServiceException("是否支持终端格式不正确");
            }
        }
        // 规划路线/区域
        if (StringUtils.isBlank(currentModel.getWorkarea())) {
            throw new ServiceException("需要输入规划路线/区域");
        } else {
            Long workareaId = stationMap.get(currentModel.getWorkarea());
            if (workareaId == null || workareaId <= 0) {
                throw new ServiceException("规划路线/区域输入错误");
            }
        }
    }

    @GetMapping("/importAshcanModel")
    @ApiOperationSupport(order = 9)
    @ApiLog(value = "导入垃圾桶模板下载")
    @ApiOperation(value = "导入模板下载", notes = "")
    public R importAshcanModel() throws Exception {
        String name = DictCache.getValue(FacilityConstant.DICT_IMPORT_EXCEL_MODEL, FacilityConstant.DICT_IMPORT_EXCEL_MODEL_ASHCAN);
        String link = ossClient.getObjectLink(FacilityConstant.OSS_BUCKET_NAME, name).getData();
        return R.data(link);
    }

    @GetMapping("/importResultExcel")
    @ApiOperationSupport(order = 9)
    @ApiLog(value = "导入结果下载")
    @ApiOperation(value = "导入结果下载", notes = "")
    public void getImportResultExcel(String key) throws Exception {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        Object object = AshcanCache.getImportRecords(key);
        List<AshcanImportResultModel> modelList = new ArrayList<>();
        for (Object o : (List<?>) object) {
            AshcanImportResultModel model = BeanUtil.copy(o, AshcanImportResultModel.class);
            modelList.add(model);
        }
        OutputStream out = null;
        try {
            response.reset(); // 清除buffer缓存
            String fileName = "垃圾桶信息导入结果";
            out = response.getOutputStream();
            response.setContentType("application/x-msdownload;charset=utf-8");
            response.setHeader("Content-disposition", "attachment;filename= " + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            Sheet sheet1 = new Sheet(1, 0, AshcanImportResultModel.class);
            sheet1.setSheetName("sheet1");
            writer.write(modelList, sheet1);
            writer.finish();
        } catch (IOException e) {
            throw new ServiceException("没有记录");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @GetMapping("/downloadAllQrCode")
    @ApiOperationSupport(order = 9)
    @ApiLog(value = "批量下载二维码")
    @ApiOperation(value = "批量下载二维码", notes = "")
    public void downloadAllQrCode(AshcanInfoVO ashcanInfo) throws Exception {
        List<AshcanInfo> ashcanInfoList = ashcanInfoService.listByAshcanInfo(ashcanInfo);
        if (ashcanInfoList == null || ashcanInfoList.isEmpty()) {
            throw new ServiceException("没有记录");
        }
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
        BufferedOutputStream bos = new BufferedOutputStream(zos);
        try {
            response.reset();
            String zipName = java.net.URLEncoder.encode("垃圾桶二维码批量", "UTF-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + zipName + ".zip");

            for (AshcanInfo ashcan : ashcanInfoList) {
                String base64 = ashcanInfoService.createQrCode(ashcan);
                BufferedImage bim = ZXingCodeUtil.base64StringToImage(base64);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(bim, "png", stream);
                byte[] file = stream.toByteArray();
                BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(file));
                zos.putNextEntry(new ZipEntry(ashcan.getAshcanCode() + ".png"));
                int len = 0;
                byte[] buf = new byte[10 * 1024];
                while ((len = bis.read(buf, 0, buf.length)) != -1) {
                    bos.write(buf, 0, len);
                }
                bis.close();
                bos.flush();
            }
        } catch (Exception e) {
            throw new ServiceException("没有记录");
        } finally {
            if (bos != null) {
                bos.close();
            }
        }
    }

    @GetMapping("/downloadOneQrCode")
    @ApiOperationSupport(order = 9)
    @ApiLog(value = "单个下载二维码")
    @ApiOperation(value = "单个下载二维码", notes = "")
    public void downloadOneQrCode(AshcanInfoVO ashcanInfo) throws Exception {
        AshcanInfo detail = AshcanCache.getAshcanById(ashcanInfo.getId());
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        OutputStream out = response.getOutputStream();
        try {
            response.reset();
            String fileName = java.net.URLEncoder.encode(detail.getAshcanCode(), "UTF-8");
            response.setContentType("image/jpeg");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".png");
            String base64 = ashcanInfoService.createQrCode(detail);
            BufferedImage bim = ZXingCodeUtil.base64StringToImage(base64);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(bim, "png", stream);
            byte[] file = stream.toByteArray();
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(file));
            int len = 0;
            byte[] buf = new byte[10 * 1024];
            while ((len = bis.read(buf, 0, buf.length)) != -1) {
                out.write(buf, 0, len);
            }
            bis.close();
        } catch (Exception e) {
            throw new ServiceException("下载失败");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
