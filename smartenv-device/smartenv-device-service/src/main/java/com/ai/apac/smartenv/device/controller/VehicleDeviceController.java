package com.ai.apac.smartenv.device.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.device.dto.VehicleMonitorDeviceImportResultModel;
import com.ai.apac.smartenv.device.dto.VehicleSensorDeviceImportResultModel;
import com.ai.apac.smartenv.device.entity.*;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.service.*;
import com.ai.apac.smartenv.device.vo.*;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.EntityCategoryCache;
import com.ai.apac.smartenv.system.entity.CharSpecValue;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.feign.ICharSpecClient;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.cache.VehicleCategoryCache;
import com.ai.apac.smartenv.vehicle.dto.VehicleInfoImportResultModel;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

import static com.ai.apac.smartenv.system.cache.EntityCategoryCache.bladeRedisCache;

/**
 * @ClassName PersonDeviceController
 * @Desc ????????????????????????
 * @Author ZHANGLEI25
 * @Date 2020/2/18 16:14
 * @Version 1.0
 */
@RestController
@AllArgsConstructor
@RequestMapping("/vehicleDevice")
@Api(value = "????????????????????????", tags = "??????????????????????????????")
public class VehicleDeviceController {

    private IDeviceInfoService deviceInfoService;

    private IDeviceChannelService deviceChannelService;

    private ISimInfoService simInfoService;

    private IDeviceExtService deviceExtService;

    private IDeviceRelService deviceRelService;

    private ISimRelService simRelService;

    private IEntityCategoryClient entityCategoryClient;

    private IOssClient ossClient;

    private IDeviceClient deviceClient;

    private ICharSpecClient charSpecClient;


    private BladeRedisCache bladeRedisCache;
    /**
     * ??????????????? ??????????????????
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    @ApiLog(value = "????????????????????????")
    public R<IPage<VehicleDeviceVO>> page(DeviceInfo deviceInfo, Query query,
                                          @RequestParam(name = "tag", required = false) String tag,
                                          @RequestParam(name = "simCode", required = false) String simCode) {
        IPage<VehicleDeviceVO> pages = deviceInfoService.pageDevices4Query(deviceInfo, query,tag,simCode);
        List<VehicleDeviceVO> deviceInfoList = pages.getRecords();
        List<VehicleDeviceVO> reDeviceInfoVOList = new ArrayList<VehicleDeviceVO>();
        deviceInfoList.forEach(vehicleDeviceVO -> {
//            VehicleDeviceVO vehicleDeviceVO = Objects.requireNonNull(BeanUtil.copy(deviceInfo_, VehicleDeviceVO.class));
            vehicleDeviceVO.setDeviceFactoryName(DictCache.getValue("device_manufacturer",vehicleDeviceVO.getDeviceFactory()));
            vehicleDeviceVO.setEntityCategoryName(EntityCategoryCache.getCategoryNameById(vehicleDeviceVO.getEntityCategoryId()));
//            List<DeviceRel> deviceRelList = deviceRelService.getDeviceRelByDeviceId(vehicleDeviceVO.getId());
            if(StringUtils.isNotBlank(vehicleDeviceVO.getRelEntityId())){
//            	VehicleInfo vehicleInfo = vehicleClient.vehicleInfoById(deviceRelList.get(0).getEntityId()).getData();
                VehicleInfo vehicleInfo = VehicleCache.getVehicleById(null, Long.valueOf(vehicleDeviceVO.getRelEntityId()));
                vehicleDeviceVO.setIsBinded(1);
//                    vehicleDeviceVO.setRelEntityId(deviceRelList.get(0).getEntityId().toString());
               // String categoryName = EntityCategoryCache.getCategoryNameById(vehicleInfo.getEntityCategoryId());
               // String entityCategoryName = entityCategoryClient.getCategoryName(vehicleInfo.getEntityCategoryId()).getData();
                String entityCategoryName = VehicleCategoryCache.getCategoryNameByCode(vehicleInfo.getEntityCategoryId().toString(), AuthUtil.getTenantId());

                vehicleDeviceVO.setRelEntityDesc(vehicleInfo.getPlateNumber()+"("+entityCategoryName+")");
//                }
            }else{
                vehicleDeviceVO.setIsBinded(0);
                vehicleDeviceVO.setRelEntityId("-99");
                vehicleDeviceVO.setRelEntityDesc("???");
            }

//            QueryWrapper<SimRel> simRelQueryWrapper = new QueryWrapper<SimRel>();
//            simRelQueryWrapper.lambda().eq(SimRel::getDeviceId,deviceInfo_.getId());
//            SimRel simRel = simRelService.getOne(simRelQueryWrapper);
//            if(ObjectUtil.isNotEmpty(simRel)){
//                SimInfo simInfo = simInfoService.getById(simRel.getSimId());
//                vehicleDeviceVO.setSim(simInfo.getSimCode());
//                vehicleDeviceVO.setSimId(simRel.getSimId().toString());
//                vehicleDeviceVO.setSimNumber(simInfo.getSimNumber());
//            }

//            List<DeviceChannel> deviceChannelList = deviceChannelService.getChannelInfoByDeviceId(deviceInfo_.getId());
//            if(deviceChannelList.size() > 0 ){
//                vehicleDeviceVO.setDeviceChannelList(deviceChannelList);
//            }

            reDeviceInfoVOList.add(vehicleDeviceVO);
        });
        IPage<VehicleDeviceVO> iPage = new Page<>(query.getCurrent(), query.getSize(), pages.getTotal());
        iPage.setRecords(reDeviceInfoVOList);
        return R.data(iPage);
    }

    /**
     * ??????
     */
    @PostMapping("")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????????????????", notes = "??????VehicleDeviceVO")
    @Transactional(rollbackFor = Exception.class)
    @ApiLog(value = "??????????????????")
    public R save(@Valid @RequestBody VehicleDeviceVO vehicleDeviceVO) throws IOException {
        // ????????????
        verifyParam(vehicleDeviceVO);

        boolean save = deviceInfoService.saveVehicleDeviceInfo(vehicleDeviceVO);

        return R.status(save);
    }


