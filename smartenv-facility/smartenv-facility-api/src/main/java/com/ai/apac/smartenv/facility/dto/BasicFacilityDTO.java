package com.ai.apac.smartenv.facility.dto;

import com.ai.apac.smartenv.common.annotation.Latitude;
import com.ai.apac.smartenv.common.annotation.Longitude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author qianlong
 * @description 人员基本信息DTO
 * @Date 2020/3/21 07:21 上午
 **/
@Data
@ApiModel(value = "设施基本信息", description = "设施基本信息")
@Document
public class BasicFacilityDTO implements Serializable {
    private static final long serialVersionUID = 8924163048094356921L;

    @ApiModelProperty(value = "设施ID")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @Indexed
    private Long id;

    @ApiModelProperty(value = "设施名称")
    @Indexed
    private String facilityName;

    /*暂定 1 为中转站 2 为垃圾桶*/
    @ApiModelProperty(value = "总类型")
    @Indexed
    private Integer facilityMainType;

    /*中转站 型号
      垃圾桶 分类
      */
    @ApiModelProperty(value = "类别")
    @Indexed
    private String facilityType;

    @ApiModelProperty(value = "类别名称")
    private String facilityTypeName;

    /**
     * 员工所属部门
     */
    @ApiModelProperty(value = "容量")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long capacity;
    /*片区*/
    @ApiModelProperty(value = "片区")
    @Indexed
    private String regionId;

    @ApiModelProperty(value = "片区名称")
    private String facilityRegionName;

    @ApiModelProperty(value = "工作状态")
    @Indexed
    private String workStatus;

    @ApiModelProperty(value = "工作状态名称")
    private String workStatusName;

    @ApiModelProperty(value = "状态")
    @Indexed
    private Integer facilityStatus;

    @ApiModelProperty(value = "状态名称")
    private String facilityStatusName;

    @ApiModelProperty(value = "租户id")
    @Indexed
    private String tenantId;

    // 实时位置
    @Latitude
    private String lat;
    @Longitude
    private String lng;
}
