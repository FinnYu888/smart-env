package com.ai.apac.smartenv.device.controller;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.device.dto.PersonDeviceImportResultModel;
import com.ai.apac.smartenv.device.entity.*;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.service.*;
import com.ai.apac.smartenv.device.vo.DeviceInfoVO;
import com.ai.apac.smartenv.device.vo.PersonDeviceImportResultVO;
import com.ai.apac.smartenv.device.vo.PersonDeviceVO;
import com.ai.apac.smartenv.device.vo.VehicleDeviceVO;
import com.ai.apac.smartenv.device.wrapper.DeviceInfoWrapper;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.CharSpecValue;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.feign.ICharSpecClient;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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
@RequestMapping("/personDevice")
@Api(value = "????????????????????????", tags = "??????????????????????????????")
public class PersonDeviceController {

    private IDeviceInfoService deviceInfoService;

    private IDeviceExtService deviceExtService;

    private IDeviceRelService deviceRelService;

    private ISimRelService simRelService;

    private ISimInfoService simInfoService;

    private IDictClient dictClient;

    private IPersonClient personClient;

    private ICharSpecClient charSpecClient;

    private IEntityCategoryClient entityCategoryClient;

    private IOssClient ossClient;

    private IDeviceClient deviceClient;


    private BladeRedisCache bladeRedisCache;

    /**
     * ??????????????? ??????????????????
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    @ApiLog(value = "????????????????????????")
    public R<IPage<PersonDeviceVO>> page(DeviceInfo deviceInfo, Query query,
                                         @RequestParam(name = "tag", required = false) String tag,
                                         @RequestParam(name = "simCode", required = false) String simCode) {
        IPage<DeviceInfo> pages = deviceInfoService.pageDevices(deviceInfo, query, tag, simCode);

        List<DeviceInfo> deviceInfoList = pages.getRecords();
        List<PersonDeviceVO> reDeviceInfoVOList = new ArrayList<PersonDeviceVO>();
        deviceInfoList.forEach(deviceInfo_ -> {
            PersonDeviceVO personDeviceVO = Objects.requireNonNull(BeanUtil.copy(deviceInfo_, PersonDeviceVO.class));
            personDeviceVO.setDeviceFactoryName(DictCache.getValue("device_manufacturer", deviceInfo_.getDeviceFactory()));
            personDeviceVO.setEntityCategoryName(entityCategoryClient.getCategoryName(deviceInfo_.getEntityCategoryId()).getData());
            List<DeviceRel> deviceRelList = deviceRelService.getDeviceRelByDeviceId(deviceInfo_.getId());
            if (deviceRelList.size() > 0) {
//                Person person = personClient.getPerson(deviceRelList.get(0).getEntityId()).getData();
                Person person = PersonCache.getPersonById(null, deviceRelList.get(0).getEntityId());
                if (null != person && null != person.getId()) {
                    personDeviceVO.setIsBinded(1);
                    personDeviceVO.setRelEntityId(deviceRelList.get(0).getEntityId().toString());
                    personDeviceVO.setRelEntityDesc(person.getPersonName() + "(" + person.getJobNumber() + ")");
                }
            } else {
                personDeviceVO.setIsBinded(0);
                personDeviceVO.setRelEntityId("-99");
                personDeviceVO.setRelEntityDesc("???");
            }

            QueryWrapper<SimRel> simRelQueryWrapper = new QueryWrapper<SimRel>();
            simRelQueryWrapper.lambda().eq(SimRel::getDeviceId, deviceInfo_.getId());
            SimRel simRel = simRelService.getOne(simRelQueryWrapper);
            if (ObjectUtil.isNotEmpty(simRel)) {
                SimInfo simInfo = simInfoService.getById(simRel.getSimId());
                personDeviceVO.setSim(simInfo.getSimCode());
                personDeviceVO.setSimId(simRel.getSimId().toString());
                personDeviceVO.setSimNumber(simInfo.getSimNumber());
            }

            reDeviceInfoVOList.add(personDeviceVO);
        });
        IPage<PersonDeviceVO> iPage = new Page<>(query.getCurrent(), query.getSize(), pages.getTotal());
        iPage.setRecords(reDeviceInfoVOList);
        return R.data(iPage);
    }

    /**
     * ??????
     */
    @PostMapping("")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????????????????", notes = "??????PersonDeviceVO")
    @Transactional(rollbackFor = Exception.class)
    @ApiLog(value = "??????????????????")
    public R save(@Valid @RequestBody PersonDeviceVO personDeviceVO) throws IOException {
        // ????????????
        verifyParam(personDeviceVO);
        Boolean save = deviceInfoService.savePersonDeviceInfo(personDeviceVO);
        return R.status(save);
    }


