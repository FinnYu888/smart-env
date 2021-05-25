/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.common.utils;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.utils.StringUtil;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用工具类
 *
 * @author Chill
 */
public class CommonUtil {

    static final double RAD = CommonConstant.PI / 180;

    static final Pattern CHINESE_PATTERN = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");

    static final Pattern CHAR_PATTERN = Pattern.compile("\"^[a-zA-Z0-9\\u4E00-\\u9FA5]+$\";");

    public static boolean inRegion(List<Point2D.Double> pts, Point2D.Double point) {

        if (BaiduMapUtils.IsPtInPoly(point, pts)) {
            return true;
        }
        return false;
    }


    /**
     * 计算地表两个坐标点的距离
     *
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return
     */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = lat1 * RAD;  // // RAD=π/180
        double radLat2 = lat2 * RAD;
        double a = radLat1 - radLat2;
        double b = (lng1 - lng2) * RAD;
        double s = 2 * Math.sin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * CommonConstant.RE;
        s = Math.round(s * 10000) / 10000;
        return s;
    }


    /**
     * 计算三个点形成的三角形第一个角的角度
     *
     * @param lngCen 第一个角经度
     * @param latCen 第一个角纬度
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return
     */
    public static double get_angle(double lngCen, double latCen, double lng1, double lat1, double lng2, double lat2) {
        double rd1 = getDistance(lngCen, latCen, lng1, lat1);// 计算三条边长度
        double rd2 = getDistance(lngCen, latCen, lng2, lat2);// 计算三条边长度
        double rd3 = getDistance(lng1, lat1, lng2, lat2);// 计算三条边长度


        double P = (rd1 + rd2 + rd3) / 2;// 海伦公式中的 P 参考： https://baike.baidu.com/item/%E6%B5%B7%E4%BC%A6%E5%85%AC%E5%BC%8F#1
        double S = Math.sqrt(P * (P - rd1) * (P - rd2) * (P - rd3));// 利用海伦公式求面积 https://baike.baidu.com/item/%E6%B5%B7%E4%BC%A6%E5%85%AC%E5%BC%8F#1
        double h = 2 * S / rd3; //通过面积算第一个点的对边的高

        double de1 = Math.toDegrees(Math.acos(h / rd1));//将高画出来，角1为高线和 rd1 这条边形成的夹角的角度
        double de2 = Math.toDegrees(Math.acos(h / rd2));//将高画出来，角2为高线和 rd2 这条边形成的夹角的角度

//        return Math.toDegrees(Math.acos(((rd1 * rd1) + (rd2 * rd2) - (rd3 * rd3)) / (2 * rd1 * rd2)));
        return de1 + de2;


    }

    /**
     * 根据真实姓名获取呢称
     *
     * @param realName
     * @return
     */
    public static String getNickName(String realName) {
        if (StringUtil.isBlank(realName)) {
            return "";
        }
        String nickName = "";
        if (realName.length() == 2) {
            nickName = realName;
        } else if (realName.length() == 3) {
            nickName = realName.substring(1, 3);
        } else if (realName.length() >= 4) {
            nickName = realName.substring(realName.length() - 2, realName.length());
        }
        return nickName;
    }

    /**
     * 字符串是否包含中文
     *
     * @param str 待校验字符串
     * @return true 包含中文字符 false 不包含中文字符
     * @throws Exception
     */
    public static boolean isContainChinese(String str) {

        if (StringUtils.isEmpty(str)) {
            return false;
        }
        Matcher m = CHINESE_PATTERN.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否只有中文、英文和数字
     *
     * @param str
     * @return
     */
    public static boolean isValidStr(String str) {
        Matcher match = CHAR_PATTERN.matcher(str);
        return match.matches();
    }

    public static void main(String[] args) {
//        double angle = get_angle(118.790599, 32.076513, 118.744319, 32.063785, 118.812374, 32.0638);
//        System.out.println(angle);

        System.out.println(isValidStr("中文123abc-)"));

    }
}
