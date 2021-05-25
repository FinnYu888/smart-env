/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.common.constant;

/**
 * 通用常量
 *
 * @author Chill
 */
public interface CommonConstant {

	String BUCKET = "smartenv";

	/**
	 * sword 系统名
	 */
	String SWORD_NAME = "sword";

	/**
	 * saber 系统名
	 */
	String SABER_NAME = "saber";

	/**
	 * 顶级父节点id
	 */
	Long TOP_PARENT_ID = 0L;

	/**
	 * 顶级父节点名称
	 */
	String TOP_PARENT_NAME = "顶级";

	/**
	 * 未封存状态值
	 */
	Integer NOT_SEALED_ID = 0;

	/**
	 * 默认密码
	 */
	String DEFAULT_PASSWORD = "123456";

	/**
	 * 默认排序字段
	 */
	String SORT_FIELD = "sort";

	/**
	 * 数据权限类型
	 */
	Integer DATA_SCOPE_CATEGORY = 1;

	/**
	 * 接口权限类型
	 */
	Integer API_SCOPE_CATEGORY = 2;

	String COORDS_HEADER_NAME="coordsType";

	String ACCEPT_CHANNEL_TYPE = "acceptChannelType";

	interface ACCEPT_CHANNEL_TYPES {
		String WEB = "1";
		String MINI_APP = "2";
	}

	interface ASSIGNED_HISTORY_TYPE {
		Integer ASSIGNED = 1;
		Integer CHECK = 2;
	}
	interface CHECK_RESULT {
		Integer UNQUALIFIED = 2;
		Integer QUALIFIED = 3;
	}

	interface ENTITY_TYPE {
		Long ALARM = 1L;
		Long VEHICLE = 2L;
		Long DEVICE = 3L;
		Long FACILITY = 4L;
		Long PERSON = 5L;
		Long GREEN = 6L;
		Long EVENT = 7L;
		Long ASHCAN = 8L;
		Long TOILET = 9L;
	}

	interface WORK_STATUS {
		Long ON = 1L;
		Long OFF = 2L;
		Long REST = 3L;
	}

	interface RES_CODE {
		Integer CODE301 = 301;//前端展现对话框
		Integer CODE302 = 302;//前端展现提示框
	}



	Double PI=Math.PI; //π的值。
	Double RE=6378137.0;//地球半径

	Long PERSON_WARCH_COORDS_CATEGORY_ID=1000000000000000041L;
	Long VEHICLE_WARCH_COORDS_CATEGORY_ID=1000000000000000040L;
	Long VEHICLE_ACC_CATEGORY_ID=1227854530373226498L;
	Long PERSON_ACC_CATEGORY_ID=1225410941508714504L;

	String DICT_THIRD_PATH = "third_path";

	String DICT_THIRD_INFO = "third_info";

	public interface DICT_THIRD_KEY {
		String MINICREATE_ADD_DEVICE_KEY = "minicreate.addDevice";//深圳点创科技 新增设备接口key
		String MINICREATE_UPDATE_DEVICE_KEY = "minicreate.updateDevice";//深圳点创科技 更新设备接口key
		String MINICREATE_DELETE_DEVICE_KEY = "minicreate.deleteDevice";//深圳点创科技 删除设备接口key
		String MINICREATE_LIVE_VIDEO_KEY = "minicreate.liveVideo";//深圳点创科技 获取实时视频接口key
		String MINICREATE_HISTORY_VIDEO_KEY = "minicreate.historyVideo";//深圳点创科技 获取历史视频接口key
		String MINICREATE_LIVE_VIDEO_STOP_KEY = "minicreate.liveVideoStop";//深圳点创科技 关闭实时视频接口key
		String MINICREATE_HISTORY_VIDEO_STOP_KEY = "minicreate.historyVideoStop";//深圳点创科技 关闭历史视频接口key
		String MINICREATE_LOGIN_KEY = "minicreate.login";//深圳点创科技 登录接口KEY

		String MINICREATE_USER_KEY = "minicreate.user";//深圳点创科技 登录用户名key
		String MINICREATE_PASSWORD_KEY = "minicreate.password";//深圳点创科技 登录密码key

		String CZ_SYNTH_KEY = "cz.synth";//沧州政务云 统计数据同步接口

	}
}