    /**
     * ???????????????????????????
     */
    @PutMapping("")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "??????????????????", notes = "??????PersonDeviceVO")
    @Transactional(rollbackFor = Exception.class)
    @ApiLog(value = "??????????????????")
    public R put(@Valid @RequestBody PersonDeviceVO personDeviceVO) throws IOException {

        // ????????????
        verifyParamForUpdate(personDeviceVO);

        // ????????????
        verifyParam(personDeviceVO);

        String newCode = personDeviceVO.getDeviceCode();
        String oldCode = deviceInfoService.getById(personDeviceVO.getId()).getDeviceCode();
        Boolean updateSync = newCode.equals(oldCode)?false:true;

        Boolean save = deviceInfoService.updatePersonDeviceInfo(personDeviceVO);

        //?????????????????????????????????????????????????????????????????????
        if(save && updateSync){
            //??????????????????????????????????????????????????????????????????????????????
            List<DeviceRel> deviceRelList = deviceRelService.getDeviceRelByDeviceId(personDeviceVO.getId());
            if(ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0){
                deviceInfoService.syncDeviceCode(oldCode,newCode,"2");
            }
        }

        return R.status(save);
    }

    /**
     * ?????????????????????????????????
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
    public R<PersonDeviceVO> get(@ApiParam(value = "??????", required = true) @RequestParam String id) {
        DeviceInfo deviceInfo = deviceInfoService.getById(id);
        PersonDeviceVO personDeviceVO = generateVO(deviceInfo);
        return R.data(personDeviceVO);
    }


    /**
     * ??????CODE?????????????????????????????????
     */
    @GetMapping("/byCode")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "??????Code??????????????????????????????", notes = "??????DeviceCode")
    @Transactional(rollbackFor = Exception.class)
    @ApiLog(value = "??????Code??????????????????????????????")
    public R<PersonDeviceVO> getByParam(@ApiParam(value = "??????", required = true) @RequestParam String code) {
        DeviceInfo deviceInfo = deviceInfoService.getDeviceInfoByCode(code);
        if (null == deviceInfo) {
            throw new ServiceException("??????????????????");
        }
        PersonDeviceVO vehicleDeviceVO = generateVO(deviceInfo);
        return R.data(vehicleDeviceVO);
    }


    @GetMapping("/importPersonDeviceModel")
    @ApiOperationSupport(order = 6)
    @ApiLog(value = "??????????????????????????????")
    @ApiOperation(value = "??????????????????????????????", notes = "??????????????????????????????")
    public R importPersonDeviceModel() throws Exception {
        String name = DictCache.getValue(DeviceConstant.DICT_IMPORT_EXCEL_MODEL, DeviceConstant.DICT_IMPORT_EXCEL_MODEL_PERSON_DEVICE);
        String link = ossClient.getObjectLink(DeviceConstant.OSS_BUCKET_NAME, name).getData();
        return R.data(link);
    }



    @SuppressWarnings("finally")
    @PostMapping("/importPersonDevice")
    @ApiOperationSupport(order = 7)
    @ApiLog(value = "????????????????????????")
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    public R<PersonDeviceImportResultVO> importPersonDevice(@RequestParam("file") MultipartFile excel) throws Exception {
        PersonDeviceImportResultVO result = new PersonDeviceImportResultVO();
        int successCount = 0;
        int failCount = 0;
        List<PersonDeviceImportResultModel> failRecords = new ArrayList<>();
        List<PersonDeviceImportResultModel> allRecords = new ArrayList<>();
        InputStream inputStream = null;
        PersonDeviceImportResultModel currentModel = new PersonDeviceImportResultModel();
        try {
            inputStream = new BufferedInputStream(excel.getInputStream());
            List<Object> datas = EasyExcelFactory.read(inputStream, new Sheet(1, 1));
            if (datas == null || datas.isEmpty()) {
                throw new ServiceException("Execl????????????,???????????????");
            }
            for (Object object : datas) {
                try {
                    // ??????????????????
                    List<String> params = new ArrayList<>();
                    for (Object o : (List<?>) object) {
                        params.add(String.class.cast(o));
                    }
                    // ??????????????????
                    currentModel = new PersonDeviceImportResultModel();
                    if (params.size() > DeviceConstant.PersonDeviceExcelImportIndex.DEVICE_CODE) {
                        currentModel.setDeviceCode(params.get(DeviceConstant.PersonDeviceExcelImportIndex.DEVICE_CODE));
                    }
                    if (params.size() > DeviceConstant.PersonDeviceExcelImportIndex.DEVICE_NAME) {
                        currentModel.setDeviceName(params.get(DeviceConstant.PersonDeviceExcelImportIndex.DEVICE_NAME));
                    }
                    if (params.size() > DeviceConstant.PersonDeviceExcelImportIndex.DEVICE_TYPE) {
                        currentModel.setDeviceType(params.get(DeviceConstant.PersonDeviceExcelImportIndex.DEVICE_TYPE));
                    }
                    if (params.size() > DeviceConstant.PersonDeviceExcelImportIndex.DEVICE_FACTORY) {
                        currentModel.setDeviceFactory(params.get(DeviceConstant.PersonDeviceExcelImportIndex.DEVICE_FACTORY));
                    }
                    if (params.size() > DeviceConstant.PersonDeviceExcelImportIndex.ENTITY_CATEGORY_ID) {
                        currentModel.setEntityCategoryId(params.get(DeviceConstant.PersonDeviceExcelImportIndex.ENTITY_CATEGORY_ID));
                    }
                    if (params.size() > DeviceConstant.PersonDeviceExcelImportIndex.COORD) {
                        currentModel.setCoord(params.get(DeviceConstant.PersonDeviceExcelImportIndex.COORD));
                    }
                    if (params.size() > DeviceConstant.PersonDeviceExcelImportIndex.SIM_CODE) {
                        currentModel.setSimCode(params.get(DeviceConstant.PersonDeviceExcelImportIndex.SIM_CODE));
                    }


                    // ??????
                    PersonDeviceVO personDeviceVO = new PersonDeviceVO();
                    personDeviceVO.setDeviceCode(currentModel.getDeviceCode());
                    personDeviceVO.setDeviceName(currentModel.getDeviceName());
                    personDeviceVO.setDeviceType(currentModel.getDeviceType());
                    personDeviceVO.setDeviceFactory(currentModel.getDeviceFactory());
                    personDeviceVO.setEntityCategoryId(Long.parseLong(currentModel.getEntityCategoryId()));
                    personDeviceVO.setCoordValue(currentModel.getCoord());
                    personDeviceVO.setSim(currentModel.getSimCode());
                    verifyParam(personDeviceVO);
                    deviceInfoService.savePersonDeviceInfo(personDeviceVO);
                    // ????????????
                    successCount++;
                    currentModel.setStatus("??????");
                    allRecords.add(currentModel);
                } catch (Exception e) {
                    failCount++;
                    currentModel.setStatus("??????");
                    currentModel.setReason(e.getMessage());
                    failRecords.add(currentModel);
                    allRecords.add(currentModel);
                }
            }
        } catch (Exception e) {
//            logger.error("Excel????????????" + e.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            result.setSuccessCount(successCount);
            result.setFailCount(failCount);
            result.setFailRecords(failRecords);

            if (failCount > 0) {
                String key = CacheNames.PERSON_DEVICE_IMPORT + ":" + DateUtil.now().getTime();
                bladeRedisCache.setEx(key, allRecords, 3600L);
                result.setFileKey(key);
            }
        }
        return R.data(result);
    }




    private PersonDeviceVO generateVO(DeviceInfo deviceInfo) {
        PersonDeviceVO personDeviceVO = Objects.requireNonNull(BeanUtil.copy(deviceInfo, PersonDeviceVO.class));
        personDeviceVO.setEntityCategoryName(entityCategoryClient.getCategoryName(personDeviceVO.getEntityCategoryId()).getData());

        QueryWrapper<SimRel> simRelQueryWrapper = new QueryWrapper<SimRel>();
        simRelQueryWrapper.lambda().eq(SimRel::getDeviceId, deviceInfo.getId());
        SimRel simRel = simRelService.getOne(simRelQueryWrapper);
        if (ObjectUtil.isNotEmpty(simRel)) {
            SimInfo simInfo = simInfoService.getById(simRel.getSimId());
            personDeviceVO.setSim(simInfo.getSimCode());
            personDeviceVO.setSimId(simRel.getSimId().toString());
        }

        DeviceExt deviceExt1 = new DeviceExt();
        deviceExt1.setDeviceId(personDeviceVO.getId());
        deviceExt1.setAttrId(DeviceConstant.DeviceCharSpec.PERSON_DEVICE_ICCID);
        List<DeviceExt> deviceExtList1 = deviceExtService.getExtInfoByParam(deviceExt1);
        if (deviceExtList1.size() > 0) {
            personDeviceVO.setAuthCode(deviceExtList1.get(0).getAttrValue());
        }

        DeviceExt deviceExt2 = new DeviceExt();
        deviceExt2.setDeviceId(personDeviceVO.getId());
        deviceExt2.setAttrId(DeviceConstant.DeviceCharSpec.COORDS_SYSTEM);
        List<DeviceExt> deviceExtList2 = deviceExtService.getExtInfoByParam(deviceExt2);
        if (deviceExtList2.size() > 0) {
            personDeviceVO.setCoordId(Long.toString(deviceExtList2.get(0).getAttrValueId()));
            personDeviceVO.setCoordValue(deviceExtList2.get(0).getAttrValue());
            personDeviceVO.setCoordValueName(deviceExtList2.get(0).getAttrDisplayValue());
        }


        return personDeviceVO;
    }

    private void verifyParamForUpdate(@Valid PersonDeviceVO personDeviceVO) {
        if (personDeviceVO.getId() == null) {
            // ??????????????????ID
            throw new ServiceException("??????Id????????????");
        }

        if (null == deviceInfoService.getById(personDeviceVO.getId())) {
            // ??????????????????ID
            throw new ServiceException("??????????????????");
        }
    }

    private void verifyParam(@Valid PersonDeviceVO personDeviceVO) {

        if (StringUtils.isBlank(personDeviceVO.getDeviceCode())) {
            // "????????????????????????"
            throw new ServiceException("????????????????????????");
        } else {
            //??????feign???????????????????????????
            List<DeviceInfo> deviceInfoList = deviceClient.getDeviceByCode(personDeviceVO.getDeviceCode()).getData();
            if(ObjectUtil.isNotEmpty(deviceInfoList)&&deviceInfoList.size()>0){
                if (null == personDeviceVO.getId() || !personDeviceVO.getId().equals(deviceInfoList.get(0).getId())) {
                    throw new ServiceException("?????????????????????,?????????");
                }
            }
        }

        if (StringUtils.isBlank(personDeviceVO.getDeviceName())) {
            // "????????????????????????"
            throw new ServiceException("????????????????????????");
        } else {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setDeviceName(personDeviceVO.getDeviceName());
            List<DeviceInfo> deviceInfoList = deviceInfoService.listDevicesByParam(deviceInfo);
            if (deviceInfoList.size() > 0) {
                if (null == personDeviceVO.getId() || !personDeviceVO.getId().equals(deviceInfoList.get(0).getId())) {
                    throw new ServiceException("?????????????????????,?????????");
                }
            }
        }
        if (personDeviceVO.getEntityCategoryId() == null) {
            // ????????????????????????
            throw new ServiceException("????????????????????????");
        }
        EntityCategory entityCategory = entityCategoryClient.getCategory(personDeviceVO.getEntityCategoryId()).getData();
        if(ObjectUtil.isEmpty(entityCategory) || ObjectUtil.isEmpty(entityCategory.getId())){
            throw new ServiceException("?????????????????????");
        }
        if (StringUtil.isBlank(personDeviceVO.getDeviceFactory())) {
            // ????????????????????????
            throw new ServiceException("??????????????????????????????");
        }
        if(StringUtil.isBlank(DictCache.getValue("device_manufacturer",personDeviceVO.getDeviceFactory()))){
            throw new ServiceException("???????????????????????????");
        }
        if (StringUtil.isBlank(personDeviceVO.getDeviceType())) {
            // ????????????????????????
            throw new ServiceException("????????????????????????");
        }
        if (StringUtil.isBlank(personDeviceVO.getDeviceCode())) {
            // ????????????????????????
            throw new ServiceException("????????????????????????");
        } else {
            DeviceInfo deviceInfo_ = new DeviceInfo();
            deviceInfo_.setDeviceCode(personDeviceVO.getDeviceCode());
            //???????????????????????????????????????????????????code??????????????????????????????????????????????????????????????????
            List<DeviceInfo> deviceInfoList_ = deviceInfoService.listDevicesByParam(deviceInfo_);
            if (deviceInfoList_.size() > 0) {
                if (null == personDeviceVO.getId() || !personDeviceVO.getId().equals(deviceInfoList_.get(0).getId())) {
                    throw new ServiceException("?????????????????????,?????????");
                }
            }
        }

        if (StringUtil.isBlank(personDeviceVO.getCoordValue())) {
            // ??????????????????????????????
            throw new ServiceException("??????????????????????????????");
        } else {
            CharSpecValue charSpecValue1 = charSpecClient.getCharSpecValue(DeviceConstant.DeviceCharSpec.COORDS_SYSTEM,personDeviceVO.getCoordValue()).getData();
            if(ObjectUtil.isNotEmpty(charSpecValue1)){
                personDeviceVO.setCoordId(charSpecValue1.getId().toString());
                personDeviceVO.setCoordValueName(charSpecValue1.getDisplayValue());
            }
        }

        if(ObjectUtil.isEmpty(personDeviceVO.getSimId()) && ObjectUtil.isNotEmpty(personDeviceVO.getSim()) ){
            QueryWrapper<SimInfo> simInfoQueryWrapper = new QueryWrapper<SimInfo>();
            simInfoQueryWrapper.lambda().eq(SimInfo::getSimCode,personDeviceVO.getSim());
            List<SimInfo> simInfoList =  simInfoService.list(simInfoQueryWrapper);
            if(ObjectUtil.isNotEmpty(simInfoList) && simInfoList.size() > 0){
                SimInfo simInfo = simInfoList.get(0);
                QueryWrapper<SimRel> simRelQueryWrapper = new QueryWrapper<SimRel>();
                simRelQueryWrapper.lambda().eq(SimRel::getSimId,simInfo.getId());
                List<SimRel> simRelList = simRelService.list(simRelQueryWrapper);
                if(ObjectUtil.isNotEmpty(simRelList) && simRelList.size() > 0){
                    throw new ServiceException("SIM???????????????????????????,?????????");
                }
                personDeviceVO.setSimId(simInfo.getId().toString());
            }else{
                throw new ServiceException("SIM???????????????,?????????");
            }
        }

    }
}
