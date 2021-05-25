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
package com.ai.apac.smartenv.inventory.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.InventoryConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.inventory.cache.InventoryCache;
import com.ai.apac.smartenv.inventory.dto.ResInfoPutInStorageExcelModelDTO;
import com.ai.apac.smartenv.inventory.entity.ResInfo;
import com.ai.apac.smartenv.inventory.entity.ResInfoQuery;
import com.ai.apac.smartenv.inventory.entity.ResOperate;
import com.ai.apac.smartenv.inventory.entity.ResOrder;
import com.ai.apac.smartenv.inventory.entity.ResOrderDtl;
import com.ai.apac.smartenv.inventory.entity.ResSpec;
import com.ai.apac.smartenv.inventory.mapper.ResInfoMapper;
import com.ai.apac.smartenv.inventory.service.IResInfoService;
import com.ai.apac.smartenv.inventory.service.IResManageService;
import com.ai.apac.smartenv.inventory.service.IResOperateService;
import com.ai.apac.smartenv.inventory.service.IResOrderDtlService;
import com.ai.apac.smartenv.inventory.service.IResOrderService;
import com.ai.apac.smartenv.common.utils.ExcelUtil;
import com.ai.apac.smartenv.inventory.vo.ResInfoPutInStorageExcelResultModelVO;
import com.ai.apac.smartenv.inventory.vo.ResInfoPutInStorageVO;
import com.ai.apac.smartenv.inventory.vo.ResInfoQueryVO;
import com.ai.apac.smartenv.inventory.vo.ResInfoVO;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.entity.DictBiz;
import com.ai.smartenv.cache.util.SmartCache;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.util.NumberUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-02-25
 */
@Slf4j
@Service
@AllArgsConstructor
public class ResInfoServiceImpl extends BaseServiceImpl<ResInfoMapper, ResInfo> implements IResInfoService {
	
	private IResOperateService resOperateService;
	
	private IResManageService resManageService;
	
	private IResOrderService resOrderService;
	
	private IResOrderDtlService orderDtlService;
	
	private IPersonClient personClient;
	
	
	@Override
	public IPage<ResInfoVO> selectResInfoPage(IPage<ResInfoVO> page, ResInfoVO resInfo) {
		return page.setRecords(baseMapper.selectResInfoPage(page, resInfo));
	}

