package com.ai.apac.smartenv.ops.service;

import com.ai.apac.smartenv.facility.vo.FacilityTranstationDetailVO;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/12/20 9:11 下午
 **/
public interface ITranstationMockService {

    /**
     * 导入中转站垃圾称重数据
     * @param facilityTranstationDetailVO
     */
    void importTranstationData(FacilityTranstationDetailVO facilityTranstationDetailVO);
}
