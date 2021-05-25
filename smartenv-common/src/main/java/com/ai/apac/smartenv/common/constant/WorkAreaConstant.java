package com.ai.apac.smartenv.common.constant;

public interface WorkAreaConstant {

	interface WorkareaRelEntityType {
		String PERSON = "1";// 人
		String VEHICLE = "2";// 车
	}


	/**
	 * 区域类型
	 */
	interface AreaType {
		Long AREA = 2L;// 区域
		Long ROAD = 1L;// 路线
	}

	/**
	 * 工作区域分类
	 */
	interface AreaCategory{
		int NO_SINGLE = 0;
		int PERSON_WORK_AREA = 1;
		int GAS_AREA = 2;
		int WATER_AREA = 3;
		int VEHICLE_RUN_AREA = 4;
		int MAINTAIN_AREA = 5;
		int VEHICLE_REST_AREA = 6;
		int PERSON_REST_AREA = 7;
		int VEHICLE_WORK_AREA = 8;
		int NONE = -1;
	}

	/**
	 * 工作状态
	 */
	interface WorkStatus{
		/**
		 * 在岗
		 */
		Integer ONLINE = 1;

		/**
		 * 休息
		 */
		Integer REST = 2;

		/**
		 * 告警
		 */
		Integer ALARM = 3;

		/**
		 * 静值
		 */
		Integer ON_STANDBY = 4;

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

//		/**
//		 * 正常
//		 */
//		Integer NORMAL = 8;

		/**
		 * 未排班
		 */
		Integer UN_ARRANGE = 9;

		/**
		 * 未排班
		 */
		Integer VEHICLE_MAINTAIN = 10;
	}
}
