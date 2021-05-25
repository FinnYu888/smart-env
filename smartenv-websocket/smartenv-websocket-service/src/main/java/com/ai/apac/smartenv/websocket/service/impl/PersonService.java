package com.ai.apac.smartenv.websocket.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.enums.DeviceStatusEnum;
import com.ai.apac.smartenv.common.enums.PersonStatusImgEnum;
import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import com.ai.apac.smartenv.omnic.entity.OmnicPersonInfo;
import com.ai.apac.smartenv.omnic.entity.PicStatus;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.omnic.feign.IRealTimeStatusClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.entity.Company;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.entity.Station;
import com.ai.apac.smartenv.system.feign.ICompanyClient;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.ai.apac.smartenv.system.feign.IRegionClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.vo.BusiRegionTreeVO;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.websocket.common.GetPersonPositionDTO;
import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonDetailVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonInfoVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonMonitorInfoVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonMonitorVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorInfoVO;
import com.ai.apac.smartenv.websocket.service.IBaseService;
import com.ai.apac.smartenv.websocket.service.IPersonService;
import com.ai.apac.smartenv.websocket.task.*;
import com.ai.apac.smartenv.websocket.wrapper.PersonInfoWrapper;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import sun.misc.GC;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ai.apac.smartenv.websocket.controller.PersonController.GET_PERSON_TRACK_REALTIME;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/24 4:14 下午
 **/
@Service
@Slf4j
@AllArgsConstructor
public class PersonService implements IPersonService {

    private WebSocketTaskService websocketTaskService;

    private IRealTimeStatusClient realTimeStatusClient;

    private DeviceService deviceService;

    private AlarmService alarmService;

    private IScheduleClient scheduleClient;

    private IPersonClient personClient;

    private IWorkareaRelClient workareaRelClient;

    private ISysClient sysClient;

    private IWorkareaClient workareaClient;

    private MongoTemplate mongoTemplate;

    private IBaseService baseService;

    private CoordsTypeConvertUtil coordsTypeConvertUtil;

    private IRegionClient regionClient;

    private IProjectClient projectClient;

    private ICompanyClient companyClient;
    /**
     * 向客户端推送人员状态统计信息
     *
     * @param websocketTask
     */
    @Override
    public void pushPersonStatus(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        websocketTaskService.deleteSameTask(websocketTask.getSessionId(), websocketTask.getTenantId(), websocketTask.getTaskType());
        websocketTaskService.createTask(websocketTask);
        PersonStatusTask task = new PersonStatusTask(websocketTask);
        ThreadUtil.execute(task);
//        task.run();
    }

    /**
     * 向客户端实时推送人员位置信息
     *
     * @param websocketTask
     */
    @Override
    public void pushPersonPosition(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        websocketTaskService.deleteSameTask(websocketTask.getSessionId(), websocketTask.getTenantId(), websocketTask.getTaskType());
        websocketTaskService.createTask(websocketTask);
//
        PersonPositionTask task = new PersonPositionTask(websocketTask);
//        Executor taskExecutor = SpringUtil.getBean("taskExecutor", Executor.class);
//        taskExecutor.execute(task);
        task.run();


    }


    /**
     * 向客户端实时推送当前人员的详细信息
     *
     * @param websocketTask
     */
    @Override
    public void pushPersonDetail(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        websocketTaskService.deleteSameTask(websocketTask.getSessionId(), websocketTask.getTenantId(), websocketTask.getTaskType());
        websocketTaskService.createTask(websocketTask);

        PersonDetailTask task = new PersonDetailTask(websocketTask);
        baseService.getTaskExecutor().execute(task);
//        task.run();
    }

    /**
     * 向客户端实时推送当前人员的运行轨迹
     *
     * @param websocketTask
     */
    @Override
    public void pushPersonTrackRealTime(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        websocketTaskService.deleteSameTask(websocketTask.getSessionId(), websocketTask.getTenantId(), websocketTask.getTaskType());
        websocketTaskService.createTask(websocketTask);

        PersonTrackRealTimeTask task = new PersonTrackRealTimeTask(websocketTask);
        ThreadUtil.execute(task);
//        task.run();
    }

