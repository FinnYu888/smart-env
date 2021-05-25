package com.ai.apac.smartenv.websocket.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.enums.DeviceStatusEnum;
import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import com.ai.apac.smartenv.omnic.entity.OmnicVehicleInfo;
import com.ai.apac.smartenv.omnic.entity.PicStatus;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.omnic.feign.IRealTimeStatusClient;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonVehicleRelClient;
import com.ai.apac.smartenv.system.cache.CompanyCache;
import com.ai.apac.smartenv.system.cache.ProjectCache;
import com.ai.apac.smartenv.system.entity.Company;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.feign.ICompanyClient;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.ai.apac.smartenv.system.feign.IRegionClient;
import com.ai.apac.smartenv.system.vo.BusiRegionTreeVO;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.websocket.common.GetVehiclePositionDTO;
import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleDetailVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorInfoVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorVO;
import com.ai.apac.smartenv.websocket.service.IBaseService;
import com.ai.apac.smartenv.websocket.service.IDeviceService;
import com.ai.apac.smartenv.websocket.service.IVehicleService;
import com.ai.apac.smartenv.websocket.task.*;
import com.ai.apac.smartenv.websocket.wrapper.VehicleInfoWrapper;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ai.apac.smartenv.websocket.controller.VehicleController.GET_VEHICLE_POSITION;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/17 8:00 上午
 **/
@Slf4j
@Service
@RequestMapping("/vehicle")
@AllArgsConstructor
public class VehicleService implements IVehicleService {

    @Autowired
    private WebSocketTaskService websocketTaskService;


//    @Autowired
//    private IDeviceClient deviceClient;
//
//    @Autowired
//    private IDeviceRelClient deviceRelClient;
//
//    @Autowired
//    private IVehicleClient vehicleClient;

    private IPersonVehicleRelClient personVehicleRelClient;

    private IRealTimeStatusClient realTimeStatusClient;

    private IDeviceService deviceService;

    private AlarmService alarmService;

    private IScheduleClient scheduleClient;

    private IEntityCategoryClient entityCategoryClient;

    private IWorkareaRelClient workareaRelClient;

    private IBaseService baseService;

    private IWorkareaClient workareaClient;

    private IVehicleClient vehicleClient;

    private MongoTemplate mongoTemplate;

    private CoordsTypeConvertUtil coordsTypeConvertUtil;

    private IRegionClient regionClient;


    private IProjectClient projectClient;

    private ICompanyClient companyClient;

    @Override
    public PicStatus getPicStatusByVehicleId(Long vehicleId) {
        return realTimeStatusClient.getPicStatusByVehicleId(vehicleId.toString()).getData();

    }


    /**
     * 向客户端推送车辆状态统计信息
     *
     * @param websocketTask
     */
    @Override
    public void pushVehicleStatus(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        websocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        websocketTaskService.createTask(websocketTask);
        VehicleStatusTask task = new VehicleStatusTask(websocketTask);
        ThreadUtil.execute(task);
//        task.run();
    }

    /**
     * 向客户端实时推送车辆位置信息
     *
     * @param websocketTask
     */
    @Override
    public void pushVehiclePosition(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        websocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        websocketTaskService.createTask(websocketTask);

        //不允许使用线程池了
        VehiclePositionTask task = new VehiclePositionTask(websocketTask);
        task.run();
    }

    /**
     * 根据状态向客户端实时推送当前车辆位置信息
     *
     * @param websocketTask
     */
    @Override
    public void pushVehiclePositionByStatus(WebsocketTask websocketTask) {
//        //删除目前该session下相同类型的任务,重新创建
//        websocketTaskService.deleteSameTask(websocketTask.getUserId(),
//                websocketTask.getSessionId(), websocketTask.getTaskType());
//
//        websocketTask.setSchedule("0/5 * * * * ?");
//        websocketTaskService.createTask(websocketTask);
//
//        StatusVehiclePositionTask task = new StatusVehiclePositionTask(websocketTask);
//        task.run();
    }

    /**
     * 向客户端实时推送当前车辆的详细信息
     *
     * @param websocketTask
     */
    @Override
    public void pushVehicleDetail(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        websocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        websocketTaskService.createTask(websocketTask);


        VehicleDetailTask task = new VehicleDetailTask(websocketTask);
        baseService.getTaskExecutor().execute(task);
//        task.run();
    }

