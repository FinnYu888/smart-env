package com.ai.apac.smartenv.statistics.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author qianlong
 * @description 项目概要
 * @Date 2020/12/8 7:15 下午
 **/
@Data
@ApiModel("项目综合信息")
@Document(collection = "project_info")
public class ProjectInfo {

    @Id
    @Field("id")
    private String id;

    @ApiModelProperty("经度")
    private String lng;

    @ApiModelProperty("纬度")
    private String lat;

    @ApiModelProperty("城市")
    private String city;

    @Field("area_code")
    @ApiModelProperty("城市编码")
    private String areaCode;

    @Field("project_name")
    @ApiModelProperty("项目名称")
    private String projectName;

    @Field("project_size")
    @ApiModelProperty("项目规模")
    private String projectSize;

    @Field("money")
    @ApiModelProperty("项目投资额")
    private String money;

    @Field("url")
    @ApiModelProperty("项目链接")
    private String url;

    @Field("remote_open")
    @ApiModelProperty("打开方式")
    private String remoteOpen;

    @Field("create_date")
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createDate;
}
