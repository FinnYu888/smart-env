package com.ai.apac.smartenv.ops.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/20 10:04 上午
 **/
@Slf4j
public class TokenUtil {

    static final String CLIENT_ID = "bigdata";
    static final String CLIENT_SECRET = "123456";

    static final String ACCOUNT = "lichuan";
    static final String PASSWORD = "123456";

    static final String SERVER = "http://www.asiainfo.tech:31203/";
    static final String AUTH_URL = SERVER + "/smartenv-system/auth";

    static String token = null;

    public static String getAuthToken() {
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        return getToken(ACCOUNT, PASSWORD);
    }

    public static String getAuthToken(String account, String password) {
        return getToken(account, password);
    }

    private static String getToken(String account, String password) {
        // 1.先使用Base64加密生成authorization
        String authorization = Base64.encode(CLIENT_ID + ":" + CLIENT_SECRET);
        log.debug("authorization:{}", authorization);

        // 2. 使用用户名和密码获取正式的身份认证，获取令牌Token
        password = Base64.encode(password);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("account", account);
        params.put("password", password);
        String body = HttpUtil.createRequest(Method.GET, AUTH_URL)
                .contentType(ContentType.FORM_URLENCODED.toString())
                .header("authorization", "Basic " + authorization)
                .form(params)
                .execute().body();
        System.out.println(body);
        if (body != null && !body.equals("")) {
            JSONObject jsonObject = JSONUtil.parseObj(body);
            JSONObject dataResult = jsonObject.getJSONObject("data");
            token = dataResult.getStr("token_type") + " " + dataResult.getStr("access_token");
            return token;
        }
        return null;
    }

}