    /**
     * 向客户端实时推送当前车辆的运行轨迹
     *
     * @param websocketTask
     */
    @Override
    public void pushVehicleTrackRealTime(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        websocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        websocketTaskService.createTask(websocketTask);

        VehicleTrackRealTimeTask task = new VehicleTrackRealTimeTask(websocketTask);
        baseService.getTaskExecutor().execute(task);
//        task.run();
    }

    /**
     * 根据车辆ID获取驾驶员
     *
     * @param vehicleId
     * @return
     */
    @Async
    @Override
    public Future<Person> getVehicleDriver(String vehicleId) {
        R<Person> result = personVehicleRelClient.getCurrentDriver(Long.valueOf(vehicleId));
        if (result != null && result.getData() != null) {
            return new AsyncResult<Person>(result.getData());
        }
        return null;
    }

    /**
     * 根据车辆ID获取车辆信息
     *
     * @param vehicleId
     * @return
     */
    @Async
    @Override
    public Future<VehicleInfo> getVehicleById(String vehicleId) {
//    	R<VehicleInfo> vehicleInfoResult = vehicleClient.vehicleInfoById(Long.valueOf(vehicleId));
        VehicleInfo vehicleInfoResult = VehicleCache.getVehicleById(null, Long.valueOf(vehicleId));
//        if (vehicleInfoResult.isSuccess() && vehicleInfoResult.getData() != null) {
        if (vehicleInfoResult != null) {
            return new AsyncResult<VehicleInfo>(vehicleInfoResult);
        }
        return null;
    }

    /**
     * 根据租户获取当前车辆状态
     *
     * @param tenantId
     * @return
     */
    @Async
    @Override
    public Future<StatusCount> getStatusCount(String tenantId) {
        R<StatusCount> statusCountResult = realTimeStatusClient.getAllVehicleStatusCount(tenantId);
        if (statusCountResult.isSuccess() && statusCountResult.getData() != null) {
            return new AsyncResult<StatusCount>(statusCountResult.getData());
        }
        return null;
    }

    /**
     * 根据状态查询对应的车辆信息
     *
     * @param status
     * @param tenantId
     * @return
     */
    @Override
    public Future<List<OmnicVehicleInfo>> getVehicleByStatus(Integer status, String tenantId) {
        R<List<OmnicVehicleInfo>> dataResult = realTimeStatusClient.getVehicleByStatus(status, tenantId);
        if (dataResult.isSuccess() && dataResult.getData() != null) {
            return new AsyncResult<List<OmnicVehicleInfo>>(dataResult.getData());
        }
        return null;
    }


    @Override
    public Future<List<String>> getVehicleByWorkareaIdsAndStatus(String tenantId) {

        Criteria criteria=new Criteria();
        criteria.andOperator(
                Criteria.where("tenantId").is(tenantId),
                Criteria.where("workStatus").ne(VehicleConstant.VehicleStatus.VACATION),
                new Criteria().orOperator(
                        Criteria.where("gpsDeviceCode").ne(null),
                        Criteria.where("nvrDeviceCode").ne(null),
                        Criteria.where("cvrDeviceCode").ne(null)
                )
        );

        List<BasicVehicleInfoDTO> basicVehicleInfoDTOS = mongoTemplate.find(Query.query(criteria), BasicVehicleInfoDTO.class);
        List<String> collect = basicVehicleInfoDTOS.stream().map(basicVehicleInfoDTO -> basicVehicleInfoDTO.getId().toString()).collect(Collectors.toList());

        return new AsyncResult<List<String>>(collect);
    }

