package com.ai.apac.smartenv.common.dto;

import com.ai.apac.smartenv.common.annotation.Latitude;
import com.ai.apac.smartenv.common.annotation.Longitude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qianlong
 * @description 区域坐标对象
 * @Date 2020/11/29 10:30 下午
 **/
@Data
public class AreaNode {

    /**
     * 由于精度问题，直接存储字符串
     */
    @Longitude
    @ApiModelProperty(value = "由于精度问题，直接存储字符串")
    private String lng;
    /**
     * 由于精度问题，直接存储字符串
     */
    @Latitude
    @ApiModelProperty(value = "由于精度问题，直接存储字符串")
    private String lat;
}
