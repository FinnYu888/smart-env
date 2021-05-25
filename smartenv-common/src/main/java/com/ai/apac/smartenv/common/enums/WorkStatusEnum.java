package com.ai.apac.smartenv.common.enums;

import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.constant.WorkAreaConstant;

/**
 * Created by qianlong on 2020/2/17.
 */
public enum WorkStatusEnum implements EnumValue<Integer, String> {

    //由于工作区域类型有两种都是正常，所以要在这定义出来。
    ON_LINE(WorkAreaConstant.WorkStatus.ONLINE,"正常"),
    REST(WorkAreaConstant.WorkStatus.REST, "休息"),
    ALARM(WorkAreaConstant.WorkStatus.ALARM, "告警"),
    ON_STANDBY(WorkAreaConstant.WorkStatus.ON_STANDBY, "静值"),
    WATER_ING(WorkAreaConstant.WorkStatus.WATERING, "加水"),
    OIL_ING(WorkAreaConstant.WorkStatus.OIL_ING, "加油"),
    VACATION(WorkAreaConstant.WorkStatus.VACATION, "休假"),
    UN_ARRANGE(WorkAreaConstant.WorkStatus.UN_ARRANGE, "未排班"),
    VEHICLE_MAINTAIN(WorkAreaConstant.WorkStatus.VEHICLE_MAINTAIN, "车辆维修");

    private int value;
    private String desc;

    private WorkStatusEnum(int value, String desc) {
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
        for (WorkStatusEnum objEnum : WorkStatusEnum.values()) {
            if (objEnum.getValue().equals(value)) {
                return objEnum.getDesc();
            }
        }
        return "";
    }

}
