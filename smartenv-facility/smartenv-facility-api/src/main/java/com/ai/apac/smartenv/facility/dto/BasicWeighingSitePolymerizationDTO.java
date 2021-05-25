package com.ai.apac.smartenv.facility.dto;

import lombok.Data;

import java.util.Date;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: BasicWeighingSitePolymerizationDTO
 * @Description: 通用的 称重点聚合对象
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/12/8
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/12/8  15:02    panfeng          v1.0.0             修改原因
 */
@Data
public class BasicWeighingSitePolymerizationDTO {
    private String date;
    private Double value;
    private String obj;
    private Date createTime;
    private String companyId;

}
