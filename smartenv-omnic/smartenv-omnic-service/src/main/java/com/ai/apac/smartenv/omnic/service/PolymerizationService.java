package com.ai.apac.smartenv.omnic.service;

import com.ai.apac.smartenv.omnic.dto.DeviceWorkAreaDTO;
import com.ai.apac.smartenv.omnic.dto.SynthInfoDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: PolymerizationService
 * @Description: 数据聚合service
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/11
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/11  17:04    panfeng          v1.0.0             修改原因
 */
public interface PolymerizationService {


    void initSynthInfo(String tenantId);


    @Async
    void initAllPersonDataToMongoDb(Boolean isReload);

    @Async
    void initAllVehicleDataToMongoDb(Boolean isReload);

    @Async
    void initAllFacilityDataToMongoDb();

    Long updateVehicleWorkStatus(List<Long> vehicleIdList, Integer workStatus);

    /**
     * 更新MongoDB中车辆工作状态及设备状态
     * @param vehicleId
     * @param workStatus
     * @param deviceStatus
     * @return
     */
    Long updateVehicleWorkStatus(Long vehicleId, Integer workStatus, Long deviceStatus);

    Integer reloadVehicleInfo(List<Long> vehicleIds);


    Long updatePersonWorkStatus(List<Long> personIdList, Integer status);

    /**
     * 更新MongoDB中人员工作状态及设备状态
     * @param personId
     * @param workStatus
     * @param deviceStatus
     * @return
     */
    Long updatePersonWorkStatus(Long personId, Integer workStatus, Long deviceStatus);

    @Async
    Integer reloadPersonInfo(List<Long> personIdList);
    @Async
    Integer addOrUpdateFacility(String facilityListId, Integer facilityMainType);
    @Async
    Boolean addOrUpdateVehicleList(List<VehicleInfo> vehicleInfoList);

    Long removePersonList(List<Long> personIdList);

    Long removeVehicleList(List<Long> vehicleIdList);

    Long removeFacilityList(List<Long> facilityList);

    @Async
    Boolean addOrUpdatePersonList(List<Person> personList);

    /**
     * 根据大数据侧传过来的设备工作状态,更新应用侧的工作状态
     * @param workAreaType 大数据传过来的区域工作状态
     * @param deviceCode 设备编号
     * @return
     */
    Boolean updateWorkStatus(Integer workAreaType,String deviceCode);

    /**
     * 根据大数据侧传过来的设备列表，批量更新应用侧的工作状态
     * @param deviceList
     * @return
     */
    Boolean batchUpdateWorkStatus(List<DeviceWorkAreaDTO> deviceList);
}
