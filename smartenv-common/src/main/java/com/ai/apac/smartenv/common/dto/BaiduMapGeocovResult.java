package com.ai.apac.smartenv.common.dto;

import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: BaiduMapGeocovResult
 * @Description: 百度地图坐标转换结果
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/27
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/27  21:30    panfeng          v1.0.0             修改原因
 */
@Data
public class BaiduMapGeocovResult extends BaiduMapResult {

    private List<Coords> result;

}
