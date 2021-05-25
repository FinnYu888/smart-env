package com.ai.apac.smartenv.address.util;

import com.ai.apac.smartenv.address.dto.CoordsAllSystem;
import com.ai.apac.smartenv.address.feign.IGisInfoCacheClient;
import com.ai.apac.smartenv.common.annotation.Latitude;
import com.ai.apac.smartenv.common.annotation.Longitude;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: CoordsTypeConvertUtil
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/5/28
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/5/28 20:20    panfeng          v1.0.0             修改原因
 */
@Component
public class CoordsTypeConvertUtil {


    @Autowired
    public BaiduMapUtils baiduMapUtils;
    @Autowired
    private IGisInfoCacheClient gisInfoCache;


    /**
     * @param objCoords
     * @param <T>
     * @return
     */
    public <T> List<T> fromWebConvert(List<T> objCoords) {

        String coordsTypeStr = WebUtil.getHeader("coordsType");
        if (StringUtil.isBlank(coordsTypeStr)) {
            return objCoords;
        }
        int coordsType = Integer.parseInt(coordsTypeStr);

        BaiduMapUtils.CoordsSystem coordsSystem = BaiduMapUtils.CoordsSystem.getCoordsSystem(coordsType);
        return objectCoordsConvert(coordsSystem, BaiduMapUtils.CoordsSystem.GC02, objCoords);

    }


    public <T> List<T> toWebConvert(List<T> objCoords) {

        String coordsTypeStr = WebUtil.getHeader("coordsType");
        if (StringUtil.isBlank(coordsTypeStr)) {
            return objCoords;
        }
        int coordsType = Integer.parseInt(coordsTypeStr);

        BaiduMapUtils.CoordsSystem coordsSystem = BaiduMapUtils.CoordsSystem.getCoordsSystem(coordsType);
        return objectCoordsConvert(BaiduMapUtils.CoordsSystem.GC02, coordsSystem, objCoords);

    }

    public <T> List<T> deviceToWebConvert(List<T> objCoords) {

        String coordsTypeStr = WebUtil.getHeader("coordsType");
        if (StringUtil.isBlank(coordsTypeStr)) {
            return objCoords;
        }
        int coordsType = Integer.parseInt(coordsTypeStr);

        BaiduMapUtils.CoordsSystem coordsSystem = BaiduMapUtils.CoordsSystem.getCoordsSystem(coordsType);
        return objectCoordsConvert(BaiduMapUtils.CoordsSystem.WGS84, coordsSystem, objCoords);

    }



