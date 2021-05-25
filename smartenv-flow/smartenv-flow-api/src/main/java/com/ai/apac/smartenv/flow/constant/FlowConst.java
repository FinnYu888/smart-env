package com.ai.apac.smartenv.flow.constant;

public interface FlowConst {

    //流程定义编码
    public static final String FLOW_CODE = "flow_code";

    //流程定义是否配置标识
    public interface FLOW_CONFIG_FLAG {
        public static final Integer yes = 1;
        public static final Integer no = 0;
        public static final String NO = "否";
        public static final String YES = "是";
    }
    //
    public interface done_type {
        public static final String PERSON = "1";
        public static final String STATIION = "2";
        public static final String ROLE = "3";
    }
}
