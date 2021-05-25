package com.ai.apac.smartenv.device.controller;

import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.device.entity.*;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.service.*;
import com.ai.apac.smartenv.device.vo.DeviceInfoVO;
import com.ai.apac.smartenv.device.vo.FacilityDeviceVO;
import com.ai.apac.smartenv.device.vo.PersonDeviceVO;
import com.ai.apac.smartenv.device.wrapper.DeviceInfoWrapper;
import com.ai.apac.smartenv.facility.entity.FacilityInfo;
import com.ai.apac.smartenv.facility.feign.IFacilityClient;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
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
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/facilityDevice")
@Api(value = "记录设施设备信息", tags = "记录设施设备信息接口")
public class FacilityDeviceController {

    private IDeviceInfoService deviceInfoService;

    private IDeviceChannelService deviceChannelService;

    private IDeviceExtService deviceExtService;

    private IDeviceRelService deviceRelService;

    private ISimInfoService simInfoService;

    private IEntityCategoryClient entityCategoryClient;

    private IFacilityClient facilityClient;

    private ISimRelService simRelService;

    private IDeviceClient deviceClient;


    /**
     * 自定义分页 记录设备信息
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "设施设备分页展现", notes = "设施设备分页展现")
    @ApiLog(value = "设施设备分页展现")
    public R<IPage<FacilityDeviceVO>> page(DeviceInfo deviceInfo, Query query,
                                           @RequestParam(name = "tag", required = false) String tag,
                                           @RequestParam(name = "simCode", required = false) String simCode) {
        IPage<DeviceInfo> pages = deviceInfoService.pageDevices(deviceInfo, query, tag, simCode);

        List<DeviceInfo> deviceInfoList = pages.getRecords();
        List<FacilityDeviceVO> reDeviceInfoVOList = new ArrayList<FacilityDeviceVO>();

        deviceInfoList.forEach(deviceInfo_ -> {
            FacilityDeviceVO facilityDeviceVO = Objects.requireNonNull(BeanUtil.copy(deviceInfo_, FacilityDeviceVO.class));
            facilityDeviceVO.setDeviceFactoryName(DictCache.getValue("device_manufacturer", deviceInfo_.getDeviceFactory()));
            facilityDeviceVO.setEntityCategoryName(entityCategoryClient.getCategoryName(deviceInfo_.getEntityCategoryId()).getData());
            List<DeviceRel> deviceRelList = deviceRelService.getDeviceRelByDeviceId(deviceInfo_.getId());
            if (deviceRelList.size() > 0) {
                FacilityInfo facilityInfo = facilityClient.getFacilityInfoById(deviceRelList.get(0).getEntityId()).getData();
                if (null != facilityInfo && null != facilityInfo.getId()) {
                    facilityDeviceVO.setIsBinded(1);
                    facilityDeviceVO.setRelEntityId(deviceRelList.get(0).getEntityId().toString());
                    facilityDeviceVO.setRelEntityDesc(facilityInfo.getFacilityName() + "(" + facilityInfo.getProjectNo() + ")");
                }
            } else {
                facilityDeviceVO.setIsBinded(0);
                facilityDeviceVO.setRelEntityId("-99");
                facilityDeviceVO.setRelEntityDesc("无");
            }
            QueryWrapper<SimRel> simRelQueryWrapper = new QueryWrapper<SimRel>();
            simRelQueryWrapper.lambda().eq(SimRel::getDeviceId, deviceInfo_.getId());
            SimRel simRel = simRelService.getOne(simRelQueryWrapper);
            if (ObjectUtil.isNotEmpty(simRel)) {
                SimInfo simInfo = simInfoService.getById(simRel.getSimId());
                facilityDeviceVO.setSim(simInfo.getSimCode());
                facilityDeviceVO.setSimId(simRel.getSimId().toString());
                facilityDeviceVO.setSimNumber(simInfo.getSimNumber());
            }
            reDeviceInfoVOList.add(facilityDeviceVO);
        });
        IPage<FacilityDeviceVO> iPage = new Page<>(query.getCurrent(), query.getSize(), pages.getTotal());
        iPage.setRecords(reDeviceInfoVOList);
        return R.data(iPage);
    }

    /**
     * 新增
     */
    @PostMapping("")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "新增设施终端", notes = "传入FacilityDeviceVO")
    @Transactional(rollbackFor = Exception.class)
    @ApiLog(value = "新增设施终端")
    public R save(@Valid @RequestBody FacilityDeviceVO facilityDeviceVO) {
        // 验证入参
        verifyParam(facilityDeviceVO);


        DeviceInfo deviceInfo = Objects.requireNonNull(BeanUtil.copy(facilityDeviceVO, DeviceInfo.class));
        deviceInfo.setDeviceStatus(Long.parseLong(DeviceConstant.DeviceStatus.NO));
        deviceInfo.setDeviceType(deviceInfo.getDeviceType().toUpperCase());

        boolean save = deviceInfoService.saveOrUpdateDeviceInfo(deviceInfo);


        Long id = deviceInfo.getId();

        //保存SIM卡号
        if (StringUtil.isNotBlank(facilityDeviceVO.getSimId())) {
            SimRel simRel = new SimRel();
            simRel.setSimId(Long.parseLong(facilityDeviceVO.getSimId()));
            simRel.setDeviceId(id.toString());
            simRelService.save(simRel);
        }

        //保存ICCID
        if (StringUtil.isNotBlank(facilityDeviceVO.getAuthCode())) {
            DeviceExt deviceExt_ = new DeviceExt();
            deviceExt_.setDeviceId(id);
            deviceExt_.setAttrId(DeviceConstant.DeviceCharSpec.VEHICLE_DEVICE_ICCID);
            deviceExt_.setAttrName("鉴权码");
            deviceExt_.setAttrValue(facilityDeviceVO.getAuthCode());
            deviceExt_.setAttrDisplayValue(facilityDeviceVO.getAuthCode());
            deviceExtService.save(deviceExt_);
        }


        if (null != facilityDeviceVO.getDeviceChannelList() && facilityDeviceVO.getDeviceChannelList().size() > 0) {
            facilityDeviceVO.getDeviceChannelList().forEach(deviceChannel -> {
                deviceChannel.setDeviceId(id.toString());
                deviceChannelService.save(deviceChannel);
            });
        }
        return R.status(save);
    }


    /**
     * 更新设施基本信息表
     */
    @PutMapping("")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新设施终端", notes = "传入设施DeviceVO")
    @Transactional(rollbackFor = Exception.class)
    @ApiLog(value = "更新设施终端")
    public R put(@Valid @RequestBody FacilityDeviceVO facilityDeviceVO) {
        //提交时前端保证设备渠道名称，编号唯一。

        // 验证入参
        verifyParamForUpdate(facilityDeviceVO);

        // 验证入参
        verifyParam(facilityDeviceVO);

        DeviceInfo deviceInfo = Objects.requireNonNull(BeanUtil.copy(facilityDeviceVO, DeviceInfo.class));
        deviceInfo.setDeviceType(deviceInfo.getDeviceType().toUpperCase());

        boolean save = deviceInfoService.saveOrUpdateDeviceInfo(deviceInfo);


        Long id = facilityDeviceVO.getId();

        //新增或更新SIM
        QueryWrapper<SimRel> simRelQueryWrapper = new QueryWrapper<SimRel>();
        simRelQueryWrapper.lambda().eq(SimRel::getDeviceId, id);
        if (StringUtil.isBlank(facilityDeviceVO.getSimId())) {
            simRelService.remove(simRelQueryWrapper);
        } else {

            SimRel simRel = simRelService.getOne(simRelQueryWrapper);
            if (ObjectUtil.isNotEmpty(simRel)) {
                if (!facilityDeviceVO.getSimId().equals(simRel.getSimId())) {
                    simRel.setSimId(Long.parseLong(facilityDeviceVO.getSimId()));
                    simRelService.updateById(simRel);
                }
            } else {
                SimRel simRel1 = new SimRel();
                simRel1.setSimId(Long.parseLong(facilityDeviceVO.getSimId()));
                simRel1.setDeviceId(id.toString());
                simRelService.save(simRel1);
            }
        }

        DeviceExt deviceExt_ = new DeviceExt();
        deviceExt_.setDeviceId(id);
        deviceExt_.setAttrId(DeviceConstant.DeviceCharSpec.VEHICLE_DEVICE_ICCID);
        List<DeviceExt> deviceExtList_ = deviceExtService.getExtInfoByParam(deviceExt_);
        if (StringUtil.isNotBlank(facilityDeviceVO.getAuthCode())) {
            //新增或更新ICCID
            if (deviceExtList_.size() > 0) {
                deviceExt_.setId(deviceExtList_.get(0).getId());
            }
            deviceExt_.setAttrName("鉴权码");
            deviceExt_.setAttrValue(facilityDeviceVO.getAuthCode());
            deviceExt_.setAttrDisplayValue(facilityDeviceVO.getAuthCode());
            deviceExtService.saveOrUpdate(deviceExt_);
        } else {
            //删除ICCID
            if (deviceExtList_.size() > 0) {
                deviceExtService.removeById(deviceExtList_.get(0).getId());
            }
        }

        List<DeviceChannel> deviceChannels = deviceChannelService.getChannelInfoByDeviceId(id);
        List<DeviceChannel> currentDeviceChannels = facilityDeviceVO.getDeviceChannelList();
        if (null != deviceChannels && deviceChannels.size() > 0) {
            List<Long> channelIds = new ArrayList<Long>();
            deviceChannels.forEach(deviceChannel -> {
                channelIds.add(deviceChannel.getId());
            });
            if (null != facilityDeviceVO.getDeviceChannelList() && facilityDeviceVO.getDeviceChannelList().size() > 0) {

                //数据库有，页面有
                //0.过滤数据库有的，但是页面上没有了
                currentDeviceChannels.forEach(deviceChannelVO -> {
                    channelIds.remove(deviceChannelVO.getId());
                });
                //1.把数据库有的，但是页面上没有了的删了
                if (channelIds.size() > 0) {
                    deviceChannelService.removeByIds(channelIds);
                }
                //2.再把页面上的数据新增或更新
                currentDeviceChannels.forEach(deviceChannelVO -> {
                    deviceChannelVO.setDeviceId(id.toString());
                    deviceChannelService.saveOrUpdate(deviceChannelVO);
                });
            } else {
                //数据库有，页面没有-> 所有删除
                deviceChannelService.removeByIds(channelIds);
            }
        } else if (null != currentDeviceChannels && currentDeviceChannels.size() > 0) {
            //数据库没有，页面有-> 所有新增
            currentDeviceChannels.forEach(deviceChannelVO -> {
                deviceChannelVO.setDeviceId(id.toString());
                deviceChannelService.save(deviceChannelVO);
            });
        }
        return R.status(save);
    }

    /**
     * 删除设施终端
     */
    @DeleteMapping("")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "批量删除设施终端", notes = "传入设备ID列表")
    @Transactional(rollbackFor = Exception.class)
    @ApiLog(value = "批量删除设施终端")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(deviceInfoService.batchRemove(Func.toLongList(ids)));

    }

    /**
     * 查询设施终端基本信息表
     */
    @GetMapping("")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "查询设施终端", notes = "传入设备ID列表")
    @Transactional(rollbackFor = Exception.class)
    @ApiLog(value = "查询设施终端")
    public R<FacilityDeviceVO> get(@ApiParam(value = "主键", required = true) @RequestParam String id) {
        DeviceInfo deviceInfo = deviceInfoService.getById(id);
        if (null == deviceInfo) {
            throw new ServiceException("该设备不存在");
        }
        FacilityDeviceVO FacilityDeviceVO = generateVO(deviceInfo);

        return R.data(FacilityDeviceVO);
    }

    /**
     * 根据CODE查询车辆终端基本信息表
     */
    @GetMapping("/byCode")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据Code参数精准查询设施终端", notes = "传入DeviceCode")
    @Transactional(rollbackFor = Exception.class)
    @ApiLog(value = "根据Code参数精准查询设施终端")
    public R<FacilityDeviceVO> getByParam(@ApiParam(value = "主键", required = true) @RequestParam String code) {
        DeviceInfo deviceInfo = deviceInfoService.getDeviceInfoByCode(code);
        if (null == deviceInfo) {
            throw new ServiceException("该设备不存在");
        }
        FacilityDeviceVO vehicleDeviceVO = generateVO(deviceInfo);
        return R.data(vehicleDeviceVO);
    }


    private FacilityDeviceVO generateVO(DeviceInfo deviceInfo) {
        FacilityDeviceVO facilityDeviceVO = Objects.requireNonNull(BeanUtil.copy(deviceInfo, FacilityDeviceVO.class));
        facilityDeviceVO.setEntityCategoryName(entityCategoryClient.getCategoryName(facilityDeviceVO.getEntityCategoryId()).getData());


        QueryWrapper<SimRel> simRelQueryWrapper = new QueryWrapper<SimRel>();
        simRelQueryWrapper.lambda().eq(SimRel::getDeviceId, deviceInfo.getId());
        SimRel simRel = simRelService.getOne(simRelQueryWrapper);
        if (ObjectUtil.isNotEmpty(simRel)) {
            SimInfo simInfo = simInfoService.getById(simRel.getSimId());
            facilityDeviceVO.setSim(simInfo.getSimCode());
            facilityDeviceVO.setSimId(simRel.getSimId().toString());
        }

        DeviceExt deviceExt_ = new DeviceExt();
        deviceExt_.setDeviceId(facilityDeviceVO.getId());
        deviceExt_.setAttrId(DeviceConstant.DeviceCharSpec.VEHICLE_DEVICE_ICCID);
        List<DeviceExt> deviceExtList_ = deviceExtService.getExtInfoByParam(deviceExt_);
        if (deviceExtList_.size() > 0) {
            facilityDeviceVO.setAuthCode(deviceExtList_.get(0).getAttrValue());
        }

        List<DeviceChannel> deviceChannelList = deviceChannelService.getChannelInfoByDeviceId(deviceInfo.getId());
        if (deviceChannelList.size() > 0) {
            facilityDeviceVO.setDeviceChannelList(deviceChannelList);
        }
        return facilityDeviceVO;
    }


    private void verifyParamForUpdate(@Valid FacilityDeviceVO FacilityDeviceVO) {
        if (FacilityDeviceVO.getId() == null) {
            // 需要输入设备ID
            throw new ServiceException("设备Id不能为空");
        }

        if (null == deviceInfoService.getById(FacilityDeviceVO.getId())) {
            // 需要输入设备ID
            throw new ServiceException("该设备不存在");
        }
    }

    private void verifyParam(@Valid FacilityDeviceVO facilityDeviceVO) {

        if (StringUtils.isBlank(facilityDeviceVO.getDeviceCode())) {
            // "需要输入终端编码"
            throw new ServiceException("终端编码不能为空");
        } else {
            //调用feign接口过滤掉租户信息
            List<DeviceInfo> deviceInfoList = deviceClient.getDeviceByCode(facilityDeviceVO.getDeviceCode()).getData();
            if(ObjectUtil.isNotEmpty(deviceInfoList)&&deviceInfoList.size()>0){
                if (null == facilityDeviceVO.getId() || !facilityDeviceVO.getId().equals(deviceInfoList.get(0).getId())) {
                    throw new ServiceException("终端编码不可用,请修改");
                }
            }
        }


        if (StringUtils.isBlank(facilityDeviceVO.getDeviceName())) {
            // "需要输入终端名称"
            throw new ServiceException("终端名称不能为空");
        } else {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setDeviceName(facilityDeviceVO.getDeviceName());
            List<DeviceInfo> deviceInfoList = deviceInfoService.listDevicesByParam(deviceInfo);
            if (deviceInfoList.size() > 0) {
                if (null == facilityDeviceVO.getId() || !facilityDeviceVO.getId().equals(deviceInfoList.get(0).getId())) {
                    throw new ServiceException("终端名称已存在,请修改");
                }
            }
        }
        if (facilityDeviceVO.getEntityCategoryId() == null) {
            // 需要输入终端类型
            throw new ServiceException("终端类型不能为空");
        }
        if (StringUtil.isBlank(facilityDeviceVO.getDeviceFactory())) {
            // 需要输入生产厂家
            throw new ServiceException("生产厂家不能为空");
        }
        if (StringUtil.isBlank(facilityDeviceVO.getDeviceType())) {
            // 需要输入设备型号
            throw new ServiceException("设备型号不能为空");
        }
        if (StringUtil.isBlank(facilityDeviceVO.getDeviceCode())) {
            // 需要输入设备型号
            throw new ServiceException("设备编码不能为空");
        } else {
            DeviceInfo deviceInfo_ = new DeviceInfo();
            deviceInfo_.setDeviceCode(facilityDeviceVO.getDeviceCode());
            List<DeviceInfo> deviceInfoList_ = deviceInfoService.listDevicesByParam(deviceInfo_);
            if (deviceInfoList_.size() > 0) {
                if (null == facilityDeviceVO.getId() || !facilityDeviceVO.getId().equals(deviceInfoList_.get(0).getId())) {
                    throw new ServiceException("终端编码已存在,请修改");
                }
            }
        }

//        if (StringUtil.isBlank(facilityDeviceVO.getSim())) {
//        //    if(!DeviceConstant.DeviceCategory.FACILiTY_CVR_MONITOR_DEVICE.equals(facilityDeviceVO.getEntityCategoryId()) &&
//          //          !DeviceConstant.DeviceCategory.FACILiTY_NVR_MONITOR_DEVICE.equals(facilityDeviceVO.getEntityCategoryId()) ){
//                // 需要输入设备SIM
//                throw new ServiceException("终端SIM卡号不能为空");
//           // }
//        }

    }
}
