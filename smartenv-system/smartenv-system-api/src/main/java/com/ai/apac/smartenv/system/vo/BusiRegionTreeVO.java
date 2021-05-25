package com.ai.apac.smartenv.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "BusiRegionTreeVO对象", description = "BusiRegionTreeVO对象")
public class BusiRegionTreeVO {
    @ApiModelProperty(value = "区域对象")
    private BusiRegionVO busiRegionVO;


    /**
     * 当前业务区域的子区域列表
     */
    @ApiModelProperty(value = "工作区域节点列表")
    private List<BusiRegionVO> childBusiRegionVOList;
}
