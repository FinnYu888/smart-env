package com.ai.apac.smartenv.omnic.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.enums.PersonStatusEnum;
import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.common.enums.WorkStatusEnum;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.common.utils.OkhttpUtil;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.cache.DeviceRelCache;
import com.ai.apac.smartenv.device.dto.DevicePersonInfoDto;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.facility.dto.BasicFacilityDTO;
import com.ai.apac.smartenv.facility.entity.AshcanInfo;
import com.ai.apac.smartenv.facility.entity.FacilityInfo;
import com.ai.apac.smartenv.facility.feign.IAshcanClient;
import com.ai.apac.smartenv.facility.feign.IFacilityClient;
import com.ai.apac.smartenv.omnic.dto.*;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.omnic.feign.IStatisticsClient;
import com.ai.apac.smartenv.omnic.service.IDataChangeEventService;
import com.ai.apac.smartenv.omnic.service.PolymerizationService;
import com.ai.apac.smartenv.omnic.service.RealStatusService;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.person.dto.PersonDeviceStatusCountDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.EntityCategoryCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.constant.RegionConstant;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.cache.VehicleCategoryCache;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.dto.VehicleDeviceStatusCountDTO;
import com.ai.apac.smartenv.vehicle.dto.VehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.entity.VehicleWorkType;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.websocket.feign.IBigScreenDataClient;
import com.ai.apac.smartenv.websocket.feign.IHomeDataClient;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.common.constant.WsMonitorEventConstant.EventType.PERSON_WORK_STATUS_EVENT;
import static com.ai.apac.smartenv.common.constant.WsMonitorEventConstant.EventType.VEHICLE_WORK_STATUS_EVENT;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: PolymerizationServiceImpl
 * @Description: 车辆 人员聚合树service
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/11
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/11  17:04    panfeng          v1.0.0             修改原因
 */
@Service
@Slf4j
public class PolymerizationServiceImpl implements PolymerizationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IPersonClient personClient;

    @Autowired
    private IVehicleClient vehicleClient;

    @Autowired
    private IDeviceClient deviceClient;

    @Autowired
    private IScheduleClient scheduleClient;

    @Autowired
    private IWorkareaRelClient workareaRelClient;
    @Autowired
    private IWorkareaClient workareaClient;

    @Autowired
    private IHomeDataClient homeDataClient;

    @Autowired
    private IBigScreenDataClient bigScreenDataClient;
    @Autowired
    private IFacilityClient facilityClient;
    @Autowired
    private IAshcanClient ashcanClient;
    @Autowired
    private IDictClient dictClient;
    @Autowired
    private ISysClient iSysClient;
    @Autowired
    private IDeviceRelClient deviceRelClient;
    @Autowired
    private IEventInfoClient iEventInfoClient;

    @Autowired
    private IProjectClient projectClient;


    @Autowired
    private IEntityCategoryClient entityCategoryClient;

    @Autowired
    private IDataChangeEventService dataChangeEventService;

    @Autowired
    private RealStatusService realStatusService;


    /**
     * 根据项目编码、设备在线统计人员设备数量
     *
     * @param projectCode
     * @param deviceStatus
     * @return
     */
    private Long countPersonByDeviceStatus(String projectCode, Long deviceStatus) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("tenantId").is(projectCode).and("deviceStatus").is(deviceStatus));
//        Long count = mongoTemplate.count(query, BasicPersonDTO.class);
//        if (count == null || count < 0) {
//            return 0L;
//        }
        R<PersonDeviceStatusCountDTO> result = personClient.getPersonDeviceStatusStat(projectCode);
        if(result.isSuccess() && result.getData() != null){
            return result.getData().getOnPersonCount().longValue();
        }
        return 0L;
    }

    /**
     * 根据项目编码、设备在线统计车辆设备数量
     *
     * @param projectCode
     * @param deviceStatus
     * @return
     */
    private Long countVehicleByDeviceStatus(String projectCode, Long deviceStatus) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("tenantId").is(projectCode).and("deviceStatus").is(deviceStatus));
