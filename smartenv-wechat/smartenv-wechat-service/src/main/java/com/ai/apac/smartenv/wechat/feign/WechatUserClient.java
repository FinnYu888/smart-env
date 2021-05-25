package com.ai.apac.smartenv.wechat.feign;

import com.ai.apac.smartenv.wechat.entity.WeChatUser;
import com.ai.apac.smartenv.wechat.service.IWeChatUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/10/21 3:46 下午
 **/

@ApiIgnore
@RestController
@RequiredArgsConstructor
@Slf4j
public class WechatUserClient implements IWechatUserClient{

    @Autowired
    private IWeChatUserService weChatUserService;

    /**
     * 根据用户(帐户)ID查询微信用户信息
     *
     * @param userId 根据用户(帐户)ID查询微信用户信息
     * @return Menu
     */
    @Override
    @GetMapping(GET_USER_BY_USERID)
    public R<WeChatUser> getWechatUserByUserId(Long userId) {
        return R.data(weChatUserService.getWechatUserByUserId(userId));
    }

    /**
     * 根据小程序用户ID查询微信用户信息
     *
     * @param appOpenId 根据小程序用户ID查询微信用户信息
     * @return Menu
     */
    @Override
    @GetMapping(GET_USER_BY_APP_USERID)
    public R<WeChatUser> getWechatUserByAppOpenId(String appOpenId) {
        return R.data(weChatUserService.getWechatUserByAppOpenId(appOpenId));
    }
}
