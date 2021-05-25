package com.ai.apac.smartenv.statistics.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * @author qianlong
 * @description 作业违规分析
 * @Date 2020/8/26 5:03 下午
 **/
@Data
@ApiModel("区域作业违规分析信息")
@Document("area_illegal_behavior_info")
public class AreaIllegalBehaviorInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Field("id")
    private String id;

    @Field("area_code")
    @ApiModelProperty("区域编码")
    private String areaCode;

    @Field("stat_date")
    @ApiModelProperty("统计日期")
    private String statDate;

    @Field("item")
    @ApiModelProperty("行为名称")
    private String item;

    @Field("item_type")
    @ApiModelProperty("行为类型")
    private String itemType;

    @Field("item_category")
    @ApiModelProperty("行为分类")
    private String itemCategory;

    @Field("value")
    @ApiModelProperty("数量")
    private Integer value;

    @Field("group")
    @ApiModelProperty("日期")
    private String group;

    @Field("updateTime")
    @ApiModelProperty("最近更新时间")
    private Date updateTime;

    public AreaIllegalBehaviorInfo(String id, String areaCode, String statDate, String item, String itemType, String itemCategory, Integer value, String group) {
        this.id = id;
        this.areaCode = areaCode;
        this.statDate = statDate;
        this.item = item;
        this.itemType = itemType;
        this.itemCategory = itemCategory;
        this.value = value;
        this.group = group;
    }

    public AreaIllegalBehaviorInfo(){
        this.id = null;
        this.areaCode = null;
        this.statDate = null;
        this.item = null;
        this.itemType = null;
        this.itemCategory = null;
        this.value = 0;
        this.group = null;
    }
}
