package com.ai.apac.smartenv.omnic.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ai.apac.smartenv.alarm.cache.AlarmInfoCache;
import com.ai.apac.smartenv.alarm.dto.AlarmInfoQueryDTO;
import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.alarm.vo.AlarmAmountVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.WorkAreaConstant;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.omnic.entity.OmnicPersonInfo;
import com.ai.apac.smartenv.omnic.entity.OmnicVehicleInfo;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.omnic.mapper.RealStatusMapper;
import com.ai.apac.smartenv.omnic.service.RealStatusService;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.person.dto.PersonStatusStatDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.dto.VehicleStatusStatDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.smartenv.cache.util.SmartCache;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ai.apac.smartenv.common.cache.CacheNames.VEHICLE_STATUS_COUNT_MAP;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: RealStatusServiceImpl
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/12  23:52    panfeng          v1.0.0             修改原因
 */
@Service
@Slf4j
public class RealStatusServiceImpl implements RealStatusService {


    @Autowired
    private IVehicleClient vehicleClient;

    @Autowired
    private IPersonClient personClient;


    @Autowired
    private IAlarmInfoClient alarmInfoClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BladeRedis bladeRedis;

    @Override
    public StatusCount selectAllVehicleDeviceStatusCount(String tenantId) {
        StatusCount statusCount = null;

        Query query = Query.query(Criteria.where("tenantId").is(tenantId).and("gpsDeviceCode").ne(null));
        List<BasicVehicleInfoDTO> basicVehicleInfoDTOS = mongoTemplate.find(query, BasicVehicleInfoDTO.class);
        Map<Integer, Long> collect = basicVehicleInfoDTOS.stream().collect(Collectors.groupingBy(BasicVehicleInfoDTO::getWorkStatus, Collectors.counting()));
//        Stream<BasicVehicleInfoDTO> basicVehicleInfoDTOStream = basicVehicleInfoDTOS.stream().filter(basicVehicleInfoDTO -> basicVehicleInfoDTO.getTodayAlarmCount() > 0);
//        Long alarm = 0L;
//        if (basicVehicleInfoDTOStream.isParallel()) {
//            alarm = basicVehicleInfoDTOStream.count();
//        }

        /**
         * 获取告警数量统计
         */
        AlarmAmountVO alarmAmountVO = AlarmInfoCache.getSummaryAlarmAmount(tenantId);
        statusCount = new StatusCount();
        statusCount.setAlarm(Long.valueOf(alarmAmountVO.getVehicleOutOfAreaAlarmCount() + alarmAmountVO.getVehicleSpeedingAlarmCount() + alarmAmountVO.getVehicleViolationAlarmCount()));

        //静值
        statusCount.setDeparture(collect.containsKey(WorkAreaConstant.WorkStatus.ON_STANDBY) ? collect.get(WorkAreaConstant.WorkStatus.ON_STANDBY) : 0L);
        //工作中
        statusCount.setWorking(collect.containsKey(WorkAreaConstant.WorkStatus.ONLINE) ? collect.get(WorkAreaConstant.WorkStatus.ONLINE) : 0L);
        //休息中
        statusCount.setSitBack(collect.containsKey(WorkAreaConstant.WorkStatus.REST) ? collect.get(WorkAreaConstant.WorkStatus.REST) : 0L);
        //加水中
        statusCount.setWaterCnt(collect.containsKey(WorkAreaConstant.WorkStatus.WATERING) ? collect.get(WorkAreaConstant.WorkStatus.WATERING) : 0L);
        //加油中
        statusCount.setOilCnt(collect.containsKey(WorkAreaConstant.WorkStatus.OIL_ING) ? collect.get(WorkAreaConstant.WorkStatus.OIL_ING) : 0L);
        //休假
        statusCount.setVacationCnt(collect.containsKey(WorkAreaConstant.WorkStatus.VACATION) ? collect.get(WorkAreaConstant.WorkStatus.VACATION) : 0L);
        //车辆维修中
        statusCount.setMaintainCnt(collect.containsKey(WorkAreaConstant.WorkStatus.VEHICLE_MAINTAIN) ? collect.get(WorkAreaConstant.WorkStatus.VEHICLE_MAINTAIN) : 0L);
        //未排班
        statusCount.setUnArrangeCnt(collect.containsKey(WorkAreaConstant.WorkStatus.UN_ARRANGE) ? collect.get(WorkAreaConstant.WorkStatus.UN_ARRANGE) : 0L);

        return statusCount;
    }

