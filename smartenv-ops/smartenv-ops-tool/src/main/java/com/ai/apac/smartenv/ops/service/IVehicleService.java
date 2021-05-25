package com.ai.apac.smartenv.ops.service;

import java.util.List;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2021/1/16 11:35 下午
 **/
public interface IVehicleService {

    /**
     * 根据项目编码查询车辆轨迹数据并生成报表
     * @param dateList YYYYMMDD集合
     * @param projectCode
     */
    void saveVehicleTrackInfoReport(List<String> dateList, String projectCode);
}
