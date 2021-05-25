package com.ai.apac.smartenv.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 实体类
 *
 * @author qianlong
 * @since 2020-11-26
 */
@Data
@TableName("ai_project")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Project对象", description = "Project对象")
public class Project extends BaseEntity {

    private static final long serialVersionUID = 1L;
    
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
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司ID")
    private Long companyId;
    /**
     * 项目类型
     */
    @ApiModelProperty(value = "项目类型")
    private String projectType;
    /**
     * 负责人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "负责人员工ID")
    private Long ownerId;
    /**
     * 人员数量
     */
    @ApiModelProperty(value = "人员数量")
    private Integer personNum;
    /**
     * 车辆数量
     */
    @ApiModelProperty(value = "车辆数量")
    private Integer vehicleNum;
    /**
     * 设备数量
     */
    @ApiModelProperty(value = "设备数量")
    private Integer deviceNum;
    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private String lng;
    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private String lat;
    /**
     * 地图缩放级别
     */
    @ApiModelProperty(value = "地图缩放级别")
    private String mapScale;
    /**
     * 所在城市编码
     */
    @ApiModelProperty(value = "所在城市编码")
    private Long cityId;

    /**
     * 城市国家编码
     */
    @ApiModelProperty(value = "城市国家编码")
    private Long adcode;

    /**
     * 项目地址
     */
    @ApiModelProperty(value = "项目地址")
    private String address;
    /**
     * 项目描述
     */
    @ApiModelProperty(value = "remark")
    private String remark;

    @ApiModelProperty(value = "项目主题色")
    private String themeColor;
}
