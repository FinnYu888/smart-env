package com.ai.apac.smartenv.common.constant;

/**
 * 
 * Copyright: Copyright (c) 2020 Asiainfo
 * 
 * @ClassName: PersonConstant.java
 * @Description: 人员管理
 *
 * @version: v1.0.0
 * @author: zhaoaj
 * @date: 2020年2月17日 上午11:57:34 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 * 2020年2月17日     zhaoaj           v1.0.0               修改原因
 */
public interface PersonConstant {

	String BUCKET = "smartenv";

	public interface PersonException {
		String CODE = "exception_message";
		String KEY_NEED_JOB_NUMBER = "person000001";
		String KEY_NEED_PERSON_NAME = "person000002";
		String KEY_NEED_PERSON_DEPT_ID = "person000003";
		String KEY_NEED_POSITION_ID = "person000004";
		String KEY_NEED_ID_CARD_TYPE = "person000005";
		String KEY_NEED_ID_CARD = "person000006";
		String KEY_NEED_MOBILE_NUMBER = "person000007";
		String KEY_NEED_EMAIL = "person000008";
		String KEY_NEED_ENTRY_TIME = "person000009";
		String KEY_NEED_IS_INCUMBENCY = "person000010";
		String KEY_JOB_NUMBER_EXIST = "person000011";
		String KEY_NEED_PARAMS = "person000012";
		String KEY_NO_DEPT_BY_ID = "person000013";
		String KEY_NO_PERSON = "person000014";
		String KEY_REL_WORKAREA = "person000015";
		String KEY_REL_ARRANGE = "person000016";
		String KEY_REL_VEHICLE = "person000017";
		String KEY_PERSON_DEPT_ID_WRONG = "person000018";
		String KEY_POSITION_ID_WRONG = "person000019";
		String KEY_MOBILE_NUMBER_WRONG = "person000020";
		String KEY_ENTRY_TIME_WRONG = "person000021";
		String KEY_ID_CARD_WRONG = "person000022";
	}
	
	public interface PersonExtAttr {
        Long DRIVER_LICENSE_FIRST_ATTR_ID = 100001L;
        Long DRIVER_LICENSE_SECOND_ATTR_ID = 100002L;
        String DRIVER_LICENSE_FIRST_ATTR_NAME = "Driver License First";
        String DRIVER_LICENSE_SECOND_ATTR_NAME = "Driver License Second";
    }

	public interface DictCode {
		String POSITION = "position";// 岗位
		String ID_CARD_TYPE = "id_card_type";// 证件类型
		String GENDER = "gender";// 性别
		String POLITICAL_KIND = "political_kind";// 政治面貌
		String MARITAL_STATUS = "marital_status";// 婚姻状况
		String INCUMBENCY_STATUS = "incumbency_status";// 在职状态
		String CONTRACT_TYPE = "contract_type";// 合同类型
		String QUALIFICATION = "qualification";// 学历
	}

	public interface IncumbencyStatus {
		Integer IN = 1;// 在职
		Integer UN = 2;// 离职
		Integer TEMPORARY = 3;// 临时工
		String IN_AND_TEMPORARY = "1,3";// 在职和临时工
	}
	public interface IsUser {
		Integer NO = 0;
		Integer YES = 1;
	}
	
	public interface ExcelImportIndex {
    	int PERSON_NAME = 0;
    	int JOB_NUMBER = 1;
    	int PERSON_DEPT_ID = 2;
    	int ID_CARD = 3;
    	int MOBILE_NUMBER = 4;
//    	int EMAIL = 5;
    	int ENTRY_TIME = 5;
    	int GENDER = 6;
    	int PERSON_POSITION_NAME = 7;
    }

	public interface Position {
		Long DRIVER = 3L;
	}

	/**
	 * 人员状态对应图片文件路径
	 */
	public interface PersonStatusPicPath {
		String ONLINE = "static/person_online.png";
		String OFF_ONLINE = "static/person_off_online.png";
		String ONLINE_ALARM = "static/person_alarm_online.png";
		String OFF_ONLINE_ALARM = "static/person_off_online_alarm.png";
	}

	/**
	 * 车辆状态
	 */
	public interface PersonStatus {

		/**
		 * 在岗
		 */
		Integer ONLINE = 1;

		/**
		 * 休息
		 */
		Integer OFF_ONLINE = 2;

		/**
		 * 在岗有告警
		 */
		Integer ONLINE_ALARM = 3;

		/**
		 * 静值(脱岗)
		 */
		Integer OFF_ONLINE_ALARM = 4;
		/**
		 * 休假
		 */
		Integer VACATION = 7;


		/**
		 * 同 1
		 */
		Integer NORMAL = 8;
	}

	Long WORKAREA_REL_PERSON = 1L;

	interface Device{
		Long BRACELET = 1225410941508715504L;
		Long WATCH = 1225410941508714504L;
	}
	/* 性别*/
	interface Gender {
		public static final String MAN = "1";
		public static final String MAN_NAME = "男";
		public static final String WOMAN = "2";
		public static final String WOMAN_NAME = "女";
	}
	
	public interface PersonRelBind {
        Integer TRUE = 1;
        Integer FALSE = 0;
    }

    String BODY_BIOLOGICAL_URL = "https://qm.sqiming.com/template/rtswz/chart-data.php";
}
