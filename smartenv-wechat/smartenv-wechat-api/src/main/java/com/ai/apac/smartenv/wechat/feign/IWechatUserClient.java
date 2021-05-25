package com.ai.apac.smartenv.wechat.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.wechat.entity.WeChatUser;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/5/25 8:53 下午
 **/
@FeignClient(
        value = ApplicationConstant.APPLICATION_WECHAT_NAME,
        fallback = IWechatUserFallbackClient.class
)
public interface IWechatUserClient {

    String API_PREFIX = "/client";
    String GET_USER_BY_USERID = API_PREFIX + "/get-user-by-userId";
    String GET_USER_BY_APP_USERID = API_PREFIX + "/get-user-by-app-userId";

    /**
     * 根据用户(帐户)ID查询微信用户信息
     *
     * @param userId 根据用户(帐户)ID查询微信用户信息
     * @return Menu
     */
    @GetMapping(GET_USER_BY_USERID)
    R<WeChatUser> getWechatUserByUserId(@RequestParam Long userId);

    /**
     * 根据小程序用户ID查询微信用户信息
     *
     * @param appOpenId 根据小程序用户ID查询微信用户信息
     * @return Menu
     */
    @GetMapping(GET_USER_BY_APP_USERID)
    R<WeChatUser> getWechatUserByAppOpenId(@RequestParam String appOpenId);
}
