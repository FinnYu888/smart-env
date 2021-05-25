package com.ai.apac.smartenv.common.constant;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/1 10:56 上午
 **/
public interface ProjectConstant {

    interface ProjectStatus {
        /**
         * 正常
         */
        Integer Normal = 1;

        /**
         * 锁定
         */
        Integer Lock = 2;
    }

    interface ProjectDefault {
        /**
         * 默认项目
         */
        Integer Default = 1;

        /**
         * 非默认项目
         */
        Integer NotDefault = 2;
    }
}
