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
package com.ai.apac.smartenv.device.controller;

import cn.hutool.core.util.StrUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.dto.SIMInfoImportResultModel;
import com.ai.apac.smartenv.device.dto.SimInfoDTO;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.SimRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.service.ISimRelService;
import com.ai.apac.smartenv.device.vo.PersonDeviceVO;
import com.ai.apac.smartenv.device.vo.SIMImportResultVO;
import com.ai.apac.smartenv.device.vo.SimRelVO;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.EntityCategoryCache;
import com.ai.apac.smartenv.system.entity.Dict;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.device.entity.SimInfo;
import com.ai.apac.smartenv.device.vo.SimInfoVO;
import com.ai.apac.smartenv.device.wrapper.SimInfoWrapper;
import com.ai.apac.smartenv.device.service.ISimInfoService;
import org.springblade.core.boot.ctrl.BladeController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ai.apac.smartenv.system.cache.EntityCategoryCache.bladeRedisCache;
import static com.ai.apac.smartenv.system.cache.EntityCategoryCache.entityCategoryClient;

/**
 * SIM卡信息 控制器
 *
 * @author Blade
 * @since 2020-05-08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/simInfo")
@Api(value = "SIM卡信息", tags = "SIM卡信息接口")
public class SimInfoController {

	private ISimInfoService simInfoService;

	private ISimRelService simRelService;

	private IDeviceClient deviceClient;

	private IOssClient ossClient;

	private BladeRedisCache bladeRedisCache;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入simInfo")
	public R<SimInfoVO> detail(SimInfo simInfo) {
		SimInfo detail = simInfoService.getOne(Condition.getQueryWrapper(simInfo));
		SimInfoVO simInfoVO = SimInfoWrapper.build().entityVO(detail);
		SimRel simRel = simRelService.selectSimRelBySimId(simInfo.getId());
		if(null != simRel && StringUtil.isNotBlank(simRel.getDeviceId())){
			DeviceInfo deviceInfo = deviceClient.getDeviceById(simRel.getDeviceId()).getData();
			if(ObjectUtil.isNotEmpty(deviceInfo)){
				simInfoVO.setDeviceCode(deviceInfo.getDeviceCode());
				simInfoVO.setDeviceEntityCategoryId(String.valueOf(deviceInfo.getEntityCategoryId()));
				simInfoVO.setDeviceEntityCategoryName(EntityCategoryCache.getCategoryNameById(deviceInfo.getEntityCategoryId()));
				simInfoVO.setDeviceType(deviceInfo.getDeviceType());
			}
		}
		return R.data(SimInfoWrapper.build().entityVO(detail));
	}

	/**
	 * 查询未被使用的SIM卡信息
	 */
	@GetMapping("/unBindSim")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "查询未被使用的SIM卡信息", notes = "查询未被使用的SIM卡信息")
	public R<List<SimInfo>> listUnBindSim(SimInfo simInfo) {
		return R.data(simInfoService.listUnBindSim(simInfo));
	}


	/**
	 * 自定义分页 SIM卡信息
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入simInfo")
	public R<IPage<SimInfoVO>> page(SimInfo simInfo, Query query) {
		IPage<SimInfo> pages = simInfoService.selectSimInfoPage(query, simInfo);
		List<SimInfo> simInfoList =  pages.getRecords();
		List<SimInfoVO> simInfoVOList = new ArrayList<SimInfoVO>();

		for(SimInfo simInfo_:simInfoList){
			SimInfoVO simInfoVO = Objects.requireNonNull(BeanUtil.copy(simInfo_, SimInfoVO.class));

			simInfoVO.setSimTypeName(DictCache.getValue("sim_type",simInfo_.getSimType()));
			SimRel simRel = simRelService.selectSimRelBySimId(simInfo_.getId());
			if(!ObjectUtils.isEmpty(simRel)){
				DeviceInfo deviceInfo = DeviceCache.getDeviceById(AuthUtil.getTenantId(),Long.parseLong(simRel.getDeviceId()));
				if(!ObjectUtils.isEmpty(deviceInfo)){
						simInfoVO.setDeviceCode(deviceInfo.getDeviceCode());
						simInfoVO.setDeviceEntityCategoryId(String.valueOf(deviceInfo.getEntityCategoryId()));
						simInfoVO.setDeviceEntityCategoryName(EntityCategoryCache.getCategoryNameById(deviceInfo.getEntityCategoryId()));
						simInfoVO.setDeviceType(deviceInfo.getDeviceType());
				}
			}
			simInfoVOList.add(simInfoVO);
		};
		IPage<SimInfoVO> iPage = new Page<>(query.getCurrent(), query.getSize(), pages.getTotal());
		iPage.setRecords(simInfoVOList);
		return R.data(iPage);
	}

	/**
	 * 新增或修改 SIM卡信息
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入simInfoDTO")
	public R save(@Valid @RequestBody SimInfoDTO simInfoDTO) {
		return R.status(simInfoService.saveOrUpdateSimInfo(simInfoDTO));
	}

	/**
	 * 删除SIM卡信息
	 */
	@DeleteMapping("")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R save(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(simInfoService.removeSimInfo(ids));
	}



	@GetMapping("/importSimInfoModel")
	@ApiOperationSupport(order = 8)
	@ApiLog(value = "导入SIM卡模板下载")
	@ApiOperation(value = "导入SIM卡模板下载", notes = "导入SIM卡模板下载")
	public R importSIMInfoModel() throws Exception {
		String name = DictCache.getValue(DeviceConstant.DICT_IMPORT_EXCEL_MODEL, DeviceConstant.DICT_IMPORT_EXCEL_MODEL_SIM);
		String link = ossClient.getObjectLink(DeviceConstant.OSS_BUCKET_NAME, name).getData();
		return R.data(link);
	}



	private void verifyParamForImport(SIMInfoImportResultModel currentModel) {
		String simType = currentModel.getSimType();
		String simCode = currentModel.getSimCode();
		String simNumber = currentModel.getSimNumber();
		if (StringUtils.isBlank(simType)) {
			throw new ServiceException("SIM卡类型不能为空");
		}
		if (StringUtils.isBlank(simCode)) {
			throw new ServiceException("SIM卡号不能为空");
		}
		if (StringUtils.isBlank(simNumber)) {
			throw new ServiceException("SIM卡电话号码不能为空");
		}
		if(StringUtil.isBlank(DictCache.getValue("sim_type",simType))){
			throw new ServiceException("SIM卡类型不存在");
		}

		String codeRegex = DeviceConstant.regex.simCodeRegex;
		if(!simCode.matches(codeRegex)){
			throw new ServiceException("SIM卡号格式不正确");
		}

		String numberRegex = DeviceConstant.regex.simNumberRegex;
		if(!simNumber.matches(numberRegex)){
			throw new ServiceException("SIM卡电话号码格式不正确");
		}


	}


	@SuppressWarnings("finally")
	@PostMapping("/importSIMInfo")
	@ApiOperationSupport(order = 9)
	@ApiLog(value = "导入SIM卡信息")
	@ApiOperation(value = "导入SIM卡信息", notes = "导入SIM卡信息")
	public R<SIMImportResultVO> importSIMInfo(@RequestParam("file") MultipartFile excel) throws Exception {
		SIMImportResultVO result = new SIMImportResultVO();
		int successCount = 0;
		int failCount = 0;
		List<SIMInfoImportResultModel> failRecords = new ArrayList<>();
		List<SIMInfoImportResultModel> allRecords = new ArrayList<>();
		InputStream inputStream = null;
		SIMInfoImportResultModel currentModel = new SIMInfoImportResultModel();
		try {
			inputStream = new BufferedInputStream(excel.getInputStream());
			List<Object> datas = EasyExcelFactory.read(inputStream, new Sheet(1, 1));
			if (datas == null || datas.isEmpty()) {
				throw new ServiceException("Execl内容为空,请重新上传");
			}
			for (Object object : datas) {
				try {
					// 获取每行数据
					List<String> params = new ArrayList<>();
					for (Object o : (List<?>) object) {
						params.add(String.class.cast(o));
					}
					// 导入结果对象
					currentModel = new SIMInfoImportResultModel();
					if (params.size() > DeviceConstant.SIMExcelImportIndex.SIM_TYPE) {
						currentModel.setSimType(params.get(DeviceConstant.SIMExcelImportIndex.SIM_TYPE));
					}
					if (params.size() > DeviceConstant.SIMExcelImportIndex.SIM_CODE) {
						currentModel.setSimCode(params.get(DeviceConstant.SIMExcelImportIndex.SIM_CODE));
					}
					if (params.size() > DeviceConstant.SIMExcelImportIndex.SIM_NUMBER) {
						currentModel.setSimNumber(params.get(DeviceConstant.SIMExcelImportIndex.SIM_NUMBER));
					}
					if (params.size() > DeviceConstant.SIMExcelImportIndex.DEVICE_CODE) {
						currentModel.setDeviceCode(params.get(DeviceConstant.SIMExcelImportIndex.DEVICE_CODE));
					}
					if (params.size() > DeviceConstant.SIMExcelImportIndex.REMARK) {
						currentModel.setRemark(params.get(DeviceConstant.SIMExcelImportIndex.REMARK));
					}
					// 校验数据
					verifyParamForImport(currentModel);

					// 保存
					SimInfoDTO simInfoDTO = new SimInfoDTO();
					String simCode = currentModel.getSimCode();
//					int length= currentModel.getSimCode().length();
//					if(length < 12 ){
//						simCode = String.format("%012d", Long.parseLong(simCode));
//					}

					simInfoDTO.setSimType(currentModel.getSimType());
					simInfoDTO.setSimCode(simCode);
					simInfoDTO.setSimNumber(currentModel.getSimNumber());
					simInfoDTO.setRemark(currentModel.getRemark());
					DeviceInfo deviceInfo = DeviceCache.getDeviceByCode(AuthUtil.getTenantId(),currentModel.getDeviceCode());
					if(!ObjectUtils.isEmpty(deviceInfo)){
						simInfoDTO.setDeviceId(deviceInfo.getId().toString());
					}
					simInfoService.saveOrUpdateSimInfo(simInfoDTO);
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
//            logger.error("Excel操作异常" + e.getMessage());
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			result.setSuccessCount(successCount);
			result.setFailCount(failCount);
			result.setFailRecords(failRecords);

			if (failCount > 0) {
				String key = CacheNames.SIM_IMPORT + ":" + DateUtil.now().getTime();
				bladeRedisCache.setEx(key, allRecords, 3600L);
				result.setFileKey(key);
			}
		}
		return R.data(result);
	}
	
}