    /**
     * ???????????????????????????
     */
    @PutMapping("")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "??????????????????", notes = "????????????DeviceVO")
    @Transactional(rollbackFor = Exception.class)
    @ApiLog(value = "??????????????????")
    public R put(@Valid @RequestBody VehicleDeviceVO vehicleDeviceVO) throws IOException {
        //?????????????????????????????????????????????????????????

        // ????????????
        verifyParamForUpdate(vehicleDeviceVO);

        // ????????????
        verifyParam(vehicleDeviceVO);

        String newCode = vehicleDeviceVO.getDeviceCode();
        String oldCode = deviceInfoService.getById(vehicleDeviceVO.getId()).getDeviceCode();
        Boolean updateSync = newCode.equals(oldCode)?false:true;

        boolean save = deviceInfoService.updateVehicleDeviceInfo(vehicleDeviceVO);

        //?????????????????????????????????????????????????????????????????????
        if(save && updateSync){
            //??????????????????????????????????????????????????????????????????????????????
            List<DeviceRel> deviceRelList = deviceRelService.getDeviceRelByDeviceId(vehicleDeviceVO.getId());
            if(ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0){
                deviceInfoService.syncDeviceCode(oldCode,newCode,"1");
            }
        }

        return R.status(save);
    }

    /**
     * ??????????????????
     */
    @DeleteMapping("")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "????????????????????????", notes = "????????????ID??????")
    @Transactional(rollbackFor = Exception.class)
    @ApiLog(value = "????????????????????????")
    public R remove(@ApiParam(value = "????????????", required = true) @RequestParam String ids) {

        return R.status(deviceInfoService.batchRemove(Func.toLongList(ids)));

    }

    /**
     * ?????????????????????????????????
     */
    @GetMapping("")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "??????????????????", notes = "????????????ID??????")
    @Transactional(rollbackFor = Exception.class)
    @ApiLog(value = "??????????????????")
    public R<VehicleDeviceVO> get(@ApiParam(value = "??????", required = true) @RequestParam String id) {
        DeviceInfo deviceInfo = deviceInfoService.getById(id);
        if(null == deviceInfo){
            throw new ServiceException("??????????????????");
        }
        VehicleDeviceVO vehicleDeviceVO = generateVO(deviceInfo);
        return R.data(vehicleDeviceVO);
    }

    /**
     * ??????CODE?????????????????????????????????
     */
    @GetMapping("/byCode")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "??????Code??????????????????????????????", notes = "??????DeviceCode")
    @Transactional(rollbackFor = Exception.class)
    @ApiLog(value = "??????Code??????????????????????????????")
    public R<VehicleDeviceVO> getByParam(@ApiParam(value = "??????", required = true) @RequestParam String code) {
        DeviceInfo deviceInfo = deviceInfoService.getDeviceInfoByCode(code);
        if(null == deviceInfo){
            throw new ServiceException("??????????????????");
        }
        VehicleDeviceVO vehicleDeviceVO = generateVO(deviceInfo);
        return R.data(vehicleDeviceVO);
    }

    @GetMapping("/importVehicleDeviceModel")
    @ApiOperationSupport(order = 6)
    @ApiLog(value = "??????????????????????????????")
    @ApiOperation(value = "??????????????????????????????", notes = "??????????????????????????????")
    public R importVehicleDeviceModel() throws Exception {
        String name = DictCache.getValue(DeviceConstant.DICT_IMPORT_EXCEL_MODEL, DeviceConstant.DICT_IMPORT_EXCEL_MODEL_VEHICLE_DEVICE);
        String link = ossClient.getObjectLink(DeviceConstant.OSS_BUCKET_NAME, name).getData();
        return R.data(link);
    }



    @SuppressWarnings("finally")
    @PostMapping("/importVehicleDevice")
    @ApiOperationSupport(order = 7)
    @ApiLog(value = "????????????????????????")
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    public R<VehicleDeviceImportResultVO> importVehicleDevice(@RequestParam("file") MultipartFile excel) throws Exception {
        VehicleDeviceImportResultVO result = new VehicleDeviceImportResultVO();
        int successCount = 0;
        int failCount = 0;
        List<VehicleMonitorDeviceImportResultModel> failRecords1 = new ArrayList<>();
        List<VehicleMonitorDeviceImportResultModel> allRecords1 = new ArrayList<>();
        List<VehicleSensorDeviceImportResultModel> failRecords2 = new ArrayList<>();
        List<VehicleSensorDeviceImportResultModel> allRecords2 = new ArrayList<>();
        InputStream inputStream1 = null;
        InputStream inputStream2 = null;
        try {
            inputStream1 = new BufferedInputStream(excel.getInputStream());
            inputStream2 = new BufferedInputStream(excel.getInputStream());
            List<Object> datas1 = EasyExcelFactory.read(inputStream1, new Sheet(1, 1));
            List<Object> datas2 = EasyExcelFactory.read(inputStream2, new Sheet(2, 1));
            if ((datas1 == null || datas1.isEmpty()) && (datas2 == null || datas2.isEmpty()) ) {
                throw new ServiceException("Execl????????????,???????????????");
            }
            if(ObjectUtil.isNotEmpty(datas1) && datas1.size() > 0){
            for (Object object : datas1) {
                VehicleMonitorDeviceImportResultModel currentModel = new VehicleMonitorDeviceImportResultModel();
                try {
                    // ??????????????????
                    List<String> params = new ArrayList<>();
                    for (Object o : (List<?>) object) {
                        params.add(String.class.cast(o));
                    }
                    // ??????????????????
                    VehicleDeviceVO vehicleDeviceVO = new VehicleDeviceVO();
                    List<DeviceChannel> deviceChannelList = new ArrayList<DeviceChannel>();
                    if (params.size() > DeviceConstant.VehicleMonitorDeviceExcelImportIndex.DEVICE_CODE) {
                        currentModel.setDeviceCode(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.DEVICE_CODE));
                        vehicleDeviceVO.setDeviceCode(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.DEVICE_CODE));
                    }
                    if (params.size() > DeviceConstant.VehicleMonitorDeviceExcelImportIndex.DEVICE_NAME) {
                        currentModel.setDeviceName(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.DEVICE_NAME));
                        vehicleDeviceVO.setDeviceName(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.DEVICE_NAME));
                    }
                    if (params.size() > DeviceConstant.VehicleMonitorDeviceExcelImportIndex.DEVICE_TYPE) {
                        currentModel.setDeviceType(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.DEVICE_TYPE));
                        vehicleDeviceVO.setDeviceType(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.DEVICE_TYPE));
                    }
                    if (params.size() > DeviceConstant.VehicleMonitorDeviceExcelImportIndex.DEVICE_FACTORY) {
                        currentModel.setDeviceFactory(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.DEVICE_FACTORY));
                        vehicleDeviceVO.setDeviceFactory(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.DEVICE_FACTORY));
                    }
                    if (params.size() > DeviceConstant.VehicleMonitorDeviceExcelImportIndex.ENTITY_CATEGORY_ID) {
                        currentModel.setEntityCategoryId(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.ENTITY_CATEGORY_ID));
                        vehicleDeviceVO.setEntityCategoryId(Long.parseLong(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.ENTITY_CATEGORY_ID)));
                    }
                    if (params.size() > DeviceConstant.VehicleMonitorDeviceExcelImportIndex.SIM_CODE) {
                        currentModel.setSimCode(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.SIM_CODE));
                        vehicleDeviceVO.setSim(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.SIM_CODE));

                    }

                    if (params.size() > DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL1) {
                        currentModel.setChannel1(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL1));
                        DeviceChannel deviceChannel = new DeviceChannel();
                        deviceChannel.setChannelName(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL1));
                        deviceChannel.setChannelSeq("1");
                        deviceChannelList.add(deviceChannel);
                    }
                    if (params.size() > DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL2) {
                        currentModel.setChannel2(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL2));
                        DeviceChannel deviceChannel = new DeviceChannel();
                        deviceChannel.setChannelName(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL2));
                        deviceChannel.setChannelSeq("2");
                        deviceChannelList.add(deviceChannel);
                    }
                    if (params.size() > DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL3) {
                        currentModel.setChannel3(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL3));
                        DeviceChannel deviceChannel = new DeviceChannel();
                        deviceChannel.setChannelName(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL3));
                        deviceChannel.setChannelSeq("3");
                        deviceChannelList.add(deviceChannel);

                    }
                    if (params.size() > DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL4) {
                        currentModel.setChannel4(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL4));
                        DeviceChannel deviceChannel = new DeviceChannel();
                        deviceChannel.setChannelName(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL4));
                        deviceChannel.setChannelSeq("4");
                        deviceChannelList.add(deviceChannel);

                    }
                    if (params.size() > DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL5) {
                        currentModel.setChannel5(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL5));
                        DeviceChannel deviceChannel = new DeviceChannel();
                        deviceChannel.setChannelName(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL5));
                        deviceChannel.setChannelSeq("5");
                        deviceChannelList.add(deviceChannel);

                    }
                    if (params.size() > DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL6) {
                        currentModel.setChannel6(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL6));
                        DeviceChannel deviceChannel = new DeviceChannel();
                        deviceChannel.setChannelName(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL6));
                        deviceChannel.setChannelSeq("6");
                        deviceChannelList.add(deviceChannel);

                    }
                    if (params.size() > DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL7) {
                        currentModel.setChannel7(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL7));
                        DeviceChannel deviceChannel = new DeviceChannel();
                        deviceChannel.setChannelName(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL7));
                        deviceChannel.setChannelSeq("7");
                        deviceChannelList.add(deviceChannel);
                    }
                    if (params.size() > DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL8) {
                        currentModel.setChannel8(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL8));
                        DeviceChannel deviceChannel = new DeviceChannel();
                        deviceChannel.setChannelName(params.get(DeviceConstant.VehicleMonitorDeviceExcelImportIndex.CHANNEL8));
                        deviceChannel.setChannelSeq("8");
                        deviceChannelList.add(deviceChannel);
                    }
                    if(deviceChannelList.size() > 0){
                        vehicleDeviceVO.setDeviceChannelList(deviceChannelList);
                    }
                    // ??????
                    verifyParam(vehicleDeviceVO);
                    deviceInfoService.saveVehicleDeviceInfo(vehicleDeviceVO);
                    // ????????????
                    successCount++;
                    currentModel.setStatus("??????");
                    allRecords1.add(currentModel);
                } catch (Exception e) {
                    failCount++;
                    currentModel.setStatus("??????");
                    currentModel.setReason(e.getMessage());
                    failRecords1.add(currentModel);
                    allRecords1.add(currentModel);
                }
            }}

            if(ObjectUtil.isNotEmpty(datas2) && datas2.size() > 0){
                for (Object object : datas2) {
                    VehicleSensorDeviceImportResultModel currentModel = new VehicleSensorDeviceImportResultModel();
                    try {
                        // ??????????????????
                        List<String> params = new ArrayList<>();
                        for (Object o : (List<?>) object) {
                            params.add(String.class.cast(o));
                        }
                        // ??????????????????
                        VehicleDeviceVO vehicleDeviceVO = new VehicleDeviceVO();
                        if (params.size() > DeviceConstant.VehicleSensorDeviceExcelImportIndex.DEVICE_CODE) {
                            currentModel.setDeviceCode(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.DEVICE_CODE));
                            vehicleDeviceVO.setDeviceCode(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.DEVICE_CODE));
                        }
                        if (params.size() > DeviceConstant.VehicleSensorDeviceExcelImportIndex.DEVICE_NAME) {
                            currentModel.setDeviceName(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.DEVICE_NAME));
                            vehicleDeviceVO.setDeviceName(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.DEVICE_NAME));
                        }
                        if (params.size() > DeviceConstant.VehicleSensorDeviceExcelImportIndex.DEVICE_TYPE) {
                            currentModel.setDeviceType(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.DEVICE_TYPE));
                            vehicleDeviceVO.setDeviceType(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.DEVICE_TYPE));
                        }
                        if (params.size() > DeviceConstant.VehicleSensorDeviceExcelImportIndex.DEVICE_FACTORY) {
                            currentModel.setDeviceFactory(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.DEVICE_FACTORY));
                            vehicleDeviceVO.setDeviceFactory(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.DEVICE_FACTORY));
                        }
                        if (params.size() > DeviceConstant.VehicleSensorDeviceExcelImportIndex.ENTITY_CATEGORY_ID) {
                            currentModel.setEntityCategoryId(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.ENTITY_CATEGORY_ID));
                            vehicleDeviceVO.setEntityCategoryId(Long.parseLong(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.ENTITY_CATEGORY_ID)));
                        }
                        if (params.size() > DeviceConstant.VehicleSensorDeviceExcelImportIndex.SIM_CODE) {
                            currentModel.setSimCode(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.SIM_CODE));
                            vehicleDeviceVO.setSim(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.SIM_CODE));
                        }
                        if (params.size() > DeviceConstant.VehicleSensorDeviceExcelImportIndex.COORD) {
                            currentModel.setSimCode(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.COORD));
                            vehicleDeviceVO.setCoordValue(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.COORD));
                        }
                        if (params.size() > DeviceConstant.VehicleSensorDeviceExcelImportIndex.AUTH_CODE) {
                            currentModel.setSimCode(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.AUTH_CODE));
                            vehicleDeviceVO.setAuthCode(params.get(DeviceConstant.VehicleSensorDeviceExcelImportIndex.AUTH_CODE));
                        }

                        // ??????
                        verifyParam(vehicleDeviceVO);
                        deviceInfoService.saveVehicleDeviceInfo(vehicleDeviceVO);
                        // ????????????
                        successCount++;
                        currentModel.setStatus("??????");
                        allRecords2.add(currentModel);
                    } catch (Exception e) {
                        failCount++;
                        currentModel.setStatus("??????");
                        currentModel.setReason(e.getMessage());
                        failRecords2.add(currentModel);
                        allRecords2.add(currentModel);
                    }
                }}
        } catch (Exception e) {
//            logger.error("Excel????????????" + e.getMessage());
        } finally {
            if (inputStream1 != null) {
                inputStream1.close();
            }
            if (inputStream2 != null) {
                inputStream2.close();
            }
            result.setSuccessCount(successCount);
            result.setFailCount(failCount);
            result.setFailRecords1(failRecords1);
            result.setFailRecords2(failRecords2);
            if (failCount > 0) {
                if(failRecords1.size() > 0){
                    String key = CacheNames.VEHICLE_MONITOR_DEVICE_IMPORT + ":" + DateUtil.now().getTime();
                    bladeRedisCache.setEx(key, allRecords1, 3600L);
                    result.setFileKey1(key);
                }
                if(failRecords2.size() > 0){
                    String key = CacheNames.VEHICLE_SENSOR_DEVICE_IMPORT + ":" + DateUtil.now().getTime();
                    bladeRedisCache.setEx(key, allRecords2, 3600L);
                    result.setFileKey2(key);
                }
            }
        }
        return R.data(result);
    }



    @GetMapping("/importResultExcel")
    @ApiOperationSupport(order = 9)
    @ApiLog(value = "??????????????????????????????")
    @ApiOperation(value = "??????????????????????????????", notes = "")
    public void getImportResultExcel(String key1,String key2) throws Exception {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        Object object1 = bladeRedisCache.get(key1);
        Object object2 = bladeRedisCache.get(key2);
        List<VehicleMonitorDeviceImportResultModel> modelList1 = new ArrayList<>();
        List<VehicleSensorDeviceImportResultModel> modelList2 = new ArrayList<>();

        if(ObjectUtil.isNotEmpty(object1)){
        for (Object o : (List<?>) object1) {
            VehicleMonitorDeviceImportResultModel model = BeanUtil.copy(o, VehicleMonitorDeviceImportResultModel.class);
            modelList1.add(model);
        }}
        if(ObjectUtil.isNotEmpty(object2)){
            for (Object o : (List<?>) object2) {
                VehicleSensorDeviceImportResultModel model = BeanUtil.copy(o, VehicleSensorDeviceImportResultModel.class);
                modelList2.add(model);
            }}
        OutputStream out = null;
        try {
            response.reset(); // ??????buffer??????
            String fileName = "????????????????????????";
            out = response.getOutputStream();
            response.setContentType("application/x-msdownload;charset=utf-8");
            response.setHeader("Content-disposition", "attachment;filename= " + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            int sheetNo = 1;
            if(modelList1.size()>0){
                Sheet sheet1 = new Sheet(sheetNo, 0, VehicleMonitorDeviceImportResultModel.class);
                sheet1.setSheetName("??????????????????????????????");
                writer.write(modelList1, sheet1);
                sheetNo ++;
            }
            if(modelList2.size()>0){
                Sheet sheet2 = new Sheet(sheetNo, 0, VehicleSensorDeviceImportResultModel.class);
                sheet2.setSheetName("?????????????????????????????????");
                writer.write(modelList2, sheet2);
            }
            writer.finish();
        } catch (IOException e) {
            throw new ServiceException("????????????????????????????????????");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw new ServiceException("????????????????????????????????????");
            }
        }
    }


    private VehicleDeviceVO generateVO(DeviceInfo deviceInfo){
        VehicleDeviceVO vehicleDeviceVO = Objects.requireNonNull(BeanUtil.copy(deviceInfo, VehicleDeviceVO.class));
        EntityCategory entityCategory = entityCategoryClient.getCategory(vehicleDeviceVO.getEntityCategoryId()).getData();
        vehicleDeviceVO.setEntityCategoryName(entityCategory.getCategoryName());
        EntityCategory parentEntityCategory = entityCategoryClient.getCategory(entityCategory.getParentCategoryId()).getData();
        vehicleDeviceVO.setParentEntityCategoryId(parentEntityCategory.getId().toString());
        vehicleDeviceVO.setParentEntityCategoryName(parentEntityCategory.getCategoryName());

        QueryWrapper<SimRel> simRelQueryWrapper = new QueryWrapper<SimRel>();
        simRelQueryWrapper.lambda().eq(SimRel::getDeviceId,deviceInfo.getId());
        SimRel simRel = simRelService.getOne(simRelQueryWrapper);
        if(ObjectUtil.isNotEmpty(simRel)){
            SimInfo simInfo = simInfoService.getById(simRel.getSimId());
            vehicleDeviceVO.setSim(simInfo.getSimCode());
            vehicleDeviceVO.setSimId(simRel.getSimId().toString());
        }

        DeviceExt deviceExt1 = new DeviceExt();
        deviceExt1.setDeviceId(vehicleDeviceVO.getId());
        deviceExt1.setAttrId(DeviceConstant.DeviceCharSpec.VEHICLE_DEVICE_ICCID);
        List<DeviceExt> deviceExtList1 = deviceExtService.getExtInfoByParam(deviceExt1);
        if(deviceExtList1.size() > 0 ){
            vehicleDeviceVO.setAuthCode(deviceExtList1.get(0).getAttrValue());
        }

        DeviceExt deviceExt2 = new DeviceExt();
        deviceExt2.setDeviceId(vehicleDeviceVO.getId());
        deviceExt2.setAttrId(DeviceConstant.DeviceCharSpec.COORDS_SYSTEM);
        List<DeviceExt> deviceExtList2 = deviceExtService.getExtInfoByParam(deviceExt2);
        if(deviceExtList2.size() > 0){
            vehicleDeviceVO.setCoordId(Long.toString(deviceExtList2.get(0).getAttrValueId()));
            vehicleDeviceVO.setCoordValue(deviceExtList2.get(0).getAttrValue());
            vehicleDeviceVO.setCoordValueName(deviceExtList2.get(0).getAttrDisplayValue());
        }


        List<DeviceChannel> deviceChannelList = deviceChannelService.getChannelInfoByDeviceId(deviceInfo.getId());
        if(deviceChannelList.size() > 0 ){
            vehicleDeviceVO.setDeviceChannelList(deviceChannelList);
        }
        return vehicleDeviceVO;
    }


    private void verifyParamForUpdate(@Valid VehicleDeviceVO vehicleDeviceVO) {
        if (vehicleDeviceVO.getId() == null) {
            // ??????????????????ID
            throw new ServiceException("??????Id????????????");
        }

        if(null == deviceInfoService.getById(vehicleDeviceVO.getId())){
            // ??????????????????ID
            throw new ServiceException("??????????????????");
        }
    }

    private void verifyParam(@Valid VehicleDeviceVO vehicleDeviceVO) {

        if (StringUtils.isBlank(vehicleDeviceVO.getDeviceCode())) {
            // "????????????????????????"
            throw new ServiceException("????????????????????????");
        } else {
            //??????feign???????????????????????????
            List<DeviceInfo> deviceInfoList = deviceClient.getDeviceByCode(vehicleDeviceVO.getDeviceCode()).getData();
            if(ObjectUtil.isNotEmpty(deviceInfoList)&&deviceInfoList.size()>0){
                if (null == vehicleDeviceVO.getId() || !vehicleDeviceVO.getId().equals(deviceInfoList.get(0).getId())) {
                    throw new ServiceException("?????????????????????,?????????");
                }
            }
        }


        if (StringUtils.isBlank(vehicleDeviceVO.getDeviceName())) {
            // "????????????????????????"
            throw new ServiceException("????????????????????????");
        }else{
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setDeviceName(vehicleDeviceVO.getDeviceName());
            List<DeviceInfo> deviceInfoList = deviceInfoService.listDevicesByParam(deviceInfo);
            if(deviceInfoList.size() > 0 ){
                if(null == vehicleDeviceVO.getId() || !vehicleDeviceVO.getId().equals(deviceInfoList.get(0).getId())){
                    throw new ServiceException("?????????????????????,?????????");
                }
            }
        }
        if (vehicleDeviceVO.getEntityCategoryId() == null) {
            // ????????????????????????
            throw new ServiceException("????????????????????????");
        }
        if (StringUtil.isBlank(vehicleDeviceVO.getDeviceFactory())) {
            // ????????????????????????
            throw new ServiceException("????????????????????????");
        }
        if (StringUtil.isBlank(vehicleDeviceVO.getDeviceType())) {
            // ????????????????????????
            throw new ServiceException("????????????????????????");
        }
        if (StringUtil.isBlank(vehicleDeviceVO.getDeviceCode())) {
            // ????????????????????????
            throw new ServiceException("????????????????????????");
        }else{
            DeviceInfo deviceInfo_ = new DeviceInfo();
            deviceInfo_.setDeviceCode(vehicleDeviceVO.getDeviceCode());
            //???????????????????????????????????????????????????code??????????????????????????????????????????????????????????????????
            List<DeviceInfo> deviceInfoList_ = deviceInfoService.listDevicesByParam(deviceInfo_);
            if (deviceInfoList_.size() > 0) {
                if (null == vehicleDeviceVO.getId() || !vehicleDeviceVO.getId().equals(deviceInfoList_.get(0).getId())) {
                    throw new ServiceException("?????????????????????,?????????");
                }
            }
        }

        //????????????????????????sim????????????????????????
        if (StringUtil.isNotBlank(vehicleDeviceVO.getSim()) && StringUtil.isBlank(vehicleDeviceVO.getSimId())) {
            QueryWrapper<SimInfo> queryWrapper = new QueryWrapper<SimInfo>();
            queryWrapper.lambda().eq(SimInfo::getSimCode,vehicleDeviceVO.getSim());
            List<SimInfo> simInfoList = simInfoService.list(queryWrapper);
            if(ObjectUtil.isNotEmpty(simInfoList) && simInfoList.size()>0){
                SimRel simRel = simRelService.selectSimRelBySimId(simInfoList.get(0).getId());
                if(ObjectUtil.isNotEmpty(simRel) && ObjectUtil.isNotEmpty(simRel.getId())){
                    throw new ServiceException("??????["+vehicleDeviceVO.getSim()+"]???SIM????????????");
                }else{
                    vehicleDeviceVO.setSimId(simInfoList.get(0).getId().toString());
                    vehicleDeviceVO.setSimNumber(simInfoList.get(0).getSimNumber());
                }
            }else{
                throw new ServiceException("??????["+vehicleDeviceVO.getSim()+"]???SIM????????????");
            }
        }


        List<DeviceChannel> deviceChannelList = vehicleDeviceVO.getDeviceChannelList();
        if(ObjectUtil.isNotEmpty(vehicleDeviceVO.getDeviceChannelList())){
            List<String> channelCode = new ArrayList<String>();
            if(deviceChannelList.size() > 0 ){
                deviceChannelList.forEach(deviceChannel -> {
                    if(channelCode.contains(deviceChannel.getChannelSeq())){
                        throw new ServiceException("????????????????????????");
                    }else{
                        channelCode.add(deviceChannel.getChannelSeq());
                    }
                });
            }
        }

        if(vehicleDeviceVO.getEntityCategoryId().equals(DeviceConstant.DeviceCategory.VEHICLE_GPS_DEVICE)){
        if (StringUtil.isBlank(vehicleDeviceVO.getCoordValue())) {
            // ??????????????????????????????
            throw new ServiceException("????????????["+vehicleDeviceVO.getDeviceCode()+"]????????????????????????");
        } else {
            CharSpecValue charSpecValue1 = charSpecClient.getCharSpecValue(DeviceConstant.DeviceCharSpec.COORDS_SYSTEM,vehicleDeviceVO.getCoordValue()).getData();
            if(ObjectUtil.isNotEmpty(charSpecValue1)){
                vehicleDeviceVO.setCoordId(charSpecValue1.getId().toString());
                vehicleDeviceVO.setCoordValueName(charSpecValue1.getDisplayValue());
            }
        }}

    }
}
