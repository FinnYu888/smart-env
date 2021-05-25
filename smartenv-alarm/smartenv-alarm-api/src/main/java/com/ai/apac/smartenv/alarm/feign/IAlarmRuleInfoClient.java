package com.ai.apac.smartenv.alarm.feign;

import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        value = ApplicationConstant.APPLICATION_ALARM_NAME,
        fallback = IAlarmRuleInfoClientFallBack.class
)
public interface IAlarmRuleInfoClient {

    String API_PREFIX = "/client";
    String LIST_ALARM_RULE_BY_TENANT = API_PREFIX + "/list-alarm-rule-by-tenant";
    String LIST_ALARM_RULE_INFO_BY_TYPE = API_PREFIX + "/list-alarm-rule-by-type";
    String GET_ALARM_RULE_INFO_BY_ID = API_PREFIX + "/alarm-rule-by-id";
    String COPY_ALARM_RULE_INFO_BY_TENANT_ID_AND_RULE_ID = API_PREFIX + "/copy-alarm-rule";

    /**
     * 根据租户Id查询告警规则信息
     * @param tenantId
     * @return
     */
    @GetMapping(LIST_ALARM_RULE_BY_TENANT)
    R<List<AlarmRuleInfo>> listAlarmRuleInfoByTenant(@RequestParam String tenantId);

    @GetMapping(LIST_ALARM_RULE_INFO_BY_TYPE)
    R<List<AlarmRuleInfo>> listAlarmRuleInfoByType(@RequestParam Long entityCategoryId);

    /**
     * 根据告警规则id查询告警规则信息
     * @param alarmRuleInfoId
     * @return
     */
    @GetMapping(GET_ALARM_RULE_INFO_BY_ID)
    R<AlarmRuleInfo> getAlarmRuleInfoById(@RequestParam Long alarmRuleInfoId);

    /**
     * 复制默认租户的告警规则数据到新租户，可指定具体某一条规则
     * @param alarmRuleInfo
     * @return
     */
    @PostMapping(COPY_ALARM_RULE_INFO_BY_TENANT_ID_AND_RULE_ID)
    R copyDefaultAlarmRule4SpecifiedTenantOrRuleId(@RequestBody AlarmRuleInfo alarmRuleInfo);
}
