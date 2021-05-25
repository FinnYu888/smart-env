package com.ai.apac.smartenv.oss.cache;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.enums.VehicleStatusImgEnum;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/17 6:12 下午
 **/
public class OssCache {

    private static final String IMG_MAP = "smartenv:oss:imgMap";

    private static BladeRedis bladeRedis = null;

    private static IOssClient ossClient = null;

    /**
     * 过期时间24小时,单位是秒
     */
    private static final Long EXPIRATION_TIME = 86400L;

    private static IOssClient getOssClient() {
        if (ossClient == null) {
            ossClient = SpringUtil.getBean(IOssClient.class);
        }
        return ossClient;
    }

    private static BladeRedis getBladeRedis() {
        if (bladeRedis == null) {
            bladeRedis = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedis;
    }

    /**
     * 从缓存中获取链接
     *
     * @param bucketName
     * @param fileName
     * @return
     */
    public static String getLink(String bucketName, String fileName) {
        String link = getBladeRedis().get(fileName);
        if (StringUtils.isBlank(link)) {
            R<String> result = getOssClient().getObjectLink(bucketName, fileName);
            if (result.isSuccess() && result.getData() != null) {
                link = result.getData();
                getBladeRedis().setEx(fileName, link, CacheNames.ExpirationTime.EXPIRATION_TIME_10MIN);
                return link;
            }
        }
        return link;
    }
}
