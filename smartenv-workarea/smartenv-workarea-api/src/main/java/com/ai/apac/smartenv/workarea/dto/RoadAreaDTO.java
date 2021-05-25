package com.ai.apac.smartenv.workarea.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoadAreaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roadLevel;

    private Long roadArea;

}
