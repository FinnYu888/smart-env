package com.ai.apac.smartenv.facility.vo;

import com.ai.apac.smartenv.facility.entity.GarbageAmountDaily;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName last30GarbageAmountVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/23 14:49
 * @Version 1.0
 */
@Data
@ApiModel(value = "最近N天垃圾收集吨数对象", description = "最近N天垃圾收集吨数对象")
public class LastDaysGarbageAmountVO implements Serializable {
    private static final long serialVersionUID = 1L;

    String garbageTypeId;

    String garbageTypeName;

    List<GarbageAmountDaily> garbageAmountDailyList;

}
