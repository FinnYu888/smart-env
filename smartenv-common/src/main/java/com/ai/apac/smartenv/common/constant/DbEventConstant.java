package com.ai.apac.smartenv.common.constant;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/10/27 4:36 下午
 **/
public interface DbEventConstant {

    interface EventType {

        /**
         * 排班事件
         */
        String ARRANGE_EVENT = "arrangeEvent";

        /**
         * 巡查事件
         */
        String INSPECT_EVENT = "inspectEvent";

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
         * 事辆信息变更事件(包括新增/修改)
         */
        String NEW_OR_UPDATE_VEHICLE_EVENT = "newOrUpdateVehicleEvent";

        /**
         * 删除事辆信息事件
         */
        String REMOVE_VEHICLE_EVENT = "removeVehicleEvent";

        /**
         * 事辆更新或删除事件
         */
        String UPDATE_OR_DEL_VEHICLE_EVENT = "updateOrDelVehicleEvent";

        /**
         * 人员工作状态变更事件
         */
        String PERSON_WORK_STATUS_EVENT = "personWorkStatusEvent";

        /**
         * 人员位置变更事件
         */
        String PERSON_GPS_EVENT = "personGPSEvent";

        /**
         * 人员信息变更事件(包含新增/变更)
         */
        String NEW_OR_UPDATE_PERSON_EVENT = "newPersonEvent";

        /**
         * 删除人员信息变更事件
         */
        String REMOVE_PERSON_EVENT = "removePersonEvent";

        /**
         * 中转站信息变更事件(包含新增/修改)
         */
        String NEW_OR_UPDATE_TRANS_STATION_EVENT = "newOrUpdateTransStationEvent";

        /**
         * 删除中转站信息变更事件
         */
        String REMOVE_TRANS_STATION_EVENT = "removeTransStationEvent";

        /**
         * 中转站工作状态变更事件
         */
        String TRANS_STATION_WORK_STATUS_EVENT = "transStationWorkStatusEvent";

        /**
         * 垃圾桶信息变更事件(包含新增/修改)
         */
        String NEW_OR_UPDATE_TRASH_INFO_EVENT = "newOrUpdateTrashEvent";

        /**
         * 垃圾桶信息删除事件
         */
        String REMOVE_TRASH_EVENT = "removeTrashEvent";

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
         * 车辆ACC状态变更事件
         */
        String VEHICLE_ACC_STATUS_EVENT = "vehicleAccStatusEvent";

        /**
         * 手表设备状态变更事件
         */
        String PERSON_WATCH_STATUS_EVENT = "personWatchStatusEvent";
    }
}

