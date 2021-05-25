package com.ai.apac.smartenv.system.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.SystemConstant;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.feign.IPersonUserRelClient;
import com.ai.apac.smartenv.system.entity.TenantExt;
import com.ai.apac.smartenv.system.service.ILoginService;
import com.ai.apac.smartenv.system.service.ITenantExtService;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Base64Util;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/10 4:21 下午
 **/
@Slf4j
@Service
public class LoginServiceImpl implements ILoginService {

    private static final String CAPTURE_CODE = "ABCDEFGHJKLMNOPRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";

    @Value("${smartenv.app.baseUrl}")
    private String appBaseUrl;

    private String authRequestUrl = "/smartenv-auth/oauth/token";

    @Autowired
    private ITenantExtService tenantExtService;

    @Autowired
    private BladeRedis bladeRedis;

    /**
     * 用户登录
     *
     * @param account
     * @param password
     * @param captchaCode
     * @return
     */
    @Override
    public R<JSONObject> login(String account, String password, String captchaCode) {
        log.info("2:account:{}|password:{}|verificationCode:{}", account, password, captchaCode);

        //校验验证码是否失效
        Object captchaCodeTmp = bladeRedis.get(CAPTCHA_CODE + captchaCode.toLowerCase());
        if (captchaCodeTmp == null) {
            throw new ServiceException("验证码不正确或已失效");
        }
        log.info("3:account:{}|password:{}|verificationCode:{}", account, password, captchaCode);

        //校验该操作员是否与员工绑定,如果该帐号角色只是普通用户并且没有绑定员工则不能登录
        User user = UserCache.getUserByAcct(account);
        if (user == null || user.getAccount() == null || user.getPassword() == null) { //返回空对象（每个字段为null，user！=null），通过账户判断是否存在
            throw new ServiceException("帐号或密码错误");
        }
        if (user.getRoleGroup().indexOf(SystemConstant.RoleAlias.USER) >= 0) {
            String personName = PersonUserRelCache.getPersonNameByUser(user.getId());
            if (StringUtils.isBlank(personName)) {
                throw new ServiceException("该帐号尚未和任何员工绑定,无法登录,请联系管理员");
            }
        }
        JSONObject res = this.auth(account, password, AUTHORIZATION).getData();
        String tenantId = res.getString("tenant_id");
        //新增租户扩展信息。
        QueryWrapper<TenantExt> tenantExtQuery = new QueryWrapper<TenantExt>();
        tenantExtQuery.lambda().eq(TenantExt::getTenantId, tenantId);
        TenantExt tenantExt = tenantExtService.getOne(tenantExtQuery);
        if (ObjectUtil.isNotEmpty(tenantExt)) {
            if (ObjectUtil.isNotEmpty(tenantExt.getMapZoom())) {
                res.put("map_zoom", tenantExt.getMapZoom());
            }
            if (ObjectUtil.isNotEmpty(tenantExt.getWebTitle())) {
                res.put("web_title", tenantExt.getWebTitle());
            }
            if (ObjectUtil.isNotEmpty(tenantExt.getAppTitle())) {
                res.put("app_title", tenantExt.getAppTitle());
            }
            if (ObjectUtil.isNotEmpty(tenantExt.getScreenTitle())) {
                res.put("screen_title", tenantExt.getScreenTitle());
            }
            if (ObjectUtil.isNotEmpty(tenantExt.getLogoUri())) {
                res.put("logo_uri", tenantExt.getLogoUri());
            }
        }
        //将登录帐号与登录密码缓存起来,用于后续切换项目时候使用
        String loginPassword = Base64Util.decode(password);
        bladeRedis.setEx("smartenv:account:" + StringPool.COLON + account, loginPassword, CacheNames.ExpirationTime.EXPIRATION_TIME_24HOURS);
        return R.data(res);

    }

    /**
     * 用户登录获取token
     *
     * @param account
     * @param password
     * @param authorization
     * @return
     */
    @Override
    public R<JSONObject> auth(String account, String password, String authorization) {
        //对密码进行base64解密
        String loginPassword = Base64Util.decode(password);

        //通过HTTP方式请求auth服务
        Map<String, Object> bodyParams = new HashMap<String, Object>();
        bodyParams.put("grant_type", "password");
        bodyParams.put("scope", "all");
        bodyParams.put("username", account);
        bodyParams.put("password", loginPassword);
        HttpResponse httpResponse = HttpUtil.createRequest(Method.POST, appBaseUrl + authRequestUrl)
                .contentType(ContentType.FORM_URLENCODED.toString())
                .header("Authorization", authorization)
                .form(bodyParams)
                .execute();
        log.info("HTTP Status is:{}", httpResponse.getStatus());
        String body = httpResponse.body();
        log.info("AUTH Response Body={}", body);
        int httpStatus = httpResponse.getStatus();
        JSONObject result = JSON.parseObject(body);
        if (httpStatus == 200) {
            return R.data(result);
        } else {
            String errorMsg = result.getString("error_description");
            if (StringUtils.isNotBlank(errorMsg)) {
                throw new ServiceException(errorMsg);
            } else {
                throw new ServiceException(result.toJSONString());
            }
        }
    }


    /**
     * 生成验证码
     *
     * @return
     */
    @Override
    public LineCaptcha generateCaptcha(String authorization) {
        if (!AUTHORIZATION.equals(authorization)) {
            throw new ServiceException("Authorization不正确");
        }
        //定义图形验证码的长和宽
        RandomGenerator randomGenerator = new RandomGenerator(CAPTURE_CODE, 4);
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(250, 100, 4, 10);
        lineCaptcha.setGenerator(randomGenerator);
        //验证码code放入缓存中,失效时间30秒
        String code = lineCaptcha.getCode().toLowerCase();
        bladeRedis.setEx(CAPTCHA_CODE + code, code, 60L);
        return lineCaptcha;
    }

}
