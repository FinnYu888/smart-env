package com.ai.apac.smartenv.alarm.constant;

/**
 * alarmInfo表ishandle字段枚举类型
 */
public enum AlarmHandledName {
    HANDLED_NO("未处理", 0),
    HANDLED_YES("已处理", 1);
    
    private String name;
    private int index;

    AlarmHandledName(String name, int index) {
        this.name = name;
        this.index = index; 
    }

    public static String getName(int index) {
        for (AlarmHandledName value : AlarmHandledName.values()) {
            if (value.getIndex() == index) {
                return value.getName();
            }
        }
        return null;
    }

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
