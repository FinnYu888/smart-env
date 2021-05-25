package com.ai.apac.smartenv.system.controller;

import cn.hutool.captcha.ICaptcha;
import com.ai.apac.smartenv.system.service.ILoginService;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/10 8:36 上午
 **/
@RestController
@AllArgsConstructor
@Api(value = "登录", tags = "登录")
@Slf4j
public class LoginController {

    private ILoginService loginService;

    /**
     * 登录
     */
    @GetMapping("/login")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "登录帐号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "登录密码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "captchaCode", value = "验证码", paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "登录", notes = "登录")
    public R<JSONObject> login(@RequestParam String account,
                               @RequestParam String password,
                               @RequestParam String captchaCode) {
        log.info("account:{}|password:{}|verificationCode:{}", account, password, captchaCode);
        if (StringUtils.isBlank(account)) {
            throw new ServiceException("请输入登录用户名");
        }
        if (StringUtils.isBlank(password)) {
            throw new ServiceException("登录密码");
        }
        if (StringUtils.isBlank(captchaCode)) {
            throw new ServiceException("验证码");
        }
        return loginService.login(account, password, captchaCode);
    }

    /**
     * 用户鉴权
     */
    @GetMapping("/auth")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "登录帐号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "登录密码", paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "用户鉴权", notes = "用户鉴权")
    public R<JSONObject> auth(@RequestParam String account,
                              @RequestParam String password,
                              @RequestHeader String authorization) {
        if (StringUtils.isBlank(account)) {
            throw new ServiceException("请输入登录用户名");
        }
        if (StringUtils.isBlank(password)) {
            throw new ServiceException("登录密码不能为空");
        }
        if (StringUtils.isBlank(authorization)) {
            throw new ServiceException("鉴权码不能为空");
        }
        return loginService.auth(account, password, authorization);
    }

    @GetMapping("/captcha")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    public void getCaptcha(@RequestHeader(value = "Authorization", required = false) String authorization, HttpServletResponse response) {
        log.info("getCaptcha:authorization:{}" + authorization);
        if (StringUtils.isBlank(authorization)) {
            throw new ServiceException("Http Header缺少Authorization参数");
        }
        ICaptcha captcha = loginService.generateCaptcha(authorization);
        ServletOutputStream os = null;
        try {
            os = response.getOutputStream();
            response.setContentType("image/jpeg");
            // 发响应头控制浏览器不要缓存图片
            response.setDateHeader("expries", -1);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            captcha.write(os);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
