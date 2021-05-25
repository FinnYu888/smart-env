package com.ai.apac.smartenv.system.service;

import com.ai.apac.smartenv.system.dto.WeatherDTO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @ClassName ITokenService
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/6/9 11:11
 * @Version 1.0
 */
public interface ITokenService {

    String getMiniCreateToken(boolean init);

}
