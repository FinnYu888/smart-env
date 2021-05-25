package com.ai.apac.smartenv.alarm.constant;

public enum MinicreateADASAlarm {
    FORWARD_COLLISION("前向碰撞报警", 1),
    LANE_DEPARTURE("车道偏离报警", 2),
    TOO_CLOSE("车距过近报警", 3),
    PEDESTRIAN_COLLISION("行人碰撞报警", 4),
    FREQUENT_LANE_CHANGES("频繁变道报警", 5),
    ROAD_SIGN_OVERRUN("道路标识超限报警", 6),
    OBSTACLE("障碍物报警", 7);

    private String name;
    private int index;

    MinicreateADASAlarm(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static String getName(int index) {
        for (MinicreateADASAlarm value : MinicreateADASAlarm.values()) {
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
