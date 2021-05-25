package com.ai.apac.smartenv.inventory.vo;

import com.ai.apac.smartenv.inventory.entity.ResOrder;
import com.ai.apac.smartenv.inventory.entity.ResSpec;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName ResOrder4Home
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/27 15:02
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "首页显示的ResOrderVO对象", description = "首页显示的ResOrderVO对象")
public class ResOrder4HomeVO extends ResOrder {
    private static final long serialVersionUID = 1L;

    private String orderId;

    private String relUserId;

    private String relUserName;


    private String orderStatusName;

    private String resTypeName;

    private String resSpecName;

}