    @Override
    public Future<List<String>> getVehicleEasyVList(List<String> tenantIds) {
        Criteria criteria=new Criteria();
        criteria.andOperator(
                Criteria.where("tenantId").in(tenantIds),
                Criteria.where("deviceStatus").is(Long.parseLong(DeviceConstant.DeviceStatus.ON)),
                new Criteria().orOperator(
                        Criteria.where("gpsDeviceCode").ne(null),
                        Criteria.where("nvrDeviceCode").ne(null),
                        Criteria.where("cvrDeviceCode").ne(null)
                )
        );

        List<BasicVehicleInfoDTO> basicVehicleInfoDTOS = mongoTemplate.find(Query.query(criteria), BasicVehicleInfoDTO.class);
        List<String> collect = basicVehicleInfoDTOS.stream().map(basicVehicleInfoDTO -> basicVehicleInfoDTO.getId().toString()).collect(Collectors.toList());

        return new AsyncResult<List<String>>(collect);
    }


    /**
     * 批量从Mongo中获取车辆监控信息
     * @param vehicleIdList
     * @param coordsSystem
     * @return
     */
    @Override
    public List<VehicleMonitorInfoVO> getVehicleMonitorInfo(List<Long> vehicleIdList, BaiduMapUtils.CoordsSystem coordsSystem) {
        Query query = Query.query(Criteria.where("id").in(vehicleIdList).and("lat").ne(null).and("lng").ne(null));
        List<BasicVehicleInfoDTO> basicVehicleInfoDTOS = mongoTemplate.find(query, BasicVehicleInfoDTO.class);
        List<VehicleMonitorInfoVO> vehicleDetailVOList = basicVehicleInfoDTOS.stream().map(basicVehicleInfoDTO -> {
            VehicleMonitorInfoVO vehicleDetailVO = new VehicleMonitorInfoVO();
            VehicleInfoVO vehicleInfoVO = BeanUtil.copy(basicVehicleInfoDTO, VehicleInfoVO.class);
            PositionDTO positionDTO = null;
//            if (basicVehicleInfoDTO.getLat()==null||basicVehicleInfoDTO.getLng()==null){
//                try {
//                    VehicleMonitorInfoVO vehicleMonitorInfo = getVehicleMonitorInfo(basicVehicleInfoDTO.getId().toString(), coordsSystem);
//                    if (vehicleMonitorInfo!=null&&vehicleMonitorInfo.getPosition()!=null){
//                        positionDTO=vehicleMonitorInfo.getPosition();
//                    }else {
//                        return null;
//                    }
//                } catch (Exception e) {
//                    log.warn("获取位置失败："+basicVehicleInfoDTO.getPlateNumber(),e);
//                    return null;
//                }
//            }else {
                positionDTO=new PositionDTO();
                positionDTO.setLat(basicVehicleInfoDTO.getLat());
                positionDTO.setLng(basicVehicleInfoDTO.getLng());
                List<PositionDTO> list=new ArrayList();
                list.add(positionDTO);
                coordsTypeConvertUtil.objectCoordsConvert(BaiduMapUtils.CoordsSystem.GC02,coordsSystem,list);
                positionDTO.setTimestamp(System.currentTimeMillis());
//            }
            vehicleInfoVO.setVehicleId(basicVehicleInfoDTO.getId().toString());
            vehicleInfoVO.setDeviceId(String.valueOf(basicVehicleInfoDTO.getGpsDeviceId()));
            vehicleInfoVO.setDeviceCode(String.valueOf(basicVehicleInfoDTO.getGpsDeviceCode()));
            vehicleInfoVO.setStatus(basicVehicleInfoDTO.getWorkStatus());
            vehicleInfoVO.setStatusName(basicVehicleInfoDTO.getWorkStatusName());
            vehicleInfoVO.setIcon(VehicleCache.getVehicleStatusImg(basicVehicleInfoDTO.getWorkStatus()));
            vehicleDetailVO.setVehicleInfo(vehicleInfoVO);
            vehicleDetailVO.setPosition(positionDTO);
            return vehicleDetailVO;

        }).filter(vehicleMonitorInfoVO -> vehicleMonitorInfoVO!=null).collect(Collectors.toList());
        if (vehicleDetailVOList.size() >= vehicleIdList.size()) {
            return vehicleDetailVOList;
        }
        Map<Long, List<BasicVehicleInfoDTO>> collect = basicVehicleInfoDTOS.stream().collect(Collectors.groupingBy(BasicVehicleInfoDTO::getId));
        Stream<Long> notInStream = vehicleIdList.stream().filter(vehicleId -> !collect.containsKey(vehicleId));
        if (!notInStream.isParallel()){
            return vehicleDetailVOList;
        }
        List<Long> notIn = notInStream.collect(Collectors.toList());
        List<VehicleMonitorInfoVO> notInResult = notIn.stream().map(vehicleId -> {
            try {
                VehicleMonitorInfoVO vehicleMonitorInfo = getVehicleMonitorInfo(vehicleId.toString(), coordsSystem);
                return vehicleMonitorInfo;
            }catch (Exception e){
                return null;
            }
        }).filter(personMonitorInfoVO -> personMonitorInfoVO!=null).collect(Collectors.toList());
        vehicleDetailVOList.addAll(notInResult);
        return vehicleDetailVOList;

    }


