package com.ai.apac.smartenv.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleConstant.java
 * @Description: 车辆常量类
 * @version: v1.0.0
 * @author: zhaoaj
 * @date: 2020年2月7日 上午10:45:55
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 * 2020年2月7日     zhaoaj           v1.0.0               修改原因
 */
public interface VehicleConstant {

    public interface VehicleExtAttr {
        Long PIC_ATTR_ID = 100000L;
        Long DRIVING_PIC_FIRST_ATTR_ID = 100001L;
        Long DRIVING_PIC_SECOND_ATTR_ID = 100002L;
        String PIC_ATTR_NAME = "Vehicle Picture";
        String DRIVING_PIC_FIRST_ATTR_NAME = "Driving Picture First";
        String DRIVING_PIC_SECOND_ATTR_NAME = "Driving Picture Second";
    }

    public interface VehicleException {
        String CODE = "exception_message";
        String KEY_PLATE_NUMBER_EXIST = "vehicle000001";
        String KEY_NEED_PLATE_NUMBER = "vehicle000002";
        String KEY_NEED_KIND_CODE = "vehicle000003";
        String KEY_NEED_ENTITY_CATEGORY_ID = "vehicle000004";
        String KEY_NEED_DEPT_ID = "vehicle000005";
        String KEY_NEED_DEPT_ADD_TIME = "vehicle000006";
        String KEY_NEED_VEHICLE_ID = "vehicle000007";
        String KEY_NO_RECORDS = "vehicle000008";
        String KEY_EXPORT_ERROR = "vehicle000009";
        String KEY_ADD_TIME_BEFROE_REMOVE = "vehicle000010";
		String KEY_REL_WORKAREA = "vehicle000011";
		String KEY_REL_ARRANGE = "vehicle000012";
		String KEY_REL_DRIVER = "vehicle000013";
		String KEY_NEED_PARAMS = "vehicle000014";
		String KEY_ENTITY_CATEGORY_ID_WRONG = "vehicle000015";
		String KEY_DEPT_ID_WRONG = "vehicle000016";
		String KEY_DEPT_ADD_TIME_WRONG = "vehicle000017";
    }

    String OSS_BUCKET_NAME = "smartenv";
    Long ALARM_RULE_REL_TYPE_VEHICLE = 100001L;
    String DEVICE_REL_TYPE_VEHICLE = "100001";
    String DICT_DEFAULT_IMAGE = "default_image";
    String DICT_DEFAULT_IMAGE_VEHICLE = "1";
    String DICT_DEFAULT_IMAGE_PERSON = "2";
    String DICT_IMPORT_EXCEL_MODEL = "import_excel_model";
    String DICT_IMPORT_EXCEL_MODEL_PERSON = "1";
    String DICT_IMPORT_EXCEL_MODEL_VEHICLE = "2";
    String DICT_FUEL_TYPE = "fuel_type";
    String DICT_VEHICLE_STATE = "vehicle_state";
    String DICT_VEHICLE_ZH = "机动车";
    String DICT_NONVEHICLE_ZH = "非机动车";
    String DICT_VEHICLE_TYPE = "VEHICLE_TYPE";
    String DICT_VEHICLE_CODE_1 = "1";
    String DICT_VEHICLE_CODE_2 = "2";

    String WORK_STATUS_KEY = "work_status";
    Long WORKAREA_REL_VEHICLE = 2L;
    String FULE_TANK_SIZE_SPLIT = "\\*";


    public interface VehicleRelBind {
        Integer TRUE = 1;
        Integer FALSE = 0;
    }

    public interface KindCode {
    	Long MOTOR = 1225410941508714499L;// 机动车
    	Long NON_MOTOR = 1225410941508714507L;// 非机动车
    }

    public interface NON_MOTOR_CATEGORY {
        Long ZXC = 1225410941508714508L;// 自行车
    }

    public interface MOTOR_CATEGORY {
        Long LJQSC = 1225410941508714500L;// 垃圾清扫车
        Long SSC = 1225410941508714498L;// 洒水车
        Long YSC = 1227854530373226509L;// 压缩车
        Long HLQXC = 1225410941508714610L;// 护栏清洗车
        Long GBC = 1225410941508714603L;// 钩臂车
        Long NYC = 1225410941508714605L;// 农用车
        Long DGNYCC = 1227854530373226518L;// 农用车
        Long QXC = 1227854530373226510L;//清洗车
        Long LJQYC = 1225410941508714601L;//垃圾清运车
    }

    String BUCKET = "smartenv";

    Long VEHICLE_POSITION_DEVICE_TYPE = 1225410941508714509L;
    Long VEHICLE_ACC_DEVICE_TYPE = 1227854530373226498L;
    Long PERSON_POSITION_DEVICE_TYPE = 1225410941508714504L;





    /**
     * 车辆状态对应图片文件路径
     */
    interface VehicleStatusPicPath {
        String ONLINE = "static/vehicle_web_defaule.png";
        String OFF_ONLINE = "static/vehicle_off_online.png";
//        String ONLINE_ALARM = "static/online_alarm.png";
//        String OFF_ONLINE_ALARM = "static/off_online.png";
    }

    /**
     * 车辆状态
     */
    public interface VehicleStatus {

