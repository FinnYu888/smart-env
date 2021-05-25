package com.ai.apac.smartenv.address.entity;

import com.ai.apac.smartenv.common.constant.AddressConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/8/27 2:13 下午
 **/
@Data
@ApiModel
@Document(collection = AddressConstant.MongoDBTable.GIS_INFO)
public class GisInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("经度")
    private String lng;

    @ApiModelProperty("纬度")
    private String lat;

    @ApiModelProperty("区域名称")
    @Field("area_name")
    private String areaName;

    @ApiModelProperty("区域编号")
    @Indexed
    @Field("area_code")
    private String areaCode;

    @ApiModelProperty("城市区号")
    @Indexed
    @Field("city_code")
    private String cityCode;

    @ApiModelProperty("父区域")
    @Indexed
    @Field("parent_area")
    private String parentArea;
}
