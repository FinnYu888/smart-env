package com.ai.apac.smartenv.facility.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "FacilityManageVO对象", description = "中转站管理总工作信息")
public class FacilityManageTotalVO implements Serializable {
    private static final long serialVersionUID = -1112903995717400158L;
    //中转站总数量
    private String numTotal;
    //垃圾总重量
    private String garbageWeightTotal;
    //总转运次数
    private String transitTimesTotal;
    //中转站列表
    private List<FacilityManageVO> facilityManageVOList;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
}
