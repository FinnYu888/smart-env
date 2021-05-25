package com.ai.apac.smartenv.common.constant;

/**
 * @ClassName AssessmentConstant
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/31 16:02
 * @Version 1.0
 */
public interface AssessmentConstant {

    interface TargetStatus {
        Integer TO_START = 0; //未启动
        Integer STARTED = 1; //已启动
        Integer TO_SCORE = 2; //待评分
        Integer SCORED = 3; //已评分
        Integer END = 4; //已发布
    }

}
