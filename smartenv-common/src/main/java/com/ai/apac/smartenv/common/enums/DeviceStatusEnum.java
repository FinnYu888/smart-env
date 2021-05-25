package com.ai.apac.smartenv.common.enums;

/**
 * Created by qianlong on 2020/2/18.
 */
public enum DeviceStatusEnum implements EnumValue<Long, String> {

    NULL(99L,"无信息"),
    ON(0L, "开启"),
    OFF(1L, "正常关闭"),
    OFF_ERROR(2L, "异常关闭"),
    NO_DEV(-1L, "未绑定设备");

    private Long value;
    private String desc;

    private DeviceStatusEnum(Long value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public static String getDescByValue(Long value) {
        for (DeviceStatusEnum objEnum : DeviceStatusEnum.values()) {
            if (objEnum.getValue() == value) {
                return objEnum.getDesc();
            }
        }
        return "";
    }

}
