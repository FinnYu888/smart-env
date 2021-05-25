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

import cn.hutool.core.util.StrUtil;
import com.ai.apac.smartenv.assessment.cache.AssessmentCache;
import com.ai.apac.smartenv.assessment.dto.*;
import com.ai.apac.smartenv.assessment.entity.*;
import com.ai.apac.smartenv.assessment.service.*;
import com.ai.apac.smartenv.assessment.vo.KpiTargetVO;
import com.ai.apac.smartenv.assessment.mapper.KpiTargetMapper;
import com.ai.apac.smartenv.assessment.vo.StaffKpiInsVO;
import com.ai.apac.smartenv.assessment.wrapper.KpiTargetWrapper;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.common.constant.AssessmentConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.sql.Timestamp;
import java.util.*;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-03-02
 */
@Service
@AllArgsConstructor
public class KpiTargetServiceImpl extends BaseServiceImpl<KpiTargetMapper, KpiTarget> implements IKpiTargetService {

	private IKpiTargetDetailService kpiTargetDetailService;

	private IStaffKpiInsService staffKpiInsService;

	private IStaffKpiInsDetailService staffKpiInsDetailService;

	private IKpiTplDetailService kpiTplDetailService;

	private IKpiTplDefService kpiTplDefService;

	private IKpiDefService kpiDefService;

	private IKpiCatalogService kpiCatalogService;

	private MongoTemplate mongoTemplate;

	@Override
	public IPage<KpiTargetVO> selectKpiTargetPage(IPage<KpiTarget> page, KpiTargetQueryDTO kpiTargetQueryDTO) {
		QueryWrapper<KpiTarget> queryWrapper = new QueryWrapper<KpiTarget>();
		if(ObjectUtil.isNotEmpty(kpiTargetQueryDTO.getStartTime())){
			queryWrapper.lambda().ge(KpiTarget::getStartTime,new Timestamp(kpiTargetQueryDTO.getStartTime()));
		}
		if(ObjectUtil.isNotEmpty(kpiTargetQueryDTO.getEndTime())){
			queryWrapper.lambda().le(KpiTarget::getEndTime,new Timestamp(kpiTargetQueryDTO.getEndTime()));
		}
		if(ObjectUtil.isNotEmpty(kpiTargetQueryDTO.getStationId())){
			queryWrapper.lambda().eq(KpiTarget::getStationId,kpiTargetQueryDTO.getStationId());
		}
		if(ObjectUtil.isNotEmpty(kpiTargetQueryDTO.getStatus())){
			queryWrapper.lambda().eq(KpiTarget::getStatus,kpiTargetQueryDTO.getStatus());
		}
        if(ObjectUtil.isNotEmpty(kpiTargetQueryDTO.getName())){
            queryWrapper.lambda().like(KpiTarget::getTargetName,kpiTargetQueryDTO.getName());
        }
		IPage<KpiTarget> kpiTargetRes = baseMapper.selectPage(page, queryWrapper);
		List<KpiTarget> kpiTargetList = kpiTargetRes.getRecords();

        IPage<KpiTargetVO> kpiTargetVOIPage = new Page<KpiTargetVO>(kpiTargetRes.getCurrent(), kpiTargetRes.getSize(), kpiTargetRes.getTotal());
        List<KpiTargetVO> kpiTargetVOList= new ArrayList<KpiTargetVO>();

        if(kpiTargetList.size() > 0){
            kpiTargetList.forEach(kpiTarget_ -> {
                org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
                query.addCriteria(Criteria.where("kpiTargetId").is(kpiTarget_.getId()))
                        .addCriteria(Criteria.where("tenantId").is(kpiTarget_.getTenantId()));
                KpiInsMongoDBDTO kpiIns = mongoTemplate.findOne(query,KpiInsMongoDBDTO.class,"kpiIns");
                KpiTargetVO kpiTargetVO = Objects.requireNonNull(BeanUtil.copy(kpiTarget_, KpiTargetVO.class));
                kpiTargetVO.setKpiTplName(kpiIns.getKpiTplName());
                kpiTargetVO.setStatusName(DictCache.getValue("target_status",kpiTarget_.getStatus()));
                kpiTargetVO.setStationName(StationCache.getStationName(Long.parseLong(kpiTarget_.getStationId())));
                kpiTargetVOList.add(kpiTargetVO);
			});
		}
        kpiTargetVOIPage.setRecords(kpiTargetVOList);
		return kpiTargetVOIPage;
	}

