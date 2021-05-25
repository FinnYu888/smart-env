package com.ai.apac.smartenv.facility.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "FacilityManageVO对象", description = "中转站工作信息")
public class FacilityManageVO implements Serializable {

    private static final long serialVersionUID = 3591773558918071393L;
    //转运垃圾量
    private String garbageWeigth;
    //转运次数
    private String transitTimes;
    //臭味级别
    private String odorLevel;
    //中转站id
    private Long facilityId;
    //中转站名称
    private String facilityName;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
}
