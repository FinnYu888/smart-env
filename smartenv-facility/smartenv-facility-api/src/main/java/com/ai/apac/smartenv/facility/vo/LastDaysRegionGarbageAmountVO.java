package com.ai.apac.smartenv.facility.vo;

import com.ai.apac.smartenv.facility.entity.GarbageAmountDaily;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName LastDaysRegionGarbageAmountVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/21 10:22
 * @Version 1.0
 */
@Data
@ApiModel(value = "最近N天某区域垃圾收集总数统计", description = "最近N天某区域垃圾收集总数统计")
public class LastDaysRegionGarbageAmountVO implements Serializable {
    private static final long serialVersionUID = 1L;

    String regionId;

    String regionName;

    String garbageAmount;

}
