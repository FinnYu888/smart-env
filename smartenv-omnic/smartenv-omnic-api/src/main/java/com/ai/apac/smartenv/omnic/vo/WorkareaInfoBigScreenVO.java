package com.ai.apac.smartenv.omnic.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xubr
 * @Description:
 * @Company: AsiaInfo International LTD.
 * @Date: Created at 2020/7/28 15:38.
 * <p>Modification History:
 * <p>Date          Author        Version        Description
 * <p>---------------------------------------------------------
 * <p>2020/7/28      xubr           1.0          first version
 */
@Data
@ApiModel(value = "大屏统一WorkareaInfo对象", description = "统一Workarea对象")
public class WorkareaInfoBigScreenVO implements Serializable {
    String id;
    String total;
    String size;
    String current;
    String orders;
    String hitCount;
    String searchCount;
    String pages;
    List<WorkareaInfoDetailVO> records;

}
