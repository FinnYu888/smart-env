package com.ai.apac.smartenv.wechat.feign;

import com.ai.apac.smartenv.wechat.entity.WeChatUser;
import org.springblade.core.tool.api.R;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/10/21 3:42 下午
 **/
public class IWechatUserFallbackClient implements IWechatUserClient{

    /**
     * 发送邮件
     *
     * @param userId 根据用户(帐户)ID查询微信用户信息
     * @return Menu
     */
    @Override
    public R<WeChatUser> getWechatUserByUserId(Long userId) {
        return R.fail("返回数据异常");
    }

    /**
     * 根据小程序用户ID查询微信用户信息
     *
     * @param appOpenId 根据小程序用户ID查询微信用户信息
     * @return Menu
     */
    @Override
    public R<WeChatUser> getWechatUserByAppOpenId(String appOpenId) {
        return R.fail("返回数据异常");
    }
}
