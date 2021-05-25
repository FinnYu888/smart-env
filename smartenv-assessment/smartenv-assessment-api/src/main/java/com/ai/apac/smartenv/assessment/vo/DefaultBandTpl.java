package com.ai.apac.smartenv.assessment.vo;

import com.ai.apac.smartenv.system.entity.Dict;
import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: DefaultBandTpl
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/14  8:31    panfeng          v1.0.0             修改原因
 */
@Data
public class DefaultBandTpl {


    private String valueName;
    private String value;
    List<KpiTplBandVO> bandLevels;
    private Double maxScore;

}
