package com.ai.apac.smartenv.omnic.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/6 3:24 下午
 **/
@Data
public class RealTimePositionResp implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;

    private boolean success;

    private RealTimePositionDTO data;

    private String msg;
}
