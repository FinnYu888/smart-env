package com.ai.apac.smartenv.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: BaiduMapReverseGeoCodingResult
 * @Description: 百度地图逆地理编码结果
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/27
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/27  21:34    panfeng          v1.0.0             修改原因
 */
@Data
public class BaiduMapReverseGeoCodingResult extends BaiduMapResult implements Serializable {

    private String _id;

    private String key;

    private Coords baiduCoords;

    public ReverseGeoResult result;

    @Data
    public static class ReverseGeoResult {
        private Coords location;
        private String formatted_address;
        private String business;
        private AddressComponent addressComponent;
        //        private List<BaiduMapPoi> pois;  //周边poi
//        private List<BaiduMapRoad> roads; //周边道路
//        private List<BaiduMapPoiregion> poiRegions; // poi 区域
        private String sematic_description;
        private String cityCode;
    }


    @Data
    public static class AddressComponent {
        /**
         * 国家
         */
        private String country;
        /**
         * 国家编码
         */
        private Integer country_code;
        /**
         * 国家英文缩写（三位）
         */
        private String country_code_iso;
        /**
         * 国家英文缩写（两位）
         */
        private String country_code_iso2;
        /**
         * 省名
         */
        private String province;
        /**
         * 城市名
         */
        private String city;
        /**
         * 城市所在级别（仅国外有参考意义。
         * 国外行政区划与中国有差异，城市对应的层级不一定为『city』。
         * country、province、city、district、town分别对应0-4级，
         * 若city_level=3，则district层级为该国家的city层级）
         */
        private Integer city_level;
        /**
         * 区县名
         */
        private String district;
        /**
         * 乡镇名
         */
        private String town;
        /**
         * 乡镇id
         */
        private String town_code;
        /**
         * 街道名（行政区划中的街道层级）
         */
        private String street;
        /**
         * 街道门牌号
         */
        private String street_number;
        /**
         * 行政区划代码 <a href="http://lbsyun.baidu.com/index.php?title=open/dev-res" >百度地图资源</a>
         * adCode:  <a href="https://mapopen-pub-webserviceapi.bj.bcebos.com/geocoding/%E8%A1%8C%E6%94%BF%E5%8C%BA%E5%88%92%E4%B9%A1%E9%95%87%E6%B8%85%E5%8D%95201910.xlsx" >adcode资源</a>
         */
        private Integer adcode;
        /**
         * 相对当前坐标点的方向，当有门牌号的时候返回数据
         */
        private String direction;
        /**
         * 相对当前坐标点的距离，当有门牌号的时候返回数据
         */
        private String distance;
    }


//    @Data
//    public static class BaiduMapPoi{
//        private String addr;
//        private String cp;
//        private String direction;
//        private String distance;
//        private String name;
//        private String tag;
//        private String point;
//        private String tel;
//        private String uid;
//        private String zip;
//        private String parent_poi;
//    }

//    @Data
//    public static class BaiduMapRoad{
//        String name;
//        String distance;
//    }

//    @Data
//    public static class BaiduMapPoiregion{
//        String direction_desc;
//        String name;
//        String tag;
//    }


}
