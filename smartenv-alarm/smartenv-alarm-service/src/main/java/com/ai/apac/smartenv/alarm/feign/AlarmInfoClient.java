package com.ai.apac.smartenv.alarm.feign;

import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.ai.apac.smartenv.alarm.dto.AlarmInfoCountDTO;
import com.ai.apac.smartenv.alarm.dto.AlarmInfoQueryDTO;
import com.ai.apac.smartenv.alarm.entity.AlarmInfo;
import com.ai.apac.smartenv.alarm.service.IAlarmInfoService;
import com.ai.apac.smartenv.alarm.vo.AlarmAmountVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoScreenViewVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tenant.constant.TenantConstant;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * /**
 * Copyright: Copyright (c) 2019 Asiainfo
 * @ClassName: AlarmInfoClient
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/2/19
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/2/19     zhaidx           v1.0.0               修改原因
 */
//@ApiIgnore
@RestController
@AllArgsConstructor
public class AlarmInfoClient implements IAlarmInfoClient {

    @Autowired
    private IAlarmInfoService alarmInfoService;

    @Override
    public R<List<AlarmInfoScreenViewVO>> getBigScreenAlarmList(String tenantId, Long alarmNum) {
        return R.data(alarmInfoService.getLastAlarmInfosDaily(alarmNum,tenantId));
    }

    @Override
    public R<AlarmAmountVO> countAllRuleAlarmAmount(String tenantId) {
        AlarmAmountVO alarmAmountVO = new AlarmAmountVO();
        AlarmInfo alarmInfo = new AlarmInfo();
        if(StringUtil.isNotBlank(AuthUtil.getTenantId())){
            alarmInfo.setTenantId(AuthUtil.getTenantId());
        }else{
            alarmInfo.setTenantId(tenantId);
        }
        alarmInfo.setIsHandle(AlarmConstant.IsHandle.HANDLED_NO);
        alarmInfo.setParentRuleCategoryId(AlarmConstant.PERSON_ABNORMAL_ALARM_CATEGORY);
        alarmAmountVO.setPersonUnusualAlarmCount(alarmInfoService.countAlarmInfoAmount(alarmInfo));
        alarmInfo.setParentRuleCategoryId(AlarmConstant.PERSON_VIOLATION_ALARM_CATEGORY);
        alarmAmountVO.setPersonViolationAlarmCount(alarmInfoService.countAlarmInfoAmount(alarmInfo));
        alarmInfo.setParentRuleCategoryId(AlarmConstant.VEHICLE_VIOLATION_ALARM_CATEGORY);
        alarmAmountVO.setVehicleViolationAlarmCount(alarmInfoService.countAlarmInfoAmount(alarmInfo));
        alarmInfo.setParentRuleCategoryId(AlarmConstant.VEHICLE_OUT_OF_AREA_ALARM_CATEGORY);
        alarmAmountVO.setVehicleOutOfAreaAlarmCount(alarmInfoService.countAlarmInfoAmount(alarmInfo));
        alarmInfo.setParentRuleCategoryId(AlarmConstant.VEHICLE_OVERSPEED_ALARM_CATEGORY);
        alarmAmountVO.setVehicleSpeedingAlarmCount(alarmInfoService.countAlarmInfoAmount(alarmInfo));
        return R.data(alarmAmountVO);
    }

    /**
     * 根据设备编码获取未处理的告警
     *
     * @param deviceCode
     * @return
     */
    @Override
    @GetMapping(GET_NOHANDLE_ALARM_INFO_BY_DEVICECODE)
    public R<List<AlarmInfo>> getNoHandleAlarmInfoByDeviceCode(@RequestParam String deviceCode) {
        AlarmInfo entity = new AlarmInfo();
        entity.setIsHandle(0);
        entity.setDeviceCode(deviceCode);
        return R.data(alarmInfoService.list(Condition.getQueryWrapper(entity)));

    }


    /**
     * 根据设备编码获取未处理的告警数量
     *
     * @param deviceCode
     * @returns
     */
    @Override
    @GetMapping(GET_NOHANDLE_ALARMCOUNT_BY_DEVICECODE)
    public R<Integer> getNoHandleAlarmCountByDeviceCode(@RequestParam String deviceCode) {
        AlarmInfo entity = new AlarmInfo();
        entity.setIsHandle(0);
        entity.setDeviceCode(deviceCode);
        return R.data(alarmInfoService.count(Condition.getQueryWrapper(entity)));

    }


    @Override
    @PostMapping(value = ALARM_INFO)
    public R<List<AlarmInfoHandleInfoVO>> listAlarmInfoByCondition(@RequestBody AlarmInfoQueryDTO alarmInfoQueryDTO) {
        return R.data(alarmInfoService.listAlarmHandleInfoNoPage(alarmInfoQueryDTO));
    }

    @Override
    public R<Integer> countAlarmInfoAmount(String tenantId) {
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setTenantId(tenantId);
        alarmInfo.setIsHandle(AlarmConstant.IsHandle.HANDLED_NO);
        return R.data(alarmInfoService.countAlarmInfoAmount(alarmInfo));
    }

    @Override
    public R<Integer> countAlarmInfoAmountByEntityIds(@RequestBody AlarmInfoCountDTO alarmInfoCountDTO) {
        return R.data(alarmInfoService.countAlarmInfoAmountByEntityIds(alarmInfoCountDTO));
    }

    @Override
    @GetMapping(value = COUNT_ALARM_INFO_AMOUNT_BYENTITY)
    public R<Integer> countNoHandleAlarmInfoByEntity(@RequestParam Long entityId, @RequestParam Long entityType) {
        AlarmInfo entity = new AlarmInfo();
        entity.setIsHandle(0);
        entity.setEntityId(entityId);
        entity.setEntityType(entityType);
        QueryWrapper<AlarmInfo> queryWrapper=new QueryWrapper<>(entity);

        return R.data(alarmInfoService.count(queryWrapper));
    }

    @Override
    @GetMapping(COUNT_ALARM_INFO_BY_CONDITION)
    public R<Long> countAlarmInfoByCondition(AlarmInfoQueryDTO alarmInfoQueryDTO) {
        return R.data(alarmInfoService.countAlarmInfoByCondition(alarmInfoQueryDTO));
    }

    @Override
    public R<List<AlarmInfo>> getUnRegionAlarmInfo() {
        QueryWrapper<AlarmInfo> queryWrapper = new QueryWrapper<AlarmInfo>();
        queryWrapper.lambda().isNotNull(AlarmInfo::getLongitude);
        queryWrapper.lambda().isNull(AlarmInfo::getRegionId);
        return R.data(alarmInfoService.list(queryWrapper));
    }

    @Override
    public R updateAlarmInfo(AlarmInfo alarmInfo) {
        return R.data(alarmInfoService.updateById(alarmInfo));

    }

	/*
	 * aboard报表查询用，尽量别修改
	 */
    @Override
	public R<List<AlarmInfo>> listAlarmInfoByCategory(Long ruleCategoryId, String alarmDate) {
		AlarmInfo entity = new AlarmInfo();
        entity.setIsDeleted(0);;
        entity.setRuleCategoryId(ruleCategoryId);
        QueryWrapper<AlarmInfo> queryWrapper = Condition.getQueryWrapper(entity);
        queryWrapper.likeRight("alarm_time", alarmDate);
        queryWrapper.isNotNull("entity_id");
        queryWrapper.orderByAsc("entity_id");
        queryWrapper.orderByAsc("alarm_time");
        return R.data(alarmInfoService.list(queryWrapper));
	}
}
