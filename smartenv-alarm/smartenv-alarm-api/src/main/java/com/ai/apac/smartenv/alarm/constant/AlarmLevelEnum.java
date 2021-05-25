package com.ai.apac.smartenv.alarm.constant;

/**
 * alarmRuleInfo表alarmLevel字段枚举类型
 */
public enum AlarmLevelEnum {

    NORMAL("普通告警", 1),
    EMERGENCY("紧急告警", 2);
    
    private String name;
    private int index;

    AlarmLevelEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static String getName(int index) {
        for (AlarmLevelEnum value : AlarmLevelEnum.values()) {
            if (value.getIndex() == index) {
                return value.getName();
            }
        }
        return null;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
