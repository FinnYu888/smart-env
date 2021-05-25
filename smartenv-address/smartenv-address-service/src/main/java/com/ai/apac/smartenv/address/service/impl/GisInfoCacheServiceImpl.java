package com.ai.apac.smartenv.address.service.impl;

import com.ai.apac.smartenv.address.dto.CoordsAllSystem;
import com.ai.apac.smartenv.address.feign.IGisInfoCacheClient;
import com.ai.apac.smartenv.address.service.IGisInfoCacheService;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import org.springblade.core.tool.api.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: GisInfoCacheServiceImpl
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/6/1
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/6/1 10:13    panfeng          v1.0.0             修改原因
 */
@Service
public class GisInfoCacheServiceImpl implements IGisInfoCacheService {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public Boolean saveOrupdateCoordsAllSystemList(BaiduMapUtils.CoordsSystem from, List<CoordsAllSystem> coordsAllSystemList) {

        for (CoordsAllSystem coordsAllSystem : coordsAllSystemList) {
            saveOrUpdate(from, coordsAllSystem);
        }
        return true;
    }


    @Override
    public Boolean saveOrUpdate(BaiduMapUtils.CoordsSystem from, CoordsAllSystem coordsAllSystem) {

        String lat = null;
        String lng = null;
        Query query = new Query();
        if (from.equals(BaiduMapUtils.CoordsSystem.GC02)) {
            lat = coordsAllSystem.getLatitude_gc02();
            lng = coordsAllSystem.getLongitude_gc02();
            query.addCriteria(Criteria.where("longitude_gc02").is(lng));
            query.addCriteria(Criteria.where("latitude_gc02").is(lat));
        } else if (from.equals(BaiduMapUtils.CoordsSystem.WGS84)) {
            lat = coordsAllSystem.getLatitude_wgs84();
            lng = coordsAllSystem.getLongitude_wgs84();
            query.addCriteria(Criteria.where("longitude_wgs84").is(lng));
            query.addCriteria(Criteria.where("latitude_wgs84").is(lat));
        } else if (from.equals(BaiduMapUtils.CoordsSystem.BD09LL)) {
            lat = coordsAllSystem.getLatitude_baidu09ll();
            lng = coordsAllSystem.getLongitude_baidu09ll();
            query.addCriteria(Criteria.where("longitude_baidu09ll").is(lng));
            query.addCriteria(Criteria.where("latitude_baidu09ll").is(lat));
        }
        boolean exists = mongoTemplate.exists(query, CoordsAllSystem.class);
        if (!exists) {
            mongoTemplate.save(coordsAllSystem);
        } else {

            CoordsAllSystem one = mongoTemplate.findOne(query, CoordsAllSystem.class);
            BeanUtils.copyProperties(coordsAllSystem, one);
            if (!one.equals(coordsAllSystem)){
                Update update=new Update();
                update.set("longitude_wgs84",coordsAllSystem.getLongitude_wgs84());
                update.set("latitude_wgs84",coordsAllSystem.getLatitude_wgs84());
                update.set("longitude_gc02",coordsAllSystem.getLongitude_gc02());
                update.set("latitude_gc02",coordsAllSystem.getLatitude_gc02());
                update.set("longitude_baidu09ll",coordsAllSystem.getLongitude_baidu09ll());
                update.set("latitude_baidu09ll",coordsAllSystem.getLatitude_baidu09ll());
                mongoTemplate.updateFirst(query,update,CoordsAllSystem.class);
            }

        }

        return true;
    }

    @Override
    public CoordsAllSystem getCoordsAllSystem(BaiduMapUtils.CoordsSystem coordsSystem, Coords coords) {
        Query query = new Query();
        if (coordsSystem.equals(BaiduMapUtils.CoordsSystem.GC02)) {
            query.addCriteria(Criteria.where("longitude_gc02").is(coords.getLongitude()));
            query.addCriteria(Criteria.where("latitude_gc02").is(coords.getLatitude()));
        } else if (coordsSystem.equals(BaiduMapUtils.CoordsSystem.WGS84)) {
            query.addCriteria(Criteria.where("longitude_wgs84").is(coords.getLongitude()));
            query.addCriteria(Criteria.where("latitude_wgs84").is(coords.getLatitude()));
        } else if (coordsSystem.equals(BaiduMapUtils.CoordsSystem.BD09LL)) {
            query.addCriteria(Criteria.where("longitude_baidu09ll").is(coords.getLongitude()));
            query.addCriteria(Criteria.where("latitude_baidu09ll").is(coords.getLatitude()));
        }
        CoordsAllSystem one = mongoTemplate.findOne(query, CoordsAllSystem.class);
        return one;
    }


    // 比较coords  target 不为空
    @Override
    public Coords getCoords(BaiduMapUtils.CoordsSystem from, BaiduMapUtils.CoordsSystem target, Coords coords) {
        Query query = new Query();
        if (from.equals(BaiduMapUtils.CoordsSystem.GC02)) {
            query.addCriteria(Criteria.where("longitude_gc02").is(coords.getLongitude()));
            query.addCriteria(Criteria.where("latitude_gc02").is(coords.getLatitude()));
        } else if (from.equals(BaiduMapUtils.CoordsSystem.WGS84)) {
            query.addCriteria(Criteria.where("longitude_wgs84").is(coords.getLongitude()));
            query.addCriteria(Criteria.where("latitude_wgs84").is(coords.getLatitude()));
        } else if (from.equals(BaiduMapUtils.CoordsSystem.BD09LL)) {
            query.addCriteria(Criteria.where("longitude_baidu09ll").is(coords.getLongitude()));
            query.addCriteria(Criteria.where("latitude_baidu09ll").is(coords.getLatitude()));
        }
        CoordsAllSystem one = mongoTemplate.findOne(query, CoordsAllSystem.class);
        if (one != null && target.equals(BaiduMapUtils.CoordsSystem.GC02) && one.getLatitude_gc02() != null && one.getLongitude_gc02() != null) {
            Coords result=new Coords();
            result.setLatitude(one.getLatitude_gc02());
            result.setLatitude(one.getLongitude_gc02());
            return result;

        } else if (one != null && target.equals(BaiduMapUtils.CoordsSystem.WGS84) && one.getLatitude_wgs84() != null && one.getLongitude_wgs84() != null) {
            Coords result=new Coords();
            result.setLatitude(one.getLatitude_gc02());
            result.setLatitude(one.getLongitude_gc02());
            return result;

        } else if (one != null && target.equals(BaiduMapUtils.CoordsSystem.BD09LL) && one.getLatitude_baidu09ll() != null && one.getLongitude_baidu09ll() != null) {
            Coords result=new Coords();
            result.setLatitude(one.getLatitude_gc02());
            result.setLatitude(one.getLongitude_gc02());
            return result;
        }
        return null;
    }

}
