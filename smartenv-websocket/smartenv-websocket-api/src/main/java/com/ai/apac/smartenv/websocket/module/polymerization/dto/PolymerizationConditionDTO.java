package com.ai.apac.smartenv.websocket.module.polymerization.dto;

import lombok.Data;

import java.util.List;

@Data
/**
 * 传入条件
 *
 */
public class PolymerizationConditionDTO {

    private String keyWord;

    private List<Long> regionIds;
    private List<Long> vehicleTypes;
    private List<Long> vehicleWorkstatus;
    private List<Long> vehicleAccStatuses;
    private List<Long> perosnDepts;
    private List<Long> personWorkstatus;
    private List<Long> personWatchStatuses;
    
    private List<Long> transferStationScales;
    private List<Long> transferStationStatuses;
    //垃圾桶
    private List<Long> ashcanTypes;
    private List<Long> ashcanStatuses;
    private List<Long> ashcanWorkStatuses;
    //公厕
    private List<Long> wcLevel;
    private List<Long> wcState;

    private List<Long> eventStatuses;
    private List<Long> eventLevels;
}
