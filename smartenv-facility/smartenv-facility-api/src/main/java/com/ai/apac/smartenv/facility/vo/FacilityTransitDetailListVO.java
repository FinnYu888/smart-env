package com.ai.apac.smartenv.facility.vo;

import com.ai.apac.smartenv.facility.entity.TranstationEveryDay;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "FacilityManageVO对象", description = "中转站工作详情信息")
public class FacilityTransitDetailListVO implements Serializable {
    private static final long serialVersionUID = 9007607243898715263L;
    private String garbageWeightTotal;
    private String harmfulGarbageWeight;
    private String kitchenGarbageWeight;
    private String recyclableGarbageWeight;
    private String otherGarbageWeight;
    private Integer transferTimesTotal;
    private IPage<TranstationEveryDay> detailIPage;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
}
