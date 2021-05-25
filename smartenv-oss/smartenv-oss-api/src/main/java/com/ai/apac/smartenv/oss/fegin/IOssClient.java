package com.ai.apac.smartenv.oss.fegin;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import io.swagger.annotations.ApiParam;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Feign接口类
 *
 * @author qianlong
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_OSS_NAME,
        fallback = IOssClientFallback.class
)
public interface IOssClient {

    String API_PREFIX = "/client";
    String OBJECT_LINK = API_PREFIX + "/objectLink";
    String OBJECT_LINKS = API_PREFIX + "/objectLinks";
    String OBJECT_PUT_BASE_64 = API_PREFIX + "/putBase64Stream";
    String PUT_FILE = API_PREFIX + "/putFile";
    String REMOVE_FILE = API_PREFIX + "/removeFile";

    /**
     * 获取文件下载链接
     *
     * @param bucketName
     * @param fileName
     * @return
     */
    @GetMapping(OBJECT_LINK)
    R<String> getObjectLink(@RequestParam("bucketName") String bucketName, @RequestParam("fileName") String fileName);

    @PostMapping(OBJECT_LINKS)
    R<Map<String,String>> getObjectLinks(@RequestParam("bucketName") String bucketName, @RequestBody Map<String,String> fileNames);


    @PostMapping(OBJECT_PUT_BASE_64)
    R<String> putBase64Stream(@RequestParam("bucketName") String bucketName, @RequestParam("fileName") String fileName,@RequestBody String data);


    @PostMapping(REMOVE_FILE)
	void removeFile(@RequestParam("bucketName") String bucketName, @RequestParam("fileName") String fileName);

    /**
     * 上传文件
     * @param uploadFile
     * @param bucketName
     * @return
     */
    @PostMapping(PUT_FILE)
    R<String> putFile(@ApiParam(value = "文件对象", required = true) @RequestParam MultipartFile uploadFile,
                      @ApiParam(value = "文件桶名称", required = true, defaultValue = "smartenv") @RequestParam String bucketName);
}
