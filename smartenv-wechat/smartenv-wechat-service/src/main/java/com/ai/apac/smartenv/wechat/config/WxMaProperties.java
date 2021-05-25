package com.ai.apac.smartenv.wechat.config;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Data
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxMaProperties {

    private List<Config> configs;

    @Data
    public static class Config {
        /**
         * 设置微信小程序的appid
         */
        private String appid;

        /**
         * 设置微信小程序的Secret
         */
        private String secret;

        /**
         * 设置微信小程序消息服务器配置的token
         */
        private String token;

        /**
         * 设置微信小程序消息服务器配置的EncodingAESKey
         */
        private String aesKey;

        /**
         * 消息格式，XML或者JSON
         */
        private String msgDataFormat;
    }

    /**
     * 根据APPID获取配置
     * @param appId
     * @return
     */
    public Config getConfigByAppId(String appId) {
        if (CollUtil.isNotEmpty(configs)) {
            for (Config config : configs) {
                if(appId.equalsIgnoreCase(config.getAppid())){
                    return config;
                }
            }
        }
        return null;
    }

}
