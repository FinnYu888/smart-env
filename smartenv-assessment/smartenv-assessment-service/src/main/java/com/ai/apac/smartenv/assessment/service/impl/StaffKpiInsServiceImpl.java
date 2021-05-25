/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.assessment.service.impl;

import com.ai.apac.smartenv.assessment.dto.*;
import com.ai.apac.smartenv.assessment.entity.KpiTarget;
import com.ai.apac.smartenv.assessment.entity.KpiTplDetail;
import com.ai.apac.smartenv.assessment.entity.StaffKpiIns;
import com.ai.apac.smartenv.assessment.entity.StaffKpiInsDetail;
import com.ai.apac.smartenv.assessment.service.IKpiTargetService;
import com.ai.apac.smartenv.assessment.service.IKpiTplDefService;
import com.ai.apac.smartenv.assessment.service.IStaffKpiInsDetailService;
import com.ai.apac.smartenv.assessment.vo.KpiTargetVO;
import com.ai.apac.smartenv.assessment.vo.StaffKpiInsVO;
import com.ai.apac.smartenv.assessment.mapper.StaffKpiInsMapper;
import com.ai.apac.smartenv.assessment.service.IStaffKpiInsService;
import com.ai.apac.smartenv.assessment.wrapper.KpiTargetWrapper;
import com.ai.apac.smartenv.assessment.wrapper.StaffKpiInsWrapper;
import com.ai.apac.smartenv.common.constant.AssessmentConstant;
import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.feign.IPersonUserRelClient;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 考核实例表，存放每个人的kpi 服务实现类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Service
@AllArgsConstructor
public class StaffKpiInsServiceImpl extends BaseServiceImpl<StaffKpiInsMapper, StaffKpiIns> implements IStaffKpiInsService {

    private IStaffKpiInsDetailService staffKpiInsDetailService;

    private IPersonClient personClient;

    private IPersonUserRelClient personUserRelClient;


    private MongoTemplate mongoTemplate;


