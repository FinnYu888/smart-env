package com.ai.apac.smartenv.alarm.constant;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: AlarmConstant
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/2/6
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/2/6     zhaidx           v1.0.0               修改原因
 */
public class AlarmConstant {

    /**
     * 报警等级
     * 一般 = 1 紧急 = 2
     */
    public static final String ALARM_LEVEL = "alarm_level";
    public interface AlarmLevel {
        int NORMAL = 1;
        int EMERGENCY = 2;
    }

    /**
     * 通知类型
     * 短信 = 1, 微信 = 2, 后台通知 = 3, 手环 = 4
     */
    public static final String INFORM_TYPE = "inform_type";
    public interface InformType {
        String EMAIL = "1";
        String WECHAT = "2";
        String BACKEND_NOTIFICATION = "3";
        String WRIST_BAND = "4";
        String VEHICLE_MACHINE = "5";
        String SMS = "6";
    }

    /**
     * 是否抄送领导
     */
    public interface CCToLeader {
        int NO = 0;
        int YES = 1;
    }
    
    /**
     * 告警规则
     */
    public static final String ALARM_RULE = "ALARM_RULE"; // 告警规则
    public static final String VEHICLE_ALARM_PREFIX = "VA"; // 车辆告警规则code前缀
    public static final String PERSON_ALARM_PREFIX = "PA"; // 人员告警规则code前缀
    public static final String VEHICLE_ALARM_RULE = "VA01"; // 车辆告警规则
    public static final String PERSON_ALARM_RULE = "PA01"; // 人员告警规则
    public static final String VEHICLE_OVERSPEED_ALARM_TYPE = "VA0101"; // 车辆超速告警规则类型
    public static final String VEHICLE_OVERSPEED_ALARM = "VA010101"; // 车辆超速告警规则
    public static final String VEHICLE_AREA_ALARM_TYPE = "VA0102"; // 车辆区域告警规则类型
    public static final String VEHICLE_OUT_OF_AREA_ALARM = "VA010201"; // 区域告警-越界
    public static final String VEHICLE_STAY_ALARM = "VA010202"; // 区域告警-滞留
    public static final String VEHICLE_LOSE_SIGNAL = "VA010203"; // 区域告警-工作信号丢失
    public static final String VEHICLE_VIOLATION_ALARM_TYPE = "VA0103"; // 车辆违规告警规则类型
    public static final String VEHICLE_NON_DESIGNATED_PLACE_ALARM = "VA010301"; // 非指定路线/区域工作车辆违规告警规则
    public static final String VEHICLE_IRREGULAR_OPERATION_ALARM = "VA010302"; // 作业不规范车辆违规告警规则
    public static final String VEHICLE_INITIATIVE_ALARM = "VA010303"; // 主动安全告警
    public static final String PERSON_VIOLATION_ALARM_TYPE = "PA0101"; // 人员违规告警
    public static final String PERSON_OUT_OF_AREA_ALARM = "PA010101"; // 人员违规区域告警-越界
    public static final String PERSON_STAY_ALARM = "PA010102"; // 人员违规区域告警-滞留
    public static final String PERSON_LOSE_SIGNAL = "PA010103"; // 人员违规区域告警-工作信号丢失
    public static final String PERSON_ABNORMAL_ALARM_TYPE = "PA0102"; // 人员异常告警
    public static final String PERSON_BLOOD_PRESSURE_ALARM = "PA010201"; // 人员血压异常
    public static final String PERSON_LOWEST_BATTERY_ALARM = "PA010202"; // 人员电量异常
    public static final String PERSON_HERAT_RATE_ALARM = "PA010203"; // 人员心率异常
    public static final String PERSON_WATCH_SOS_ALARM = "PA010204"; // 人员手表SOS告警
    
    public static final Long VEHICLE_OVERSPEED_ALARM_CATEGORY = 1225410857647800322L; // 车辆超速告警规则分类
    public static final Long VEHICLE_OUT_OF_AREA_ALARM_CATEGORY = 1225410906771488769L; // 车辆区域告警规则分类
    public static final Long VEHICLE_VIOLATION_ALARM_CATEGORY = 1225410941508714497L; // 车辆违规告警规则分类
    public static final Long PERSON_VIOLATION_ALARM_CATEGORY = 1227158886943821825L; // 人员违规告警分类
    public static final Long PERSON_ABNORMAL_ALARM_CATEGORY = 1227158933324435458L; // 人员异常告警分类
    /**
     * 超速告警规则属性名称
     */
    public interface OverSpeedAlarmAttr {
        String SPEED = "SPEED"; // 速度
    }
    /**
     * 车辆区域告警，人员违规告警的参数属性名称
     */
    public interface OutOfAreaAlarm {
        String OUT_OF_AREA_DEVIATION_VALUE = "OUT_OF_AREA_DEVIATION_VALUE";
        String DURATION = "DURATION";
    }

