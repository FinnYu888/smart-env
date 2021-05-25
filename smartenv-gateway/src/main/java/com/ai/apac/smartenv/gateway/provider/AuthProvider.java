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
package com.ai.apac.smartenv.gateway.provider;

import org.springblade.core.launch.constant.TokenConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 鉴权配置
 *
 * @author Chill
 */
public class AuthProvider {

	public static String TARGET = "/**";
	public static String REPLACEMENT = "";
	public static String AUTH_KEY = TokenConstant.HEADER;
	private static List<String> defaultSkipUrl = new ArrayList<>();

	/**
	 * 第三方请求单独校验的请求
	 */
	private static List<String> thirdPartyAuthUrl = new ArrayList<>();

	static {
		defaultSkipUrl.add("/example");
		defaultSkipUrl.add("/oauth/token/**");
		defaultSkipUrl.add("/oauth/captcha/**");
		defaultSkipUrl.add("/oauth/clear-cache/**");
		defaultSkipUrl.add("/oauth/user-info");
		defaultSkipUrl.add("/token/**");
		defaultSkipUrl.add("/actuator/health/**");
		defaultSkipUrl.add("/v2/api-docs/**");
		defaultSkipUrl.add("/v2/api-docs-ext/**");
		defaultSkipUrl.add("/auth/**");
		defaultSkipUrl.add("/log/**");
		defaultSkipUrl.add("/menu/routes");
		defaultSkipUrl.add("/menu/auth-routes");
		defaultSkipUrl.add("/menu/top-menu");
		defaultSkipUrl.add("/tenant/info");
		defaultSkipUrl.add("/process/resource-view");
		defaultSkipUrl.add("/process/diagram-view");
		defaultSkipUrl.add("/manager/check-upload");
		defaultSkipUrl.add("/error/**");
		defaultSkipUrl.add("/assets/**");
		defaultSkipUrl.add("/tenant");
		defaultSkipUrl.add("/ws/**");
		defaultSkipUrl.add("/login");
		defaultSkipUrl.add("/auth");
		defaultSkipUrl.add("/captcha");
		defaultSkipUrl.add("/alarm_bigdata");
		defaultSkipUrl.add("/deviceinfo/status");
		//接受中轉站試試數據
		defaultSkipUrl.add("/facilityManage/receiveTranstationInfo");
		defaultSkipUrl.add("/scheduleobject/sync/scheduleObject");// 排班数据同步大数据
		defaultSkipUrl.add("/vehicleinfo/reSyncDevices");// 车辆信息重新同步大数据
		//微信小程序登录接口
		defaultSkipUrl.add("/wx/user/login");
		//大屏接口放行
		defaultSkipUrl.add("/screenview/workingDataCount");
		defaultSkipUrl.add("/facilitytranstationdetail/lastDaysGarbageAmount");
		defaultSkipUrl.add("/eventinfo/countEventGroupByType");
		defaultSkipUrl.add("/staffkpiinsdetail/lostPoints");
		defaultSkipUrl.add("/facilitytranstationdetail/lastDaysGarbageAmountGroupByRegion");
		defaultSkipUrl.add("/alarminfo/currentDay/amountDetails");
		defaultSkipUrl.add("/alarminfo/lastAlarmInfosDaily");
		defaultSkipUrl.add("/polymerization/workStatusChange");
		defaultSkipUrl.add("/weather");
		//微信公众号认证放行
		defaultSkipUrl.add("/mp/portal");
		//easyv大屏放行
		defaultSkipUrl.add("/easyv");

		//区域信息全量同步大数据接口放行
		defaultSkipUrl.add("/smartenv-workarea/workareainfo/workAreaInfo2BigData");

		// 统计分析
		defaultSkipUrl.add("/smartenv-statistic/rptvehicleinfo/syncVehicleInfo");
		defaultSkipUrl.add("/smartenv-statistic/rptpersoninfo/syncPersonInfo");
		defaultSkipUrl.add("/smartenv-statistic/rptvehiclestay/syncVehicleStay");
		defaultSkipUrl.add("/smartenv-statistic/rptpersonstay/syncPersonStay");
		defaultSkipUrl.add("/smartenv-statistic/rptpersonsafe/syncPersonSafe");
		defaultSkipUrl.add("/smartenv-statistic/rptpersonoutofarea/syncPersonOutOfArea");
		defaultSkipUrl.add("/smartenv-statistic/rptvehicleoil/syncVehicleOil");
		defaultSkipUrl.add("/smartenv-statistic/rpttoiletinfo/syncToiletInfo");

		//沧州项目
		defaultSkipUrl.add("/smartenv-statistic/easyv/area/synthInfo");
		defaultSkipUrl.add("/smartenv-statistic/easyv/area/vehicle/operationrate");
		defaultSkipUrl.add("/smartenv-statistic/data/initialSynthInfo");
		defaultSkipUrl.add("/smartenv-vehicle/vehicleinfo/pageByCompany");

		defaultSkipUrl.add("/smartenv-statistic/weighingSite/getLast7DayAllComyany");
		defaultSkipUrl.add("/smartenv-statistic/weighingSite/weighingDataAllRegionByComyany");

		defaultSkipUrl.add("/smartenv-omnic/polymerization/initSynthInfo");

		//行政区域城市
		defaultSkipUrl.add("/administrativeCity/cityName/");

		//根据经纬度反向获取地址
		defaultSkipUrl.add("/smartenv-address/address/getAddressByCoords");

		//公众上报事件相关
		defaultSkipUrl.add("/smartenv-wechat/ma/public/publicEventKpi");
		defaultSkipUrl.add("/smartenv-wechat/wx/user/publicAuth");
		defaultSkipUrl.add("/smartenv-oss/public/oss/object");
		defaultSkipUrl.add("/smartenv-oss/public/oss/objectLink");
		defaultSkipUrl.add("/smartenv-oss/public/oss/objectLink");
		defaultSkipUrl.add("/smartenv-event/public/eventInfo");
		defaultSkipUrl.add("/smartenv-event/eventinfo/getRegionByAddress");
		defaultSkipUrl.add("/smartenv-wechat/wx/user/wxUserInfo");
		defaultSkipUrl.add("/smartenv-event/publicEventKpi");

		defaultSkipUrl.add("/smartenv-event/publicEvent/listPublicEventInfoByWechat");
		defaultSkipUrl.add("/smartenv-event/publicEvent/getDetail");


		defaultSkipUrl.add("/smartenv-websocket/trigger/getPersonAndVehicleTypeByTenant");
		defaultSkipUrl.add("/smartenv-websocket/trigger/cangZScreenPosition");
		defaultSkipUrl.add("/bi/vehicle");
		defaultSkipUrl.add("/smartenv-system/project/simpleProjectList");


		defaultSkipUrl.add("/smartenv-statistic/bi/vehicle/statVehicleWork");

	}

	static{
		//行政区域城市
		thirdPartyAuthUrl.add("/administrativeCity/cityName/");
	}

	/**
	 * 默认无需鉴权的API
	 */
	public static List<String> getDefaultSkipUrl() {
		return defaultSkipUrl;
	}

	public static List<String> getThirdPartyAuthUrl(){
		return thirdPartyAuthUrl;
	}

}
