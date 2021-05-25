package com.ai.apac.smartenv.system.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 城市信息数据传输类
 * @Date 2020/2/9 9:14 上午
 **/
@Data
public class CityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String pid;

    private String cityCode;

    private String cityName;

    private String postCode;

    private String areaCode;

}
