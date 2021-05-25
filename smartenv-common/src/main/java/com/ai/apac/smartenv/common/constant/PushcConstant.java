package com.ai.apac.smartenv.common.constant;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/6/23 4:34 下午
 **/
public interface PushcConstant {

    String MONGODB_NOTICE_INFO = "notice_info";

    /**
     * 发送渠道
     */
    interface NoticeChannel {
        Integer EMAIL = 1;
        Integer WECHAT_MP = 2;
        Integer SMS = 3;
    }

    /**
     * 发送状态
     */
    interface SendStatus{
        Integer PENDING = 1;
        Integer SUCCESS = 2;
        Integer FAIL = 3;
    }
}
