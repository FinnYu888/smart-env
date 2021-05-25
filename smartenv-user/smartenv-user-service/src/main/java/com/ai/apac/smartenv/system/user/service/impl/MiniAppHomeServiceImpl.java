package com.ai.apac.smartenv.system.user.service.impl;

import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.omnic.feign.IRealTimeStatusClient;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.system.user.service.IMiniAppHomeService;
import com.ai.apac.smartenv.system.user.vo.MiniAppHomeDataCountVO;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName MiniAppHomeServiceImpl
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/15 14:11
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class MiniAppHomeServiceImpl  implements IMiniAppHomeService {

    @Autowired
    private IAlarmInfoClient alarmInfoClient;
    @Autowired
    private IEventInfoClient eventInfoClient;
    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public MiniAppHomeDataCountVO getMiniAppHomeDataCount() {

        MiniAppHomeDataCountVO miniAppHomeDataCountVO = new MiniAppHomeDataCountVO();
        miniAppHomeDataCountVO.setAlarmCount(alarmInfoClient.countAlarmInfoAmount(AuthUtil.getTenantId()).getData());
        miniAppHomeDataCountVO.setEventCount(eventInfoClient.countEventDaily(AuthUtil.getTenantId()).getData());

        Query query = new Query();

        query.addCriteria(Criteria.where("tenantId").is(AuthUtil.getTenantId()));
        query.addCriteria(Criteria.where("watchDeviceId").exists(true));
        List<BasicPersonDTO> basicPersonDTOList = mongoTemplate.find(query, BasicPersonDTO.class);
        Map<Integer, Long> collect = basicPersonDTOList.stream().collect(Collectors.groupingBy(BasicPersonDTO::getWorkStatus, Collectors.counting()));
        //静值人员数量
        Long staticPersonCount = collect.containsKey(PersonConstant.PersonStatus.OFF_ONLINE_ALARM) ? collect.get(PersonConstant.PersonStatus.OFF_ONLINE_ALARM) : 0L;
        //在岗人员数量
        Long workingPersonCount = collect.containsKey(PersonConstant.PersonStatus.ONLINE) ? collect.get(PersonConstant.PersonStatus.ONLINE) : 0L;
        //休息人员数量
        Long restPersonCount = collect.containsKey(PersonConstant.PersonStatus.OFF_ONLINE) ? collect.get(PersonConstant.PersonStatus.OFF_ONLINE) : 0L;


        miniAppHomeDataCountVO.setRestPersonCount(restPersonCount);
        miniAppHomeDataCountVO.setStaticPersonCount(staticPersonCount);
        miniAppHomeDataCountVO.setWorkingPersonCount(workingPersonCount);
        miniAppHomeDataCountVO.setShouldWorkPersonCount(restPersonCount+staticPersonCount+workingPersonCount);



        Query query1 = new Query();

        query1.addCriteria(Criteria.where("tenantId").is(AuthUtil.getTenantId()));
        query1.addCriteria(new Criteria().orOperator(Criteria.where("gpsDeviceId").exists(true),Criteria.where("nvrDeviceId").exists(true),Criteria.where("cvrDeviceId").exists(true)));
        List<BasicVehicleInfoDTO> basicVehicleInfoDTOList = mongoTemplate.find(query1, BasicVehicleInfoDTO.class);
        Map<Integer, Long> collect1 = basicVehicleInfoDTOList.stream().collect(Collectors.groupingBy(BasicVehicleInfoDTO::getWorkStatus, Collectors.counting()));

        //在岗车辆数量
        Long workingVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.ONLINE) ? collect1.get(VehicleConstant.VehicleStatus.ONLINE) : 0L;

        //静值车辆数量
        Long staticVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.OFF_ONLINE_ALARM) ? collect1.get(VehicleConstant.VehicleStatus.OFF_ONLINE_ALARM) : 0L;

        //休息车辆数量
        Long restVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.OFF_ONLINE) ? collect1.get(VehicleConstant.VehicleStatus.OFF_ONLINE) : 0L;

        //加水车辆数量
        Long wateringVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.WATERING) ? collect1.get(VehicleConstant.VehicleStatus.WATERING) : 0L;

        //加油车辆数量
        Long oilingVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.OIL_ING) ? collect1.get(VehicleConstant.VehicleStatus.OIL_ING) : 0L;

        miniAppHomeDataCountVO.setOilingVehicleCount(oilingVehicleCount);
        miniAppHomeDataCountVO.setRestVehicleCount(restVehicleCount);
        miniAppHomeDataCountVO.setStaticVehicleCount(staticVehicleCount);
        miniAppHomeDataCountVO.setWorkingVehicleCount(workingVehicleCount);
        miniAppHomeDataCountVO.setWateringVehicleCount(wateringVehicleCount);
        miniAppHomeDataCountVO.setShouldWorkVehicleCount(oilingVehicleCount+restVehicleCount+staticVehicleCount+workingVehicleCount+wateringVehicleCount);


        return miniAppHomeDataCountVO;
    }
}
