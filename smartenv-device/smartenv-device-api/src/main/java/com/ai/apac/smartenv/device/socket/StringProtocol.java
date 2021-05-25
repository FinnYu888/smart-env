package com.ai.apac.smartenv.device.socket;

import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

/**
 * @author qianlong
 */
@Component("protocol")
@Slf4j
public class StringProtocol implements Protocol<String> {
    @Override
    public String decode(ByteBuffer byteBuffer, AioSession<String> aioSession) {
        if (byteBuffer.remaining() > 0) {
            byte[] data = new byte[byteBuffer.remaining()];
            byteBuffer.get(data);
            String msg = new String(data);
            log.info("Receive the message after decode:{}", msg);
            msg = msg.toUpperCase();
            return msg;
//            return str;
            // type 1,2,3 message, see:
            // https://smartboot.gitee.io/docs/smart-socket/second/3-type-one.html
            // https://smartboot.gitee.io/docs/smart-socket/second/4-type-two.html
            // https://smartboot.gitee.io/docs/smart-socket/second/5-type-three.html
        }
        return null;
    }
}
