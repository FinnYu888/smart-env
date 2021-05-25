package com.ai.apac.smartenv.system.vo;

import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "BusiRegionVO对象", description = "BusiRegionVO对象")
public class BusiRegionVO {
    @ApiModelProperty(value = "区域对象")
    private Region region;

    /**
     * 工作区域节点列表
     */
    @ApiModelProperty(value = "工作区域节点列表")
    private WorkareaNode[] workareaNodes;
}
