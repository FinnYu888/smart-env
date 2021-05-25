package com.ai.apac.smartenv.common.constant;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/29 2:39 下午
 **/
public interface SystemConstant {

    interface Channel {
        String WEB = "smartenv-web";
        String MINI_APP = "smartenv-mini-app";
        String BI_SCREEN = "smartenv-bi-scrren";
    }

    interface RoleAlias {
        String ADMINISTRATOR = "administrator";
        String ADMIN = "admin";
        String USER = "user";
    }

    interface MenuId {
        Long INV_APPROVE = 900102L;
        Long INV_STOCK_OUT = 900103L;
    }

    interface RegionType {
        String TYPE_1 = "1";//行政区域类型
        String TYPE_2 = "2";//业务区域类型
    }

    interface UserStatus {
        Integer Normal = 1;//正常
        Integer Lock = 2;//锁定
    }
}
