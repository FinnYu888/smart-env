package com.ai.apac.smartenv.oss.feign;

import com.ai.apac.smartenv.oss.builder.OssBuilder;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.oss.service.IOssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springblade.core.oss.model.BladeFile;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.schema.Entry;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/8 11:46 上午
 **/
//@ApiIgnore
@RestController
@AllArgsConstructor
@Api(value = "对象存储管理Feign", tags = "对象存储管理Feign")
public class OssClient implements IOssClient {

//    private MinioTemplate minioTemplate;

    @Autowired
    private IOssService ossService;

    /**
     * 对象存储构建类
     */
    private OssBuilder ossBuilder;

    /**
     * 获取文件下载链接
     *
     * @param fileNames
     * @param bucketName
     * @return
     */
    @Override
    @PostMapping(OBJECT_LINKS)
    public R<Map<String,String>> getObjectLinks(String bucketName, Map<String,String> fileNames) {
//        String shareLink = minioTemplate.getShareLink(bucketName, fileName);
        Map<String,String> picMap = new HashMap<>();
        for(Map.Entry<String, String> entry : fileNames.entrySet()){
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            String shareLink = ossBuilder.template().fileLink(bucketName, mapValue);
            picMap.put(mapKey,shareLink);
        }
        return R.data(picMap);
    }

    /**
     * 批量获取文件下载链接
     *
     * @param fileName
     * @param bucketName
     * @return
     */
    @Override
    @GetMapping(OBJECT_LINK)
    public R<String> getObjectLink(String bucketName, String fileName) {
//        String shareLink = minioTemplate.getShareLink(bucketName, fileName);
        String shareLink = ossBuilder.template().fileLink(bucketName, fileName);
        return R.data(shareLink);
    }

    /**
     * @param bucketName
     * @param fileName
     * @param data
     * @return
     */
    @Override
    @PostMapping(OBJECT_PUT_BASE_64)
    public R<String> putBase64Stream(@RequestParam("bucketName") String bucketName, @RequestParam("fileName") String fileName, @RequestBody String data) {
        byte[] bytes = Base64Utils.decodeFromString(data);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
//        BladeFile bladeFile = ossService.putFile(bucketName, fileName, byteArrayInputStream);
        BladeFile bladeFile = ossBuilder.template().putFile(bucketName, fileName, byteArrayInputStream);
        return R.data(bladeFile.getName());
    }

	@Override
	@PostMapping(REMOVE_FILE)
	public void removeFile(String bucketName, String fileName) {
		ossBuilder.template().removeFile(bucketName, fileName);
	}


    /**
     * 上传文件
     *
     * @param uploadFile
     * @param bucketName
     * @return
     */
    @SneakyThrows
    @Override
    @PostMapping(PUT_FILE)
    public R<String> putFile(@ApiParam(value = "文件对象", required = true) @RequestParam MultipartFile uploadFile,
                             @ApiParam(value = "文件桶名称", required = true, defaultValue = "smartenv") @RequestParam String bucketName) {
        String originalFilename = uploadFile.getOriginalFilename();
        // 如果文件名一样会覆盖，文件名拼上当前时间
        long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        String fileName = now + "." + uploadFile.getOriginalFilename().replaceAll("[\\\\/:*?\"<>|,]", "");
        BladeFile bladeFile = ossBuilder.template().putFile(bucketName, fileName, uploadFile.getInputStream());
        bladeFile.setOriginalName(originalFilename);
        return R.data(bladeFile.getLink());
    }
}