	private boolean checkKpiTarget(KpiTargetDTO kpiTargetDTO){
		Date currtStartTime = kpiTargetDTO.getStartTime();
		Date currtEndTime = kpiTargetDTO.getEndTime();
		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(AssessmentConstant.TargetStatus.END);
		//校验同一个人在某一时间段内只能存在一类考核
		QueryWrapper<KpiTargetDetail> queryWrapper = new QueryWrapper<KpiTargetDetail>();
		queryWrapper.lambda().in(KpiTargetDetail::getStaffId,kpiTargetDTO.getStaffIdList());
		queryWrapper.lambda().notIn(KpiTargetDetail::getStatus,statusList);
		List<KpiTargetDetail> kpiTargetDetailList = kpiTargetDetailService.list(queryWrapper);
		if(kpiTargetDetailList.size() > 0 ){
			kpiTargetDetailList.forEach(kpiTargetDetail -> {
			KpiTarget kpiTarget = this.getById(kpiTargetDetail.getTargetId());
			Date startTime = kpiTarget.getStartTime();
			Date endTime = kpiTarget.getEndTime();
			if((!currtStartTime.after(startTime) && !currtEndTime.before(startTime)) ||
					(!currtStartTime.before(startTime) && !currtStartTime.after(endTime))
			){
				throw new ServiceException(StrUtil.format("该时间段内，员工{} 的此项考核正进行中....不能重复考核", PersonCache.getPersonById(AuthUtil.getTenantId(),kpiTargetDetail.getStaffId()).getPersonName()));
			}

		});}
		return true;
	}

