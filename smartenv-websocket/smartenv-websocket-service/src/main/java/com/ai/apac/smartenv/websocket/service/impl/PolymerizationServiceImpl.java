package com.ai.apac.smartenv.websocket.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSON;
import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.event.dto.EventQueryDTO;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.event.vo.EventInfoVO;
import com.ai.apac.smartenv.facility.dto.BasicFacilityDTO;
import com.ai.apac.smartenv.facility.dto.ToiletQueryDTO;
import com.ai.apac.smartenv.facility.feign.IAshcanClient;
import com.ai.apac.smartenv.facility.feign.IFacilityClient;
import com.ai.apac.smartenv.facility.feign.IToiletClient;
import com.ai.apac.smartenv.facility.vo.AshcanInfoVO;
import com.ai.apac.smartenv.facility.vo.FacilityInfoExtVO;
import com.ai.apac.smartenv.facility.vo.ToiletInfoVO;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.feign.IRegionClient;
import com.ai.apac.smartenv.system.vo.BusiRegionTreeVO;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.controller.BigScreenController;
import com.ai.apac.smartenv.websocket.controller.PolymerizationController;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonDetailVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonMonitorInfoVO;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.*;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.AshcanPolyDetailDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.PolymerizationConditionDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.PolymerizationDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.BasicPolymerizationDetailDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.TransferStationPolyDetailDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.VehiclePolymerizationDetailDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.vo.PolymerizationCountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleDetailVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorInfoVO;
import com.ai.apac.smartenv.websocket.service.IBaseService;
import com.ai.apac.smartenv.websocket.service.IPersonService;
import com.ai.apac.smartenv.websocket.service.IPolymerizationService;
import com.ai.apac.smartenv.websocket.service.IVehicleService;
import com.ai.apac.smartenv.websocket.task.PolymerizationDetailTask;
import com.ai.apac.smartenv.websocket.task.PolymerizationEntityCountTask;
import com.ai.apac.smartenv.websocket.task.PolymerizationListTask;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springblade.core.tool.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PolymerizationServiceImpl implements IPolymerizationService {
    @Autowired
    private WebSocketTaskService websocketTaskService;

    @Autowired
    private IBaseService baseService;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private IVehicleService vehicleService;

    @Autowired
    private IPersonService personService;

    @Autowired
    private IRegionClient regionClient;

    @Autowired
    private IAlarmInfoClient alarmInfoClient;

    @Autowired
    private IAshcanClient ashcanClient;

    @Autowired
    private IFacilityClient facilityClient;

    @Autowired
    private CoordsTypeConvertUtil coordsTypeConvertUtil;

    @Autowired
    private IEventInfoClient eventInfoClient;

    @Autowired
    private IToiletClient toiletClient;


    @Autowired
    private IPersonClient personClient;

    @Autowired
    private IVehicleClient vehicleClient;

    @Autowired
    private BladeRedisCache bladeRedisCache;


    @Override
    public void updatePolymerizationCountRedis(String tenantId, String entityType) {
        try {

            String cacheName = CacheNames.Polymerization_DATA + StringPool.COLON + PolymerizationController.GET_ALL_ENTITY_COUNT;
            String cacheName1 = CacheNames.Polymerization_DATA + StringPool.COLON + BigScreenController.GET_BIGSCREEN_ALL_ENTITY_COUNT;

            bladeRedisCache.hDel(cacheName, tenantId);
            bladeRedisCache.hDel(cacheName1, tenantId);


            PolymerizationCountVO polymerizationCountVO = new PolymerizationCountVO();

            List<VehicleInfo> vehicleInfoList = vehicleClient.getVehicleByTenant(tenantId).getData();
            Long onVehicleCount = 0l;

            Long offVehicleCount = 0l;

            Long nodVehicleCount = 0l;

            Long vehicleCount = 0l;
            if (ObjectUtil.isNotEmpty(vehicleInfoList) && vehicleInfoList.size() > 0) {
                for (VehicleInfo vehicleInfo : vehicleInfoList) {
                    if (vehicleInfo.getIsUsed() != null && vehicleInfo.getIsUsed() == VehicleConstant.VehicleState.IN_USED) {
                        vehicleCount ++;
                        switch (vehicleInfo.getAccDeviceStatus().toString()) {
                            case DeviceConstant.DeviceStatus.NO:
                                nodVehicleCount++;
                                break;
                            case DeviceConstant.DeviceStatus.NO_DEV:
                                nodVehicleCount++;
                                break;
                            case DeviceConstant.DeviceStatus.OFF:
                                offVehicleCount++;
                                break;
                            case DeviceConstant.DeviceStatus.ON:
                                onVehicleCount++;
                                break;
                            default:
                                nodVehicleCount++;
                        }
                    }

                }
                polymerizationCountVO.setOffVehicleCount(offVehicleCount);
                polymerizationCountVO.setOnVehicleCount(onVehicleCount);
                polymerizationCountVO.setNodVehicleCount(nodVehicleCount);
                polymerizationCountVO.setVehicleCount(vehicleCount);


            }


            List<Person> personList = personClient.getPersonByTenant(tenantId).getData();

            Long onPersonCount = 0l;

            Long offPersonCount = 0l;

            Long nodPersonCount = 0l;

            Long personCount = 0l;
            if (ObjectUtil.isNotEmpty(personList) && personList.size() > 0) {
                for (Person person : personList) {
                    if (person.getIsIncumbency() != null && (person.getIsIncumbency() == PersonConstant.IncumbencyStatus.IN ||person.getIsIncumbency() == PersonConstant.IncumbencyStatus.TEMPORARY )) {
                        personCount++;
                        switch (person.getWatchDeviceStatus().toString()) {
                            case DeviceConstant.DeviceStatus.NO:
                                nodPersonCount++;
                                break;
                            case DeviceConstant.DeviceStatus.NO_DEV:
                                nodPersonCount++;
                                break;
                            case DeviceConstant.DeviceStatus.OFF:
                                offPersonCount++;
                                break;
                            case DeviceConstant.DeviceStatus.ON:
                                onPersonCount++;
                                break;
                            default:
                                nodPersonCount++;
                        }
                    }
                }
                polymerizationCountVO.setOffPersonCount(offPersonCount);
                polymerizationCountVO.setOnPersonCount(onPersonCount);
                polymerizationCountVO.setNodPersonCount(nodPersonCount);
                polymerizationCountVO.setPersonCount(personCount);

            }


            polymerizationCountVO.setFacilityCount(facilityClient.countAllFacility(tenantId).getData().longValue());


            polymerizationCountVO.setAshcanCount(ashcanClient.countAshcanInfo(tenantId).getData().longValue());


            polymerizationCountVO.setToiletCount(toiletClient.countAllToilet(tenantId).getData().longValue());


            polymerizationCountVO.setTodayEventCount(eventInfoClient.countEventDaily(tenantId).getData().longValue());


            Long alarmAmount = alarmInfoClient.countAlarmInfoAmount(tenantId).getData().longValue();
            polymerizationCountVO.setTodayAlarmCount(alarmAmount);

            log.info("countAlarmInfoAmount:"+alarmAmount);
            bladeRedisCache.hSet(cacheName, tenantId, polymerizationCountVO);
            bladeRedisCache.hSet(cacheName1, tenantId, polymerizationCountVO);

            Date endToday = TimeUtil.getDateOfCurrentEndDay(new Date());
            bladeRedisCache.expireAt(cacheName, endToday);
            bladeRedisCache.expireAt(cacheName1, endToday);
        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
    }

    @Override
    public void pushPolymerizationEntityCount(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        websocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        websocketTaskService.createTask(websocketTask);
        PolymerizationEntityCountTask task = new PolymerizationEntityCountTask(websocketTask);
        task.run();
    }

    @Override
    public PolymerizationCountVO getPolymerizationEntityCount(String tenantId) {
        log.info("--------------getPolymerizationEntityCount"+tenantId);

        PolymerizationCountVO polymerizationCountVO = new PolymerizationCountVO();

        List<VehicleInfo> vehicleInfoList = vehicleClient.getVehicleByTenant(tenantId).getData();
        Long onVehicleCount = 0l;

        Long offVehicleCount = 0l;

        Long nodVehicleCount = 0l;

        Long vehicleCount = 0l;
        if (ObjectUtil.isNotEmpty(vehicleInfoList) && vehicleInfoList.size() > 0) {
            log.info("--------------vehicleInfoList");

            for (VehicleInfo vehicleInfo : vehicleInfoList) {
                if (vehicleInfo.getIsUsed() != null && vehicleInfo.getIsUsed() == VehicleConstant.VehicleState.IN_USED) {
                    vehicleCount ++;
                    switch (vehicleInfo.getAccDeviceStatus().toString()) {
                        case DeviceConstant.DeviceStatus.NO:
                            nodVehicleCount++;
                            break;
                        case DeviceConstant.DeviceStatus.NO_DEV:
                            nodVehicleCount++;
                            break;
                        case DeviceConstant.DeviceStatus.OFF:
                            offVehicleCount++;
                            break;
                        case DeviceConstant.DeviceStatus.ON:
                            onVehicleCount++;
                            break;
                        default:
                            nodVehicleCount++;
                    }
                }

            }
            polymerizationCountVO.setOffVehicleCount(offVehicleCount);
            polymerizationCountVO.setOnVehicleCount(onVehicleCount);
            polymerizationCountVO.setNodVehicleCount(nodVehicleCount);
            polymerizationCountVO.setVehicleCount(vehicleCount);


        }


        List<Person> personList = personClient.getPersonByTenant(tenantId).getData();

        Long onPersonCount = 0l;

        Long offPersonCount = 0l;

        Long nodPersonCount = 0l;

        Long personCount = 0l;
        if (ObjectUtil.isNotEmpty(personList) && personList.size() > 0) {
            log.info("--------------personList");

            for (Person person : personList) {
                if (person.getIsIncumbency() != null && (person.getIsIncumbency() == PersonConstant.IncumbencyStatus.IN ||person.getIsIncumbency() == PersonConstant.IncumbencyStatus.TEMPORARY )) {
                    personCount++;
                    switch (person.getWatchDeviceStatus().toString()) {
                        case DeviceConstant.DeviceStatus.NO:
                            nodPersonCount++;
                            break;
                        case DeviceConstant.DeviceStatus.NO_DEV:
                            nodPersonCount++;
                            break;
                        case DeviceConstant.DeviceStatus.OFF:
                            offPersonCount++;
                            break;
                        case DeviceConstant.DeviceStatus.ON:
                            onPersonCount++;
                            break;
                        default:
                            nodPersonCount++;
                    }
                }
            }
            polymerizationCountVO.setOffPersonCount(offPersonCount);
            polymerizationCountVO.setOnPersonCount(onPersonCount);
            polymerizationCountVO.setNodPersonCount(nodPersonCount);
            polymerizationCountVO.setPersonCount(personCount);

        }


        polymerizationCountVO.setFacilityCount(facilityClient.countAllFacility(tenantId).getData().longValue());


        polymerizationCountVO.setAshcanCount(ashcanClient.countAshcanInfo(tenantId).getData().longValue());


        polymerizationCountVO.setToiletCount(toiletClient.countAllToilet(tenantId).getData().longValue());


        polymerizationCountVO.setTodayEventCount(eventInfoClient.countEventDaily(tenantId).getData().longValue());


        Long alarmAmount = alarmInfoClient.countAlarmInfoAmount(tenantId).getData().longValue();
        polymerizationCountVO.setTodayAlarmCount(alarmAmount);

        log.info("getPolymerizationEntityCount--------"+JsonUtil.toJson(polymerizationCountVO));


        return polymerizationCountVO;

    }

    /**
     * 推送聚合数据列表到前端websocket
     *
     * @param websocketTask
     */
    @Override
    public void pushPolymerizationEntityList(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        websocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        websocketTaskService.createTask(websocketTask);


//
        PolymerizationListTask task = new PolymerizationListTask(websocketTask);
        baseService.getTaskExecutor().execute(task);

    }


    /**
     * 推送聚合数据详情到websocket
     *
     * @param websocketTask
     */
    @Override
    public void pushPolymerizationEntityDetail(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        websocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        websocketTaskService.createTask(websocketTask);

        websocketTaskService.createTask(websocketTask);
        PolymerizationDetailTask task = new PolymerizationDetailTask(websocketTask);
        baseService.getTaskExecutor().execute(task);
    }


    /**
     * 根据条件查询人员
     *
     * @return
     */
    @Override
    public List<PolymerizationDTO> getPersonPolymerization(PolymerizationConditionDTO conditionDTO, String tenantId) {
        //条件不允许为空
        if (CollectionUtil.isEmpty(conditionDTO.getPerosnDepts()) && CollectionUtil.isEmpty(conditionDTO.getPersonWorkstatus()) && CollectionUtil.isEmpty(conditionDTO.getPersonWatchStatuses())) {
            return null;
        }

        Query query = new Query();
        //条件拼接
        if (CollectionUtil.isNotEmpty(conditionDTO.getPerosnDepts())) {
            query.addCriteria(Criteria.where("personPositionId").in(conditionDTO.getPerosnDepts()));
        }
        if (CollectionUtil.isNotEmpty(conditionDTO.getPersonWorkstatus())) {
            query.addCriteria(Criteria.where("workStatus").in(conditionDTO.getPersonWorkstatus()));
        }
        if (CollectionUtil.isNotEmpty(conditionDTO.getPersonWatchStatuses())) {
            if (conditionDTO.getPersonWatchStatuses().size() == 1 && conditionDTO.getPersonWatchStatuses().get(0).equals(Long.parseLong(DeviceConstant.DeviceStatus.ON))) {
                query.addCriteria(Criteria.where("deviceStatus").in(Long.parseLong(DeviceConstant.DeviceStatus.ON)));
            } else if (conditionDTO.getPersonWatchStatuses().size() == 1 && conditionDTO.getPersonWatchStatuses().get(0).equals(Long.parseLong(DeviceConstant.DeviceStatus.OFF))) {
                query.addCriteria(Criteria.where("deviceStatus").ne(Long.parseLong(DeviceConstant.DeviceStatus.ON)));
            }
        }
        //区域ID需要加载下级所有区域ID
        if (CollectionUtil.isNotEmpty(conditionDTO.getRegionIds())) {
            query.addCriteria(Criteria.where("personBelongRegion").in(getAllSubRegionByRegionIds(conditionDTO.getRegionIds())));
        }
        if (StringUtil.isNotBlank(conditionDTO.getKeyWord())) {
            query.addCriteria(Criteria.where("personName").regex("^.*" + conditionDTO.getKeyWord() + ".*$"));
        }
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        List<BasicPersonDTO> basicPersonDTOS = mongoTemplate.find(query, BasicPersonDTO.class);
        if (CollectionUtil.isEmpty(basicPersonDTOS)) {
            return null;
        }
        //转为PolymerizationDTO对象
        List<PolymerizationDTO> collect = basicPersonDTOS.stream().map(basicPersonDTO -> {
            try {
                PolymerizationDTO polymerizationDTO = new PolymerizationDTO();
                polymerizationDTO.setObjID(basicPersonDTO.getId().toString());
                polymerizationDTO.setObjName(basicPersonDTO.getPersonName());
                polymerizationDTO.setObjType(WebSocketConsts.PolymerizationType.PERSON);
//            polymerizationDTO.setObjLat(basicPersonDTO.getLat());
//            polymerizationDTO.setObjLng(basicPersonDTO.getLng());


                if (basicPersonDTO.getLng() == null || basicPersonDTO.getLng() == null) {
                    PersonMonitorInfoVO personMonitorInfo = personService.getPersonMonitorInfo(basicPersonDTO.getId().toString(), BaiduMapUtils.CoordsSystem.GC02);
                    if (personMonitorInfo != null && personMonitorInfo.getPosition() != null) {
                        polymerizationDTO.setObjLat(personMonitorInfo.getPosition().getLat());
                        polymerizationDTO.setObjLng(personMonitorInfo.getPosition().getLng());
                    }
                } else {
                    polymerizationDTO.setObjLat(basicPersonDTO.getLat());
                    polymerizationDTO.setObjLng(basicPersonDTO.getLng());
                }
                polymerizationDTO.setCategoryName(basicPersonDTO.getPersonPositionName());
                polymerizationDTO.setCategoryId(basicPersonDTO.getPersonPositionId());
                polymerizationDTO.setIsAlarm(basicPersonDTO == null ? false : basicPersonDTO.getTodayAlarmCount() != null && !basicPersonDTO.getTodayAlarmCount().equals(0));
                polymerizationDTO.setAlarmName(basicPersonDTO.getLastAlarmContent());
                polymerizationDTO.setWorkStatus(basicPersonDTO.getWorkStatus());
                polymerizationDTO.setWorkStatusName(basicPersonDTO.getWorkStatusName());
                polymerizationDTO.setRegionId(basicPersonDTO.getPersonBelongRegion());
                if (polymerizationDTO.getIsAlarm()) {
                    polymerizationDTO.setObjIcon(websocketTaskService.getOssObjLink(WebSocketConsts.PolymerizationIcon.PERSON_ALARM));
                } else {
                    polymerizationDTO.setObjIcon(websocketTaskService.getOssObjLink(WebSocketConsts.PolymerizationIcon.PERSON_NORMAL));
                }
                return polymerizationDTO;
            } catch (Exception e) {
                return null;
            }
        }).collect(Collectors.toList()).stream().filter(polymerizationDTO -> polymerizationDTO != null && StringUtil.isNotBlank(polymerizationDTO.getObjLat()) && StringUtil.isNotBlank(polymerizationDTO.getObjLng())).collect(Collectors.toList());

        coordsTypeConvertUtil.objectCoordsConvert(BaiduMapUtils.CoordsSystem.GC02, BaiduMapUtils.CoordsSystem.BD09LL, collect);

        return collect;
    }


    /**
     * 根据条件查询车辆
     *
     * @return
     */
    @Override
    public List<PolymerizationDTO> getVehiclePolymerization(PolymerizationConditionDTO conditionDTO, String tenantId) {
        if (CollectionUtil.isEmpty(conditionDTO.getVehicleTypes()) && CollectionUtil.isEmpty(conditionDTO.getVehicleWorkstatus()) && CollectionUtil.isEmpty(conditionDTO.getVehicleAccStatuses())) {
            return null;
        }
        Query query = new Query();
        if (CollectionUtil.isNotEmpty(conditionDTO.getVehicleTypes())) {
            query.addCriteria(Criteria.where("entityCategoryId").in(conditionDTO.getVehicleTypes()));
        }
        if (CollectionUtil.isNotEmpty(conditionDTO.getVehicleWorkstatus())) {
            query.addCriteria(Criteria.where("workStatus").in(conditionDTO.getVehicleWorkstatus()));
        }
        if (CollectionUtil.isNotEmpty(conditionDTO.getVehicleAccStatuses())) {
            if (conditionDTO.getVehicleAccStatuses().size() == 1 && conditionDTO.getVehicleAccStatuses().get(0).equals(Long.parseLong(DeviceConstant.DeviceStatus.ON))) {
                query.addCriteria(Criteria.where("deviceStatus").in(Long.parseLong(DeviceConstant.DeviceStatus.ON)));
            } else if (conditionDTO.getVehicleAccStatuses().size() == 1 && conditionDTO.getVehicleAccStatuses().get(0).equals(Long.parseLong(DeviceConstant.DeviceStatus.OFF))) {
                query.addCriteria(Criteria.where("deviceStatus").ne(Long.parseLong(DeviceConstant.DeviceStatus.ON)));
            }
        }
        if (CollectionUtil.isNotEmpty(conditionDTO.getRegionIds())) {
            query.addCriteria(Criteria.where("vehicleBelongRegion").in(getAllSubRegionByRegionIds(conditionDTO.getRegionIds())));
        }
        if (StringUtil.isNotBlank(conditionDTO.getKeyWord())) {
            query.addCriteria(Criteria.where("plateNumber").regex("^.*" + conditionDTO.getKeyWord() + ".*$"));
        }
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        List<BasicVehicleInfoDTO> basicVehicleInfoDTOS = mongoTemplate.find(query, BasicVehicleInfoDTO.class);
        if (CollectionUtil.isEmpty(basicVehicleInfoDTOS)) {
            return null;
        }
        List<PolymerizationDTO> collect = basicVehicleInfoDTOS.stream().map(basicVehicleInfoDTO -> {
            PolymerizationDTO polymerizationDTO = new PolymerizationDTO();
            polymerizationDTO.setObjID(basicVehicleInfoDTO.getId().toString());
            polymerizationDTO.setObjName(basicVehicleInfoDTO.getPlateNumber());
            polymerizationDTO.setObjType(WebSocketConsts.PolymerizationType.VEHICLE);
//            polymerizationDTO.setObjLat(basicVehicleInfoDTO.getLat());
//            polymerizationDTO.setObjLng(basicVehicleInfoDTO.getLng());
            if (basicVehicleInfoDTO.getLat() == null || basicVehicleInfoDTO.getLng() == null) {
                try {
                    VehicleMonitorInfoVO vehicleMonitorInfo = vehicleService.getVehicleMonitorInfo(basicVehicleInfoDTO.getId().toString(), BaiduMapUtils.CoordsSystem.BD09LL);
                    if (vehicleMonitorInfo != null && vehicleMonitorInfo.getPosition() != null) {
                        PositionDTO position = vehicleMonitorInfo.getPosition();
                        polymerizationDTO.setObjLat(position.getLat());
                        polymerizationDTO.setObjLng(position.getLng());
                    }
                } catch (Exception e) {
                    log.warn("获取位置失败：" + basicVehicleInfoDTO.getPlateNumber(), e);
                }
            } else {
                polymerizationDTO.setObjLat(basicVehicleInfoDTO.getLat());
                polymerizationDTO.setObjLng(basicVehicleInfoDTO.getLng());
                polymerizationDTO.setGpsTime(System.currentTimeMillis());
            }


//            polymerizationDTO.setObjLat("115.762611");
//            polymerizationDTO.setObjLng("29.327474");
            polymerizationDTO.setCategoryId(basicVehicleInfoDTO.getEntityCategoryId());
            polymerizationDTO.setCategoryName(basicVehicleInfoDTO.getVehicleTypeName());
//            polymerizationDTO.setCategoryName("洒水车");
            polymerizationDTO.setIsAlarm(basicVehicleInfoDTO.getTodayAlarmCount() == null ? false : basicVehicleInfoDTO.getTodayAlarmCount() != 0);
            polymerizationDTO.setAlarmName(basicVehicleInfoDTO.getLastAlarmContent());

            polymerizationDTO.setWorkStatus(basicVehicleInfoDTO.getWorkStatus());
            polymerizationDTO.setWorkStatusName(basicVehicleInfoDTO.getWorkStatusName());
            polymerizationDTO.setRegionId(basicVehicleInfoDTO.getVehicleBelongRegion());
            if (polymerizationDTO.getIsAlarm()) {
                polymerizationDTO.setObjIcon(websocketTaskService.getOssObjLink(WebSocketConsts.PolymerizationIcon.VEHICLE_ALARM));
            } else {
                polymerizationDTO.setObjIcon(websocketTaskService.getOssObjLink(WebSocketConsts.PolymerizationIcon.VEHICLE_NORMAL));
            }
            return polymerizationDTO;
        }).collect(Collectors.toList()).stream().filter(polymerizationDTO -> polymerizationDTO != null && StringUtil.isNotBlank(polymerizationDTO.getObjLat()) && StringUtil.isNotBlank(polymerizationDTO.getObjLng())).collect(Collectors.toList());
        coordsTypeConvertUtil.objectCoordsConvert(BaiduMapUtils.CoordsSystem.GC02, BaiduMapUtils.CoordsSystem.BD09LL, collect);

        return collect;


    }


    /**
     * 根据条件查询中转站
     *
     * @return
     */
    @Override
    public List<PolymerizationDTO> getTransferStationPolymerization(PolymerizationConditionDTO conditionDTO, String tenantId) {
        if (CollectionUtil.isEmpty(conditionDTO.getTransferStationScales()) && CollectionUtil.isEmpty(conditionDTO.getTransferStationStatuses())) {
            return null;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("facilityMainType").is(Integer.parseInt(FacilityConstant.FacilityType.TRANSFER_STATION))); // 类型为中转站
        if (CollectionUtil.isNotEmpty(conditionDTO.getTransferStationScales())) {
            query.addCriteria(Criteria.where("facilityType").in(conditionDTO.getTransferStationScales().stream().map(String::valueOf).collect(Collectors.toList())));
        }
        if (CollectionUtil.isNotEmpty(conditionDTO.getTransferStationStatuses())) {
            query.addCriteria(Criteria.where("facilityStatus").in(conditionDTO.getTransferStationStatuses()));
        }
        if (CollectionUtil.isNotEmpty(conditionDTO.getRegionIds())) {
            // 取所有根加子region信息
            List<Long> allSubRegionByRegionIds = getAllSubRegionByRegionIds(conditionDTO.getRegionIds());
            query.addCriteria(Criteria.where("regionId").in(allSubRegionByRegionIds.stream().map(String::valueOf).collect(Collectors.toList())));
        }
        if (StringUtil.isNotBlank(conditionDTO.getKeyWord())) {
            query.addCriteria(Criteria.where("facilityName").regex("^.*" + conditionDTO.getKeyWord() + ".*$"));
        }
        // 带租户查询
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        List<BasicFacilityDTO> basicFacilityDTOList = mongoTemplate.find(query, BasicFacilityDTO.class);
        if (CollectionUtil.isEmpty(basicFacilityDTOList)) {
            return null;
        }
        return basicFacilityDTOList.stream().map(basicFacilityDTO -> {
            PolymerizationDTO polymerizationDTO = new PolymerizationDTO();
            polymerizationDTO.setObjID(basicFacilityDTO.getId().toString());
            polymerizationDTO.setObjName(basicFacilityDTO.getFacilityName());
            polymerizationDTO.setObjType(WebSocketConsts.PolymerizationType.TRANSFER_STATION);
            polymerizationDTO.setObjLat(basicFacilityDTO.getLat());
            polymerizationDTO.setObjLng(basicFacilityDTO.getLng());
            polymerizationDTO.setCategoryName(basicFacilityDTO.getFacilityTypeName());
            polymerizationDTO.setStatus(basicFacilityDTO.getFacilityStatus());
            polymerizationDTO.setStatusName(basicFacilityDTO.getFacilityStatusName());
            polymerizationDTO.setRegionId(Long.valueOf(basicFacilityDTO.getRegionId()));
            polymerizationDTO.setRegionName(basicFacilityDTO.getFacilityRegionName());
            if (FacilityConstant.TranStationStatus.WORKING.equals(basicFacilityDTO.getWorkStatus())) {
                polymerizationDTO.setObjIcon(websocketTaskService.getOssObjLink(WebSocketConsts.PolymerizationIcon.TRANSFER_NORMAL));
            } else {
                polymerizationDTO.setObjIcon(websocketTaskService.getOssObjLink(WebSocketConsts.PolymerizationIcon.TRANSFER_ALARM));
            }
            return polymerizationDTO;
        }).collect(Collectors.toList()).stream().filter(polymerizationDTO -> polymerizationDTO != null && StringUtil.isNotBlank(polymerizationDTO.getObjLat()) && StringUtil.isNotBlank(polymerizationDTO.getObjLng())).collect(Collectors.toList());
    }

    /**
     * 根据条件查询垃圾桶
     *
     * @return
     */
    @Override
    public List<PolymerizationDTO> getAshcanPolymerization(PolymerizationConditionDTO conditionDTO, String tenantId) {
        if (CollectionUtil.isEmpty(conditionDTO.getAshcanTypes()) && CollectionUtil.isEmpty(conditionDTO.getAshcanWorkStatuses())) {
            return null;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("facilityMainType").is(Integer.parseInt(FacilityConstant.FacilityType.ASHCAN))); // 类型为垃圾桶
        if (CollectionUtil.isNotEmpty(conditionDTO.getAshcanTypes())) {
            query.addCriteria(Criteria.where("facilityType").in(conditionDTO.getAshcanTypes().stream().map(String::valueOf).collect(Collectors.toList())));
        }
        if (CollectionUtil.isNotEmpty(conditionDTO.getAshcanStatuses())) {
            query.addCriteria(Criteria.where("facilityStatus").in(conditionDTO.getAshcanStatuses()));
        }
        if (CollectionUtil.isNotEmpty(conditionDTO.getAshcanWorkStatuses())) {
            query.addCriteria(Criteria.where("workStatus").in(conditionDTO.getAshcanWorkStatuses().stream().map(String::valueOf).collect(Collectors.toList())));
        }
        if (CollectionUtil.isNotEmpty(conditionDTO.getRegionIds())) {
            // 取所有根加子region信息
            List<Long> allSubRegionByRegionIds = getAllSubRegionByRegionIds(conditionDTO.getRegionIds());
            query.addCriteria(Criteria.where("regionId").in(allSubRegionByRegionIds.stream().map(String::valueOf).collect(Collectors.toList())));
        }
        if (StringUtil.isNotBlank(conditionDTO.getKeyWord())) {
            query.addCriteria(Criteria.where("facilityName").regex("^.*" + conditionDTO.getKeyWord() + ".*$"));
        }
        // 带租户查询
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        List<BasicFacilityDTO> basicFacilityDTOList = mongoTemplate.find(query, BasicFacilityDTO.class);
        if (CollectionUtil.isEmpty(basicFacilityDTOList)) {
            return null;
        }
        return basicFacilityDTOList.stream().map(basicFacilityDTO -> {
            PolymerizationDTO polymerizationDTO = new PolymerizationDTO();
            polymerizationDTO.setObjID(basicFacilityDTO.getId().toString());
            polymerizationDTO.setObjName(basicFacilityDTO.getFacilityName());
            polymerizationDTO.setObjType(WebSocketConsts.PolymerizationType.ASHCAN);
            polymerizationDTO.setObjLat(basicFacilityDTO.getLat());
            polymerizationDTO.setObjLng(basicFacilityDTO.getLng());
            polymerizationDTO.setCategoryName(basicFacilityDTO.getFacilityTypeName());
            polymerizationDTO.setStatus(basicFacilityDTO.getFacilityStatus());
            polymerizationDTO.setStatusName(basicFacilityDTO.getFacilityStatusName());
            polymerizationDTO.setWorkStatus(Integer.valueOf(basicFacilityDTO.getWorkStatus()));
            polymerizationDTO.setWorkStatusName(basicFacilityDTO.getWorkStatusName());
            polymerizationDTO.setRegionId(Long.valueOf(basicFacilityDTO.getRegionId()));
            polymerizationDTO.setRegionName(basicFacilityDTO.getFacilityRegionName());
            if (FacilityConstant.AshcanWorkStatus.NORMAL.equals(basicFacilityDTO.getWorkStatus())) {
                polymerizationDTO.setObjIcon(websocketTaskService.getOssObjLink(WebSocketConsts.PolymerizationIcon.ASHCAN_NORMAL));
            } else {
                polymerizationDTO.setObjIcon(websocketTaskService.getOssObjLink(WebSocketConsts.PolymerizationIcon.ASHCAN_ALARM));
            }
            return polymerizationDTO;
        }).collect(Collectors.toList()).stream().filter(polymerizationDTO -> polymerizationDTO != null && StringUtil.isNotBlank(polymerizationDTO.getObjLat()) && StringUtil.isNotBlank(polymerizationDTO.getObjLng())).collect(Collectors.toList());
    }

    /**
     * 根据条件查询事件
     *
     * @return
     */
    @Override
    public List<PolymerizationDTO> getEventPolymerization(PolymerizationConditionDTO conditionDTO, String tenantId) {
        if (CollectionUtil.isEmpty(conditionDTO.getEventLevels()) && CollectionUtil.isEmpty(conditionDTO.getEventStatuses())) {
            return null;
        }
        EventQueryDTO eventQueryDTO = new EventQueryDTO();
        // 分租户
        eventQueryDTO.setTenantId(tenantId);
        List<Long> eventLevels = conditionDTO.getEventLevels();
        if (CollUtil.isNotEmpty(eventLevels)) {
            eventQueryDTO.setEventLevels(eventLevels);
        }
        List<Long> eventStatuses = conditionDTO.getEventStatuses();
        if (CollUtil.isNotEmpty(eventStatuses)) {
            eventQueryDTO.setEventStatuses(eventStatuses);
        }
        if (StringUtil.isNotBlank(conditionDTO.getKeyWord())) {
            eventQueryDTO.setEventTypeName(conditionDTO.getKeyWord());
        }

        // 综合监控只查当天的事件
        Calendar start=Calendar.getInstance();
        start.set(Calendar.HOUR,0);
        start.set(Calendar.MINUTE,0);
        start.set(Calendar.SECOND,0);
        eventQueryDTO.setStartTime(start.getTime().getTime());

        Calendar end=Calendar.getInstance();
        end.set(Calendar.HOUR,23);
        end.set(Calendar.MINUTE,59);
        end.set(Calendar.SECOND,59);
        eventQueryDTO.setEndTime(end.getTime().getTime());

        List<EventInfoVO> eventInfoVOList = eventInfoClient.listEventInfoByCondition(eventQueryDTO).getData();
        if (CollectionUtil.isEmpty(eventInfoVOList)) {
            return null;
        }
        return eventInfoVOList.stream().map(eventInfoVO -> {
            PolymerizationDTO polymerizationDTO = new PolymerizationDTO();
            polymerizationDTO.setObjID(eventInfoVO.getId().toString());
            polymerizationDTO.setObjName(eventInfoVO.getEventTypeName());
            polymerizationDTO.setObjType(WebSocketConsts.PolymerizationType.EVENT);
            polymerizationDTO.setObjLat(eventInfoVO.getLatitudinal());
            polymerizationDTO.setObjLng(eventInfoVO.getLongitude());
            polymerizationDTO.setObjIcon(websocketTaskService.getOssObjLink(WebSocketConsts.PolymerizationIcon.EVENT));
            polymerizationDTO.setCategoryName(eventInfoVO.getEventTypeName());
            polymerizationDTO.setStatus(eventInfoVO.getStatus());
            polymerizationDTO.setStatusName(eventInfoVO.getStatusName());
            polymerizationDTO.setRegionId(eventInfoVO.getBelongArea());
            polymerizationDTO.setRegionName(eventInfoVO.getBelongAreaName());
            polymerizationDTO.setAreaManageName(eventInfoVO.getWorkAreaManageName());
            return polymerizationDTO;
        }).collect(Collectors.toList()).stream().filter(polymerizationDTO -> polymerizationDTO != null && StringUtil.isNotBlank(polymerizationDTO.getObjLat()) && StringUtil.isNotBlank(polymerizationDTO.getObjLng())).collect(Collectors.toList());

    }

    @Override
    public List<PolymerizationDTO> getToiletPolymerization(PolymerizationConditionDTO conditionDTO, String tenantId) {
        if (CollectionUtil.isEmpty(conditionDTO.getWcLevel()) && CollectionUtil.isEmpty(conditionDTO.getWcState())) {
            return null;
        }
        ToiletQueryDTO queryDTO = new ToiletQueryDTO();
        queryDTO.setTenantId(tenantId);
        List<Long> levels = conditionDTO.getWcLevel();
        if (CollectionUtil.isNotEmpty(levels)) {
            queryDTO.setLevels(levels);
        }
        List<Long> statuses = conditionDTO.getWcState();
        if (CollectionUtil.isNotEmpty(statuses)) {
            queryDTO.setStatuses(statuses);
        }
        List<Long> regionIds = conditionDTO.getRegionIds();
        if (CollectionUtil.isNotEmpty(regionIds)) {
            List<Long> allSubRegionByRegionIds = getAllSubRegionByRegionIds(regionIds);
            queryDTO.setRegionIds(allSubRegionByRegionIds);
        }
        List<ToiletInfoVO> toiletInfoVOList = toiletClient.listToiletVOByCondition(queryDTO).getData();
        if (CollectionUtil.isEmpty(toiletInfoVOList)) {
            return null;
        }
        return toiletInfoVOList.stream().map(toiletInfoVO -> {
            PolymerizationDTO polymerizationDTO = new PolymerizationDTO();
            polymerizationDTO.setObjID(toiletInfoVO.getId().toString());
            polymerizationDTO.setObjName(toiletInfoVO.getToiletName());
            polymerizationDTO.setObjType(WebSocketConsts.PolymerizationType.PUBLIC_TOILET);
            polymerizationDTO.setObjLat(toiletInfoVO.getLat());
            polymerizationDTO.setObjLng(toiletInfoVO.getLng());
            polymerizationDTO.setCategoryName(toiletInfoVO.getToiletLevelName());
            polymerizationDTO.setWorkStatus(StringUtil.isNotBlank(toiletInfoVO.getWorkStatus()) ? Integer.valueOf(toiletInfoVO.getWorkStatus()) : null);
            polymerizationDTO.setWorkStatusName(toiletInfoVO.getWorkStatusName());
            if (toiletInfoVO.getWorkStatus().equals(FacilityConstant.ToiletWorkStatus.NORMAL)) {
                polymerizationDTO.setObjIcon(websocketTaskService.getOssObjLink(WebSocketConsts.PolymerizationIcon.WC_NORMAL));
            } else {
                polymerizationDTO.setObjIcon(websocketTaskService.getOssObjLink(WebSocketConsts.PolymerizationIcon.WC_MAINTENANCE));
            }
            polymerizationDTO.setRegionId(toiletInfoVO.getRegionId());
            polymerizationDTO.setRegionName(toiletInfoVO.getRegionName());
            return polymerizationDTO;
        }).collect(Collectors.toList()).stream().filter(polymerizationDTO -> polymerizationDTO != null && StringUtil.isNotBlank(polymerizationDTO.getObjLat()) && StringUtil.isNotBlank(polymerizationDTO.getObjLng())).collect(Collectors.toList());
    }

    /**
     * 查询人员弹框详情
     *
     * @return
     */
    @Override
    public BasicPolymerizationDetailDTO getPersonPolymerizationDetail(Long entityId) {
        PersonDetailVO personDetailInfo = personService.getPersonDetailInfo(entityId.toString());
        PersonPolymerizationDetailDTO personPolymerizationDetailDTO = new PersonPolymerizationDetailDTO();

//        personPolymerizationDetailDTO.setAlarmCount(0);
//        personPolymerizationDetailDTO.setAvgSpeed("100");
//        personPolymerizationDetailDTO.setBatteryPercent("80");
//        personPolymerizationDetailDTO.setJobNumber("11111111");
//        personPolymerizationDetailDTO.setStationId("11111");
//        personPolymerizationDetailDTO.setStationName("清扫员");
//        personPolymerizationDetailDTO.setPersonName("张三");
//        personPolymerizationDetailDTO.setMobile("13000000000");
//        personPolymerizationDetailDTO.setScheduleName("凌晨");
//        personPolymerizationDetailDTO.setTotalDistance("1000");
//        personPolymerizationDetailDTO.setMaxSpeed("70");
//        personPolymerizationDetailDTO.setAvgSpeed("60");
//        personPolymerizationDetailDTO.setSpeed("30");
//        personPolymerizationDetailDTO.setWorkBeginTime(System.currentTimeMillis());
//        personPolymerizationDetailDTO.setTimeOfDuration(1000L);
//        personPolymerizationDetailDTO.setAlarmCount(11);
//        personPolymerizationDetailDTO.setLastAlarmContent("违规告警");
//        personPolymerizationDetailDTO.setDeviceStatus(1L);
//        personPolymerizationDetailDTO.setDeviceStatusName("开启");
//        personPolymerizationDetailDTO.setWorkStatus(1);
//        personPolymerizationDetailDTO.setWorkStatusName("正常");
        BeanUtil.copy(personDetailInfo, personPolymerizationDetailDTO);
        personPolymerizationDetailDTO.setEntityId(entityId.toString());
        personPolymerizationDetailDTO.setEntityType(WebSocketConsts.PolymerizationType.PERSON);


        return personPolymerizationDetailDTO;
    }


    /**
     * 获取车辆详情信息（对应综合监控的弹框）
     *
     * @return
     */
    @Override
    public BasicPolymerizationDetailDTO getVehiclePolymerizationDetail(Long entityId, String tenantId) {
        VehiclePolymerizationDetailDTO detailDTO = new VehiclePolymerizationDetailDTO();
        VehicleDetailVO vehicleDetail = vehicleService.getVehicleDetailRealTime(entityId, tenantId, BaiduMapUtils.CoordsSystem.BD09LL);
        BeanUtil.copy(vehicleDetail, detailDTO);
        //获取车辆大类
        detailDTO.setKindCode(vehicleDetail.getKindCode() == null ? null : vehicleDetail.getKindCode().toString());
        return detailDTO;
    }


    /**
     * 根据条件查询中转站
     *
     * @return
     */
    @Override
    public BasicPolymerizationDetailDTO getTransferStationPolymerizationDetail(Long entityId) {
        TransferStationPolyDetailDTO detailDTO = new TransferStationPolyDetailDTO();
        FacilityInfoExtVO facilityInfoExtVO = facilityClient.getFacilityDetailById(entityId).getData();
        if (facilityInfoExtVO == null) {
            return null;
        }
        BeanUtil.copy(facilityInfoExtVO, detailDTO);
        return detailDTO;
    }

    /**
     * 根据条件查询垃圾桶
     *
     * @return
     */
    @Override
    public BasicPolymerizationDetailDTO getAshcanPolymerizationDetail(Long entityId) {
        AshcanPolyDetailDTO detailDTO = new AshcanPolyDetailDTO();
        AshcanInfoVO ashcanInfoVO = ashcanClient.getAshcanVO(entityId).getData();
        if (ashcanInfoVO == null) {
            return null;
        }
        BeanUtil.copy(ashcanInfoVO, detailDTO);
        return detailDTO;
    }

    /**
     * 根据条件查询事件
     *
     * @return
     */
    @Override
    public BasicPolymerizationDetailDTO getEventPolymerizationDetail(Long entityId) {
        EventPolyDetailDTO detailDTO = new EventPolyDetailDTO();
        EventInfoVO eventInfoVO = eventInfoClient.getEventDetailById(entityId).getData();
        if (eventInfoVO == null) {
            return null;
        }
        BeanUtil.copy(eventInfoVO, detailDTO);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String createDate = simpleDateFormat.format(new Date(eventInfoVO.getCreateTime().getTime()));
        detailDTO.setStartTime(createDate);
        detailDTO.setWorkAreaManageName(eventInfoVO.getHandlePersonName());
        detailDTO.setLocation(eventInfoVO.getEventAddress());
        return detailDTO;
    }

    /**
     * 根据条件查询公厕
     *
     * @return
     */
    @Override
    public BasicPolymerizationDetailDTO getToiletPolymerizationDetail(Long entityId) {
        ToiletPolyDetailDTO detailDTO = new ToiletPolyDetailDTO();
        ToiletInfoVO toiletInfoVO = toiletClient.getToilet(entityId).getData();
        if (toiletInfoVO == null) {
            return null;
        }
        BeanUtil.copy(toiletInfoVO, detailDTO);
        int totalCount = detailDTO.getManAQuotaCount() + detailDTO.getManBQuotaCount() + detailDTO.getWomanBQuotaCount() + detailDTO.getMomQuotaCount() + detailDTO.getBarrierFreeQuotaCount();
        detailDTO.setTotalQuotaCount(totalCount);
        return detailDTO;
    }


    /**
     * 获取区域以及下面所有的子区域ID集合
     *
     * @param parentRegionIds
     * @return
     */
    private List<Long> getAllSubRegionByRegionIds(List<Long> parentRegionIds) {

        List<Long> allData = new ArrayList<>();
        allData.addAll(parentRegionIds);
        for (Long regionId : parentRegionIds) {
            R<BusiRegionTreeVO> busiRegionTreeVOR = regionClient.queryChildBusiRegionList(regionId);
            if (busiRegionTreeVOR != null && busiRegionTreeVOR.getData() != null && CollectionUtil.isNotEmpty(busiRegionTreeVOR.getData().getChildBusiRegionVOList())) {
                List<Long> collect = busiRegionTreeVOR.getData().getChildBusiRegionVOList().stream().map(busiRegionVO -> busiRegionVO.getRegion().getId()).collect(Collectors.toList());
                allData.addAll(collect);
            }
        }

        return allData;
    }


}
