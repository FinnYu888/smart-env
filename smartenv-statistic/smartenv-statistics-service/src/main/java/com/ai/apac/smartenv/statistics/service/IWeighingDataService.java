package com.ai.apac.smartenv.statistics.service;

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
public interface IWeighingDataService {


    Boolean weighingSiteAllRegionLastMonthPolymerizationData(String companyId);

    Boolean weighingSiteCompanyLastMonthPolymerizationData(String companyName);

    Boolean weighingSiteAllCompanyLastWeekPolymerizationData(String companyId);
}