    public <T> List<T> objectCoordsConvert(BaiduMapUtils.CoordsSystem from, BaiduMapUtils.CoordsSystem to, List<T> objCoords) {
        if (from.equals(to)){
            return objCoords;
        }
        if (CollectionUtil.isEmpty(objCoords)) {
            return objCoords;
        }
        T t = objCoords.get(0);
        Class tClass = t.getClass();
        Class tempClass = t.getClass();

        ArrayList<Field> declaredFields=new ArrayList<>();

        while (tempClass!=null&&!tempClass.getName().equals(Object.class.getName())){
            Field[] allFields = tempClass.getDeclaredFields();
            declaredFields.addAll(CollectionUtil.arrayToList(allFields));
            tempClass=tempClass.getSuperclass();
        }




        Map<Integer, Field> lats = new HashMap<>();
        Map<Integer, Field> lngs = new HashMap<>();


        for (Field field : declaredFields) {
            Latitude latitudeAnn = field.getAnnotation(Latitude.class);
            Longitude longitudeAnn = field.getAnnotation(Longitude.class);
            if (latitudeAnn != null) {
                int tag = latitudeAnn.tag();
                if (lats.get(tag) != null) {
                    throw new RuntimeException("重复纬度注解，tags=" + tag);
                }
                lats.put(tag, field);
                continue;
            }
            if (longitudeAnn != null) {
                int tag = longitudeAnn.tag();
                if (lngs.get(tag) != null) {
                    throw new RuntimeException("重复经度注解，tags=" + tag);
                }
                lngs.put(tag, field);
                continue;
            }
        }
        List<Coords> coordsList = new ArrayList<>();

        for (T obj : objCoords) {
            lats.forEach((key, value) -> {
                Field lngField = lngs.get(key);
                try {
                    PropertyDescriptor latPro = new PropertyDescriptor(value.getName(), tClass);
                    PropertyDescriptor lngPro = new PropertyDescriptor(lngField.getName(), tClass);
                    Method latProReadMethod = latPro.getReadMethod();
                    Method lngProReadMethod = lngPro.getReadMethod();
                    Object latVal = latProReadMethod.invoke(obj);
                    Object lngVal = lngProReadMethod.invoke(obj);
                    if (latVal != null && lngVal != null) {
                        Coords coords = new Coords();
                        coords.setLongitude(lngVal.toString());
                        coords.setLatitude(latVal.toString());
                        coordsList.add(coords);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        try {


            List<Coords> coordsResult = coordsConvert(from, to, coordsList);
            ;
            Iterator<Coords> iterator = coordsResult.iterator();

            for (T obj : objCoords) {
                lats.forEach((key, value) -> {
                    PropertyDescriptor latPro = null;
                    Field lngField = lngs.get(key);
                    try {
                        latPro = new PropertyDescriptor(value.getName(), tClass);
                        PropertyDescriptor lngPro = new PropertyDescriptor(lngField.getName(), tClass);
                        Method latProWriteMethod = latPro.getWriteMethod();
                        Method lngProWriteMethod = lngPro.getWriteMethod();

                        Method latProReadMethod = latPro.getReadMethod();
                        Method lngProReadMethod = lngPro.getReadMethod();
                        Object latVal = latProReadMethod.invoke(obj);
                        Object lngVal = lngProReadMethod.invoke(obj);
                        if (latVal != null && lngVal != null) {
                            if (iterator.hasNext()) {
                                Coords next = iterator.next();
                                latProWriteMethod.invoke(obj, next.getLatitude());
                                lngProWriteMethod.invoke(obj, next.getLongitude());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        } catch (IOException e) {
            throw new RuntimeException("百度坐标系转换失败");
        }

        return objCoords;

    }


    public List<Coords> coordsConvert(BaiduMapUtils.CoordsSystem from, BaiduMapUtils.CoordsSystem to, List<Coords> coordsList) throws IOException {

        if (from==to){
            return coordsList;
        }

        List<Coords> coordsListAllResult = new ArrayList<>();

        Map<Integer, Coords> coordsMap = new HashMap<>();
        for (int i = 0; i < coordsList.size(); i++) {
            coordsMap.put(i, coordsList.get(i));
        }
        Iterator<Integer> iterator = coordsMap.keySet().iterator();
        Map<Integer, Coords> notInCache = new HashMap<>();

        List<Integer> notInCacheList = new ArrayList<>();

        while (iterator.hasNext()) {
            Integer next = iterator.next();
            Coords coords = coordsMap.get(next);
//            Coords coordsResult = gisInfoCache.getCoords(from, BaiduMapUtils.CoordsSystem.BD09LL, coords).getData();
//            if (coordsResult != null && StringUtil.isNotBlank(coordsResult.getLatitude()) && StringUtil.isNotBlank(coordsResult.getLongitude())) {
//                coordsMap.put(next, coordsResult);
//            } else {
//                notInCache.put(next, coords);
//                notInCacheList.add(next);
//            }


            notInCache.put(next, coords);
            notInCacheList.add(next);
        }
        List<Coords> notInchcheList = new ArrayList<>();
        Set<Integer> keySet = notInCache.keySet();
        keySet.forEach(key -> notInchcheList.add(notInCache.get(key)));


        List<Coords> coordsListResult = null;
        if (to.equals(BaiduMapUtils.CoordsSystem.BD09LL)) {
            coordsListResult = baiduMapUtils.coordsToBaiduMapllAll(from, coordsList);
        } else if (from.equals(BaiduMapUtils.CoordsSystem.BD09LL) && to.equals(BaiduMapUtils.CoordsSystem.GC02)) {
            coordsListResult = baiduMapUtils.baiduMapllToGC02All(coordsList);
        } else if (from.equals(BaiduMapUtils.CoordsSystem.WGS84) && to.equals(BaiduMapUtils.CoordsSystem.GC02)) {
            coordsListResult = baiduMapUtils.wgs84ToGC02All(coordsList);
        } else if (from.equals(BaiduMapUtils.CoordsSystem.WGS84) && to.equals(BaiduMapUtils.CoordsSystem.BD09LL)) {
            coordsListResult = baiduMapUtils.coordsToBaiduMapllAll(BaiduMapUtils.CoordsSystem.WGS84, coordsList);
        }


        List<CoordsAllSystem> allSystems = new ArrayList<>();

        for (int i = 0; i < notInCacheList.size(); i++) {
            Coords coords = coordsListResult.get(i);
            Integer key = notInCacheList.get(i);
            coordsMap.put(key, coords);
            Coords fromCoords = coordsList.get(key);

            CoordsAllSystem coordsAllSystem = new CoordsAllSystem();
            coordsAllSystem.setLongitude_baidu09ll(coords.getLongitude());
            coordsAllSystem.setLatitude_baidu09ll(coords.getLatitude());
            if (from.equals(BaiduMapUtils.CoordsSystem.GC02)) {
                coordsAllSystem.setLongitude_gc02(fromCoords.getLongitude());
                coordsAllSystem.setLatitude_gc02(fromCoords.getLatitude());
            } else if (from.equals(BaiduMapUtils.CoordsSystem.WGS84)) {
                coordsAllSystem.setLongitude_wgs84(fromCoords.getLongitude());
                coordsAllSystem.setLatitude_wgs84(fromCoords.getLatitude());
            }
            allSystems.add(coordsAllSystem);
        }

//        gisInfoCache.saveOrupdateCoordsAllSystemList(from, allSystems);

        coordsMap.forEach((key, value) -> {
            coordsListAllResult.add(value);
        });

        return coordsListAllResult;
    }


}
