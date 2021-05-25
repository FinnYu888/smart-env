package com.ai.apac.smartenv.facility.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WeighingSiteMetaData
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/12/8
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/12/8  9:50    panfeng          v1.0.0             修改原因
 */
@Data
@Document("WeighingSiteMetaData")
public class WeighingSiteMetaDataDTO {
    List<String> transportUnitList;
    List<String> shipperList;


}
