package com.ai.apac.smartenv.common.enums;

import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.constant.WorkAreaConstant;

/**
 * Created by qianlong on 2020/2/17.
 */
public enum PersonStatusImgEnum implements EnumValue<Integer, String> {

    ON_LINE(PersonConstant.PersonStatus.ONLINE, PersonConstant.PersonStatusPicPath.ONLINE),
    OFF_LINE(PersonConstant.PersonStatus.OFF_ONLINE, PersonConstant.PersonStatusPicPath.OFF_ONLINE),
    ONLINE_ALARM(PersonConstant.PersonStatus.ONLINE_ALARM, PersonConstant.PersonStatusPicPath.ONLINE_ALARM),
    OFFLINE_ALARM(PersonConstant.PersonStatus.OFF_ONLINE_ALARM, PersonConstant.PersonStatusPicPath.OFF_ONLINE_ALARM),
    VACATION(VehicleConstant.VehicleStatus.VACATION, PersonConstant.PersonStatusPicPath.OFF_ONLINE),
    UN_ARRANGE(WorkAreaConstant.WorkStatus.UN_ARRANGE, PersonConstant.PersonStatusPicPath.OFF_ONLINE);
    private int value;
    private String desc;

    private PersonStatusImgEnum(int value, String desc) {
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
        for (PersonStatusImgEnum objEnum : PersonStatusImgEnum.values()) {
            if (objEnum.getValue() == value) {
                return objEnum.getDesc();
            }
        }
        return "";
    }

}
