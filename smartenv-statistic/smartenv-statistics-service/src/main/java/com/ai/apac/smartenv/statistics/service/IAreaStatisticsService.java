package com.ai.apac.smartenv.statistics.service;

import com.ai.apac.smartenv.statistics.dto.DeviceOnlineInfoDTO;
import com.ai.apac.smartenv.statistics.dto.SynthInfoDTO;
import com.ai.apac.smartenv.statistics.dto.VehicleWorkSynthInfoDTO;
import com.ai.apac.smartenv.statistics.entity.*;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/8 7:23 下午
 **/
public interface IAreaStatisticsService {

    /**
     * 根据区域获取项目汇总数据
     *
     * @param areaCode
     * @param statDate
     * @return
     */
    AreaProjectInfo getAreaProjectInfo(String areaCode, String statDate);

    /**
     * 保存区域项目汇总数据
     *
     * @param areaProjectInfo
     * @return
     */
    boolean saveAreaProjectInfo(AreaProjectInfo areaProjectInfo);

    /**
     * 根据区域和日期查询工作完成情况
     *
     * @param areaCode
     * @param statDate
     * @return
     */
    AreaWorkInfo getAreaWorkInfo(String areaCode, String statDate);

    /**
     * 保存区域工作完成情况
     *
     * @param areaWorkInfo
     * @return
     */
    boolean saveAreaWorkInfo(AreaWorkInfo areaWorkInfo);

    /**
     * 获取区域垃圾收运重量数据
     *
     * @param areaCode
     * @param statDate
     * @return
     */
    AreaTrashInfo getAreaTrashInfo(String areaCode, String statDate);

    /**
     * 获取区域垃圾收运历史数量数据
     *
     * @param areaCode
     * @param beginDate
     * @param endDate
     * @return
     */
    List<AreaTrashInfo> getAreaTrashInfoHistory(String areaCode, String beginDate, String endDate);

    /**
     * 保存区域工作完成情况
     *
     * @param areaTrashInfo
     * @return
     */
    boolean saveAreaTrashInfo(AreaTrashInfo areaTrashInfo);

    /**
     * 获取区域作业违规分析数据
     *
     * @param areaCode
     * @param statDate
     * @return
     */
    AreaIllegalBehaviorInfo getAreaIllegalBehaviorInfo(String areaCode, String statDate);

    /**
     * 获取区域作业车辆违规分析数据
     *
     * @param areaCode
     * @param projectCode
     * @param statDate
     * @return
     */
    List<AreaIllegalBehaviorInfo> getAreaIllegalBehaviorForVehicle(String areaCode, String projectCode, String statDate);

    /**
     * 获取区域作业人员违规分析数据
     *
     * @param areaCode
     * @param projectCode
     * @param statDate
     * @return
     */
    List<AreaIllegalBehaviorInfo> getAreaIllegalBehaviorForPerson(String areaCode, String projectCode, String statDate);

    /**
     * 保存区域作业违规数据
     *
     * @param areaIllegalBehaviorInfo
     * @return
     */
    boolean saveAreaIllegalBehaviorInfo(AreaIllegalBehaviorInfo areaIllegalBehaviorInfo);

    /**
     * 获取区域历史告警数量数据
     *
     * @param areaCode
     * @param beginDate
     * @param endDate
     * @return
     */
    List<AreaAlarmCountInfo> getAreaAlarmCountHistory(String areaCode, String beginDate, String endDate);

    /**
     * 保存区域历史告警数量数据
     *
     * @param areaAlarmCountInfo
     * @return
     */
    boolean saveAlarmCount(AreaAlarmCountInfo areaAlarmCountInfo);


    SynthInfoDTO getSynthInfo(String adcode, String companyId, String projectCode);

    List<VehicleWorkSynthInfoDTO> getRealVehicleOperationrate(String areaId, String projectCode, String today);

    /**
     * 计算区域的告警数字
     *
     * @param areaCode
     */
    void statTotalAlarmCount(String areaCode, String statDate);

    /**
     * 保存项目信息
     *
     * @param projectInfo
     */
    void saveProjectInfo(ProjectInfo projectInfo);

    /**
     * 根据区域查询区域信息
     *
     * @param areaCode
     * @return
     */
    List<ProjectInfo> listProjectInfo(String areaCode);

    /**
     * 根据项目编码查询设备在线率
     *
     * @param adcode 城市编码
     * @param projectCode 多个设备编码用逗号分隔
     * @return
     */
    List<DeviceOnlineInfoDTO> listDeviceOnlineInfo(String adcode, String projectCode);
}
