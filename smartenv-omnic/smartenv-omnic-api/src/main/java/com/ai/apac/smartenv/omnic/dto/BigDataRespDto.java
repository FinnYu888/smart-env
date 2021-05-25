package com.ai.apac.smartenv.omnic.dto;

import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: BigDataRespDto
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/6
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/6  18:09    panfeng          v1.0.0             修改原因
 */
@Data
public class BigDataRespDto {

    private int code;

    private boolean success;

    private List<TrackPositionDto> data;


    private String msg;

}