    /**
     * 根据车辆ID构建VO信息
     *
     * @param vehicleId
     * @param coordsSystem
     * @return
     */
    @Override
    public VehicleMonitorInfoVO getVehicleMonitorInfo(String vehicleId, BaiduMapUtils.CoordsSystem coordsSystem) {
        try {
            PositionDTO positionDTO = null;

            Query query = Query.query(Criteria.where("id").is(Long.valueOf(vehicleId)));
            BasicVehicleInfoDTO basicVehicleInfoDTO = mongoTemplate.findOne(query, BasicVehicleInfoDTO.class);
            VehicleInfoVO vehicleInfoVO = null;
            VehicleInfo vehicleInfo = null;
            if (basicVehicleInfoDTO!=null&&basicVehicleInfoDTO.getLat() != null && basicVehicleInfoDTO.getLng() != null) {
                vehicleInfoVO = BeanUtil.copy(basicVehicleInfoDTO, VehicleInfoVO.class);
                positionDTO = new PositionDTO();
                positionDTO.setLat(basicVehicleInfoDTO.getLat());
                positionDTO.setLng(basicVehicleInfoDTO.getLng());
                positionDTO.setTimestamp(System.currentTimeMillis());
                vehicleInfoVO.setDeviceId(String.valueOf(basicVehicleInfoDTO.getGpsDeviceId()));
                vehicleInfoVO.setDeviceCode(String.valueOf(basicVehicleInfoDTO.getGpsDeviceCode()));
                vehicleInfoVO.setStatus(basicVehicleInfoDTO.getWorkStatus());
                vehicleInfoVO.setStatusName(basicVehicleInfoDTO.getWorkStatusName());
                vehicleInfoVO.setIcon(VehicleCache.getVehicleStatusImg(basicVehicleInfoDTO.getWorkStatus()));
            } else {
                Long deviceId = null;
                String deviceCode = basicVehicleInfoDTO.getGpsDeviceCode();
                DeviceInfo deviceInfo = null;
                Future<DeviceInfo> deviceInfoResult = deviceService.getPositionDeviceByVehicle(vehicleId);
                if (deviceInfoResult != null && deviceInfoResult.get() != null) {
                    deviceInfo = deviceInfoResult.get();
                    deviceId = deviceInfo.getId();
                    deviceCode = deviceInfo.getDeviceCode();
//                deviceCode = "SS000001";
                }
                //调用大数据接口获取位置
                Future<PositionDTO> positionResult = deviceService.getDevicePosition(deviceCode, coordsSystem);
                if (positionResult != null && positionResult.get() != null) {
                    positionDTO = positionResult.get();
                    positionDTO.setTimestamp(TimeUtil.getTimestamp(positionDTO.getTime(), TimeUtil.YYYYMMDDHHMMSS));
                } else {
                    return null;
                }
                //获取车辆状态
                PicStatus picStatus = null;
                R<PicStatus> picStatusResult = realTimeStatusClient.getPicStatusByVehicleId(vehicleId);
                if (picStatusResult != null && picStatusResult.getData() != null) {
                    picStatus = picStatusResult.getData();
                }

                //从缓存中获取车辆信息
                vehicleInfo = VehicleCache.getVehicleById(deviceInfo.getTenantId(), Long.valueOf(vehicleId));
                vehicleInfoVO = VehicleInfoWrapper.build().entityVO(vehicleInfo, deviceInfo, picStatus);

                basicVehicleInfoDTO.setLat(positionDTO.getLat());
                basicVehicleInfoDTO.setLng(positionDTO.getLng());
                List<PositionDTO> coords=new ArrayList<>();
                coords.add(positionDTO);
                List<PositionDTO> positionDTOS = coordsTypeConvertUtil.objectCoordsConvert(BaiduMapUtils.CoordsSystem.BD09LL, BaiduMapUtils.CoordsSystem.GC02, coords);

                Update update = Update.update("lat", positionDTOS.get(0).getLat()).set("lng", positionDTOS.get(0).getLng());
                mongoTemplate.upsert(Query.query(Criteria.where("id").is(basicVehicleInfoDTO.getId())), update, BasicVehicleInfoDTO.class);
            }
            VehicleMonitorInfoVO vehicleMonitorInfoVO = new VehicleMonitorInfoVO();
            vehicleMonitorInfoVO.setVehicleInfo(vehicleInfoVO);
            vehicleMonitorInfoVO.setPosition(positionDTO);
            return vehicleMonitorInfoVO;
        } catch (Exception ex) {
            log.warn("",ex);
            throw new ServiceException(ex.getMessage());
        }
    }