    /**
     * 根据租户获取当前人员状态
     *
     * @param tenantId
     * @return
     */
    @Override
    public Future<StatusCount> getStatusCount(String tenantId) {
        R<StatusCount> statusCountResult = realTimeStatusClient.getAllPersonStatusCount(tenantId);
        if (statusCountResult.isSuccess() && statusCountResult.getData() != null) {
            return new AsyncResult<StatusCount>(statusCountResult.getData());
        }
        return null;
    }

    /**
     * 根据状态查询对应的人员信息
     *
     * @param status
     * @param tenantId
     * @return
     */
    @Override
    public Future<List<OmnicPersonInfo>> getPersonByStatus(Integer status, String tenantId) {
        R<List<OmnicPersonInfo>> dataResult = realTimeStatusClient.getPersonByStatus(status, tenantId);
        if (dataResult.isSuccess() && dataResult.getData() != null) {
            return new AsyncResult<List<OmnicPersonInfo>>(dataResult.getData());
        }
        return null;
    }

    /**
     * 根据工作区域ID获取人员信息
     *
     * @param tenantId
     * @return
     */
    @Override
    public Future<List<String>> getPersonByWorkareaIdsAndStatus(String tenantId) {

        Query query = Query.query(Criteria.where("tenantId").is(tenantId).and("watchDeviceCode").ne(null).and("workStatus").ne(PersonConstant.PersonStatus.VACATION));
        List<BasicPersonDTO> basicPersonDTOS = mongoTemplate.find(query, BasicPersonDTO.class);
        List<String> collect = basicPersonDTOS.stream().map(basicPersonDTO -> basicPersonDTO.getId().toString()).collect(Collectors.toList());

        return new AsyncResult<List<String>>(collect);
    }

    /**
     * 根据工作区域ID获取人员信息
     *
     * @param tenantIds
     * @return
     */
    @Override
    public Future<List<String>> getPersonEasyVList(List<String> tenantIds) {

        Query query = Query.query(Criteria.where("tenantId").in(tenantIds).and("watchDeviceCode").ne(null).and("deviceStatus").is(Long.parseLong(DeviceConstant.DeviceStatus.ON)));
        List<BasicPersonDTO> basicPersonDTOS = mongoTemplate.find(query, BasicPersonDTO.class);
        List<String> collect = basicPersonDTOS.stream().map(basicPersonDTO -> basicPersonDTO.getId().toString()).collect(Collectors.toList());

        return new AsyncResult<List<String>>(collect);
    }


