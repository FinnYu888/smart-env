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
package com.ai.apac.smartenv.common.cache;

/**
 * 缓存名
 *
 * @author Chill
 */
public interface CacheNames {

    String CAPTCHA_KEY = "blade:auth::blade:captcha:";

    interface ExpirationTime {
        /**
         * 过期时间7天,单位是秒
         */
        Long EXPIRATION_TIME_7DAYS = 604800L;

        /**
         * 过期时间5天,单位是秒
         */
        Long EXPIRATION_TIME_5DAYS = 432000L;

        /**
         * 过期时间24小时,单位是秒
         */
        Long EXPIRATION_TIME_24HOURS = 86400L;

        /**
         * 过期时间1小时,单位是秒
         */
        Long EXPIRATION_TIME_1HOURS = 3600L;

        /**
         * 过期时间2小时,单位是秒
         */
        Long EXPIRATION_TIME_2HOURS = 7200L;

        /**
         * 过期时间10分钟,单位是秒
         */
        Long EXPIRATION_TIME_10MIN = 600L;
    }



    String NOTICE_ONE = "notice:one";

    String WEATHER = "smartenv:weather";
    String TOKEN = "smartenv:token";

    String ROLE_NAME_MAP = "smartenv:roleNameMap";
    String ROLE_MAP = "smartenv:roleMap";
    String TENANT_ROLE_MAP = "smartenv:tenantRoleMap";

    String DEPT_FULL_NAME_MAP = "smartenv:deptFullNameMap";
    String DEPT_NAME_MAP = "smartenv:deptNameMap";
    String DEPT_MAP = "smartenv:deptMap";
    String MENU_MAP = "smartenv:menuMap";
    String MENU_CODE_MAP = "smartenv:menuCodeMap";
    String USER_ID_MAP = "smartenv:userIdMap";
    String USER_ACCT_MAP = "smartenv:userAcctMap";
    String TENANT_MAP = "smartenv:tenantMap";
    String DICT_MAP = "smartenv:dictMap";
    String BIZ_DICT_MAP = "smartenv:bizDictMap";
    String PERSON_MAP = "smartenv:personMap";
    String PERSON_COUNT_MAP = "smartenv:personCountMap";
    String PERSON_WATCH_STATUS_MAP = "smartenv:personWatchStatusMap";
    String PERSON_USER_REL_MAP = "smartenv:personUserRelMap";
    String PERSON_ACCOUNT_MAP = "smartenv:personAccountMap";
    String PERSON_STATUS_IMG = "smartenv:personStatusImg";
    String VEHICLE_MAP = "smartenv:vehicleMap";
    String VEHICLE_COUNT_MAP = "smartenv:vehicleCount";
    String VEHICLE_ACC_STATUS_MAP = "smartenv:vehicleAccStatusMap";
    String VEHICLE_STATUS_IMG = "smartenv:vehicleStatusImg";
    String VEHICLE_STATUS_COUNT_MAP = "smartenv:vehicleStatusCountMap";
    String CITY_MAP = "smartenv:cityMap";
    String ADMIN_CITY_MAP = "smartenv:adminCityMap";
    String CITY_NAME_MAP = "smartenv:cityNameMap";
    String ADMIN_CITY_NAME_MAP = "smartenv:adminCityNameMap";
    String CITY_TREE = "smartenv:cityTree";
    String ADMIN_CITY_TREE = "smartenv:adminCityTree";
    String DEVICE_ID_MAP = "smartenv:deviceIdMap";
    String WORKAREA_ID_MAP = "smartenv:workareaIdMap";
    String DEVICE_CODE_MAP = "smartenv:deviceCodeMap";
    String ALL_DEVICE_CODE_MAP = "smartenv:allDeviceCodeMap";
    String ALL_DEVICE_ID_MAP = "smartenv:allDeviceIdMap";
    String SCHEDULE_MAP = "smartenv:scheduleMap";
    String SCHEDULE_OBJECT_MAP = "smartenv:scheduleObjectMap";
    String HOME_DATA = "smartenv:homeData";
    String Polymerization_DATA = "smartenv:polymerizationData";
    String BIGSCREEN_DATA = "smartenv:bigScreenData";
    String SCHEDULE_OBJECT_ASYNC_MAP = "smartenv:scheduleObjectAsyncMap";
    String WORKING_VEHICLE_COUNT_TODAY = "smartenv:workingVehicleCountToday";
    String WORKING_PERSON_COUNT_TODAY = "smartenv:workingPersonCountToday";
    String WORKAREA_REL_ASYNC_MAP = "smartenv:workareaRelAsyncMap";
    String KPI_CATALOG_MAP = "smartenv:kpiCatalogMap";
    String EVENT_KPI_TPL_MAP = "smartenv:eventKpiTplMap";
    String KPI_DEF_MAP = "smartenv:kpiDefMap";
    String EVENT_KPI_CATALOG_MAP = "smartenv:eventKpiCatalogMap";
    String EVENT_KPI_REL_MAP = "smartenv:eventKpiRelMap";
    String EVENT_KPI_DEF_MAP = "smartenv:eventKpiDefMap";
    String PUBLIC_EVENT_KPI_MAP = "smartenv:publicEventKpifMap";
    String PUBLIC_EVENT_KPI_ID_MAP = "smartenv:publicEventKpIdMap";
    String EVENT_KPI_TPL_DEF_MAP = "smartenv:eventKpiTplDefMap";
    String EVENT_COUNT_MAP = "smartenv:eventCountMap";
    String DEVICE_LAST_INFO = "smartenv:deviceTrackInfo";
    String ALARM_RULE_INFO_MAP = "smartenv:alarmRuleInfoMap";
    String UN_HANDLE_ALARM_COUNT_MAP = "smartenv:unHandleAlarmCountMap";
    String SUMMARY_ALARM_COUNT_MAP = "smartenv:summaryAlarmCountMap";
    String INVENTORY_MAP = "smartenv:inventory";

