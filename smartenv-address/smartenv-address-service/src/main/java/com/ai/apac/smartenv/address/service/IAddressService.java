package com.ai.apac.smartenv.address.service;

import com.ai.apac.smartenv.address.entity.GisInfo;
import com.ai.apac.smartenv.address.vo.GisInfoVO;
import com.ai.apac.smartenv.common.dto.BaiduMapReverseGeoCodingResult;
import com.ai.apac.smartenv.common.dto.Coords;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/29 6:36 下午
 **/
public interface IAddressService {

    void saveBaiDuAddress(String lat, String lon);

    void saveAddress(BaiduMapReverseGeoCodingResult geoCodingResult);

    BaiduMapReverseGeoCodingResult getAddress(Coords coords);

    /**
     * 保存GIS信息到MongoDB
     * @param gisInfo
     */
    void saveGisInfo(GisInfo gisInfo);

    /**
     * 根据区域编码获取GIS信息
     * @param areaCode
     * @return
     */
    GisInfoVO getGisInfoByAreaCode(String areaCode);
}
