package com.ai.apac.smartenv.omnic.service.impl;

import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.enums.DeviceStatusEnum;
import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.device.dto.mongo.GreenScreenDeviceDTO;
import com.ai.apac.smartenv.device.dto.mongo.GreenScreenDevicesDTO;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.event.dto.mongo.GreenScreenEventDTO;
import com.ai.apac.smartenv.event.dto.mongo.GreenScreenEventsDTO;
import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.green.dto.mongo.*;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.omnic.feign.IRealTimeStatusClient;
import com.ai.apac.smartenv.omnic.service.IViewService;
import com.ai.apac.smartenv.omnic.vo.TenantDetailsVO;
import com.ai.apac.smartenv.omnic.vo.WorkAreaDetailVO;
import com.ai.apac.smartenv.omnic.vo.WorkareaInfoBigScreenVO;
import com.ai.apac.smartenv.omnic.vo.WorkingDataCountVO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @ClassName BigScreenViewServiceImpl
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/13 14:55
 * @Version 1.0
 */
@Service
@Slf4j
@AllArgsConstructor
public class ViewServiceImpl implements IViewService {

    private IScheduleClient scheduleClient;

    private IWorkareaClient workareaClient;

    private IDeviceClient deviceClient;

    private IPersonClient personClient;

    private IWorkareaRelClient workareaRelClient;

    private IEventInfoClient eventInfoClient;

    private IAlarmInfoClient alarmInfoClient;

    private IRealTimeStatusClient realTimeStatusClient;

    private MongoTemplate mongoTemplate;

    @Override
    public WorkingDataCountVO getWorkingDataCount() {
        Integer shouldWorkVehicleCount = 0;
        Integer shouldWorkPersonCount = 0;
        Integer eventCount = 0;
        Integer alarmCount = 0;
        Integer workingVehicleCount = 0;
        Integer workingPersonCount = 0;


        //调用接口查今日应该到岗车辆树
        R<Integer> shouldWorkVehicleCountResult = scheduleClient.countVehicleForToday(AuthUtil.getTenantId());
        if (shouldWorkVehicleCountResult != null && shouldWorkVehicleCountResult.getData() != null) {
            shouldWorkVehicleCount = shouldWorkVehicleCountResult.getData();
        }
        //调用接口查今日应该到岗人数
        R<Integer> shouldWorkPersonCountResult = scheduleClient.countPersonForToday(AuthUtil.getTenantId());
        if (shouldWorkPersonCountResult != null && shouldWorkPersonCountResult.getData() != null) {
            shouldWorkPersonCount = shouldWorkPersonCountResult.getData();
        }

        //调用接口查今日告警总数
        R<Integer> alarmCountResult = alarmInfoClient.countAlarmInfoAmount(AuthUtil.getTenantId());
        if (alarmCountResult != null && alarmCountResult.getData() != null) {
            alarmCount = alarmCountResult.getData();
        }

        //调用接口查今日事件总数
        R<Integer> eventCountResult = eventInfoClient.countEventDaily(AuthUtil.getTenantId());
        if (eventCountResult != null && eventCountResult.getData() != null) {
            eventCount = eventCountResult.getData();
        }

        //调用接口查人的各种状态统计数字
        R<StatusCount> statusCountResult = realTimeStatusClient.getAllPersonStatusCount(AuthUtil.getTenantId());
        if (statusCountResult != null && statusCountResult.getData() != null) {
            StatusCount statusCount = statusCountResult.getData();
            workingPersonCount = statusCount.getWorking().intValue();
        }

        //调用接口查车的各种状态统计数字
        R<StatusCount> statusCountResult_ = realTimeStatusClient.getAllVehicleStatusCount(AuthUtil.getTenantId());
        if (statusCountResult_ != null && statusCountResult_.getData() != null) {
            StatusCount statusCount_ = statusCountResult_.getData();
            workingVehicleCount = statusCount_.getWorking().intValue();
        }

        WorkingDataCountVO workingDataCountVO = new WorkingDataCountVO();

        workingDataCountVO.setShouldWorkVehicleCount(shouldWorkVehicleCount);
        workingDataCountVO.setShouldWorkPersonCount(shouldWorkPersonCount);
        workingDataCountVO.setEventCount(eventCount);
        workingDataCountVO.setAlarmCount(alarmCount);
        workingDataCountVO.setWorkingVehicleCount(workingVehicleCount);
        workingDataCountVO.setWorkingPersonCount(workingPersonCount);
        return workingDataCountVO;
    }

