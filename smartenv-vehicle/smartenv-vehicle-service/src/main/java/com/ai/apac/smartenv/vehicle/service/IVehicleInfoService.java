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
package com.ai.apac.smartenv.vehicle.service;

import com.ai.apac.smartenv.omnic.dto.SummaryDataForVehicle;
import com.ai.apac.smartenv.vehicle.dto.VehicleDeviceStatusCountDTO;
import com.ai.apac.smartenv.vehicle.dto.VehicleStatusStatDTO;
import com.ai.apac.smartenv.vehicle.dto.SimpleVehicleTrackInfoDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleNode;
import com.ai.apac.smartenv.vehicle.vo.VehicleVideoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 车辆基本信息表 服务类
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface IVehicleInfoService extends BaseService<VehicleInfo> {

    /**
     * 自定义分页
     *
     * @param page
     * @param vehicleInfo
     * @return
     */
    IPage<VehicleInfoVO> selectVehicleInfoPage(IPage<VehicleInfoVO> page, VehicleInfoVO vehicleInfo);

    List<VehicleInfo> getVehicleInfoByDeptId(Long deptId);

    /**
     * @param vehicleInfo
     * @return
     * @Function: IVehicleInfoService::updateVehicleInfoById
     * @Description: 更新，可置空
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月20日 下午2:29:54
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    Integer updateVehicleInfoById(VehicleInfoVO vehicleInfo);

    /**
     * @param vehicleInfo
     * @param query
     * @param vehicleState
     * @return
     * @Function: IVehicleInfoService::page
     * @Description: 分页查询
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月22日 下午3:53:45
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    IPage<VehicleInfo> page(VehicleInfoVO vehicleInfo, Query query, String deviceStatus);

    IPage<VehicleInfoVO> selectVehicleInfoVOPage(VehicleInfo vehicleInfo, Query query, String deviceStatus, String isBindTerminal, String vehicleState);

    boolean saveVehicleInfo(VehicleInfoVO vehicleInfoVO);

    Integer updateVehicleInfo(VehicleInfoVO vehicleInfo);

    /**
     * @param vehicleInfo
     * @param vehicleState
     * @return
     * @Function: IVehicleInfoService::listAll
     * @Description: 查询所有
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月24日 下午4:15:09
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    List<VehicleInfo> listAll(VehicleInfoVO vehicleInfo);

    /**
     * @param vehicle
     * @param query
     * @param filterVehicleIdList
     * @return
     * @Function: IVehicleInfoService::pageForPerson
     * @Description: 查询要绑定车辆信息
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月26日 下午3:04:16
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    IPage<VehicleInfo> pageForPerson(VehicleInfoVO vehicle, Query query, Long personId);

    boolean removeVehicle(List<Long> idList);

    /**
     * 获取车辆树
     *
     * @param treeType 1-按车辆类型分组，2-按部门分组
     * @param tenantId 租户ID
     * @return
     */
    List<VehicleNode> getVehicleTree(Integer treeType, String tenantId);

    /**
     * 获取当天车辆出勤状态统计
     *
     * @param tenantId
     * @return
     */
    VehicleStatusStatDTO getVehicleStatusStatToday(String tenantId);

    /**
     * @param tenantId
     * @return
     * @Function: IVehicleInfoService::getUsedVehicleByTenant
     * @Description: 获取在用车辆
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年3月23日 下午12:30:52
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    List<VehicleInfo> getUsedVehicleByTenant(String tenantId);

    /**
     * @param vehicleInfo
     * @return
     * @Function: IVehicleInfoService::countAll
     * @Description: 获取所有车辆数量
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年3月23日 下午12:30:42
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    int countAll(VehicleInfoVO vehicleInfo);

    /**
     * @param tenantId
     * @return
     * @Function: IVehicleInfoService::getTotalNormalVehicleCount
     * @Description: 获取在用车辆数量
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年3月23日 下午12:30:31
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    int getTotalNormalVehicleCount(String tenantId);

    List<VehicleNode> treeByDept(String nodeName, String tenantId, List<Long> invalidEntityIdList);

    /**
     * 根据车辆ID获取历史视频播放地址
     *
     * @param vehicleId 车辆ID
     * @param channelNo 通道编号
     * @return
     */
    VehicleVideoVO getHistoryVideoUrl(Long vehicleId, String channelNo, String startTime, String endTime);

    VehicleVideoVO getVehicleVideosLive(Long vehicleId, String device, String channel);

    VehicleVideoVO getVehicleVideosHistory(Long vehicleId, String device, String channel, String startTime, String endTime, Boolean isTransfer);

    Integer updateVehicleAccstateById(Long accState, Long vehicleId);

    /**
     * 更新车辆状态
     *
     * @author 66578
     */
    void updateVehicleStateById(Integer vehicleState, Long vehicleId);

    void reSyncDevices();

    Long getVehicleRealWorkingArea(Long vehicleId);

    /**
     * 获取车辆工作状态今日实时数据统计
     *
     * @param tenantId 租户ID
     * @return
     */
    SummaryDataForVehicle getSummaryDataForVehicleToday(String tenantId);

    /**
     * 获取车辆设备状态实时统计
     *
     * @param tenantId
     * @return
     */
    VehicleDeviceStatusCountDTO getVehicleDeviceStatusCount(String tenantId);

    /**
     * 批量获取车辆设备状态实时统计
     *
     * @param tenantId
     * @return
     */
    List<VehicleDeviceStatusCountDTO> listVehicleDeviceStatusCount(String tenantId);

    Future<IPage<VehicleInfo>> pageByCompanyId(Query query, String companyId);

    /**
     * 根据车牌号获取车辆轨迹数据
     *
     * @param plateNumber
     * @param beginTime
     * @param endTime
     * @return
     */
    List<SimpleVehicleTrackInfoDTO> getVehicleTrackInfoByPlateNumber(String plateNumber, String beginTime, String endTime);

    /**
     * 根据项目编号、日期获取车辆轨迹数据
     *
     * @param projectCode
     * @param statDate
     * @return
     */
    List<SimpleVehicleTrackInfoDTO> getVehicleTrackInfoByProjectCode(String projectCode, String statDate);

    /**
     * 根据项目编号、日期统计车辆行驶汇总数据
     *
     * @param projectCode
     * @param statDate
     */
    void statVehicleTrackInfoByProjectCode(String projectCode, String statDate);
}
