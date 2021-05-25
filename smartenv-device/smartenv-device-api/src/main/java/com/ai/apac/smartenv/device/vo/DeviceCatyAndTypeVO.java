package com.ai.apac.smartenv.device.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DeviceCatyAndTypeVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/18 10:26
 * @Version 1.0
 */
@Data
@ApiModel(value = "Device分类和型号VO对象", description = "Device分类和型号")
public class DeviceCatyAndTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String deviceCategoryId;

    private String deviceCategoryName;

    List<String> deviceTypes;
}
