package com.ai.apac.smartenv.oss.builder;

import com.ai.apac.smartenv.oss.entity.Oss;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.SneakyThrows;
import org.springblade.core.oss.OssTemplate;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.OssRule;
import org.springblade.core.oss.tencent.TencentCosTemplate;

/**
 * 腾讯云存储构建类
 *
 * @author qianlong
 */
public class TencentOssBuilder {

	@SneakyThrows
	public static OssTemplate template(Oss oss, OssRule ossRule) {
		// 创建配置类
		OssProperties ossProperties = new OssProperties();
		ossProperties.setEndpoint(oss.getEndpoint());
		ossProperties.setAccessKey(oss.getAccessKey());
		ossProperties.setSecretKey(oss.getSecretKey());
		ossProperties.setBucketName(oss.getBucketName());
		ossProperties.setAppId(oss.getAppId());
		ossProperties.setRegion(oss.getRegion());
		// 初始化用户身份信息（secretId, secretKey）
		COSCredentials credentials = new BasicCOSCredentials(ossProperties.getAccessKey(), ossProperties.getSecretKey());
		// 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
		Region region = new Region(ossProperties.getRegion());
		// clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
		ClientConfig clientConfig = new ClientConfig(region);
		// 设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
		clientConfig.setMaxConnectionsCount(1024);
		// 设置Socket层传输数据的超时时间，默认为50000毫秒。
		clientConfig.setSocketTimeout(50000);
		// 设置建立连接的超时时间，默认为50000毫秒。
		clientConfig.setConnectionTimeout(50000);
		// 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
		clientConfig.setConnectionRequestTimeout(1000);
		COSClient cosClient = new COSClient(credentials, clientConfig);
		return new TencentCosTemplate(cosClient, ossProperties, ossRule);
	}

}
