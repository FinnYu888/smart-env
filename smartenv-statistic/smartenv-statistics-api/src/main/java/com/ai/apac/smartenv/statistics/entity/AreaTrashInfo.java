package com.ai.apac.smartenv.statistics.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 垃圾分类收运分析
 * @Date 2020/12/09 10:42 上午
 **/
@Data
@ApiModel("垃圾分类收运分析")
@Document("area_trash_info")
public class AreaTrashInfo implements Serializable {

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

    @Field("item_type")
    @ApiModelProperty("垃圾分类类型")
    private String itemType;

    @Field("weight")
    @ApiModelProperty("垃圾重量")
    private Double weight;

    public AreaTrashInfo() {
        this.id = null;
        this.areaCode = null;
        this.statDate = null;
        this.itemType = null;
        this.weight = 0.0;
    }

    public AreaTrashInfo(String id, String areaCode, String statDate, String itemType, Double weight) {
        this.id = id;
        this.areaCode = areaCode;
        this.statDate = statDate;
        this.itemType = itemType;
        this.weight = weight;
    }
}
