package com.ai.apac.smartenv.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.utils.OkhttpUtil;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.service.ITokenService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.AllArgsConstructor;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static com.ai.apac.smartenv.system.cache.EntityCategoryCache.bladeRedisCache;
import static org.springblade.core.cache.constant.CacheConstant.DICT_CACHE;

/**
 * @ClassName TokenService
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/6/9 11:13
 * @Version 1.0
 */
@Service
@AllArgsConstructor
public class TokenService implements ITokenService {

    private static final String THIRD_CODE = "code:";

    @Autowired
    private BladeRedisCache bladeRedisCache;

    @Override
    public String getMiniCreateToken(boolean init) {
        String name = "miniCreate";
        try {
            String token = null;
             String key = CacheNames.TOKEN + ":" +THIRD_CODE + name;
            if(!init){
                token = bladeRedisCache.get(key);
                if (token != null) {
                    return token;
                }
            }
            String user = DictCache.getValue(CommonConstant.DICT_THIRD_INFO,CommonConstant.DICT_THIRD_KEY.MINICREATE_USER_KEY);
            String password = DictCache.getValue(CommonConstant.DICT_THIRD_INFO,CommonConstant.DICT_THIRD_KEY.MINICREATE_PASSWORD_KEY);
            String value = DictCache.getValue(CommonConstant.DICT_THIRD_PATH, CommonConstant.DICT_THIRD_KEY.MINICREATE_LOGIN_KEY);
            String uri = value.split(" ")[1];
            uri = StrUtil.format(uri,user,password);
            String resStr = OkhttpUtil.getSync(uri).body().string();
            if (ObjectUtil.isNotEmpty(resStr)) {
                JSONObject res = JSONUtil.parseObj(resStr);
                int result = res.getInt("Result");
                if (0 == result) {
                    token = res.getStr("Token");
                    bladeRedisCache.setEx(key, token, 7200L);
                    return token;
                }
            }

        } catch (IOException e) {
            throw new ServiceException("获取点创科技Token失败");
        }
        return null;
    }
}
