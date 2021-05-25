package com.ai.apac.smartenv.wechat.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.constant.ParamConstant;
import com.ai.apac.smartenv.common.constant.WeChatConstant;
import com.ai.apac.smartenv.system.cache.ParamCache;
import com.ai.apac.smartenv.wechat.config.WxMaConfiguration;
import com.ai.apac.smartenv.wechat.config.WxMaProperties;
import com.ai.apac.smartenv.wechat.dto.PublicAuthInfoDTO;
import com.ai.apac.smartenv.wechat.entity.WeChatUser;
import com.ai.apac.smartenv.wechat.service.IWeChatUserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.Base64Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 微信小程序用户接口
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@RestController
@RequestMapping("/wx/user")
@Slf4j
@Api(value = "用户管理", tags = "用户管理")
@EnableConfigurationProperties(WxMaProperties.class)
public class WxMaUserController {

    @Autowired
    private IWeChatUserService weChatUserService;

    @Autowired
    private WxMaProperties waProperties;

    /**
     * 登陆接口
     */
    @ApiOperationSupport(order = 1)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "登录帐号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "登录密码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "code", value = "微信认证code", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "appid", value = "小程序APPID", paramType = "query", dataType = "string", defaultValue = "wx9d33bcb12eba9f67")
    })
    @ApiOperation(value = "登录", notes = "登录")
    @GetMapping("/login")
    public R<JSONObject> login(@RequestParam(required = false) String appid,
                               @RequestParam String account, @RequestParam String password, @RequestParam String code) {
        if (StringUtils.isBlank(appid)) {
            appid = WeChatConstant.DEFAULT_APP_ID;
        }

        if (StringUtils.isBlank(code)) {
            throw new ServiceException("empty jscode");
        }

        final WxMaService wxService = WxMaConfiguration.getMaService(appid);
        WxMaJscode2SessionResult session = null;
        try {
            session = wxService.getUserService().getSessionInfo(code);
            log.info(session.getSessionKey());
            log.info(session.getOpenid());
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("微信登录异常");
        }
        return weChatUserService.auth(account, password, session);
    }

    /**
     * 公众鉴权接口
     */
//    @ApiOperationSupport(order = 1)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "sign", value = "密钥", paramType = "header", dataType = "string"),
//            @ApiImplicitParam(name = "account", value = "登录帐号", paramType = "query", dataType = "string"),
//            @ApiImplicitParam(name = "password", value = "登录密码", paramType = "query", dataType = "string"),
//            @ApiImplicitParam(name = "code", value = "微信认证code", paramType = "query", dataType = "string"),
//            @ApiImplicitParam(name = "appid", value = "小程序APPID", paramType = "query", dataType = "string", defaultValue = "wx9d33bcb12eba9f67")
//    })
//    @ApiOperation(value = "公众鉴权接口", notes = "公众鉴权接口")
//    @GetMapping("/publicAuth")
//    public R<JSONObject> publicAuth(@RequestHeader String sign, @RequestParam(required = false) String appid,
//                                    @RequestParam String account, @RequestParam String password, @RequestParam String code) {
//        //对authToken进行校验
//        String secretKey = ParamCache.getValue(ParamConstant.WechatMaLoginSecret.WX_MA_SECRET_KEY);
//        String loginAccount = ParamCache.getValue(ParamConstant.WechatMaLoginSecret.WX_MA_ACCOUNT);
//        String loginPassword = ParamCache.getValue(ParamConstant.WechatMaLoginSecret.WX_MA_PASSWORD);
//        log.info(secretKey);
//        //对传入参数进行校验,判断是否正确
//        String inputParams = SecureUtil.sha1(account + password + sign);
//        String compareStr = SecureUtil.sha1(loginAccount + Base64Util.encode(loginPassword) + secretKey);
//        if (!inputParams.equals(compareStr)) {
//            throw new ServiceException("非法的客户端访问");
//        }
//        String realLoginAccount = ParamCache.getValue(ParamConstant.WechatMaLoginSecret.WX_MA_ACCOUNT);
//        String realLoginPassword = ParamCache.getValue(ParamConstant.WechatMaLoginSecret.WX_PASSWORD);
//        return login(appid, realLoginAccount, realLoginPassword, code);
//    }

    /**
     * 解密用户信息
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "解密用户信息", notes = "解密用户信息")
    @GetMapping("/wxUserInfo")
    public R<WxMaUserInfo> getUserInfo(@RequestParam String appid, @RequestParam String code, @RequestParam String encryptedData, @RequestParam String iv) {
        final WxMaService wxService = WxMaConfiguration.getMaService(appid);
        WxMaJscode2SessionResult session = null;
        try {
            session = wxService.getUserService().getSessionInfo(code);
            WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(session.getSessionKey(), encryptedData, iv);
            if (userInfo != null) {
                //更新数据库中最新的数据
                weChatUserService.saveOrUpdateWechatUser(userInfo);
            }
            return R.data(userInfo);
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("解密微信用户信息异常");
        }
    }

    /**
     * 微信用户认证
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "公众鉴权接口", notes = "公众鉴权接口")
    @GetMapping("/publicAuth")
    public R<PublicAuthInfoDTO> publicAuth(@RequestParam String appid, @RequestParam String code, @RequestParam String encryptedData, @RequestParam String iv) {
        final WxMaService wxService = WxMaConfiguration.getMaService(appid);
        WxMaJscode2SessionResult session = null;
        try {
//            session = wxService.getUserService().getSessionInfo(code);
//            WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(session.getSessionKey(), encryptedData, iv);
            WxMaUserInfo userInfo = new WxMaUserInfo();
            userInfo.setOpenId("1122334");
            userInfo.setUnionId("qianlong");
            return R.data(weChatUserService.publicAuth(userInfo));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("鉴权异常,请稍候再试");
        }
    }

    /**
     * <pre>
     * 获取用户信息接口
     * </pre>
     */
    @ApiOperation(value = "获取用户信息接口", notes = "获取用户信息接口")
    @GetMapping("/info")
    public String info(@RequestParam(required = false, defaultValue = "wx9d33bcb12eba9f67") String appid,
                       @RequestParam String sessionKey,
                       String signature, String rawData, String encryptedData, String iv) {
        final WxMaService wxService = WxMaConfiguration.getMaService(appid);

        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return "user check failed";
        }

        // 解密用户信息
        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);

        return JSON.toJSONString(userInfo);
    }

    /**
     * <pre>
     * 获取用户绑定手机号信息
     * </pre>
     */
    @ApiOperation(value = "获取用户绑定手机号信息", notes = "获取用户绑定手机号信息")
    @GetMapping("/phone")
    public String phone(@PathVariable String appid, String sessionKey, String signature,
                        String rawData, String encryptedData, String iv) {
        final WxMaService wxService = WxMaConfiguration.getMaService(appid);

        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return "user check failed";
        }

        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);

        return JSON.toJSONString(phoneNoInfo);
    }

    public static void setWxUserValue(WeChatUser weChatUser, WxMaUserInfo userWxInfo) {
        weChatUser.setAppOpenId(userWxInfo.getOpenId());
        weChatUser.setNickName(userWxInfo.getNickName());
        weChatUser.setUnionId(userWxInfo.getUnionId());
        weChatUser.setAvatar(userWxInfo.getAvatarUrl());
    }
}
