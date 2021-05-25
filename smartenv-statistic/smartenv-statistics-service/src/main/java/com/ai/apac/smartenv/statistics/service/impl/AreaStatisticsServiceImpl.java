package com.ai.apac.smartenv.statistics.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.alarm.vo.AlarmAmountVO;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.person.dto.PersonDeviceStatusCountDTO;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.statistics.dto.DeviceOnlineInfoDTO;
import com.ai.apac.smartenv.statistics.dto.SynthInfoDTO;
import com.ai.apac.smartenv.statistics.dto.VehicleWorkSynthInfoDTO;
import com.ai.apac.smartenv.statistics.entity.*;
import com.ai.apac.smartenv.statistics.service.IAreaStatisticsService;
import com.ai.apac.smartenv.statistics.service.IVehicleWorkStatService;
import com.ai.apac.smartenv.system.cache.ProjectCache;
import com.ai.apac.smartenv.system.entity.AdministrativeCity;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.dto.VehicleDeviceStatusCountDTO;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/8 7:24 下午
 **/
@Service
@Slf4j
public class AreaStatisticsServiceImpl implements IAreaStatisticsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ISysClient sysClient;

    @Autowired
    private IProjectClient projectClient;

    @Autowired
    private IVehicleWorkStatService vehicleWorkStatService;

    @Autowired
    private IAlarmInfoClient alarmInfoClient;

    private static final Integer SEARCH_VEHICLE = 1;

    private static final Integer SEARCH_PERSON = 2;

    @Autowired
    private IVehicleClient vehicleClient;

    @Autowired
    private IPersonClient personClient;

    /**
     * 根据区域获取项目汇总数据
     *
     * @param areaCode
     * @param statDate
     * @return
     */
    @Override
    public AreaProjectInfo getAreaProjectInfo(String areaCode, String statDate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("area_code").is(areaCode));
        AreaProjectInfo areaProjectInfo = mongoTemplate.findOne(query, AreaProjectInfo.class);
        return areaProjectInfo;
    }

    /**
     * 保存数据
     *
     * @param areaProjectInfo
     * @return
     */
    @Override
    public boolean saveAreaProjectInfo(AreaProjectInfo areaProjectInfo) {
        Query query = this.buildQuery(areaProjectInfo.getAreaCode(), areaProjectInfo.getStatDate());
        mongoTemplate.remove(query, AreaProjectInfo.class);
        areaProjectInfo.setId(IdUtil.objectId());
        mongoTemplate.save(areaProjectInfo);
        return true;
    }

    /**
     * 根据区域和日期查询工作完成情况
     *
     * @param areaCode
     * @param statDate
     * @return
     */
    @Override
    public AreaWorkInfo getAreaWorkInfo(String areaCode, String statDate) {
        Query query = this.buildQuery(areaCode, statDate);
        AreaWorkInfo areaWorkInfo = mongoTemplate.findOne(query, AreaWorkInfo.class);
        return areaWorkInfo;
    }

    /**
     * 保存区域工作完成情况
     *
     * @param areaWorkInfo
     * @return
     */
    @Override
    public boolean saveAreaWorkInfo(AreaWorkInfo areaWorkInfo) {
        Query query = this.buildQuery(areaWorkInfo.getAreaCode(), areaWorkInfo.getStatDate());
        mongoTemplate.remove(query, AreaWorkInfo.class);
        areaWorkInfo.setId(IdUtil.objectId());
        mongoTemplate.save(areaWorkInfo);
        return true;
    }

    /**
     * 构造查询条件
     *
     * @param areaCode
     * @param statDate
     * @return
     */
    private Query buildQuery(String areaCode, String statDate) {
        if (StringUtils.isBlank(areaCode)) {
            return null;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("area_code").is(areaCode));
        if (StringUtils.isBlank(statDate)) {
            //默认查询当天实时数据
            statDate = DateUtil.today();
        }
        query.addCriteria(Criteria.where("stat_date").is(statDate));
        return query;
    }

    /**
     * 获取区域垃圾收运重量数据
     *
     * @param areaCode
     * @param statDate
     * @return
     */
    @Override
    public AreaTrashInfo getAreaTrashInfo(String areaCode, String statDate) {
        Query query = this.buildQuery(areaCode, statDate);
        AreaTrashInfo areaTrashInfo = mongoTemplate.findOne(query, AreaTrashInfo.class);
        return areaTrashInfo;
    }

    /**
     * 获取区域垃圾收运历史数量数据
     *
     * @param areaCode
     * @param beginDate
     * @param endDate
     * @return
     */
    @Override
    public List<AreaTrashInfo> getAreaTrashInfoHistory(String areaCode, String beginDate, String endDate) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("stat_date").gte(beginDate), Criteria.where("stat_date").lte(endDate));
        query.addCriteria(criteria);
        query.addCriteria(Criteria.where("area_code").is(areaCode));
        return mongoTemplate.find(query, AreaTrashInfo.class);
    }

    /**
     * 保存区域工作完成情况
     *
     * @param areaTrashInfo
     * @return
     */
    @Override
    public boolean saveAreaTrashInfo(AreaTrashInfo areaTrashInfo) {
        Query query = this.buildQuery(areaTrashInfo.getAreaCode(), areaTrashInfo.getStatDate());
        mongoTemplate.remove(query, AreaTrashInfo.class);
        areaTrashInfo.setId(IdUtil.objectId());
        mongoTemplate.save(areaTrashInfo);
        return true;
    }

    /**
     * 获取区域作业违规分析数据
     *
     * @param areaCode
     * @param statDate
     * @return
     */
    @Override
    public AreaIllegalBehaviorInfo getAreaIllegalBehaviorInfo(String areaCode, String statDate) {
        Query query = this.buildQuery(areaCode, statDate);
        AreaIllegalBehaviorInfo areaIllegalBehaviorInfo = mongoTemplate.findOne(query, AreaIllegalBehaviorInfo.class);
        return areaIllegalBehaviorInfo;
    }

    /**
     * 获取区域作业车辆违规分析数据
     *
     * @param areaCode
     * @param statDate
     * @return
     */
    @Override
    public List<AreaIllegalBehaviorInfo> getAreaIllegalBehaviorForVehicle(String areaCode, String projectCode, String statDate) {
//        if (StringUtils.isEmpty(statDate)) {
//            statDate = DateUtil.today();
//        }
//        Query query = this.buildQuery(areaCode, statDate);
//        query.addCriteria(Criteria.where("item_category").is("vehicle"));
//        List<AreaIllegalBehaviorInfo> list = null;
//        if (StringUtils.isNotEmpty(projectCode)) {
//            list = this.listAlarmCount(projectCode, areaCode, statDate, 1);
//        } else {
//            list = mongoTemplate.find(query, AreaIllegalBehaviorInfo.class);
//            if (CollUtil.isEmpty(list)) {
//                //获取项目信息
//                R<List<Project>> projectListResult = projectClient.getProjectByAdcode(Long.valueOf(areaCode));
//                if (projectListResult.isSuccess() && projectListResult.getData() != null) {
//                    list = this.listAlarmCount(projectListResult.getData(), areaCode, statDate, 1);
//                } else {
//                    return null;
//                }
//            }
//        }
        return this.getAreaIllegalBehavior(areaCode, projectCode, statDate, SEARCH_VEHICLE);
    }

    /**
     * 查询指定项目的告警数量
     *
     * @param projectCode
     * @param areaCode
     * @param statDate
     * @param queryType
     * @return
     */
    List<AreaIllegalBehaviorInfo> listAlarmCount(String projectCode, String areaCode, String statDate, Integer queryType) {
        List<Project> projectList = new ArrayList<>();
        List<String> projectCodeList = Func.toStrList(projectCode);
        for (String projectCodeStr : projectCodeList) {
            Project project = ProjectCache.getProjectByCode(projectCodeStr);
            if (project != null && project.getId() != null) {
                projectList.add(project);
            }
        }
        return this.listAlarmCount(projectList, areaCode, statDate, queryType);
    }

    /**
     * 查询告警数量
     *
     * @param projectList
     * @param queryType   1-查询车辆 2-查询人员
     * @return
     */
    List<AreaIllegalBehaviorInfo> listAlarmCount(List<Project> projectList, String areaCode, String statDate, Integer queryType) {
        List<AreaIllegalBehaviorInfo> list = new ArrayList<AreaIllegalBehaviorInfo>();
        Integer vehicleSpeedingAlarmCount = 0;
        Integer vehicleOutOfAreaAlarmCount = 0;
        Integer vehicleViolationAlarmCount = 0;
        Integer personViolationAlarmCount = 0;
        Integer personUnusualAlarmCount = 0;
        for (Project project : projectList) {
            R<AlarmAmountVO> alarmAmountResult = alarmInfoClient.countAllRuleAlarmAmount(project.getProjectCode());
            if (alarmAmountResult.isSuccess() && alarmAmountResult.getData() != null) {
                AlarmAmountVO alarmAmountVO = alarmAmountResult.getData();
                vehicleSpeedingAlarmCount = vehicleSpeedingAlarmCount + alarmAmountVO.getVehicleSpeedingAlarmCount();
                vehicleOutOfAreaAlarmCount = vehicleOutOfAreaAlarmCount + alarmAmountVO.getVehicleOutOfAreaAlarmCount();
                vehicleViolationAlarmCount = vehicleViolationAlarmCount + alarmAmountVO.getVehicleViolationAlarmCount();
                personViolationAlarmCount = personViolationAlarmCount + alarmAmountVO.getPersonViolationAlarmCount();
                personUnusualAlarmCount = personUnusualAlarmCount + alarmAmountVO.getPersonUnusualAlarmCount();
            }
        }
        if (queryType.equals(1)) {
            list.add(new AreaIllegalBehaviorInfo(IdUtil.fastUUID(), areaCode, statDate, "违规", "1", "vehicle", vehicleViolationAlarmCount, statDate));
            list.add(new AreaIllegalBehaviorInfo(IdUtil.fastUUID(), areaCode, statDate, "越界", "2", "vehicle", vehicleOutOfAreaAlarmCount, statDate));
            list.add(new AreaIllegalBehaviorInfo(IdUtil.fastUUID(), areaCode, statDate, "超速", "3", "vehicle", vehicleSpeedingAlarmCount, statDate));
        } else {
            list.add(new AreaIllegalBehaviorInfo(IdUtil.fastUUID(), areaCode, statDate, "违规", "1", "person", personViolationAlarmCount, statDate));
            list.add(new AreaIllegalBehaviorInfo(IdUtil.fastUUID(), areaCode, statDate, "异常", "2", "person", personUnusualAlarmCount, statDate));
        }
        return list;
    }


    /**
     * 获取区域作业人员违规分析数据
     *
     * @param areaCode
     * @param statDate
     * @return
     */
    @Override
    public List<AreaIllegalBehaviorInfo> getAreaIllegalBehaviorForPerson(String areaCode, String projectCode, String statDate) {
//        if (StringUtils.isEmpty(statDate)) {
//            statDate = DateUtil.today();
//        }
//        Query query = this.buildQuery(areaCode, statDate);
//        query.addCriteria(Criteria.where("item_category").is("person"));
//        List<AreaIllegalBehaviorInfo> list = null;
//        if (StringUtils.isNotEmpty(projectCode)) {
//            list = this.listAlarmCount(projectCode, areaCode, statDate, 2);
//        } else {
//            list = mongoTemplate.find(query, AreaIllegalBehaviorInfo.class);
//            if (CollUtil.isEmpty(list)) {
//                //获取项目信息
//                if (areaCode.equalsIgnoreCase("100000")) {
//
//                }
//                R<List<Project>> projectListResult = projectClient.getProjectByAdcode(Long.valueOf(areaCode));
//                if (projectListResult.isSuccess() && projectListResult.getData() != null) {
//                    list = this.listAlarmCount(projectListResult.getData(), areaCode, statDate, 2);
//                } else {
//                    return null;
//                }
//            }
//        }
        return this.getAreaIllegalBehavior(areaCode, projectCode, statDate, SEARCH_PERSON);
    }

    /**
     * 获取作业异常行为分析
     *
     * @param areaCode
     * @param projectCode
     * @param statDate
     * @param queryType
     * @return
     */
    private List<AreaIllegalBehaviorInfo> getAreaIllegalBehavior(String areaCode, String projectCode, String statDate, Integer queryType) {
        if (StringUtils.isEmpty(statDate)) {
            statDate = DateUtil.today();
        }
        List<AreaIllegalBehaviorInfo> list = null;
        if (StringUtils.isNotEmpty(projectCode)) {
            list = this.listAlarmCount(projectCode, areaCode, statDate, queryType);
        } else {
            Query query = this.buildQuery(areaCode, statDate);
            if (query.equals(1)) {
                query.addCriteria(Criteria.where("item_category").is("vehicle"));
            } else {
                query.addCriteria(Criteria.where("item_category").is("person"));
            }
            list = mongoTemplate.find(query, AreaIllegalBehaviorInfo.class);
            if (CollUtil.isEmpty(list)) {
                //获取项目信息
                R<List<Project>> projectListResult = null;
                if (areaCode.equalsIgnoreCase("100000")) {
                    //代表查询全国所有项目
                    projectListResult = projectClient.getALLProject();
                } else {
                    projectListResult = projectClient.getProjectByAdcode(Long.valueOf(areaCode));
                }
                if (projectListResult.isSuccess() && projectListResult.getData() != null) {
                    list = this.listAlarmCount(projectListResult.getData(), areaCode, statDate, queryType);
                } else {
                    return null;
                }
            }
        }
        return list;
    }

    /**
     * 保存区域作业违规分析数据
     *
     * @param areaIllegalBehaviorInfo
     * @return
     */
    @Override
    public boolean saveAreaIllegalBehaviorInfo(AreaIllegalBehaviorInfo areaIllegalBehaviorInfo) {
        Query query = this.buildQuery(areaIllegalBehaviorInfo.getAreaCode(), areaIllegalBehaviorInfo.getStatDate());
        query.addCriteria(Criteria.where("item_category").is(areaIllegalBehaviorInfo.getItemCategory()));
        query.addCriteria(Criteria.where("item_type").is(areaIllegalBehaviorInfo.getItemType()));
        mongoTemplate.remove(query, AreaIllegalBehaviorInfo.class);
        areaIllegalBehaviorInfo.setId(IdUtil.objectId());
        mongoTemplate.save(areaIllegalBehaviorInfo);
        return true;
    }

    /**
     * 获取区域历史告警数量数据
     *
     * @param areaCode
     * @param beginDate
     * @param endDate
     * @return
     */
    @Override
    public List<AreaAlarmCountInfo> getAreaAlarmCountHistory(String areaCode, String beginDate, String endDate) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("stat_date").gte(beginDate), Criteria.where("stat_date").lte(endDate));
        query.addCriteria(criteria);
        query.addCriteria(Criteria.where("area_code").is(areaCode));
        return mongoTemplate.find(query, AreaAlarmCountInfo.class);
    }

    /**
     * 保存区域历史告警数量数据
     *
     * @param areaAlarmCountInfo
     * @return
     */
    @Override
    public boolean saveAlarmCount(AreaAlarmCountInfo areaAlarmCountInfo) {
        Query query = this.buildQuery(areaAlarmCountInfo.getAreaCode(), areaAlarmCountInfo.getStatDate());
        mongoTemplate.remove(query, AreaAlarmCountInfo.class);
        areaAlarmCountInfo.setId(IdUtil.objectId());
        DateTime dateTime = new DateTime(areaAlarmCountInfo.getStatDate() + " 00:00:00", DatePattern.NORM_DATETIME_FORMAT);
        areaAlarmCountInfo.setCreateDate(dateTime);
        mongoTemplate.save(areaAlarmCountInfo);
        return true;
    }


    @Override
    public SynthInfoDTO getSynthInfo(String adcode, String companyId, String projectCode) {
        SynthInfoDTO synthInfoDTO = new SynthInfoDTO();
        Long allPersonCount = 0L;
        Long allVehicleCount = 0L;
        Long personCount = 0L;
        Long vehicleCount = 0L;
        Long facilityCount = 0L;
        Long workingPersonCount = 0L;
        Long onlinePersonCount = 0L;
        Long workingVehicleCount = 0L;
        Long onlineVehicleCount = 0L;
        Long workingFacilityCount = 0L;
        double personWorkAreaCount = 0;
        double vehicleWorkAreaCount = 0;

        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        if (ObjectUtil.isNotEmpty(adcode)) {
            query.addCriteria(Criteria.where("areaCode").is(adcode));
            synthInfoDTO.setAreaCode(adcode);
        }
        if (ObjectUtil.isNotEmpty(companyId)) {
            query.addCriteria(Criteria.where("companyId").is(companyId));
            synthInfoDTO.setCompanyId(companyId);
        }
        if (ObjectUtil.isNotEmpty(projectCode)) {
            List<String> projectCodeList = Arrays.asList(projectCode.split(","));
            query.addCriteria(Criteria.where("projectCode").in(projectCodeList));
            synthInfoDTO.setProjectCode(projectCode);
        }
        List<SynthInfoDTO> synthInfoDTOList = mongoTemplate.find(query, SynthInfoDTO.class);
        if (ObjectUtil.isNotEmpty(synthInfoDTOList) && synthInfoDTOList.size() > 0) {
            for (SynthInfoDTO synthInfoDTO1 : synthInfoDTOList) {
                allPersonCount = allPersonCount + synthInfoDTO1.getAllPersonCount();
                allVehicleCount = allVehicleCount + synthInfoDTO1.getAllVehicleCount();
                personCount = personCount + synthInfoDTO1.getPersonCount();
                vehicleCount = vehicleCount + synthInfoDTO1.getVehicleCount();
                facilityCount = facilityCount + synthInfoDTO1.getFacilityCount();
                workingPersonCount = workingPersonCount + synthInfoDTO1.getWorkingPersonCount();
                onlinePersonCount = onlinePersonCount + synthInfoDTO1.getOnlinePersonCount();

                workingVehicleCount = workingVehicleCount + synthInfoDTO1.getWorkingVehicleCount();
                onlineVehicleCount = onlineVehicleCount + synthInfoDTO1.getOnlineVehicleCount();

                workingFacilityCount = workingFacilityCount + synthInfoDTO1.getWorkingFacilityCount();
                personWorkAreaCount = personWorkAreaCount + synthInfoDTO1.getPersonWorkAreaCount();
                vehicleWorkAreaCount = vehicleWorkAreaCount + synthInfoDTO1.getVehicleWorkAreaCount();
            }
            if (synthInfoDTOList.size() == 1) {
                synthInfoDTO.setProjectName(synthInfoDTOList.get(0).getProjectName());
            }
        }

        synthInfoDTO.setAllPersonCount(allPersonCount);
        synthInfoDTO.setAllVehicleCount(allVehicleCount);
        synthInfoDTO.setOnlinePersonCount(onlinePersonCount);
        synthInfoDTO.setOnlineVehicleCount(onlineVehicleCount);
        synthInfoDTO.setFacilityCount(facilityCount);
        synthInfoDTO.setPersonCount(personCount);
        synthInfoDTO.setPersonWorkAreaCount(new BigDecimal(personWorkAreaCount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        synthInfoDTO.setVehicleCount(vehicleCount);
        synthInfoDTO.setVehicleWorkAreaCount(new BigDecimal(vehicleWorkAreaCount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        synthInfoDTO.setWorkingFacilityCount(workingFacilityCount);
        synthInfoDTO.setWorkingPersonCount(workingPersonCount);
        synthInfoDTO.setWorkingVehicleCount(workingVehicleCount);
        return synthInfoDTO;
    }

    @Override
    public List<VehicleWorkSynthInfoDTO> getRealVehicleOperationrate(String adcode, String projectCode, String today) {

        List<VehicleWorkSynthInfoDTO> vehicleWorkSynthInfoDTOList = new ArrayList<VehicleWorkSynthInfoDTO>();
        List<String> projectCodeList = Arrays.asList(projectCode.split(","));
        QueryWrapper<VehicleWorkStatResult> wrapper = new QueryWrapper<VehicleWorkStatResult>();
        List<VehicleWorkStatResult> vehicleWorkStatResults = new ArrayList<VehicleWorkStatResult>();
        Double area = 0.00;
        Double distanceArea = 0.00;
        Double operationRate = 0.00;

        String endTime = TimeUtil.getYYYYMMDDHHMMSS(new Date());
        if ("1".equals(today)) {
            //取最新的统计数据
            wrapper.clear();
            wrapper.orderByDesc("end_time");
            wrapper.lambda().in(VehicleWorkStatResult::getTenantId, projectCodeList);
            vehicleWorkStatResults = vehicleWorkStatService.list(wrapper);
            if (ObjectUtil.isNotEmpty(vehicleWorkStatResults) && vehicleWorkStatResults.size() > 0) {
                endTime = vehicleWorkStatResults.get(0).getEndTime();
                wrapper.clear();
                wrapper.lambda().eq(VehicleWorkStatResult::getEndTime, endTime);
                wrapper.lambda().in(VehicleWorkStatResult::getTenantId, projectCodeList);
                wrapper.lambda().eq(VehicleWorkStatResult::getVehicleWorktype, "1");//目前先统计机扫作业
                List<VehicleWorkStatResult> resList = vehicleWorkStatService.list(wrapper);
                if (ObjectUtil.isNotEmpty(resList) && resList.size() > 0) {
                    for (VehicleWorkStatResult result : resList) {
                        distanceArea = distanceArea + result.getRealWorkAcreage();
                        area = area + result.getWorkareaAcreage();
                    }
                }
                operationRate = new BigDecimal(distanceArea / area).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
            }
            endTime = endTime.substring(0,endTime.lastIndexOf(":"));
        } else {
            //取第二新的统计数据
            endTime = TimeUtil.getYYYY_MM_DD(TimeUtil.generateYesterday(new Date()));
            wrapper.clear();
            wrapper.lambda().eq(VehicleWorkStatResult::getStatDate, endTime);
            wrapper.lambda().in(VehicleWorkStatResult::getTenantId, projectCodeList);
            wrapper.lambda().eq(VehicleWorkStatResult::getVehicleWorktype, "1");//目前先统计机扫作业
            List<VehicleWorkStatResult> resList = vehicleWorkStatService.list(wrapper);
            if (ObjectUtil.isNotEmpty(resList) && resList.size() > 0) {
                for (VehicleWorkStatResult result : resList) {
                    distanceArea = distanceArea + result.getRealWorkAcreage();
                    area = area + result.getWorkareaAcreage();
                }
            }
            operationRate = new BigDecimal(distanceArea / area).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

        }

        VehicleWorkSynthInfoDTO vehicleWorkSynthInfoDTO = new VehicleWorkSynthInfoDTO();
        vehicleWorkSynthInfoDTO.setOperationRate(Double.toString(operationRate));
        vehicleWorkSynthInfoDTO.setWorkareaArea(Double.toString(area));
        vehicleWorkSynthInfoDTO.setDistanceArea(Double.toString(distanceArea));
        vehicleWorkSynthInfoDTO.setStatDate(endTime);
        vehicleWorkSynthInfoDTOList.add(vehicleWorkSynthInfoDTO);

        return vehicleWorkSynthInfoDTOList;
    }

    /**
     * 计算区域的告警数字
     *
     * @param areaCode
     * @param statDate
     */
    @Override
    public void statTotalAlarmCount(String areaCode, String statDate) {

    }

    /**
     * 保存项目信息
     *
     * @param projectInfo
     */
    @Override
    public void saveProjectInfo(ProjectInfo projectInfo) {
        projectInfo.setCreateDate(new Date());
        mongoTemplate.save(projectInfo);
    }

    /**
     * 根据区域查询区域信息
     *
     * @param areaCode
     * @return
     */
    @Override
    public List<ProjectInfo> listProjectInfo(String areaCode) {
        Query query = new Query();
        query.with(Sort.by(
                Sort.Order.asc("area_code")
        ));
        List<ProjectInfo> projectInfoList = null;
        if (StringUtils.isEmpty(areaCode) || areaCode.equals("100000") || areaCode.equals("000000")) {
            projectInfoList = mongoTemplate.find(query, ProjectInfo.class);
        } else {
            List<String> areaCodeList = new ArrayList<>();
            areaCodeList.add(areaCode);
            //再查询该区域是否有子区域
            R<List<AdministrativeCity>> result1 = sysClient.getAdminCityByParent(Long.valueOf(areaCode));
            if (result1.isSuccess() && result1.getData() != null) {
                List<AdministrativeCity> list1 = result1.getData();
                list1.stream().forEach(administrativeCity1 -> {
                    areaCodeList.add(String.valueOf(administrativeCity1.getId()));
                    //再遍历一遍,可能是市一级
                    R<List<AdministrativeCity>> result2 = sysClient.getAdminCityByParent(administrativeCity1.getId());
                    if (result2 != null && result2.getData() != null) {
                        List<AdministrativeCity> list2 = result2.getData();
                        list2.stream().forEach(administrativeCity2 -> {
                            areaCodeList.add(String.valueOf(administrativeCity2.getId()));
                        });
                    }
                });
                query.addCriteria(Criteria.where("area_code").in(areaCodeList));
                projectInfoList = mongoTemplate.find(query, ProjectInfo.class);
            }

        }
        return projectInfoList;
    }

    /**
     * 根据项目编码查询设备在线率
     *
     * @param projectCode 多个设备编码用逗号分隔
     * @return
     */
    @Override
    public List<DeviceOnlineInfoDTO> listDeviceOnlineInfo(String adcode, String projectCode) {
        List<DeviceOnlineInfoDTO> list = null;
        List<String> projectCodeList = null;
        if (StringUtils.isNotEmpty(projectCode)) {
            projectCodeList = Func.toStrList(projectCode);
        } else if (StringUtils.isNotEmpty(adcode)) {
            R<List<Project>> projectListResult = projectClient.getProjectByAdcode(Long.valueOf(adcode));
            if (projectListResult.isSuccess() && projectListResult.getData() != null) {
                projectCodeList = projectListResult.getData().stream().map(project -> {
                    return project.getProjectCode();
                }).collect(Collectors.toList());
            }
        }
        if (CollUtil.isNotEmpty(projectCodeList)) {
            R<List<PersonDeviceStatusCountDTO>> personResult = personClient.listPersonDeviceStatusStat(projectCode);
            R<List<VehicleDeviceStatusCountDTO>> vehicleResult = vehicleClient.listVehicleDeviceStatusStat(projectCode);
            Map<String, PersonDeviceStatusCountDTO> personMap = new HashMap<String, PersonDeviceStatusCountDTO>();
            Map<String, VehicleDeviceStatusCountDTO> vehicleMap = new HashMap<String, VehicleDeviceStatusCountDTO>();
            if (personResult.isSuccess() && personResult.getData() != null) {
                List<PersonDeviceStatusCountDTO> personDeviceStatusCountDTOList = personResult.getData();
                for (PersonDeviceStatusCountDTO personDeviceStatusCountDTO : personDeviceStatusCountDTOList) {
                    personMap.put(personDeviceStatusCountDTO.getProjectCode(), personDeviceStatusCountDTO);
                }
            }
            if (vehicleResult.isSuccess() && vehicleResult.getData() != null) {
                List<VehicleDeviceStatusCountDTO> vehicleDeviceStatusCountDTOList = vehicleResult.getData();
                for (VehicleDeviceStatusCountDTO vehicleDeviceStatusCountDTO : vehicleDeviceStatusCountDTOList) {
                    vehicleMap.put(vehicleDeviceStatusCountDTO.getProjectCode(), vehicleDeviceStatusCountDTO);
                }
            }
            list = projectCodeList.stream().map(projectCodeStr -> {
                Long personOnlineCount = personMap.get(projectCodeStr) == null ? 0L : personMap.get(projectCodeStr).getOnPersonCount();
                Long vehicleOnlineCount = vehicleMap.get(projectCodeStr) == null ? 0L : vehicleMap.get(projectCodeStr).getOnVehicleCount();
                DeviceOnlineInfoDTO deviceOnlineDTO = new DeviceOnlineInfoDTO();
                deviceOnlineDTO.setPersonDeviceOnline(personOnlineCount);
                deviceOnlineDTO.setVehicleDeviceOnline(vehicleOnlineCount);
                deviceOnlineDTO.setProjectCode(projectCodeStr);
                deviceOnlineDTO.setProjectName(ProjectCache.getProjectNameByCode(projectCodeStr));
                return deviceOnlineDTO;
            }).collect(Collectors.toList());
        }
        return list;
    }
}
