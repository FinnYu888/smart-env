package com.ai.apac.smartenv.device.feign;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.device.dto.DevicePersonInfoDto;
import com.ai.apac.smartenv.device.entity.*;
import com.ai.apac.smartenv.device.service.*;
import com.ai.apac.smartenv.device.vo.DeviceStatusVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: DeviceFeignImpl
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/8
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/8  16:59    panfeng          v1.0.0             修改原因
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class DeviceClient implements IDeviceClient {

    private IDeviceInfoService deviceInfoService;

    private IDeviceAsyncService deviceAsyncService;


    private IDeviceChannelService deviceChannelService;

    private IDeviceExtService deviceExtService;

    private IDeviceRelService deviceRelService;

    @Override
    public R<Boolean> deviceInfoAsync(@RequestBody List<List<String>> datasList, @RequestParam String tenantId, @RequestParam String actionType) {
        return R.data(deviceAsyncService.thirdDeviceInfoAsync(datasList,tenantId,actionType,true));
    }

    @Override
    @GetMapping(API_GET_DEVICE_BY_ID)
    public R<DeviceInfo> getDeviceById(String id) {
        DeviceInfo detail = deviceInfoService.getById(id);
        return R.data(detail);
    }


    @Override
    @GetMapping(value = API_GET_DEVICES_BY_PARAM)
    public R<List<DeviceInfo>> getDevicesByParam(String ids, Long categoryId) {
        List<DeviceInfo> deviceInfoList = new ArrayList<DeviceInfo>();
        String[] idSet = ids.split(",");
        if (idSet.length > 0) {
            List<String> idList = Arrays.asList(idSet);
            deviceInfoList = deviceInfoService.listDevicesByParam(idList, categoryId);
        }
        return R.data(deviceInfoList);
    }

    @Override
    public R<List<DeviceInfo>> getDevices(DeviceInfo deviceInfo) {
        QueryWrapper<DeviceInfo> deviceInfoQueryWrapper = new QueryWrapper<DeviceInfo>();
        if(ObjectUtil.isNotEmpty(deviceInfo.getEntityCategoryId())){
            deviceInfoQueryWrapper.lambda().eq(DeviceInfo::getEntityCategoryId,deviceInfo.getEntityCategoryId());
        }
        if(ObjectUtil.isNotEmpty(deviceInfo.getDeviceStatus())){
            deviceInfoQueryWrapper.lambda().eq(DeviceInfo::getDeviceStatus,deviceInfo.getDeviceStatus());
        }
        if(ObjectUtil.isNotEmpty(deviceInfo.getTenantId())){
            deviceInfoQueryWrapper.lambda().eq(DeviceInfo::getTenantId,deviceInfo.getTenantId());
        }
        return R.data(deviceInfoService.list(deviceInfoQueryWrapper));
    }

    @Override
    public R<DeviceInfo> saveDeviceInfo(DeviceInfo deviceInfo) {
        deviceInfoService.save(deviceInfo);
        return R.data(deviceInfo);
    }


    @Override
    @GetMapping(API_GET_EXTINFO_BY_DEVICEID)
    public R<List<DeviceExt>> getExtInfoByDeviceId(Long id) {
        return R.data(deviceExtService.getExtInfoByDeviceId(id));
    }

    @Override
    public R<List<DeviceExt>> getExtInfoByParam(Long id, Long attrId) {
        DeviceExt deviceExt = new DeviceExt();
        deviceExt.setDeviceId(id);
        deviceExt.setAttrId(attrId);
        return R.data(deviceExtService.getExtInfoByParam(deviceExt));
    }

    @Override
    @GetMapping(API_GET_ChANNEL_BY_DEVICEID)
    public R<List<DeviceChannel>> getChannelInfoByDeviceId(Long id) {
        return R.data(deviceChannelService.getChannelInfoByDeviceId(id));
    }

    @Override
    public R<List<DeviceStatusVO>> getDeviceStatusByIds(String ids) throws IOException {
        List<DeviceStatusVO> deviceStatusVOList = new ArrayList<DeviceStatusVO>();
        String[] deviceIdSet = ids.split(",");
        if (deviceIdSet.length > 0) {
            for (String deviceId : deviceIdSet) {
                DeviceStatusVO deviceStatusVO = new DeviceStatusVO();
                //调用大数据
                JSONObject param = new JSONObject();
                param.put("deviceId", deviceId);
                String reStr = BigDataHttpClient.getBigDataBody(BigDataHttpClient.getDeviceStatus, param);
                if (StringUtil.isNotBlank(reStr) && JSONUtil.parseObj(reStr).get("code").equals(0)) {
                    JSONObject data = (JSONObject) JSONUtil.parseObj(reStr).get("data");
                    String status = data.getStr("status");
                    String deviceStatus = DeviceConstant.DeviceStatus.NO;
                    if (!StringUtil.isBlank(status)) {
                        deviceStatus = status;
                    }
                    deviceStatusVO.setStatusCode(deviceStatus);
                    deviceStatusVO.setDeviceId(deviceId);
                    deviceStatusVOList.add(deviceStatusVO);
                }
            }
        }
        return R.data(deviceStatusVOList);
    }


    @Override
    @GetMapping(API_GET_DEVICE_BY_CODE)
    public R<List<DeviceInfo>> getDeviceByCode(String deviceCode) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceCode(deviceCode);
        return R.data(deviceInfoService.list(Condition.getQueryWrapper(deviceInfo)));
    }

    @Override
    @PostMapping(value = API_UNBIND_DEVICE)
    public R<Boolean> unbindDevice(Long entityId, Long entityType) {
        return R.data(deviceInfoService.unbindDevice(entityId, entityType));
    }


    @Override
    public R<List<DeviceInfo>> getAllDevice() {
        return R.data(deviceInfoService.selectDeviceList(new DeviceInfo()));
    }

    @Override
    public R<List<DeviceInfo>> getTenantDevice(String tenantId) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setTenantId(tenantId);
        return R.data(deviceInfoService.selectDeviceList(deviceInfo));
    }


    @Override
    @GetMapping(REL_GET_BY_CATEORY)
    public R<DeviceInfo> getByEntityAndCategory(Long entityId, Long entityCategoryId) {
        DeviceRel deviceRel = new DeviceRel();
        deviceRel.setEntityId(entityId);

        QueryWrapper<DeviceRel> queryWrapper = Condition.getQueryWrapper(deviceRel);
        List<DeviceRel> list = deviceRelService.list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            List<String> ids = new ArrayList<>();
            list.forEach(rel -> {
                ids.add(rel.getDeviceId().toString());
            });
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setEntityCategoryId(entityCategoryId);
            QueryWrapper<DeviceInfo> deviceInfoQueryWrapper = Condition.getQueryWrapper(deviceInfo);
            deviceInfoQueryWrapper.in("id", ids);
            List<DeviceInfo> deviceInfos = deviceInfoService.list(deviceInfoQueryWrapper);
            if (CollectionUtil.isNotEmpty(deviceInfos)) {
                for (DeviceInfo deviceinfo : deviceInfos) {
                    if (entityCategoryId.equals(deviceInfo.getEntityCategoryId())) {
                        return R.data(deviceinfo);
                    }
                }
            }
        }
        return R.data(null);
    }


    /**
     * 获取历史轨迹专用的设备绑定记录
     * 查询原则如下：
     * 1、如果查询时间段没有绑定过设备，则取前一次绑定的设备，并且返回的记录中有两条记录，
     * 一条的更新时间为传入的起始时间，一条为结束时间。
     * 2、如果查询时间段内绑定过设备，则查出所有绑定的记录，并加入两条记录，
     * 在返回list的最前面加入一条更新时间为起始时间的记录。最后面加入一条更新时间为结束时间的设备
     * 这样，就可以直接通过返回的list 来拼接大数据的多次查询的条件
     *
     * @param entityId
     * @param entityType
     * @param entityCategoryId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    @GetMapping(REL_GET_FOR_TRACK)
    public R<List<DeviceInfo>> getForTrack(@RequestParam("entityId") Long entityId, @RequestParam("entityType") Long entityType, @RequestParam("entityCategoryId") Long entityCategoryId, @RequestParam("startTime") Long startTime, @RequestParam("endTime") Long endTime) {

        List<DeviceInfo> deviceInfos = new ArrayList<>();
//
//
        if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(entityType)) {

            /*
               TODO 有隐患： 如果是车辆，查询设备的时候需要判断是否为三合一设备。如果为三合一设备，在取定位设备的时候无法知道在指定的时间是否需要取三合一设备。
                这样的话，车辆历史轨迹在查询的时候始终查的是最新绑定的设备，如果设备解绑重新绑定。查绑定之前的历史轨迹查到的就不是当前车辆的历史轨迹，而是当前
                车辆现在绑定的设备在哪个时间的历史轨迹。后期需要想办法改造
                @Asiainfo.panfneg.90246  2020-06-22
             */
            DeviceInfo deviceInfo = this.getByEntityAndCategory(entityId, entityCategoryId).getData();
            if (deviceInfo != null && deviceInfo.getId() != null && DeviceConstant.DeviceFactory.MINICREATE.equals(deviceInfo.getDeviceFactory())) {
                DeviceInfo nvr = this.getByEntityAndCategory(entityId, DeviceConstant.DeviceCategory.VEHICLE_NVR_MONITOR_DEVICE).getData();
                if (nvr != null && nvr.getId() != null && DeviceConstant.DeviceFactory.MINICREATE.equals(nvr.getDeviceFactory())) {
                    deviceInfo = nvr;
                } else {
                    DeviceInfo cvr = this.getByEntityAndCategory(entityId, DeviceConstant.DeviceCategory.VEHICLE_CVR_MONITOR_DEVICE).getData();
                    deviceInfo = cvr;
                }

            }
            if (deviceInfo == null || deviceInfo.getId() == null) {
                return R.data(deviceInfos);

            }
            DeviceInfo copy = BeanUtil.copy(deviceInfo, DeviceInfo.class);
            deviceInfo.setUpdateTime(new Date(startTime));
            copy.setUpdateTime(new Date(endTime));
            deviceInfos.add(deviceInfo);
            deviceInfos.add(copy);

        } else {
            List<DeviceRel> deviceRels = deviceRelService.listForEntityAndTime(entityId, entityType, entityCategoryId, new Timestamp(startTime), new Timestamp(endTime));

            List<DeviceRel> modifyList = deviceRelService.getModifyList(entityId, entityType, entityCategoryId, new Timestamp(endTime));
            if (CollectionUtil.isNotEmpty(modifyList)) {
                DeviceInfo deviceInfo = deviceInfoService.getById(modifyList.get(0).getDeviceId());
                deviceInfo.setUpdateTime(new Date(startTime));
                deviceInfos.add(deviceInfo);
                if (deviceRels.isEmpty()) {
                    DeviceInfo copy = BeanUtil.copy(deviceInfo, DeviceInfo.class);
                    copy.setUpdateTime(new Date(endTime));
                    deviceInfos.add(copy);
                }
            }
            if (CollectionUtil.isNotEmpty(deviceRels)) {
                DeviceInfo last = null;
                for (DeviceRel deviceRel : deviceRels) {
                    Long deviceId = deviceRel.getDeviceId();
                    DeviceInfo deviceInfo = deviceInfoService.getById(deviceId);
                    deviceInfo.setUpdateTime(deviceRel.getUpdateTime());
                    deviceInfos.add(deviceInfo);
                    last = deviceInfo;
                }
                DeviceInfo copy = BeanUtil.copy(last, DeviceInfo.class);
                copy.setUpdateTime(new Date(endTime));
                deviceInfos.add(copy);
            }
        }


        return R.data(deviceInfos);
    }

    @Override
    public R<Map<Long,DevicePersonInfoDto>> getByEntityAndCategoryMap(List<Long> scheduleVehicleIdList, Long personWatchDevice) {
        List<DevicePersonInfo> deviceInfos = deviceInfoService.getByEntityAndCategoryList(scheduleVehicleIdList,personWatchDevice);
        Map<Long,DevicePersonInfoDto> deviceInfoMap  = new HashMap<>();
        if (CollectionUtil.isNotEmpty(deviceInfos)) {
            deviceInfos.forEach(deviceInfo -> {
                DevicePersonInfoDto devicePersonInfoDto = new DevicePersonInfoDto();
                BeanUtil.copyProperties(deviceInfo,devicePersonInfoDto);
                deviceInfoMap.put(deviceInfo.getEntityId(),devicePersonInfoDto);
            });
        }
        return R.data(deviceInfoMap);
    }


    @Override
    public R<Map<Long,DevicePersonInfoDto>> listDeviceByCategoryId(Long categoryId) {
        List<DevicePersonInfo> deviceInfos = deviceInfoService.listDeviceByCategoryId(categoryId);
        Map<Long,DevicePersonInfoDto> deviceInfoMap  = new HashMap<>();
        if (CollectionUtil.isNotEmpty(deviceInfos)) {
            deviceInfos.forEach(deviceInfo -> {
                DevicePersonInfoDto devicePersonInfoDto = new DevicePersonInfoDto();
                BeanUtil.copyProperties(deviceInfo,devicePersonInfoDto);
                deviceInfoMap.put(deviceInfo.getEntityId(),devicePersonInfoDto);
            });
        }
        return R.data(deviceInfoMap);

    }

}
