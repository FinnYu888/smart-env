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
package com.ai.apac.smartenv.facility.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.ai.apac.smartenv.common.constant.FacilityConstant;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.facility.dto.ToiletQueryDTO;
import com.ai.apac.smartenv.facility.entity.ToiletInfo;
import com.ai.apac.smartenv.facility.entity.ToiletQuota;
import com.ai.apac.smartenv.facility.service.IToiletQuotaService;
import com.ai.apac.smartenv.facility.vo.ToiletInfoVO;
import com.ai.apac.smartenv.facility.mapper.ToiletInfoMapper;
import com.ai.apac.smartenv.facility.service.IToiletInfoService;
import com.ai.apac.smartenv.facility.wrapper.ToiletInfoWrapper;
import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.feign.IMappingClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-09-16
 */
@Service
@AllArgsConstructor
public class ToiletInfoServiceImpl extends BaseServiceImpl<ToiletInfoMapper, ToiletInfo> implements IToiletInfoService {

	private ISysClient sysClient;

	private IToiletQuotaService toiletQuotaService;

	private IOssClient ossClient;

	private IMappingClient mappingClient;

	@Override
	public IPage<ToiletInfoVO> selectToiletInfoPage(IPage<ToiletInfoVO> page, ToiletInfoVO toiletInfo) {
		return page.setRecords(baseMapper.selectToiletInfoPage(page, toiletInfo));
	}

	@Override
	public ToiletInfoVO getToiletDetailsById(Long id) {
		ToiletInfoVO toiletInfoVO = getToiletViewById(id);

		int manAQuotaCount = 0;
		int manBQuotaCount = 0;
		int womanBQuotaCount = 0;
		int momQuotaCount = 0;
		int barrierFreeQuotaCount = 0;

		List<ToiletQuota> toiletQuotaList = toiletQuotaService.list(new QueryWrapper<ToiletQuota>().lambda().eq(ToiletQuota::getToiletId,id));

		if(ObjectUtil.isNotEmpty(toiletQuotaList) && toiletQuotaList.size() > 0){
			for(ToiletQuota toiletQuota:toiletQuotaList){
				switch (toiletQuota.getQuotaType()){
					case "1":
						manAQuotaCount ++;
						continue;
					case "2":
						manBQuotaCount ++;
						continue;
					case "3":
						womanBQuotaCount ++;
						continue;
					case "4":
						momQuotaCount ++;
						continue;
					case "5":
						barrierFreeQuotaCount ++;
						continue;
				}
			};
		}

		toiletInfoVO.setBarrierFreeQuotaCount(barrierFreeQuotaCount);
		toiletInfoVO.setManAQuotaCount(manAQuotaCount);
		toiletInfoVO.setManBQuotaCount(manBQuotaCount);
		toiletInfoVO.setMomQuotaCount(momQuotaCount);
		toiletInfoVO.setWomanBQuotaCount(womanBQuotaCount);

		return toiletInfoVO;

	}



	@Override
	public ToiletInfoVO getToiletViewById(Long id) {
		ToiletInfo toiletInfo = this.getById(id);
		if(ObjectUtil.isEmpty(toiletInfo)){
			throw new ServiceException("未找到公厕消息");
		}
		return getToiletInfoVO(toiletInfo);
	}

