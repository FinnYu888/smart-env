package com.ai.apac.smartenv.alarm.constant;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: MinicreateDSMAlarm
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/9/10
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/9/10     zhaidx           v1.0.0               修改原因
 */
public enum MinicreateDSMAlarm {
    FATIGUE_DRIVING("疲劳驾驶报警", 1),
    MAKE_CALL("接打电话报警", 2),
    SMOKING("抽烟报警", 3),
    DISTRACTION_DRIVING("分神驾驶报警", 4),
    DRIVER_EXCEPTION("驾驶员异常报警", 5);
    
    private String name;
    private int index;

    MinicreateDSMAlarm(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static String getName(int index) {
        for (MinicreateDSMAlarm value : MinicreateDSMAlarm.values()) {
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
