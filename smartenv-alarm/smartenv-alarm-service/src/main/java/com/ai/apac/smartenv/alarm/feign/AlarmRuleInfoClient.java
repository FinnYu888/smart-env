package com.ai.apac.smartenv.alarm.feign;

import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@AllArgsConstructor
public class AlarmRuleInfoClient implements IAlarmRuleInfoClient {

    @Autowired
    private IAlarmRuleInfoService alarmRuleInfoService;

    @Override
    @GetMapping(LIST_ALARM_RULE_BY_TENANT)
    public R<List<AlarmRuleInfo>> listAlarmRuleInfoByTenant(String tenantId) {
        return R.data(alarmRuleInfoService.list(new LambdaQueryWrapper<AlarmRuleInfo>().eq(AlarmRuleInfo::getTenantId, tenantId)));
    }

    @Override
    public R<List<AlarmRuleInfo>> listAlarmRuleInfoByType(Long entityCategoryId) {
		return R.data(alarmRuleInfoService.list(new LambdaQueryWrapper<AlarmRuleInfo>().eq(AlarmRuleInfo::getEntityCategoryId, entityCategoryId)));
    }

    @Override
    @GetMapping(GET_ALARM_RULE_INFO_BY_ID)
    public R<AlarmRuleInfo> getAlarmRuleInfoById(Long alarmRuleInfoId) {
        return R.data(alarmRuleInfoService.getById(alarmRuleInfoId));
    }

    @Override
    @PostMapping(COPY_ALARM_RULE_INFO_BY_TENANT_ID_AND_RULE_ID)
    public R copyDefaultAlarmRule4SpecifiedTenantOrRuleId(@RequestBody AlarmRuleInfo alarmRuleInfo) {
        @NotEmpty(message = "租户Id不能为空") String tenantId = alarmRuleInfo.getTenantId();
        return R.status(alarmRuleInfoService.copyDefaultAlarmRule4SpecifiedTenantOrRuleId(tenantId, alarmRuleInfo.getId()));
    }

}
