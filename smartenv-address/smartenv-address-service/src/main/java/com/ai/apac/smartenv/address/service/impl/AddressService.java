package com.ai.apac.smartenv.address.service.impl;

import com.ai.apac.smartenv.address.entity.GisInfo;
import com.ai.apac.smartenv.address.service.IAddressService;
import com.ai.apac.smartenv.address.vo.GisInfoVO;
import com.ai.apac.smartenv.common.dto.BaiduMapReverseGeoCodingResult;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import jodd.util.StringPool;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/29 7:20 下午
 **/
@Service
public class AddressService implements IAddressService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BaiduMapUtils baiduMapUtils;

    @Override
    public void saveBaiDuAddress(String lat, String lon) {
        BaiduMapReverseGeoCodingResult result = new BaiduMapReverseGeoCodingResult();
        BaiduMapReverseGeoCodingResult.ReverseGeoResult reverseGeoResult = new BaiduMapReverseGeoCodingResult.ReverseGeoResult();
        reverseGeoResult.setCityCode("025");
        reverseGeoResult.setBusiness(lat + StringPool.COLON + lon);
        result.setResult(reverseGeoResult);
        mongoTemplate.save(result);
    }

    @Override
    public void saveAddress(BaiduMapReverseGeoCodingResult geoCodingResult) {
        Coords baiduCoords = geoCodingResult.getBaiduCoords();
        Query query = new Query();
        String key = geoCodingResult.getBaiduCoords().getLatitude() + "," + geoCodingResult.getBaiduCoords().getLongitude();
        query.addCriteria(Criteria.where("key").is(key));

        boolean exists = mongoTemplate.exists(query, BaiduMapReverseGeoCodingResult.class);
        if (exists) {
            List<BaiduMapReverseGeoCodingResult> baiduMapReverseGeoCodingResults = mongoTemplate.find(query, BaiduMapReverseGeoCodingResult.class);
            mongoTemplate.remove(baiduMapReverseGeoCodingResults.get(0));
        }
        geoCodingResult.setKey(key);
        mongoTemplate.save(geoCodingResult);
    }

    @Override
    public BaiduMapReverseGeoCodingResult getAddress(Coords coords) {
        String key = coords.getLatitude() + "," + coords.getLongitude();
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is(key));
        List<BaiduMapReverseGeoCodingResult> baiduMapReverseGeoCodingResults = mongoTemplate.find(query, BaiduMapReverseGeoCodingResult.class);



        if (CollectionUtil.isEmpty(baiduMapReverseGeoCodingResults)) {
            return null;
        }

        return baiduMapReverseGeoCodingResults.get(0);
    }

    /**
     * 保存GIS信息到MongoDB
     *
     * @param gisInfo
     */
    @Override
    public void saveGisInfo(GisInfo gisInfo) {
        String areaCode = gisInfo.getAreaCode();
        if (StringUtils.isBlank(areaCode)) {
            throw new ServiceException("区域编码不能为空");
        }

        //查询该区域编码是否存在,如果已存在则更新,否则就新增
        Query query = new Query(Criteria.where("area_code").is(areaCode));
        Update update = new Update();
        update.set("lat", gisInfo.getLat());
        update.set("lng", gisInfo.getLng());
        update.set("area_code", gisInfo.getAreaCode());
        update.set("area_name", gisInfo.getAreaName());
        update.set("parent_area", gisInfo.getParentArea());
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        options.upsert(true);

        mongoTemplate.findAndModify(query, update, options, GisInfo.class);

    }

    /**
     * 根据区域编码获取GIS信息
     *
     * @param areaCode
     * @return
     */
    @Override
    public GisInfoVO getGisInfoByAreaCode(String areaCode) {
        Query query = new Query(Criteria.where("area_code").is(areaCode));
        GisInfo gisInfo = mongoTemplate.findOne(query, GisInfo.class);
        if (gisInfo == null || StringUtils.isEmpty(gisInfo.getAreaCode())) {
            return null;
        }
        query = new Query(Criteria.where("area_code").is(gisInfo.getParentArea()));
        GisInfo parentInfo = mongoTemplate.findOne(query, GisInfo.class);
        String fullName = "";
        if(parentInfo == null){
            fullName = gisInfo.getAreaName();
        }else{
            fullName = parentInfo.getAreaName() + " -> " + gisInfo.getAreaName();
        }
        GisInfoVO gisInfoVO = BeanUtil.copy(gisInfo, GisInfoVO.class);
        gisInfoVO.setFullAreaName(fullName);
        return gisInfoVO;
    }
}