    /**
     * 批量从Mongo中获取人员监控信息
     *
     * @param personIdList
     * @param coordsSystem
     * @return
     */
    @Override
    public List<PersonMonitorInfoVO> getPersonMonitorInfo(List<String> personIdList, BaiduMapUtils.CoordsSystem coordsSystem) {

        List<Long> collect1 = personIdList.stream().map(Long::parseLong).collect(Collectors.toList());
        Query query = Query.query(Criteria.where("id").in(collect1).and("lat").ne(null).and("lng").ne(null));
        List<BasicPersonDTO> basicPersonDTOS = mongoTemplate.find(query, BasicPersonDTO.class);
        List<PersonMonitorInfoVO> personMonitorInfoVOS = basicPersonDTOS.stream().map(basicPersonDTO -> {
            PersonMonitorInfoVO personMonitorInfoVO = new PersonMonitorInfoVO();
            PositionDTO positionDTO = null;
            positionDTO = new PositionDTO();
            positionDTO.setLat(basicPersonDTO.getLat());
            positionDTO.setLng(basicPersonDTO.getLng());
            positionDTO.setTimestamp(System.currentTimeMillis());

            List<PositionDTO> list = new ArrayList();
            list.add(positionDTO);
            coordsTypeConvertUtil.objectCoordsConvert(BaiduMapUtils.CoordsSystem.GC02, coordsSystem, list);

            positionDTO.setTimestamp(System.currentTimeMillis());
            PersonInfoVO personInfoVO = BeanUtil.copy(basicPersonDTO, PersonInfoVO.class);
            personInfoVO.setDeviceId(basicPersonDTO.getWechatId());
            personInfoVO.setDeviceCode(basicPersonDTO.getWatchDeviceCode());
            personInfoVO.setStatus(basicPersonDTO.getWorkStatus());
            personInfoVO.setStatusName(basicPersonDTO.getWorkStatusName());
            personInfoVO.setIcon(PersonCache.getPersonStatusImg(basicPersonDTO.getWorkStatus()));
            personInfoVO.setPersonId(basicPersonDTO.getId().toString());
            personMonitorInfoVO.setPosition(positionDTO);
            personMonitorInfoVO.setPersonInfo(personInfoVO);
            return personMonitorInfoVO;
        }).filter(personMonitorInfoVO -> personMonitorInfoVO != null).collect(Collectors.toList());
        return personMonitorInfoVOS;
    }

    /**
     * 根据人员ID获取人员监控信息
     *
     * @param personId
     * @param coordsSystem
     * @return
     */
    @Override
    public PersonMonitorInfoVO getPersonMonitorInfo(String personId, BaiduMapUtils.CoordsSystem coordsSystem) {
        try {


            Query query = Query.query(Criteria.where("id").is(Long.valueOf(personId)));
            BasicPersonDTO basicPersonDTO = mongoTemplate.findOne(query, BasicPersonDTO.class);
            //根据车辆ID从大数据侧获取实时位置
            String deviceCode = basicPersonDTO.getWatchDeviceCode();
            DeviceInfo deviceInfo = null;
            Future<DeviceInfo> deviceInfoResult = deviceService.getDeviceByPerson(personId);
            if (deviceInfoResult != null && deviceInfoResult.get() != null) {
                deviceInfo = deviceInfoResult.get();
                deviceCode = deviceInfo.getDeviceCode();
            }
            PositionDTO positionDTO = null;
            //调用大数据接口获取位置
            Future<PositionDTO> positionResult = deviceService.getDevicePosition(deviceCode, coordsSystem);
            if (positionResult != null && positionResult.get() != null) {
                positionDTO = positionResult.get();
                positionDTO.setTimestamp(TimeUtil.getTimestamp(positionDTO.getTime(), TimeUtil.YYYYMMDDHHMMSS));
            } else {
                return null;
            }


            PicStatus picStatus = new PicStatus();
            picStatus.setEntityId(personId);
            picStatus.setPicStatus(VehicleStatusEnum.OFF_LINE.getValue());
            if (deviceInfo != null && deviceInfo.getId() != null) {
                Boolean isNeedWork = scheduleClient.checkNeedWork(Long.parseLong(personId), "2", new Date()).getData();
                VehicleStatusEnum status = null;
                if (!isNeedWork) {
                    status = VehicleStatusEnum.OFF_LINE;
                } else if (isNeedWork && deviceInfo.getDeviceStatus().equals(0L)) {
                    status = VehicleStatusEnum.ON_LINE;
                } else {
                    status = VehicleStatusEnum.OFFLINE_ALARM;
                }
                picStatus.setPicStatus(status.getValue());
            }


            //从缓存中获取人员信息
            Person person = PersonCache.getPersonById(deviceInfo.getTenantId(), Long.valueOf(personId));
            PersonInfoVO personInfo = PersonInfoWrapper.build().entityVO(person, deviceInfo, picStatus);
            PersonMonitorInfoVO personMonitorInfoVO = new PersonMonitorInfoVO();
            personMonitorInfoVO.setPosition(positionDTO);
            personMonitorInfoVO.setPersonInfo(personInfo);
            List<PositionDTO> coords = new ArrayList<>();
            coords.add(positionDTO);
            List<PositionDTO> positionDTOS = coordsTypeConvertUtil.objectCoordsConvert(coordsSystem, BaiduMapUtils.CoordsSystem.GC02, coords);
            Update update = Update.update("lat", positionDTOS.get(0).getLat()).set("lng", positionDTOS.get(0).getLng());
            mongoTemplate.upsert(Query.query(Criteria.where("id").is(basicPersonDTO.getId())), update, BasicPersonDTO.class);
            return personMonitorInfoVO;
//            }

        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }

    }

