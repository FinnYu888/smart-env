package com.ai.apac.smartenv.websocket.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface IWebsocketConsumer {


//
//    String POLYMERIZATION_VEHICLE_CHANGE_INPUT = "polymerization_vehicle_change_input";
//    String POLYMERIZATION_PERSON_CHANGE_INPUT = "polymerization_person_change_input";
    String POLYMERIZATION_ALARM_AMOUNT_UPDATE_INPUT = "polymerization_alarm-amount_update_input";


    /*-----------首页websockrt的消费者------------*/
    String HOME_COUNT_UPDATE_INPUT = "home_count_update_input";

    String HOME_EVENT_LIST_UPDATE_INPUT = "home_event_list_update_input";

    String HOME_ALARM_LIST_UPDATE_INPUT = "home_alarm_list_update_input";

    String HOME_ORDER_LIST_UPDATE_INPUT = "home_order_list_update_input";

    String HOME_GARBAGE_AMOUNT_UPDATE_INPUT = "home_garbage_amount_update_input";


    /*-----------大屏websockrt的消费者------------*/
    String BIGSCREEN_COUNT_UPDATE_INPUT = "bigscreen_count_update_input";

    String BIGSCREEN_GARBAGE_AMOUNT_BY_REGION_UPDATE_INPUT = "bigscreen_garbage-amount-by-region_update_input";

    String BIGSCREEN_LAST_GARBAGE_AMOUNT_UPDATE_INPUT = "bigscreen_last-garbage-amount_update_input";

    String BIGSCREEN_ALL_RULE_ALARM_AMOUNT_UPDATE_INPUT = "bigscreen_all-rule-alarm-amount_update_input";

    String BIGSCREEN_LAST_ALARM_UPDATE_INPUT = "bigscreen_last-alarm_update_input";

    String BIGSCREEN_EVENT_COUNT_BY_TYPE_UPDATE_INPUT = "bigscreen_event-count-by-type_update_input";

    /*-----------Websocket监听事件的消费者------------*/
    String WEBSOCKET_MONITOR_EVENT = "websocket-monitor-event-input";

//    @Input(POLYMERIZATION_VEHICLE_CHANGE_INPUT)
//    SubscribableChannel polymerizationVehicleChangeInput();
//
//
//    @Input(POLYMERIZATION_PERSON_CHANGE_INPUT)
//    SubscribableChannel polymerizationPersonChangeInput();


    @Input(POLYMERIZATION_ALARM_AMOUNT_UPDATE_INPUT)
    SubscribableChannel polymerizationAlarmAmountUpdateInput();

    /*-----------首页websockrt的消费者------------*/
    @Input(HOME_COUNT_UPDATE_INPUT)
    SubscribableChannel homeCountUpdateInput();

    @Input(HOME_EVENT_LIST_UPDATE_INPUT)
    SubscribableChannel homeEventListUpdateInput();

    @Input(HOME_ALARM_LIST_UPDATE_INPUT)
    SubscribableChannel homeAlarmListUpdateInput();

    @Input(HOME_ORDER_LIST_UPDATE_INPUT)
    SubscribableChannel homeOrderListUpdateInput();

    @Input(HOME_GARBAGE_AMOUNT_UPDATE_INPUT)
    SubscribableChannel homeGarbageAmountUpdateInput();

    /*-----------大屏websockrt的消费者------------*/
    @Input(BIGSCREEN_COUNT_UPDATE_INPUT)
    SubscribableChannel bigScreenCountUpdateInput();

    @Input(BIGSCREEN_GARBAGE_AMOUNT_BY_REGION_UPDATE_INPUT)
    SubscribableChannel bigScreenGarbageAmountByRegionUpdateInput();

    @Input(BIGSCREEN_LAST_GARBAGE_AMOUNT_UPDATE_INPUT)
    SubscribableChannel bigScreenLastGarbageUpdateInput();

    @Input(BIGSCREEN_ALL_RULE_ALARM_AMOUNT_UPDATE_INPUT)
    SubscribableChannel bigScreenAllRuleAlarmAmountUpdateInput();

    @Input(BIGSCREEN_LAST_ALARM_UPDATE_INPUT)
    SubscribableChannel bigScreenLastAlarmUpdateInput();

    @Input(BIGSCREEN_EVENT_COUNT_BY_TYPE_UPDATE_INPUT)
    SubscribableChannel bigScreenEventCountByTypeUpdateInput();

    /**
     * Websocket监听事件
     */
    @Input(WEBSOCKET_MONITOR_EVENT)
    SubscribableChannel  websocketMonitorEvent();
}
