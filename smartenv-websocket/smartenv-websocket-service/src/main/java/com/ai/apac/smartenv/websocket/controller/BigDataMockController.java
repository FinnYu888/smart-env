package com.ai.apac.smartenv.websocket.controller;

import com.ai.apac.smartenv.websocket.module.mock.dto.PutTrackDTO;
import com.ai.apac.smartenv.websocket.module.mock.dto.PutTrackTaskDTO;
import com.ai.apac.smartenv.websocket.service.IBigDataMockService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/10 6:18 下午
 **/
@RestController
@RequestMapping("/mock/bigData")
@Api(value = "大数据Mock工具", tags = "大数据Mock工具")
public class BigDataMockController extends BladeController {

    @Autowired
    private IBigDataMockService bigDataMockService;

    /**
     * 模拟单次发送轨迹
     * @param putTrackDTO
     * @return
     */
    @PostMapping("/putTrackSingle")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "模拟单次发送轨迹", notes = "模拟单次发送轨迹")
    public R<String> putTrackSingle(PutTrackDTO putTrackDTO){
        return R.data(bigDataMockService.putTrackSingle(putTrackDTO));
    }

    /**
     * 生成定时任务按指定时长发送轨迹
     * @param putTrackTaskDTO
     * @return
     */
    @PostMapping("/putTrackTask")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "生成定时任务按指定时长发送轨迹", notes = "生成定时任务按指定时长发送轨迹")
    public R<String> putTrackBatch(PutTrackTaskDTO putTrackTaskDTO){
        return R.data(bigDataMockService.putTrackTask(putTrackTaskDTO));
    }
}
