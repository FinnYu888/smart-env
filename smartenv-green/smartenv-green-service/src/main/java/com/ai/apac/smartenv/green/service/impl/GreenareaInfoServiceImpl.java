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
package com.ai.apac.smartenv.green.service.impl;

import com.ai.apac.smartenv.device.dto.mongo.GreenScreenDeviceDTO;
import com.ai.apac.smartenv.event.dto.mongo.GreenScreenEventDTO;
import com.ai.apac.smartenv.green.dto.GreenareaInfoDTO;
import com.ai.apac.smartenv.green.dto.GreenareaItemDTO;
import com.ai.apac.smartenv.green.dto.mongo.*;
import com.ai.apac.smartenv.green.entity.GreenareaInfo;
import com.ai.apac.smartenv.green.entity.GreenareaItem;
import com.ai.apac.smartenv.green.service.IGreenareaItemService;
import com.ai.apac.smartenv.green.vo.GreenareaInfoVO;
import com.ai.apac.smartenv.green.mapper.GreenareaInfoMapper;
import com.ai.apac.smartenv.green.service.IGreenareaInfoService;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.feign.IWorkareaNodeClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.ArrayList;
import java.util.List;

/**
 * 绿化养护信息 服务实现类
 *
 * @author Blade
 * @since 2020-07-22
 */
@Service
@AllArgsConstructor
public class GreenareaInfoServiceImpl extends BaseServiceImpl<GreenareaInfoMapper, GreenareaInfo> implements IGreenareaInfoService {

	private IGreenareaItemService greenareaItemService;

	private IWorkareaNodeClient workareaNodeClient;

	private MongoTemplate mongoTemplate;

	@Override
	public IPage<GreenareaInfoVO> selectGreenareaInfoPage(IPage<GreenareaInfoVO> page, GreenareaInfoVO greenareaInfo) {
		return page.setRecords(baseMapper.selectGreenareaInfoPage(page, greenareaInfo));
	}

