package com.ai.apac.smartenv.common.constant;

/**
 * WebSocket常量
 *
 * @author qianlong
 */
public interface WebSocketConsts {
    String PUSH_SERVER = "/topic/server";

    String PUSH_ALARM_COUNT = "/topic/alarmCount";

    String PUSH_NOTIFICATION_COUNT = "/topic/notificationCount";

    String PUSH_VEHICLE_MONITOR = "/topic/vehicle/monitor";

    String PUSH_PERSON_MONITOR = "/topic/person/monitor";

    String PUSH_HOME_DATA_COUNT = "/topic/home/dataCount";

    String PUSH_HOME_LAST10_ALARM = "/topic/home/last10Alarm";

    String PUSH_HOME_PAGE = "/topic/home";

    String PUSH_TEST_PAGE = "/topic/test";


    String PUSH_BIGSCREEN_PAGE = "/topic/bigScreen";

    String PUSH_MESSAGE = "/topic/message";

    String PUSH_NOTIFICATION = "/topic/notification";

    String PUSH_BIGSCREEN_ENTITYS = "/topic/pushBigscreenEntitys";

    String PUSH_POLYMERIZATION_ENTITYS = "/topic/pushPolymerizationEntitys";


    interface CacheNames {
        //默认过期时间24小时,单位是秒
        Long EXPIRE_TIME = 86400L;

        String CACHE_PREFIX = "smartenv:ws";

        String SESSION_USER = CACHE_PREFIX + ":sessionId";

//        @Deprecated
//        String USER_WEBSOCKET_TASK = CACHE_PREFIX + ":userTask";

        String EVENT_WEBSOCKET_TASK = CACHE_PREFIX + ":eventTask";
        String EVENT_WEBSOCKET_EASYV = CACHE_PREFIX + ":EasyV";

        String NOTIFICATION_CHANNEL = CACHE_PREFIX + "notification";
    }

    interface WebsocketTaskStatus {

        /**
         * 已创建,等待执行
         */
        int CREATED = 1;

        /**
         * 执行中
         */
        int PROCESSING = 2;

        /**
         * 已结束
         */
        int FINISH = 3;
    }

    interface NotificationLevel {
        String INFO = "info";
        String WARNING = "warning";
    }

    interface NotificationPath {
        String VEHICLE_HISTORY_TRACK = "car-history-track";
        String PERSON_HISTORY_TRACK = "person-history-track";
        String ATTANCEDENT = "attendanceExport";
        String ALARM_INFO = "alarm-detail";
        String EVENT_INFO = "event-detail";
    }

    interface NotificationCategory {
        String ALARM = "alarm";
        String EVENT = "event";
        String TASK = "task";
    }

    interface NotificationPathType {
        Integer INNER_LINK = 1;
        Integer EXTERNAL_LINK = 2;
    }


    interface TaskType {

        String pushPersonPositionAction = "person.pushPersonPositionAction";
        String pushVehiclePositionAction = "vehicle.pushVehiclePositionAction";

        String pushHomeLast10AlarmAction = "home.getLast10Alarm";
        String pushHomeLast10EventAction = "home.getLast10Event";
        String pushHomeDataCountDailyAction = "home.getDataCountDaily";
        String pushHomeLast30GarbageAction = "home.getLast30Garbage";
        String pushHomeLast6OrderAction = "home.getLast6Order";

        String pushMonitorEventAction = "event.pushMonitorEventAction";
        String pushMonitorAshcanAction = "ashcan.pushMonitorAshcanAction";
        String pushMonitorTransferStationAction = "transferStation.pushMonitorTransferStationAction";
        String pushMonitorToiletAction = "toilet.pushMonitorToiletAction";
    }

    /**
     * 聚合类型
     */
    interface PolymerizationType {
        /**
         * 车
         */
        Integer VEHICLE = 1;
        /**
         * 人
         */
        Integer PERSON = 2;
        /**
         * 中转站
         */
        Integer TRANSFER_STATION = 3;
        /**
         * 事件
         */
        Integer EVENT = 4;
        /**
         * 垃圾桶
         */
        Integer ASHCAN = 5;
        /**
         * 公厕
         */
        Integer PUBLIC_TOILET = 6;

    }

    interface PolymerizationIcon{
        String VEHICLE_ALARM="static/polymerization/vehicle-alarm.png";
        String VEHICLE_NORMAL="static/polymerization/vehicle-normal.png";
        String ASHCAN_ALARM ="static/polymerization/ashcan-alarm.png";
        String ASHCAN_NORMAL ="static/polymerization/ashcan-normal.png";
        String PERSON_ALARM="static/polymerization/person-alarm.png";
        String PERSON_NORMAL="static/polymerization/person-normal.png";
        String EVENT="static/polymerization/event.png";
        String TRANSFER_ALARM="static/polymerization/transfer-alarm.png";
        String TRANSFER_NORMAL="static/polymerization/transfer-normal.png";
        String WC_NORMAL="static/polymerization/wc-normal.png";
        String WC_MAINTENANCE="static/polymerization/wc-maintenance.png";


    }


}
