package com.ai.apac.smartenv.alarm.feign;

import com.ai.apac.smartenv.alarm.dto.AlarmInfoCountDTO;
import com.ai.apac.smartenv.alarm.dto.AlarmInfoQueryDTO;
import com.ai.apac.smartenv.alarm.entity.AlarmInfo;
import com.ai.apac.smartenv.alarm.vo.AlarmAmountVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoScreenViewVO;
import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: IAlarmInfoClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/19  13:31    panfeng          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_ALARM_NAME,
        fallback = IAlarmInfoClientFallBack.class
)
public interface IAlarmInfoClient {

    String API_PREFIX = "/client";
    String ALARM_INFO = API_PREFIX + "/alarm-info";
    String GET_NOHANDLE_ALARMCOUNT_BY_DEVICECODE = API_PREFIX + "/get-noHandle-alarmCount-By-DeviceCode";
    String GET_NOHANDLE_ALARM_INFO_BY_DEVICECODE = API_PREFIX + "/get-noHandle-alarmInfo-By-DeviceCode";
    String COUNT_ALARM_INFO_AMOUNT = API_PREFIX + "/count-AlarmInfo-Amount";
    String COUNT_ALARM_INFO_AMOUNT_BY_ENTITYIDS = API_PREFIX + "/count-alarmInfo-amount-by-entityIds";
    String COUNT_ALARM_INFO_AMOUNT_BYENTITY = API_PREFIX + "/count-AlarmInfo-Amount-byentity";
    String COUNT_ALARM_INFO_BY_CONDITION = API_PREFIX + "/count-alarm-info-by-condition";
    String API_GET_UNREGION_ALARM= API_PREFIX + "/unregion-alarm";

    String API_POST_ALARM_REGION = API_PREFIX + "/alarm-region";

    String COUNT_ALL_RULE_ALARM_AMOUNT = API_PREFIX + "/count-all-rule-alarm-amount";

    String GET_BIGSCREEN_ALARM_LIST = API_PREFIX + "/getBigScreenAlarmList";

    String LIST_ALARM_INFO_BY_CATEGORY = API_PREFIX + "/list-alarm-info-by-category";

    @GetMapping(GET_BIGSCREEN_ALARM_LIST)
    R<List<AlarmInfoScreenViewVO>> getBigScreenAlarmList(@RequestParam("tenantId") String tenantId,@RequestParam("alarmNum") Long alarmNum);

    @GetMapping(COUNT_ALL_RULE_ALARM_AMOUNT)
    R<AlarmAmountVO> countAllRuleAlarmAmount(@RequestParam("tenantId") String tenantId);

    /**
     * 根据设备编码获取所有未处理的告警
     *
     * @param deviceCode
     * @return
     */
    @GetMapping(GET_NOHANDLE_ALARM_INFO_BY_DEVICECODE)
    R<List<AlarmInfo>> getNoHandleAlarmInfoByDeviceCode(@RequestParam String deviceCode);

    @GetMapping(LIST_ALARM_INFO_BY_CATEGORY)
    R<List<AlarmInfo>> listAlarmInfoByCategory(@RequestParam Long ruleCategoryId, @RequestParam String alarmDate);

    /**
     * 根据设备编码获取所有未处理告警的数量
     *
     * @param deviceCode
     * @return
     */
    @GetMapping(GET_NOHANDLE_ALARMCOUNT_BY_DEVICECODE)
    R<Integer> getNoHandleAlarmCountByDeviceCode(@RequestParam String deviceCode);

    @PostMapping(value = ALARM_INFO)
    R<List<AlarmInfoHandleInfoVO>> listAlarmInfoByCondition(@RequestBody AlarmInfoQueryDTO alarmInfoQueryDTO);

    @GetMapping(value = COUNT_ALARM_INFO_AMOUNT)
    R<Integer> countAlarmInfoAmount(@RequestParam("tenantId") String tenantId);

    @PostMapping(value = COUNT_ALARM_INFO_AMOUNT_BY_ENTITYIDS)
    R<Integer> countAlarmInfoAmountByEntityIds(@RequestBody AlarmInfoCountDTO alarmInfoCountDTO);

    @GetMapping(value = COUNT_ALARM_INFO_AMOUNT_BYENTITY)
    R<Integer> countNoHandleAlarmInfoByEntity(@RequestParam Long entityId,@RequestParam Long entityType);

    /**
     * 根据条件统计告警数量
     * @param alarmInfoQueryDTO
     * @return
     */
    @GetMapping(COUNT_ALARM_INFO_BY_CONDITION)
    R<Long> countAlarmInfoByCondition(AlarmInfoQueryDTO alarmInfoQueryDTO);

    /**
     * 根据facilityId找设施
     * @return
     */
    @GetMapping(API_GET_UNREGION_ALARM)
    R<List<AlarmInfo>> getUnRegionAlarmInfo();

    @PostMapping(API_POST_ALARM_REGION)
    R updateAlarmInfo(@RequestBody AlarmInfo alarmInfo);
}