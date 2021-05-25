package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.omnic.entity.OmnicVehicleInfo;
import com.ai.apac.smartenv.omnic.entity.PicStatus;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.websocket.common.GetVehiclePositionDTO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleDetailVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorInfoVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorVO;
import org.springblade.core.tool.api.R;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author qianlong
 * @Description 车辆服务
 * @Date 2020/2/16 4:22 下午
 **/
public interface IVehicleService {

    PicStatus getPicStatusByVehicleId(Long vehicleId);

    /**
     * 向客户端推送车辆状态统计信息
     *
     * @param websocketTask
     */
    void pushVehicleStatus(WebsocketTask websocketTask);

    /**
     * 向客户端实时推送车辆位置信息
     * @param websocketTask
     */
    void pushVehiclePosition(WebsocketTask websocketTask);

    /**
     * 根据状态向客户端实时推送当前所有车辆位置信息
     * @param websocketTask
     */
    void pushVehiclePositionByStatus(WebsocketTask websocketTask);

    /**
     * 向客户端实时推送当前车辆的详细信息
     * @param websocketTask
     */
    void pushVehicleDetail(WebsocketTask websocketTask);

    /**
     * 向客户端实时推送当前车辆的运行轨迹
     * @param websocketTask
     */
    void pushVehicleTrackRealTime(WebsocketTask websocketTask);

    /**
     * 根据车辆ID获取驾驶员
     * @param vehicleId
     * @return
     */
    Future<Person> getVehicleDriver(String vehicleId);

    /**
     * 根据车辆ID获取车辆信息
     * @param vehicleId
     * @return
     */
    Future<VehicleInfo> getVehicleById(String vehicleId);

    /**
     * 根据租户获取当前车辆状态
     * @param tenantId
     * @return
     */
    Future<StatusCount> getStatusCount(String tenantId);

    /**
     * 根据状态查询对应的车辆信息
     * @param status
     * @param tenantId
     * @return
     */
    Future<List<OmnicVehicleInfo>> getVehicleByStatus(Integer status,String tenantId);



    Future<List<String>> getVehicleByWorkareaIdsAndStatus(String tenantId);


    Future<List<String>> getVehicleEasyVList(List<String> tenantIds);

    List<VehicleMonitorInfoVO> getVehicleMonitorInfo(List<Long> vehicleIdList, BaiduMapUtils.CoordsSystem coordsSystem);

    /**
     * 根据车辆ID构建VO信息
     * @param deviceId
     * @param coordsSystem
     * @return
     */
    VehicleMonitorInfoVO getVehicleMonitorInfo(String deviceId, BaiduMapUtils.CoordsSystem coordsSystem);

    /**
     * 根据车辆ID获取实时信息
     * @param vehicleId
     * @param tenantId
     * @param coordsSystem
     * @return
     */
    VehicleDetailVO getVehicleDetailRealTime(Long vehicleId, String tenantId, BaiduMapUtils.CoordsSystem coordsSystem);

    VehicleDetailVO getVehicleStatusRealTime(Long vehicleId, String tenantId);


    List<VehicleInfo> getVehicleInfoByRegionId(Long regionId);

    R<VehicleMonitorVO> getVehiclesPosition(GetVehiclePositionDTO getVehiclePositionDTO);

    VehicleDetailVO getVehicleDetail(String vehicleId);
}
