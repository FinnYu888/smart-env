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
package com.ai.apac.smartenv.event.service.impl;

import cn.hutool.json.JSONArray;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.EventConstant;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.ExcelUtil;
import com.ai.apac.smartenv.event.cache.EventCache;
import com.ai.apac.smartenv.event.dto.EventKpiDefDTO;
import com.ai.apac.smartenv.event.dto.EventKpiTplDefDTO;
import com.ai.apac.smartenv.event.dto.EventKpiTplRelDTO;
import com.ai.apac.smartenv.event.entity.*;
import com.ai.apac.smartenv.event.mapper.EventKpiDefMapper;
import com.ai.apac.smartenv.event.mapper.EventKpiTplDefMapper;
import com.ai.apac.smartenv.event.service.IEventKpiCatalogService;
import com.ai.apac.smartenv.event.service.IEventKpiDefService;
import com.ai.apac.smartenv.event.service.IEventKpiTplDefService;
import com.ai.apac.smartenv.event.service.IEventKpiTplRelService;
import com.ai.apac.smartenv.event.vo.EventKpiCatalogVO;
import com.ai.apac.smartenv.event.vo.EventKpiDefVO;
import com.ai.apac.smartenv.event.vo.EventKpiTplDefVO;
import com.ai.apac.smartenv.event.vo.EventKpiTplRelVO;
import com.ai.apac.smartenv.event.wrapper.EventKpiDefWrapper;
import com.ai.apac.smartenv.event.wrapper.EventKpiTplRelWrapper;
import com.ai.apac.smartenv.omnic.vo.BasicInfo4BSVO;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorInfoVO;
import com.ai.smartenv.cache.util.SmartCache;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.rmi.ServerException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 考核指标定义表 服务实现类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Service
public class EventKpiTplDefServiceImpl extends BaseServiceImpl<EventKpiTplDefMapper, EventKpiTplDef> implements IEventKpiTplDefService {


	@Autowired
	private IEventKpiTplRelService eventKpiTplRelService;

	@Autowired
	@Lazy
	private IEventKpiDefService eventKpiDefService;

	@Autowired
	private IEventKpiCatalogService eventKpiCatalogService;