    @Override
    public IPage<StaffKpiInsVO> selectStaffKpiInsPage(IPage<StaffKpiIns> page, StaffKpiInsQueryDTO staffKpiInsQueryDTO) {
        Map<Long, Person> personMap = new HashMap<Long, Person>();
        if(!ObjectUtil.isNotEmpty(staffKpiInsQueryDTO.getFlag())){
            staffKpiInsQueryDTO.setFlag(false);
        }
        QueryWrapper<StaffKpiIns> queryWrapper = generateQueryWrapper(staffKpiInsQueryDTO);

        IPage<StaffKpiIns> staffKpiInsRes = baseMapper.selectPage(page, queryWrapper);
        List<StaffKpiIns> staffKpiInsList = staffKpiInsRes.getRecords();
        IPage<StaffKpiInsVO> staffKpiInsVOPage = new Page<StaffKpiInsVO>(staffKpiInsRes.getCurrent(), staffKpiInsRes.getSize(), staffKpiInsRes.getTotal());
        List<StaffKpiInsVO> staffKpiInsVOList = new ArrayList<StaffKpiInsVO>();

        if (staffKpiInsList.size() > 0) {
            staffKpiInsList.forEach(staffKpiIns -> {
            			//要么查全部，要么查已考核的
                        if (!staffKpiInsQueryDTO.getFlag() || (staffKpiInsQueryDTO.getFlag() && ObjectUtil.isNotEmpty(staffKpiIns.getTotalScore()) && !"0".equals(staffKpiIns.getTotalScore()))) {

                            StaffKpiInsVO staffKpiInsVO_ = Objects.requireNonNull(BeanUtil.copy(staffKpiIns, StaffKpiInsVO.class));
                            Person person = personClient.getPerson(staffKpiIns.getStaffId()).getData();
                            if (ObjectUtil.isNotEmpty(person) && ObjectUtil.isNotEmpty(person.getId())
                                   && ObjectUtil.isNotEmpty(person.getIsIncumbency()) && person.getIsIncumbency() == PersonConstant.IncumbencyStatus.IN) {
                                staffKpiInsVO_.setStatusName(DictCache.getValue("target_status", staffKpiInsVO_.getStatus()));
                                staffKpiInsVO_.setStationName(StationCache.getStationName(person.getPersonPositionId()));
                                staffKpiInsVO_.setDeptName(DeptCache.getDeptName(person.getPersonDeptId().toString()));
                                staffKpiInsVO_.setStaffName(person.getPersonName());
                                Person scorer  = personClient.getPerson(staffKpiIns.getScorer()).getData();
                                if(ObjectUtil.isEmpty(scorer) || ObjectUtil.isEmpty(scorer.getPersonName())){
                                    PersonUserRel rel =  personUserRelClient.getRelByUserId(staffKpiIns.getScorer()).getData();
                                    if(!ObjectUtil.isEmpty(rel) && !ObjectUtil.isEmpty(rel.getPersonId())){
                                        scorer  = personClient.getPerson(rel.getPersonId()).getData();
                                    }
                                }
                                staffKpiInsVO_.setScorerName(scorer.getPersonName()+"("+scorer.getJobNumber()+")");
                                QueryWrapper<StaffKpiInsDetail> queryWrapper1 = new QueryWrapper<StaffKpiInsDetail>();
                                queryWrapper1.lambda().eq(StaffKpiInsDetail::getKpiInsId, staffKpiIns.getId());
                                List<StaffKpiInsDetail> staffKpiInsDetailList = staffKpiInsDetailService.list(queryWrapper1);
                                Map<String, StaffKpiInsDetail> Map = new HashMap<String, StaffKpiInsDetail>();
                                staffKpiInsDetailList.forEach(staffKpiInsDetail -> {
                                    Map.put(staffKpiInsDetail.getKpiTplDetailId().toString(), staffKpiInsDetail);
                                });

                                Query query = new Query();
                                query.addCriteria(Criteria.where("kpiTargetId").is(staffKpiIns.getKpiTargetId()))
                                        .addCriteria(Criteria.where("tenantId").is(AuthUtil.getTenantId()));
                                KpiInsMongoDBDTO kpiInsMongoDBDTO = mongoTemplate.findOne(query, KpiInsMongoDBDTO.class, "kpiIns");
                                staffKpiInsVO_.setMaxScore(kpiInsMongoDBDTO.getTotalScore());
                                staffKpiInsVO_.setScoreType(kpiInsMongoDBDTO.getScoreType());
                                staffKpiInsVO_.setScoreTypeName(kpiInsMongoDBDTO.getScoreTypeName());
                                List<KpiInsDetailsMongoDBDTO> kpiInsDetailsMongoDBDTOList = kpiInsMongoDBDTO.getKpiInsDetailsMongoDBDTOList();
                                if(ObjectUtil.isNotEmpty(kpiInsDetailsMongoDBDTOList) && kpiInsDetailsMongoDBDTOList.size()>0){
                                    kpiInsDetailsMongoDBDTOList.forEach(kpiInsDetailsMongoDBDTO -> {
                                        List<SingleKpiInsDetailsMongoDBDTO> singleKpiInsDetailsMongoDBDTOS = kpiInsDetailsMongoDBDTO.getSingleKpiInsDetailsMongoDBDTOList();
                                        singleKpiInsDetailsMongoDBDTOS.forEach(singleKpiInsDetailsMongoDBDTO -> {
                                            StaffKpiInsDetail staffKpiInsDetail = Map.get(singleKpiInsDetailsMongoDBDTO.getKpiTplDetailId().toString());
                                            singleKpiInsDetailsMongoDBDTO.setScore(staffKpiInsDetail.getScore());
                                            singleKpiInsDetailsMongoDBDTO.setStaffRemark(staffKpiInsDetail.getStaffRemark());
                                            singleKpiInsDetailsMongoDBDTO.setManagerRemark(staffKpiInsDetail.getManagerRemark());
                                        });
                                    });
                                    staffKpiInsVO_.setKpiInsDetailsMongoDBDTOList(kpiInsDetailsMongoDBDTOList);
                                }
                                staffKpiInsVOList.add(staffKpiInsVO_);
                            }
                        }
                    }
            );
        }
        staffKpiInsVOPage.setRecords(staffKpiInsVOList);
        return staffKpiInsVOPage;
    }