	@Override
	public IPage<ResInfoQuery> selectResInfoQueryPage(IPage<ResInfoQuery> page, QueryWrapper<ResInfoQueryVO> queryWrapper) {
		return page.setRecords(baseMapper.selectResInfoQueryPage(page,queryWrapper));
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public boolean putInStorage(ResInfoPutInStorageVO putInStorageVO) {
		List<ResInfoVO> resInfoList = putInStorageVO.getResInfoList();
		if (CollectionUtil.isEmpty(resInfoList)) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_RESINFO_ADD));
		}
		String purchasingAgent =  putInStorageVO.getPurchasingAgent();
		Date purchasingDate = putInStorageVO.getPurchasingDate();
		String storageId = putInStorageVO.getStorageId();
		String remark = putInStorageVO.getRemark();
		for (ResInfoVO resInfoVO : resInfoList)  {
			//记录入库记录
			ResOperate resOperate = new ResOperate();
			resOperate.setInventoryId(Func.toLong(storageId));
			resOperate.setResType(resInfoVO.getResType());
			resOperate.setResSpec(resInfoVO.getResSpecId());
			resOperate.setOperateType(InventoryConstant.ResBusinessType.PUTIN_STORAGE);
			resOperate.setOperateState(InventoryConstant.ResManageStatus.NORMAL);
			if (StringUtils.isNotEmpty(putInStorageVO.getPurchasingAgentId())) {
				resOperate.setPurchasingAgent(purchasingAgent+"("+putInStorageVO.getPurchasingAgentId()+")");
			}else {
				resOperate.setPurchasingAgent(purchasingAgent);
			}
			resOperate.setPurchasingDate(TimeUtil.formatDateTimeToDate(purchasingDate));
			resOperate.setResourceSource(putInStorageVO.getResourceSource());
			resOperate.setAmount(resInfoVO.getAmount());
			resOperate.setUnitPrice(resInfoVO.getUnitPrice());
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(purchasingAgent).append("于").append(TimeUtil.getYYYYMMDDHHMMSS(purchasingDate)).append("采购入库");
			resOperate.setManageStateReasonDesc(stringBuffer.toString());
			resOperate.setRemark(remark);

			//校验是否有该物资，有直接更新库存数量
			LambdaQueryWrapper<ResInfo> queryWrapper = new LambdaQueryWrapper();
			queryWrapper.eq(ResInfo::getResType,resInfoVO.getResType());
			queryWrapper.eq(ResInfo::getResSpecId,resInfoVO.getResSpecId());
			queryWrapper.eq(ResInfo::getInventoryId,storageId);
			queryWrapper.eq(ResInfo::getManageState, InventoryConstant.ResManageStatus.NORMAL);
			ResInfo existResInfoRecord = getOne(queryWrapper);
			if (null != existResInfoRecord && existResInfoRecord.getId()>0) {
				int amount = existResInfoRecord.getAmount()+resInfoVO.getAmount();
				existResInfoRecord.setAmount(amount);
				updateById(existResInfoRecord);
				//保存入库记录
				resOperate.setResourceId(existResInfoRecord.getId());
				resOperateService.save(resOperate);
				continue;
			}

			ResInfo resInfo = new ResInfo();
			BeanUtil.copy(resInfoVO,resInfo);
			resInfo.setInventoryId(storageId);
			resInfo.setManageState(InventoryConstant.ResManageStatus.NORMAL);
			save(resInfo);
			//保存入库记录
			resOperate.setResourceId(resInfo.getId());
			resOperateService.save(resOperate);
		}
		return true;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public boolean modifyStorageNumber(ResInfoVO resInfoVO, BladeUser user) {
		ResInfo originResInfo = getById(resInfoVO.getId());
		if (null == originResInfo) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_RECORD_MODIFY));
		}
		//原库存数量
		int originAmount = originResInfo.getAmount();
		//目标库存数量
		int destAmount = resInfoVO.getAmount();
		if (originAmount == destAmount) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_RESINFO_AMOUNT));
		}
		originResInfo.setAmount(destAmount);
		//记录修改记录
		ResOperate resOperate = new ResOperate();
		resOperate.setResourceId(originResInfo.getId());
		resOperate.setInventoryId(Func.toLong(originResInfo.getInventoryId()));
		resOperate.setResType(originResInfo.getResType());
		resOperate.setResSpec(originResInfo.getResSpecId());
		resOperate.setOperateType(InventoryConstant.ResBusinessType.MODIFY_STORAGE);
		resOperate.setOperateState(originResInfo.getManageState());
		resOperate.setAmount(originAmount);
		if (null != user) resOperate.setPurchasingAgent(user.getUserName()+"("+user.getAccount()+")");
		StringBuffer stringBuffer = new StringBuffer();
		try {

			stringBuffer.append("原库存:").append(originAmount).append(";新库存:").append(destAmount);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}

		resOperate.setManageStateReasonDesc(stringBuffer.toString());
		resOperate.setRemark(resInfoVO.getRemark());
		resOperateService.save(resOperate);
		//更新库存数量

		return updateById(originResInfo);
	}

	@Override
	public Boolean resDeliveryRecord(Long orderId) {
		ResOrder resOrder = resOrderService.getById(orderId);
		if ( InventoryConstant.Order_Status.FINISH != resOrder.getOrderStatus()) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_RESAPPLY_DELIEVERY));
		}
		LambdaQueryWrapper<ResOrderDtl> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ResOrderDtl::getOrderId,orderId);
		List<ResOrderDtl> orderDtls =orderDtlService.list(queryWrapper);
		if (CollectionUtil.isNotEmpty(orderDtls)) {
			orderDtls.forEach(resOrderDtl ->{
				//更新物资库存数量
				QueryWrapper qw = new QueryWrapper();
				qw.eq("res_type",resOrderDtl.getResTypeId());
				qw.eq("res_spec_id",resOrderDtl.getResSpecId());
				//todo 66578 qw.eq("inventory_id","1");

				ResInfo resInfo = getOne(qw);
				if (null == resInfo || resInfo.getAmount()<resOrderDtl.getAmount()) {
					throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_RESAPPLY_DELIEVERY));
				}
				resInfo.setAmount(resInfo.getAmount()-resOrderDtl.getAmount());
				updateById(resInfo);

				//记录出库记录
				ResOperate resOperate = new ResOperate();
				resOperate.setInventoryId(Func.toLong(resInfo.getInventoryId()));
				resOperate.setRelOrdId(orderId);
				resOperate.setResourceId(resInfo.getId());
				resOperate.setResType(resOrderDtl.getResTypeId());
				resOperate.setResSpec(resOrderDtl.getResSpecId());
				resOperate.setOperateType(InventoryConstant.ResBusinessType.RES_DELIEVERY);
				resOperate.setOperateState(InventoryConstant.ResManageStatus.NORMAL);
				resOperate.setPurchasingAgent(resOrder.getCustName());
				resOperate.setReservationRecipient(Func.toStr(resOrder.getCustId()));
				resOperate.setPurchasingDate(TimeUtil.getSysDate());
				resOperate.setAmount(resOrderDtl.getAmount());
				resOperate.setTenantId(resOrder.getTenantId());
				resOperate.setCreateUser(resInfo.getUpdateUser());
				resOperate.setUpdateUser(resInfo.getUpdateUser());
				resOperate.setCreateTime(resInfo.getUpdateTime());
				resOperate.setUpdateTime(resInfo.getUpdateTime());

				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append(resOrder.getCustName()).append("于").append(TimeUtil.getYYYYMMDDHHMMSS(TimeUtil.getSysDate())).append("领取物资");
				resOperate.setManageStateReasonDesc(stringBuffer.toString());
				resOperateService.save(resOperate);
			});
		}
		return true;
	}

	@Override
	public void downloadDynamicResImportTemplate(BladeUser user, HttpServletRequest request, HttpServletResponse response) {
		String fileName = "物资批量导入模板";
		String hintMessage = "时间格式：2020/1/1；采购人、入库仓库、物资规格直接通过单元格下拉列表选出；入库数量为整数；单价最多两位小数；\r\n导入失败的数据需要单独编辑重新导入！";
		String[] headers = new String[]{"物资来源(*)", "采购人(*)", "采购日期(*)", "入库仓库(*)", "物资规格(*)", "入库数量(*)", "单价（元）(*)", "备注"};
		Integer[] downCols = new Integer[]{0, 1, 3, 4};
		List<String[]> downData = new ArrayList<>();
		// 物资来源
		List<Dict> resourceSourceList = DictCache.getList(InventoryConstant.ResourceSource.CODE);
		String[] resourceSources = resourceSourceList.stream().map(Dict::getDictValue).toArray(String[]::new);
		downData.add(resourceSources);
		// 采购人
		List<Person> personList = PersonCache.getActivePerson(user.getTenantId());
		String[] purchasers = personList.stream().map(Person::getPersonName).toArray(String[]::new);
		downData.add(purchasers);
		// 仓库
		List<DictBiz> storageList = DictBizCache.getList(user.getTenantId(), InventoryConstant.StorageName.CODE);
		String[] storages = storageList.stream().filter(dictBiz -> !dictBiz.getDictKey().equals("0")).map(DictBiz::getDictValue).toArray(String[]::new);
		downData.add(storages);
		// 物资规格
		String[] typeSpecNames = InventoryCache.listTypeSpecNamesByTenant(user.getTenantId()).toArray(new String[0]);
		downData.add(typeSpecNames);
		ExcelUtil.createExcelTemplate(fileName, hintMessage, headers, downData, downCols, request, response);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public ResInfoPutInStorageExcelResultModelVO inventoryBatchInput(MultipartFile excel, String tenantId) throws Exception {
		ResInfoPutInStorageExcelResultModelVO result = new ResInfoPutInStorageExcelResultModelVO();
		int successCount = 0;
		int failCount = 0;
		List<ResInfoPutInStorageExcelModelDTO> failRecords = new ArrayList<>();
		List<ResInfoPutInStorageExcelModelDTO> allRecords = new ArrayList<>();
		BufferedInputStream bufferedInputStream = null;
		ResInfoPutInStorageExcelModelDTO currentModel = new ResInfoPutInStorageExcelModelDTO();
		try {
			bufferedInputStream = new BufferedInputStream(excel.getInputStream());
			List<Object> datas = EasyExcelFactory.read(bufferedInputStream, new Sheet(1, 2)); // sheet1 第三行开始是数据
			if (datas == null || datas.isEmpty()) {
				throw new ServiceException("Excel内容为空,请重新上传");
			}
			for (Object object : datas) {
				try {
					// 获取每行数据
					List<String> params = new ArrayList<>();
					for (Object o : (List<?>) object) {
						params.add(String.class.cast(o));
					}
					// 导入结果对象
					currentModel = new ResInfoPutInStorageExcelModelDTO();
					// 校验参数
					ResInfoPutInStorageVO resInfoPutInStorageVO = checkBatchInventoryInputValue(currentModel, params, tenantId);
					// 保存
					this.putInStorage(resInfoPutInStorageVO);					
					// 保存成功
					successCount++;
					currentModel.setStatus("成功");
					allRecords.add(currentModel);
				} catch (Exception e) {
					failCount++;
					currentModel.setStatus("失败");
					currentModel.setReason(e.getMessage());
					failRecords.add(currentModel);
					allRecords.add(currentModel);
				}
			}
		} catch (Exception e) {
            log.error("Excel操作异常" + e.getMessage());
		} finally {
			if (bufferedInputStream != null) {
				bufferedInputStream.close();
			}
			result.setSuccessCount(successCount);
			result.setFailCount(failCount);
			result.setFailRecords(failRecords);

			if (failCount > 0) {
				String key = CacheNames.INVENTORY_IMPORT + ":" + DateUtil.now();
				BladeRedisCache bladeRedisCache = InventoryCache.getBladeRedisCache();
				bladeRedisCache.setEx(key, allRecords, CacheNames.ExpirationTime.EXPIRATION_TIME_1HOURS);

				result.setFileKey(key);
			}
		}
		return result;
	}

	/**
	 * 批量物资导入字段校验和数据转换
	 *
	 * @param currentModel
	 * @param params
	 * @param tenantId
	 * @return
	 * @throws Exception
	 */
	private ResInfoPutInStorageVO checkBatchInventoryInputValue(ResInfoPutInStorageExcelModelDTO currentModel, List<String> params, String tenantId) throws Exception {
		ResInfoPutInStorageVO resInfoPutInStorageVO = new ResInfoPutInStorageVO();
		List<ResInfoVO> resInfoVOS = new ArrayList<>();
		ResInfoVO resInfoVO = new ResInfoVO();
		String resourceSource = null;
		if (params.size() > InventoryConstant.ResInventoryExcelImportIndex.RESOURCE_SOURECE) {
			resourceSource = params.get(InventoryConstant.ResInventoryExcelImportIndex.RESOURCE_SOURECE);
			currentModel.setResourceSource(resourceSource);
		}
		String purchasingAgent = null;
		if (params.size() > InventoryConstant.ResInventoryExcelImportIndex.PURCHASING_AGENT) {
			purchasingAgent = params.get(InventoryConstant.ResInventoryExcelImportIndex.PURCHASING_AGENT);
			currentModel.setPurchasingAgent(purchasingAgent);
		}
		String purchasingDate = null;
		if (params.size() > InventoryConstant.ResInventoryExcelImportIndex.PURCHASING_DATE) {
			purchasingDate = params.get(InventoryConstant.ResInventoryExcelImportIndex.PURCHASING_DATE);
			currentModel.setPurchasingDate(purchasingDate);
		}
		String storageName = null;
		if (params.size() > InventoryConstant.ResInventoryExcelImportIndex.STORAGE_NAME) {
			storageName = params.get(InventoryConstant.ResInventoryExcelImportIndex.STORAGE_NAME);
			currentModel.setStorageName(storageName);
		}
		String resSpecName = null;
		if (params.size() > InventoryConstant.ResInventoryExcelImportIndex.RES_SPEC_NAME) {
			resSpecName = params.get(InventoryConstant.ResInventoryExcelImportIndex.RES_SPEC_NAME);
			currentModel.setResSpecName(resSpecName);
		}
		String amount = null;
		if (params.size() > InventoryConstant.ResInventoryExcelImportIndex.AMOUNT) {
			amount = params.get(InventoryConstant.ResInventoryExcelImportIndex.AMOUNT);
			currentModel.setAmount(amount);
		}
		String unitPrice = null;
		if (params.size() > InventoryConstant.ResInventoryExcelImportIndex.UNIT_PRICE) {
			unitPrice = params.get(InventoryConstant.ResInventoryExcelImportIndex.UNIT_PRICE);
			currentModel.setUnitPrice(unitPrice);
		}
		String remark = null;
		if (params.size() > InventoryConstant.ResInventoryExcelImportIndex.REMARK) {
			remark = params.get(InventoryConstant.ResInventoryExcelImportIndex.REMARK);
			currentModel.setRemark(remark);
		}
		// 物资来源
		if (StringUtils.isNotBlank(resourceSource)) {
			List<Dict> dicts = DictCache.getList(InventoryConstant.ResourceSource.CODE);
			Map<String, String> values = dicts.stream().collect(Collectors.toMap(Dict::getDictValue, Dict::getDictKey));
			if (values.containsKey(resourceSource)) {
				resInfoPutInStorageVO.setResourceSource(values.get(resourceSource));
			} else {
				throw new ServiceException("物资来源填写不正确");				
			}
		} else {
			throw new ServiceException("物资来源不能为空");
		}
		// 采购人
		if (StringUtils.isNotBlank(purchasingAgent)) {
			PersonVO personQuery = new PersonVO();
			personQuery.setPersonName(purchasingAgent);
			personQuery.setTenantId(tenantId);
			List<Person> data = personClient.listPerson(personQuery).getData();
			if (CollectionUtils.isEmpty(data)) {
				throw new ServiceException("人员不存在");
			} else if (data.size() > 1) {
				throw new ServiceException("多人同名");
			}
			Person person = data.get(0);
			resInfoPutInStorageVO.setPurchasingAgent(person.getPersonName() + (StringUtil.isBlank(person.getJobNumber()) ? null : "(" + person.getJobNumber() + ")"));
		} else {
			throw new ServiceException("采购人不能为空");
		}
		// 采购日期
		if (StringUtils.isNotBlank(purchasingDate)) {
			try {
				DateTime dateTime = DateUtil.parseDate(purchasingDate);
				resInfoPutInStorageVO.setPurchasingDate(dateTime);
			} catch (Exception e) {
				throw new ServiceException("时间格式错误，样例：[2020/1/1]");
			}	
		} else {
			throw new ServiceException("采购日期不能为空");
		}
		// 仓库
		if (StringUtils.isNotBlank(storageName)) {
			String storageId = "";
			List<DictBiz> storageList = DictBizCache.getList(tenantId, InventoryConstant.StorageName.CODE);
			for (DictBiz storage : storageList) {
				if (storageName.equals(storage.getDictValue())) {
					storageId = storage.getDictKey();
				}
			}
			if (StrUtil.isBlank(storageId)) {
				throw new ServiceException("入库仓库输入不正确");
			}
			resInfoPutInStorageVO.setStorageId(storageId);
		} else {
			throw new ServiceException("入库仓库必填");
		}
		// 规格
		if (StrUtil.isNotBlank(resSpecName)) {
			String resSpecId = InventoryCache.getSpecIdByTenantAndName(tenantId, resSpecName);
			if (StringUtil.isBlank(resSpecId)) {
				throw new ServiceException("物资规格不存在");
			}
			String cacheName = CacheNames.INVENTORY_RES_SPEC + StringPool.COLON + tenantId;
			ResSpec resSpec = SmartCache.hget(cacheName, resSpecId);
			if (resSpec == null) {
				throw new ServiceException("物资规格不存在");
			}
			resInfoVO.setResType(resSpec.getResType());
			resInfoVO.setResSpecId(resSpec.getId());
			resInfoVOS.add(resInfoVO);
			resInfoPutInStorageVO.setResInfoList(resInfoVOS);
		} else {
			throw new ServiceException("物资规格必填");
		}
		// 数量
		if (StringUtils.isNotBlank(amount)) {
			try {
				Double aDouble = Double.valueOf(amount);
				resInfoVO.setAmount(aDouble.intValue());
			} catch (NumberFormatException e) {
				throw new ServiceException("数量需为正整数");
			}
		} else {
			throw new ServiceException("数量必填且为正整数");
		}
		// 单价
		if (StringUtils.isNotBlank(unitPrice)) {
			try {
				BigDecimal bigDecimal = new BigDecimal(unitPrice);
				double up = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				resInfoVO.setUnitPrice(String.valueOf(up));
			} catch (Exception e) {
				throw new ServiceException("单价为2位小数");
			}	
		} else {
			throw new ServiceException("单价必填且最多有2位小数");
		}
		// 备注
		if (StringUtils.isNotBlank(remark)) {
			resInfoPutInStorageVO.setRemark(remark);
		}
		return resInfoPutInStorageVO;
	}

	@Override
	public void importResultExcelDownload(String key) throws Exception {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletResponse response = requestAttributes.getResponse();
		BladeRedisCache bladeRedisCache = InventoryCache.getBladeRedisCache();
		Object object = bladeRedisCache.get(key);
		List<ResInfoPutInStorageExcelModelDTO> modelList = new ArrayList<>();
		for (Object o : (List<?>) object) {
			ResInfoPutInStorageExcelModelDTO model = BeanUtil.copy(o, ResInfoPutInStorageExcelModelDTO.class);
			modelList.add(model);
		}
		OutputStream out = null;
		try {
			response.reset(); // 清除buffer缓存
			String fileName = "物资批量导入结果";
			out = response.getOutputStream();
			response.setContentType("application/x-msdownload;charset=utf-8");
			response.setHeader("Content-disposition", "attachment;filename= " + URLEncoder.encode(fileName, "UTF-8") + ".xls");
			ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLS);
			Sheet sheet1 = new Sheet(1, 2, ResInfoPutInStorageExcelModelDTO.class);
			sheet1.setSheetName("sheet1");
			writer.write(modelList, sheet1);
			writer.finish();
		} catch (IOException e) {
			throw new ServiceException("没有查到物资导入结果数据");
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				throw new ServiceException("没有查到物资导入结果数据");
			}
		}
	}
	@Override
	@Transactional(rollbackFor=Exception.class)
	public Boolean updateResOperate(ResOperate resOperate, ResInfo resInfo,ResInfo newResInfo) {
		resOperateService.updateById(resOperate);
		if (null != resInfo) updateById(resInfo);
		if (null != newResInfo) saveOrUpdate(newResInfo);
		return true;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public Boolean deleteLogicResOperate(ResOperate resOperate, ResInfo resInfo) {
		resOperateService.deleteLogic(Func.toLongList(resOperate.getId().toString()));
		updateById(resInfo);
		return true;
	}
}
