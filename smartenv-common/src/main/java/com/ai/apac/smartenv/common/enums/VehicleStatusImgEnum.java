package com.ai.apac.smartenv.common.enums;

import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.constant.WorkAreaConstant;

/**
 * Created by qianlong on 2020/2/17.
 */
public enum VehicleStatusImgEnum implements EnumValue<Integer, String> {

    ON_LINE(VehicleConstant.VehicleStatus.ONLINE, VehicleConstant.VehicleStatusPicPath.ONLINE),
    OFF_LINE(VehicleConstant.VehicleStatus.OFF_ONLINE, VehicleConstant.VehicleStatusPicPath.OFF_ONLINE),
    ONLINE_ALARM(VehicleConstant.VehicleStatus.ONLINE_ALARM, VehicleConstant.VehicleStatusPicPath.ONLINE),
    OFFLINE_ALARM(VehicleConstant.VehicleStatus.OFF_ONLINE_ALARM, VehicleConstant.VehicleStatusPicPath.ONLINE),
    WATERING(VehicleConstant.VehicleStatus.WATERING, VehicleConstant.VehicleStatusPicPath.ONLINE),
    OIL_ING(VehicleConstant.VehicleStatus.OIL_ING, VehicleConstant.VehicleStatusPicPath.ONLINE),
    VACATION(VehicleConstant.VehicleStatus.VACATION, VehicleConstant.VehicleStatusPicPath.OFF_ONLINE),
    UN_ARRANGE(WorkAreaConstant.WorkStatus.UN_ARRANGE, VehicleConstant.VehicleStatusPicPath.OFF_ONLINE);

    private int value;
    private String desc;

    private VehicleStatusImgEnum(int value, String desc) {
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
        for (VehicleStatusImgEnum objEnum : VehicleStatusImgEnum.values()) {
            if (objEnum.getValue() == value) {
                return objEnum.getDesc();
            }
        }
        return "";
    }

}
