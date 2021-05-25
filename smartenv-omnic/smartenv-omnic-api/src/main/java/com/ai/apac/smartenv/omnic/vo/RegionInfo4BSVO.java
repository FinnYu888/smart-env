package com.ai.apac.smartenv.omnic.vo;

import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

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
@ApiModel(value = "大屏区域查询对象", description = "大屏区域查询对象")
public class RegionInfo4BSVO implements Serializable {
    private static final long serialVersionUID = -1883102944590929842L;
    String id;
    String name;
    @ApiModelProperty(value = "工作区域节点列表")
    private WorkareaNode[] workareaNodes;


}
