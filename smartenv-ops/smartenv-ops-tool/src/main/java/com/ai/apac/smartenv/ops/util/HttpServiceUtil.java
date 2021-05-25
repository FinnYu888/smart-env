package com.ai.apac.smartenv.ops.util;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/20 10:07 上午
 **/
public class HttpServiceUtil {

    /**
     * 表单方式提交
     * @param requestUrl
     * @param params
     * @param token
     * @return
     */
    public static String postForm(String requestUrl, HashMap<String, Object> params,String token) {
        HttpRequest request = HttpRequest.post(requestUrl)
                .header("Blade-Auth", token)
                .contentType(ContentType.FORM_URLENCODED.toString())
                .form(params)
                .timeout(20000);
        return request.execute().body();
    }

    /**
     * 表单方式Get请求
     * @param requestUrl
     * @param params
     * @param token
     * @return
     */
    public static String getForm(String requestUrl, HashMap<String, Object> params,String token) {
        HttpRequest request = HttpRequest.get(requestUrl)
                .header("Blade-Auth", token)
                .contentType(ContentType.FORM_URLENCODED.toString())
                .form(params);
        return request.execute().body();
    }

    /**
     * Restful方式提交
     * @param requestUrl
     * @param params
     * @param token
     * @return
     */
    public static String postRestful(String requestUrl, Object params,String token) {
        HttpRequest request = HttpRequest.post(requestUrl)
                .header("Blade-Auth", token)
                .contentType(ContentType.FORM_URLENCODED.toString())
                .body(JSONUtil.toJsonStr(params))
                .timeout(20000);
        return request.execute().body();
    }
}