	@Override
	public Boolean saveGreenareaInfo(GreenareaInfoDTO greenareaInfoDTO) {
		GreenareaInfo greenareaInfo = BeanUtil.copy(greenareaInfoDTO, GreenareaInfo.class);
		Boolean save = this.save(greenareaInfo);
		Long greenareaId =  greenareaInfo.getId();
		Integer treeNum = 0;
		if(ObjectUtil.isNotEmpty(greenareaInfoDTO.getGreenareaItemDTOList()) && greenareaInfoDTO.getGreenareaItemDTOList().size() > 0){
			for(GreenareaItemDTO greenareaItemDTO:greenareaInfoDTO.getGreenareaItemDTOList()) {
				GreenareaItem greenareaItem = BeanUtil.copy(greenareaItemDTO, GreenareaItem.class);
				greenareaItem.setGreenareaId(greenareaId);
				treeNum = treeNum + Integer.parseInt(greenareaItem.getItemCount());
				greenareaItemService.save(greenareaItem);
			}
		}

		//*****************更新绿化大屏mongo对象************************************//
		org.springframework.data.mongodb.core.query.Query mongoQuery = new org.springframework.data.mongodb.core.query.Query();
		mongoQuery.addCriteria(Criteria.where("tenantId").is(AuthUtil.getTenantId()));
		GreenScreenGreenAreasDTO greenAreaTotalDTO = mongoTemplate.findOne(mongoQuery, GreenScreenGreenAreasDTO.class,"GreenScreen_GreenAreasData");

		if(ObjectUtil.isNotEmpty(greenAreaTotalDTO)) {
			List<GreenScreenGreenAreaDTO> greenAreaList = greenAreaTotalDTO.getGreenAreaList();
			GreenScreenGreenAreaDTO greenScreenGreenAreaDTO = new GreenScreenGreenAreaDTO();
			greenScreenGreenAreaDTO.setArea(greenareaInfo.getRegionArea());
			greenScreenGreenAreaDTO.setGreenAreaId(greenareaInfo.getId().toString());
			greenScreenGreenAreaDTO.setGreenArea(greenareaInfo.getGreenareaArea());
			greenScreenGreenAreaDTO.setGreenAreaName(greenareaInfo.getRegionName());
			greenScreenGreenAreaDTO.setGreenPer(Math.ceil(100 * (Double.parseDouble(greenareaInfo.getGreenareaArea()) / Double.parseDouble(greenareaInfo.getRegionArea()))) + "");
			greenScreenGreenAreaDTO.setTreeNum(treeNum.toString());
			greenScreenGreenAreaDTO.setLawnArea(greenareaInfo.getLawnArea());
			List<WorkareaNode> workareaNodes = workareaNodeClient.queryRegionNodesList(greenareaInfo.getRegionId()).getData();
			List<GreenScreenNodeDTO> nodes = new ArrayList<GreenScreenNodeDTO>();
			if (ObjectUtil.isNotEmpty(workareaNodes) && workareaNodes.size() > 0) {
				for (WorkareaNode workareaNode : workareaNodes) {
					GreenScreenNodeDTO greenScreenNodeDTO = new GreenScreenNodeDTO();
					greenScreenNodeDTO.setLat(workareaNode.getLatitudinal());
					greenScreenNodeDTO.setLng(workareaNode.getLongitude());
					nodes.add(greenScreenNodeDTO);
				}
				greenScreenGreenAreaDTO.setNodes(nodes);
			}

			greenAreaList.add(greenScreenGreenAreaDTO);
			Update update = new Update();
			Double totalArea = Double.parseDouble(greenAreaTotalDTO.getTotalArea()) + Double.parseDouble(greenareaInfo.getRegionArea());
			Double totalGreenArea = Double.parseDouble(greenAreaTotalDTO.getTotalGreenArea()) + Double.parseDouble(greenareaInfo.getGreenareaArea());

			update.set("totalArea", Math.ceil(totalArea) + "");
			update.set("totalGreenArea", Math.ceil(totalGreenArea) + "");
			update.set("totalGreenPer", Math.ceil(100 * (totalGreenArea / totalArea)) + "");
			update.set("totalTreeNum", Integer.parseInt(greenAreaTotalDTO.getTotalTreeNum()) + treeNum + "");
			update.set("totalLawnArea", Math.ceil(Double.parseDouble(greenAreaTotalDTO.getTotalLawnArea()) + Double.parseDouble(greenareaInfo.getLawnArea())) + "");
			update.set("greenAreaList", greenAreaList);
			mongoTemplate.upsert(mongoQuery, update, "GreenScreen_GreenAreasData");
		}else{
			greenAreaTotalDTO = new GreenScreenGreenAreasDTO();
			List<GreenScreenGreenAreaDTO> greenAreaList = new ArrayList<GreenScreenGreenAreaDTO>();
			GreenScreenGreenAreaDTO greenScreenGreenAreaDTO = new GreenScreenGreenAreaDTO();
			greenScreenGreenAreaDTO.setArea(greenareaInfo.getRegionArea());
			greenScreenGreenAreaDTO.setGreenAreaId(greenareaInfo.getId().toString());
			greenScreenGreenAreaDTO.setGreenArea(greenareaInfo.getGreenareaArea());
			greenScreenGreenAreaDTO.setGreenAreaName(greenareaInfo.getRegionName());
			greenScreenGreenAreaDTO.setGreenPer(Math.ceil(100 * (Double.parseDouble(greenareaInfo.getGreenareaArea()) / Double.parseDouble(greenareaInfo.getRegionArea()))) + "");
			greenScreenGreenAreaDTO.setTreeNum(treeNum.toString());
			greenScreenGreenAreaDTO.setLawnArea(greenareaInfo.getLawnArea());
			List<WorkareaNode> workareaNodes = workareaNodeClient.queryRegionNodesList(greenareaInfo.getRegionId()).getData();
			List<GreenScreenNodeDTO> nodes = new ArrayList<GreenScreenNodeDTO>();
			if (ObjectUtil.isNotEmpty(workareaNodes) && workareaNodes.size() > 0) {
				for (WorkareaNode workareaNode : workareaNodes) {
					GreenScreenNodeDTO greenScreenNodeDTO = new GreenScreenNodeDTO();
					greenScreenNodeDTO.setLat(workareaNode.getLatitudinal());
					greenScreenNodeDTO.setLng(workareaNode.getLongitude());
					nodes.add(greenScreenNodeDTO);
				}
				greenScreenGreenAreaDTO.setNodes(nodes);
			}

			greenAreaList.add(greenScreenGreenAreaDTO);
			greenAreaTotalDTO.setGreenAreaList(greenAreaList);
			Double totalArea = Double.parseDouble(greenareaInfo.getRegionArea());
			Double totalGreenArea = Double.parseDouble(greenareaInfo.getGreenareaArea());
			Double totalLawnArea = Double.parseDouble(greenareaInfo.getLawnArea());
			greenAreaTotalDTO.setTenantId(AuthUtil.getTenantId());
			greenAreaTotalDTO.setTotalArea(totalArea+"");
			greenAreaTotalDTO.setTotalGreenArea(totalGreenArea+"");
			greenAreaTotalDTO.setTotalGreenPer(Math.ceil(100 * (totalGreenArea / totalArea)) + "");
			greenAreaTotalDTO.setTotalTreeNum(treeNum + "");
			greenAreaTotalDTO.setTotalLawnArea(totalLawnArea+"");
			mongoTemplate.save(greenAreaTotalDTO,"GreenScreen_GreenAreasData");
		}
		return save;
	}


