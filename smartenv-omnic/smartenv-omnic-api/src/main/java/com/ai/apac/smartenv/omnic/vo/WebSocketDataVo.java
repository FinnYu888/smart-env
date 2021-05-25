package com.ai.apac.smartenv.omnic.vo;

import lombok.Data;
import org.springblade.core.tool.api.R;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WebSocketData
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/5
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/5  16:33    panfeng          v1.0.0             修改原因
 */
@Data
public class WebSocketDataVo<T> extends R<T> implements Serializable {
    //响应给前端的消息类型，用于前端对消息的判断
    public Integer msgType;
}
