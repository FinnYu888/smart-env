package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.websocket.module.mock.dto.PutTrackDTO;
import com.ai.apac.smartenv.websocket.module.mock.dto.PutTrackTaskDTO;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/10 5:46 下午
 **/
public interface IBigDataMockService {

    /**
     * 模拟上报轨迹数据
     * @param putTrackTaskDTO
     */
    String putTrackTask(PutTrackTaskDTO putTrackTaskDTO);

    /**
     * 单次上报轨迹
     * @param putTrackDTO
     * @return
     */
    String putTrackSingle(PutTrackDTO putTrackDTO);
}
