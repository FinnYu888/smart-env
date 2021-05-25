package com.ai.apac.smartenv.websocket.task;

import cn.hutool.core.util.RandomUtil;
import com.ai.apac.smartenv.common.constant.MessageConstant;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.system.user.dto.RelMessageDTO;
import com.ai.apac.smartenv.system.user.dto.UserMessageDTO;
import com.ai.apac.smartenv.vehicle.vo.VehicleDriverVO;
import com.ai.apac.smartenv.websocket.module.main.vo.HomePageDataCountVO;
import com.ai.apac.smartenv.websocket.module.message.vo.RelMessageVO;
import com.ai.apac.smartenv.websocket.module.message.vo.UnReadMessageCountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.BeanUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * @ClassName UnReadMessageCountTask
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/3 10:51
 * @Version 1.0
 */
@Getter
@Setter
@Slf4j
public class UnReadMessageCountTask extends BaseTask implements Runnable {

    public UnReadMessageCountTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }

    @Override
    public void run() {
        runTask();
//        try {
//            while (true) {
//                //TODO 先简单实现,每5秒执行一次,以后再用定时任务来实现
//                Thread.sleep(30000);
//            }
//        } catch (InterruptedException e) {
//            log.error("当前线程中断:", e.getMessage());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            log.error("获取统计异常:", ex.getMessage());
//            return;
//        }
    }

    /**
     * 具体的执行方法,由子类实现
     */
    @Override
    protected R<UnReadMessageCountVO> execute() {
        R<UnReadMessageCountVO> result = null;
        try {
            UnReadMessageCountVO unReadMessageCountVO = new UnReadMessageCountVO();
            Future<UserMessageDTO> UnReadMessageCountRes = getMessageService().unReadMessage(getTenantId(),getWebsocketTask().getUserId());
            if (UnReadMessageCountRes != null && UnReadMessageCountRes.get() != null) {
                UserMessageDTO userMessageDTO  = UnReadMessageCountRes.get();
                unReadMessageCountVO.setUserId(AuthUtil.getUserId().toString());
                unReadMessageCountVO.setUnReadAlarmCount(userMessageDTO.getUnReadAlarmCount());
                unReadMessageCountVO.setUnReadAnnounCount(userMessageDTO.getUnReadAnnounCount());
                unReadMessageCountVO.setUnReadEventCount(userMessageDTO.getUnReadEventCount());
                unReadMessageCountVO.setUnReadAlarmMessageList(BeanUtil.copyProperties(userMessageDTO.getAlarmMessageList(),RelMessageVO.class));
                unReadMessageCountVO.setUnReadEventMessageList(BeanUtil.copyProperties(userMessageDTO.getEventMessageList(),RelMessageVO.class));
                unReadMessageCountVO.setUnReadAnnounMessageList(BeanUtil.copyProperties(userMessageDTO.getAnnounMessageList(),RelMessageVO.class));

            }


            unReadMessageCountVO.setTopicName(getWebsocketTask().getTopic());
            unReadMessageCountVO.setActionName(getWebsocketTask().getTaskType());
            unReadMessageCountVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            result = R.data(unReadMessageCountVO);
            log.debug("================推送未读消息实时数量统计================");
        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }
}
