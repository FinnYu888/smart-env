package com.ai.apac.smartenv.common.launch;

import com.ai.apac.smartenv.common.constant.LauncherConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.auto.service.AutoService;
import org.springblade.core.launch.service.LauncherService;
import org.springblade.core.launch.utils.PropsUtil;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 启动参数拓展
 *
 * @author qianlong
 */
@Component
@AutoService(LauncherService.class)
@Slf4j
public class LauncherServiceImpl implements LauncherService {

    @Override
    public void launcher(SpringApplicationBuilder builder, String appName, String profile, boolean isLocalDev) {
        Properties props = System.getProperties();
        String nacosAddr = props.getProperty("NACOS_ADDR");
        String nacosNameSpace = props.getProperty("NACOS_NAMESPACE");
        String nacosGroup = props.getProperty("NACOS_GROUP");
        log.info("-------------------外部启动参数begin-------------------");
        log.info("NACOS_ADDR:{}", nacosAddr);
        log.info("NACOS_NAMESPACE:{}", nacosNameSpace);
        log.info("NACOS_GROUP:{}", nacosGroup);
        log.info("-------------------外部启动参数end-------------------");
        // 通用注册
        if (StringUtils.isNotBlank(nacosAddr)) {
            PropsUtil.setProperty(props, "spring.cloud.nacos.discovery.server-addr", nacosAddr);
            PropsUtil.setProperty(props, "spring.cloud.nacos.config.server-addr", nacosAddr);
        } else {
            PropsUtil.setProperty(props, "spring.cloud.nacos.discovery.server-addr", LauncherConstant.nacosAddr(profile));
            PropsUtil.setProperty(props, "spring.cloud.nacos.config.server-addr", LauncherConstant.nacosAddr(profile));
        }
        if (StringUtils.isNotBlank(nacosGroup)) {
            PropsUtil.setProperty(props, "spring.cloud.nacos.config.group", nacosGroup);
            PropsUtil.setProperty(props, "spring.cloud.nacos.discovery.group", nacosGroup);
            String property = props.getProperty("spring.cloud.nacos.config.shared-dataids");
            if (StringUtils.isNotBlank(property)) {
                String[] dataIds = property.split(",");
                for (int i = 0; i < dataIds.length; i++) {
                    PropsUtil.setProperty(props, "spring.cloud.nacos.config.ext-config[" + i + "].data-id", dataIds[i]);
                    PropsUtil.setProperty(props, "spring.cloud.nacos.config.ext-config[" + i + "].group", nacosGroup);
                }
            }
        }
        if (StringUtils.isNotBlank(nacosNameSpace)) {
            PropsUtil.setProperty(props, "spring.cloud.nacos.config.namespace", nacosNameSpace);
            PropsUtil.setProperty(props, "spring.cloud.nacos.discovery.namespace", nacosNameSpace);
            PropsUtil.setProperty(props, "spring.cloud.nacos.config.namespace", LauncherConstant.NACOS_NAMESPACE);
            PropsUtil.setProperty(props, "spring.cloud.nacos.discovery.namespace", LauncherConstant.NACOS_NAMESPACE);
        }
//        PropsUtil.setProperty(props, "spring.cloud.nacos.discovery.server-addr", LauncherConstant.nacosAddr(profile));
//        PropsUtil.setProperty(props, "spring.cloud.nacos.config.server-addr", LauncherConstant.nacosAddr(profile));
//        PropsUtil.setProperty(props, "spring.cloud.nacos.config.group", "DEFAULT_GROUP");
//        PropsUtil.setProperty(props, "spring.cloud.nacos.discovery.group", "DEFAULT_GROUP");
//		PropsUtil.setProperty(props, "spring.cloud.nacos.config.namespace", LauncherConstant.NACOS_NAMESPACE);
//		PropsUtil.setProperty(props, "spring.cloud.nacos.discovery.namespace", LauncherConstant.NACOS_NAMESPACE);
//        PropsUtil.setProperty(props, "spring.cloud.sentinel.transport.dashboard", LauncherConstant.sentinelAddr(profile));

    }

}
