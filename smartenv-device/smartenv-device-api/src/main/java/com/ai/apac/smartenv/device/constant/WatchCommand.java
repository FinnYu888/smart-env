package com.ai.apac.smartenv.device.constant;

import lombok.Data;
import org.smartboot.socket.transport.AioSession;

/**
 * @author qianlong
 * @description 手表指令内容
 * @Date 2020/4/12 5:38 下午
 **/
@Data
public class WatchCommand {

    private AioSession<String> session;

    private String deviceVendor;

    private String deviceCode;

    private String command;

    private String content;

    public WatchCommand(AioSession<String> session, String deviceVendor, String deviceCode, String command, String content) {
        this.session = session;
        this.deviceVendor = deviceVendor;
        this.deviceCode = deviceCode;
        this.command = command;
        this.content = content;
    }
}
