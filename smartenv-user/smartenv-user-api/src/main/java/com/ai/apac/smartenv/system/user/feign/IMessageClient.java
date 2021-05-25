package com.ai.apac.smartenv.system.user.feign;

import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.system.user.dto.UserMessageDTO;
import com.ai.apac.smartenv.system.user.entity.User;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName IMessageClient
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/3 11:02
 * @Version 1.0
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_USER_NAME
)
public interface IMessageClient {

    String API_PREFIX = "/client";
    String UNREAD_MESSAGE = API_PREFIX + "/unread-message";
    /**
     * 获取用户信息
     *
     * @param userId 用户id
     * @return
     */
    @GetMapping(UNREAD_MESSAGE)
    R<UserMessageDTO> unReadMessage(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);
}
