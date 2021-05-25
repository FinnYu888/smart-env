package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.module.main.vo.EventVO;
import com.ai.apac.smartenv.websocket.module.main.vo.Last10EventVO;
import com.ai.apac.smartenv.websocket.module.main.vo.Last6OrderVO;
import com.ai.apac.smartenv.websocket.module.main.vo.ResOrder4HomeVO;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020/8/19 Asiainfo
 *
 * @ClassName: InventoryTask
 * @Description: 首页显示的ResOrderVO任务
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  17:23    zhanglei25          v1.0.0             修改原因
 */
@Getter
@Setter
@Slf4j
public class ResOrderTask extends BaseTask implements Runnable {

    public ResOrderTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }


    @Override
    public void run() {
        runTask();
    }

    /**
     * 具体的执行方法,由子类实现
     */
    @Override
    protected R<Last6OrderVO> execute() {
        R<Last6OrderVO> result = null;
        Last6OrderVO last6OrderVO = new Last6OrderVO();
        List<ResOrder4HomeVO> resOrder4HomeVOList = new ArrayList<ResOrder4HomeVO>();
        try {
            resOrder4HomeVOList = getResOrderService().getLast6Order(getTenantId(),getWebsocketTask().getUserId());
            last6OrderVO.setResOrder4HomeVOList(resOrder4HomeVOList);
            last6OrderVO.setTopicName(getWebsocketTask().getTopic());
            last6OrderVO.setActionName(getWebsocketTask().getTaskType());
            last6OrderVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            result = R.data(last6OrderVO);

        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }
}
