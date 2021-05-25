package com.ai.apac.smartenv.alarm.constant;

/**
 * alarmRuleInfo表informType字段枚举类型
 */
public enum InformTypeEnum {
    
    //1.邮件 2.微信 3.后台通知 4.手环 5.车机 6.短信
    EMAIL("邮件通知", 1),
    WECHAT("微信通知", 2),
    BACKEND_NOTIFICATION("后台通知（web消息）", 3),
    WRIST_WATCH("手表通知", 4),
    VEHICLE_MACHINE("车机通知", 5),
    SMS("短信通知", 6);


    // 成员变量  
    private String name;
    private int index;

    // 构造方法  
    InformTypeEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法  
    public static String getName(int index) {
        for (InformTypeEnum informTypeEnum : InformTypeEnum.values()) {
            if (informTypeEnum.getIndex() == index) {
                return informTypeEnum.name;
            }
        }
        return null;
    }

    // get set 方法  
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
