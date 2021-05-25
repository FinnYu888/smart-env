package com.ai.apac.smartenv.flow.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FlowInfoDetailVO implements Serializable {

    @ApiModelProperty(value = "流程id")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long id;
    @ApiModelProperty(value = "流程key")
    private String flowCode;
    @ApiModelProperty(value = "流程名称")
    private String flowName;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "流程定义图片")
    private String image;
    @ApiModelProperty(value = "是否配置标识")
    private Integer configFlag;

    @ApiModelProperty(value = "流程节点定义")
    private List<FlowTaskAllotVO> taskAllotVOList;
}
