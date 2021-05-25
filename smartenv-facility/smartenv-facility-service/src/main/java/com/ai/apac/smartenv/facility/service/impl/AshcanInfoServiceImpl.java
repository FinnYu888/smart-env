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

import cn.hutool.core.util.StrUtil;
import com.ai.apac.smartenv.common.constant.FacilityConstant;
import com.ai.apac.smartenv.common.utils.ZXingCodeUtil;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.facility.cache.AshcanCache;
import com.ai.apac.smartenv.facility.entity.AshcanInfo;
import com.ai.apac.smartenv.facility.vo.AshcanInfoVO;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.facility.mapper.AshcanInfoMapper;
import com.ai.apac.smartenv.facility.service.IAshcanInfoService;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;

import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import cn.hutool.core.codec.Base64;
import lombok.AllArgsConstructor;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-07-20
 */
@Service
@AllArgsConstructor
public class AshcanInfoServiceImpl extends BaseServiceImpl<AshcanInfoMapper, AshcanInfo> implements IAshcanInfoService {

	private IWorkareaClient workareaClient;
	private IOssClient ossClient;
	private ISysClient sysClient;
	private IDeviceClient deviceClient;
	
	@Override
	public IPage<AshcanInfoVO> selectAshcanInfoPage(IPage<AshcanInfoVO> page, AshcanInfoVO ashcanInfo) {
		return page.setRecords(baseMapper.selectAshcanInfoPage(page, ashcanInfo));
	}

	/*
	 * 新增垃圾桶
	 */
	@Override
	public boolean saveAshcanInfo(AshcanInfoVO ashcanInfoVO) throws IOException {
		// 验证参数
		validateAshcanInfo(ashcanInfoVO);
		checkAshcanCode(ashcanInfoVO);
		// 删除缓存
		AshcanCache.delAshcan(ashcanInfoVO.getId());
		ashcanInfoVO.setAshcanStatus(FacilityConstant.AshcanStatus.NORMAL);// 默认正常
		ashcanInfoVO.setWorkStatus(FacilityConstant.AshcanWorkStatus.NORMAL);// 默认正常
		// 保存
		// 设置传感器上报位置 TODO
		//溢满传感器数据上报设置 TODO

		if(StringUtils.isNotBlank(ashcanInfoVO.getDeviceId())) {
			ashcanInfoVO.setSupportDevice(FacilityConstant.SupportDevice.YES);
		}else {
			ashcanInfoVO.setSupportDevice(FacilityConstant.SupportDevice.NO);
		}

		boolean saveResult = save(ashcanInfoVO);
		if (saveResult) {
//			createQrCode(ashcanInfoVO);
		}
		return saveResult;
	}

	/*
	 * 校验名字是否重复
	 */
	private boolean checkAshcanCode(AshcanInfoVO ashcanInfoVO) {
		QueryWrapper<AshcanInfo> queryWrapper = new QueryWrapper<>();
        if (ashcanInfoVO.getId() != null) {
            queryWrapper.notIn("id", ashcanInfoVO.getId());
        }
        queryWrapper.eq("ashcan_code", ashcanInfoVO.getAshcanCode());
        BladeUser user = AuthUtil.getUser();
        if (user != null && StringUtils.isNotBlank(user.getTenantId())) {
            queryWrapper.eq("tenant_id", user.getTenantId());
        }
        List<AshcanInfo> list = list(queryWrapper);
        if (list != null && list.size() > 0) {
            throw new ServiceException("该垃圾桶编号已存在");
        }
        return true;
	}

	@Override
	public String createQrCode(AshcanInfo ashcanInfo) throws IOException {
		String base64 = AshcanCache.getAshcanQrCodeById(ashcanInfo.getId());
		if (StringUtils.isBlank(base64)) {
			// 生成二维码
			BufferedImage bim = ZXingCodeUtil.createCode(String.valueOf(ashcanInfo.getId()));
			base64 = ZXingCodeUtil.imageToBase64String(bim);
			AshcanCache.saveAshcanQrCode(ashcanInfo.getId(), base64);
		}
		return base64;
	}

	private void validateAshcanInfo(AshcanInfoVO ashcanInfoVO) {
        Set<ConstraintViolation<@Valid AshcanInfo>> validateSet = Validation.buildDefaultValidatorFactory().getValidator()
                .validate(ashcanInfoVO, new Class[0]);
        if (validateSet != null && !validateSet.isEmpty()) {
            String messages = validateSet.stream().map(ConstraintViolation::getMessage)
                    .reduce((m1, m2) -> m1 + "；" + m2).orElse("参数输入有误！");
            throw new ServiceException(messages);
        }
	}

	@Override
	public boolean removeAshcan(List<Long> idList) {
		idList.forEach(ashcanId -> {
			AshcanInfo ashcan = AshcanCache.getAshcanById(ashcanId);
            if (ashcan == null) {
                throw new ServiceException("垃圾桶不存在");
            }
            // 删除缓存
            AshcanCache.delAshcan(ashcanId);
            // 逻辑删除
            deleteLogic(Arrays.asList(ashcanId));
        });
        return true;
	}