	@Override
	public Boolean updateGreenareaInfo(GreenareaInfoDTO greenareaInfoDTO) {
		GreenareaInfo greenareaInfo = BeanUtil.copy(greenareaInfoDTO, GreenareaInfo.class);
		Boolean save = this.updateById(greenareaInfo);
		Long greenareaId =  greenareaInfo.getId();
		List<Long> itemIdList = new ArrayList<Long>();
		Integer treeNum = 0;
		if(ObjectUtil.isNotEmpty(greenareaInfoDTO.getGreenareaItemDTOList()) && greenareaInfoDTO.getGreenareaItemDTOList().size() > 0){
			for(GreenareaItemDTO greenareaItemDTO : greenareaInfoDTO.getGreenareaItemDTOList()){

				treeNum = treeNum + Integer.parseInt(greenareaItemDTO.getItemCount());

				GreenareaItem greenareaItem = BeanUtil.copy(greenareaItemDTO, GreenareaItem.class);
				greenareaItem.setGreenareaId(greenareaId);
				if(ObjectUtil.isNotEmpty(greenareaItem.getId())){
					greenareaItemService.updateById(greenareaItem);
					itemIdList.add(greenareaItem.getId());
				}else{
					greenareaItemService.save(greenareaItem);
					itemIdList.add(greenareaItem.getId());
				}
			}
		}
		QueryWrapper<GreenareaItem> wrapper = new QueryWrapper<GreenareaItem>();
		wrapper.lambda().eq(GreenareaItem::getGreenareaId,greenareaId);
		if(ObjectUtil.isNotEmpty(itemIdList) && itemIdList.size()>0){
			wrapper.lambda().notIn(GreenareaItem::getId,itemIdList);
		}
		greenareaItemService.remove(wrapper);

		//*****************更新绿化大屏mongo对象************************************//
		org.springframework.data.mongodb.core.query.Query mongoQuery = new org.springframework.data.mongodb.core.query.Query();
		mongoQuery.addCriteria(Criteria.where("tenantId").is(AuthUtil.getTenantId()));
		GreenScreenGreenAreasDTO greenAreaTotalDTO = mongoTemplate.findOne(mongoQuery, GreenScreenGreenAreasDTO.class,"GreenScreen_GreenAreasData");

		List<GreenScreenGreenAreaDTO> greenAreaList = greenAreaTotalDTO.getGreenAreaList();
		Double totalArea = Double.parseDouble(greenAreaTotalDTO.getTotalArea());
		Double totalGreenArea = Double.parseDouble(greenAreaTotalDTO.getTotalGreenArea());
		String totalTreeNum = greenAreaTotalDTO.getTotalTreeNum();
		Double totalLawnArea = Double.parseDouble(greenAreaTotalDTO.getTotalLawnArea());
		for(GreenScreenGreenAreaDTO greenScreenGreenAreaDTO:greenAreaList){
			if(greenScreenGreenAreaDTO.getGreenAreaId().equals(greenareaId.toString())){
				totalArea = totalArea + Double.parseDouble(greenareaInfo.getRegionArea()) - Double.parseDouble(greenScreenGreenAreaDTO.getGreenArea());
				greenScreenGreenAreaDTO.setArea(greenareaInfo.getRegionArea());
				greenScreenGreenAreaDTO.setGreenAreaId(greenareaInfo.getId().toString());
				totalGreenArea = totalGreenArea + Double.parseDouble(greenareaInfo.getGreenareaArea()) - Double.parseDouble(greenScreenGreenAreaDTO.getGreenArea());
				greenScreenGreenAreaDTO.setGreenArea(greenareaInfo.getGreenareaArea());
				greenScreenGreenAreaDTO.setGreenAreaName(greenareaInfo.getRegionName());
				greenScreenGreenAreaDTO.setGreenPer(Math.ceil(100*(Double.parseDouble(greenareaInfo.getGreenareaArea())/Double.parseDouble(greenareaInfo.getRegionArea())))+"");
				totalTreeNum = Integer.parseInt(totalTreeNum)+treeNum-Integer.parseInt(greenScreenGreenAreaDTO.getTreeNum())+"";
				greenScreenGreenAreaDTO.setTreeNum(treeNum.toString());
				totalLawnArea = totalLawnArea + Double.parseDouble(greenareaInfo.getLawnArea()) - Double.parseDouble(greenScreenGreenAreaDTO.getLawnArea());
				greenScreenGreenAreaDTO.setLawnArea(greenareaInfo.getLawnArea());
				List<WorkareaNode> workareaNodes = workareaNodeClient.queryRegionNodesList(greenareaInfo.getRegionId()).getData();
				List<GreenScreenNodeDTO> nodes = new ArrayList<GreenScreenNodeDTO>();
				if(ObjectUtil.isNotEmpty(workareaNodes) && workareaNodes.size() > 0 ){
					for(WorkareaNode workareaNode:workareaNodes){
						GreenScreenNodeDTO greenScreenNodeDTO = new GreenScreenNodeDTO();
						greenScreenNodeDTO.setLat(workareaNode.getLatitudinal());
						greenScreenNodeDTO.setLng(workareaNode.getLongitude());
						nodes.add(greenScreenNodeDTO);
					}
				}
				greenScreenGreenAreaDTO.setNodes(nodes);
			}}

			Update update = new Update();
			update.set("totalArea",Math.ceil(totalArea)+"");
			update.set("totalGreenArea",Math.ceil(totalGreenArea)+"");
			update.set("totalGreenPer",Math.ceil(100*(totalGreenArea/totalArea))+"");
			update.set("totalTreeNum",Integer.parseInt(greenAreaTotalDTO.getTotalTreeNum()) + treeNum+"");
			update.set("totalLawnArea",Math.ceil(Double.parseDouble(greenAreaTotalDTO.getTotalLawnArea())+Double.parseDouble(greenareaInfo.getLawnArea()))+"");
			update.set("greenAreaList",greenAreaList);
			mongoTemplate.upsert(mongoQuery,update,"GreenScreen_GreenAreasData");

			return save;
		}



