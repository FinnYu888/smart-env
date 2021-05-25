package com.ai.apac.smartenv.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author qianlong
 * @description MongoDB连接配置
 * @Date 2020/10/1 6:55 下午
 **/
@Component
@Validated
@Data
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MongoProperties {

    private String host;
    private String username;
    private String password;
    private String database;
}
