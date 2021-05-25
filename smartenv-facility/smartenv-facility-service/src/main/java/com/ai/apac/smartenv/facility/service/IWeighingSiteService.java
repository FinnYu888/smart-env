package com.ai.apac.smartenv.facility.service;

import com.ai.apac.smartenv.facility.dto.WeighingSiteRecordDTO;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IWeighingSiteService
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/12/3
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/12/3  16:45    panfeng          v1.0.0             修改原因
 */
public interface IWeighingSiteService {
    Integer batchImportWeighingSiteData(List<WeighingSiteRecordDTO> weighingSiteRecordDTOS);

//    Boolean weighingSiteAllRegionLastMonthPolymerizationData();
//
//    Boolean weighingSiteCompanyLastMonthPolymerizationData(String companyName);
//
//    Boolean weighingSiteAllCompanyLastWeekPolymerizationData();
}
