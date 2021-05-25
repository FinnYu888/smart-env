package com.ai.apac.smartenv.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;


/**
 * 城市
 *
 * @author qianlong
 * @since 2020-12-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_city_baidu")
@ApiModel(value = "百度地图城市对象", description = "百度地图城市对象")
public class AdministrativeCity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 城市ID
     */
    @ApiModelProperty(value = "城市ID")
    private Long id;

    /**
     * 上级城市ID
     */
    @ApiModelProperty(value = "城市ID")
    private Long parentId;

    /**
     * 国家
     */
    @ApiModelProperty(value = "国家")
    private String country;

    /**
     * 城市英文名
     */
    @ApiModelProperty(value = "城市名称")
    private String cityName;

    /**
     * 城市编号
     */
    @ApiModelProperty(value = "城市编号")
    private String cityCode;

    /**
     * 城市区号
     */
    @ApiModelProperty(value = "城市区号")
    private String areaCode;

    /**
     * 邮编
     */
    @ApiModelProperty(value = "邮编")
    private String postCode;
    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private String lon;
    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private String lat;

    @ApiModelProperty(value = "项目数量")
    private Integer projectNum;

}
