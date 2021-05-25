package com.ai.apac.smartenv.device.feign;

import org.springblade.core.tool.api.R;

import java.util.List;

/**
 * @ClassName IWatchClientFallBack
 * @Desc TODO
 * @Author qianlong
 * @Date 2020/9/18 20:08
 * @Version 1.0
 */
public class IWatchClientFallBack implements IWatchClient {

    /**
     * 发送文字消息
     *
     * @param deviceCode
     * @param message
     * @return
     */
    @Override
    public R sendText(List<String> deviceCodes, String message) {
        return R.fail("获取数据失败");
    }

    /**
     * 发送语音消息
     *
     * @param deviceCode
     * @param message
     * @return
     */
    @Override
    public R sendVoice(List<String> deviceCodes, String message) {
        return R.fail("获取数据失败");
    }

}