    @Override
    public List<StaffKpiInsModel> selectStaffKpiIns(StaffKpiInsQueryDTO staffKpiInsQueryDTO) {
        QueryWrapper<StaffKpiIns> queryWrapper = generateQueryWrapper(staffKpiInsQueryDTO);
        List<StaffKpiIns> staffKpiInsList = this.list(queryWrapper);
        List<StaffKpiInsModel> staffKpiInsModelList = new ArrayList<StaffKpiInsModel>();
        if (staffKpiInsList.size() > 0) {
            staffKpiInsList.forEach(staffKpiIns -> {
                        if (ObjectUtil.isNotEmpty(staffKpiIns.getTotalScore()) && !"0".equals(staffKpiIns.getTotalScore())) {
                            StaffKpiInsModel staffKpiInsModel = Objects.requireNonNull(BeanUtil.copy(staffKpiIns, StaffKpiInsModel.class));
                            Person person = personClient.getPerson(staffKpiIns.getStaffId()).getData();
                            if (ObjectUtil.isNotEmpty(person) || ObjectUtil.isNotEmpty(person.getId())) {
                                staffKpiInsModel.setStatusName(DictBizCache.getValue("target_status", staffKpiIns.getStatus()));
                                staffKpiInsModel.setStationName(StationCache.getStationName(person.getPersonPositionId()));
                                staffKpiInsModel.setDeptName(DeptCache.getDeptName(person.getPersonDeptId().toString()));
                                staffKpiInsModel.setStaffName(person.getPersonName() + "(" + person.getJobNumber() + ")");
                                staffKpiInsModel.setStartTime(TimeUtil.getYYYYMMDD(staffKpiIns.getStartTime()));
                                staffKpiInsModel.setEndTime(TimeUtil.getYYYYMMDD(staffKpiIns.getEndTime()));
                            }
                            staffKpiInsModelList.add(staffKpiInsModel);
                        }
                    }
            );
        }
        return staffKpiInsModelList;
    }

    private QueryWrapper<StaffKpiIns> generateQueryWrapper(StaffKpiInsQueryDTO staffKpiInsQueryDTO) {
        List<Long> personIdList = new ArrayList<Long>();
        QueryWrapper<StaffKpiIns> queryWrapper = new QueryWrapper<StaffKpiIns>();
        if (ObjectUtil.isNotEmpty(staffKpiInsQueryDTO.getStartTime())) {
            queryWrapper.lambda().ge(StaffKpiIns::getStartTime, new Timestamp(staffKpiInsQueryDTO.getStartTime()));
        }
        if (ObjectUtil.isNotEmpty(staffKpiInsQueryDTO.getEndTime())) {
            queryWrapper.lambda().le(StaffKpiIns::getEndTime, new Timestamp(staffKpiInsQueryDTO.getEndTime()));
        }
        if (ObjectUtil.isNotEmpty(staffKpiInsQueryDTO.getStatus())) {
            queryWrapper.lambda().eq(StaffKpiIns::getStatus, staffKpiInsQueryDTO.getStatus());
        }

        if (ObjectUtil.isNotEmpty(staffKpiInsQueryDTO.getName())
                || ObjectUtil.isNotEmpty(staffKpiInsQueryDTO.getStationId())
                || ObjectUtil.isNotEmpty(staffKpiInsQueryDTO.getDeptId())) {
            String name = ObjectUtil.isNotEmpty(staffKpiInsQueryDTO.getName()) ? staffKpiInsQueryDTO.getName() : "";
            String stationId = ObjectUtil.isNotEmpty(staffKpiInsQueryDTO.getStationId()) ? staffKpiInsQueryDTO.getStationId().toString() : "";
            String deptId = ObjectUtil.isNotEmpty(staffKpiInsQueryDTO.getDeptId()) ? staffKpiInsQueryDTO.getDeptId(): "";
            if(ObjectUtil.isNotEmpty(deptId)){
                if(DeptCache.getDept(Long.parseLong(deptId)).getParentId() == 0l){
                    deptId = "";
                }
            }
            List<Person> personList = personClient.getAssignPerson(name, stationId, deptId).getData();
            if (ObjectUtil.isNotEmpty(personList) && personList.size() > 0) {
                personList.forEach(person -> {
                    personIdList.add(person.getId());
                });
            } else {
                personIdList.add(-1L);
            }
            queryWrapper.lambda().in(StaffKpiIns::getStaffId, personIdList);
        }else{
            List<Person> activePersonList = PersonCache.getActivePerson(AuthUtil.getTenantId());
            List<Long> personIdList_ = activePersonList.stream().map(Person::getId).collect(Collectors.toList());
            queryWrapper.lambda().in(StaffKpiIns::getStaffId, personIdList_);
        }

        if (staffKpiInsQueryDTO.getFlag()) {
            queryWrapper.lambda().isNotNull(StaffKpiIns::getTotalScore);
        }
        queryWrapper.lambda().eq(StaffKpiIns::getTenantId,AuthUtil.getTenantId());
            return queryWrapper;
    }

