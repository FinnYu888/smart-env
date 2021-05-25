package com.ai.apac.smartenv.oss.builder;

import com.ai.apac.smartenv.oss.entity.Oss;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.SneakyThrows;
import org.springblade.core.oss.OssTemplate;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.qiniu.QiniuTemplate;
import org.springblade.core.oss.rule.OssRule;

/**
 * 七牛云存储构建类
 *
 * @author qianlong
 */
public class QiniuOssBuilder {

	@SneakyThrows
	public static OssTemplate template(Oss oss, OssRule ossRule) {
		Configuration cfg = new Configuration(Zone.autoZone());
		Auth auth = Auth.create(oss.getAccessKey(), oss.getSecretKey());
		UploadManager uploadManager = new UploadManager(cfg);
		BucketManager bucketManager = new BucketManager(auth, cfg);
		OssProperties ossProperties = new OssProperties();
		ossProperties.setEndpoint(oss.getEndpoint());
		ossProperties.setAccessKey(oss.getAccessKey());
		ossProperties.setSecretKey(oss.getSecretKey());
		ossProperties.setBucketName(oss.getBucketName());
		return new QiniuTemplate(auth, uploadManager, bucketManager, ossProperties, ossRule);
	}

}
