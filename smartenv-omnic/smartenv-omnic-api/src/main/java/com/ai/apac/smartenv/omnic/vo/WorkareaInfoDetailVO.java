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
public class WorkareaInfoDetailVO implements Serializable {
    String workareaId;
//    Long regionId;
//    Long status;
//    Long areaType;
//    String areaName;
//    String division;
//    Long workAreaType;
//    String length;
//    String width;
//    String area;
//    Long personCount;
//    Long vehicleCount;
//    Long bindType;
//    String areaHead;
//    String areaHeadName;
//    String divisionName;
//    String workAreaName;
//    Long relId;
    List<WorkareaInfoNodeVO> nodes;

}
