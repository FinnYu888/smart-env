package com.ai.apac.smartenv.common.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author qianlong
 * @description Mongo连接设置
 * @Date 2020/10/1 6:57 下午
 **/
@Component
@Slf4j
public class MongoConfig {

    @Autowired
    private MongoProperties mongoProperties;

    @Bean
    @ConditionalOnMissingBean(MongoClient.class)
    public MongoClient getMongodbClients() {
        String host = mongoProperties.getHost();
        if (StringUtils.isEmpty(host)) {
            throw new RuntimeException("MongoDB host is empty.");
        }
        String[] hosts = host.split(",");
        String[] address;
        List<ServerAddress> addresses = new ArrayList<ServerAddress>();
        for (String h : hosts) {
            if (StringUtils.isEmpty(h)) {
                log.warn("This MongoDB host [{}] is empty, continue init this host.", h);
                continue;
            }

            if (h.indexOf(":") != -1) {
                address = h.split(":");
                log.info("Init MongoDB : host [{}], port [{}]", address[0], address[1]);
                addresses.add(new ServerAddress(address[0], Integer.valueOf(address[1])));
            } else {
                log.info("Init MongoDB : host [{}], no port.", h);
                addresses.add(new ServerAddress(h));
            }
        }
        MongoClient client = null;
        if (StringUtils.isNotBlank(mongoProperties.getUsername()) && StringUtils.isNotBlank(mongoProperties.getPassword())) {
            MongoCredential credential = MongoCredential.createCredential(mongoProperties.getUsername(),
                    mongoProperties.getDatabase(), mongoProperties.getPassword().toCharArray());
            MongoClientOptions options = MongoClientOptions.builder().sslEnabled(true).build();
            client = new MongoClient(addresses, credential, options);
        } else {
            client = new MongoClient(addresses);
        }
        return client;
    }
}
