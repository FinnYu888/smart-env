package com.ai.apac.smartenv.common.constant;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/2/19 6:45 下午
 **/
public interface ResultCodeConstant {

    public interface WebSocketCode {
        public int SESSION_TIME_OUT = 900001;
        public int TASK_FINISHED = 900002;
    }
    public interface ResponseCode {
        public int SUCCESS = 200;
        public int BUSINESS_ERROR = 400;
    }
}
