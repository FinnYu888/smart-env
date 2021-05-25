package com.ai.apac.smartenv.device.socket;

import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.buffer.BufferPagePool;
import org.smartboot.socket.extension.plugins.MonitorPlugin;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadFactory;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/5/18 11:43 上午
 **/
@Slf4j
@Component
public class SocketClient {

    private static AioQuickClient<String> aioQuickClient = null;

    private static AsynchronousChannelGroup asynchronousChannelGroup = null;

    private static String host;

    private static Integer port;

    @Value("${dmp.socketServer.host}")
    public void setHost(String host) {
        SocketClient.host = host;
    }

    @Value("${dmp.socketServer.port}")
    public void setPort(Integer port) {
        SocketClient.port = port;
    }

    public static AioQuickClient<String> getSocketClient() throws IOException {
        if (aioQuickClient == null) {
            BufferPagePool bufferPagePool = new BufferPagePool(1024 * 1024 * 32, 10, true);
            AbstractMessageProcessor<String> processor = new AbstractMessageProcessor<String>() {
                @Override
                public void process0(AioSession<String> session, String msg) {
                    log.info("收到服务端响应:" + msg);
                }

                @Override
                public void stateEvent0(AioSession<String> session, StateMachineEnum stateMachineEnum, Throwable throwable) {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    }
                }
            };
//            processor.addPlugin(new MonitorPlugin(5));
            AsynchronousChannelGroup asynchronousChannelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "ClientGroup");
                }
            });
            aioQuickClient = new AioQuickClient<>(host, port, new StringProtocol(), processor);
            aioQuickClient.setBufferPagePool(bufferPagePool);
            aioQuickClient.setWriteBuffer(1024 * 1024, 10);
            if (asynchronousChannelGroup == null) {
                asynchronousChannelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "ClientGroup");
                    }
                });
            }
        }
        return aioQuickClient;
    }

    public static AioQuickClient<String> getSimpleSocketClient() throws IOException {
        if (aioQuickClient == null) {
            aioQuickClient = new AioQuickClient<String>(host, port, new StringProtocol(), new MessageProcessor<String>() {
                @Override
                public void process(AioSession<String> session, String msg) {
                    log.info("接收到服务端返回消息:{}", msg);
                }

                @Override
                public void stateEvent(AioSession<String> session, StateMachineEnum stateMachineEnum, Throwable throwable) {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    }
                }
            });

        }
        return aioQuickClient;
    }

    /**
     * 向服务端发送消息
     *
     * @param message
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    public static void sendToServer(String message) {
        AioSession<String> session = null;
        try {
//            AioSession<String> session = getSocketClient().start(asynchronousChannelGroup);
            session = getSimpleSocketClient().start();
            byte[] data = message.getBytes();
            session.writeBuffer().write(data);
        } catch (InterruptedException ex) {
            log.error(ex.getMessage());
        } catch (ExecutionException ex) {
            log.error(ex.getMessage());
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }finally {
            if(session != null){
                session.writeBuffer().flush();
            }
        }
    }
}