	@Override
	public IPage<GreenareaInfo> pageGreenareas(GreenareaInfo greenareaInfo, Query query) {
		QueryWrapper<GreenareaInfo> queryWrapper = new QueryWrapper<GreenareaInfo>();

		if (StringUtil.isNotBlank(greenareaInfo.getRegionName())) {
			queryWrapper.lambda().like(GreenareaInfo::getRegionName, greenareaInfo.getRegionName());
		}
		return baseMapper.selectPage(Condition.getPage(query), queryWrapper);

	}

	@Override
	public Boolean removeGreenareaInfo(List<Long> ids) {
		org.springframework.data.mongodb.core.query.Query mongoQuery = new org.springframework.data.mongodb.core.query.Query();
		mongoQuery.addCriteria(Criteria.where("tenantId").is(AuthUtil.getTenantId()));
		GreenScreenGreenAreasDTO greenAreaTotalDTO = mongoTemplate.findOne(mongoQuery, GreenScreenGreenAreasDTO.class,"GreenScreen_GreenAreasData");

		List<GreenScreenGreenAreaDTO> greenAreaList = greenAreaTotalDTO.getGreenAreaList();
		List<GreenScreenGreenAreaDTO> greenAreaList_ = new ArrayList<GreenScreenGreenAreaDTO>();
		Double totalArea = Double.parseDouble(greenAreaTotalDTO.getTotalArea());
		Double totalGreenArea = Double.parseDouble(greenAreaTotalDTO.getTotalGreenArea());
		Integer totalTreeNum = Integer.parseInt(greenAreaTotalDTO.getTotalTreeNum());
		Double totalLawnArea = Double.parseDouble(greenAreaTotalDTO.getTotalLawnArea());
		for(GreenScreenGreenAreaDTO greenScreenGreenAreaDTO:greenAreaList){
			Long id = Long.parseLong(greenScreenGreenAreaDTO.getGreenAreaId());
			if(ids.contains(id)){
				totalArea = totalArea - Double.parseDouble(greenScreenGreenAreaDTO.getArea());
				totalGreenArea = totalGreenArea - Double.parseDouble(greenScreenGreenAreaDTO.getGreenArea());
				totalLawnArea = totalLawnArea - Double.parseDouble(greenScreenGreenAreaDTO.getLawnArea());
				Integer treeNum_ = 0;
				QueryWrapper<GreenareaItem> itemQueryWrapper = new QueryWrapper<GreenareaItem>();
				itemQueryWrapper.lambda().eq(GreenareaItem::getGreenareaId,id);
				List<GreenareaItem> items = greenareaItemService.list(itemQueryWrapper);
				if(ObjectUtil.isNotEmpty(items) && items.size() > 0){
					for(GreenareaItem greenareaItem:items) {
						treeNum_ = treeNum_ + Integer.parseInt(greenareaItem.getItemCount());
					}
					totalTreeNum = totalTreeNum - treeNum_;
				}
			}else{
				greenAreaList_.add(greenScreenGreenAreaDTO);
			}
		}

		Update update = new Update();
		update.set("totalArea",Math.ceil(totalArea)+"");
		update.set("totalGreenArea",Math.ceil(totalGreenArea)+"");
		update.set("totalGreenPer",Math.ceil(100*(totalGreenArea/totalArea))+"");
		update.set("totalTreeNum",totalTreeNum +"");
		update.set("totalLawnArea",Math.ceil(totalLawnArea)+"");
		update.set("greenAreaList",greenAreaList_);
		mongoTemplate.upsert(mongoQuery,update,"GreenScreen_GreenAreasData");

		Boolean remove =  this.deleteLogic(ids);

		return remove;
	}


}
