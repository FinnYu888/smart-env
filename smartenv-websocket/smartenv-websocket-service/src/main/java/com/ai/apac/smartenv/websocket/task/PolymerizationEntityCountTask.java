package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.controller.BigScreenController;
import com.ai.apac.smartenv.websocket.module.main.vo.HomePageDataCountVO;
import com.ai.apac.smartenv.websocket.module.polymerization.vo.PolymerizationCountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringPool;

/**
 * Copyright: Copyright (c) 2020/9/22 Asiainfo
 *
 * @ClassName: PolymerizationEntityCountTask
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/9/22
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/9/22  14:47    zhanglei25          v1.0.0             修改原因
 */
@Slf4j
public class PolymerizationEntityCountTask extends BaseTask<PolymerizationCountVO> implements Runnable {


    public PolymerizationEntityCountTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }

    @Override
    protected R<PolymerizationCountVO> execute() {
        R<PolymerizationCountVO> result = null;
        try {
            PolymerizationCountVO polymerizationCountVO = getPolymerizationService().getPolymerizationEntityCount(getTenantId());
            polymerizationCountVO.setTopicName(getWebsocketTask().getTopic());
            polymerizationCountVO.setActionName(getWebsocketTask().getTaskType());
            polymerizationCountVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            result = R.data(polymerizationCountVO);
        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;



    }
}