	@Override
	public boolean updateAshcan(AshcanInfoVO ashcanInfo) {
		// 验证参数
        validateAshcanInfo(ashcanInfo);
        checkAshcanCode(ashcanInfo);
        // 删除缓存
        AshcanCache.delAshcan(ashcanInfo.getId());
        // 保存
        boolean updateResult = updateById(ashcanInfo);
		return updateResult;
	}

	@Override
	public IPage<AshcanInfo> page(AshcanInfo ashcanInfo, Query query) {
		QueryWrapper<AshcanInfo> queryWrapper = generateQueryWrapper(ashcanInfo);
        IPage<AshcanInfo> pages = page(Condition.getPage(query), queryWrapper);
        return pages;
	}

	@Override
	public List<AshcanInfo> listByAshcanInfo(AshcanInfo ashcanInfo) {
		QueryWrapper<AshcanInfo> queryWrapper = generateQueryWrapper(ashcanInfo);
		List<AshcanInfo> ashcanInfoList = list(queryWrapper);
		return ashcanInfoList;
	}

	private QueryWrapper<AshcanInfo> generateQueryWrapper(AshcanInfo ashcanInfo) {
		QueryWrapper<AshcanInfo> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(ashcanInfo.getAshcanCode())) {
            queryWrapper.like("ashcan_code", ashcanInfo.getAshcanCode());
        }
        if (StringUtils.isNotBlank(ashcanInfo.getAshcanType())) {
            queryWrapper.eq("ashcan_type", ashcanInfo.getAshcanType());
        }
        if (StringUtils.isNotBlank(ashcanInfo.getAshcanStatus())) {
        	queryWrapper.eq("ashcan_status", ashcanInfo.getAshcanStatus());
        }
        if (StringUtils.isNotBlank(ashcanInfo.getWorkStatus())) {
        	queryWrapper.eq("work_status", ashcanInfo.getWorkStatus());
        }
        if (ashcanInfo.getCapacity() != null) {
            queryWrapper.eq("capacity", ashcanInfo.getCapacity());
        }
        if (ashcanInfo.getWorkareaId() != null) {
        	queryWrapper.eq("workarea_id", ashcanInfo.getWorkareaId());
        }
        if (ashcanInfo.getRegionId() != null) {
        	List<Long> workareaIdList = new ArrayList<>();
        	List<WorkareaInfo> workareaInfolist = workareaClient.getWorkareaInfoByRegion(ashcanInfo.getRegionId()).getData();
        	if (workareaInfolist != null && !workareaInfolist.isEmpty()) {
        		workareaInfolist.forEach(workareaInfo -> {
        			workareaIdList.add(workareaInfo.getId());
				});
        		queryWrapper.in("workarea_id", workareaIdList);
        	} else {
        		workareaIdList.add(-1L);
        		queryWrapper.in("workarea_id", workareaIdList);
        	}
        }
        if (StringUtils.isNotBlank(ashcanInfo.getTenantId())) {
            queryWrapper.eq("tenant_id", ashcanInfo.getTenantId());
        } else {
            BladeUser user = AuthUtil.getUser();
            if (user != null) {
                queryWrapper.eq("tenant_id", user.getTenantId());
            }
        }
        queryWrapper.orderByAsc("ashcan_code");
        return queryWrapper;
	}

	@Override
	public AshcanInfoVO getAshcanAllInfoByVO(AshcanInfoVO ashcanInfoVO) {
		if (ashcanInfoVO == null || ashcanInfoVO.getId() == null) {
			return ashcanInfoVO;
		}
		// 部门名称
		if (ashcanInfoVO.getDeptId() != null && ashcanInfoVO.getDeptId() > 0) {
			Dept dept = DeptCache.getDept(ashcanInfoVO.getDeptId());
			if (dept != null) {
				ashcanInfoVO.setDeptName(dept.getFullName());
			}
		}
		// 垃圾桶分类
		if (ashcanInfoVO.getAshcanType() != null) {
			ashcanInfoVO.setAshcanTypeName(DictCache.getValue(FacilityConstant.DictCode.ASHCAN_TYPE, ashcanInfoVO.getAshcanType()));
		}
		// 垃圾桶状态
		if (ashcanInfoVO.getAshcanStatus() != null) {
			ashcanInfoVO.setAshcanStatusName(DictCache.getValue(FacilityConstant.DictCode.ASHCAN_STATUS, ashcanInfoVO.getAshcanStatus()));
		}
		// 垃圾桶工作状态
		if (ashcanInfoVO.getWorkStatus() != null) {
			ashcanInfoVO.setWorkStatusName(DictCache.getValue(FacilityConstant.DictCode.ASHCAN_WORK_STATUS, ashcanInfoVO.getWorkStatus()));
		}
		// 是否支持设备
		if (ashcanInfoVO.getSupportDevice() != null) {
			ashcanInfoVO.setSupportDeviceName(DictCache.getValue(FacilityConstant.DictCode.YES_NO, ashcanInfoVO.getSupportDevice()));
		}
		// 所属区域
		WorkareaInfo workareaInfo = workareaClient.getWorkInfoById(ashcanInfoVO.getWorkareaId()).getData();
		if (workareaInfo != null && workareaInfo.getId() != null) {
			ashcanInfoVO.setWorkareaName(workareaInfo.getAreaName());
			Region region = sysClient.getRegion(workareaInfo.getRegionId()).getData();
			ashcanInfoVO.setRegionId(workareaInfo.getRegionId());
			if (region != null) {
				ashcanInfoVO.setRegionName(region.getRegionName());
			}
		}
		if(StringUtils.isNotBlank(ashcanInfoVO.getDeviceId())) {
			ashcanInfoVO.setDeviceName(deviceClient.getDeviceById(ashcanInfoVO.getDeviceId()).getData().getDeviceName());
		}
		if (StringUtils.isBlank(ashcanInfoVO.getAshcanQrCode())) {
			String qrCode = null;
			try {
				qrCode = this.createQrCode(ashcanInfoVO);
			} catch (IOException e) {
				log.error(StrUtil.format("垃圾桶[{}]生成二维码失败，失败原因：{}", ashcanInfoVO.getAshcanCode(), e.getMessage()));
			}
			ashcanInfoVO.setAshcanQrCode(qrCode);
		}
		// 展示经纬度设置，优先取传感器上报位置，如果没有传感器上报位置则使用规划位置
		if(StringUtils.isBlank(ashcanInfoVO.getDeviceLat()) && StringUtils.isBlank(ashcanInfoVO.getDeviceLng())) {
			ashcanInfoVO.setShowLat(ashcanInfoVO.getLat());
			ashcanInfoVO.setShowLng(ashcanInfoVO.getLng());
		}else {
			ashcanInfoVO.setShowLat(ashcanInfoVO.getDeviceLat());
			ashcanInfoVO.setShowLng(ashcanInfoVO.getDeviceLng());
		}

		//图标展示
		String picture = "";
		if(FacilityConstant.SupportDevice.YES.equals(ashcanInfoVO.getSupportDevice())) { // 綁定終端
			if(ashcanInfoVO.getWorkStatus() == null || ashcanInfoVO.getAshcanStatus() == null) { //初始状态
				picture = FacilityConstant.AshcanDevicePicture.NORMAL;
			}else {
				if(FacilityConstant.AshcanStatus.NORMAL.equals(ashcanInfoVO.getAshcanStatus())) { // 是否损坏
					if(FacilityConstant.AshcanWorkStatus.NORMAL.equals(ashcanInfoVO.getWorkStatus())) { // 正常未满溢
						picture = FacilityConstant.AshcanDevicePicture.NORMAL;
					}else if(FacilityConstant.AshcanWorkStatus.OVERFLOW.equals(ashcanInfoVO.getWorkStatus())) { // 正常满溢
						picture = FacilityConstant.AshcanDevicePicture.OVERFLOW;
					}else if(FacilityConstant.AshcanWorkStatus.NOSIGNAL.equals(ashcanInfoVO.getWorkStatus())) { //无信号
						picture = FacilityConstant.AshcanDevicePicture.NO_SIGNAL;
					}

				}else  {
					picture = FacilityConstant.AshcanDevicePicture.DAMAGED;
				}
			}

		} else if(FacilityConstant.SupportDevice.NO.equals(ashcanInfoVO.getSupportDevice())){ // 未绑定终端
			if(ashcanInfoVO.getWorkStatus() == null || ashcanInfoVO.getAshcanStatus() == null) {
				picture = FacilityConstant.AshcanPicture.NORMAL; //初始状态
			}else{
				if(FacilityConstant.AshcanStatus.NORMAL.equals(ashcanInfoVO.getAshcanStatus())) { // 是否损坏
					if(FacilityConstant.AshcanWorkStatus.NORMAL.equals(ashcanInfoVO.getWorkStatus())) { // 正常未满溢
						picture = FacilityConstant.AshcanPicture.NORMAL;
					}else if(FacilityConstant.AshcanWorkStatus.OVERFLOW.equals(ashcanInfoVO.getWorkStatus())) { // 正常满溢
						picture = FacilityConstant.AshcanPicture.OVERFLOW;
					}
				}else {
					picture = FacilityConstant.AshcanPicture.DAMAGED;
				}
			}

		}

		String pic = ossClient.getObjectLink(FacilityConstant.OSS_BUCKET_NAME, picture).getData();
		ashcanInfoVO.setAshcanPicture(pic);
		return ashcanInfoVO;
	}
	
}
