package com.ai.apac.smartenv.common.constant;
/**
*  设备模块常量信息
*/
public interface FacilityConstant {

	String DICT_IMPORT_EXCEL_MODEL = "import_excel_model";
	String DICT_IMPORT_EXCEL_MODEL_ASHCAN = "8";
	String OSS_BUCKET_NAME = "smartenv";



	String COMPANY_LAST_WEEK_POLYMERIZATION_DATA="CompanyLastWeekPolymerizationData";
	String REGION_LAST_WEEK_POLYMERIZATION_DATA="RegionLastWeekPolymerizationData";


    interface ExceptionMsg {
        String CODE = "exception_message";
        String KEY_DEVICE_NULL = "facility000001";//中转站编号和传感器编号不能同时为空
        String KEY_DEVICE_FACILITY_ERR = "facility000002";//根据传感器终端id没有找到绑定的中转站
        String KEY_NUMER_FORMAT_ERR = "facility000003";//数据格式错误
        String KEY_PROJECT_NO_EXIST = "facility000004";//中转站编号已经存在
        String KEY_TRANSFER_NAME_EXIST = "facility000005";//中转站名称已经存在
        String KEY_TRANSFER_STATUS_MODIFY = "facility000006";//工作中和故障中的不能修改为规划中
        String KEY_TRANSFER_STATUS_DEL = "facility000007";//请先解除绑定的终端再删除该中转站
        String KEY_TRANSFER__DEL = "facility000008";//删除中转站时获取设备模块接口异常
        String KEY_TRANSFER__DROP = "facility000009";//弃用的中转站不能修改
        String KEY_TRANSFERSTATUS_DROP = "facility000010";//请先解除绑定终端在弃用
    }

    //设施类型
    interface FacilityType {
        String TRANSFER_STATION = "1";//中转站
        String ASHCAN = "2";//垃圾桶
        String PUBLIC_TOILET = "3";//公共厕所
        String WEIGHING_SITE="4"; // 垃圾称重点
    }

    //中转站型号
    interface TranStationModel {
        String CODE = "TranStationModel";
        String SMALL = "1";//小型中转站
        String MIDDLE = "2";//中型中转站
        String BIG = "3";//大型中转站
    }
    //中转站状态
    interface TranStationStatus {
        String CODE = "TranStationStatus";
        String STOPPING = "0";//维修
        String WORKING = "1";//正常
        String PLANNING = "2";//规划中
        String DROP = "3";//暂停服务
    }
    //垃圾類型
    interface GarbageType {
        String CODE = "GarbageType";
        String KITCHEN = "1";//厨余垃圾
        String RECYCLEABLE = "2";//可回收物
        String HARMFUL ="3";//有害垃圾
        String OTHER = "4";//其它垃圾
    }

    public interface DictCode {
		String YES_NO = "yes_no";// 是否
		String ASHCAN_TYPE = "ashcan_type";// 垃圾桶分类
		String ASHCAN_CAPACITY = "ashcan_capacity";// 垃圾桶大小
		String ASHCAN_STATUS = "ashcan_status";// 垃圾桶状态
		String ASHCAN_WORK_STATUS = "ashcan_work_status";// 垃圾桶工作状态
	}

    public interface AshcanStatus {
    	String NORMAL = "1";// 正常
    	String DAMAGED = "2";// 损坏
    }

    public interface SupportDevice {
        String NO = "1";// 否
        String YES = "2";// 是
    }

    public interface AshcanPicture { //垃圾桶状态图标
        String NORMAL = "static/ashcanNormal.png"; //正常未溢满
        String OVERFLOW = "static/ashcanOverFlow.png"; // 正常溢满
        String DAMAGED = "static/ashcanDamaged.png";// 损坏
    }

    public interface AshcanDevicePicture { //垃圾桶已绑传感器状态图标
        String NORMAL = "static/ashcanDeviceNormal.png"; // 正常未溢满
        String OVERFLOW = "static/ashcanDeviceOverFlow.png"; // 正常溢满
        String NO_SIGNAL = "static/ashcanDeviceNoSignal.png"; //无信号
        String DAMAGED = "static/ashcanDeviceDamaged.png"; //损坏
    }

    public interface AshcanWorkStatus { //垃圾桶状态图标
        String NORMAL = "1"; //正常未溢满
        String OVERFLOW = "2"; // 正常溢满
        String NOSIGNAL = "3";// 损坏
    }

    public interface ToiletWorkStatus { // 公厕状态
        String TOILET_WORK_STATUS = "wc_state";
        String NORMAL = "1"; // 正常
        String REPAIR = "2"; // 维修
        String STOP_SERVICE = "3"; // 暂停服务
    }

    public interface ExcelImportIndex {
    	int CODE = 0;
    	int TYPE = 1;
    	int CAPACITY = 2;
    	int SUPPORT_DEVICE = 3;
    	int WORKAREA = 4;
    	int ADDRESS = 5;

    	int DEPT = 6;
    }
    //中转站臭味级别
    interface FacilityOdorLevel {
        String CODE = "facility_odor_level";
    }
}
