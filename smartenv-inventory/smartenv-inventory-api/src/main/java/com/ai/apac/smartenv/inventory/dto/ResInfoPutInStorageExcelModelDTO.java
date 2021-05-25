package com.ai.apac.smartenv.inventory.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: ResInfoPutInStorageExcelModelDTO
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/7/14
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/7/14     zhaidx           v1.0.0               修改原因
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResInfoPutInStorageExcelModelDTO extends BaseRowModel implements Serializable {

    private static final long serialVersionUID = 5844656513281002634L;

    @ExcelProperty(value = "物资来源", index = 0)
    private String resourceSource;
    @ExcelProperty(value = "采购人", index = 1)
    private String purchasingAgent;
    @ExcelProperty(value = "采购日期", index = 2)
    private String purchasingDate;
    @ExcelProperty(value = "仓库名称", index = 3)
    private String storageName;
    @ExcelProperty(value = "物资类型", index = 4)
    private String resSpecName;
    @ExcelProperty(value = "入库数量", index = 5)
    private String amount;
    @ExcelProperty(value = "单价", index = 6)
    private String unitPrice;
    @ExcelProperty(value = "备注", index = 7)
    private String remark;
    @ExcelProperty(value = "状态" ,index = 8)
    private String status;
    @ExcelProperty(value = "原因" ,index = 9)
    private String reason;
}