    /**
     * 车辆非指定/路线区域工作
     */
    public interface NonDesignedPlaceAlarm {
        String NON_DESIGNATED_PLACE_ADD_WATER = "NON_DESIGNATED_PLACE_ADD_WATER"; // 非指定地点加水
        String NON_DESIGNATED_PLACE_REFUELING = "NON_DESIGNATED_PLACE_REFUELING"; // 非指定地点加油
        String NON_DESIGNATED_PLACE_TAKE_OUT_TRASH = "NON_DESIGNATED_PLACE_TAKE_OUT_TRASH"; // 非指定地点倒垃圾
        String NON_DESIGNATED_PLACE_BLOWDOWN = "NON_DESIGNATED_PLACE_BLOWDOWN"; // 非指定地点排污
    }
    /**
     * 车辆作业不规范 
     */
    public interface IrregularOperationAlarm {
        String NOT_TURN_OFF_WHEN_PARKING = "NOT_TURN_OFF_WHEN_PARKING";
        String TRASH_CAN_NOT_LOWERED_WHEN_DRIVING = "TRASH_CAN_NOT_LOWERED_WHEN_DRIVING";
        String DURATION = "DURATION";
    }
    /**
     *  终端（手表）低电量属性名称
      */    
    public static String LOWEST_BATTERY_VALUE = "LOWEST_BATTERY_VALUE";
    /**
     * 血压异常属性名称
     */
    public interface BloodPressureAlarm {
        String MIN_BLOOD_PRESSURE = "MIN_BLOOD_PRESSURE";
        String MAX_BLOOD_PRESSURE = "MAX_BLOOD_PRESSURE";
        String DURATION = "DURATION";
    }
    /**
     * 心率异常属性名称
     */
    public interface HeartRateAlarm {
        String HEART_RATE_MIN = "HEART_RATE_MIN";
        String HEART_RATE_MAX = "HEART_RATE_MAX";
        String DURATION = "DURATION";
    }
    
    

    /**
     * 告警信息处理状态
     */
    public interface IsHandle {
        int HANDLED_NO = 0; // 未处理
        int HANDLED_YES = 1; // 已处理
    }

    /**
     * 告警规则停用 启用
     */
    public interface Status {
        int NO = 0; // 停用
        int YES = 1; // 启用
    }

    /**
     * 扩展属性输入方式
     */
    public interface AttrInputType {
        int TEXT_INPUT = 1; // 输入框
        int CHECK_BOX = 2; // 勾选框
        // 勾选框的值
        interface CheckBoxValue {
            String UN_SELECTED = "0"; // 未勾选
            String SELECTED = "1"; // 已勾选
        }
    }

    public static final String MONGODB_ALARM_INFO = "alarmInfo";

    public interface MinicreatAlarmType {
        int DSM_ALARM = 1; // 对着驾驶员的摄像头
        int ADAS_ALARM = 2; // 对着车前进方向的摄像头
    }

    /**
     * 告警规则类型编码，对应系统配置表中配置数据
     */
    public interface AlarmTypeCode {
        String VEHICLE_ALARM_TYPE = "vehicle_alarm_type";
        Long VEHILCE_ALARM_OVERSPEED = 1227852490335064065L;
        Long VEHILCE_ALARM_OUT_OF_AREA = 1227852935828869122L;
        Long VEHILCE_ALARM_STAY = 1227853000492453889L;
        Long VEHILCE_ALARM_LOSE_SIGNAL = 1227853000492453890L;
        Long VEHILCE_ALARM_INITIATIVE_ALARM = 1227854314542731267L;
        String PERSON_ALARM_TYPE = "person_alarm_type";
        Long PERSON_ALARM_OUT_OF_AREA = 1227854224298086402L;
        Long PERSON_ALARM_STAY = 1227854269462351874L;
        Long PERSON_ALARM_LOSE_SIGNAL = 1227854314542731266L;
        Long PERSON_ALARM_WATCH_LOW_POWER = 1227854488140779521L;
        Long PERSON_ALARM_WATCH_SOS = 1303653286628298753L;
    }
}
