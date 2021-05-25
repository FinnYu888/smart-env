package com.ai.apac.smartenv.common.dto;

import com.ai.apac.smartenv.common.annotation.Latitude;
import com.ai.apac.smartenv.common.annotation.Longitude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: Coords
 * @Description: 百度地图各个坐标格式
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/20
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/20  20:29    panfeng          v1.0.0             修改原因
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coords implements Serializable {

    //纬度
    @Latitude
    String latitude;
    //经度
    @Longitude
    String longitude;

    // 以下两个变量只在百度中使用，
    //纬度，
    private String lat;
    //经度
    private String lng;

    // 以下两个变量只在百度中使用，
    //经度
    String x;
    //纬度
    String y;


}
