package com.ai.apac.smartenv.common.constant;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: OmnicConstant
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/5
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/5  16:35    panfeng          v1.0.0             修改原因
 */
public interface OmnicConstant {
    String BUCKET = "smartenv";

    /**
     * WebSocket 响应消息类型
     */
    interface WebSocketRespMsgType {
        int CONNECTION_MSG = 1001; //连接消息
        int POSITION_MSG = 1002; //位置消息
        int COUNT_MSG = 1003;//实时数量消息
    }

    interface mongoNmae {
        String WORK_COUNT_TODAY = "GreenScreen_WorkingEntitiesData";
        String GREEN_AREA_TOTAL = "GreenScreen_GreenAreasData";
        String LAST_DAYS_TASK = "GreenScreen_TasksData";
        String DEVICE_DATA = "GreenScreen_DevicesData";
        String WORK_AREA_INFO = "WorkareaInfoBigScreen";
    }

    interface THIRD_INFO_TYPE {
        String PERSON = "1";
        String VEHICLE = "2";
        String FACILITY = "3";
        String WORKAREA = "4";
        String AREA = "5";
        String DEVICE = "6";
        String STATION = "7";
        String DEPT = "8";
        String TOILET = "10";
    }

    interface ACTION_TYPE {
        String NEW = "1";
        String UPDATE = "2";
        String DELETE = "3";
    }


}
