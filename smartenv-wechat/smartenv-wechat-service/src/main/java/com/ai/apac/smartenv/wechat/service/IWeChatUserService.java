package com.ai.apac.smartenv.wechat.service;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.ai.apac.smartenv.wechat.dto.PublicAuthInfoDTO;
import com.ai.apac.smartenv.wechat.entity.WeChatUser;
import com.alibaba.fastjson.JSONObject;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.tool.api.R;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/4/15 4:27 下午
 **/
public interface IWeChatUserService extends BaseService<WeChatUser> {

    /**
     * 用户登录获取token
     *
     * @param account
     * @param password
     * @param wxSession
     * @return
     */
    R<JSONObject> auth(String account, String password, WxMaJscode2SessionResult wxSession);

    /**
     * 将微信公众号用户ID与用户绑定
     * @param account 登录用户名或手机号
     * @param password 登录密码
     * @param mpOpenId 微信公众号ID
     * @return
     */
    R bindMpAccount(String account, String password,String mpOpenId);

    /**
     * 根据小程序用户openId获取用户信息
     * @param appOpenId
     * @return
     */
    WeChatUser getWechatUserByAppOpenId(String appOpenId);

    /**
     * 根据小程序用户unionId获取用户信息
     * @param unionId
     * @return
     */
    WeChatUser getWechatUserByUnionId(String unionId);

    /**
     * 根据用户ID获取微信用户信息
     * @param userId
     * @return
     */
    WeChatUser getWechatUserByUserId(Long userId);

    /**
     * 更新或保存微信用户信息
     * @param wxMaUserInfo
     */
    void saveOrUpdateWechatUser(WxMaUserInfo wxMaUserInfo);

    /**
     * 对微信免登录用户进行鉴权
     * @param wxMaUserInfo
     * @return
     */
    PublicAuthInfoDTO publicAuth(WxMaUserInfo wxMaUserInfo);
}
