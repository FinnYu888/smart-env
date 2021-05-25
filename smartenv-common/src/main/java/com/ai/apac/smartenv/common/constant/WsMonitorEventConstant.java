package com.ai.apac.smartenv.common.constant;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/10/27 4:36 下午
 **/
public interface WsMonitorEventConstant {

    interface EventType {

        /**
         * 排班事件
         */
        String ARRANGE_EVENT = "arrangeEvent";

        /**
         * 考核事件
         */
        String ASSESS_EVENT = "assessEvent";

        String GARBAGE_EVENT = "garbageEvent";


        /**
         * 考核事件
         */
        String ORDER_EVENT = "orderEvent";

        /**
         * 告警事件
         */
        String ALARM_EVENT = "alarmEvent";

        /**
         * 车辆工作状态变更事件
         */
        String VEHICLE_WORK_STATUS_EVENT = "vehicleWorkStatusEvent";

        /**
         * 事辆位置变更事件
         */
        String VEHICLE_GPS_EVENT = "vehicleGPSEvent";

        /**
         * 事辆信息变更事件
         */
        String VEHICLE_INFO_EVENT = "vehicleInfoEvent";

        /**
         * 人员工作状态变更事件
         */
        String PERSON_WORK_STATUS_EVENT = "personWorkStatusEvent";

        /**
         * 人员位置变更事件
         */
        String PERSON_GPS_EVENT = "personGPSEvent";

        /**
         * 人员信息变更事件
         */
        String PERSON_INFO_EVENT = "personInfoEvent";

        /**
         * 中转站信息变更事件
         */
        String TRANS_STATION_INFO_EVENT = "transStationInfoEvent";

        /**
         * 中转站工作状态变更事件
         */
        String TRANS_STATION_WORK_STATUS_EVENT = "transStationWorkStatusEvent";

        /**
         * 垃圾桶信息变更事件
         */
        String TRASH_INFO_EVENT = "trashInfoEvent";

        /**
         * 垃圾桶工作状态变更事件
         */
        String TRASH_WORK_STATUS_EVENT = "trashWorkStatusEvent";

        /**
         * 公厕信息变更事件
         */
        String TOILET_INFO_EVENT = "toiletInfoEvent";

        /**
         * 公厕工作状态变更事件
         */
        String TOILET_WORK_STATUS_EVENT = "toiletWorkStatusEvent";

        /**
         * 设备状态变更事件
         */
        String DEVICE_STATUS_EVENT = "deviceStatusEvent";
    }
}

