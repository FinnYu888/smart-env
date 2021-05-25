package com.ai.apac.smartenv.system.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description ProjectDTO对象
 * @Date 2020/11/30 11:42 上午
 **/
@Data
@ApiModel("ProjectDTO对象")
public class SimpleProjectDTO implements Serializable {

    @ApiModelProperty("主键id")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long id;

    /**
     * 项目全称
     */
    @ApiModelProperty(value = "项目全称")
    private String projectName;
    /**
     * 项目简称
     */
    @ApiModelProperty(value = "项目简称")
    private String shortName;
    /**
     * 项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private String projectCode;
    /**
     * 公司ID
     */
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @ApiModelProperty(value = "公司ID")
    private Long companyId;

    /**
     * 负责人ID
     */
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @ApiModelProperty(value = "负责人员工ID")
    private Long ownerId;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String lng;
    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String lat;

    /**
     * 所在城市编码
     */
    @ApiModelProperty(value = "所在城市编码")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long cityId;

    /**
     * 城市国家编码
     */
    @ApiModelProperty(value = "城市国家编码")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long adcode;
}