    /**
     * 根据人员ID实时获取最新信息  对应客户端的弹窗
     *
     * @param personId
     * @return
     */
    @Override
    public PersonDetailVO getPersonDetailRealTime(String personId, String tenantId) {
        PersonDetailVO personDetailVO = new PersonDetailVO();
        try {
            //获取人员信息
            Person person = PersonCache.getPersonById(tenantId, Long.valueOf(personId));
            if (person == null || person.getId() == null) {
                log.error("没有符合条件的人员");
                return null;
            } else {
                personDetailVO.setJobNumber(person.getJobNumber());
                personDetailVO.setPersonName(person.getPersonName());
                if (!ObjectUtils.isEmpty(person.getPersonPositionId())) {
                    Long personPositionId = person.getPersonPositionId();
                    personDetailVO.setStationId(personPositionId.toString());
                    Station station = sysClient.getStationById(personPositionId).getData();
                    if (!ObjectUtils.isEmpty(station)) {
                        personDetailVO.setStationName(station.getStationName());
                    }
                }

                String mobile = person.getMobileNumber() == null ? "" : String.valueOf(person.getMobileNumber());
                personDetailVO.setMobile(mobile);
            }

            //获取设备开关信息
            String deviceCode = null;
            Future<DeviceInfo> deviceInfoResult = deviceService.getDeviceByPerson(personId);
            if (deviceInfoResult != null && deviceInfoResult.get() != null) {
                deviceCode = deviceInfoResult.get().getDeviceCode();
                personDetailVO.setDeviceStatus(deviceInfoResult.get().getDeviceStatus());
                personDetailVO.setDeviceStatusName(DeviceStatusEnum.getDescByValue(personDetailVO.getDeviceStatus()));
            } else {
                personDetailVO.setDeviceStatus(null);
                personDetailVO.setDeviceStatusName(null);
            }
//            deviceCode = "SS000001";

            //获取行驶总里程、平均速度、最高速度
            Future<TrackPositionDto.Statistics> lastDeviceRunInfoResult = deviceService.getLastDeviceRunInfo(deviceCode);
            String maxSpeed = "0";
            String avgSpeed = "0";
            String totalDistance = "0";
            if (lastDeviceRunInfoResult != null && lastDeviceRunInfoResult.get() != null) {
                TrackPositionDto.Statistics statistics = lastDeviceRunInfoResult.get();
                if (statistics != null) {
                    maxSpeed = statistics.getMaxSpeed() == null ? "0" : statistics.getMaxSpeed();
                    avgSpeed = statistics.getAvgSpeed() == null ? "0" : statistics.getAvgSpeed();
                    totalDistance = statistics.getTotalDistance() == null ? "0" : statistics.getTotalDistance();
                }
            }
            personDetailVO.setMaxSpeed(new BigDecimal(maxSpeed).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
            personDetailVO.setAvgSpeed(new BigDecimal(avgSpeed).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());


            BigDecimal bigDecimal = new BigDecimal(totalDistance);
            bigDecimal.setScale(2, BigDecimal.ROUND_HALF_DOWN);
//                BigDecimal multiple=new BigDecimal(1000);
//                BigDecimal divide = bigDecimal.divide(multiple);
            personDetailVO.setTotalDistance(bigDecimal.toString());

//            personDetailVO.setTotalDistance(totalDistance);

            //获取人员出勤状态

            BasicPersonDTO one = mongoTemplate.findOne(Query.query(Criteria.where("id").is(person.getId())), BasicPersonDTO.class);
            if (one != null) {
                personDetailVO.setWorkStatus(one.getWorkStatus());
                personDetailVO.setWorkStatusName(one.getWorkStatusName());
                personDetailVO.setBatteryPercent(one.getWatchBattery());
            } else {
                personDetailVO.setWorkStatus(VehicleStatusEnum.VACATION.getValue());
                personDetailVO.setWorkStatusName(VehicleStatusEnum.VACATION.getDesc());
            }

            //获取告警信息
            Future<List<AlarmInfoHandleInfoVO>> alarmResult = alarmService.getTodayAlarmByPerson(Long.valueOf(personId));
            if (alarmResult != null && alarmResult.get() != null) {
                List<AlarmInfoHandleInfoVO> alarmList = alarmResult.get();
                personDetailVO.setAlarmCount(alarmList.size());
                personDetailVO.setLastAlarmContent(alarmList.get(0).getAlarmMessage());
            } else {
                personDetailVO.setAlarmCount(0);
                personDetailVO.setLastAlarmContent(null);
            }
            // 设置项目ID和公司ID
            try {
                R<Project> projectByTenantIdR = projectClient.getProjectByTenantId(person.getTenantId());
                if (!ObjectUtils.isEmpty(projectByTenantIdR) && !ObjectUtils.isEmpty(projectByTenantIdR.getData())) {
                    personDetailVO.setProjectId(projectByTenantIdR.getData().getId() == null ? null : projectByTenantIdR.getData().getId().toString());
                    personDetailVO.setProjectName(projectByTenantIdR.getData().getProjectName());

                    R<Company> companyByIdR = companyClient.getCompanyById(projectByTenantIdR.getData().getCompanyId());
                    if (!ObjectUtils.isEmpty(companyByIdR)&&!ObjectUtils.isEmpty(companyByIdR.getData())){
                        personDetailVO.setCompanyId(projectByTenantIdR.getData().getCompanyId()==null?null:projectByTenantIdR.getData().getCompanyId().toString());
                        personDetailVO.setCompanyName(companyByIdR.getData().getFullName());
                    }


                }
            } catch (Exception e) {
                log.info("无法设置公司ID和项目ID");
            }

            return personDetailVO;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ServiceException(ResultCode.FAILURE, ex);
        }

    }

    @Override
    public PersonDetailVO getPersonStatusRealTime(String personId, String tenantId) {
        PersonDetailVO personDetailVO = new PersonDetailVO();
        try {
            //获取人员信息
            Person person = PersonCache.getPersonById(tenantId, Long.valueOf(personId));
            if (person == null || person.getId() == null) {
                log.error("没有符合条件的人员");
                return null;
            } else {
                personDetailVO.setJobNumber(person.getJobNumber());
                personDetailVO.setPersonName(person.getPersonName());
                if (!ObjectUtils.isEmpty(person.getPersonPositionId())) {
                    Long personPositionId = person.getPersonPositionId();
                    personDetailVO.setStationId(personPositionId.toString());
                    Station station = sysClient.getStationById(personPositionId).getData();
                    if (!ObjectUtils.isEmpty(station)) {
                        personDetailVO.setStationName(station.getStationName());
                    }
                }

                String mobile = person.getMobileNumber() == null ? "" : String.valueOf(person.getMobileNumber());
                personDetailVO.setMobile(mobile);
            }

            //获取设备开关信息
            String deviceCode = null;
            Future<DeviceInfo> deviceInfoResult = deviceService.getDeviceByPerson(personId);
            if (deviceInfoResult != null && deviceInfoResult.get() != null) {
                deviceCode = deviceInfoResult.get().getDeviceCode();
                personDetailVO.setDeviceStatus(deviceInfoResult.get().getDeviceStatus());
                personDetailVO.setDeviceStatusName(DeviceStatusEnum.getDescByValue(personDetailVO.getDeviceStatus()));
            } else {
                personDetailVO.setDeviceStatus(null);
                personDetailVO.setDeviceStatusName(null);
            }

            Boolean isNeedWork = scheduleClient.checkNeedWork(person.getId(), "2", new Date()).getData();
            VehicleStatusEnum status = null;
            if (!isNeedWork) {
                status = VehicleStatusEnum.OFF_LINE;
            } else {
                if (deviceCode != null) {
                    if (isNeedWork && personDetailVO.getDeviceStatus().equals(0L)) {
                        status = VehicleStatusEnum.ON_LINE;
                    } else {
                        status = VehicleStatusEnum.OFFLINE_ALARM;
                    }
                } else {
                    //人员未带手表，默认静值
                    status = VehicleStatusEnum.OFFLINE_ALARM;
                }
            }
            personDetailVO.setWorkStatus(status.getValue());
            personDetailVO.setWorkStatusName(status.getDesc());

            return personDetailVO;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
    }

    /**
     * 获取人员最新位置信息
     *
     * @param personId
     * @param deviceInfo
     * @param trackPosition
     * @param tenantId
     * @return
     */
    @Override
    public PersonMonitorVO getPersonLastInfo(String personId, DeviceInfo deviceInfo, List<PositionDTO> trackPosition, String tenantId) {
        PersonMonitorVO personMonitorVO = new PersonMonitorVO();
        PersonMonitorInfoVO personMonitorInfoVO = new PersonMonitorInfoVO();

        //获取人员信息
        Person person = PersonCache.getPersonById(tenantId, Long.valueOf(personId));
        if (person == null || person.getId() == null) {
            log.error("没有符合条件的人员");
            return null;
        }

        //获取人员出勤状态
//        R<PicStatus> picStatus = realTimeStatusClient.getPicStatusByPersonId(personId);
        PicStatus picStatus = new PicStatus();
        picStatus.setEntityId(personId);
        picStatus.setPicStatus(VehicleStatusEnum.OFF_LINE.getValue());
        if (deviceInfo != null && deviceInfo.getId() != null) {
            Boolean isNeedWork = scheduleClient.checkNeedWork(person.getId(), "2", new Date()).getData();
            VehicleStatusEnum status = null;
            if (!isNeedWork) {
                status = VehicleStatusEnum.OFF_LINE;
            } else if (isNeedWork && deviceInfo.getDeviceStatus().equals(0L)) {
                status = VehicleStatusEnum.ON_LINE;
            } else {
                status = VehicleStatusEnum.OFFLINE_ALARM;
            }
            picStatus.setPicStatus(status.getValue());
        }


        List<PersonMonitorInfoVO> personMonitorInfoVOList = new ArrayList<PersonMonitorInfoVO>();

        PersonInfoVO personInfoVO = PersonInfoWrapper.build().entityVO(person, deviceInfo, picStatus);
        personMonitorInfoVO.setPersonInfo(personInfoVO);
        personMonitorInfoVO.setPosition(trackPosition.get(trackPosition.size() - 1));
        personMonitorInfoVOList.add(personMonitorInfoVO);
        personMonitorVO.setPersonList(personMonitorInfoVOList);

        return personMonitorVO;
    }


    @Override
    public List<Long> getPersonIdsByRegionId(String regionId) {
        if (regionId == null) {
            return null;
        }

//        List<WorkareaInfo> workareaInfoList = workareaClient.getWorkareaInfoByRegion(Long.parseLong(regionId)).getData();
//        if (CollectionUtil.isNotEmpty(workareaInfoList)){
//            List<String> workareaIds = workareaInfoList.stream().map(workareaInfo -> workareaInfo.getId().toString()).collect(Collectors.toList());
//            if (CollectionUtil.isNotEmpty(workareaIds)){
//                List<WorkareaRel> workareaRels = workareaRelClient.getByWorkareaIds(workareaIds, PersonConstant.WORKAREA_REL_PERSON).getData();
//                entityIds = workareaRels.stream().map(WorkareaRel::getEntityId).collect(Collectors.toList());
//            }
//        }
        List<Long> regionIds = new ArrayList<>();
        regionIds.add(Long.parseLong(regionId));
        //获取所有下级区域
        List<Long> allSubRegionByRegionIds = getAllSubRegionByRegionIds(regionIds);
        //查询所有车
        Query query = Query.query(Criteria.where("personBelongRegion").in(getAllSubRegionByRegionIds(allSubRegionByRegionIds)).and("workStatus").ne(7));
        List<BasicPersonDTO> personDTOS = mongoTemplate.find(query, BasicPersonDTO.class);
        if (CollectionUtil.isEmpty(personDTOS)) {
            return null;
        }

        List<Long> entityIds = personDTOS.stream().map(BasicPersonDTO::getId).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(entityIds)) {
            return null;
        }

        return entityIds;
    }


    @Override
    public R<PersonMonitorVO> getPersonPosition(GetPersonPositionDTO getPersonPositionDTO) {
        Query query = new Query();
        //区域ID需要加载下级所有区域ID
        if (StringUtil.isNotBlank(getPersonPositionDTO.getRegionId())) {
            List<Long> regionIds = new ArrayList<>();
            regionIds.add(Long.parseLong(getPersonPositionDTO.getRegionId()));
            query.addCriteria(Criteria.where("personBelongRegion").in(getAllSubRegionByRegionIds(regionIds)));
        }
        if (StringUtil.isNotBlank(getPersonPositionDTO.getPersonName())) {
            query.addCriteria(Criteria.where("personName").regex("^.*" + getPersonPositionDTO.getPersonName() + ".*$"));
        }
        query.addCriteria(Criteria.where("tenantId").is(AuthUtil.getTenantId()));

        List<BasicPersonDTO> basicPersonDTOS = mongoTemplate.find(query, BasicPersonDTO.class);
        if (CollectionUtil.isEmpty(basicPersonDTOS)) {
            return null;
        }
        String vehicleIds = basicPersonDTOS.stream().map(BasicPersonDTO::getId).map(String::valueOf).collect(Collectors.joining(","));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("personIds", vehicleIds);
        params.put("coordsSystem", BaiduMapUtils.CoordsSystem.GC02.value);
        WebsocketTask websocketTask = new WebsocketTask();
        websocketTask.setParams(params);
        websocketTask.setTaskType(GET_PERSON_TRACK_REALTIME);
        websocketTask.setTopic(WebSocketConsts.PUSH_PERSON_MONITOR);

        PersonPositionTask personPositionTask = new PersonPositionTask(websocketTask);

        R<PersonMonitorVO> execute = personPositionTask.execute();
        return execute;

    }


    @Override
    public PersonDetailVO getPersonDetailInfo(String personId) {
        PersonDetailVO personDetailRealTime = getPersonDetailRealTime(personId, AuthUtil.getTenantId());
        try {
            Future<List<AlarmInfoHandleInfoVO>> alarmResult = alarmService.getTodayAlarmByPerson(Long.valueOf(personId));
            if (alarmResult != null && alarmResult.get() != null) {
                List<AlarmInfoHandleInfoVO> alarmList = alarmResult.get();
                personDetailRealTime.setAlarmCount(alarmList.size());
                personDetailRealTime.setLastAlarmContent(alarmList.get(0).getAlarmMessage());
            } else {
                personDetailRealTime.setAlarmCount(0);
                personDetailRealTime.setLastAlarmContent(null);
            }

        } catch (Exception e) {
            return null;
        }
        return personDetailRealTime;

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
