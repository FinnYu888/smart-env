package com.ai.apac.smartenv.common.constant;

/**
 * @ClassName EventConstant
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/11 17:34
 * @Version 1.0
 */
public interface EventConstant {

    interface BUTTONS {
        Integer BUTTON_1= 1;//重新指派
        Integer BUTTON_2 = 2;//整改
        Integer BUTTON_3 = 3;//编辑
        Integer BUTTON_4 = 4;//检查
    }

    //设施类型
    interface Event_Status {
        Integer HANDLE_1= 1;//处理中
        Integer HANDLE_2 = 2;//待检查
        Integer HANDLE_3 = 3;//已检查
    }

    //公众事件状态
    interface PublicEventStatus {
        Integer HANDLE_0= 0;//待处理
        Integer HANDLE_1= 1;//处理中
        Integer HANDLE_2 = 2;//已处理
        Integer HANDLE_3 = 3;//已取消
    }

    interface Event_Kpi_Tpl_Status {
        Integer LIVE= 1;//已激活
        Integer DRAFT = 2;//草稿
    }

    interface Event_Result {
        Integer SUCCESS= 1;//检查合格
        Integer FAILED = 2;//检查不合格
    }

    interface  MediumDetailType {
        Integer PRE_CHECK = 1; // 整改前
        Integer AFTER_CHECK = 2; //整改后
    }

    interface Type {
        Integer ASSIGN= 1;//检查合格
        Integer CHECK = 2;//检查不合格
    }

    //设施类型
    interface Event_LEVEL {
        String LEVEL_1= "1";//紧急
        String LEVEL_2 = "2";//普通
    }
    //事件mongo存储对象
    public final String EVENT_MONGO_NAME = "eventinfo_";
}
