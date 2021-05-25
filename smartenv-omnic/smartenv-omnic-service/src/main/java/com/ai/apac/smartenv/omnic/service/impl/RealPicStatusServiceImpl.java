package com.ai.apac.smartenv.omnic.service.impl;

import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.enums.PersonStatusImgEnum;
import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.common.enums.VehicleStatusImgEnum;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.omnic.entity.PicStatus;
import com.ai.apac.smartenv.omnic.mapper.RealPicStatusMapper;
import com.ai.apac.smartenv.omnic.service.RealPicStatusService;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: RealPicStatusServiceImpl
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/17
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/17  22:51    panfeng          v1.0.0             修改原因
 */
@Service
public class RealPicStatusServiceImpl implements RealPicStatusService {


    @Autowired
    private IVehicleClient vehicleClient;

    @Autowired
    private IPersonClient personClient;
    @Autowired
    private IScheduleClient scheduleClient;
    @Autowired
    private IAlarmInfoClient alarmInfoClient;

    private IDeviceClient deviceClient;


    @Autowired
    private RealPicStatusMapper mapper;

    @Override
    public PicStatus selectVehiclePicStatusById(String vehicle) {
        VehicleInfo data = vehicleClient.getVehicleInfoById(Long.parseLong(vehicle)).getData();
        Boolean needWork = scheduleClient.checkNowNeedWork(Long.parseLong(vehicle), ArrangeConstant.ScheduleObjectEntityType.VEHICLE).getData();
        PicStatus picStatus = new PicStatus();
        picStatus.setEntityId(vehicle);
        Integer count = alarmInfoClient.countNoHandleAlarmInfoByEntity(Long.parseLong(vehicle), CommonConstant.ENTITY_TYPE.VEHICLE).getData();
        if (!needWork) {
            if (count != null && count.intValue() != 0) {

                picStatus.setPicStatus(VehicleStatusImgEnum.OFFLINE_ALARM.getValue());
                return picStatus;

            } else {
                picStatus.setPicStatus(VehicleStatusImgEnum.OFF_LINE.getValue());
                return picStatus;
            }

        } else {
            String deviceStatus = DeviceCache.getDeviceStatus(Long.parseLong(vehicle), VehicleConstant.VEHICLE_ACC_DEVICE_TYPE, data.getTenantId());

            if (count != null && count.intValue() != 0) {
                picStatus.setPicStatus(VehicleStatusImgEnum.ONLINE_ALARM.getValue());
                return picStatus;
            } else {
                if (DeviceConstant.DeviceStatus.ON.equals(deviceStatus)) {
                    picStatus.setPicStatus(VehicleStatusImgEnum.ON_LINE.getValue());
                    return picStatus;
                }
                picStatus.setPicStatus(VehicleStatusImgEnum.ONLINE_ALARM.getValue());
                return picStatus;

            }
        }

//        PicStatus picStatus = mapper.selectVehiclePicStatusById(vehicle);
//        return picStatus;

    }


    @Override
    public PicStatus selectPersonPicStatusById(String personId) {
//        PicStatus picStatus = mapper.selectPersonPicStatusById(personId);
//        return picStatus;
        Person data = personClient.getPerson(Long.valueOf(personId)).getData();
        Boolean needWork = scheduleClient.checkNowNeedWork(Long.parseLong(personId), ArrangeConstant.ScheduleObjectEntityType.PERSON).getData();
        PicStatus picStatus = new PicStatus();
        picStatus.setEntityId(personId);
        Integer count = alarmInfoClient.countNoHandleAlarmInfoByEntity(Long.parseLong(personId), CommonConstant.ENTITY_TYPE.PERSON).getData();
        if (!needWork) {
            if (count != null && count.intValue() != 0) {

                picStatus.setPicStatus(PersonStatusImgEnum.OFFLINE_ALARM.getValue());
                return picStatus;

            } else {
                picStatus.setPicStatus(PersonStatusImgEnum.OFF_LINE.getValue());
                return picStatus;
            }

        } else {
            String deviceStatus = DeviceCache.getDeviceStatus(Long.parseLong(personId), VehicleConstant.PERSON_POSITION_DEVICE_TYPE, data.getTenantId());

            if (count != null && count.intValue() != 0) {
                picStatus.setPicStatus(PersonStatusImgEnum.ONLINE_ALARM.getValue());
                return picStatus;
            } else {
                if (DeviceConstant.DeviceStatus.ON.equals(deviceStatus)) {
                    picStatus.setPicStatus(PersonStatusImgEnum.ON_LINE.getValue());
                    return picStatus;
                }
                picStatus.setPicStatus(PersonStatusImgEnum.ONLINE_ALARM.getValue());
                return picStatus;

            }
        }




    }

}
