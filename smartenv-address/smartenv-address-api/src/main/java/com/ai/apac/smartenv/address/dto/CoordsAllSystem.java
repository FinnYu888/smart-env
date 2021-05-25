package com.ai.apac.smartenv.address.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: CoordsAllSystem
 * @Description: 多个坐标系对应坐标
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/5/29
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/5/29 14:49    panfeng          v1.0.0             修改原因
 */
@Data
@EqualsAndHashCode
public class CoordsAllSystem {



    // wgs84 对应坐标
    private String longitude_wgs84;
    private String latitude_wgs84;


    //GC02 对应坐标
    private String longitude_gc02;
    private String latitude_gc02;

    //GC02 百度 经纬度坐标系坐标
    private String longitude_baidu09ll;
    private String latitude_baidu09ll;

}
