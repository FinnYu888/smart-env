package com.ai.apac.smartenv.alarm.feign;

import com.ai.apac.smartenv.alarm.dto.AlarmInfoCountDTO;
import com.ai.apac.smartenv.alarm.dto.AlarmInfoQueryDTO;
import com.ai.apac.smartenv.alarm.entity.AlarmInfo;
import com.ai.apac.smartenv.alarm.vo.AlarmAmountVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoScreenViewVO;
import org.springblade.core.tool.api.R;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: AlarmInfoClientFallBack
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/19  13:32    panfeng          v1.0.0             修改原因
 */
public class IAlarmInfoClientFallBack implements IAlarmInfoClient{


    @Override
    public R<List<AlarmInfoScreenViewVO>> getBigScreenAlarmList(String tenantId, Long alarmNum) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<AlarmAmountVO> countAllRuleAlarmAmount(String tenantId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<AlarmInfo>> getNoHandleAlarmInfoByDeviceCode(String deviceCode) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<Integer> getNoHandleAlarmCountByDeviceCode(String deviceCode) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<AlarmInfoHandleInfoVO>> listAlarmInfoByCondition(AlarmInfoQueryDTO alarmInfoQueryDTO) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<Integer> countAlarmInfoAmount(String tenantId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<Integer> countAlarmInfoAmountByEntityIds(AlarmInfoCountDTO alarmInfoCountDTO) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<Integer> countNoHandleAlarmInfoByEntity(Long entityId, Long entityType) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<Long> countAlarmInfoByCondition(AlarmInfoQueryDTO alarmInfoQueryDTO) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<AlarmInfo>> getUnRegionAlarmInfo() {
        return R.fail("接收数据失败");
    }

    @Override
    public R updateAlarmInfo(AlarmInfo alarmInfo) {
        return R.fail("接收数据失败");
    }

	@Override
	public R<List<AlarmInfo>> listAlarmInfoByCategory(Long ruleCategoryId, String alarmDate) {
		return R.fail("接收数据失败");
	}

}
