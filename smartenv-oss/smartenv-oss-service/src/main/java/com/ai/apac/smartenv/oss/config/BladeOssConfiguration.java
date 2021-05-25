package com.ai.apac.smartenv.oss.config;

import com.ai.apac.smartenv.oss.builder.OssBuilder;
import com.ai.apac.smartenv.oss.service.IOssService;
import lombok.AllArgsConstructor;
import org.springblade.core.oss.props.OssProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Oss配置类
 *
 * @author qianlong
 */
@Configuration
@AllArgsConstructor
public class BladeOssConfiguration {

	private OssProperties ossProperties;

	private IOssService ossService;

	@Bean
	public OssBuilder ossBuilder() {
		return new OssBuilder(ossProperties, ossService);
	}

}
