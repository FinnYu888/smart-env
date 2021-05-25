package com.ai.apac.smartenv.omnic.vo;

import com.ai.apac.smartenv.event.dto.mongo.GreenScreenEventDTO;
import com.ai.apac.smartenv.green.dto.mongo.GreenScreenGreenAreaDTO;
import com.ai.apac.smartenv.green.dto.mongo.GreenScreenGreenAreasDTO;
import com.ai.apac.smartenv.green.dto.mongo.GreenScreenTaskDTO;
import com.ai.apac.smartenv.green.dto.mongo.GreenScreenWorkingCountDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xubr
 * @Description:
 * @Company: AsiaInfo International LTD.
 * @Date: Created at 2020/7/22 19:44.
 * <p>Modification History:
 * <p>Date          Author        Version        Description
 * <p>---------------------------------------------------------
 * <p>2020/7/22      xubr           1.0          first version
 */
@Data
@ApiModel(value = "大屏统一租户对象", description = "统一租户VO对象")
public class TenantDetailsVO implements Serializable {
    public String tenantId;
    public GreenScreenWorkingCountDTO workingCountToday;
    public GreenScreenGreenAreasDTO greenAreaTotal;
    public List<GreenScreenTaskDTO> lastDaysTaskCount;
    public List<GreenScreenEventDTO> lastDaysEvents;

}