//        Long count = mongoTemplate.count(query, BasicVehicleInfoDTO.class);
//        if (count == null || count < 0) {
//            return 0L;
//        }
        R<VehicleDeviceStatusCountDTO> result = vehicleClient.getVehicleDeviceStatusStat(projectCode);
        if(result.isSuccess() && result.getData() != null){
            return result.getData().getOnVehicleCount().longValue();
        }
        return 0L;
    }


    @Override
    public void initSynthInfo(String tenantId) {
        SynthInfoDTO synthInfoDTO = new SynthInfoDTO();

        Project project = projectClient.getProjectById(tenantId).getData();
        if (ObjectUtil.isNotEmpty(project) && ObjectUtil.isNotEmpty(project.getCompanyId())) {
            Integer allPersonCount = personClient.countPersonByTenantId(tenantId, "").getData();
            synthInfoDTO.setAllPersonCount(allPersonCount.longValue());

//            Integer onlinePersonCount = personClient.countPersonByTenantId(tenantId, DeviceConstant.DeviceStatus.ON).getData();
            Long onlinePersonCount = this.countPersonByDeviceStatus(tenantId, Long.valueOf(DeviceConstant.DeviceStatus.ON));
            synthInfoDTO.setOnlinePersonCount(onlinePersonCount);

            Integer personCount = ScheduleCache.getWorkingCountForPersonToday(tenantId);
            synthInfoDTO.setPersonCount(personCount.longValue());
            StatusCount statusCount = realStatusService.selectAllPersonDeviceStatusCount(tenantId);
            Long workingPersonCount = 0L;
            if (ObjectUtil.isNotEmpty(statusCount)) {
                //Long restPersonCount = statusCount.getSitBack()==null?0:statusCount.getSitBack();
                Long workingPersonCount_ = statusCount.getWorking() == null ? 0 : statusCount.getWorking();
                Long staticPersonCount = statusCount.getDeparture() == null ? 0 : statusCount.getDeparture();
                //workingPersonCount = restPersonCount+workingPersonCount_+staticPersonCount;
                workingPersonCount = workingPersonCount_ + staticPersonCount;
            }
            synthInfoDTO.setWorkingPersonCount(workingPersonCount);

            Integer allVehicleCount = vehicleClient.countVehicleByTenantId(tenantId, "").getData();
            synthInfoDTO.setAllVehicleCount(allVehicleCount.longValue());

//            Integer onlineVehicleCount = vehicleClient.countVehicleByTenantId(tenantId, DeviceConstant.DeviceStatus.ON).getData();
            Long onlineVehicleCount = this.countVehicleByDeviceStatus(tenantId, Long.valueOf(DeviceConstant.DeviceStatus.ON));
            synthInfoDTO.setOnlineVehicleCount(onlineVehicleCount.longValue());

            Integer vehicleCount = ScheduleCache.getWorkingCountForVehicleToday(tenantId);
            synthInfoDTO.setVehicleCount(vehicleCount.longValue());
            StatusCount statusCount2 = realStatusService.selectAllVehicleDeviceStatusCount(tenantId);
            Long workingVehicleCount = 0L;
            if (ObjectUtil.isNotEmpty(statusCount2)) {
                //Long restVehicleCount = statusCount2.getSitBack()==null?0:statusCount2.getSitBack();
                Long workingVehicleCount_ = statusCount2.getWorking() == null ? 0 : statusCount2.getWorking();
                Long staticVehicleCount = statusCount2.getDeparture() == null ? 0 : statusCount2.getDeparture();
                Long wateringVehicleCount = statusCount2.getWaterCnt() == null ? 0 : statusCount2.getWaterCnt();
                Long oilingVehicleCount = statusCount2.getOilCnt() == null ? 0 : statusCount2.getOilCnt();
                //workingVehicleCount = restVehicleCount+workingVehicleCount_+staticVehicleCount+wateringVehicleCount+oilingVehicleCount;
                workingVehicleCount = workingVehicleCount_ + staticVehicleCount + wateringVehicleCount + oilingVehicleCount;
            }
            synthInfoDTO.setWorkingVehicleCount(workingVehicleCount);

            Integer facilityCount = facilityClient.countFacilityByTenantId(tenantId, "").getData();
            synthInfoDTO.setFacilityCount(facilityCount.longValue());
            synthInfoDTO.setWorkingFacilityCount(facilityCount.longValue());

            synthInfoDTO.setCompanyId(project.getCompanyId().toString());
            synthInfoDTO.setProjectCode(tenantId);
            synthInfoDTO.setAreaCode(project.getAdcode().toString());
            synthInfoDTO.setProjectName(project.getProjectName());

            /**
             * 统计该租户下的车辆工作面积，人员工作面积
             */
            String personWorkAreaType = "3";//默认都是从第三方项目方取数据
            String vehicleWorkAreaType = "3";//默认都是从第三方项目方取数据

            String personWorkAreaParam = DictBizCache.getValue(tenantId, "workarea_area", 1);
            if (ObjectUtil.isNotEmpty(personWorkAreaParam) && personWorkAreaParam.split(":").length == 2
                    && "1".equals(personWorkAreaParam.split(":")[0])) {
                //配置了人员工作区域面积取值来源且是局方
                personWorkAreaType = "1";
                synthInfoDTO.setPersonWorkAreaCount(new BigDecimal(Double.parseDouble(personWorkAreaParam.split(":")[1])).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            String vehicleWorkAreaParam = DictBizCache.getValue(tenantId, "workarea_area", 2);
            if (ObjectUtil.isNotEmpty(vehicleWorkAreaParam) && vehicleWorkAreaParam.split(":").length == 2
                    && "1".equals(vehicleWorkAreaParam.split(":")[0])) {
                //配置了车辆工作区域面积取值来源且是局方
                vehicleWorkAreaType = "1";
                synthInfoDTO.setVehicleWorkAreaCount(new BigDecimal(Double.parseDouble(vehicleWorkAreaParam.split(":")[1])).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }

            if ("3".equals(personWorkAreaType) || "3".equals(vehicleWorkAreaType)) {
                List<WorkareaInfo> workareaInfos = workareaClient.getWorkareaInfoByTenantId(tenantId).getData();
                Double personLineArea = 0.00;
                Double personAreaArea = 0.00;
                Double vehicleLineArea = 0.00;
                Double vehicleAreaArea = 0.00;
                if (ObjectUtil.isNotEmpty(workareaInfos) && workareaInfos.size() > 0) {
                    for (WorkareaInfo workareaInfo : workareaInfos) {
                        if ("3".equals(vehicleWorkAreaType) && workareaInfo.getAreaType().toString().equals("1") && workareaInfo.getBindType().toString().equals("2")) {
                            //车辆路线
                            String width = workareaInfo.getWidth();
                            String length = workareaInfo.getLength();
                            if (ObjectUtil.isNotEmpty(width) && ObjectUtil.isNotEmpty(length)) {
                                vehicleLineArea = vehicleLineArea + Double.parseDouble(width) * Double.parseDouble(length);
                            }
                        } else if ("3".equals(personWorkAreaType) && workareaInfo.getAreaType().toString().equals("1") && workareaInfo.getBindType().toString().equals("1")) {
                            //人员路线
                            String width = workareaInfo.getWidth();
                            String length = workareaInfo.getLength();
                            if (ObjectUtil.isNotEmpty(width) && ObjectUtil.isNotEmpty(length)) {
                                personLineArea = personLineArea + Double.parseDouble(width) * Double.parseDouble(length);
                            }
                        } else if ("3".equals(vehicleWorkAreaType) && workareaInfo.getAreaType().toString().equals("2") && workareaInfo.getBindType().toString().equals("2")) {
                            //车辆区域
                            String area = workareaInfo.getArea();
                            if (ObjectUtil.isNotEmpty(area)) {
                                vehicleAreaArea = vehicleAreaArea + Double.parseDouble(area);
                            }
                        } else if ("3".equals(personWorkAreaType) && workareaInfo.getAreaType().toString().equals("2") && workareaInfo.getBindType().toString().equals("1")) {
                            //人员区域
                            String area = workareaInfo.getArea();
                            if (ObjectUtil.isNotEmpty(area)) {
                                personAreaArea = personAreaArea + Double.parseDouble(area);
                            }
                        }
                    }
                }
                if ("3".equals(personWorkAreaType)) {
                    synthInfoDTO.setPersonWorkAreaCount(new BigDecimal(personLineArea + personAreaArea).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                }
                if ("3".equals(vehicleWorkAreaType)) {
                    synthInfoDTO.setVehicleWorkAreaCount(new BigDecimal(vehicleLineArea + vehicleAreaArea).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                }
            }


            org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
            query.addCriteria(Criteria.where("projectCode").is(synthInfoDTO.getProjectCode()));
            Update update = new Update();
            update.set("allPersonCount", synthInfoDTO.getAllPersonCount());
            update.set("onlineVehicleCount", synthInfoDTO.getOnlineVehicleCount());
            update.set("onlinePersonCount", synthInfoDTO.getOnlinePersonCount());
            update.set("allPersonCount", synthInfoDTO.getAllPersonCount());
            update.set("companyId", synthInfoDTO.getCompanyId());
            update.set("projectName", synthInfoDTO.getProjectName());
            update.set("areaCode", synthInfoDTO.getAreaCode());
            update.set("facilityCount", synthInfoDTO.getFacilityCount());
            update.set("workingFacilityCount", synthInfoDTO.getWorkingFacilityCount());
            update.set("personCount", synthInfoDTO.getPersonCount());
            update.set("workingPersonCount", synthInfoDTO.getWorkingPersonCount());
            update.set("vehicleCount", synthInfoDTO.getVehicleCount());
            update.set("workingVehicleCount", synthInfoDTO.getWorkingVehicleCount());
            update.set("updateTime", TimeUtil.getNoLineYYYYMMDDHHMMSS(new Date()));
            update.set("personWorkAreaCount", synthInfoDTO.getPersonWorkAreaCount());
            update.set("vehicleWorkAreaCount", synthInfoDTO.getVehicleWorkAreaCount());
            log.info("synthInfoDTO.toString() ----- " + synthInfoDTO.toString());
            mongoTemplate.findAndModify(query, update, SynthInfoDTO.class);

            //同步沧州政务云。
            String value = DictCache.getValue(CommonConstant.DICT_THIRD_PATH, CommonConstant.DICT_THIRD_KEY.CZ_SYNTH_KEY);
            if (ObjectUtil.isNotEmpty(value)) {
                try {
                    String resStr = OkhttpUtil.postSyncJson(value, JSONUtil.parseObj(synthInfoDTO).toString()).body().string();
                    log.info("OkhttpUtil End ----- " + resStr);
                } catch (IOException e) {
                    log.info("OkhttpUtil End ----- " + e.getMessage());
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * 初始化人员聚合数据到mongodb
     */
    @Override
    @Async
    public void initAllPersonDataToMongoDb(Boolean isReload) {

        Map<Long, DevicePersonInfoDto> personWatchDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE).getData();

        ScheduleObject scheduleObject = new ScheduleObject();
        scheduleObject.setScheduleDate(LocalDate.now());
        scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.PERSON);
        R<List<ScheduleObject>> listR = scheduleClient.listScheduleObjectByCondition(scheduleObject);
        List<ScheduleObject> data = listR.getData();
        Map<Long, List<ScheduleObject>> collect = data.stream().collect(Collectors.groupingBy(ScheduleObject::getEntityId));
        Map<Long, BasicPersonDTO> basicPersonDTOMap = null;
        if (isReload) {
            List<BasicPersonDTO> basicPersonDTOS = mongoTemplate.find(Query.query(new Criteria()), BasicPersonDTO.class);
            basicPersonDTOMap = basicPersonDTOS.stream().collect(Collectors.toMap(BasicPersonDTO::getId, basicPersonDTO -> basicPersonDTO));
        }


        List<Person> activePerson = personClient.listPerson(new PersonVO()).getData();
        Map<Long, BasicPersonDTO> finalBasicPersonDTOMap = basicPersonDTOMap;
        List<BasicPersonDTO> allDataVOS = activePerson.stream().map(person -> {
                    BasicPersonDTO personDTO = BeanUtil.copy(person, BasicPersonDTO.class);
                    DevicePersonInfoDto devicePersonInfoDto = personWatchDeviceMap.get(personDTO.getId());
                    if (devicePersonInfoDto != null) {
                        personDTO.setWatchDeviceId(devicePersonInfoDto.getId());
                        personDTO.setWatchDeviceCode(devicePersonInfoDto.getDeviceCode());
                        personDTO.setDeviceStatus(devicePersonInfoDto.getDeviceStatus());
                    }

                    String categoryNameById = StationCache.getStationName(person.getPersonPositionId());
                    personDTO.setPersonPositionName(categoryNameById);
                    personDTO.setTenantId(person.getTenantId());
                    if (collect.containsKey(person.getId())) {
                        if (isReload) {
                            BasicPersonDTO basicPersonDTO = finalBasicPersonDTOMap.get(person.getId());
                            personDTO.setWorkStatus(basicPersonDTO.getWorkStatus());
                            personDTO.setWorkStatusName(basicPersonDTO.getWorkStatusName());
                            personDTO.setDeviceStatus(basicPersonDTO.getDeviceStatus());
                        }
                        List<ScheduleObject> scheduleObjects = collect.get(person.getId());
                        PersonStatusEnum personStatusEnum = PersonStatusEnum.VACATION;

                        for (ScheduleObject so : scheduleObjects) {
                            if (ArrangeConstant.TureOrFalse.INT_TRUE == so.getStatus().intValue()) {
                                personStatusEnum = PersonStatusEnum.OFF_LINE;
                            }
                        }
                        personDTO.setWorkStatus(personStatusEnum.getValue());
                        personDTO.setWorkStatusName(personStatusEnum.getDesc());
                    } else {
                        personDTO.setWorkStatus(PersonStatusEnum.UN_ARRANGE.getValue());
                        personDTO.setWorkStatusName(PersonStatusEnum.UN_ARRANGE.getDesc());
                    }
                    List<WorkareaRel> personRels = workareaRelClient.getByEntityIdAndType(personDTO.getId(), 1L).getData();
                    if (personRels != null && personRels.size() > 0 && personRels.get(0).getId() != null) {
                        WorkareaInfo workareaInfo = workareaClient.getWorkInfoById(personRels.get(0).getWorkareaId()).getData(); // 经过沟通，人员一天几乎不可能分到不同的作业区域或路线，所以取第一条（一般只有一条）
                        if (workareaInfo != null && workareaInfo.getId() != null) {
                            personDTO.setPersonBelongRegion(workareaInfo.getRegionId()); // 设置人员工作所属的片区，便于大屏根据片区统计人员数据
                        }
                    }
                    personDTO.setCreateTime(new Date());
                    return personDTO;
                }
        ).collect(Collectors.toList());

        log.info("人员聚合数据初始化完成！正在插表");

        while (true) {
            if (mongoTemplate.collectionExists(BasicPersonDTO.class)) {
                mongoTemplate.dropCollection(BasicPersonDTO.class);
                continue;
            }
            break;
        }
        //创建集合  并创建索引
        mongoTemplate.createCollection(BasicPersonDTO.class);
        mongoTemplate.insert(allDataVOS, BasicPersonDTO.class);
        log.info("人员聚合数据插表完成");

        initAllPersonPositionToMongoDb(allDataVOS);
        log.info("人员聚合数据更新完成");
    }

    /**
     * 初始化所有人员位置到mongodb
     *
     * @param allDataVOS
     */
    private void initAllPersonPositionToMongoDb(List<BasicPersonDTO> allDataVOS) {
        if (CollectionUtil.isEmpty(allDataVOS)) {
            return;
        }

        Map<String, List<BasicPersonDTO>> watchDeviceCode = allDataVOS.stream().filter(basicPersonDTO -> StringUtil.isNotBlank(basicPersonDTO.getWatchDeviceCode())).collect(Collectors.groupingBy(BasicPersonDTO::getWatchDeviceCode));
        Set<String> deviceCodes = watchDeviceCode.keySet();
        for (String deviceCode : deviceCodes) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("deviceIds", deviceCode);
                RealTimePositionResp resp = null;// 所有设备实时位置数据
                resp = BigDataHttpClient.getBigDataBodyToObjcet(BigDataHttpClient.getPersonCarRealTime,
                        params, RealTimePositionResp.class);
                if (resp.getData() != null && CollectionUtil.isNotEmpty(resp.getData().getPositions())) {
                    RealTimePositionDTO.Position position = resp.getData().getPositions().get(0);
                    Query query = new Query();
                    query.addCriteria(Criteria.where("watchDeviceCode").is(deviceCode));
                    Update update = Update.update("lat", position.getLat()).set("lng", position.getLng()).set("updateTime", DateUtil.now());
                    mongoTemplate.updateMulti(query, update, BasicPersonDTO.class);
                    log.info(deviceCode + "位置更新成功");
                    continue;
                }
            } catch (IOException e) {
                log.info(deviceCode + "获取位置失败");
            }

        }

    }


    /**
     * 初始化车辆聚合数据到Mongo
     */
    @Override
    @Async
    public void initAllVehicleDataToMongoDb(Boolean isReload) {
        List<VehicleInfo> vehicleInfoList = VehicleCache.getVehicleClient().listVehicleByCondition(new VehicleInfoDTO()).getData();
        Map<Long, DevicePersonInfoDto> vehicleAccDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE).getData();
        Map<Long, DevicePersonInfoDto> vehicleGPSDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.VEHICLE_GPS_DEVICE).getData();
        Map<Long, DevicePersonInfoDto> vehicleCvrDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.VEHICLE_GPS_DEVICE).getData();
        Map<Long, DevicePersonInfoDto> vehicleNvrDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.VEHICLE_GPS_DEVICE).getData();
        ScheduleObject scheduleObject = new ScheduleObject();
        scheduleObject.setScheduleDate(LocalDate.now());
        scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.VEHICLE);

        Map<Long, BasicVehicleInfoDTO> basicVehicleInfoDTOMap = null;
        if (isReload) {
            List<BasicVehicleInfoDTO> basicVehicleInfoDTOS = mongoTemplate.find(Query.query(new Criteria()), BasicVehicleInfoDTO.class);
            basicVehicleInfoDTOMap = basicVehicleInfoDTOS.stream().collect(Collectors.toMap(BasicVehicleInfoDTO::getId, basicVehicleInfoDTO -> basicVehicleInfoDTO));
        }


        R<List<ScheduleObject>> listR = scheduleClient.listScheduleObjectByCondition(scheduleObject);
        List<ScheduleObject> data = listR.getData();
        Map<Long, List<ScheduleObject>> collect = data.stream().collect(Collectors.groupingBy(ScheduleObject::getEntityId));


        Map<Long, BasicVehicleInfoDTO> finalBasicVehicleInfoDTOMap = basicVehicleInfoDTOMap;
        List<BasicVehicleInfoDTO> allDataVOS = vehicleInfoList.stream().map(vehicleInfo -> {
            BasicVehicleInfoDTO vehicleInfoDTO = BeanUtil.copy(vehicleInfo, BasicVehicleInfoDTO.class);

//            String categoryName = EntityCategoryCache.getCategoryNameById(vehicleInfo.getEntityCategoryId());
            EntityCategory categoryById = EntityCategoryCache.getCategoryById(vehicleInfo.getEntityCategoryId());
            vehicleInfoDTO.setVehicleTypeName(categoryById.getCategoryName());

            if (categoryById != null && StringUtil.isNotBlank(categoryById.getCategoryCode())) {
                VehicleWorkType workTypeByCategoryId = VehicleCategoryCache.getWorkTypeByCategoryId(categoryById.getId().toString());
                vehicleInfoDTO.setVehicleWorkTypeCode(workTypeByCategoryId.getWorkTypeCode());
                vehicleInfoDTO.setVehicleWorkTypeName(DictCache.getValue("vehicle_work_type", workTypeByCategoryId.getWorkTypeCode()));
            }

            if (collect.containsKey(vehicleInfo.getId())) {
                if (isReload) {
                    BasicVehicleInfoDTO basicVehicleInfoDTO = finalBasicVehicleInfoDTOMap.get(vehicleInfo.getId());
                    vehicleInfoDTO.setWorkStatus(basicVehicleInfoDTO.getWorkStatus());
                    vehicleInfoDTO.setWorkStatusName(basicVehicleInfoDTO.getWorkStatusName());
                    vehicleInfoDTO.setDeviceStatus(basicVehicleInfoDTO.getDeviceStatus());
                }
                List<ScheduleObject> scheduleObjects = collect.get(vehicleInfo.getId());
                VehicleStatusEnum vehicleStatusEnum = VehicleStatusEnum.VACATION;

                for (ScheduleObject so : scheduleObjects) {
                    if (ArrangeConstant.TureOrFalse.INT_TRUE == so.getStatus().intValue()) {
                        vehicleStatusEnum = VehicleStatusEnum.OFF_LINE;
                    }
                }
                vehicleInfoDTO.setWorkStatus(vehicleStatusEnum.getValue());
                vehicleInfoDTO.setWorkStatusName(vehicleStatusEnum.getDesc());


            } else {
                vehicleInfoDTO.setWorkStatus(PersonStatusEnum.UN_ARRANGE.getValue());
                vehicleInfoDTO.setWorkStatusName(PersonStatusEnum.UN_ARRANGE.getDesc());
            }

            vehicleInfoDTO.setTenantId(vehicleInfo.getTenantId());

            DevicePersonInfoDto vehicleAccDevice = vehicleAccDeviceMap.get(vehicleInfo.getId());
            DevicePersonInfoDto vehicleGPSDevice = vehicleGPSDeviceMap.get(vehicleInfo.getId());
            DevicePersonInfoDto vehicleCvrDevice = vehicleCvrDeviceMap.get(vehicleInfo.getId());
            DevicePersonInfoDto vehicleNvrDevice = vehicleNvrDeviceMap.get(vehicleInfo.getId());
            // 将各种设备聚合到mongoDB

            if (vehicleAccDevice != null) {
                vehicleInfoDTO.setAccDeviceId(vehicleAccDevice.getId());
                vehicleInfoDTO.setAccDeviceCode(vehicleAccDevice.getDeviceCode());
                vehicleInfoDTO.setDeviceStatus(vehicleAccDevice.getDeviceStatus());
            }
            if (vehicleGPSDevice != null && !DeviceConstant.DeviceFactory.MINICREATE.equals(vehicleGPSDevice.getDeviceFactory())) {
                vehicleInfoDTO.setGpsDeviceId(vehicleGPSDevice.getId());
                vehicleInfoDTO.setGpsDeviceCode(vehicleGPSDevice.getDeviceCode());
                vehicleInfoDTO.setDeviceStatus(vehicleGPSDevice.getDeviceStatus());
            }
            if (vehicleCvrDevice != null) {
                vehicleInfoDTO.setCvrDeviceId(vehicleCvrDevice.getId());
                vehicleInfoDTO.setCvrDeviceCode(vehicleCvrDevice.getDeviceCode());
                if (DeviceConstant.DeviceFactory.MINICREATE.equals(vehicleGPSDevice.getDeviceFactory())) {// 点创3合1设备
                    vehicleInfoDTO.setGpsDeviceId(vehicleCvrDevice.getId());
                    vehicleInfoDTO.setGpsDeviceCode(vehicleCvrDevice.getDeviceCode());
                    vehicleInfoDTO.setDeviceStatus(vehicleCvrDevice.getDeviceStatus());
                }
            }
            if (vehicleNvrDevice != null) {
                vehicleInfoDTO.setNvrDeviceId(vehicleNvrDevice.getId());
                vehicleInfoDTO.setNvrDeviceCode(vehicleNvrDevice.getDeviceCode());
                if (DeviceConstant.DeviceFactory.MINICREATE.equals(vehicleGPSDevice.getDeviceFactory())) {// 点创3合1设备
                    vehicleInfoDTO.setGpsDeviceId(vehicleNvrDevice.getId());
                    vehicleInfoDTO.setGpsDeviceCode(vehicleNvrDevice.getDeviceCode());
                    vehicleInfoDTO.setDeviceStatus(vehicleNvrDevice.getDeviceStatus());
                }

            }

            // 根据车辆id和车辆分类查询绑定的作业路线和区域 ，如果一辆车既绑定了区域又绑定了路线，则统计时及为1
            List<WorkareaRel> vehicleInfoRels = workareaRelClient.getByEntityIdAndType(vehicleInfoDTO.getId(), 2L).getData();
            if (vehicleInfoRels != null && vehicleInfoRels.size() > 0 && vehicleInfoRels.get(0).getId() != null) {
                // 经过沟通，车辆一天几乎不可能分到不同的作业区域或路线，所以取第一条（一般只有一条）
                WorkareaInfo workareaInfo = workareaClient.getWorkInfoById(vehicleInfoRels.get(0).getWorkareaId()).getData();
                if (workareaInfo != null && workareaInfo.getId() != null) {
                    // 设置车辆工作所属的片区，便于大屏根据片区统计车辆数据
                    vehicleInfoDTO.setVehicleBelongRegion(workareaInfo.getRegionId());
                }
            }
            vehicleInfoDTO.setCreateTime(new Date());
            return vehicleInfoDTO;
        }).collect(Collectors.toList());

        while (true) {
            if (mongoTemplate.collectionExists(BasicVehicleInfoDTO.class)) {
                mongoTemplate.dropCollection(BasicVehicleInfoDTO.class);
                continue;
            }
            break;
        }

        //创建集合  并创建索引
        mongoTemplate.createCollection(BasicVehicleInfoDTO.class);
        mongoTemplate.insert(allDataVOS, BasicVehicleInfoDTO.class);
        initAllVehiclePositionToMongoDb(allDataVOS);
    }

    /**
     * 初始化所有车辆位置到MongoDb
     *
     * @param allDataVOS
     */

    private void initAllVehiclePositionToMongoDb(List<BasicVehicleInfoDTO> allDataVOS) {
        if (CollectionUtil.isEmpty(allDataVOS)) {
            return;
        }
        Map<String, List<BasicVehicleInfoDTO>> gpsDeviceCodeMap = allDataVOS.stream().filter(basicVehicleInfoDTO -> StringUtil.isNotBlank(basicVehicleInfoDTO.getGpsDeviceCode())).collect(Collectors.groupingBy(BasicVehicleInfoDTO::getGpsDeviceCode));

        Set<String> deviceCodes = gpsDeviceCodeMap.keySet();
        for (String deviceCode : deviceCodes) {

            try {
                Map<String, Object> params = new HashMap<>();
                params.put("deviceIds", deviceCode);
                RealTimePositionResp resp = null;// 所有设备实时位置数据
                resp = BigDataHttpClient.getBigDataBodyToObjcet(BigDataHttpClient.getPersonCarRealTime,
                        params, RealTimePositionResp.class);
                if (resp.getData() != null && CollectionUtil.isNotEmpty(resp.getData().getPositions())) {
                    RealTimePositionDTO.Position position = resp.getData().getPositions().get(0);
                    Query query = new Query();
                    query.addCriteria(Criteria.where("gpsDeviceCode").is(deviceCode));
                    Update update = Update.update("lat", position.getLat()).set("lng", position.getLng()).set("updateTime", DateUtil.now());
                    mongoTemplate.updateMulti(query, update, BasicVehicleInfoDTO.class);
                    log.info(deviceCode + "位置更新成功");
                    continue;
                }
            } catch (IOException e) {
                log.info(deviceCode + "获取位置失败");
            }

        }
    }


    /**
     * 初始设施聚合数据到mongodb
     */
    @Override
    @Async
    public void initAllFacilityDataToMongoDb() {
        //查询出所有垃圾设施和中转站设施
        //中转设施列表
        List<FacilityInfo> facilityList = facilityClient.getAllFacility().getData();
        //垃圾设施列表
        List<AshcanInfo> ashcanList = ashcanClient.listAshcanInfoAll().getData();

        List<BasicFacilityDTO> facilityVOS = facilityList.stream().map(facility -> {
                    BasicFacilityDTO facilityDTO = new BasicFacilityDTO();
                    facilityDTO.setId(facility.getId());
                    //中转站设为1
                    facilityDTO.setFacilityMainType(Integer.valueOf(FacilityConstant.FacilityType.TRANSFER_STATION));
                    facilityDTO.setFacilityName(facility.getFacilityName());
                    facilityDTO.setFacilityType(facility.getExt1());
                    String typeName = dictClient.getValue(FacilityConstant.TranStationModel.CODE, facility.getExt1()).getData();
                    facilityDTO.setFacilityTypeName(typeName);
                    // 状态
                    facilityDTO.setFacilityStatus(facility.getStatus());
                    String statusName = dictClient.getValue(FacilityConstant.TranStationStatus.CODE, String.valueOf(facility.getStatus())).getData();
                    facilityDTO.setFacilityStatusName(statusName);
                    facilityDTO.setLat(facility.getLat());
                    facilityDTO.setLng(facility.getLng());
                    facilityDTO.setTenantId(facility.getTenantId());
                    if (StringUtil.isNotBlank(facility.getRegionId())) {
                        facilityDTO.setRegionId(facility.getRegionId());
                        List<Region> regions = iSysClient.getRegionForBS(String.valueOf(RegionConstant.REGION_TYPE.BUSI_REGION), facility.getTenantId()).getData();
                        regions.stream()
                                .filter(r -> facility.getRegionId().equals(r.getId()))
                                .findFirst()
                                .ifPresent(region -> facilityDTO.setFacilityRegionName(region.getRegionName()));
                    }
                    return facilityDTO;
                }
        ).collect(Collectors.toList());

        List<BasicFacilityDTO> ashcanVOS = ashcanList.stream().map(ashcan -> {
                    BasicFacilityDTO facilityDTO = new BasicFacilityDTO();
                    facilityDTO.setId(ashcan.getId());
                    //垃圾桶设为2
                    facilityDTO.setFacilityMainType(Integer.valueOf(FacilityConstant.FacilityType.ASHCAN));
                    facilityDTO.setFacilityName(ashcan.getAshcanCode());
                    facilityDTO.setFacilityType(ashcan.getAshcanType());
                    String typeName = dictClient.getValue(FacilityConstant.DictCode.ASHCAN_TYPE, ashcan.getAshcanType()).getData();
                    facilityDTO.setFacilityTypeName(typeName);
                    // 状态
                    facilityDTO.setFacilityStatus(ashcan.getStatus());
                    String statusName = dictClient.getValue(FacilityConstant.DictCode.ASHCAN_STATUS, String.valueOf(ashcan.getStatus())).getData();
                    facilityDTO.setFacilityStatusName(statusName);
                    // 工作状态
                    facilityDTO.setWorkStatus(ashcan.getWorkStatus());
                    String workStatusName = dictClient.getValue(FacilityConstant.DictCode.ASHCAN_WORK_STATUS, ashcan.getWorkStatus()).getData();
                    facilityDTO.setWorkStatusName(workStatusName);
                    facilityDTO.setTenantId(ashcan.getTenantId());
                    if (null != ashcan.getRegionId()) {
                        facilityDTO.setRegionId(String.valueOf(ashcan.getRegionId()));
                        List<Region> regions = iSysClient.getRegionForBS(String.valueOf(RegionConstant.REGION_TYPE.BUSI_REGION), ashcan.getTenantId()).getData();
                        regions.stream()
                                .filter(r -> ashcan.getRegionId().equals(r.getId()))
                                .findFirst()
                                .ifPresent(region -> facilityDTO.setFacilityRegionName(region.getRegionName()));
                    }
                    facilityDTO.setLng(ashcan.getLng());
                    facilityDTO.setLat(ashcan.getLat());
                    return facilityDTO;
                }
        ).collect(Collectors.toList());
        //合并两个list
        ashcanVOS.addAll(facilityVOS);

        while (true) {
            if (mongoTemplate.collectionExists(BasicFacilityDTO.class)) {
                mongoTemplate.dropCollection(BasicFacilityDTO.class);
                continue;
            }
            break;
        }
        //创建集合  并创建索引
        mongoTemplate.createCollection(BasicFacilityDTO.class);
        mongoTemplate.insert(ashcanVOS, BasicFacilityDTO.class);

    }


    /**
     * 更新车辆工作状态
     *
     * @param vehicleIdList
     * @param status
     * @return
     */
    @Override
    @Async
    public Long updateVehicleWorkStatus(List<Long> vehicleIdList, Integer status) {
        if (status != null && status.intValue() == VehicleConstant.VehicleStatus.NORMAL) {
            status = VehicleConstant.VehicleStatus.ONLINE;
        }
        Update update = Update.update("workStatus", status).set("workStatusName", VehicleStatusEnum.getDescByValue(status)).set("updateTime", new Date());
        if (mongoTemplate.collectionExists(BasicVehicleInfoDTO.class)) {
            Query query = Query.query(Criteria.where("id").in(vehicleIdList).and("workStatus").ne(VehicleStatusEnum.VACATION.getValue()).orOperator(
                    Criteria.where("gpsDeviceCode").exists(true),
                    Criteria.where("nvrDeviceCode").exists(true),
                    Criteria.where("cvrDeviceCode").exists(true)));
            long modifiedCount = mongoTemplate.updateMulti(query, update, BasicVehicleInfoDTO.class).getModifiedCount();
            sendVehicleChangeMessageToMQ(vehicleIdList);
            return modifiedCount;
        }
        return 0L;
    }

    /**
     * 更新车辆工作状态
     *
     * @param vehicleId
     * @param workStatus
     * @return
     */
    @Override
    public Long updateVehicleWorkStatus(Long vehicleId, Integer workStatus, Long deviceStatus) {
        long modifiedCount = 0L;
        if (workStatus != null && workStatus.intValue() == VehicleConstant.VehicleStatus.NORMAL) {
            workStatus = VehicleConstant.VehicleStatus.ONLINE;
        }

        Update update = Update
                .update("workStatus", workStatus)
                .set("workStatusName", WorkStatusEnum.getDescByValue(workStatus))
                .set("updateTime", new Date())
                .set("deviceStatus", deviceStatus);
        if (mongoTemplate.collectionExists(BasicVehicleInfoDTO.class)) {
            Query query = Query.query(Criteria.where("id").is(vehicleId));
            BasicVehicleInfoDTO one = mongoTemplate.findOne(query, BasicVehicleInfoDTO.class);
            if (one == null) {
                return 0L;
            }
            List<Long> vehicleIdList = new ArrayList<>();
            vehicleIdList.add(vehicleId);
//            if (one != null && (!workStatus.equals(one.getWorkStatus()) || !deviceStatus.equals(one.getDeviceStatus()))) {
                Query updateQuery = Query.query(Criteria.where("id").is(vehicleId));
                modifiedCount = mongoTemplate.updateMulti(updateQuery, update, BasicVehicleInfoDTO.class).getModifiedCount();
                sendVehicleChangeMessageToMQ(vehicleIdList);
                log.info("updateVehicleWorkStatus[{}||{}] status from [{}] to [{}]", one.getId(), one.getPlateNumber(), WorkStatusEnum.getDescByValue(one.getWorkStatus()), WorkStatusEnum.getDescByValue(workStatus));
//            }
        }
        return modifiedCount;

    }

    /**
     * 重新加载车辆
     *
     * @param vehicleIds
     * @return
     */
    @Override
    public Integer reloadVehicleInfo(List<Long> vehicleIds) {

        VehicleInfoDTO updateDTO = new VehicleInfoDTO();
        updateDTO.setVehicleIds(vehicleIds);
        List<VehicleInfo> vehicleInfoList = VehicleCache.getVehicleClient().listVehicleByCondition(updateDTO).getData();


        ScheduleObject scheduleObject = new ScheduleObject();
        scheduleObject.setScheduleDate(LocalDate.now());
        scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.VEHICLE);
        R<List<ScheduleObject>> listR = scheduleClient.listScheduleObjectByCondition(scheduleObject);
        List<ScheduleObject> data = listR.getData();
        Map<Long, List<ScheduleObject>> collect = data.stream().collect(Collectors.groupingBy(ScheduleObject::getEntityId));


        Query query = Query.query(Criteria.where("id").in(vehicleIds));
        List<BasicVehicleInfoDTO> basicVehicleInfoDTOS = mongoTemplate.find(query, BasicVehicleInfoDTO.class);
        Map<Long, BasicVehicleInfoDTO> basicVehicleInfoDTOMap = basicVehicleInfoDTOS.stream().collect(Collectors.toMap(BasicVehicleInfoDTO::getId, basicVehicleInfoDTO -> basicVehicleInfoDTO));

        Map<Long, DevicePersonInfoDto> vehicleAccDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE).getData();
        Map<Long, DevicePersonInfoDto> vehicleGPSDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.VEHICLE_GPS_DEVICE).getData();
        Map<Long, DevicePersonInfoDto> vehicleCvrDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.VEHICLE_GPS_DEVICE).getData();
        Map<Long, DevicePersonInfoDto> vehicleNvrDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.VEHICLE_GPS_DEVICE).getData();

        List<BasicVehicleInfoDTO> allDataVOS = vehicleInfoList.stream().map(vehicleInfo -> {
            BasicVehicleInfoDTO vehicleInfoDTO = BeanUtil.copy(vehicleInfo, BasicVehicleInfoDTO.class);

            if (vehicleAccDeviceMap.containsKey(vehicleInfo.getId())) {
                BasicVehicleInfoDTO basicVehicleInfoDTO = basicVehicleInfoDTOMap.get(vehicleInfo.getId());
                vehicleInfoDTO.setWorkStatus(basicVehicleInfoDTO.getWorkStatus());
                vehicleInfoDTO.setWorkStatusName(basicVehicleInfoDTO.getWorkStatusName());
            } else if (collect.containsKey(vehicleInfo.getId())) {
                vehicleInfoDTO.setWorkStatus(PersonStatusEnum.OFF_LINE.getValue());
                vehicleInfoDTO.setWorkStatusName(PersonStatusEnum.OFF_LINE.getDesc());
            } else {
                vehicleInfoDTO.setWorkStatus(PersonStatusEnum.UN_ARRANGE.getValue());
                vehicleInfoDTO.setWorkStatusName(PersonStatusEnum.UN_ARRANGE.getDesc());
            }

            vehicleInfoDTO.setTenantId(vehicleInfo.getTenantId());
            DevicePersonInfoDto vehicleAccDevice = vehicleAccDeviceMap.get(vehicleInfo.getId());
            DevicePersonInfoDto vehicleGPSDevice = vehicleGPSDeviceMap.get(vehicleInfo.getId());
            DevicePersonInfoDto vehicleCvrDevice = vehicleCvrDeviceMap.get(vehicleInfo.getId());
            DevicePersonInfoDto vehicleNvrDevice = vehicleNvrDeviceMap.get(vehicleInfo.getId());
            // 将各种设备聚合到mongoDB

            if (vehicleAccDevice != null) {
                vehicleInfoDTO.setAccDeviceId(vehicleAccDevice.getId());
                vehicleInfoDTO.setAccDeviceCode(vehicleAccDevice.getDeviceCode());
            }
            if (vehicleGPSDevice != null && !DeviceConstant.DeviceFactory.MINICREATE.equals(vehicleGPSDevice.getDeviceFactory())) {
                vehicleInfoDTO.setGpsDeviceId(vehicleGPSDevice.getId());
                vehicleInfoDTO.setGpsDeviceCode(vehicleGPSDevice.getDeviceCode());
            }
            if (vehicleCvrDevice != null) {
                vehicleInfoDTO.setCvrDeviceId(vehicleCvrDevice.getId());
                vehicleInfoDTO.setCvrDeviceCode(vehicleCvrDevice.getDeviceCode());
                if (DeviceConstant.DeviceFactory.MINICREATE.equals(vehicleGPSDevice.getDeviceFactory())) {// 点创3合1设备
                    vehicleInfoDTO.setGpsDeviceId(vehicleCvrDevice.getId());
                    vehicleInfoDTO.setGpsDeviceCode(vehicleCvrDevice.getDeviceCode());
                }
            }
            if (vehicleNvrDevice != null) {
                vehicleInfoDTO.setNvrDeviceId(vehicleNvrDevice.getId());
                vehicleInfoDTO.setNvrDeviceCode(vehicleNvrDevice.getDeviceCode());
                if (DeviceConstant.DeviceFactory.MINICREATE.equals(vehicleGPSDevice.getDeviceFactory())) {// 点创3合1设备
                    vehicleInfoDTO.setGpsDeviceId(vehicleNvrDevice.getId());
                    vehicleInfoDTO.setGpsDeviceCode(vehicleNvrDevice.getDeviceCode());
                }

            }
            List<WorkareaRel> personRels = workareaRelClient.getByEntityIdAndType(vehicleInfoDTO.getId(), 2L).getData();
            if (personRels != null && personRels.size() > 0 && personRels.get(0).getId() != null) {
                WorkareaInfo workareaInfo = workareaClient.getWorkInfoById(personRels.get(0).getWorkareaId()).getData(); // 经过沟通，车辆一天几乎不可能分到不同的作业区域或路线，所以取第一条（一般只有一条）
                if (workareaInfo != null && workareaInfo.getId() != null) {
                    vehicleInfoDTO.setVehicleBelongRegion(workareaInfo.getRegionId()); // 设置车辆工作所属的片区，便于大屏根据片区统计车辆数据
                }
            }
            Query updateQuery = Query.query(Criteria.where("id").is(vehicleInfoDTO.getId()));
            Update update = getVehicleUpdate(vehicleInfoDTO);
            mongoTemplate.upsert(updateQuery, update, BasicVehicleInfoDTO.class);

            return vehicleInfoDTO;
        }).collect(Collectors.toList());

        return allDataVOS.size();
    }


    /**
     * 更新车辆工作状态
     *
     * @param personIdList
     * @param status
     * @return
     */
    @Override
    @Async
    public Long updatePersonWorkStatus(List<Long> personIdList, Integer status) {

        if (status != null && status.intValue() == PersonConstant.PersonStatus.NORMAL) {
            status = PersonConstant.PersonStatus.ONLINE;
        }
        Update update = Update.update("workStatus", status).set("workStatusName", PersonStatusEnum.getDescByValue(status)).set("updateTime", new Date());
        if (mongoTemplate.collectionExists(BasicPersonDTO.class)) {
            Query query = Query.query(Criteria.where("id").in(personIdList).and("workStatus").ne(PersonStatusEnum.VACATION.getValue()).orOperator(
                    Criteria.where("watchDeviceCode").exists(true)

            ));
            UpdateResult updateResult = mongoTemplate.updateMulti(query, update, BasicPersonDTO.class);
            sendPersonChangeMessageToMQ(personIdList);
            return updateResult.getModifiedCount();
        }
        return 0L;
    }

    /**
     * 更新车辆工作状态
     *
     * @param personId
     * @param workStatus
     * @return
     */
    @Override
    public Long updatePersonWorkStatus(Long personId, Integer workStatus, Long deviceStatus) {
        long modifiedCount = 0L;
        if (workStatus != null && workStatus.intValue() == PersonConstant.PersonStatus.NORMAL) {
            workStatus = PersonConstant.PersonStatus.ONLINE;
        }

        if (PersonStatusEnum.getDescByValue(workStatus) == null) {
            return 0L;
        }

        Update update = Update
                .update("workStatus", workStatus)
                .set("workStatusName", WorkStatusEnum.getDescByValue(workStatus))
                .set("updateTime", new Date())
                .set("deviceStatus", deviceStatus);
        if (mongoTemplate.collectionExists(BasicPersonDTO.class)) {
            Query query = Query.query(Criteria.where("id").is(personId));
            BasicPersonDTO one = mongoTemplate.findOne(query, BasicPersonDTO.class);
            if (one == null) {
                return 0L;
            }

            List<Long> personIdList = new ArrayList<>();
            personIdList.add(personId);
//            if (one != null && (!workStatus.equals(one.getWorkStatus()) || !deviceStatus.equals(one.getDeviceStatus()))) {
                Query updateQuery = Query.query(Criteria.where("id").is(personId));
                modifiedCount = mongoTemplate.updateMulti(updateQuery, update, BasicPersonDTO.class).getModifiedCount();
                log.info("updatePersonWorkStatus[{}||{}] status from [{}] to [{}]", one.getId(), one.getPersonName(), WorkStatusEnum.getDescByValue(one.getWorkStatus()), WorkStatusEnum.getDescByValue(workStatus));
//            }
        }
        return modifiedCount;

    }


    /**
     * 初始化人员聚合数据到mongodb
     */
    @Override
    public Integer reloadPersonInfo(List<Long> personIdList) {

        Map<Long, DevicePersonInfoDto> personWatchDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE).getData();
        PersonVO personCondition = new PersonVO();
        personCondition.setPersonIdList(personIdList);
        List<Person> personNewData = personClient.listPerson(personCondition).getData();


        Query query = Query.query(Criteria.where("id").in(personIdList));
        List<BasicPersonDTO> basicPersonDTOS = mongoTemplate.find(query, BasicPersonDTO.class);
        Map<Long, BasicPersonDTO> basicPersonDTOMap = basicPersonDTOS.stream().collect(Collectors.toMap(BasicPersonDTO::getId, basicPersonDTO -> basicPersonDTO));


        ScheduleObject scheduleObject = new ScheduleObject();
        scheduleObject.setScheduleDate(LocalDate.now());
        scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.PERSON);
        R<List<ScheduleObject>> listR = scheduleClient.listScheduleObjectByCondition(scheduleObject);
        List<ScheduleObject> data = listR.getData();
        Map<Long, List<ScheduleObject>> collect = data.stream().collect(Collectors.groupingBy(ScheduleObject::getEntityId));


        /**
         * 删除已经离职的
         */
        personNewData.stream().filter(person -> PersonConstant.IncumbencyStatus.UN.equals(person.getIsIncumbency())).forEach(person -> {
            mongoTemplate.remove(Query.query(Criteria.where("id").is(person.getId())), BasicPersonDTO.class);
        });


        /**
         * 更新在职的
         */
        List<BasicPersonDTO> allData = personNewData.stream().filter(person -> PersonConstant.IncumbencyStatus.IN.equals(person.getIsIncumbency())).map(person -> {
                    BasicPersonDTO personDTO = BeanUtil.copy(person, BasicPersonDTO.class);
                    DevicePersonInfoDto devicePersonInfoDto = personWatchDeviceMap.get(personDTO.getId());
                    if (devicePersonInfoDto != null) {
                        personDTO.setWatchDeviceId(devicePersonInfoDto.getId());
                        personDTO.setWatchDeviceCode(devicePersonInfoDto.getDeviceCode());
                        personDTO.setDeviceStatus(devicePersonInfoDto.getDeviceStatus());
                    }
                    personDTO.setTenantId(person.getTenantId());
                    if (basicPersonDTOMap.containsKey(person.getId())) {
                        BasicPersonDTO basicPersonDTO = basicPersonDTOMap.get(person.getId());
                        personDTO.setWorkStatus(basicPersonDTO.getWorkStatus());
                        personDTO.setWorkStatusName(basicPersonDTO.getWorkStatusName());

                    } else if (collect.containsKey(person.getId())) {
                        personDTO.setWorkStatus(PersonStatusEnum.OFF_LINE.getValue());
                        personDTO.setWorkStatusName(PersonStatusEnum.OFF_LINE.getDesc());
                    } else {
                        personDTO.setWorkStatus(PersonStatusEnum.UN_ARRANGE.getValue());
                        personDTO.setWorkStatusName(PersonStatusEnum.UN_ARRANGE.getDesc());
                    }

                    List<WorkareaRel> personRels = workareaRelClient.getByEntityIdAndType(personDTO.getId(), 1L).getData();
                    if (personRels != null && personRels.size() > 0 && personRels.get(0).getId() != null) {
                        WorkareaInfo workareaInfo = workareaClient.getWorkInfoById(personRels.get(0).getWorkareaId()).getData(); // 经过沟通，人员一天几乎不可能分到不同的作业区域或路线，所以取第一条（一般只有一条）
                        if (workareaInfo != null && workareaInfo.getId() != null) {
                            personDTO.setPersonBelongRegion(workareaInfo.getRegionId()); // 设置人员工作所属的片区，便于大屏根据片区统计人员数据
                        }
                    }


                    Query updateQuery = Query.query(Criteria.where("id").is(personDTO.getId()));
                    Update update = getPersonUpdate(personDTO);
                    mongoTemplate.upsert(updateQuery, update, BasicPersonDTO.class);
                    return personDTO;
                }
        ).collect(Collectors.toList());

        return allData.size();

    }

    /**
     * 初始化人员聚合数据到mongodb
     */
    @Override
    @Async
    public Integer addOrUpdateFacility(String facilityId, Integer facilityMainType) {
        BasicFacilityDTO basicFacilityNewDto = new BasicFacilityDTO();
        if (Integer.parseInt(FacilityConstant.FacilityType.TRANSFER_STATION) == facilityMainType) {
            FacilityInfo facilityInfo = facilityClient.getFacilityInfoById(Long.valueOf(facilityId)).getData();
            basicFacilityNewDto.setId(facilityInfo.getId());
            //中转站设为1
            basicFacilityNewDto.setFacilityMainType(Integer.parseInt(FacilityConstant.FacilityType.TRANSFER_STATION));
            basicFacilityNewDto.setFacilityName(facilityInfo.getFacilityName());
            basicFacilityNewDto.setFacilityType(facilityInfo.getExt1());
            String typeName = dictClient.getValue(FacilityConstant.TranStationModel.CODE, facilityInfo.getExt1()).getData();
            basicFacilityNewDto.setFacilityTypeName(typeName);
            // 状态
            basicFacilityNewDto.setFacilityStatus(facilityInfo.getStatus());
            String statusName = dictClient.getValue(FacilityConstant.TranStationStatus.CODE, String.valueOf(facilityInfo.getStatus())).getData();
            basicFacilityNewDto.setFacilityStatusName(statusName);
            basicFacilityNewDto.setLat(facilityInfo.getLat());
            basicFacilityNewDto.setLng(facilityInfo.getLng());
            basicFacilityNewDto.setTenantId(facilityInfo.getTenantId());
            if (StringUtil.isNotBlank(facilityInfo.getRegionId())) {
                basicFacilityNewDto.setRegionId(facilityInfo.getRegionId());
                List<Region> regions = iSysClient.getRegionForBS(String.valueOf(RegionConstant.REGION_TYPE.BUSI_REGION), facilityInfo.getTenantId()).getData();
                regions.stream()
                        .filter(r -> facilityInfo.getRegionId().equals(r.getId()))
                        .findFirst()
                        .ifPresent(region -> basicFacilityNewDto.setFacilityRegionName(region.getRegionName()));
            }
        } else if (Integer.parseInt(FacilityConstant.FacilityType.ASHCAN) == facilityMainType) {
            AshcanInfo ashcan = ashcanClient.getAshcan(Long.valueOf(facilityId)).getData();
            basicFacilityNewDto.setId(ashcan.getId());
            //垃圾桶设为2
            basicFacilityNewDto.setFacilityMainType(Integer.parseInt(FacilityConstant.FacilityType.ASHCAN));
            basicFacilityNewDto.setFacilityName(ashcan.getAshcanCode());
            basicFacilityNewDto.setFacilityType(ashcan.getAshcanType());
            String typeName = dictClient.getValue(FacilityConstant.DictCode.ASHCAN_TYPE, ashcan.getAshcanType()).getData();
            basicFacilityNewDto.setFacilityTypeName(typeName);
            // 状态
            basicFacilityNewDto.setFacilityStatus(ashcan.getStatus());
            String statusName = dictClient.getValue(FacilityConstant.DictCode.ASHCAN_STATUS, String.valueOf(ashcan.getStatus())).getData();
            basicFacilityNewDto.setFacilityStatusName(statusName);
            // 工作状态
            basicFacilityNewDto.setWorkStatus(ashcan.getWorkStatus());
            String workStatusName = dictClient.getValue(FacilityConstant.DictCode.ASHCAN_WORK_STATUS, ashcan.getWorkStatus()).getData();
            basicFacilityNewDto.setWorkStatusName(workStatusName);
            basicFacilityNewDto.setTenantId(ashcan.getTenantId());
            if (null != ashcan.getRegionId()) {
                basicFacilityNewDto.setRegionId(String.valueOf(ashcan.getRegionId()));
                List<Region> regions = iSysClient.getRegionForBS(String.valueOf(RegionConstant.REGION_TYPE.BUSI_REGION), ashcan.getTenantId()).getData();
                regions.stream()
                        .filter(r -> ashcan.getRegionId().equals(r.getId()))
                        .findFirst()
                        .ifPresent(region -> basicFacilityNewDto.setFacilityRegionName(region.getRegionName()));
            }
            basicFacilityNewDto.setLng(ashcan.getLng());
            basicFacilityNewDto.setLat(ashcan.getLat());
        }
        //根据id更新数据
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(Long.valueOf(facilityId)));
        Update update = getFacilityUpdate(basicFacilityNewDto);
        mongoTemplate.upsert(query, update, BasicFacilityDTO.class);

        return 1;

    }

    @Override
    public Long removeFacilityList(List<Long> facilityList) {
        Query query = Query.query(Criteria.where("_id").in(facilityList));
        DeleteResult remove = mongoTemplate.remove(query, BasicFacilityDTO.class);
        return remove.getDeletedCount();
    }


    @Async
    @Override
    public Boolean addOrUpdateVehicleList(List<VehicleInfo> vehicleInfoList) {
        try {
            Map<Long, DevicePersonInfoDto> vehicleAccDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE).getData();
            Map<Long, DevicePersonInfoDto> vehicleGPSDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.VEHICLE_GPS_DEVICE).getData();
            Map<Long, DevicePersonInfoDto> vehicleCvrDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.VEHICLE_GPS_DEVICE).getData();
            Map<Long, DevicePersonInfoDto> vehicleNvrDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.VEHICLE_GPS_DEVICE).getData();
            ScheduleObject scheduleObject = new ScheduleObject();
            scheduleObject.setScheduleDate(LocalDate.now());
            scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.VEHICLE);
            R<List<ScheduleObject>> listR = scheduleClient.listScheduleObjectByCondition(scheduleObject);
            List<ScheduleObject> data = listR.getData();
            Map<Long, List<ScheduleObject>> collect = data.stream().collect(Collectors.groupingBy(ScheduleObject::getEntityId));


            List<BasicVehicleInfoDTO> allDataVOS = vehicleInfoList.stream().map(vehicleInfo -> {
                BasicVehicleInfoDTO vehicleInfoDTO = BeanUtil.copy(vehicleInfo, BasicVehicleInfoDTO.class);

                //查看静态是否需要上班来判断状态，今天不需要上班的全为休假，需要上班的暂时为休息
                if (collect.containsKey(vehicleInfo.getId())) {
                    vehicleInfoDTO.setWorkStatus(PersonStatusEnum.OFF_LINE.getValue());
                    vehicleInfoDTO.setWorkStatusName(PersonStatusEnum.OFF_LINE.getDesc());
                } else {
                    vehicleInfoDTO.setWorkStatus(PersonStatusEnum.VACATION.getValue());
                    vehicleInfoDTO.setWorkStatusName(PersonStatusEnum.VACATION.getDesc());
                }

                vehicleInfoDTO.setTenantId(vehicleInfo.getTenantId());

                DevicePersonInfoDto vehicleAccDevice = vehicleAccDeviceMap.get(vehicleInfo.getId());
                DevicePersonInfoDto vehicleGPSDevice = vehicleGPSDeviceMap.get(vehicleInfo.getId());
                DevicePersonInfoDto vehicleCvrDevice = vehicleCvrDeviceMap.get(vehicleInfo.getId());
                DevicePersonInfoDto vehicleNvrDevice = vehicleNvrDeviceMap.get(vehicleInfo.getId());
                // 将各种设备聚合到mongoDB
                if (vehicleAccDevice != null) {
                    vehicleInfoDTO.setAccDeviceId(vehicleAccDevice.getId());
                    vehicleInfoDTO.setAccDeviceCode(vehicleAccDevice.getDeviceCode());
                }
                if (vehicleGPSDevice != null && !DeviceConstant.DeviceFactory.MINICREATE.equals(vehicleGPSDevice.getDeviceFactory())) {
                    vehicleInfoDTO.setGpsDeviceId(vehicleGPSDevice.getId());
                    vehicleInfoDTO.setGpsDeviceCode(vehicleGPSDevice.getDeviceCode());
                }
                if (vehicleCvrDevice != null) {
                    vehicleInfoDTO.setCvrDeviceId(vehicleCvrDevice.getId());
                    vehicleInfoDTO.setCvrDeviceCode(vehicleCvrDevice.getDeviceCode());
                    if (DeviceConstant.DeviceFactory.MINICREATE.equals(vehicleGPSDevice.getDeviceFactory())) {// 点创3合1设备
                        vehicleInfoDTO.setGpsDeviceId(vehicleCvrDevice.getId());
                        vehicleInfoDTO.setGpsDeviceCode(vehicleCvrDevice.getDeviceCode());
                    }
                }
                if (vehicleNvrDevice != null) {
                    vehicleInfoDTO.setNvrDeviceId(vehicleNvrDevice.getId());
                    vehicleInfoDTO.setNvrDeviceCode(vehicleNvrDevice.getDeviceCode());
                    if (DeviceConstant.DeviceFactory.MINICREATE.equals(vehicleGPSDevice.getDeviceFactory())) {// 点创3合1设备
                        vehicleInfoDTO.setGpsDeviceId(vehicleNvrDevice.getId());
                        vehicleInfoDTO.setGpsDeviceCode(vehicleNvrDevice.getDeviceCode());
                    }

                }
                List<WorkareaRel> personRels = workareaRelClient.getByEntityIdAndType(vehicleInfoDTO.getId(), 2L).getData();
                if (personRels != null && personRels.size() > 0 && personRels.get(0).getId() != null) {
                    WorkareaInfo workareaInfo = workareaClient.getWorkInfoById(personRels.get(0).getWorkareaId()).getData(); // 经过沟通，车辆一天几乎不可能分到不同的作业区域或路线，所以取第一条（一般只有一条）
                    if (workareaInfo != null && workareaInfo.getId() != null) {
                        vehicleInfoDTO.setVehicleBelongRegion(workareaInfo.getRegionId()); // 设置车辆工作所属的片区，便于大屏根据片区统计车辆数据
                    }
                }
                return vehicleInfoDTO;
            }).collect(Collectors.toList());

            allDataVOS.forEach(basicVehicleInfoDTO -> {
                //更新或者插入
                Query query = Query.query(Criteria.where("id").is(basicVehicleInfoDTO.getId()));
                Update update = getVehicleUpdate(basicVehicleInfoDTO);
                mongoTemplate.upsert(query, update, BasicVehicleInfoDTO.class);

            });
        } catch (Exception e) {
            log.error("更新车辆聚合数据失败：" + vehicleInfoList);
            return false;
        }

        return true;
    }

    @Override
    public Long removePersonList(List<Long> personIdList) {
        Query query = Query.query(Criteria.where("id").in(personIdList));
        DeleteResult remove = mongoTemplate.remove(query, BasicPersonDTO.class);
        return remove.getDeletedCount();
    }

    @Override
    public Long removeVehicleList(List<Long> vehicleIdList) {
        Query query = Query.query(Criteria.where("id").in(vehicleIdList));
        DeleteResult remove = mongoTemplate.remove(query, BasicVehicleInfoDTO.class);
        return remove.getDeletedCount();
    }

    @Async
    @Override
    public Boolean addOrUpdatePersonList(List<Person> personList) {
        try {
            Map<Long, DevicePersonInfoDto> personWatchDeviceMap = deviceClient.listDeviceByCategoryId(DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE).getData();

            ScheduleObject scheduleObject = new ScheduleObject();
            scheduleObject.setScheduleDate(LocalDate.now());
            scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.PERSON);
            R<List<ScheduleObject>> listR = scheduleClient.listScheduleObjectByCondition(scheduleObject);
            List<ScheduleObject> data = listR.getData();
            Map<Long, List<ScheduleObject>> collect = data.stream().collect(Collectors.groupingBy(ScheduleObject::getEntityId));


            List<BasicPersonDTO> allDataVOS = personList.stream().map(person -> {
                        BasicPersonDTO personDTO = BeanUtil.copy(person, BasicPersonDTO.class);
                        DevicePersonInfoDto devicePersonInfoDto = personWatchDeviceMap.get(personDTO.getId());
                        if (devicePersonInfoDto != null) {
                            personDTO.setWatchDeviceId(devicePersonInfoDto.getId());
                            personDTO.setWatchDeviceCode(devicePersonInfoDto.getDeviceCode());
                        }
                        personDTO.setTenantId(person.getTenantId());
                        if (collect.containsKey(person.getId())) {
                            personDTO.setWorkStatus(PersonStatusEnum.OFF_LINE.getValue());
                            personDTO.setWorkStatusName(PersonStatusEnum.OFF_LINE.getDesc());
                        } else {
                            personDTO.setWorkStatus(PersonStatusEnum.VACATION.getValue());
                            personDTO.setWorkStatusName(PersonStatusEnum.VACATION.getDesc());
                        }

                        List<WorkareaRel> personRels = workareaRelClient.getByEntityIdAndType(personDTO.getId(), 1L).getData();
                        if (personRels != null && personRels.size() > 0 && personRels.get(0).getId() != null) {
                            WorkareaInfo workareaInfo = workareaClient.getWorkInfoById(personRels.get(0).getWorkareaId()).getData(); // 经过沟通，人员一天几乎不可能分到不同的作业区域或路线，所以取第一条（一般只有一条）
                            if (workareaInfo != null && workareaInfo.getId() != null) {
                                personDTO.setPersonBelongRegion(workareaInfo.getRegionId()); // 设置人员工作所属的片区，便于大屏根据片区统计人员数据
                            }
                        }

                        return personDTO;
                    }
            ).collect(Collectors.toList());
            allDataVOS.forEach(basicPersonDTO -> {
                Query query = Query.query(Criteria.where("id").is(basicPersonDTO.getId()));
                Update update = getPersonUpdate(basicPersonDTO);
                mongoTemplate.upsert(query, update, BasicPersonDTO.class);
            });
        } catch (Exception e) {
            log.error("更新人员聚合数据失败：" + personList, e);
            return false;
        }
        return true;
    }

    private Update getFacilityUpdate(BasicFacilityDTO basicFacilityDTO) {
        Update update = new Update();
        if (null != basicFacilityDTO.getFacilityName()) {
            update.set("facilityName", basicFacilityDTO.getFacilityName());
        }
        if (null != basicFacilityDTO.getFacilityMainType()) {
            update.set("facilityMainType", basicFacilityDTO.getFacilityMainType());
        }
        if (null != basicFacilityDTO.getFacilityType()) {
            update.set("facilityType", basicFacilityDTO.getFacilityType());
        }
        if (null != basicFacilityDTO.getFacilityTypeName()) {
            update.set("facilityTypeName", basicFacilityDTO.getFacilityTypeName());
        }
        if (null != basicFacilityDTO.getCapacity()) {
            update.set("capacity", basicFacilityDTO.getCapacity());
        }
        if (null != basicFacilityDTO.getRegionId()) {
            update.set("regionId", basicFacilityDTO.getRegionId());
        }
        if (null != basicFacilityDTO.getFacilityRegionName()) {
            update.set("facilityRegionName", basicFacilityDTO.getFacilityRegionName());
        }
        if (null != basicFacilityDTO.getWorkStatus()) {
            update.set("workStatus", basicFacilityDTO.getWorkStatus());
        }
        if (null != basicFacilityDTO.getWorkStatusName()) {
            update.set("workStatusName", basicFacilityDTO.getWorkStatusName());
        }
        if (null != basicFacilityDTO.getFacilityStatus()) {
            update.set("facilityStatus", basicFacilityDTO.getFacilityStatus());
        }
        if (null != basicFacilityDTO.getFacilityStatusName()) {
            update.set("facilityStatusName", basicFacilityDTO.getFacilityStatusName());
        }
        if (null != basicFacilityDTO.getTenantId()) {
            update.set("tenantId", basicFacilityDTO.getTenantId());
        }
        if (null != basicFacilityDTO.getLat()) {
            update.set("lat", basicFacilityDTO.getLat());
        }
        if (null != basicFacilityDTO.getLng()) {
            update.set("lng", basicFacilityDTO.getLng());
        }
        return update;
    }

    private Update getPersonUpdate(BasicPersonDTO basicPersonDTO) {
        Update update = new Update();
        if (basicPersonDTO.getJobNumber() != null) {
            update.set("jobNumber", basicPersonDTO.getJobNumber());
        }
        if (basicPersonDTO.getPersonName() != null) {
            update.set("personName", basicPersonDTO.getPersonName());
        }
        if (basicPersonDTO.getPersonDeptId() != null) {
            update.set("personDeptId", basicPersonDTO.getPersonDeptId());
        }
        if (basicPersonDTO.getPersonPositionId() != null) {
            update.set("personPositionId", basicPersonDTO.getPersonPositionId());
        }
        if (basicPersonDTO.getMobileNumber() != null) {
            update.set("mobileNumber", basicPersonDTO.getMobileNumber());
        }
        if (basicPersonDTO.getWechatId() != null) {
            update.set("wechatId", basicPersonDTO.getWechatId());
        }
//        if (basicPersonDTO.getWatchDeviceId() != null) {
//        }
//        if (basicPersonDTO.getWatchDeviceCode() != null) {
//        }
        update.set("watchDeviceId", basicPersonDTO.getWatchDeviceId());
        update.set("watchDeviceCode", basicPersonDTO.getWatchDeviceCode());

        if (basicPersonDTO.getEmail() != null) {
            update.set("email", basicPersonDTO.getEmail());
        }
        if (basicPersonDTO.getWorkStatus() != null) {
            update.set("workStatus", basicPersonDTO.getWorkStatus());
        }
        if (basicPersonDTO.getWorkStatusName() != null) {
            update.set("workStatusName", basicPersonDTO.getWorkStatusName());
        }
        if (basicPersonDTO.getTodayAlarmCount() != null) {
            update.set("todayAlarmCount", basicPersonDTO.getTodayAlarmCount());
        }
        if (basicPersonDTO.getLastAlarmContent() != null) {
            update.set("lastAlarmContent", basicPersonDTO.getLastAlarmContent());
        }
        if (basicPersonDTO.getTenantId() != null) {
            update.set("tenantId", basicPersonDTO.getTenantId());
        }
        if (basicPersonDTO.getLat() != null) {
            update.set("lat", basicPersonDTO.getLat());
        }
        if (basicPersonDTO.getLng() != null) {
            update.set("lng", basicPersonDTO.getLng());
        }
        if (basicPersonDTO.getPersonBelongRegion() != null) {
            update.set("personBelongRegion", basicPersonDTO.getPersonBelongRegion());
        }
        return update;
    }


    public Update getVehicleUpdate(BasicVehicleInfoDTO basicVehicleInfoDTO) {
        Update update = new Update();


        if (basicVehicleInfoDTO.getPlateNumber() != null) {
            update.set("plateNumber", basicVehicleInfoDTO.getPlateNumber());
        }
        if (basicVehicleInfoDTO.getKindCode() != null) {
            update.set("kindCode", basicVehicleInfoDTO.getKindCode());
        }
        if (basicVehicleInfoDTO.getEntityCategoryId() != null) {
            update.set("entityCategoryId", basicVehicleInfoDTO.getEntityCategoryId());
        }
        if (basicVehicleInfoDTO.getVehicleTypeName() != null) {
            update.set("vehicleTypeName", basicVehicleInfoDTO.getVehicleTypeName());
        }
        if (basicVehicleInfoDTO.getDeptId() != null) {
            update.set("deptId", basicVehicleInfoDTO.getDeptId());
        }
        if (basicVehicleInfoDTO.getWorkStatus() != null) {
            update.set("workStatus", basicVehicleInfoDTO.getWorkStatus());
        }
        if (basicVehicleInfoDTO.getWorkStatusName() != null) {
            update.set("workStatusName", basicVehicleInfoDTO.getWorkStatusName());
        }
        if (basicVehicleInfoDTO.getTodayAlarmCount() != null) {
            update.set("todayAlarmCount", basicVehicleInfoDTO.getTodayAlarmCount());
        }
        if (basicVehicleInfoDTO.getLastAlarmContent() != null) {
            update.set("lastAlarmContent", basicVehicleInfoDTO.getLastAlarmContent());
        }
        if (basicVehicleInfoDTO.getTenantId() != null) {
            update.set("tenantId", basicVehicleInfoDTO.getTenantId());
        }
        if (basicVehicleInfoDTO.getLat() != null) {
            update.set("lat", basicVehicleInfoDTO.getLat());
        }
        if (basicVehicleInfoDTO.getLng() != null) {
            update.set("lng", basicVehicleInfoDTO.getLng());
        }
//        if (basicVehicleInfoDTO.getAccDeviceId() != null) {
//        }
        update.set("accDeviceId", basicVehicleInfoDTO.getAccDeviceId());
//        if (basicVehicleInfoDTO.getAccDeviceCode() != null) {
//        }
        update.set("accDeviceCode", basicVehicleInfoDTO.getAccDeviceCode());
        if (basicVehicleInfoDTO.getGpsDeviceId() != null) {
        }
        update.set("gpsDeviceId", basicVehicleInfoDTO.getGpsDeviceId());
//        if (basicVehicleInfoDTO.getGpsDeviceCode() != null) {
//        }
        update.set("gpsDeviceCode", basicVehicleInfoDTO.getGpsDeviceCode());
//        if (basicVehicleInfoDTO.getNvrDeviceId() != null) {
//        }
        update.set("nvrDeviceId", basicVehicleInfoDTO.getNvrDeviceId());
//        if (basicVehicleInfoDTO.getNvrDeviceCode() != null) {
//        }
        update.set("nvrDeviceCode", basicVehicleInfoDTO.getNvrDeviceCode());
//        if (basicVehicleInfoDTO.getCvrDeviceId() != null) {
//        }
        update.set("cvrDeviceId", basicVehicleInfoDTO.getCvrDeviceId());
//        if (basicVehicleInfoDTO.getCvrDeviceCode() != null) {
//        }
        update.set("cvrDeviceCode", basicVehicleInfoDTO.getCvrDeviceCode());
//        if (basicVehicleInfoDTO.getVehicleBelongRegion() != null) {
//        }
        update.set("vehicleBelongRegion", basicVehicleInfoDTO.getVehicleBelongRegion());

        return update;
    }


    private void sendVehicleChangeMessageToMQ(List<Long> vehicleIdList) {
        try {
//            MessageChannel messageChannel = omnicProducerSource.polymerizationVehicleChangeOutput();
//            Message<List<Long>> message = MessageBuilder.withPayload(vehicleIdList).build();
//            messageChannel.send(message);
        } catch (Exception e) {
            log.warn("发布车辆聚合数据更改信息数据失败", e);
        }
    }


    private void sendPersonChangeMessageToMQ(List<Long> personIdList) {
        try {
//            MessageChannel messageChannel = omnicProducerSource.polymerizationVehicleChangeOutput();
//            Message<List<Long>> message = MessageBuilder.withPayload(personIdList).build();
//            messageChannel.send(message);
        } catch (Exception e) {
            log.warn("发布人员聚合数据更改信息数据失败", e);
        }
    }

    /**
     * 根据大数据侧传过来的设备工作状态,更新应用侧的工作状态
     *
     * @param workAreaType 大数据传过来的工作状态
     * @param deviceCode   设备编号
     * @return
     */
    @Override
    public Boolean updateWorkStatus(Integer workAreaType, String deviceCode) {
        //根据设备Code获取设备信息
        DeviceInfo deviceInfo = DeviceCache.getDeviceByCode(deviceCode);
        if (deviceInfo == null || deviceInfo.getId() == null) {
            log.warn("设备信息不存在");
            return false;
        }
        if (isVirtualDevice(deviceInfo)) {
            log.warn("该设备是虚拟设备,忽略");
            return false;
        }
        Integer workStatus = WorkAreaConstant.WorkStatus.ONLINE;
        //查询该设备关联的人或车,获取排班数据
        DeviceRel deviceRel = DeviceRelCache.getDeviceRel(deviceInfo.getId());
        String entityType = deviceRel.getEntityType();
        LocalDate today = LocalDate.now();
        Boolean needWork = false;
        List<ScheduleObject> scheduleList = null;

        String websocketEventType = null;


        if (CommonConstant.ENTITY_TYPE.PERSON.toString().equals(entityType)) {
            String deviceStatus = DeviceCache.getDeviceStatus(deviceRel.getEntityId(), DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE, deviceRel.getTenantId());
            scheduleList = ScheduleCache.getScheduleObjectByEntityAndDate(deviceRel.getEntityId(), ArrangeConstant.ScheduleObjectEntityType.PERSON, today);
            if (CollectionUtil.isNotEmpty(scheduleList) && !isVaction(scheduleList)) {
                needWork = scheduleClient.checkNowNeedWork(deviceRel.getEntityId(), ArrangeConstant.ScheduleObjectEntityType.PERSON).getData();
                workStatus = this.getWorkStatus(workAreaType, needWork, Long.parseLong(deviceStatus));
            } else if (CollectionUtil.isEmpty(scheduleList)) {
                //排班信息为空,则直接设置工作状态为未排班
                workStatus = WorkAreaConstant.WorkStatus.UN_ARRANGE;
            } else if (isVaction(scheduleList)) {
                workStatus = WorkAreaConstant.WorkStatus.VACATION;
            }


            //更新MongoDB中的数据
            this.updatePersonWorkStatus(deviceRel.getEntityId(), workStatus, Long.parseLong(deviceStatus));

            websocketEventType = PERSON_WORK_STATUS_EVENT;
        } else if (CommonConstant.ENTITY_TYPE.VEHICLE.toString().equals(entityType)) {

            String deviceStatus = DeviceCache.getDeviceStatus(deviceRel.getEntityId(), VehicleConstant.VEHICLE_ACC_DEVICE_TYPE, deviceRel.getTenantId());

            scheduleList = ScheduleCache.getScheduleObjectByEntityAndDate(deviceRel.getEntityId(), ArrangeConstant.ScheduleObjectEntityType.VEHICLE, today);
            if (CollectionUtil.isNotEmpty(scheduleList) && !isVaction(scheduleList)) {
                needWork = scheduleClient.checkNowNeedWork(deviceRel.getEntityId(), ArrangeConstant.ScheduleObjectEntityType.VEHICLE).getData();
                workStatus = this.getWorkStatus(workAreaType, needWork, Long.parseLong(deviceStatus));
            } else if (CollectionUtil.isEmpty(scheduleList)) {
                //排班信息为空,则直接设置工作状态为未排班
                workStatus = WorkAreaConstant.WorkStatus.UN_ARRANGE;
            } else if (isVaction(scheduleList)) {
                workStatus = WorkAreaConstant.WorkStatus.VACATION;
            }
            //更新MongoDB中的数据
            this.updateVehicleWorkStatus(deviceRel.getEntityId(), workStatus, Long.parseLong(deviceStatus));

            websocketEventType = VEHICLE_WORK_STATUS_EVENT;
        }

        //触发websocket推送事件
        BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO = new BaseWsMonitorEventDTO<String>(websocketEventType, deviceRel.getTenantId(), null, String.valueOf(deviceRel.getEntityId()));
        dataChangeEventService.doWebsocketEvent(baseWsMonitorEventDTO);

        return true;
    }

    /**
     * 判断是否为休假
     *
     * @return
     */
    public Boolean isVaction(List<ScheduleObject> scheduleList) {
        if (scheduleList == null) {
            return true;
        }

        for (ScheduleObject scheduleObject : scheduleList) {
            if (scheduleObject.getStatus().equals(ArrangeConstant.TureOrFalse.INT_TRUE)) {
                return false;
            }
        }
        return true;

    }


    /**
     * 根据大数据侧传过来的设备列表，批量更新应用侧的工作状态
     *
     * @param deviceList
     * @return
     */
    @Override
    public Boolean batchUpdateWorkStatus(List<DeviceWorkAreaDTO> deviceList) {
        ThreadUtil.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                deviceList.stream().forEach(deviceWorkAreaDTO -> {
                    log.info("updateWorkType Params[workAreaType={},deviceCode={}]", deviceWorkAreaDTO.getWorkAreaType(), deviceWorkAreaDTO.getDeviceCode());
                    updateWorkStatus(deviceWorkAreaDTO.getWorkAreaType(), deviceWorkAreaDTO.getDeviceCode());
                });
            }
        }));
        return true;
    }

    /**
     * 判断该设备是否是虚拟设备
     *
     * @param deviceInfo
     * @return
     */
    private Boolean isVirtualDevice(DeviceInfo deviceInfo) {
        if (deviceInfo == null) {
            return false;
        }
        DeviceRel deviceRel = DeviceRelCache.getDeviceRel(deviceInfo.getId());
        //人员没有虚拟设备
        if (CommonConstant.ENTITY_TYPE.PERSON.toString().equals(deviceRel.getEntityType())) {
            return false;
        }
        if (!DeviceConstant.DeviceFactory.MINICREATE.equals(deviceInfo.getDeviceFactory())) {
            return false;
        }
        if (DeviceConstant.DeviceCategory.VEHICLE_NVR_MONITOR_DEVICE.equals(deviceInfo.getEntityCategoryId())) {
            return false;
        } else if (DeviceConstant.DeviceCategory.VEHICLE_CVR_MONITOR_DEVICE.equals(deviceInfo.getEntityCategoryId())) {
            return false;
        }

        return true;
    }

    /**
     * 根据大数据传过来的区域工作状态、是否需要工作、设备工作状态(是否开启)获取真实的工作状态
     *
     * @param workAreaType
     * @param needWork
     * @param deviceStatus
     * @return
     */
    private Integer getWorkStatus(Integer workAreaType, Boolean needWork, Long deviceStatus) {
        switch (workAreaType) {
            case WorkAreaConstant.AreaCategory.NO_SINGLE: {
                if (needWork) {
                    //如果是上班时间,设置为静值,否则是休息
                    return WorkAreaConstant.WorkStatus.ON_STANDBY;
                } else {
                    return WorkAreaConstant.WorkStatus.REST;
                }
            }
            case WorkAreaConstant.AreaCategory.WATER_AREA: {
                return WorkAreaConstant.WorkStatus.WATERING;
            }
            case WorkAreaConstant.AreaCategory.GAS_AREA: {
                return WorkAreaConstant.WorkStatus.OIL_ING;
            }
            case WorkAreaConstant.AreaCategory.PERSON_REST_AREA:
            case WorkAreaConstant.AreaCategory.VEHICLE_REST_AREA: {
                return WorkAreaConstant.WorkStatus.REST;
            }
            case WorkAreaConstant.AreaCategory.MAINTAIN_AREA: {
                return WorkAreaConstant.WorkStatus.VEHICLE_MAINTAIN;
            }
            case WorkAreaConstant.AreaCategory.PERSON_WORK_AREA:
            case WorkAreaConstant.AreaCategory.VEHICLE_RUN_AREA:
            case WorkAreaConstant.AreaCategory.VEHICLE_WORK_AREA:
            case WorkAreaConstant.AreaCategory.NONE: {
                //设备状态开启为正常
                if (Long.valueOf(DeviceConstant.DeviceStatus.ON).equals(deviceStatus)) {
                    return WorkAreaConstant.WorkStatus.ONLINE;
                } else {
                    //设备状态开启为关闭
                    if (needWork) {
                        //如果是上班时间,设置为静值,否则是休息
                        return WorkAreaConstant.WorkStatus.ON_STANDBY;
                    } else {
                        return WorkAreaConstant.WorkStatus.REST;
                    }
                }
            }
            default: {
                //如果没有命中任何工作区域类型,则返回工作状态
                return WorkAreaConstant.WorkStatus.ONLINE;
            }
        }
    }

}
