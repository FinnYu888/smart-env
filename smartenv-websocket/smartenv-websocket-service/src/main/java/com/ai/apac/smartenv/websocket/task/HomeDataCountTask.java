package com.ai.apac.smartenv.websocket.task;

import cn.hutool.core.util.RandomUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.omnic.dto.SummaryAmountForHome;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.websocket.controller.BigScreenController;
import com.ai.apac.smartenv.websocket.module.main.vo.EventVO;
import com.ai.apac.smartenv.websocket.module.main.vo.HomePageDataCountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringPool;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 首页各种数量任务
 * @author zhanglei25
 * @Date 2020/3/3 15:28 下午
 **/
@Getter
@Setter
@Slf4j
public class HomeDataCountTask extends BaseTask implements Runnable {

    public HomeDataCountTask(WebsocketTask websocketTask) {
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
    protected R<HomePageDataCountVO> execute() {
        R<HomePageDataCountVO> result = null;
        try {
            HomePageDataCountVO homePageDataCountVO = getOmnicService().getHomePageCountData(getTenantId());
            homePageDataCountVO.setTopicName(getWebsocketTask().getTopic());
            homePageDataCountVO.setActionName(getWebsocketTask().getTaskType());
            homePageDataCountVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            result = R.data(homePageDataCountVO);
            log.debug("================推送首页实时数量统计================");
        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }
}

