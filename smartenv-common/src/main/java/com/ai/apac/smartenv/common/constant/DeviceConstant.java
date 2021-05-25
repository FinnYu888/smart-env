package com.ai.apac.smartenv.common.constant;

/**
 * @ClassName DeviceConstant
 * @Desc 设备参数
 * @Author ZHANGLEI25
 * @Date 2020/2/11 10:54
 * @Version 1.0
 */
public interface DeviceConstant {
    String OSS_BUCKET_NAME = "smartenv";
    String DICT_IMPORT_EXCEL_MODEL = "import_excel_model";
    String DICT_IMPORT_EXCEL_MODEL_SIM = "3";
    String DICT_IMPORT_EXCEL_MODEL_VEHICLE_DEVICE = "4";
    String DICT_IMPORT_EXCEL_MODEL_PERSON_DEVICE = "5";
    String DICT_IMPORT_EXCEL_MODEL_DEVICE_REL = "6";

    public interface DeviceStatus {
        String ON = "0";//正常开启
        String OFF = "1";//正常关闭
        String OFF_ERR = "2";//异常关闭
        String NO = "99";//无设备信息
        String NO_DEV = "-1";//未绑定设备
    }

    public interface BigDataDeviceStatus {
        Long ON = 1L;//正常开启
        Long OFF = 0L;//正常关闭
    }


    public interface DeviceCategory {
        Long GREEN_MONITOR_DEVICE = 1227854530373226613L;//绿化监控设备
        Long VEHICLE_MONITOR_DEVICE = 1225410941508714502L;//车辆监控设备
        Long VEHICLE_NVR_MONITOR_DEVICE = 1225410941508714512L;//车载NVR监控
        Long VEHICLE_CVR_MONITOR_DEVICE = 1225410941508714513L;//车载CVR监控
        Long VEHICLE_GPS_DEVICE = 1225410941508714509L;//车载定位传感器
        Long VEHICLE_SENSOR_DEVICE = 1225410941508714503L;//车辆传感器
        Long VEHICLE_ACC_DEVICE = 1227854530373226498L;//车辆ACC传感器

        Long PERSON_DEVICE = 1227854530373226501L;//人员设备
        Long PERSON_WATCH_DEVICE = 1225410941508714504L;//人员手表传感器
        Long PERSON_BAND_DEVICE = 1225410941508715504L;//人员手环传感器

        Long FACILiTY_NVR_MONITOR_DEVICE = 1227854530373226506L;//设施NVR监控
        Long FACILiTY_CVR_MONITOR_DEVICE = 1227854530373226507L;//设施CVR监控

    }


    public interface DeviceCharSpec {
        Long PERSON_DEVICE_SIM_NUMBER = 1000000000000000005L;//SIM卡号
        Long PERSON_DEVICE_ICCID = 1000000000000000006L;//鉴权码

        Long VEHICLE_DEVICE_SIM_NUMBER = 1000000000000000029L;//SIM卡号
        Long VEHICLE_DEVICE_ICCID = 1000000000000000030L;//鉴权码


        Long COORDS_SYSTEM = 1000000000000000040L;//坐标系
    }


    public interface SIMExcelImportIndex {
        int SIM_TYPE = 0;
        int SIM_CODE = 1;
        int SIM_NUMBER = 2;
        int DEVICE_CODE = 3;
        int REMARK = 4;
    }


    public interface PersonDeviceExcelImportIndex {
        int DEVICE_CODE = 0;
        int DEVICE_NAME = 1;
        int DEVICE_TYPE = 2;
        int DEVICE_FACTORY = 3;
        int ENTITY_CATEGORY_ID = 4;
        int COORD = 5;
        int SIM_CODE = 6;
    }

    public interface VehicleMonitorDeviceExcelImportIndex {
        int DEVICE_CODE = 0;
        int DEVICE_NAME = 1;
        int DEVICE_TYPE = 2;
        int DEVICE_FACTORY = 3;
        int ENTITY_CATEGORY_ID = 4;
        int SIM_CODE = 5;
        int CHANNEL1 = 6;
        int CHANNEL2 = 7;
        int CHANNEL3 = 8;
        int CHANNEL4 = 9;
        int CHANNEL5 = 10;
        int CHANNEL6 = 11;
        int CHANNEL7 = 12;
        int CHANNEL8 = 13;
    }

    public interface VehicleSensorDeviceExcelImportIndex {
        int DEVICE_CODE = 0;
        int DEVICE_NAME = 1;
        int DEVICE_TYPE = 2;
        int DEVICE_FACTORY = 3;
        int ENTITY_CATEGORY_ID = 4;
        int SIM_CODE = 5;
        int COORD = 6;
        int AUTH_CODE = 7;
    }


    public interface DeviceFactory {
        String MINICREATE = "6";//深圳点创科技
    }


    public interface miniCreateResultCode {
        int success = 0;
    }

    public interface miniCreateErrInfo {
        String failInfo = "设备同步第三方接口失败，失败原因【{}】请稍后再试";
    }


    public interface DeviceExtAttrId {
        Long miniCreateDeviceGUID = 300001L;
    }

    public interface DeviceExtAttrName {
        String miniCreateDeviceGUID = "GUID";
    }

    public interface regex {
        String simCodeRegex = "^[a-zA-Z0-9]{1,20}$";
        String simNumberRegex = "^[0-9]{1,13}$";

    }

    /**
     * SIM卡类型
     */
    interface SimCardType {
        String CARD_4G = "1";
        String CARD_5G = "2";
        String CARD_GPRS = "3";
        String CARD_GPRS_VOICE = "4";
    }

    /**
     * 设备告警状态
     */
    interface AlarmStatus {
        //正常
        Integer NORMAL = 1;

        //有告警
        Integer ALARM = 2;
    }
}
