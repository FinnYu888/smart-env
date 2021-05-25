package com.ai.apac.smartenv.alarm.feign;

import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import org.springblade.core.tool.api.R;

import java.util.List;

public class IAlarmRuleInfoClientFallBack implements IAlarmRuleInfoClient {
    @Override
    public R<List<AlarmRuleInfo>> listAlarmRuleInfoByTenant(String tenantId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<AlarmRuleInfo> getAlarmRuleInfoById(Long alarmRuleInfoId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R copyDefaultAlarmRule4SpecifiedTenantOrRuleId(AlarmRuleInfo alarmRuleInfo) {
        return R.fail("接收数据失败");
    }

	@Override
	public R<List<AlarmRuleInfo>> listAlarmRuleInfoByType(Long entityCategoryId) {
		return R.fail("接收数据失败");
	}
}
