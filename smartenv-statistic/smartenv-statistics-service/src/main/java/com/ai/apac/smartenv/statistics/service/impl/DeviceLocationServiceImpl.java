package com.ai.apac.smartenv.statistics.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.ai.apac.smartenv.alarm.cache.AlarmInfoCache;
import com.ai.apac.smartenv.alarm.entity.AlarmInfo;
import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.feign.IPersonVehicleRelClient;
import com.ai.apac.smartenv.statistics.dto.GetDeviceLocationDTO;
import com.ai.apac.smartenv.statistics.service.IDeviceLocationService;
import com.ai.apac.smartenv.statistics.vo.DeviceLocationDetailVO;
import com.ai.apac.smartenv.statistics.vo.DeviceLocationGroupVO;
import com.ai.apac.smartenv.statistics.vo.DeviceLocationVO;
import com.ai.apac.smartenv.system.cache.ProjectCache;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.websocket.feign.IBigScreenDataClient;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonDetailVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleDetailVO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2021/1/6 1:05 下午
 **/
@Service
@Slf4j
public class DeviceLocationServiceImpl implements IDeviceLocationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IBigScreenDataClient bigScreenDataClient;

    @Autowired
    private IAlarmInfoClient alarmInfoClient;

    @Autowired
    private IPersonVehicleRelClient personVehicleRelClient;

    /**
     * 查询设备实体位置
     *
     * @param getDeviceLocationDTO
     * @return
     */
    @Override
    public DeviceLocationGroupVO listDeviceLocation(GetDeviceLocationDTO getDeviceLocationDTO) {
        DeviceLocationGroupVO deviceLocationGroupVO = new DeviceLocationGroupVO();
        Integer searchObjType = getDeviceLocationDTO.getSearchObjType();
        switch (searchObjType) {
            case 1: {
                //查询车辆
                deviceLocationGroupVO.setVehicleList(getVehiclePosition(getDeviceLocationDTO));
                break;
            }
            case 2: {
                //查询人员
                deviceLocationGroupVO.setPersonList(getPersonPosition(getDeviceLocationDTO));
                break;
            }
            default: {
                //查询所有
                List<DeviceLocationVO> vehicleList = getVehiclePosition(getDeviceLocationDTO);
                List<DeviceLocationVO> personList = getPersonPosition(getDeviceLocationDTO);
                if (CollUtil.isNotEmpty(vehicleList)) {
                    deviceLocationGroupVO.setVehicleList(vehicleList);
                }
                if (CollUtil.isNotEmpty(personList)) {
                    deviceLocationGroupVO.setPersonList(personList);
                }
                break;
            }
        }
        return deviceLocationGroupVO;
    }

    /**
     * 查询车辆信息
     *
     * @param getDeviceLocationDTO
     * @return
     */
    private List<DeviceLocationVO> getVehiclePosition(GetDeviceLocationDTO getDeviceLocationDTO) {
        Query query = buildQuery(getDeviceLocationDTO);
        String vehicleTypeName = getDeviceLocationDTO.getVehicleTypeName();
        if (StringUtils.isNotEmpty(vehicleTypeName) && !"全部".equals(vehicleTypeName)) {
            Pattern pattern = Pattern.compile("^.*" + vehicleTypeName + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("vehicleTypeName").regex(pattern));
        }
        List<BasicVehicleInfoDTO> vehicleList = mongoTemplate.find(query, BasicVehicleInfoDTO.class);
        if (CollUtil.isNotEmpty(vehicleList)) {
            List<DeviceLocationVO> list = vehicleList.parallelStream().map(vehicleInfo -> {
                DeviceLocationVO devicePositionVO = new DeviceLocationVO();
                devicePositionVO.setId(vehicleInfo.getId());
                devicePositionVO.setDeviceObjType(1);
                devicePositionVO.setLat(Double.valueOf(vehicleInfo.getLat()));
                devicePositionVO.setLng(Double.valueOf(vehicleInfo.getLng()));
                devicePositionVO.setObjName(vehicleInfo.getPlateNumber());
                devicePositionVO.setWorkStatus(vehicleInfo.getWorkStatus());
                devicePositionVO.setWorkStatusName(vehicleInfo.getWorkStatusName());
                devicePositionVO.setDeviceStatus(vehicleInfo.getDeviceStatus());
                devicePositionVO.setGroupId(devicePositionVO.getId() + "-" + devicePositionVO.getDeviceObjType());
                Integer alarmCount = AlarmInfoCache.getUnHandledAlarmCountToday(vehicleInfo.getId(), CommonConstant.ENTITY_TYPE.VEHICLE);
                if (alarmCount > 0) {
                    devicePositionVO.setAlarmStatus(DeviceConstant.AlarmStatus.ALARM);
                } else {
                    devicePositionVO.setAlarmStatus(DeviceConstant.AlarmStatus.NORMAL);
                }
                return devicePositionVO;
            }).collect(Collectors.toList());
            return list;
        }
        return new ArrayList<DeviceLocationVO>();
    }

    /**
     * 查询人员信息
     *
     * @param getDeviceLocationDTO
     * @return
     */
    private List<DeviceLocationVO> getPersonPosition(GetDeviceLocationDTO getDeviceLocationDTO) {
        Query query = buildQuery(getDeviceLocationDTO);
        String personStationName = getDeviceLocationDTO.getPersonStationName();
        if (StringUtils.isNotEmpty(personStationName) && !"全部".equals(personStationName)) {
            Pattern pattern = Pattern.compile("^.*" + personStationName + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("personPositionName").regex(pattern));
        }
        List<BasicPersonDTO> personList = mongoTemplate.find(query, BasicPersonDTO.class);
        if (CollUtil.isNotEmpty(personList)) {
            List<DeviceLocationVO> list = personList.parallelStream().map(personInfo -> {
                DeviceLocationVO devicePositionVO = new DeviceLocationVO();
                devicePositionVO.setId(personInfo.getId());
                devicePositionVO.setDeviceObjType(2);
                devicePositionVO.setLat(Double.valueOf(personInfo.getLat()));
                devicePositionVO.setLng(Double.valueOf(personInfo.getLng()));
                devicePositionVO.setObjName(personInfo.getPersonName());
                devicePositionVO.setWorkStatus(personInfo.getWorkStatus());
                devicePositionVO.setWorkStatusName(personInfo.getWorkStatusName());
                devicePositionVO.setDeviceStatus(personInfo.getDeviceStatus());
                devicePositionVO.setGroupId(devicePositionVO.getId() + "-" + devicePositionVO.getDeviceObjType());
                Integer alarmCount = AlarmInfoCache.getUnHandledAlarmCountToday(personInfo.getId(), CommonConstant.ENTITY_TYPE.PERSON);
                if (alarmCount > 0) {
                    devicePositionVO.setAlarmStatus(DeviceConstant.AlarmStatus.ALARM);
                } else {
                    devicePositionVO.setAlarmStatus(DeviceConstant.AlarmStatus.NORMAL);
                }
                return devicePositionVO;
            }).collect(Collectors.toList());
            return list;
        }
        return new ArrayList<DeviceLocationVO>();
    }

    private Query buildQuery(GetDeviceLocationDTO getDeviceLocationDTO) {
        Query query = new Query();
        String projectCodes = getDeviceLocationDTO.getProjectCodes();
        List<String> projectCodeList = Func.toStrList(projectCodes);
        query.addCriteria(Criteria.where("tenantId").in(projectCodeList).and("lat").ne(null).and("lng").ne(null));
        String deviceStatusStr = getDeviceLocationDTO.getDeviceStatuss();
        if (StringUtils.isNotEmpty(deviceStatusStr)) {
            List<Long> deviceStatusList = Func.toLongList(deviceStatusStr);
            query.addCriteria(Criteria.where("deviceStatus").in(deviceStatusList));
        }
        String workstatusStr = getDeviceLocationDTO.getWorkStatuss();
        if (StringUtils.isNotEmpty(workstatusStr)) {
            List<Integer> workStatusList = Func.toIntList(workstatusStr);
            query.addCriteria(Criteria.where("workStatus").in(workStatusList));
        }
        return query;
    }

    /**
     * 查询设备
     *
     * @param deviceObjId
     * @param deviceObjType
     * @param coordsSystem
     * @return
     */
    @Override
    public DeviceLocationDetailVO getDeviceLocationDetail(Long deviceObjId, Integer deviceObjType, String coordsSystem) {
        DeviceLocationDetailVO deviceLocationDetailVO = new DeviceLocationDetailVO();
        switch (deviceObjType) {
            case 1: {
                deviceLocationDetailVO = getVehicleLocationDetail(deviceObjId, coordsSystem);
                break;
            }
            default: {
                deviceLocationDetailVO = getPersonLocationDetail(deviceObjId, coordsSystem);
                break;
            }
        }
        return deviceLocationDetailVO;
    }

    private DeviceLocationDetailVO getVehicleLocationDetail(Long vehicleId, String coordsSystem) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(vehicleId));
        BasicVehicleInfoDTO vehicleInfoDTO = mongoTemplate.findOne(query, BasicVehicleInfoDTO.class);
        if (vehicleInfoDTO != null) {
            DeviceLocationDetailVO deviceLocationDetailVO = new DeviceLocationDetailVO();
            deviceLocationDetailVO.setObjId(vehicleInfoDTO.getId());
            deviceLocationDetailVO.setWorkStatusName(vehicleInfoDTO.getWorkStatusName());
            deviceLocationDetailVO.setObjCategoryName(vehicleInfoDTO.getVehicleTypeName());
            deviceLocationDetailVO.setSpeedUnit("公里/时");
            deviceLocationDetailVO.setDistanceUnit("公里");
            deviceLocationDetailVO.setProjectCode(vehicleInfoDTO.getTenantId());
            deviceLocationDetailVO.setPlateNumber(vehicleInfoDTO.getPlateNumber());
            Project project = ProjectCache.getProjectByCode(vehicleInfoDTO.getTenantId());
            if (project != null && project.getId() != null) {
                deviceLocationDetailVO.setProjectName(project.getProjectName());
            }
            R<VehicleDetailVO> result = bigScreenDataClient.getVehicleLocationDetail(vehicleInfoDTO.getId(), vehicleInfoDTO.getTenantId(), coordsSystem);
            if (result.isSuccess() && result.getData() != null && result.getData().getId() != null) {
                VehicleDetailVO vehicleDetailVO = result.getData();
                deviceLocationDetailVO.setPersonName(vehicleDetailVO.getDriver());
                deviceLocationDetailVO.setTotalDistance(vehicleDetailVO.getTotalDistance());
                deviceLocationDetailVO.setSpeed(vehicleDetailVO.getSpeed());
                deviceLocationDetailVO.setAvgSpeed(vehicleDetailVO.getAvgSpeed());
                deviceLocationDetailVO.setMaxSpeed(vehicleDetailVO.getMaxSpeed());
            }
            deviceLocationDetailVO = this.getNoHandleAlarmInfoByDeviceCode(deviceLocationDetailVO, vehicleInfoDTO.getGpsDeviceCode());
            Person driver = this.getVehicleDriver(vehicleInfoDTO.getId(), vehicleInfoDTO.getTenantId());
            if (driver != null) {
                deviceLocationDetailVO.setPersonName(driver.getPersonName());
                deviceLocationDetailVO.setMobile(driver.getMobileNumber());
            }
            return deviceLocationDetailVO;
        }
        return null;
    }

    private DeviceLocationDetailVO getPersonLocationDetail(Long personId, String coordsSystem) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(personId));
        BasicPersonDTO personInfo = mongoTemplate.findOne(query, BasicPersonDTO.class);
        if (personInfo != null) {
            DeviceLocationDetailVO deviceLocationDetailVO = new DeviceLocationDetailVO();
            deviceLocationDetailVO.setObjId(personInfo.getId());
            deviceLocationDetailVO.setWorkStatusName(personInfo.getWorkStatusName());
            deviceLocationDetailVO.setObjCategoryName(personInfo.getPersonPositionName());
            deviceLocationDetailVO.setSpeedUnit("米/秒");
            deviceLocationDetailVO.setDistanceUnit("米");
            deviceLocationDetailVO.setProjectCode(personInfo.getTenantId());
            deviceLocationDetailVO.setMobile(personInfo.getMobileNumber() == null ? "" : String.valueOf(personInfo.getMobileNumber()));
            Project project = ProjectCache.getProjectByCode(personInfo.getTenantId());
            if (project != null && project.getId() != null) {
                deviceLocationDetailVO.setProjectName(project.getProjectName());
            }
            R<PersonDetailVO> result = bigScreenDataClient.getPersonLocationDetail(personInfo.getId(), personInfo.getTenantId());
            if (result.isSuccess() && result.getData() != null && result.getData().getId() != null) {
                PersonDetailVO personDetailVO = result.getData();
                deviceLocationDetailVO.setPersonName(personDetailVO.getPersonName());
                deviceLocationDetailVO.setProjectName(personDetailVO.getProjectName());
                deviceLocationDetailVO.setTotalDistance(personDetailVO.getTotalDistance());
                deviceLocationDetailVO.setSpeed(personDetailVO.getSpeed());
                deviceLocationDetailVO.setAvgSpeed(personDetailVO.getAvgSpeed());
                deviceLocationDetailVO.setMaxSpeed(personDetailVO.getMaxSpeed());
                deviceLocationDetailVO.setMobile(personDetailVO.getMobile());
            }
            deviceLocationDetailVO = this.getNoHandleAlarmInfoByDeviceCode(deviceLocationDetailVO, personInfo.getWatchDeviceCode());
            return deviceLocationDetailVO;
        }
        return null;
    }

    /**
     * 根据设备编码获取最新未处理的告警信息
     *
     * @param deviceCode
     * @return
     */
    private DeviceLocationDetailVO getNoHandleAlarmInfoByDeviceCode(DeviceLocationDetailVO deviceLocationDetailVO, String deviceCode) {
        R<List<AlarmInfo>> result = alarmInfoClient.getNoHandleAlarmInfoByDeviceCode(deviceCode);
        if (result.isSuccess() && result.getData() != null) {
            List<AlarmInfo> alarmList = result.getData();
            deviceLocationDetailVO.setAlarmCount(alarmList.size());
            deviceLocationDetailVO.setLastAlarmContent(alarmList.get(0).getRuleName());
            return deviceLocationDetailVO;
        } else {
            deviceLocationDetailVO.setAlarmCount(0);
            deviceLocationDetailVO.setLastAlarmContent("无");
        }
        return deviceLocationDetailVO;
    }

    /**
     * 根据车辆查询驾驶员信息
     *
     * @param vehicleId
     * @param projectCode
     * @return
     */
    private Person getVehicleDriver(Long vehicleId, String projectCode) {
        List<PersonVehicleRel> personVehicleRelList = personVehicleRelClient.getPersonVehicleRels(vehicleId).getData();
        if (CollUtil.isNotEmpty(personVehicleRelList)) {
            //有可能绑定多个驾驶员,根据排班只返回正在工作的驾驶员
            PersonVehicleRel personVehicleRel = personVehicleRelList.get(0);
            Long personId = personVehicleRel.getPersonId();
            Person person = PersonCache.getPersonById(projectCode, personId);
            if (person != null && person.getId() != null) {
                return person;
            }
        }
        return null;
    }
}
