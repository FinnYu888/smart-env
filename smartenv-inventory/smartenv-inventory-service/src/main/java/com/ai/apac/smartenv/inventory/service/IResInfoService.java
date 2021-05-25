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
package com.ai.apac.smartenv.inventory.service;

import com.ai.apac.smartenv.inventory.entity.ResInfo;
import com.ai.apac.smartenv.inventory.entity.ResInfoQuery;
import com.ai.apac.smartenv.inventory.entity.ResOperate;
import com.ai.apac.smartenv.inventory.vo.ResInfoPutInStorageExcelResultModelVO;
import com.ai.apac.smartenv.inventory.vo.ResInfoPutInStorageVO;
import com.ai.apac.smartenv.inventory.vo.ResInfoQueryVO;
import com.ai.apac.smartenv.inventory.vo.ResInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.secure.BladeUser;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  服务类
 *
 * @author Blade
 * @since 2020-02-25
 */
public interface IResInfoService extends BaseService<ResInfo> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param resInfo
	 * @return
	 */
	IPage<ResInfoVO> selectResInfoPage(IPage<ResInfoVO> page, ResInfoVO resInfo);
	/**
	*获取物资库存列表
	*/
	IPage<ResInfoQuery> selectResInfoQueryPage(IPage<ResInfoQuery> page, QueryWrapper<ResInfoQueryVO> queryWrapper);
	/**
	*物资入库接口
	*/
	boolean putInStorage(ResInfoPutInStorageVO infoPutInStorageVO);

	/**
	*修改库存数量
	*/
	boolean modifyStorageNumber(ResInfoVO resInfoVO, BladeUser user);

	/**
	 * 物资领用记录
	 */
	Boolean resDeliveryRecord(Long orderId);

	/**
	 * 取物资批量导入模板文件
     */
	void downloadDynamicResImportTemplate(BladeUser user, HttpServletRequest request, HttpServletResponse response);

    /**
	 * 批量物资入库
	 * @param excel
	 * @param tenantId
     * @return
	 * @throws Exception
	 */
	ResInfoPutInStorageExcelResultModelVO inventoryBatchInput(MultipartFile excel, String tenantId) throws Exception;

    /**
	 * 查询物资批量导入结果
	 * @param key
	 * @throws Exception
	 */
    void importResultExcelDownload(String key) throws Exception;

	/**
	 * 更新入库记录，同时更新库存数量
	 * @author 66578
	 */
	Boolean updateResOperate(ResOperate resOperate, ResInfo resInfo,ResInfo newResInfo);

	/**
	* 删除入库记录，同时更新库存数量
	* @author 66578
	*/
	Boolean deleteLogicResOperate(ResOperate resOperate, ResInfo resInfo);
}
