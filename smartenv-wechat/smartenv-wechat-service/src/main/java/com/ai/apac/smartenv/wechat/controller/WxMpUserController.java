package com.ai.apac.smartenv.wechat.controller;

import com.ai.apac.smartenv.wechat.dto.BindWxMpUserDTO;
import com.ai.apac.smartenv.wechat.service.IWeChatUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/9/1 10:48 上午
 **/
@RestController
@RequestMapping("/wx/mp/user")
@Slf4j
@Api(value = "微信公众号用户管理", tags = "微信公众号用户管理")
public class WxMpUserController {

    @Autowired
    private IWeChatUserService weChatUserService;

    @ApiOperation("微信公众号绑定指定用户")
    @PostMapping("/bindUser")
    public R bindUser(@RequestBody BindWxMpUserDTO bindWxMpUserDTO) {
        return weChatUserService.bindMpAccount(bindWxMpUserDTO.getAccount(), bindWxMpUserDTO.getPassword(), bindWxMpUserDTO.getMpOpenId());
    }
}
