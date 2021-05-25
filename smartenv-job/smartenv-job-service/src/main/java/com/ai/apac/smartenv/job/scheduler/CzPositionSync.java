package com.ai.apac.smartenv.job.scheduler;

import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.dto.RealTimePositionDTO;
import com.ai.apac.smartenv.omnic.dto.RealTimePositionResp;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.mongodb.client.result.UpdateResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: CzPositionSync
 * @Description: 沧州实时位置同步
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/12/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/12/12  9:45    panfeng          v1.0.0             修改原因
 */
@Component
@AllArgsConstructor
@Slf4j
public class CzPositionSync {
    private MongoTemplate mongoTemplate;


    private IDataChangeEventClient dataChangeEventClient;

    /**
     * 实时同步沧州设备的位置数据。每60秒同步一次
     */
    //@Scheduled(fixedDelay = 300000)
    public void syncCzPositionData() {
        try {
            // 查询出沧州所有的车，人
            Query vehicleQuery = new Query();
            Collection<String> tenantIds = new ArrayList<>();
            tenantIds.add("752224");
            tenantIds.add("991859");
            tenantIds.add("201546");


            vehicleQuery.addCriteria(Criteria.where("tenantId").in(tenantIds));
            List<BasicVehicleInfoDTO> basicVehicleInfoDTOS = mongoTemplate.find(vehicleQuery, BasicVehicleInfoDTO.class);


            Query personQuery = new Query();
            personQuery.addCriteria(Criteria.where("tenantId").in(tenantIds));
            List<BasicPersonDTO> basicPersonDTOS = mongoTemplate.find(personQuery, BasicPersonDTO.class);

            // 用 stream api 将所有的车和人对象的deviceCode 提取出来
            List<String> personDeviceCodes = basicPersonDTOS.stream().filter(basicPersonDTO -> basicPersonDTO.getWatchDeviceCode() != null).map(basicPersonDTO -> basicPersonDTO.getWatchDeviceCode()).collect(Collectors.toList());
            List<String> vehicleDeviceCodes = basicVehicleInfoDTOS.stream().filter(basicVehicleInfoDTO -> basicVehicleInfoDTO.getGpsDeviceCode() != null).map(basicVehicleInfoDTO -> basicVehicleInfoDTO.getGpsDeviceCode()).collect(Collectors.toList());
            personDeviceCodes.addAll(vehicleDeviceCodes);

            // 获取到所有要从大数据查询的设备Code，
            List<String> deviceCodes = personDeviceCodes.stream().distinct().collect(Collectors.toList());


            List<RealTimePositionDTO.Position> positions = deviceCodes.stream().map(deviceCode -> {
                try {
                    Map<String, Object> params = new HashMap<>();
                    params.put("deviceIds", deviceCode);
                    RealTimePositionResp resp = BigDataHttpClient.getBigDataBodyToObjcet(BigDataHttpClient.getPersonCarRealTime,
                            params, RealTimePositionResp.class);// 所有设备实时位置数据
                    if (resp.getData() != null && CollectionUtil.isNotEmpty(resp.getData().getPositions())) {
                        return resp.getData().getPositions().get(0);
                    }
                    return null;

                } catch (IOException e) {
                    return null;
                }

            }).collect(Collectors.toList());
            positions = positions.stream().filter(position -> position != null).collect(Collectors.toList());

            if (CollectionUtil.isEmpty(positions)) {
                return;
            }

            // 将车辆人员对象变映射为Map，map的key为设备Code，value为这个code绑定的所有车和人，方便下面通过设备获取mongo中的对象
            Map<String, List<BasicVehicleInfoDTO>> vehicleDeviceCodeMap = basicVehicleInfoDTOS.stream().filter(basicVehicleInfoDTO -> basicVehicleInfoDTO.getGpsDeviceCode() != null).collect(Collectors.groupingBy(basicVehicleInfoDTO -> basicVehicleInfoDTO.getGpsDeviceCode()));
            Map<String, List<BasicPersonDTO>> personDeviceCodeMap = basicPersonDTOS.stream().filter(basicPersonDTO -> basicPersonDTO.getWatchDeviceCode() != null).collect(Collectors.groupingBy(basicPersonDTO -> basicPersonDTO.getWatchDeviceCode()));


            // 将位置对象映射为map，map的key为设备code，key为位置对象。
            Map<String, RealTimePositionDTO.Position> positionMap = positions.stream().collect(Collectors.toMap(RealTimePositionDTO.Position::getDeviceId, position -> position));
            Set<String> deviceResults = positionMap.keySet();
            // 用于存放需要更新的mongo对象
            List<BasicVehicleInfoDTO> changedPositionVehicleInfo = new ArrayList<>();
            List<BasicPersonDTO> changedPositionPositionInfo = new ArrayList<>();

            Date now = DateUtil.now();
            //遍历从大数据获取到的位置，并找到对应的车辆/人员，更新位置
            for (String key : deviceResults) {
                RealTimePositionDTO.Position position = positionMap.get(key);
                List<BasicVehicleInfoDTO> deviceCodeVehicleDto = vehicleDeviceCodeMap.get(key);// 先查看车辆中是否有这个设备，如果有，更新对应车辆的位置
                if (CollectionUtil.isNotEmpty(deviceCodeVehicleDto)) {
                    deviceCodeVehicleDto.forEach(vehicleDevicedto -> {
//                        vehicleDevicedto.setLat(position.getLat());
//                        vehicleDevicedto.setLng(position.getLng());
//                        vehicleDevicedto.setUpdateTime(now);

                        if (vehicleDevicedto.getLat().equals(position.getLat()) && vehicleDevicedto.getLng().equals(position.getLng())) {
                            return;
                        }
                        //更新到mongo
                        Query updateQuery = new Query();
                        updateQuery.addCriteria(Criteria.where("id").is(vehicleDevicedto.getId()));
                        Update update = Update.update("lat", position.getLat()).set("lng", position.getLng()).set("updateTime", now);
                        UpdateResult updateResult = mongoTemplate.updateMulti(updateQuery, update, BasicVehicleInfoDTO.class);
                        log.info("沧州车辆位置更新：" + vehicleDevicedto.getId() + "---- updateCount：" + updateResult.getModifiedCount());
                        BaseWsMonitorEventDTO wsMonitorEvent=new BaseWsMonitorEventDTO(WsMonitorEventConstant.EventType.VEHICLE_GPS_EVENT,vehicleDevicedto.getTenantId(),null,vehicleDevicedto.getId());
                        dataChangeEventClient.doWebsocketEvent(wsMonitorEvent);
                    });
                    changedPositionVehicleInfo.addAll(deviceCodeVehicleDto);
                    continue; // 如果车辆已经更新，不判断人员了。因为一个设备只能绑定到车辆或者人员中的一个
                }
                List<BasicPersonDTO> deviceCocePersonDto = personDeviceCodeMap.get(key);
                if (CollectionUtil.isNotEmpty(deviceCocePersonDto)) {
                    deviceCocePersonDto.forEach(personDeviceDto -> {
                        if (personDeviceDto.getLat().equals(position.getLat()) && personDeviceDto.getLng().equals(position.getLng())) {
                            return;
                        }
                        Query updateQuery = new Query();
                        updateQuery.addCriteria(Criteria.where("id").is(personDeviceDto.getId()));
                        Update update = Update.update("lat", position.getLat()).set("lng", position.getLng()).set("updateTime", now);
                        UpdateResult updateResult = mongoTemplate.updateMulti(updateQuery, update, BasicPersonDTO.class);
                        log.info("沧州人员位置更新：" + personDeviceDto.getId() + "---- updateCount：" + updateResult.getModifiedCount());

                        BaseWsMonitorEventDTO wsMonitorEvent=new BaseWsMonitorEventDTO(WsMonitorEventConstant.EventType.VEHICLE_GPS_EVENT,personDeviceDto.getTenantId(),null,personDeviceDto.getId());
                        dataChangeEventClient.doWebsocketEvent(wsMonitorEvent);


                    });
                    changedPositionPositionInfo.addAll(deviceCocePersonDto);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