	@Override
	public IPage<EventKpiTplDefVO> selectEventKpiTplDefPage(IPage<EventKpiTplDefVO> page, EventKpiTplDefVO eventKpiTplDef) {
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public boolean saveEventKpiTplDef(String eventKpiTplId) {

		String cacheName = CacheNames.EVENT_KPI_TPL_MAP;
		EventKpiTplDefDTO eventKpiTplDefDTO =  SmartCache.hget(cacheName,Long.parseLong(eventKpiTplId));

		EventKpiTplDef tplDef = BeanUtil.copyProperties(eventKpiTplDefDTO,EventKpiTplDef.class);

		if(ObjectUtil.isNotEmpty(getById(Long.parseLong(eventKpiTplId)))){
			updateById(tplDef);
		}else{
			tplDef.setCreateDept(Long.parseLong(AuthUtil.getDeptId()));
			tplDef.setCreateTime(new Date());
			tplDef.setCreateUser(AuthUtil.getUserId());
			save(tplDef);
		}

		//前端页面的指标列表
		List<EventKpiTplRelDTO> eventKpiTplRelDTOList =  eventKpiTplDefDTO.getEventKpiTplRelDTOList();
		if(ObjectUtil.isEmpty(eventKpiTplRelDTOList) || eventKpiTplRelDTOList.size() == 0){
			throw new ServiceException("模板未配置事件指标");
		}
		List<EventKpiTplRel> eventKpiTplRelList = eventKpiTplRelDTOList.stream().map(eventKpiTplRelDTO -> BeanUtil.copy(eventKpiTplRelDTO, EventKpiTplRel.class)).collect(Collectors.toList());



		if(eventKpiTplDefDTO.getStatus().equals(EventConstant.Event_Kpi_Tpl_Status.LIVE)){
			Long count = eventKpiTplRelList.stream().filter(eventKpiTplRel -> ObjectUtil.isEmpty(eventKpiTplRel.getThreshold()) || eventKpiTplRel.getThreshold() <= 0).count();
			if(count > 0){
				throw new ServiceException("已激活模板配置的事件指标分值不能为空");
			}
		}

		//前端页面的指标列表中所有的主键
		List<Long> relIds = eventKpiTplRelList.stream().filter(eventKpiTplRel -> ObjectUtil.isNotEmpty(eventKpiTplRel.getId()) && -1 != eventKpiTplRel.getId()).map(EventKpiTplRel::getId).collect(Collectors.toList());

		QueryWrapper<EventKpiTplRel> wrapper = new QueryWrapper<EventKpiTplRel>();
		wrapper.lambda().eq(EventKpiTplRel::getEventKpiTplId,eventKpiTplDefDTO.getId());
		if(relIds.size() > 0){
			wrapper.lambda().notIn(EventKpiTplRel::getId,relIds);
		}
		//数据库里有，前端没有的指标
		List<EventKpiTplRel> relList = eventKpiTplRelService.list(wrapper);
		//数据库里的指标ID列表
		List<Long> relIds_ = relList.stream().map(EventKpiTplRel::getId).collect(Collectors.toList());
		if(relIds_.size() > 0 ){
			eventKpiTplRelService.deleteLogic(relIds_);
		}
		eventKpiTplRelService.saveOrUpdateBatch(eventKpiTplRelList);
		delTplDefCache(eventKpiTplDefDTO.getId().toString());

		return true;
	}

	@Override
	public void updateTplRelsCache(String eventKpiTplId, List<EventKpiTplRelDTO> eventKpiTplRelDTOList) {
		/**
		 * eventKpiIds前端选中的事件指标
		 */
		List<Long> eventKpiIds = new ArrayList<Long>();
		if(ObjectUtil.isNotEmpty(eventKpiTplRelDTOList) && eventKpiTplRelDTOList.size() > 0){
			eventKpiIds = eventKpiTplRelDTOList.stream().map(EventKpiTplRelDTO::getEventKpiId).collect(Collectors.toList());
		}


		/**
		 * eventKpiTplRelDTOList__缓存里的事件指标列表
		 */
		EventKpiTplDefDTO eventKpiTplDefDTO = getTplDefCache(eventKpiTplId,null,null);
		List<EventKpiTplRelDTO> eventKpiTplRelDTOList__ = eventKpiTplDefDTO.getEventKpiTplRelDTOList();


		/**
		 * eventKpiTplRelDTOList_需求更新的指标列表
		 */
		List<EventKpiTplRelDTO> eventKpiTplRelDTOList_ = new ArrayList<EventKpiTplRelDTO>();

		if(ObjectUtil.isNotEmpty(eventKpiIds) && eventKpiIds.size() > 0){
			if(ObjectUtil.isNotEmpty(eventKpiTplRelDTOList__) && eventKpiTplRelDTOList__.size() > 0){
				for(EventKpiTplRelDTO eventKpiTplRelDTO__:eventKpiTplRelDTOList__){
					if(eventKpiIds.contains(eventKpiTplRelDTO__.getEventKpiId())){
						//eventKpiTplRelDTOList__中的指标在前端也选中了，那就保持不变
						eventKpiTplRelDTOList_.add(eventKpiTplRelDTO__);
						eventKpiIds.remove(eventKpiTplRelDTO__.getEventKpiId());
					}
				}

			}
			//将缓存里相同的事件指标ID去掉后，剩下的再保存缓存。
			for(Long eventKpiId:eventKpiIds){
				EventKpiTplRelDTO eventKpiTplRelDTO = new EventKpiTplRelDTO();
				eventKpiTplRelDTO.setEventKpiId(eventKpiId);
				eventKpiTplRelDTO.setEventKpiTplId(Long.parseLong(eventKpiTplId));
				eventKpiTplRelDTO = getEventKpiTplRelDetails(eventKpiTplRelDTO);
				eventKpiTplRelDTOList_.add(eventKpiTplRelDTO);
			}
		}

		eventKpiTplDefDTO.setEventKpiTplRelDTOList(eventKpiTplRelDTOList_);
		String cacheName = CacheNames.EVENT_KPI_TPL_MAP;
		SmartCache.hset(cacheName,eventKpiTplId,eventKpiTplDefDTO);
	}

	@Override
	public void updateTplInfoCache(EventKpiTplDefDTO eventKpiTplDefDTO) {
		EventKpiTplDefDTO defDTO = getTplDefCache(eventKpiTplDefDTO.getId().toString(),null,null);
		defDTO.setEventKpiTplDesc(eventKpiTplDefDTO.getEventKpiTplDesc());
		defDTO.setEventKpiTplName(eventKpiTplDefDTO.getEventKpiTplName());
		defDTO.setPositionId(eventKpiTplDefDTO.getPositionId());
		String cacheName = CacheNames.EVENT_KPI_TPL_MAP;
		SmartCache.hset(cacheName,eventKpiTplDefDTO.getId(),defDTO);
	}

	@Override
	public void deleteTplRelsCache(String eventKpiTplId, List<Long> eventKpiIdList) {
		String cacheName = CacheNames.EVENT_KPI_TPL_MAP;
		EventKpiTplDefDTO defDTO = SmartCache.hget(cacheName,eventKpiTplId);
		List<EventKpiTplRelDTO>  eventKpiTplRelDTOList = defDTO.getEventKpiTplRelDTOList();
		List<EventKpiTplRelDTO> newEventKpiTplRelDTOList = eventKpiTplRelDTOList.stream().filter(eventKpiTplRelDTO -> !eventKpiIdList.contains(eventKpiTplRelDTO.getEventKpiId())).collect(Collectors.toList());
		defDTO.setEventKpiTplRelDTOList(newEventKpiTplRelDTOList);
		SmartCache.hset(cacheName,defDTO.getId(),defDTO);
	}

	@Override
	public void updateTplThresholdCache(String eventKpiTplId, String eventKpiId, double threshold) {
		EventKpiTplDefDTO eventKpiTplDefDTO = getTplDefCache(eventKpiTplId,null,null);
		List<EventKpiTplRelDTO> eventKpiTplRelDTOList =  eventKpiTplDefDTO.getEventKpiTplRelDTOList();
		for(EventKpiTplRelDTO eventKpiTplRelDTO:eventKpiTplRelDTOList){
			if(eventKpiTplRelDTO.getEventKpiId().toString().equals(eventKpiId)){
				eventKpiTplRelDTO.setThreshold(threshold);
			}
		}

		String cacheName = CacheNames.EVENT_KPI_TPL_MAP;
		SmartCache.hset(cacheName,eventKpiTplId,eventKpiTplDefDTO);
	}

	@Override
	public List<EventKpiTplRelDTO> listEventKpiTplRel(Long eventKpiTplId) {
		List<EventKpiTplRelDTO> eventKpiTplRelDTOList = new ArrayList<EventKpiTplRelDTO>();
		if(ObjectUtil.isEmpty(eventKpiTplId)){
			throw new ServiceException("事件指标模板ID不能为空");
		}
		List<EventKpiTplRel> relList =  eventKpiTplRelService.list(new QueryWrapper<EventKpiTplRel>().lambda().eq(EventKpiTplRel::getEventKpiTplId,eventKpiTplId));
		if(ObjectUtil.isEmpty(relList) || relList.size() == 0){
			return eventKpiTplRelDTOList;
		}

		for(EventKpiTplRel tplRel:relList){
			EventKpiTplRelDTO eventKpiTplRelDTO = BeanUtil.copyProperties(tplRel,EventKpiTplRelDTO.class);
			eventKpiTplRelDTO = getEventKpiTplRelDetails(eventKpiTplRelDTO);
			eventKpiTplRelDTOList.add(eventKpiTplRelDTO);
		}

		return eventKpiTplRelDTOList;
	}

	private EventKpiTplRelDTO getEventKpiTplRelDetails(EventKpiTplRelDTO eventKpiTplRelDTO){
		EventKpiDefVO eventKpiDefVO = eventKpiDefService.getEventKpiDef(eventKpiTplRelDTO.getEventKpiId());
		if(ObjectUtil.isNotEmpty(eventKpiDefVO) && ObjectUtil.isNotEmpty(eventKpiDefVO.getEventKpiCatalog())){
			eventKpiTplRelDTO.setEventKpiCatalog(eventKpiDefVO.getEventKpiCatalog());
		}
		if(ObjectUtil.isNotEmpty(eventKpiDefVO) && ObjectUtil.isNotEmpty(eventKpiDefVO.getEventKpiCatalogName())){
			eventKpiTplRelDTO.setEventKpiCatalogName(eventKpiDefVO.getEventKpiCatalogName());
		}
		if(ObjectUtil.isNotEmpty(eventKpiDefVO) && ObjectUtil.isNotEmpty(eventKpiDefVO.getEventKpiCatalogLevel())){
			eventKpiTplRelDTO.setEventKpiCatalogLevel(eventKpiDefVO.getEventKpiCatalogLevel());
		}
		if(ObjectUtil.isNotEmpty(eventKpiDefVO) && ObjectUtil.isNotEmpty(eventKpiDefVO.getEventKpiName())){
			eventKpiTplRelDTO.setEventKpiName(eventKpiDefVO.getEventKpiName());
		}
		if(ObjectUtil.isNotEmpty(eventKpiDefVO) && ObjectUtil.isNotEmpty(eventKpiDefVO.getEventKpiDescription())){
			eventKpiTplRelDTO.setEventKpiDescription(eventKpiDefVO.getEventKpiDescription());
		}
		if(ObjectUtil.isNotEmpty(eventKpiDefVO) && ObjectUtil.isNotEmpty(eventKpiDefVO.getAppraisalCriteria())){
			eventKpiTplRelDTO.setAppraisalCriteria(eventKpiDefVO.getAppraisalCriteria());
		}
		if(ObjectUtil.isNotEmpty(eventKpiDefVO) && ObjectUtil.isNotEmpty(eventKpiDefVO.getHandleLimitTime())){
			eventKpiTplRelDTO.setHandleLimitTime(eventKpiDefVO.getHandleLimitTime());
		}
		if(ObjectUtil.isNotEmpty(eventKpiDefVO) && ObjectUtil.isNotEmpty(eventKpiDefVO.getHandleLimitTimeDesc())){
			eventKpiTplRelDTO.setHandleLimitTimeDesc(eventKpiDefVO.getHandleLimitTimeDesc());
		}
		return eventKpiTplRelDTO;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public void removeEventKpiTplDef(List<Long> tplIdList) {
		deleteLogic(tplIdList);
		eventKpiTplRelService.remove(new QueryWrapper<EventKpiTplRel>().lambda().in(EventKpiTplRel::getEventKpiTplId,tplIdList));
	}

	@Override
	public void updateTplDefStatus(List<Long> tplIdList,Integer status) {
		List<EventKpiTplDef> tplDefList = tplIdList.stream().map(tplId->{
			EventKpiTplDef eventKpiTplDef = new EventKpiTplDef();
			eventKpiTplDef.setStatus(status);
			eventKpiTplDef.setId(tplId);
			return eventKpiTplDef;
		}).collect(Collectors.toList());
		updateBatchById(tplDefList);
	}

	@Override
	public IPage<EventKpiTplDef> page(EventKpiTplDef eventKpiTplDef, Query query) {
		QueryWrapper<EventKpiTplDef> queryWrapper = generateQueryWrapper(eventKpiTplDef);
		return page(Condition.getPage(query), queryWrapper);
	}


	@Override
	public EventKpiTplDefDTO getTplDefDetails(String eventKpiTplId) {
		EventKpiTplDef tplDef = getById(eventKpiTplId);
		EventKpiTplDefDTO eventKpiTplDefDTO = BeanUtil.copyProperties(tplDef,EventKpiTplDefDTO.class);
		eventKpiTplDefDTO.setEventKpiTplRelDTOList(listEventKpiTplRel(Long.parseLong(eventKpiTplId)));
		return eventKpiTplDefDTO;
	}

	@Override
	public EventKpiTplDefDTO initTplDefCache(String eventKpiTplId) {
		EventKpiTplDefDTO eventKpiTplDefDTO = new EventKpiTplDefDTO();
		String cacheName = CacheNames.EVENT_KPI_TPL_MAP;
		EventKpiTplDef eventKpiTplDef = new EventKpiTplDef();
		if(ObjectUtil.isNotEmpty(eventKpiTplId) && !"-1".equals(eventKpiTplId)){
			eventKpiTplDefDTO = getTplDefDetails(eventKpiTplId);
			SmartCache.hset(cacheName,eventKpiTplId,eventKpiTplDefDTO);
		}else{
			Long tplId = IdWorker.getId(eventKpiTplDef);
			eventKpiTplDefDTO.setId(tplId);
			eventKpiTplDefDTO.setStatus(EventConstant.Event_Kpi_Tpl_Status.DRAFT);
			SmartCache.hset(cacheName,tplId.toString(),eventKpiTplDefDTO);
		}
		if(ObjectUtil.isNotEmpty(eventKpiTplDefDTO.getPositionId())){
			eventKpiTplDefDTO.setPositionName(StationCache.getStationName(eventKpiTplDefDTO.getPositionId()));
		}
		return eventKpiTplDefDTO;
	}

	@Override
	public void delTplDefCache(String eventKpiTplId) {
		String cacheName = CacheNames.EVENT_KPI_TPL_MAP;
		SmartCache.hdel(cacheName,eventKpiTplId);
	}

	@Override
	public void previewEventKpiTpl(String eventKpiTplId, HttpServletResponse response) {
		List<EventKpiTplT> eventKpiTplTList = baseMapper.listEventKpiTplT(Long.parseLong(eventKpiTplId));
		if(ObjectUtil.isEmpty(eventKpiTplTList) || eventKpiTplTList.size() == 0){
			throw new ServiceException("该考核指标模板未配置事件指标");
		}
		Map<Long,List<EventKpiTplT>> eventKpiDefMap = new HashMap<Long,List<EventKpiTplT>>();
		Map<Long,String> catalogMap = new HashMap<Long,String>();

		for(EventKpiTplT eventKpiTplT:eventKpiTplTList){
			Long parentCatalogId = eventKpiTplT.getParentId();
			if(parentCatalogId == 0L){
				parentCatalogId = eventKpiTplT.getEventKpiCatalog();
				catalogMap.put(parentCatalogId,eventKpiTplT.getCatalogName());
				eventKpiTplT.setCatalogName("");
			}else{
				catalogMap.put(parentCatalogId,eventKpiCatalogService.getById(parentCatalogId).getCatalogName());
			}
			List<EventKpiTplT> eventKpiDefList1 = eventKpiDefMap.get(parentCatalogId);
			if(ObjectUtil.isEmpty(eventKpiDefList1) || eventKpiDefList1.size() == 0){
				eventKpiDefList1 = new ArrayList<EventKpiTplT>();
				eventKpiDefList1.add(eventKpiTplT);
				eventKpiDefMap.put(parentCatalogId,eventKpiDefList1);
			}else{
				eventKpiDefList1.add(eventKpiTplT);
			}
		}

		HSSFWorkbook wb = new HSSFWorkbook();//创建工作薄

		//表头样式
		HSSFCellStyle style = ExcelUtil.createCellStyle(wb, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, "微软雅黑", true, (short) 12);

		//新建sheet
		HSSFSheet sheet1 = wb.createSheet("Sheet1");

		String[] tableHead = {"一级分类","二级分类","指标名称","评分标准","分值"};
		HSSFRow row0 = sheet1.createRow(0);
		for (int i = 0; i < tableHead.length; i++) {//每一列的标题样式
			HSSFCell cell = row0.createCell(i);
			sheet1.setColumnWidth(i, 4000); //设置每列的列宽
			cell.setCellStyle(style); //加样式
			cell.setCellValue(tableHead[i]); //往单元格里写数据
			cell.setCellType(CellType.STRING);
		}

		int col = 1;//从第1行开始

		//以下变量用来融合相同内容 的行
		int perCol = col;
		String perCatalog = "";//上一个用途类型
		String catalog = "";//当前用途类型
		int num = 1;//要融合的第一行
		boolean flag = true;//用来记录是否是第一次循环

		Set<Long> parentCatalogIdSet = eventKpiDefMap.keySet();

		for(Long parentCatalogId:parentCatalogIdSet){
			List<EventKpiTplT> eventKpiTplTList_ = eventKpiDefMap.get(parentCatalogId);
			for(EventKpiTplT eventKpiTplT:eventKpiTplTList_){
				HSSFRow rowCol = sheet1.createRow(col);

				HSSFCell cell0 = rowCol.createCell(0);
				sheet1.setColumnWidth(0, 4000); //设置每列的列宽
				cell0.setCellStyle(style); //加样式
				cell0.setCellValue(catalogMap.get(parentCatalogId)); //往单元格里写数据
				cell0.setCellType(CellType.STRING);

				catalog = eventKpiTplT.getEventKpiCatalog().toString();

				HSSFCell cell1 = rowCol.createCell(1);
				sheet1.setColumnWidth(1, 4000); //设置每列的列宽
				cell1.setCellStyle(style); //加样式
				cell1.setCellValue(eventKpiTplT.getCatalogName()); //往单元格里写数据
				cell1.setCellType(CellType.STRING);

				if(catalog.equals(perCatalog)){
					num++;
				}else if(num > 1){
					CellRangeAddress callRangeAddress = new CellRangeAddress(col-num, col-1, 1, 1);//起始行,结束行,起始列,结束列
					sheet1.addMergedRegion(callRangeAddress);
					num = 1;
				}
				HSSFCell cell2 = rowCol.createCell(2);
				sheet1.setColumnWidth(2, 4000); //设置每列的列宽
				cell2.setCellStyle(style); //加样式
				cell2.setCellValue(eventKpiTplT.getEventKpiName()); //往单元格里写数据
				cell2.setCellType(CellType.STRING);


				HSSFCell cell3 = rowCol.createCell(3);
				sheet1.setColumnWidth(3, 4000); //设置每列的列宽
				cell3.setCellStyle(style); //加样式
				cell3.setCellValue(eventKpiTplT.getAppraisalCriteria()); //往单元格里写数据
				cell3.setCellType(CellType.STRING);


				HSSFCell cell4 = rowCol.createCell(4);
				sheet1.setColumnWidth(4, 4000); //设置每列的列宽
				cell4.setCellStyle(style); //加样式
				cell4.setCellValue(eventKpiTplT.getThreshold()); //往单元格里写数据
				cell4.setCellType(CellType.STRING);

				perCatalog = catalog;
				col++;
			}

			if(num > 1){
				CellRangeAddress callRangeAddress = new CellRangeAddress(col-num, col-1, 1, 1);//起始行,结束行,起始列,结束列
				sheet1.addMergedRegion(callRangeAddress);
				num = 1;
			}

			if(perCol != col - 1) {
				CellRangeAddress callRangeAddress1 = new CellRangeAddress(perCol, col - 1, 0, 0);//起始行,结束行,起始列,结束列
				sheet1.addMergedRegion(callRangeAddress1);
			}
			perCol = col;
		}


		try {
 /*           //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
            response.setContentType("multipart/form-data");

            //2.设置文件头：最后一个参数是设置下载文件名
            response.setHeader("Content-disposition", "attachment; filename=\""
                    + encodeChineseDownloadFileName(request, fileName + ".xls") + "\"");*/

			response.setContentType("application/x-msdownload;charset=utf-8");
			response.setHeader("Content-disposition", "attachment;filename= " + URLEncoder.encode(eventKpiTplId + ".xlsx", "UTF-8"));

			//3.通过response获取OutputStream对象(out)
			OutputStream out = new BufferedOutputStream(response.getOutputStream());

			//4.写到输出流(out)中
			out.flush();
			wb.write(out);
			out.close();
		} catch (IOException e) {
			throw new ServiceException("事件指标模板预览失败。");
		}
	}

	@Override
	public EventKpiTplDefDTO getTplDefCache(String eventKpiTplId,String catalogId,String eventKpiName) {
		String cacheName = CacheNames.EVENT_KPI_TPL_MAP;
		EventKpiTplDefDTO defDTO =  SmartCache.hget(cacheName,eventKpiTplId);
		if(ObjectUtil.isEmpty(defDTO)){
			return null;
		}
		List<EventKpiTplRelDTO> relDTOList = defDTO.getEventKpiTplRelDTOList();
		if(ObjectUtil.isNotEmpty(eventKpiName)){
			if(ObjectUtil.isNotEmpty(relDTOList) && relDTOList.size() > 0){
				relDTOList = relDTOList.stream().filter(relDTO -> relDTO.getEventKpiName().contains(eventKpiName)).collect(Collectors.toList());
			}
		}
		if(ObjectUtil.isNotEmpty(catalogId)){
			List<Long> idList = new ArrayList<Long>();
			idList.add(Long.parseLong(catalogId));
			eventKpiCatalogService.getChildCatalogIdList(Long.parseLong(catalogId),idList);
			if(ObjectUtil.isNotEmpty(relDTOList) && relDTOList.size() > 0){
				relDTOList = relDTOList.stream().filter(relDTO -> idList.contains(relDTO.getEventKpiCatalog())).collect(Collectors.toList());
			}
		}

		if(ObjectUtil.isNotEmpty(defDTO.getPositionId())){
			defDTO.setPositionName(StationCache.getStationName(defDTO.getPositionId()));
		}

		defDTO.setEventKpiTplRelDTOList(relDTOList);
		return defDTO;
	}

	private boolean checkEventKpiTplDefName(EventKpiTplDef eventKpiTplDef) {
		QueryWrapper<EventKpiTplDef> queryWrapper = new QueryWrapper<>();
		if (eventKpiTplDef.getId() != null) {
			queryWrapper.lambda().notIn(EventKpiTplDef::getId, eventKpiTplDef.getId());
		}
		queryWrapper.lambda().eq(EventKpiTplDef::getEventKpiTplName, eventKpiTplDef.getEventKpiTplName());

		List<EventKpiTplDef> list = list(queryWrapper);
		if (list != null && list.size() > 0) {
			throw new ServiceException("该考核指标模板名称已存在");
		}
		return true;
	}

	private QueryWrapper<EventKpiTplDef> generateQueryWrapper(EventKpiTplDef eventKpiTplDef) {
		QueryWrapper<EventKpiTplDef> queryWrapper = new QueryWrapper<>();
		if (StringUtils.isNotBlank(eventKpiTplDef.getEventKpiTplName())) {
			queryWrapper.lambda().like(EventKpiTplDef::getEventKpiTplName, eventKpiTplDef.getEventKpiTplName());
		}
		if(ObjectUtil.isNotEmpty(eventKpiTplDef.getPositionId())){
			queryWrapper.lambda().eq(EventKpiTplDef::getPositionId,eventKpiTplDef.getPositionId());
		}
		return queryWrapper;
	}
}