    @Override
    public WorkAreaDetailVO getWorkAreaDetails() {
        WorkAreaDetailVO workAreaDetailVO = new WorkAreaDetailVO();
//        workAreaDetailVO.setWorkAreaId(workAreaId);
//        WorkareaInfo workareaInfo = workareaClient.getWorkInfoById(Long.parseLong(workAreaId)).getData();
//        workAreaDetailVO.setWorkAreaName(workareaInfo.getAreaName());
//        if(ObjectUtil.isNotEmpty(workareaInfo.getAreaHead())){
//            workAreaDetailVO.setAreaHead(workareaInfo.getAreaHead().toString());
//            Person person = personClient.getPerson(workareaInfo.getAreaHead()).getData();
//            workAreaDetailVO.setAreaHeadName(person.getPersonName());
//        }

        //指定区域上的人或车今天发生的事件个数
        Integer eventCount = eventInfoClient.countEventDaily(AuthUtil.getTenantId()).getData();
        workAreaDetailVO.setEventCount(eventCount);

        //工作线路上绑定的人
        List<Long> entityIdList = new ArrayList<Long>();

        //目前全部应该正在工作的人
        List<Long> personIdList = new ArrayList<Long>();
        List<ScheduleObject> scheduleObjectList = scheduleClient.listPersonForNow(AuthUtil.getTenantId()).getData();
        if(ObjectUtil.isNotEmpty(scheduleObjectList) && scheduleObjectList.size() > 0){
            scheduleObjectList.forEach(scheduleObject -> {
                personIdList.add(scheduleObject.getEntityId());
            });
            workAreaDetailVO.setShouldworkPersonCount(personIdList.size());
//            List<WorkareaRel> workareaRelList1 =  workareaRelClient.getByCondition(workAreaId,CommonConstant.ENTITY_TYPE.PERSON,personIdList).getData();
//            if(ObjectUtil.isNotEmpty(workareaRelList1) && workareaRelList1.size() > 0 ){
//                workareaRelList1.forEach(workareaRel1 -> {
//                    personIdList_.add(workareaRel1.getEntityId());
//                    entityIdList.add(workareaRel1.getEntityId());
//                });
//                workAreaDetailVO.setShouldworkPersonCount(personIdList_.size());
//            }else{
//                workAreaDetailVO.setShouldworkPersonCount(0);
//            }
        }else{
            workAreaDetailVO.setShouldworkPersonCount(0);
            workAreaDetailVO.setWorkingPersonCount(0);
            workAreaDetailVO.setWorkingOffPersonCount(0);
        }

        //目前全部应该正在工作的车
        List<Long> vehicleIdIdList = new ArrayList<Long>();
        List<ScheduleObject> scheduleObjectList1 = scheduleClient.listVehicleForNow(AuthUtil.getTenantId()).getData();
        if(ObjectUtil.isNotEmpty(scheduleObjectList1) && scheduleObjectList1.size() > 0){
            scheduleObjectList1.forEach(scheduleObject1 -> {
                vehicleIdIdList.add(scheduleObject1.getEntityId());
            });
            workAreaDetailVO.setShouldWorkVehicleCount(vehicleIdIdList.size());
//            List<WorkareaRel> workareaRelList2 =  workareaRelClient.getByCondition(workAreaId,CommonConstant.ENTITY_TYPE.VEHICLE,vehicleIdIdList).getData();
//            if(ObjectUtil.isNotEmpty(workareaRelList2) && workareaRelList2.size() > 0 ){
//                workareaRelList2.forEach(workareaRel2 -> {
//                    vehicleIdList_.add(workareaRel2.getEntityId());
//                    entityIdList.add(workareaRel2.getEntityId());
//                });
//                workAreaDetailVO.setShouldWorkVehicleCount(vehicleIdList_.size());
//            }else{
//                workAreaDetailVO.setShouldWorkVehicleCount(0);
//            }
        }else{
            workAreaDetailVO.setShouldWorkVehicleCount(0);
            workAreaDetailVO.setWorkingVehicleCount(0);
            workAreaDetailVO.setWorkingOffVehicleCount(0);
        }

        //今天产生的告警个数
        Integer count = alarmInfoClient.countAlarmInfoAmount(AuthUtil.getTenantId()).getData();
        workAreaDetailVO.setAlarmCount(count);


        Integer workingPersonCount = 0;
        Integer workingOffPersonCount = 0;
        if(personIdList.size()>0){
            for(Long personId:personIdList){
                //获取设备开关信息
                DeviceInfo deviceInfo = deviceClient.getByEntityAndCategory(personId, DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE).getData();
                if (ObjectUtil.isNotEmpty(deviceInfo) && ObjectUtil.isNotEmpty(deviceInfo.getDeviceStatus())) {
                    String deviceStatus = deviceInfo.getDeviceStatus().toString();
                    if(deviceStatus.equals(DeviceConstant.DeviceStatus.ON)){
                        workingPersonCount ++;
                    }else{
                        workingOffPersonCount ++;
                    }
                }else{
                    workingOffPersonCount ++;//没绑定设备，当做在线处理
                }
            };
        }
        workAreaDetailVO.setWorkingPersonCount(workingPersonCount);
        workAreaDetailVO.setWorkingOffPersonCount(workingOffPersonCount);

        Integer workingVehicleCount = 0;
        Integer workingOffVehicleCount = 0;
        if(vehicleIdIdList.size()>0){
            for(Long vehicleId:vehicleIdIdList){
                //获取设备开关信息
                DeviceInfo deviceInfo = deviceClient.getByEntityAndCategory(vehicleId, DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE).getData();
                if (ObjectUtil.isNotEmpty(deviceInfo) && ObjectUtil.isNotEmpty(deviceInfo.getDeviceStatus())) {
                    String deviceStatus = deviceInfo.getDeviceStatus().toString();
                    if(deviceStatus.equals(DeviceConstant.DeviceStatus.ON)){
                        workingVehicleCount ++;
                    }else{
                        workingOffVehicleCount ++;
                    }
                }else{
                    workingOffVehicleCount ++;//没绑定设备，当做在线处理
                }
            };
        }
        workAreaDetailVO.setWorkingVehicleCount(workingVehicleCount);
        workAreaDetailVO.setWorkingOffVehicleCount(workingOffVehicleCount);

        return workAreaDetailVO;
    }


