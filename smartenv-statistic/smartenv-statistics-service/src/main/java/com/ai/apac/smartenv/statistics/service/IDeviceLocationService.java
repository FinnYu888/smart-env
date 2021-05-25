package com.ai.apac.smartenv.statistics.service;

import com.ai.apac.smartenv.statistics.dto.GetDeviceLocationDTO;
import com.ai.apac.smartenv.statistics.vo.DeviceLocationDetailVO;
import com.ai.apac.smartenv.statistics.vo.DeviceLocationGroupVO;

/**
 * @author qianlong
 * @description 设备定位服务
 * @Date 2021/1/6 12:56 下午
 **/
public interface IDeviceLocationService {

    /**
     * 查询设备实体位置
     *
     * @param getDeviceLocationDTO
     * @return
     */
    DeviceLocationGroupVO listDeviceLocation(GetDeviceLocationDTO getDeviceLocationDTO);

    /**
     * 查询设备
     *
     * @param deviceObjId
     * @param deviceObjType
     * @param coordsSystem
     * @return
     */
    DeviceLocationDetailVO getDeviceLocationDetail(Long deviceObjId, Integer deviceObjType, String coordsSystem);
}
