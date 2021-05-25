package com.ai.apac.smartenv.system.service;

import cn.hutool.captcha.LineCaptcha;
import com.ai.apac.smartenv.system.dto.LoginInfoDTO;
import com.alibaba.fastjson.JSONObject;
import org.springblade.core.tool.api.R;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/10 4:19 下午
 **/
public interface ILoginService {

    String AUTHORIZATION = "Basic c2FiZXI6c2FiZXJfc2VjcmV0";

    String CAPTCHA_CODE = "smartenv:captchaCode:";

    /**
     * 用户登录
     *
     * @param account
     * @param password
     * @param captchaCode
     * @return
     */
    R<JSONObject> login(String account, String password, String captchaCode);

    /**
     * 用户登录获取token
     *
     * @param account
     * @param password
     * @param authorization
     * @return
     */
    R<JSONObject> auth(String account, String password, String authorization);

    /**
     * 生成验证码
     *
     * @return
     */
    LineCaptcha generateCaptcha(String authorization);
}
