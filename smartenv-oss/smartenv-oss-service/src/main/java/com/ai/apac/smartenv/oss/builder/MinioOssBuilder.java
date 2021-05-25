package com.ai.apac.smartenv.oss.builder;

import com.ai.apac.smartenv.oss.entity.Oss;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.springblade.core.oss.OssTemplate;
import org.springblade.core.oss.minio.MinioTemplate;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.OssRule;

/**
 * Minio云存储构建类
 *
 * @author qianlong
 */
public class MinioOssBuilder {

	@SneakyThrows
	public static OssTemplate template(Oss oss, OssRule ossRule) {
		MinioClient minioClient = new MinioClient(oss.getEndpoint(), oss.getAccessKey(), oss.getSecretKey());
		OssProperties ossProperties = new OssProperties();
		ossProperties.setEndpoint(oss.getEndpoint());
		ossProperties.setAccessKey(oss.getAccessKey());
		ossProperties.setSecretKey(oss.getSecretKey());
		ossProperties.setBucketName(oss.getBucketName());
		return new MinioTemplate(minioClient, ossRule, ossProperties);
	}

}
