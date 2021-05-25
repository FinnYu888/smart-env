package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import com.ai.apac.smartenv.websocket.common.PositionDTO;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/2/19 9:25 上午
 **/
public interface IDeviceService {

    /**
     * 根据车辆ID获取车辆绑定的ACC设备信息
     *
     * @param vehicleId
     * @return
     */
    Future<DeviceInfo> getPositionDeviceByVehicle(String vehicleId);

    @Async
    Future<DeviceInfo> getAccDeviceByVehicle(String vehicleId);

    /**
     * 根据人员ID获取人员绑定的手表设备信息
     *
     * @param personId
     * @return
     */
    Future<DeviceInfo> getDeviceByPerson(String personId);

    /**
     * 根据设备编号查询设备当前位置
     *
     * @param deviceCode
     * @param coordsSystem
     * @return
     */
    Future<PositionDTO> getDevicePosition(String deviceCode, BaiduMapUtils.CoordsSystem coordsSystem);

    /**
     * 根据设备号列表批量获取位置信息,返回一个Map，key是deviceCode
     * @param deviceCodes
     * @return
     */
//    Future<HashMap<String,PositionDTO>> batchGetDevicePosition(String deviceCodes);


    Future<HashMap<String, PositionDTO>> batchGetDevicePosition(String deviceCodes, BaiduMapUtils.CoordsSystem coordsSystem);

    /**
     * 根据设备编号、起始时间段查询设备历史轨迹
     *
     * @param deviceCode
     * @return
     */
    Future<List<PositionDTO>> getDeviceTrackPosition(String deviceCode, Date beginTime, Date endTime);

    /**
     * 获取设备今天0点开始到当前时间的历史轨迹
     * @param deviceCode
     * @return
     */
    Future<List<PositionDTO>> getDeviceTrackRealTime(String deviceCode);

    /**
     * 根据设备获取最新信息
     * @param deviceCode
     * @return
     */
    Future<TrackPositionDto> getLastDeviceInfo(String deviceCode);

    /**
     * 根据设备获取最新的行驶信息
     * @param deviceCode
     * @return
     */
    Future<TrackPositionDto.Statistics> getLastDeviceRunInfo(String deviceCode);
}
