package com.ai.apac.smartenv.facility.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "FacilityManageVO对象", description = "中转站工作信息详情列表")
public class FacilityMangeListVO implements Serializable {
    private static final long serialVersionUID = -6579546689173086545L;
    //总量
    private String garbageWeightTotal;
    //转运次数总数
    private String transitTimesTotal;
    //中转站数量
    private String transtationNum;
    //转运次数列表
    private IPage<FacilityInfoVO> transtationTotaPage;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
}
