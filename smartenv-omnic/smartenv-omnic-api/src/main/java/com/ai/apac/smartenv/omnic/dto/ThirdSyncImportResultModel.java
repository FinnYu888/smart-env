package com.ai.apac.smartenv.omnic.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @ClassName thirdSyncImportResultModel
 * @Desc 第三方同步数据结果对象
 * @Author ZHANGLEI25
 * @Date 2020/7/2 15:00
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ThirdSyncImportResultModel extends BaseRowModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "编码" ,index = 0)
    private String code;

    @ExcelProperty(value = "类型" ,index = 1)
    private String type;

    @ExcelProperty(value = "状态" ,index = 2)
    private String status;

    @ExcelProperty(value = "原因" ,index = 3)
    private String reason;

}
