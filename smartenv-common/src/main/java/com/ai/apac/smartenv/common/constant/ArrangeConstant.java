package com.ai.apac.smartenv.common.constant;

/**
 * 
 * Copyright: Copyright (c) 2020 Asiainfo
 * 
 * @ClassName: ArrangeConstant.java
 * @Description: 排班常量类
 *
 * @version: v1.0.0
 * @author: zhaoaj
 * @date: 2020年2月11日 下午4:42:42 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 * 2020年2月11日     zhaoaj           v1.0.0               修改原因
 */
public interface ArrangeConstant {

	
	
	String DICT_SCHEDULE_TYPE = "schedule_type";
	String DICT_HOLIDAY_TYPE = "holiday_type";
	String DATE_SEPARATION = "~";
	Long BREAK_SCHEDULE_ID = 10L;
	String BREAK_SCHEDULE_NAME = "休息";
	
	interface ArrangeException {
		String CODE = "exception_message";
		String KEY_NEED_SCHEDULE_NAME = "arrange000001";
		String KEY_NEED_SCHEDULE_TYPE = "arrange000002";
		String KEY_NEED_SCHEDULE_BEGIN_TIME = "arrange000003";
		String KEY_NEED_SCHEDULE_END_TIME = "arrange000004";
		String KEY_NEED_SCHEDULE_ID = "arrange000005";
		String KEY_NEED_ENTITY_ID = "arrange000006";
		String KEY_NEED_ENTITY_TYPE = "arrange000007";
		String KEY_NEED_SCHEDULE_BEGIN_DATE = "arrange000008";
		String KEY_NEED_SCHEDULE_END_DATE = "arrange000009";
		String KEY_DATE_BEGIN_AFTER_END = "arrange000010";
		String KEY_NEED_BREAKS_END_TIME = "arrange000011";
		String KEY_NEED_BREAKS_BEGIN_TIME = "arrange000012";
		String KEY_TIME_BEGIN_AFTER_END = "arrange000013";
		String KEY_TIME_BREAKS_OUT_OF_SCOPE = "arrange000014";
		String KEY_SAME_SCHEDULE_TIME = "arrange000015";
	}
	
	interface SchedulePeriod {
		String MONDAY = "周一";
		String TUESDAY = "周二";
		String WEDNESDAY = "周三";
		String THURSDAY = "周四";
		String FRIDAY = "周五";
		String SATURDAY = "周六";
		String SUNDAY = "周日";
	}

	interface Temporary {
		int TRUE = 1;
		int FALSE = 0;
	}

	interface HolidayType {
		String ASSIGN = "1";// 指定
		String PERIOD = "2";// 周期
	}

	interface ScheduleObjectEntityType {
		String VEHICLE = "1";// 车
		String PERSON = "2";// 人
	}

	interface TureOrFalse {
		String STR_TRUE = "1";
		String STR_FALSE = "0";
		int INT_TRUE = 1;
		int INT_FALSE = 0;
	}

	/**
	 * 打卡状态
	 */
	interface AttendanceStatus{
		Long ATTED=1L;//已经打卡
		Long NOT_ATTED=0L;// 未打卡
	}


	/**
	 * 打卡状态
	 */
	interface GO_OFF_WORK_FLAG{
		Long ATTED=1L;//上班
		Long NOT_ATTED=2L;// 下班
	}

	interface UPDATE_SCHEDULE_FLAG {
		Integer UPDATE_NAME = 1;
		Integer UPDATE_TIME = 2;
		Integer UPDATE_PERIODS = 3;
	}

	interface SUBMIT_TYPE {
		Integer UPDATE = 1;
		Integer IGNORE = 2;
	}


}
