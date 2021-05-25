package com.ai.apac.smartenv.common.enums;

import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.constant.WorkAreaConstant;

/**
 * Created by qianlong on 2020/2/24.
 */
public enum PersonStatusEnum implements EnumValue<Integer, String> {

    //由于工作区域类型有两种都是正常，所以要在这定义出来。
    NORMAL(PersonConstant.PersonStatus.NORMAL,"正常"),

    ON_LINE(PersonConstant.PersonStatus.ONLINE,"正常"),
    OFF_LINE(PersonConstant.PersonStatus.OFF_ONLINE, "休息"),
    ONLINE_ALARM(PersonConstant.PersonStatus.ONLINE_ALARM, "告警"),
    OFFLINE_ALARM(PersonConstant.PersonStatus.OFF_ONLINE_ALARM, "静值"),
    VACATION(PersonConstant.PersonStatus.VACATION, "休假"),
    UN_ARRANGE(WorkAreaConstant.WorkStatus.UN_ARRANGE, "未排班");


    private int value;
    private String desc;

    private PersonStatusEnum(int value, String desc) {
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
        for (PersonStatusEnum objEnum : PersonStatusEnum.values()) {
            if (objEnum.getValue() == value) {
                return objEnum.getDesc();
            }
        }
        return "";
    }

}