    @Override
    public List<OmnicVehicleInfo> getVehicleInfoByStatus(Integer status, String tenantId) {

        Query query = Query.query(Criteria.where("tenantId").is(tenantId).and("workStatus").is(status));
        List<BasicVehicleInfoDTO> basicVehicleInfoDTOS = mongoTemplate.find(query, BasicVehicleInfoDTO.class);
        List<OmnicVehicleInfo> omnicVehicleInfos = BeanUtil.copy(basicVehicleInfoDTOS, OmnicVehicleInfo.class);
        return omnicVehicleInfos;
    }

    @Override
    public StatusCount selectAllPersonDeviceStatusCount(String tenantId) {
        Query query = Query.query(Criteria.where("tenantId").is(tenantId).and("watchDeviceCode").ne(null));
        List<BasicPersonDTO> basicPersonDTOS = mongoTemplate.find(query, BasicPersonDTO.class);
        Map<Integer, Long> collect = basicPersonDTOS.stream().collect(Collectors.groupingBy(BasicPersonDTO::getWorkStatus, Collectors.counting()));
//        Stream<BasicPersonDTO> basicVehicleInfoDTOStream = basicPersonDTOS.stream().filter(basicVehicleInfoDTO -> basicVehicleInfoDTO.getTodayAlarmCount() > 0);
//        Long alarm = 0L;
//        if (basicVehicleInfoDTOStream.isParallel()) {
//            alarm = basicVehicleInfoDTOStream.count();
//        }

        AlarmAmountVO alarmAmountVO = AlarmInfoCache.getSummaryAlarmAmount(tenantId);
        StatusCount statusCount = new StatusCount();
        statusCount.setAlarm(Long.valueOf(alarmAmountVO.getPersonUnusualAlarmCount() + alarmAmountVO.getPersonViolationAlarmCount()));

        //静值
        statusCount.setDeparture(collect.containsKey(WorkAreaConstant.WorkStatus.ON_STANDBY) ? collect.get(WorkAreaConstant.WorkStatus.ON_STANDBY) : 0L);
        //工作中
        statusCount.setWorking(collect.containsKey(WorkAreaConstant.WorkStatus.ONLINE) ? collect.get(WorkAreaConstant.WorkStatus.ONLINE) : 0L);
        //休息中
        statusCount.setSitBack(collect.containsKey(WorkAreaConstant.WorkStatus.REST) ? collect.get(WorkAreaConstant.WorkStatus.REST) : 0L);
        //休假
        statusCount.setVacationCnt(collect.containsKey(WorkAreaConstant.WorkStatus.VACATION) ? collect.get(WorkAreaConstant.WorkStatus.VACATION) : 0L);
        //未排班
        statusCount.setUnArrangeCnt(collect.containsKey(WorkAreaConstant.WorkStatus.UN_ARRANGE) ? collect.get(WorkAreaConstant.WorkStatus.UN_ARRANGE) : 0L);

        return statusCount;
    }

    @Override
    public List<OmnicPersonInfo> getPersonInfoByStatus(Integer status, String tenantId) {
        Query query = Query.query(Criteria.where("tenantId").is(tenantId).and("workStatus").is(status));
        List<BasicPersonDTO> basicVehicleInfoDTOS = mongoTemplate.find(query, BasicPersonDTO.class);
        List<OmnicPersonInfo> omnicVehicleInfos = BeanUtil.copy(basicVehicleInfoDTOS, OmnicPersonInfo.class);
        return omnicVehicleInfos;
    }

}