	private ToiletInfoVO getToiletInfoVO(ToiletInfo toiletInfo) {
		ToiletInfoVO toiletInfoVO = ToiletInfoWrapper.build().entityVO(toiletInfo);

		if (ObjectUtil.isNotEmpty(toiletInfoVO.getChargePersonId())) {
			Person person = PersonCache.getPersonById(toiletInfo.getTenantId(), toiletInfoVO.getChargePersonId());
			toiletInfoVO.setChargePersonName(person.getPersonName());
		}

		if (ObjectUtil.isNotEmpty(toiletInfoVO.getToiletLevel())) {
			toiletInfoVO.setToiletLevelName(DictCache.getValue("wc_level", toiletInfoVO.getToiletLevel()));
		}

		if (ObjectUtil.isNotEmpty(toiletInfoVO.getWorkStatus())) {
			toiletInfoVO.setWorkStatusName(DictCache.getValue(FacilityConstant.ToiletWorkStatus.TOILET_WORK_STATUS, toiletInfoVO.getWorkStatus()));
		}

		if (ObjectUtil.isNotEmpty(toiletInfoVO.getCompanyCode())) {
			toiletInfoVO.setCompanyName(DictBizCache.getValue(toiletInfo.getTenantId(), "company_list", toiletInfoVO.getCompanyCode()));
		}

		if (ObjectUtil.isNotEmpty(toiletInfoVO.getDeptId())) {
			// 部门名称
			Dept dept = DeptCache.getDept(toiletInfoVO.getDeptId());
			if (dept != null) {
				toiletInfoVO.setDeptName(dept.getFullName());
			}
		}

		if (ObjectUtil.isNotEmpty(toiletInfoVO.getRegionId())) {
			Region regionDto = sysClient.getRegion(toiletInfoVO.getRegionId()).getData();
			if (ObjectUtil.isNotEmpty(regionDto)) {
				toiletInfoVO.setRegionName(regionDto.getRegionName());
			}
		}

		String image = toiletInfoVO.getToiletImage();
		if (org.apache.commons.lang.StringUtils.isBlank(image)) {
			image = DictCache.getValue(VehicleConstant.DICT_DEFAULT_IMAGE, VehicleConstant.DICT_DEFAULT_IMAGE_PERSON);
		}
		toiletInfoVO.setToiletImage(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, image).getData());



		return toiletInfoVO;
	}

	@Override
	public List<ToiletInfoVO> listToiletInfosByCondition(ToiletQueryDTO queryDTO) {
		LambdaQueryWrapper<ToiletInfo> queryWrapper = new LambdaQueryWrapper<>();
		String tenantId = queryDTO.getTenantId();
		if (StringUtils.isNotBlank(tenantId)) {
			queryWrapper.eq(ToiletInfo::getTenantId, tenantId);
		}
		List<Long> levels = queryDTO.getLevels();
		if (CollectionUtil.isNotEmpty(levels)) {
			queryWrapper.in(ToiletInfo::getToiletLevel, levels.stream().map(String::valueOf).collect(Collectors.toList()));
		}
		List<Long> statuses = queryDTO.getStatuses();
		if (CollectionUtil.isNotEmpty(statuses)) {
			queryWrapper.in(ToiletInfo::getWorkStatus, statuses.stream().map(String::valueOf).collect(Collectors.toList()));
		}
		String toiletName = queryDTO.getToiletName();
		if (StringUtils.isNotBlank(toiletName)) {
			queryWrapper.like(ToiletInfo::getToiletName, toiletName);
		}
		List<Long> regionIds = queryDTO.getRegionIds();
		if (CollectionUtil.isNotEmpty(regionIds)) {
			queryWrapper.in(ToiletInfo::getRegionId, regionIds);
		}
		List<ToiletInfo> toiletInfos = this.list(queryWrapper);
		if (CollectionUtil.isEmpty(toiletInfos)) {
			return null;
		}
		List<ToiletInfoVO> toiletInfoVOList= new ArrayList<>();
		for (ToiletInfo toiletInfo : toiletInfos) {
			ToiletInfoVO toiletInfoVO = getToiletInfoVO(toiletInfo);
			toiletInfoVOList.add(toiletInfoVO);
		}
		return toiletInfoVOList;
	}

	@Override
	public Boolean thirdToiletInfoAsync(ToiletInfo toiletInfo, String actionType) {
		if(ObjectUtil.isEmpty(toiletInfo) || ObjectUtil.isEmpty(toiletInfo.getToiletCode())){
			throw new ServiceException("公厕编码不能为空");
		}
		if(OmnicConstant.ACTION_TYPE.NEW.equals(actionType)){
			this.save(toiletInfo);
			AiMapping mapping = new AiMapping();
			mapping.setThirdCode(toiletInfo.getToiletCode());
			mapping.setSscpCode(toiletInfo.getId().toString());
			mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.TOILET));
			mappingClient.saveMappingCode(mapping);
		}else{
			String code = toiletInfo.getToiletCode();
			QueryWrapper<ToiletInfo> wrapper = new QueryWrapper<ToiletInfo>();
			wrapper.lambda().eq(ToiletInfo::getToiletCode,code);
			List<ToiletInfo> toiletInfoList = this.list(wrapper);
			if(ObjectUtil.isNotEmpty(toiletInfoList) && toiletInfoList.size() > 0 ){
				this.removeById(toiletInfoList.get(0).getId());
				mappingClient.delMapping(toiletInfoList.get(0).getId().toString(),Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.TOILET));
			}
		}
		return true;
	}

}