    String VEHICLE_IMPORT = "smartenv:vehicleImport";
    String PERSON_IMPORT = "smartenv:personImport";
    String SIM_IMPORT = "smartenv:simImport";
    String PERSON_DEVICE_IMPORT = "smartenv:personDeviceImport";
    String VEHICLE_MONITOR_DEVICE_IMPORT = "smartenv:vehicleMonitorDeviceImport";
    String VEHICLE_SENSOR_DEVICE_IMPORT = "smartenv:vehicleSensorDeviceImport";
    String INVENTORY_IMPORT = "smartenv:inventoryImport";
    String ASHCAN_IMPORT = "smartenv:ashcanImport";
    String ASHCAN_MAP = "smartenv:ashcan";
    String ASHCAN_QR_CODE_MAP = "smartenv:ashcanQrCode";

    /**
     * 中转站数量
     */
    String FACILITY_COUNT_MAP = "smartenv:facilityCountMap";

    /**
     * 垃圾桶数量
     */
    String ASHCAN_COUNT_MAP = "smartenv:ashcanCountMap";

    /**
     * 公厕数量
     */
    String TOILET_COUNT_MAP = "smartenv:toiletCountMap";

    /**
     * 存放车辆和租户的对应关系
     */
    String VEHICLE_TENANT_MAP = "smartenv:vehicleTenantMap";

    /**
     * 存放人员和租户的对应关系
     */
    String PERSON_TENANT_MAP = "smartenv:personTenantMap";

    /**
     * 存放设备和租户的对应关系
     */
    String DEVICE_TENANT_MAP = "smartenv:deviceTenantMap";

    /**
     * 存放设备数据
     */
    String DEVICE_MAP = "smartenv:deviceMap";

    /**
     * 存放设备绑定关系数据
     */
    String DEVICE_REL_MAP = "smartenv:devicReleMap";

    /**
     * 存放设备与实体的对应关系
     */
    String DEVICE_REL_TENANT_MAP = "smartenv:deviceRelTenantMap";

    /**
     * 存放业务实体分类信息
     */
    String ENTITY_CATEGORY_MAP = "smartenv:entityCategoryMap";

    /**
     * 存放业务实体分类信息
     */
    String VEHICLE_CATEGORY_MAP = "smartenv:vehicleCategoryMap";
    String VEHICLE_WORK_TYPE_MAP = "smartenv:vehicleWorkTypeMap";

    /**
     * 存放岗位信息
     */
    String STATION_MAP = "smartenv:stationMap";

    /**
    * 中转站臭味级别
    */
    String FACILITY_ODOR = "smartenv:facility:odorLevel";
    /**
     * 库存管理物资类型（大类）
     */
    String INVENTORY_RES_TYPE = "smartenv:inventory:resType";
    /**
     * 库存管理物资规格（小类）
     */
    String INVENTORY_RES_SPEC = "smartenv:inventory:resSpec";
    /**
     * 库存管理物资类型/物资规格名称
     */
    String INVENTORY_RES_TYPE_RES_SPEC_NAME = "smartenv:inventory:resTypeResSpecName";
    /**
     * 公司
     */
    String COMPANY_MAP = "smartenv:companyMap";

    /**
     * 项目
     */
    String PROJECT_MAP = "smartenv:projectMap";
    String PROJECT_CODE_MAP = "smartenv:projectCodeMap";

    /**
     * 每个用户关联的项目
     */
    String ACCOUNT_PROJECT_MAP = "smartenv:accountProjectMap";

    /**
     * 微信小程序登录token
     */
    String WX_MA_TOKEN = "smartenv:wx:ma";

    String USER_ONLINE = "smartenv:online:user";

    /**
     * 业务实体未处理告警数理
     */
    String ENTITY_UNHANDLED_ALARM_COUNT = "smartenv:alarm:entity";
}
