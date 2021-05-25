package com.ai.apac.smartenv.statistics.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoMongoDBVO;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.event.entity.EventKpiDef;
import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.feign.IMappingClient;
import com.ai.apac.smartenv.statistics.dto.*;
import com.ai.apac.smartenv.statistics.service.IDataStatisticsService;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.entity.ProjectArea;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DataStatisticsServiceImpl implements IDataStatisticsService {

    private IProjectClient projectClient;

    private MongoTemplate mongoTemplate;

    private IMappingClient mappingClient;

    @Override
    public Boolean initialSynthInfo(SynthInfoDTO synthInfoDTO) {
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("projectCode").is(synthInfoDTO.getProjectCode()));
        Update update = new Update();
        update.set("allPersonCount",synthInfoDTO.getAllPersonCount());
        update.set("allVehicleCount",synthInfoDTO.getAllVehicleCount());
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
        update.set("personWorkAreaCount",synthInfoDTO.getPersonWorkAreaCount());
        update.set("vehicleWorkAreaCount",synthInfoDTO.getVehicleWorkAreaCount());
        log.info("changzou-------------synthInfoDTO.toString() ----- " + synthInfoDTO.toString());
        mongoTemplate.findAndModify(query,update, com.ai.apac.smartenv.omnic.dto.SynthInfoDTO.class);

        return true;
    }

    @Override
    public Boolean synthVehicleWorkInfo(VehicleWorkSynthInfoDTO vehicleWorkSynthInfoDTO) {
        if(org.springblade.core.tool.utils.ObjectUtil.isEmpty(vehicleWorkSynthInfoDTO.getVehicleCode())){
            throw new ServiceException("车辆编码不能为空");
        }
        AiMapping mapping = new AiMapping();
        mapping.setThirdCode(vehicleWorkSynthInfoDTO.getVehicleCode());
        mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.VEHICLE));
        AiMapping resMapping = mappingClient.getSscpCodeByThirdCode(mapping).getData();
        if(ObjectUtil.isEmpty(resMapping) || ObjectUtil.isEmpty(resMapping.getSscpCode())){
            throw new ServiceException(vehicleWorkSynthInfoDTO.getVehicleCode()+"车辆不存在");
        }
        vehicleWorkSynthInfoDTO.setVehicleId(resMapping.getSscpCode());
        vehicleWorkSynthInfoDTO.setProjectCode(resMapping.getTenantId());

        mongoTemplate.save(vehicleWorkSynthInfoDTO);
        return true;
    }

    @Override
    public Boolean synthPersonWorkInfo(PersonWorkSynthInfoDTO personWorkSynthInfoDTO) {
        if(org.springblade.core.tool.utils.ObjectUtil.isEmpty(personWorkSynthInfoDTO.getPersonCode())){
            throw new ServiceException("人员编码不能为空");
        }
        AiMapping mapping = new AiMapping();
        mapping.setThirdCode(personWorkSynthInfoDTO.getPersonCode());
        mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.PERSON));
        AiMapping resMapping = mappingClient.getSscpCodeByThirdCode(mapping).getData();
        if(ObjectUtil.isEmpty(resMapping) || ObjectUtil.isEmpty(resMapping.getSscpCode())){
            throw new ServiceException(personWorkSynthInfoDTO.getPersonCode()+"人员不存在");
        }
        personWorkSynthInfoDTO.setPersonId(resMapping.getSscpCode());
        personWorkSynthInfoDTO.setProjectCode(resMapping.getTenantId());


        mongoTemplate.save(personWorkSynthInfoDTO);
        return true;
    }

    @Override
    public Boolean synthVehicleStatInfo(VehicleStatSynthInfoDTO vehicleStatSynthInfoDTO) {
        if(org.springblade.core.tool.utils.ObjectUtil.isEmpty(vehicleStatSynthInfoDTO.getVehicleCode())){
            throw new ServiceException("车辆编码不能为空");
        }
        AiMapping mapping = new AiMapping();
        mapping.setThirdCode(vehicleStatSynthInfoDTO.getVehicleCode());
        mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.VEHICLE));
        AiMapping resMapping = mappingClient.getSscpCodeByThirdCode(mapping).getData();
        if(ObjectUtil.isEmpty(resMapping) || ObjectUtil.isEmpty(resMapping.getSscpCode())){
            throw new ServiceException(vehicleStatSynthInfoDTO.getVehicleCode()+"车辆不存在");
        }
        vehicleStatSynthInfoDTO.setVehicleId(resMapping.getSscpCode());
        vehicleStatSynthInfoDTO.setProjectCode(resMapping.getTenantId());

        mongoTemplate.save(vehicleStatSynthInfoDTO);
        return true;
    }

    @Override
    public Boolean synthPersonStatInfo(PersonStatSynthInfoDTO personStatSynthInfoDTO) {

        if(org.springblade.core.tool.utils.ObjectUtil.isEmpty(personStatSynthInfoDTO.getPersonCode())){
            throw new ServiceException("人员编码不能为空");
        }
        AiMapping mapping = new AiMapping();
        mapping.setThirdCode(personStatSynthInfoDTO.getPersonCode());
        mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.PERSON));
        AiMapping resMapping = mappingClient.getSscpCodeByThirdCode(mapping).getData();
        if(ObjectUtil.isEmpty(resMapping) || ObjectUtil.isEmpty(resMapping.getSscpCode())){
            throw new ServiceException(personStatSynthInfoDTO.getPersonCode()+"人员不存在");
        }
        personStatSynthInfoDTO.setPersonId(resMapping.getSscpCode());
        personStatSynthInfoDTO.setProjectCode(resMapping.getTenantId());

        mongoTemplate.save(personStatSynthInfoDTO);
        return true;
    }

    @Override
    public Boolean synthAlarmInfo(AlarmSynthInfoDTO alarmSynthInfoDTO) {
        if(org.springblade.core.tool.utils.ObjectUtil.isEmpty(alarmSynthInfoDTO.getDataContent())){
            throw new ServiceException("告警内容不能为空");
        }
        AiMapping mapping = new AiMapping();
        mapping.setThirdCode(alarmSynthInfoDTO.getEntityCode());
        if(OmnicConstant.THIRD_INFO_TYPE.PERSON.equals(alarmSynthInfoDTO.getEntityType())){
            mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.PERSON));
            AiMapping resMapping = mappingClient.getSscpCodeByThirdCode(mapping).getData();
            if(ObjectUtil.isEmpty(resMapping) || ObjectUtil.isEmpty(resMapping.getSscpCode())){
                throw new ServiceException(alarmSynthInfoDTO.getEntityCode()+"人员不存在");
            }
            alarmSynthInfoDTO.setEntityId(resMapping.getSscpCode());
            alarmSynthInfoDTO.setProjectCode(resMapping.getTenantId());
        }
        if(OmnicConstant.THIRD_INFO_TYPE.VEHICLE.equals(alarmSynthInfoDTO.getEntityType())){
            mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.VEHICLE));
            AiMapping resMapping = mappingClient.getSscpCodeByThirdCode(mapping).getData();
            if(ObjectUtil.isEmpty(resMapping) || ObjectUtil.isEmpty(resMapping.getSscpCode())){
                throw new ServiceException(alarmSynthInfoDTO.getEntityCode()+"车辆不存在");
            }
            alarmSynthInfoDTO.setEntityId(resMapping.getSscpCode());
            alarmSynthInfoDTO.setProjectCode(resMapping.getTenantId());
        }

        mongoTemplate.save(alarmSynthInfoDTO);
        return true;
    }
}
