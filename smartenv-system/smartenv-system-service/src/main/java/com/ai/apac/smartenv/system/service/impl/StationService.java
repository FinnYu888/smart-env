package com.ai.apac.smartenv.system.service.impl;

import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.entity.Station;
import com.ai.apac.smartenv.system.mapper.StationMapper;
import com.ai.apac.smartenv.system.service.IStationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/4/26 5:42 下午
 **/
@Service
@Validated
@AllArgsConstructor
public class StationService extends BaseServiceImpl<StationMapper, Station> implements IStationService {

    private IPersonClient personClient;

    /**
     * 岗位信息校验
     *
     * @param station
     * @return
     */
    private boolean isValidStation(Station station) {
        Long parentId = station.getParentId();
        if (parentId == null) {
            throw new ServiceException("请选择上级岗位");
        }
        if (StringUtils.isBlank(station.getStationName())) {
            throw new ServiceException("岗位名称不能为空");
        }
        Integer stationLevel = station.getStationLevel();
        if (stationLevel == null || stationLevel <= 0) {
            throw new ServiceException("请选择岗位级别");
        }
        //如果parentId != 0,则判断当前岗位的岗位级别是否低于上级岗位,数字越大级别越高
        if (parentId != 0L) {
            Station parentStation = this.getById(parentId);
            if (stationLevel.equals(parentStation.getStationLevel())) {
                throw new ServiceException("当前岗位级别不能与上级岗位的级别相同");
            }
            if (stationLevel > parentStation.getStationLevel()) {
                throw new ServiceException("当前岗位级别不能高于上级岗位的级别");
            }
        }
        return true;
    }

    /**
     * 创建岗位
     *
     * @param station
     * @return
     */
    @Override
    public boolean createStation(Station station) {
        boolean result = false;
        if (isValidStation(station)) {
            result = save(station);
        }
        if (result) {
            StationCache.saveOrUpdateStation(station);
        }
        return result;
    }

    /**
     * 更新岗位信息
     *
     * @param station
     * @return
     */
    @Override
    public boolean updateStation(Station station) {
        boolean result = false;
        Long stationId = station.getId();
        if (stationId == null || stationId == 0L) {
            throw new ServiceException("请选择一个岗位");
        }
        if (isValidStation(station)) {
            result = updateById(station);
        }
        if (result) {
            StationCache.deleteStation(stationId);
        }
        return result;
    }

    /**
     * 删除部门
     *
     * @param stationIds
     * @return
     */
    @Override
    public boolean deleteStation(String stationIds) {
        List<Long> stationIdList = Func.toLongList(stationIds);
        stationIdList.stream().forEach(stationId -> {
            //判断该岗位是否与员工关联,如果有关联则不能被删除
            R<Integer> personCountResult = personClient.getPersonCountByStation(stationId);
            if (personCountResult != null && personCountResult.getData() != null && personCountResult.getData() > 0) {
                throw new ServiceException("该岗位已经与员工关联,不能被删除");
            }
            //如果该岗位有下级岗位则不能被删除
            Station station = getById(stationId);
            if (station != null) {
                Integer count = this.count(new LambdaQueryWrapper<Station>().eq(Station::getParentId, stationId));
                if (count > 0) {
                    throw new ServiceException("该岗位[" + station.getStationName() + "]有下级岗位,不能被删除");
                }
                boolean result = removeById(stationId);
                if (result) {
                    StationCache.deleteStation(stationId);
                }
            }
        });
        return true;
    }

    /**
     * 根据岗位级别获取可以选择的父级岗位
     *
     * @param stationId
     * @param stationLevel
     * @param tenantId
     * @return
     */
    @Override
    public List<Station> getParentStation(Integer stationLevel, Long stationId, String stationName, String tenantId) {
        LambdaQueryWrapper<Station> lambdaQueryWrapper = new LambdaQueryWrapper<Station>();
        if (stationLevel != null) {
            lambdaQueryWrapper.lt(Station::getStationLevel, stationLevel);
        }
        if (StringUtils.isNotBlank(stationName)) {
            lambdaQueryWrapper.like(Station::getStationName, stationName);
        }
        List<Station> stationList = list(lambdaQueryWrapper);
        return stationList.stream().filter(station -> !station.getId().equals(stationId)).collect(Collectors.toList());
//        return list(new LambdaQueryWrapper<Station>().lt(Station::getStationLevel, stationLevel)
//                .eq(Station::getTenantId, tenantId));
    }

    /**
     * 更新岗位状态
     *
     * @param stationIds
     * @param newStatus
     * @return
     */
    @Override
    public boolean changeStationStatus(String stationIds, Integer newStatus) {
        List<Long> stationIdList = Func.toLongList(stationIds);
        stationIdList.stream().forEach(stationId -> {
            Station station = getById(stationId);
            //如果要修改为禁用,需要判断该岗位是否有下级岗位,如果有则不能被禁用
            if (station != null) {
                if(newStatus == 2){
                    Integer count = this.count(new LambdaQueryWrapper<Station>().eq(Station::getParentId, stationId));
                    if (count > 0) {
                        throw new ServiceException("该岗位[" + station.getStationName() + "]有下级岗位,不能被禁用");
                    }
                }
                station.setStatus(newStatus);
                station.setUpdateTime(new Date());
                boolean result = updateById(station);
                if(result){
                    StationCache.deleteStation(stationId);
                }
            }
        });
        return true;
    }
}
