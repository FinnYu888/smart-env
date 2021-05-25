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
package com.ai.apac.smartenv.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;


/**
 * 城市天气
 *
 * @author Blade
 * @since 2020-02-07
 */
@Data
@TableName("ai_city_weather")
@ApiModel(value = "城市天气对象", description = "城市天气对象信息")
public class CityWeather extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 城市ID
     */
    @ApiModelProperty(value = "城市ID")
    private Long id;

    /**
     * 城市英文名
     */
    @ApiModelProperty(value = "城市英文名")
    private String cityEn;
    /**
     * 城市中文名
     */
    @ApiModelProperty(value = "城市中文名")
    private String cityZh;
    /**
     * 省份英文名
     */
    @ApiModelProperty(value = "省份英文名")
    private String provinceEn;
    /**
     * 省份中文名
     */
    @ApiModelProperty(value = "省份中文名")
    private String provinceZh;
    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private String lon;
    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private String lat;

    /**
     * 上级城市英文名
     */
    @ApiModelProperty(value = "上级城市英文名")
    private String leaderEn;

    /**
     * 上级城市中文名
     */
    @ApiModelProperty(value = "上级城市中文名")
    private String leaderZh;
}
