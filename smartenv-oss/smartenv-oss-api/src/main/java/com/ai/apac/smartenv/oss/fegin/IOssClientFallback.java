package com.ai.apac.smartenv.oss.fegin;

import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Feign失败配置
 *
 * @author qianlong
 */
@Component
public class IOssClientFallback implements IOssClient {

    /**
     * 获取文件下载链接
     *
     * @param bucketName
     * @param fileName
     * @return
     */
    @Override
    public R<String> getObjectLink(String bucketName, String fileName) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<String> putBase64Stream(String bucketName, String fileName, String data) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Map<String,String>> getObjectLinks(String bucketName, Map<String,String> fileNames){
        return R.fail("获取数据失败");
    }

	@Override
	public void removeFile(String bucketName, String fileName) {
	}

    /**
     * 上传文件
     *
     * @param uploadFile
     * @param bucketName
     * @return
     */
    @Override
    public R<String> putFile(MultipartFile uploadFile, String bucketName) {
        return R.fail("上传文件失败");
    }
}
