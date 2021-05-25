package com.ai.apac.smartenv.system.user.controller;

import com.ai.apac.smartenv.system.user.service.IMiniAppHomeService;
import com.ai.apac.smartenv.system.user.vo.MiniAppHomeDataCountVO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName MiniAppHomeController
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/15 14:03
 * @Version 1.0
 */
@RestController
@RequestMapping("/miniapp")
@AllArgsConstructor
@Api(value = "小程序首页相关接口", tags = "小程序首页相关接口")
public class MiniAppHomeController {

    private IMiniAppHomeService miniAppHomeService;

    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取小程序首页统计数字")
    @GetMapping("/dataCount")
    @ApiLog(value = "获取小程序首页统计数字")
    public R<MiniAppHomeDataCountVO> getMiniAppHomeDataCount() {
        return R.data(miniAppHomeService.getMiniAppHomeDataCount());
    }
}