    @Override
    public boolean updateStaffKpiInsScore(StaffKpiInsDTO staffKpiInsDTO) {
        if (staffKpiInsDTO.getStatus() != AssessmentConstant.TargetStatus.TO_SCORE &&
                staffKpiInsDTO.getStatus() != AssessmentConstant.TargetStatus.SCORED) {
            throw new ServiceException("此考核对象暂不能打分");
        }
        StaffKpiIns staffKpiIns = Objects.requireNonNull(BeanUtil.copy(staffKpiInsDTO, StaffKpiIns.class));
        staffKpiIns.setScorer(AuthUtil.getUserId());
        staffKpiIns.setScoreTime(TimeUtil.getSysDate());
        staffKpiIns.setStatus(AssessmentConstant.TargetStatus.SCORED);
        this.updateById(staffKpiIns);

        List<StaffKpiInsDetail> staffKpiInsDetailList = staffKpiInsDTO.getStaffKpiInsDetailList();
        staffKpiInsDetailList.forEach(staffKpiInsDetail -> {
            staffKpiInsDetail.setStatus(AssessmentConstant.TargetStatus.SCORED);
            QueryWrapper<StaffKpiInsDetail> queryWrapper = new QueryWrapper<StaffKpiInsDetail>();
            queryWrapper.lambda().eq(StaffKpiInsDetail::getKpiInsId, staffKpiInsDTO.getId()).eq(StaffKpiInsDetail::getKpiTplDetailId, staffKpiInsDetail.getKpiTplDetailId());
            staffKpiInsDetailService.update(staffKpiInsDetail, queryWrapper);
        });

        return true;
    }

    @Override
    public String gradeIns(String scoreType, String score) {

        String value = DictCache.getValue("score_type", scoreType);

        JSONObject json = JSONObject.parseObject(value);

        JSONArray levels = json.getJSONArray("bandLevels");

        for (Object obj : levels) {
            JSONObject obj_ = (JSONObject) obj;
            if (obj_.getInteger("minScore") <= Double.parseDouble(score) && Double.parseDouble(score) <= obj_.getInteger("maxScore")) {
                return obj_.getString("bandLevel");
            }
        }
        ;
        return null;

    }

    @Override
    public boolean saveBatchStaffKpiIns(List<StaffKpiIns> staffKpiInsList) {
        staffKpiInsList.forEach(staffKpiIns -> {
            save(staffKpiIns);
        });
        return true;
    }

    @Override
    public List<StaffKpiIns> getStaffKpiInsList(StaffKpiIns staffKpiIns) {
        QueryWrapper<StaffKpiIns> queryWrapper = new QueryWrapper<StaffKpiIns>();
        if (ObjectUtil.isNotEmpty(staffKpiIns.getKpiTargetId())) {
            queryWrapper.lambda().eq(StaffKpiIns::getKpiTargetId, staffKpiIns.getKpiTargetId());
        }
        if (ObjectUtil.isNotEmpty(staffKpiIns.getTenantId())) {
            queryWrapper.lambda().eq(StaffKpiIns::getTenantId, staffKpiIns.getTenantId());
        }
        return list(queryWrapper);
    }


}
