package com.ai.apac.smartenv.common.enums;

import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.constant.WorkAreaConstant;

/**
 * Created by qianlong on 2020/2/17.
 */
public enum VehicleStatusEnum implements EnumValue<Integer, String> {

    //由于工作区域类型有两种都是正常，所以要在这定义出来。
    NORMAL(VehicleConstant.VehicleStatus.NORMAL,"正常"),

    ON_LINE(VehicleConstant.VehicleStatus.ONLINE,"正常"),
    OFF_LINE(VehicleConstant.VehicleStatus.OFF_ONLINE, "休息"),
    ONLINE_ALARM(VehicleConstant.VehicleStatus.ONLINE_ALARM, "告警"),
    OFFLINE_ALARM(VehicleConstant.VehicleStatus.OFF_ONLINE_ALARM, "静值"),
    WATER_ING(VehicleConstant.VehicleStatus.WATERING, "加水"),
    OIL_ING(VehicleConstant.VehicleStatus.OIL_ING, "加油"),
    VACATION(VehicleConstant.VehicleStatus.VACATION, "休假"),
    UN_ARRANGE(WorkAreaConstant.WorkStatus.UN_ARRANGE, "未排班");

    private int value;
    private String desc;

    private VehicleStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public static String getDescByValue(Integer value) {
        for (VehicleStatusEnum objEnum : VehicleStatusEnum.values()) {
            if (objEnum.getValue() == value) {
                return objEnum.getDesc();
            }
        }
        return "";
    }

    public static VehicleStatusEnum getByValue(Integer value) {
        for (VehicleStatusEnum objEnum : VehicleStatusEnum.values()) {
            if (objEnum.getValue() == value) {
                return objEnum;
            }
        }
        return null;
    }

}
