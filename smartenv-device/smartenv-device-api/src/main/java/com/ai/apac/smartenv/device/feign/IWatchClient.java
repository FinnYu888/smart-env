package com.ai.apac.smartenv.device.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/9/18 8:54 上午
 **/
@FeignClient( value = ApplicationConstant.APPLICATION_DEVICE_NAME,
        fallback = IWatchClientFallBack.class
)
public interface IWatchClient {

    String API_PREFIX = "/client";
    String API_SEND_TEXT = API_PREFIX + "/send-text";
    String API_SEND_VOICE = API_PREFIX + "/send-voice";

    /**
     * 发送文字消息
     * @param deviceCodes
     * @param message
     * @return
     */
    @PostMapping(API_SEND_TEXT)
    R sendText(@RequestParam List<String> deviceCodes, @RequestParam String message);

    /**
     * 发送语音消息
     * @param deviceCodes
     * @param message
     * @return
     */
    @PostMapping(API_SEND_VOICE)
    R sendVoice(@RequestParam List<String> deviceCodes, @RequestParam String message);
}
