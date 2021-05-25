package com.ai.apac.smartenv.ops.service.impl;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/7 1:50 下午
 **/
public class CityService {

    static final String INSERT_SQL = "insert into ai_city(id,parent_id,country,city_name,city_code,area_code,post_code,is_deleted,status,tenant_id) values"
            + "(#id,#parentId,'China','#cityName','#cityCode','#areaCode','#postCode',0,1,'000000');";

    public void generateCitySql() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL url = classLoader.getResource("citycode.json");
            File file = new File(url.getFile());
            JSONArray jsonArray = JSONUtil.readJSONArray(file, Charset.forName("UTF-8"));
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getStr("id");
                String pid = jsonObject.getStr("pid");
                String cityName = jsonObject.getStr("cityName");
                if (cityName.length() > 2) {
                    String lastName = cityName.substring(cityName.length() - 1);
                    if (lastName.indexOf("市") >= 0 || lastName.indexOf("区") >= 0 || lastName.indexOf("县") >= 0) {
                        cityName = cityName.substring(0, cityName.length() - 1);
                    }
                }
                String cityCode = jsonObject.getStr("cityCode");
                String areaCode = jsonObject.getStr("areaCode") == null ? "" : jsonObject.getStr("areaCode");
                String postCode = jsonObject.getStr("postCode") == null ? "" : jsonObject.getStr("postCode");
                String sql = INSERT_SQL.replaceAll("#id", id)
                        .replaceAll("#parentId", pid)
                        .replaceAll("#cityName", cityName)
                        .replaceAll("#cityCode", cityCode)
                        .replaceAll("#areaCode", areaCode)
                        .replaceAll("#postCode", postCode);
                System.out.println(sql);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }
    }


}