        /**
         * 在岗
         */
        Integer ONLINE = 1;

        /**
         * 休息
         */
        Integer OFF_ONLINE = 2;

        /**
         * 告警
         */
        Integer ONLINE_ALARM = 3;

        /**
         * 静值
         */
        Integer OFF_ONLINE_ALARM = 4;
        /**
         * 加水
         */
        Integer WATERING = 5;
        /**
         * 加油
         */
        Integer OIL_ING = 6;
        /**
         * 休假
         */
        Integer VACATION = 7;

        /**
         * 正常
         */
        Integer NORMAL = 8;
    }
    
    public interface ExcelImportIndex {
    	int PLATE_NUMBER = 0;
    	int ENTITY_CATEGORY_ID = 1;
    	int DEPT_ID = 2;
    	int DEPT_ADD_TIME = 3;
    }
    
    public interface VehicleState {
        Integer IN_USED = 1;// 在用
        Integer UN_USED = 2;// 退出
        Integer MAINTAIN = 4;//报修
        String IN_USED_NAME = "在用";
        String UN_USED_NAME = "报废";
    }
    /*车辆维修申请*/
    public interface VehicleMaintenanceProcess {
        public static final String CODE = "VehicleMaintenanceProcess";
        public static final String FIRST_USER = "captainId";
        public static final String SECOND_USER = "manageId";
        public static final String SECOND_USER_PERSON = "managePerson";
        public static final String agree = "agree";
        public static final String remark = "remark";
        public static final String FIRST_PERSON = "VehicleCaptain";
        public static final String SECOND_PERSON = "Manager";
        public static final String BUDGET = "Budget";
        public static final String TO_FINISH = "MaintFinish";
        public static final String VEHICLE_ID = "vehicleId";//车辆id
        public static final String VEHICLE_STATE = "vehicleState";//车辆初始状态
    }
    /*车辆维修申请状态*/
    public interface VehicleMaintanceStatus {
        public static final Integer TODO = -1;//任务待处理
        public static final Integer CANCLE = 0;//取消申请
        public static final Integer SUBMIT = 1; //提交待审批
        public static final Integer APPROVE_ONE = 2; //第一审批通过
        public static final Integer BUDGET = 3;//填报预算
        public static final Integer APPROVE_TWO = 4; //第二审批通过
        public static final Integer FINISH = 5;//申请人点确认完成
        public static final Integer REFUSE = 6;//拒绝审批结束

    }

    /*车辆加油*/
    public interface VehicleRefuel {
        public static final String REFUEL_OIL = "refuel_oil";
        public static final String REFUEL_QUERY_TIME = "refuel_query_time";
    }
    /* 车辆维修类型*/
    public interface Maint_Type {
        public static final String CODE = "vehicleMaintType";//车辆维修类型
        public static final String UPKEEP = "1295551039046422533";//车辆保养
        public static final String MAINT = "1295551039046422532";// 车辆维修
    }
    
	Map<Long, Long> CategoryBigDataMap = new HashMap<Long, Long>() {
		{
			// 机动车
			put(1225410941508714498L, 1L);// 洒水车
			put(1225410941508714500L, 2L);// 垃圾清扫车
			put(1225410941508714601L, 4L);// 垃圾清运车
			put(1225410941508714602L, 5L);// 雾炮车
			put(1225410941508714603L, 6L);// 小钩臂
			put(1225410941508714605L, 7L);// 农用车
			put(1225410941508714606L, 8L);// 新平板车
			put(1225410941508714607L, 9L);// 高压清洗车
			put(1225410941508714609L, 10L);// 压缩式侧挂桶车
			put(1225410941508714610L, 11L);// 护栏清洗车
			put(1227854530373226508L, 11L);// 小勾臂车车厢可卸式垃圾车
			put(1227854530373226509L, 12L);// 压缩式垃圾车
			put(1227854530373226510L, 13L);// 清洗车
			put(1227854530373226511L, 14L);// 大洗扫车
			put(1227854530373226512L, 15L);// 洗扫车
			put(1227854530373226513L, 16L);// 小扫路车
			put(1227854530373226514L, 17L);// 大洒水车
			put(1227854530373226515L, 18L);// 小洒水车
			put(1227854530373226516L, 19L);// 路面养护车
			put(1227854530373226517L, 20L);// 餐厨车
			put(1227854530373226518L, 21L);// 多功能抑尘车
			put(1227854530373226519L, 22L);// 平板车
			put(1227854530373226520L, 23L);// 平皮卡
			put(1227854530373226521L, 24L);// 绿化喷洒车
			
			// 非机动车
			put(1225410941508714508L, 3L);// 自行车
			put(1225410941508715508L, 25L);// 电动车
			put(1227854530373226522L, 26L);// 两轮电动巡逻车
			put(1227854530373226523L, 27L);// 电动三轮挂桶车
			put(1227854530373226524L, 28L);// 高温高压洗桶车
			put(1227854530373226525L, 29L);// 高压洗桶车
			put(1227854530373226526L, 30L);// 快速保洁车
			put(1227854530373226527L, 31L);// 燃油挂桶车
			put(1227854530373226528L, 32L);// 三轮电动车
		}
	};
}