	@Override
	public boolean saveOrUpdateKpiTarget(KpiTargetDTO kpiTargetDTO) {
		checkKpiTarget(kpiTargetDTO);
		KpiTarget kpiTarget =  Objects.requireNonNull(BeanUtil.copy(kpiTargetDTO, KpiTarget.class));
		if(ObjectUtil.isNotEmpty(kpiTargetDTO.getId())){
			//更新

		}else{
			//新增
			/**
			 * 判断考核目标的初始状态
			 */
			Date today = TimeUtil.formDateToTimestamp(TimeUtil.getStartTime(new Date()));
			Integer toDeadDays = Integer.parseInt(DictCache.getValue("KPI_TARGET_LINE",1));
			Integer status = AssessmentConstant.TargetStatus.TO_START;
			Date startTime = kpiTargetDTO.getStartTime();
			Date endTime = kpiTargetDTO.getEndTime();
			//开始时间是过去某一天
			if(!startTime.after(today)){
				status = AssessmentConstant.TargetStatus.STARTED;
				//结束时间是过去某一天
				if(endTime.before(today)){
					status = AssessmentConstant.TargetStatus.TO_SCORE;
					//deadLine是过去某一天
					if(TimeUtil.addOrMinusDays(endTime.getTime(),toDeadDays).before(today)){
						status = AssessmentConstant.TargetStatus.END;
					}
				}
			}
			kpiTarget.setStatus(status);
			KpiInsMongoDBDTO kpiInsMongoDBDTO = new KpiInsMongoDBDTO();
			save(kpiTarget);
			Long targetId = kpiTarget.getId();
			kpiInsMongoDBDTO.setKpiTargetId(targetId);
			kpiInsMongoDBDTO.setTenantId(AuthUtil.getTenantId());
			KpiTplDef kpiTplDef = kpiTplDefService.getById(kpiTarget.getKpiTplId());
            kpiInsMongoDBDTO.setKpiTplId(kpiTarget.getKpiTplId());
            kpiInsMongoDBDTO.setKpiTplName(kpiTplDef.getKpiTplName());
			kpiInsMongoDBDTO.setScoreType(kpiTplDef.getScoreType().toString());
			JSONObject scoreTypeObj = JSONObject.parseObject(DictCache.getValue("score_type",kpiTplDef.getScoreType().toString()));
			kpiInsMongoDBDTO.setScoreTypeName(scoreTypeObj.getString("valueName"));
			kpiInsMongoDBDTO.setTotalScore(scoreTypeObj.getString("maxScore"));
			Long kpiTplId = kpiTarget.getKpiTplId();
			KpiTplDetail kpiTplDetail_ = new KpiTplDetail();
			kpiTplDetail_.setKpiTplId(kpiTplId);
			kpiTplDetail_.setTenantId(AuthUtil.getTenantId());
			List<KpiTplDetail> kpiTplDetailList = kpiTplDetailService.getKpiTplDetailList(kpiTplDetail_);
			List<String> staffIdList = kpiTargetDTO.getStaffIdList();
			List<StaffKpiInsDetail> staffKpiInsDetailList = new ArrayList<StaffKpiInsDetail>();
			if(staffIdList.size() > 0){
				List<KpiTargetDetail> kpiTargetDetailList = new ArrayList<KpiTargetDetail>();
				List<StaffKpiIns> staffKpiInsList = new ArrayList<StaffKpiIns>();
				for(String staffId:staffIdList){
					KpiTargetDetail kpiTargetDetail = new KpiTargetDetail();
					StaffKpiIns staffKpiIns = new StaffKpiIns();
					kpiTargetDetail.setTargetId(targetId);
					kpiTargetDetail.setStaffId(Long.parseLong(staffId));
					kpiTargetDetail.setStatus(status);
					kpiTargetDetailList.add(kpiTargetDetail);
					staffKpiIns.setKpiTargetName(kpiTarget.getTargetName());
					staffKpiIns.setStaffId(Long.parseLong(staffId));
					staffKpiIns.setKpiTargetId(targetId);
					staffKpiIns.setScorer(kpiTarget.getGraderId());
					staffKpiIns.setStaffId(Long.parseLong(staffId));
					staffKpiIns.setStartTime(kpiTarget.getStartTime());
					staffKpiIns.setEndTime(kpiTarget.getEndTime());
					staffKpiIns.setStatus(status);
					staffKpiIns.setDeadLine(TimeUtil.formatDateTimeToDate(TimeUtil.addOrMinusDays(kpiTarget.getEndTime().getTime(),toDeadDays)));
					staffKpiInsList.add(staffKpiIns);

				};
				kpiTargetDetailService.saveBatchKpiTargetDetail(kpiTargetDetailList);
				staffKpiInsService.saveBatchStaffKpiIns(staffKpiInsList);
			}
			StaffKpiIns staffKpiIns_ = new StaffKpiIns();
			staffKpiIns_.setKpiTargetId(targetId);
			List<StaffKpiIns> staffKpiInsList = staffKpiInsService.getStaffKpiInsList(staffKpiIns_);
			Map<String,KpiInsDetailsMongoDBDTO> Catalog2KpiInsDetailsMap = new HashMap<String,KpiInsDetailsMongoDBDTO>();
			if(kpiTplDetailList.size() > 0 ){
				for(KpiTplDetail kpiTplDetail:kpiTplDetailList){
					KpiDef kpiDef = kpiDefService.getById(kpiTplDetail.getKpiId());

					SingleKpiInsDetailsMongoDBDTO singleKpiInsDetailsMongoDBDTO = new SingleKpiInsDetailsMongoDBDTO();
					singleKpiInsDetailsMongoDBDTO.setKpiName(kpiDef.getKpiName());
					singleKpiInsDetailsMongoDBDTO.setKpiTplDetailId(kpiTplDetail.getId());
					singleKpiInsDetailsMongoDBDTO.setAppraisalCriteria(kpiDef.getAppraisalCriteria());
					singleKpiInsDetailsMongoDBDTO.setWeighting(kpiTplDetail.getWeighting());
					singleKpiInsDetailsMongoDBDTO.setKpiDesc(kpiDef.getKpiDescription());
					Long kpiCatalog = kpiDef.getKpiCatalog();
					if(!ObjectUtil.isNotEmpty(Catalog2KpiInsDetailsMap.get(kpiCatalog.toString()))){
						KpiInsDetailsMongoDBDTO kpiInsDetailsMongoDBDTO = new KpiInsDetailsMongoDBDTO();
						kpiInsDetailsMongoDBDTO.setKpiCatalogId(kpiDef.getKpiCatalog());
						kpiInsDetailsMongoDBDTO.setKpiCatalogName(kpiCatalogService.getById(kpiDef.getKpiCatalog()).getCatalogName());
						List<SingleKpiInsDetailsMongoDBDTO> singleKpiInsDetailsMongoDBDTOS = new ArrayList<SingleKpiInsDetailsMongoDBDTO>();
						singleKpiInsDetailsMongoDBDTOS.add(singleKpiInsDetailsMongoDBDTO);
						kpiInsDetailsMongoDBDTO.setSingleKpiInsDetailsMongoDBDTOList(singleKpiInsDetailsMongoDBDTOS);
						Catalog2KpiInsDetailsMap.put(kpiCatalog.toString(),kpiInsDetailsMongoDBDTO);
					}else{
						KpiInsDetailsMongoDBDTO kpiInsDetailsMongoDBDTO =  Catalog2KpiInsDetailsMap.get(kpiCatalog);
						Catalog2KpiInsDetailsMap.get(kpiCatalog.toString()).getSingleKpiInsDetailsMongoDBDTOList().add(singleKpiInsDetailsMongoDBDTO);
					}
					for(StaffKpiIns staffKpiIns:staffKpiInsList){
						StaffKpiInsDetail staffKpiInsDetail = new StaffKpiInsDetail();
						staffKpiInsDetail.setKpiTplDetailId(kpiTplDetail.getId());
						staffKpiInsDetail.setKpiInsId(staffKpiIns.getId());
						staffKpiInsDetail.setStatus(status);
						staffKpiInsDetailList.add(staffKpiInsDetail);
					};
				};
				List<KpiInsDetailsMongoDBDTO> kpiInsDetailsMongoDBDTOList = new ArrayList<KpiInsDetailsMongoDBDTO>();
				Catalog2KpiInsDetailsMap.values().forEach(kpiInsDetailsMongoDBDTO ->{
					kpiInsDetailsMongoDBDTOList.add(kpiInsDetailsMongoDBDTO);
				});
				kpiInsMongoDBDTO.setKpiInsDetailsMongoDBDTOList(kpiInsDetailsMongoDBDTOList);
				staffKpiInsDetailService.saveBatchStaffKpiInsDetail(staffKpiInsDetailList);
			}

			mongoTemplate.save(kpiInsMongoDBDTO,"kpiIns");
		}
		return true;
	}


