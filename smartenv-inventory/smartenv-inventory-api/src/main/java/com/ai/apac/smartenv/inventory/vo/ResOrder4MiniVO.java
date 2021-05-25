package com.ai.apac.smartenv.inventory.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @ClassName ResOrder4MiniVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/15 15:36
 * @Version 1.0
 */
@Data
@ApiModel(value = "小程序首页待办任务", description = "小程序首页待办任务")
public class ResOrder4MiniVO implements Serializable {

    private String id;

    private String custId;

    private String custName;

    private String nickName;

    private String resOrderName;

    private String businessType;
    private String businessTypeName;


    private String resNum;

    private Timestamp resOrderTime;


}