    @Override
    public TenantDetailsVO getTenantDetails(String tenantId) {
        GreenScreenEventsDTO lastDaysEvents = null;
        TenantDetailsVO tenantDetailsVO = new TenantDetailsVO();
        //根据tenantId通过mogoDB查询各个子对象拼接成大对象
        //先从MongoDB中取数据
        Query mongoQuery = new Query();
        mongoQuery.addCriteria(Criteria.where("tenantId").is(tenantId));
        GreenScreenWorkingCountDTO workingCountToday = mongoTemplate.findOne(mongoQuery, GreenScreenWorkingCountDTO.class, OmnicConstant.mongoNmae.WORK_COUNT_TODAY);
        GreenScreenGreenAreasDTO greenAreaTotal = mongoTemplate.findOne(mongoQuery, GreenScreenGreenAreasDTO.class,OmnicConstant.mongoNmae.GREEN_AREA_TOTAL);
        GreenScreenTasksDTO lastDaysTaskCount = mongoTemplate.findOne(mongoQuery, GreenScreenTasksDTO.class,OmnicConstant.mongoNmae.LAST_DAYS_TASK);
        try {
            lastDaysEvents = eventInfoClient.queryEventInfos(tenantId).getData();
        }catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        List<GreenScreenDeviceDTO> deviceDTOs = mongoTemplate.find(mongoQuery, GreenScreenDeviceDTO.class,OmnicConstant.mongoNmae.DEVICE_DATA);
        if(null!=deviceDTOs&&deviceDTOs.size()>0){
            if(null!=greenAreaTotal.getGreenAreaList()&&greenAreaTotal.getGreenAreaList().size()>0){
                for (GreenScreenGreenAreaDTO areaDto : greenAreaTotal.getGreenAreaList()) {
                    if(null!=areaDto.getDeviceId()) {
                        for (GreenScreenDeviceDTO deviceDTO : deviceDTOs) {
                            if (areaDto.getDeviceId().equals(deviceDTO.getDeviceId())) {
                                areaDto.setIndexList(deviceDTO.getIndexList());
                                break;
                            }
                        }
                    }
                }
            }
        }
        if(null!=workingCountToday)
            tenantDetailsVO.setWorkingCountToday(workingCountToday);
        if(null!=greenAreaTotal)
            tenantDetailsVO.setGreenAreaTotal(greenAreaTotal);
        if(null!=lastDaysTaskCount&&null!=lastDaysTaskCount.getLastDaysTaskCount())
            tenantDetailsVO.setLastDaysTaskCount(lastDaysTaskCount.getLastDaysTaskCount());
        if(null!=lastDaysEvents&&null!=lastDaysEvents.getLastDaysEvents())
            tenantDetailsVO.setLastDaysEvents(lastDaysEvents.getLastDaysEvents());
        tenantDetailsVO.setTenantId(tenantId);
        return tenantDetailsVO;
    }


    @Override
    public WorkareaInfoBigScreenVO getWorkareaInfoBigScreen(String tenantId) {
        //先从MongoDB中取数据
        Query mongoQuery = new Query();
        mongoQuery.addCriteria(Criteria.where("tenantId").is(tenantId));
        WorkareaInfoBigScreenVO workingCountToday = mongoTemplate.findOne(mongoQuery, WorkareaInfoBigScreenVO.class, OmnicConstant.mongoNmae.WORK_AREA_INFO);

        return workingCountToday;
    }
}
