package com.ai.apac.smartenv.device.feign;

import com.ai.apac.smartenv.device.service.IWatchService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/9/18 9:00 上午
 **/
//@ApiIgnore
@Api(value = "手表操作Feign", tags = "手表操作Feign")
@RestController
@AllArgsConstructor
public class WatchClient implements IWatchClient{

    private IWatchService watchService;

    /**
     * 发送文字消息
     *
     * @param deviceCodes
     * @param message
     * @return
     */
    @PostMapping(API_SEND_TEXT)
    @Override
    public R sendText(@RequestParam List<String> deviceCodes,@RequestParam  String message) {
        if (message.trim().length() > 15) {
            throw new ServiceException("不能超过15个字符");
        }
        if(ObjectUtil.isNotEmpty(deviceCodes) && deviceCodes.size()>0){
            deviceCodes.forEach(deviceCode->{
                watchService.sendMessage(deviceCode, message);
            });
        }
        return R.status(true);
    }

    /**
     * 发送语音消息
     *
     * @param deviceCodes
     * @param message
     * @return
     */
    @PostMapping(API_SEND_VOICE)
    @Override
    public R sendVoice(@RequestParam List<String> deviceCodes, @RequestParam String message) {
        if(ObjectUtil.isNotEmpty(deviceCodes) && deviceCodes.size()>0){
            deviceCodes.forEach(deviceCode->{
                watchService.sendMessage2Voice(deviceCode, message);
            });
        }
        return R.status(true);    }
}
