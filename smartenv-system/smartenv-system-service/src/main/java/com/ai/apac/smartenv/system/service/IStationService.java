package com.ai.apac.smartenv.system.service;

import com.ai.apac.smartenv.system.entity.Station;
import org.springblade.core.mp.base.BaseService;

import java.util.List;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/4/26 5:42 下午
 **/
public interface IStationService extends BaseService<Station> {

    /**
     * 创建岗位
     *
     * @param station
     * @return
     */
    boolean createStation(Station station);

    /**
     * 更新岗位信息
     *
     * @param station
     * @return
     */
    boolean updateStation(Station station);

    /**
     * 删除岗位
     *
     * @param stationIds
     * @return
     */
    boolean deleteStation(String stationIds);

    /**
     * 根据条件获取可以选择的父级岗位
     *
     * @param stationId
     * @param stationLevel
     * @param stationName
     * @param tenantId
     * @return
     */
    List<Station> getParentStation(Integer stationLevel, Long stationId, String stationName, String tenantId);

    /**
     * 更新岗位状态
     *
     * @param stationIds
     * @param newStatus
     * @return
     */
    boolean changeStationStatus(String stationIds, Integer newStatus);
}
