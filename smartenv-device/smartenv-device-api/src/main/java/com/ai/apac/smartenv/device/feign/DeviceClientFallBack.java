package com.ai.apac.smartenv.device.feign;

import com.ai.apac.smartenv.device.dto.DevicePersonInfoDto;
import com.ai.apac.smartenv.device.entity.DeviceChannel;
import com.ai.apac.smartenv.device.entity.DeviceExt;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.SimInfo;
import com.ai.apac.smartenv.device.vo.DeviceStatusVO;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: DeviceFeignFallBack
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/8
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/8  17:02    panfeng          v1.0.0             修改原因
 */
public class DeviceClientFallBack implements IDeviceClient {

    @Override
    public R<Boolean> deviceInfoAsync(@RequestBody List<List<String>> datasList, @RequestParam String tenantId, @RequestParam String actionType) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<DeviceInfo> getDeviceById(String id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<DeviceInfo>> getDevicesByParam(String ids, Long categoryId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<DeviceInfo>> getDevices(DeviceInfo deviceInfo) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<DeviceInfo> saveDeviceInfo(DeviceInfo deviceInfo) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<DeviceInfo>> getAllDevice() {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<DeviceInfo>> getTenantDevice(String tenantId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<DeviceExt>> getExtInfoByDeviceId(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<DeviceExt>> getExtInfoByParam(Long id, Long attrId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<DeviceChannel>> getChannelInfoByDeviceId(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<DeviceStatusVO>> getDeviceStatusByIds(String ids) throws IOException {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<DeviceInfo>> getDeviceByCode(String deviceCode) {
        return R.fail("获取数据失败");
    }

	@Override
	public R<Boolean> unbindDevice(Long entityId, Long entityType) {
		return R.fail("获取数据失败");
	}

    @Override
    public R<DeviceInfo> getByEntityAndCategory(Long entityId, Long entityCategoryId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<DeviceInfo>> getForTrack(Long entityId, Long entityType, Long entityCategoryId, Long startTime, Long endTime) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Map<Long, DevicePersonInfoDto>> getByEntityAndCategoryMap(List<Long> scheduleVehicleIdList, Long personWatchDevice) {
        return R.data(new HashMap<>());
    }

    @Override
    public R<Map<Long, DevicePersonInfoDto>> listDeviceByCategoryId(Long categoryId) {
        return R.data(new HashMap<>());
    }

}