	@Override
	public KpiTargetVO getAllKpiTargetDetail(String id) {
		KpiTarget kpiTarget = this.getById(id);
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("kpiTargetId").is(kpiTarget.getId()))
                .addCriteria(Criteria.where("tenantId").is(kpiTarget.getTenantId()));
        KpiInsMongoDBDTO kpiIns = mongoTemplate.findOne(query,KpiInsMongoDBDTO.class,"kpiIns");
		KpiTargetVO kpiTargetVO = Objects.requireNonNull(BeanUtil.copy(kpiTarget, KpiTargetVO.class));
		kpiTargetVO.setKpiTplName(kpiIns.getKpiTplName());
		kpiTargetVO.setStatusName(DictBizCache.getValue("target_status",kpiTargetVO.getStatus()));
		kpiTargetVO.setStationName(StationCache.getStationName(Long.parseLong(kpiTargetVO.getStationId())));
		QueryWrapper<KpiTargetDetail> queryWrapper = new QueryWrapper<KpiTargetDetail>();
		queryWrapper.lambda().eq(KpiTargetDetail::getTargetId,id).eq(KpiTargetDetail::getTenantId,AuthUtil.getTenantId());
		List<KpiTargetDetail> kpiTargetDetailList = kpiTargetDetailService.list(queryWrapper);
		List<String> staffList = new ArrayList<String>();
		kpiTargetDetailList.forEach(kpiTargetDetail -> {
			staffList.add(kpiTargetDetail.getStaffId().toString());
		});
		kpiTargetVO.setStaffList(staffList);
		return kpiTargetVO;
	}

	@Override
	public boolean deleteKpiTarget(List<Long> ids) {
		ids.forEach(id->{
			this.removeById(id);
			QueryWrapper<KpiTargetDetail> queryWrapper = new QueryWrapper<KpiTargetDetail>();
			queryWrapper.lambda().eq(KpiTargetDetail::getTargetId,id).eq(KpiTargetDetail::getTenantId,AuthUtil.getTenantId());
			kpiTargetDetailService.remove(queryWrapper);
			QueryWrapper<StaffKpiIns> queryWrapper_ = new QueryWrapper<StaffKpiIns>();
			queryWrapper_.lambda().eq(StaffKpiIns::getKpiTargetId,id).eq(StaffKpiIns::getTenantId,AuthUtil.getTenantId());
			List<StaffKpiIns> staffKpiInsList = staffKpiInsService.list(queryWrapper_);
			staffKpiInsList.forEach(staffKpiIns -> {
				QueryWrapper<StaffKpiInsDetail> queryWrapper__ = new QueryWrapper<StaffKpiInsDetail>();
				queryWrapper__.lambda().eq(StaffKpiInsDetail::getKpiInsId,staffKpiIns.getId()).eq(StaffKpiInsDetail::getTenantId,AuthUtil.getTenantId());
				staffKpiInsDetailService.remove(queryWrapper__);
				staffKpiInsService.removeById(staffKpiIns.getId());
			});
		});
		return true;
	}

	@Override
	public Boolean endKpiTarget() {
		Date today = TimeUtil.formDateToTimestamp(TimeUtil.getStartTime(new Date()));
		QueryWrapper<StaffKpiIns> queryWrapper1 = new QueryWrapper<>();
		queryWrapper1.lambda().eq(StaffKpiIns::getStartTime,today);
		queryWrapper1.lambda().eq(StaffKpiIns::getStatus,AssessmentConstant.TargetStatus.TO_START);
		List<StaffKpiIns> staffKpiInsList1 = staffKpiInsService.list(queryWrapper1);
		undateKpiStatus(staffKpiInsList1, AssessmentConstant.TargetStatus.STARTED);

		QueryWrapper<StaffKpiIns> queryWrapper2 = new QueryWrapper<>();
		queryWrapper2.lambda().lt(StaffKpiIns::getEndTime,today);
		queryWrapper1.lambda().eq(StaffKpiIns::getStatus,AssessmentConstant.TargetStatus.STARTED);
		List<StaffKpiIns> staffKpiInsList2 = staffKpiInsService.list(queryWrapper2);
		undateKpiStatus(staffKpiInsList2, AssessmentConstant.TargetStatus.TO_SCORE);

		QueryWrapper<StaffKpiIns> queryWrapper3 = new QueryWrapper<>();
		queryWrapper3.lambda().eq(StaffKpiIns::getDeadLine,today);
		List<StaffKpiIns> staffKpiInsList3 = staffKpiInsService.list(queryWrapper3);
		undateKpiStatus(staffKpiInsList3, AssessmentConstant.TargetStatus.END);

		return true;

	}

	private void undateKpiStatus(List<StaffKpiIns> staffKpiInsList,Integer status){
		staffKpiInsList.forEach(staffKpiIns -> {
			Long id = staffKpiIns.getKpiTargetId();
			KpiTarget kpiTarget = new KpiTarget();
			kpiTarget.setId(id);
			kpiTarget.setStatus(status);
			this.updateById(kpiTarget);

			KpiTargetDetail kpiTargetDetail = new KpiTargetDetail();
			kpiTargetDetail.setTargetId(id);
			List<KpiTargetDetail> kpiTargetDetailList = kpiTargetDetailService.list(Condition.getQueryWrapper(kpiTargetDetail));
			kpiTargetDetailList.forEach(kpiTargetDetail1 -> {
				kpiTargetDetail1.setStatus(status);
			});
			kpiTargetDetailService.updateBatchById(kpiTargetDetailList);


			staffKpiIns.setStatus(status);
			staffKpiInsService.updateById(staffKpiIns);

			StaffKpiInsDetail staffKpiInsDetail = new StaffKpiInsDetail();
			staffKpiInsDetail.setKpiInsId(staffKpiIns.getId());
			List<StaffKpiInsDetail> staffKpiInsDetailList = staffKpiInsDetailService.list(Condition.getQueryWrapper(staffKpiInsDetail));
			staffKpiInsDetailList.forEach(staffKpiInsDetail1 -> {
				staffKpiInsDetail1.setStatus(status);
			});
			staffKpiInsDetailService.updateBatchById(staffKpiInsDetailList);

		});
	}

}
