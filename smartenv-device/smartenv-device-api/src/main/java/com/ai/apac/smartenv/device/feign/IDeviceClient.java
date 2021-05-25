package com.ai.apac.smartenv.device.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.device.dto.DevicePersonInfoDto;
import com.ai.apac.smartenv.device.entity.DeviceChannel;
import com.ai.apac.smartenv.device.entity.DeviceExt;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.SimInfo;
import com.ai.apac.smartenv.device.vo.DeviceStatusVO;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: DeviceFeign
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/8
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/8  16:59    panfeng          v1.0.0             修改原因
 */
@FeignClient( value = ApplicationConstant.APPLICATION_DEVICE_NAME,
        fallback = DeviceClientFallBack.class
)
public interface IDeviceClient {

    String API_PREFIX = "/client";
    String API_GET_DEVICE_BY_ID = API_PREFIX + "/getDeviceById";
    String API_GET_SIM_BY_DEVICEID = API_PREFIX + "/getSimByDeviceId";
    String API_GET_DEVICES = API_PREFIX + "/getDevices";
    String API_GET_DEVICES_BY_PARAM = API_PREFIX + "/getDevicesByParam";
    String API_GET_EXTINFO_BY_DEVICEID = API_PREFIX + "/getExtInfoByDeviceId";
    String API_GET_EXTINFO_BY_PARAM = API_PREFIX + "/getExtInfoByParam";


    String API_GET_ChANNEL_BY_DEVICEID = API_PREFIX + "/getChannelInfoByDeviceId";
    String API_GET_DEVICE_BY_CODE = API_PREFIX + "/getDeviceByCode";

    String API_GET_DEVICE_STATUS_BY_IDS = API_PREFIX + "/getDeviceStatusByIds";
    String API_UNBIND_DEVICE = API_PREFIX + "/unbindDevice";
    String API_GET_ALL_DEVICE = API_PREFIX + "/getAllDevice";
    String API_GET_TENANT_DEVICE = API_PREFIX + "/getTenantDevice";

    String REL_GET_BY_CATEORY= API_PREFIX + "/rel-by-entity-and-cateory";
    String REL_GET_FOR_TRACK = API_PREFIX + "/rel-list-by-condition-all";
    String REL_GET_BY_CATEORY_MAP = API_PREFIX + "/rel-by-entity-and-cateory-map";
    String LIST_DEVICE_BY_CATEGORY_ID = API_PREFIX + "/list-device-by-category-id";
    String DEVICE_INFO_ASYNC = API_PREFIX + "/device-info-async";

    String SAVE_DEVICE_INFO = API_PREFIX + "/save-device-info";


    @PostMapping(DEVICE_INFO_ASYNC)
    R<Boolean> deviceInfoAsync(@RequestBody List<List<String>> datasList, @RequestParam String tenantId, @RequestParam String actionType);

    @GetMapping(value = API_GET_DEVICE_BY_ID)
    R<DeviceInfo> getDeviceById(@RequestParam("id") String id);

    @GetMapping(value = API_GET_DEVICES_BY_PARAM)
    R<List<DeviceInfo>> getDevicesByParam(@RequestParam("ids") String ids,@RequestParam("categoryId") Long categoryId);

    @GetMapping(value = API_GET_DEVICES)
    R<List<DeviceInfo>> getDevices(@RequestBody DeviceInfo deviceInfo);

    @PostMapping(value = SAVE_DEVICE_INFO)
    R<DeviceInfo> saveDeviceInfo(@RequestBody DeviceInfo deviceInfo);

    @GetMapping(value = API_GET_EXTINFO_BY_DEVICEID)
    R<List<DeviceExt>> getExtInfoByDeviceId(@RequestParam("id") Long id);

    @GetMapping(value = API_GET_EXTINFO_BY_PARAM)
    R<List<DeviceExt>> getExtInfoByParam(@RequestParam("id") Long id,@RequestParam("attrId") Long attrId);

    @GetMapping(value = API_GET_DEVICE_BY_CODE)
    R<List<DeviceInfo>> getDeviceByCode(@RequestParam("deviceCode") String deviceCode);

    @GetMapping(value = API_GET_ALL_DEVICE)
    R<List<DeviceInfo>> getAllDevice();

    @GetMapping(value = API_GET_TENANT_DEVICE)
    R<List<DeviceInfo>> getTenantDevice(@RequestParam("tenantId") String tenantId);

    @GetMapping(value = API_GET_ChANNEL_BY_DEVICEID)
    R<List<DeviceChannel>> getChannelInfoByDeviceId(@RequestParam("id") Long id);

    @GetMapping(value = API_GET_DEVICE_STATUS_BY_IDS)
    R<List<DeviceStatusVO>> getDeviceStatusByIds(@RequestParam("id") String ids) throws IOException;

    @PostMapping(value = API_UNBIND_DEVICE)
	R<Boolean> unbindDevice(@RequestParam("entityId") Long entityId, @RequestParam("entityType") Long entityType);

    @GetMapping(REL_GET_BY_CATEORY)
    R<DeviceInfo> getByEntityAndCategory(@RequestParam("entityId") Long entityId, @RequestParam("entityCategoryId") Long entityCategoryId);

    @GetMapping(REL_GET_FOR_TRACK)
    R<List<DeviceInfo>> getForTrack(@RequestParam("entityId") Long entityId, @RequestParam("entityType") Long entityType, @RequestParam("entityCategoryId") Long entityCategoryId, @RequestParam("startTime") Long startTime, @RequestParam("endTime") Long endTime);
    @PostMapping(REL_GET_BY_CATEORY_MAP)
    R<Map<Long, DevicePersonInfoDto>> getByEntityAndCategoryMap(@RequestBody List<Long> scheduleVehicleIdList, @RequestParam("personWatchDevice")Long personWatchDevice);

    @GetMapping(LIST_DEVICE_BY_CATEGORY_ID)
    R<Map<Long,DevicePersonInfoDto>> listDeviceByCategoryId(@RequestParam Long categoryId);
}
