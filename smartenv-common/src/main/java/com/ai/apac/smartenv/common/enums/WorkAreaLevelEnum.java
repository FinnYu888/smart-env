package com.ai.apac.smartenv.common.enums;

/**
 * 作业区域等级
 * Created by qianlong on 2021/1/12.
 * @author qianlong
 */
public enum WorkAreaLevelEnum implements EnumValue<Integer, String> {

    Level1(1,"一级"),
    Level2(2,"二级"),
    Level3(3,"三级"),
    Level4(4,"四级");

    private Integer value;
    private String desc;

    private WorkAreaLevelEnum(Integer value, String desc) {
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
        for (WorkAreaLevelEnum objEnum : WorkAreaLevelEnum.values()) {
            if (objEnum.getValue().equals(value)) {
                return objEnum.getDesc();
            }
        }
        return "";
    }

}
