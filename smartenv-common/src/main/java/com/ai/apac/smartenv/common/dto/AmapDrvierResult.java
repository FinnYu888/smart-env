package com.ai.apac.smartenv.common.dto;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: AmapDrvierResult
 * @Description: 高德地图车辆导航返回结果
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2021/1/17
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2021/1/17  19:00    panfeng          v1.0.0             修改原因
 */
@Data
public class AmapDrvierResult {
    private Integer status;  //0：请求失败；1：请求成功
    private String info; //tatus为0时，info返回错误原因，否则返回“OK”
    private Integer count;//	驾车路径规划方案数目
    private DriverRouter route; //驾车路径规划信息列表
    private List<Coords> coords; // 导航路段轨迹列表（自定义）

    @Data
    public static class DriverRouter {
        private String origin;//起点坐标  规则： lon，lat（经度，纬度）， “,”分割，如117.500244, 40.417801 经纬度小数点不超过6位
        private String destination;// 终点坐标	规则： lon，lat（经度，纬度）， “,”分割，如117.500244, 40.417801 经纬度小数点不超过6位
        private Double taxi_cost;//打车费用	单位：元
        private List<DriverPath> paths; //驾车换乘方案
    }

    @Data
    public static class DriverPath {
        private Integer distance;//	行驶距离	单位：米
        private Integer duration;// 预计行驶时间	单位：秒
        private String strategy;  //导航策略
        private Double tolls;// 此导航方案道路收费	单位：元
        private Integer toll_distance; //收费路段距离
        private Integer restriction; //限行结果	0 代表限行已规避或未限行，即该路线没有限行路段 1 代表限行无法规避，即该线路有限行路段
        private Integer traffic_lights; //红绿灯个数
        private List<DriverStep> steps; //导航路段
    }

    @Data
    public static class DriverStep {
        private String instruction; //行驶指示
        private String orientation;// 方向
        private String distance;//此路段距离 单位：米
        private Double tolls;//此段收费单位：元
        private String toll_distance;//收费路段距离
        private String toll_road; //主要收费道路
        private String duration;//
        private String polyline;//此路段坐标点串 格式为坐标串，如：116.481247,39.990704;116.481270,39.990726
        private String action; //导航主要动作 <a href=https://lbs.amap.com/api/webservice/guide/api/direction#drive_action>驾车主要动作</a>
        private String assistant_action; //导航辅助动作详见 驾车动作列表
        private Tmc tmcs; //驾车导航详细信息
    }

    @Data
    public static class Tmc {
        private String distance; // 此段路的长度 单位：米
        private String status;// 此段路的交通情况 未知、畅通、缓行、拥堵、严重拥堵
        private String polyline;//polyline此段路的轨迹 规格：x1,y1;x2,y2
    }

    public void formatResult() {
        if (this.getRoute() == null || CollectionUtil.isEmpty(this.getRoute().getPaths())) {

            return;
        }
        if (this.coords == null) {
            coords = new ArrayList<>();
        }
        DriverPath driverPath = this.getRoute().getPaths().get(0);
        List<DriverStep> steps = driverPath.getSteps();
        String lastCoor = null;

        for (DriverStep driverStep : steps) {
            String polyline = driverStep.getPolyline();
            String[] latlngs = polyline.split(";");
            if (latlngs.length == 0) {
                continue;
            }
            for (int i = 0; i < latlngs.length; i++) {
                if (lastCoor != null && i == 0 & latlngs[i].equals(lastCoor)) {
                    continue;
                }
                String[] split = latlngs[i].split(",");
                String lng = split[0];//经度
                String lat = split[1]; //纬度
                Coords coords = new Coords();
                coords.setLongitude(lng);
                coords.setLatitude(lat);
                this.getCoords().add(coords);
            }
            lastCoor = latlngs[latlngs.length - 1];
        }


    }


}
