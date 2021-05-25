package com.ai.apac.smartenv.common.enums;

import com.ai.apac.smartenv.common.constant.ResultCodeConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springblade.core.tool.api.IResultCode;
import com.ai.apac.smartenv.common.constant.ResultCodeConstant.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/2/19 6:53 下午
 **/
@Getter
@AllArgsConstructor
public enum BizResultCode implements IResultCode {

    WS_SESSION_TIME_OUT(WebSocketCode.SESSION_TIME_OUT, "The session of websocket is timeout."),
    WS_TASK_FINISHED(WebSocketCode.TASK_FINISHED, "This task is finished."),
    ;

    /**
     * code编码
     */
    final int code;
    /**
     * 中文信息描述
     */
    final String message;
}
