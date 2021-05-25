package com.ai.apac.smartenv.common.constant;

/**
 * @ClassName MessageConstant
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/9 14:46
 * @Version 1.0
 */
public interface MessageConstant {
    String BUCKET = "smartenv";

    public interface MessageType{
        String ALARM_MESSAGE = "1";
        String EVENT_MESSAGE = "2";
        String ANNOUN_MESSAGE = "3";

    }

    public interface MessageOper{
        String MESSAGE_READ = "1";
        String MESSAGE_CLEAN = "2";
    }
}