    /**
     * 根据车辆ID获取实时信息
     *
     * @param vehicleId
     * @param tenantId
     * @param coordsSystem
     * @return
     */
    @Override
    public VehicleDetailVO getVehicleDetailRealTime(Long vehicleId, String tenantId, BaiduMapUtils.CoordsSystem coordsSystem) {
        VehicleDetailVO vehicleDetailVO = new VehicleDetailVO();
        try {
            //根据车辆ID获取车辆信息
            VehicleInfo vehicleInfo = VehicleCache.getVehicleById(tenantId, vehicleId);
            if (vehicleInfo == null || vehicleInfo.getId() == null) {
                log.error("车辆[{}]不存在", vehicleInfo.getId());
                return null;
            }
            vehicleDetailVO.setKindCode(vehicleInfo.getKindCode());
            vehicleDetailVO.setPlateNumber(vehicleInfo.getPlateNumber());
            if (!ObjectUtils.isEmpty(vehicleInfo.getEntityCategoryId())) {
                vehicleDetailVO.setEntityCategoryId(vehicleInfo.getEntityCategoryId());
                vehicleDetailVO.setEntityCategoryName(entityCategoryClient.getCategoryName(vehicleInfo.getEntityCategoryId()).getData());
            }


            //获取车辆驾驶员信息
            Future<Person> person = getVehicleDriver(String.valueOf(vehicleId));
            if (person != null && person.get() != null) {
                vehicleDetailVO.setDriver(person.get().getPersonName());
            } else {
                vehicleDetailVO.setDriver("-");
            }

            //获取车辆设备信息
            Future<DeviceInfo> deviceInfoResult = deviceService.getPositionDeviceByVehicle(String.valueOf(vehicleId));
            DeviceInfo deviceInfo = null;
            String deviceCode = null;
            if (deviceInfoResult != null && deviceInfoResult.get() != null) {
                deviceInfo = deviceInfoResult.get();
                deviceCode = deviceInfo.getDeviceCode();
            } else {
                log.error("车辆[{}]没有绑定传感器,无法获取车辆运行信息", vehicleInfo.getId());
                return null;
            }

            //获取最新的行驶信息
            if (deviceCode != null) {
                String speed = "0";
                Future<PositionDTO> positionResult = deviceService.getDevicePosition(deviceCode, coordsSystem);
                if (positionResult != null && positionResult.get() != null) {
                    speed = positionResult.get().getSpeed();
                }


                vehicleDetailVO.setSpeed(new BigDecimal(speed).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
            }

            String deviceStatus = DeviceCache.getDeviceStatus(vehicleId, VehicleConstant.VEHICLE_ACC_DEVICE_TYPE, tenantId);
            vehicleDetailVO.setDeviceStatus(Long.parseLong(deviceStatus));
            vehicleDetailVO.setDeviceStatusName(DeviceStatusEnum.getDescByValue(vehicleDetailVO.getDeviceStatus()));
//            Boolean isNeedWork = scheduleClient.checkNeedWork(vehicleId, ArrangeConstant.ScheduleObjectEntityType.VEHICLE, new Date()).getData();
//            VehicleStatusEnum status = null;
//            if (isNeedWork != null && isNeedWork) {
//                if (deviceStatus.equals(DeviceConstant.DeviceStatus.ON)) {
//                    //需要出勤且ACC为开
//                    status = VehicleStatusEnum.ON_LINE;
//                } else {
//                    status = VehicleStatusEnum.OFFLINE_ALARM;
//                    //没有绑定ACC、ACC关闭、ACC异常关闭都视为离岗
//                }
//            } else {
//                status = VehicleStatusEnum.OFF_LINE;
//            }

            BasicVehicleInfoDTO one = mongoTemplate.findOne(Query.query(Criteria.where("id").is(vehicleInfo.getId())), BasicVehicleInfoDTO.class);
            if (one!=null){
                vehicleDetailVO.setWorkStatus(one.getWorkStatus());
                vehicleDetailVO.setWorkStatusName(one.getWorkStatusName());
            }else {
                vehicleDetailVO.setWorkStatus(VehicleStatusEnum.VACATION.getValue());
                vehicleDetailVO.setWorkStatusName(VehicleStatusEnum.VACATION.getDesc());
            }



            //获取行驶总里程、平均速度、最高速度
            if (deviceCode != null) {
                Future<TrackPositionDto.Statistics> lastDeviceRunInfoResult = deviceService.getLastDeviceRunInfo(deviceCode);
                String maxSpeed = "0";
                String avgSpeed = "0";
                String totalDistance = "0";
                if (lastDeviceRunInfoResult != null && lastDeviceRunInfoResult.get() != null) {
                    TrackPositionDto.Statistics statistics = lastDeviceRunInfoResult.get();
                    if (statistics != null) {
                        maxSpeed = statistics.getMaxSpeed() == null ? "0" : statistics.getMaxSpeed();
                        avgSpeed = statistics.getAvgSpeed() == null ? "0" : statistics.getAvgSpeed();
                        totalDistance = statistics.getTotalDistance();
                    }
                }
                vehicleDetailVO.setMaxSpeed(new BigDecimal(maxSpeed).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
                vehicleDetailVO.setAvgSpeed(new BigDecimal(avgSpeed).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
//
                BigDecimal bigDecimal = new BigDecimal(totalDistance);
                BigDecimal divide = bigDecimal.divide(new BigDecimal("1000")).setScale(2, BigDecimal.ROUND_HALF_DOWN);
//                BigDecimal multiple=new BigDecimal(1000);
//                BigDecimal divide = bigDecimal.divide(multiple);
                vehicleDetailVO.setTotalDistance(divide.toString());
            }

            //获取告警信息
            Future<List<AlarmInfoHandleInfoVO>> alarmResult = alarmService.getTodayAlarmByVehicle(Long.valueOf(vehicleId));
            if (alarmResult != null && alarmResult.get() != null) {
                List<AlarmInfoHandleInfoVO> alarmList = alarmResult.get();
                vehicleDetailVO.setAlarmCount(alarmList.size());
                vehicleDetailVO.setLastAlarmContent(alarmList.get(0).getAlarmMessage());
            } else {
                vehicleDetailVO.setAlarmCount(0);
                vehicleDetailVO.setLastAlarmContent(null);
            }


            try {
                // 设置项目ID和公司ID
                Project project = ProjectCache.getProjectByCode(vehicleInfo.getTenantId());
//                R<Project> projectByTenantIdR = projectClient.getProjectByTenantId(vehicleInfo.getTenantId());
                if (project != null && project.getId() != null) {
                    vehicleDetailVO.setProjectId(String.valueOf(project.getId()));
                    vehicleDetailVO.setProjectName(project.getProjectName());

                    Company company = CompanyCache.getCompany(project.getCompanyId());
//                    R<Company> companyByIdR = companyClient.getCompanyById(projectByTenantIdR.getData().getCompanyId());
                    if (company != null && company.getId() != null){
                        vehicleDetailVO.setCompanyId(String.valueOf(company.getId()));
                        vehicleDetailVO.setCompanyName(company.getFullName());
                    }


                }
            } catch (Exception e) {
                log.info("无法设置公司ID和项目ID");
            }

            return vehicleDetailVO;
        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
    }


    @Override
    public VehicleDetailVO getVehicleStatusRealTime(Long vehicleId, String tenantId) {
        VehicleDetailVO vehicleDetailVO = new VehicleDetailVO();
        try {
            //根据车辆ID获取车辆信息
            VehicleInfo vehicleInfo = VehicleCache.getVehicleById(tenantId, vehicleId);
            if (vehicleInfo == null) {
                log.error("车辆[{}]不存在", vehicleInfo.getId());
                return null;
            }
            vehicleDetailVO.setPlateNumber(vehicleInfo.getPlateNumber());
            if (!ObjectUtils.isEmpty(vehicleInfo.getEntityCategoryId())) {
                vehicleDetailVO.setEntityCategoryId(vehicleInfo.getEntityCategoryId());
                vehicleDetailVO.setEntityCategoryName(entityCategoryClient.getCategoryName(vehicleInfo.getEntityCategoryId()).getData());
            }

            String deviceStatus = DeviceCache.getDeviceStatus(vehicleId, VehicleConstant.VEHICLE_ACC_DEVICE_TYPE, tenantId);
            vehicleDetailVO.setDeviceStatus(Long.parseLong(deviceStatus));
            vehicleDetailVO.setDeviceStatusName(DeviceStatusEnum.getDescByValue(vehicleDetailVO.getDeviceStatus()));
            Boolean isNeedWork = scheduleClient.checkNeedWork(vehicleId, ArrangeConstant.ScheduleObjectEntityType.VEHICLE, new Date()).getData();
            VehicleStatusEnum status = null;
            if (isNeedWork != null && isNeedWork) {
                if (deviceStatus.equals(DeviceConstant.DeviceStatus.ON)) {
                    //需要出勤且ACC为开
                    status = VehicleStatusEnum.ON_LINE;
                } else {
                    status = VehicleStatusEnum.OFFLINE_ALARM;
                    //没有绑定ACC、ACC关闭、ACC异常关闭都视为静值
                }
            } else {
                status = VehicleStatusEnum.OFF_LINE;
            }
            vehicleDetailVO.setWorkStatus(status.getValue());
            vehicleDetailVO.setWorkStatusName(status.getDesc());
            return vehicleDetailVO;
        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
    }


    @Override
    public List<VehicleInfo> getVehicleInfoByRegionId(Long regionId) {
        if (regionId == null ) {
            return null;
        }


//        List<WorkareaInfo> workareaInfoList = workareaClient.getWorkareaInfoByRegion(regionId).getData();
//        if (CollectionUtil.isNotEmpty(workareaInfoList)) {
//            List<String> workareaIds = workareaInfoList.stream().map(workareaInfo -> workareaInfo.getId().toString()).collect(Collectors.toList());
//            if (CollectionUtil.isNotEmpty(workareaIds)) {
//                List<WorkareaRel> workareaRels = workareaRelClient.getByWorkareaIds(workareaIds, VehicleConstant.WORKAREA_REL_VEHICLE).getData();
//                entityIds = workareaRels.stream().map(WorkareaRel::getEntityId).collect(Collectors.toList());
//            }
//        }

        List<Long> regionIds=new ArrayList<>();
        regionIds.add(regionId);
        //获取所有下级区域
        List<Long> allSubRegionByRegionIds = getAllSubRegionByRegionIds(regionIds);
        //查询所有车
        Query query=Query.query(Criteria.where("vehicleBelongRegion").in(getAllSubRegionByRegionIds(allSubRegionByRegionIds)));
        List<BasicVehicleInfoDTO> entityIds = mongoTemplate.find(query, BasicVehicleInfoDTO.class);
        if (CollectionUtil.isEmpty(entityIds)) {
            return null;
        }

        List<VehicleInfo> vehicleInfoList = entityIds.stream().map(basicVehicleInfoDTO -> {
            VehicleInfo vehicleInfo = BeanUtil.copy(basicVehicleInfoDTO, VehicleInfo.class);
            return vehicleInfo;
        }).collect(Collectors.toList());
//        VehicleInfoDTO vehicleInfoDto = new VehicleInfoDTO();
//        vehicleInfoDto.setVehicleIds(entityIds);
//        List<VehicleInfo> vehicleInfoList = vehicleClient.listVehicleByCondition(vehicleInfoDto).getData();

        return vehicleInfoList;
    }

    /*******************临时接口***********************/

    @Override
    public R<VehicleMonitorVO> getVehiclesPosition(GetVehiclePositionDTO getVehiclePositionDTO) {


        Query query=new Query();
        if (StringUtil.isNotBlank(getVehiclePositionDTO.getRegionId())){
            List<Long> regionIds=new ArrayList<>();
            regionIds.add(Long.parseLong(getVehiclePositionDTO.getRegionId()));
            query.addCriteria(Criteria.where("vehicleBelongRegion").in(getAllSubRegionByRegionIds(regionIds)));
        }
        if (StringUtil.isNotBlank(getVehiclePositionDTO.getCategoryId())) {
            query.addCriteria(Criteria.where("entityCategoryId").in(Long.parseLong(getVehiclePositionDTO.getCategoryId())));
        }
        if (StringUtil.isNotBlank(getVehiclePositionDTO.getPlateNumber())) {
            query.addCriteria(Criteria.where("plateNumber").regex("^.*" + getVehiclePositionDTO.getPlateNumber() + ".*$"));
        }

        query.addCriteria(Criteria.where("tenantId").is(AuthUtil.getTenantId()));
        List<BasicVehicleInfoDTO> basicVehicleInfoDTOS = mongoTemplate.find(query, BasicVehicleInfoDTO.class);
        if (CollectionUtil.isEmpty(basicVehicleInfoDTOS)){
            return R.data(null);
        }
        String vehicleIds = basicVehicleInfoDTOS.stream().map(BasicVehicleInfoDTO::getId).map(String::valueOf).collect(Collectors.joining(","));

        WebsocketTask websocketTask=new WebsocketTask();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleIds", vehicleIds);
        params.put("coordsSystem", BaiduMapUtils.CoordsSystem.GC02.value);
        websocketTask.setParams(params);
        websocketTask.setTaskType(GET_VEHICLE_POSITION);
        websocketTask.setTopic(WebSocketConsts.PUSH_VEHICLE_MONITOR);
        VehiclePositionTask positionTask = new VehiclePositionTask(websocketTask);

        R<VehicleMonitorVO> execute = positionTask.execute();
        return execute;
    }

    @Override
    public VehicleDetailVO getVehicleDetail(String vehicleId) {

        VehicleDetailVO vehicleDetailVO = getVehicleDetailRealTime(Long.parseLong(vehicleId), null, BaiduMapUtils.CoordsSystem.GC02);
        Future<DeviceInfo> deviceByPerson = deviceService.getPositionDeviceByVehicle(vehicleId);
        DeviceInfo deviceInfo = null;
        try {
            if (deviceByPerson != null && deviceByPerson.get() != null) {
                deviceInfo = deviceByPerson.get();
            } else {
                return null;
            }
            Future<PositionDTO> devicePosition = deviceService.getDevicePosition(deviceInfo.getDeviceCode(), BaiduMapUtils.CoordsSystem.GC02);
            PositionDTO positionDTO = null;
            if (devicePosition != null && devicePosition.get() != null) {
                positionDTO = devicePosition.get();
                vehicleDetailVO.setPosition(positionDTO);
            }
            Future<List<AlarmInfoHandleInfoVO>> alarmResult = alarmService.getTodayAlarmByVehicle(Long.valueOf(vehicleId));
            if (alarmResult != null && alarmResult.get() != null) {
                List<AlarmInfoHandleInfoVO> alarmList = alarmResult.get();
                vehicleDetailVO.setAlarmCount(alarmList.size());
                vehicleDetailVO.setLastAlarmContent(alarmList.get(0).getAlarmMessage());
            } else {
                vehicleDetailVO.setAlarmCount(0);
                vehicleDetailVO.setLastAlarmContent(null);
            }
            return vehicleDetailVO;

        } catch (Exception e) {
            vehicleDetailVO.setAlarmCount(0);
            vehicleDetailVO.setLastAlarmContent(null);
            return vehicleDetailVO;
        }
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
