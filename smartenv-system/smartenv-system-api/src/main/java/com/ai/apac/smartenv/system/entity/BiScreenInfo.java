package com.ai.apac.smartenv.system.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * @author qianlong
 * @description 大屏设置对象
 * @Date 2021/1/22 11:35 上午
 **/
@Data
@Document(collection = "bi_screen_info")
public class BiScreenInfo implements Serializable {

    @org.springframework.data.annotation.Id
    @Field("id")
    @ApiModelProperty("主键")
    private String Id;

    @Indexed
    @Field("menu_id")
    @ApiModelProperty("菜单ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long menuId;

    @ApiModelProperty("访问URL")
    private String path;

    @ApiModelProperty("预览URL")
    private String priviewPath;

    @Field("screen_code")
    @ApiModelProperty("大屏编码")
    private String screenCode;

    @Field("screen_name")
    @ApiModelProperty("大屏名称")
    private String screenName;

    @Field("title")
    @ApiModelProperty("大屏标题")
    private String title;

    @Indexed
    @ApiModelProperty("排序")
    private Integer sort;

    @Indexed
    @Field("project_code")
    @ApiModelProperty("项目编码")
    private String projectCode;

    @Field("create_time")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @Field("update_time")
    @ApiModelProperty("更新时间")
    private Date updateTime;
}
