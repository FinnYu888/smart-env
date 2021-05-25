package com.ai.apac.smartenv.websocket.mq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Copyright: Copyright (c) 2020/8/19 Asiainfo
 *
 * @ClassName: TaskProducerSource
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  14:32    zhanglei25          v1.0.0             修改原因
 */
public interface WebsocketProducerSource {

    /*-----------首页websockrt的消息生产者------------*/
    String HOME_COUNT_UPDATE_OUTPUT = "home_count_update_output";

    String HOME_EVENT_LIST_UPDATE_OUTPUT = "home_event_list_update_output";

    String HOME_ALARM_LIST_UPDATE_OUTPUT = "home_alarm_list_update_output";

    String HOME_ORDER_LIST_UPDATE_OUTPUT = "home_order_list_update_output";

    String HOME_GARBAGE_AMOUNT_UPDATE_OUTPUT = "home_garbage_amount_update_output";

    /*-----------大屏websockrt的消息生产者------------*/
    String BIGSCREEN_COUNT_UPDATE_OUTPUT = "bigscreen_count_update_output";

    String BIGSCREEN_GARBAGE_AMOUNT_BY_REGION_UPDATE_OUTPUT = "bigscreen_garbage-amount-by-region_update_output";

    String BIGSCREEN_LAST_GARBAGE_UPDATE_OUTPUT = "bigscreen_last-garbage-amount_update_output";

    String BIGSCREEN_ALL_RULE_ALARM_AMOUNT_UPDATE_OUTPUT = "bigscreen_all-rule-alarm-amount_update_output";

    String BIGSCREEN_LAST_ALARM_UPDATE_OUTPUT = "bigscreen_last-alarm_update_output";

    String BIGSCREEN_EVENT_COUNT_BY_TYPE_UPDATE_OUTPUT = "bigscreen_event-count-by-type_update_output";

    /*-----------综合监控websockrt的消息生产者------------*/
    String POLYMERIZATION_ALARM_AMOUNT_UPDATE_OUTPUT = "polymerization_alarm-amount_update_output";

    /*-----------综合监控websockrt的消息生产者------------*/
    //综合监控告警数据已更新
    @Output(POLYMERIZATION_ALARM_AMOUNT_UPDATE_OUTPUT)
    MessageChannel polymerizationAlarmCountUpdateOutput();

    /*-----------首页websockrt的消息生产者------------*/
    //首页统计数据已更新
    @Output(HOME_COUNT_UPDATE_OUTPUT)
    MessageChannel homeCountUpdateOutput();

    //首页事件列表已更新
    @Output(HOME_EVENT_LIST_UPDATE_OUTPUT)
    MessageChannel homeEventListUpdateOutput();

    //首页告警列表已更新
    @Output(HOME_ALARM_LIST_UPDATE_OUTPUT)
    MessageChannel homeAlarmListUpdateOutput();

    //首页任务列表已更新
    @Output(HOME_ORDER_LIST_UPDATE_OUTPUT)
    MessageChannel homeOrderListUpdateOutput();

    //首页垃圾统计数据已更新
    @Output(HOME_GARBAGE_AMOUNT_UPDATE_OUTPUT)
    MessageChannel homeGarbageAmountUpdateOutput();

    /*-----------大屏websockrt的消息生产者------------*/
    @Output(BIGSCREEN_COUNT_UPDATE_OUTPUT)
    MessageChannel bigScreenCountUpdateOutput();

    @Output(BIGSCREEN_GARBAGE_AMOUNT_BY_REGION_UPDATE_OUTPUT)
    MessageChannel bigScreenGarbageAmountByRegionUpdateOutput();

    @Output(BIGSCREEN_LAST_GARBAGE_UPDATE_OUTPUT)
    MessageChannel bigScreenLastGarbageUpdateOutput();

    @Output(BIGSCREEN_ALL_RULE_ALARM_AMOUNT_UPDATE_OUTPUT)
    MessageChannel bigScreenAllRuleAlarmAmountUpdateOutput();

    @Output(BIGSCREEN_LAST_ALARM_UPDATE_OUTPUT)
    MessageChannel bigScreenLastAlarmUpdateOutput();

    @Output(BIGSCREEN_EVENT_COUNT_BY_TYPE_UPDATE_OUTPUT)
    MessageChannel bigScreenEventCountByTypeUpdateOutput();
}
