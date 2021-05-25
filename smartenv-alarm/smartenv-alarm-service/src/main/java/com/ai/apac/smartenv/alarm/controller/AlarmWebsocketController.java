package com.ai.apac.smartenv.alarm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.jwt.JwtUtil;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: AlarmWebsocketController
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/1/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/1/19  10:04    panfeng          v1.0.0             修改原因
 */

@Component
public class AlarmWebsocketController extends TextWebSocketHandler {
    private Logger logger = LoggerFactory.getLogger(AlarmWebsocketController.class);

    private static ConcurrentHashMap<String, WebSocketSession> concurrentHashMap = new ConcurrentHashMap();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        logger.info("");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        concurrentHashMap.remove("");
        logger.info("");


    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 提取鉴权信息，存储当前session到map中。
        R r=new R();
        JSONObject jsonObject = JSONObject.parseObject(new String(message.asBytes()));
        if(jsonObject==null){
            r.setSuccess(false);
            r.setCode(401);
            r.setMsg("鉴权失败，无法识别的鉴权报文格式");
        }
        String auth = jsonObject.getString("blade-auth");
        if (!StringUtil.isNotBlank(auth)){
            r.setSuccess(false);
            r.setCode(401);
            r.setMsg("缺失令牌，鉴权失败");
        }
        String token = JwtUtil.getToken(auth);
        Claims claims = JwtUtil.parseJWT(token);
        if (claims == null) {
            r.setSuccess(false);
            r.setCode(401);
            r.setMsg("请求未鉴权");
        }else{
            String clientId = Func.toStr(claims.get("client_id"));
            Long userId = Func.toLong(claims.get("user_id"));
            String tenantId = Func.toStr(claims.get("tenant_id"));
            String deptId = Func.toStr(claims.get("dept_id"));
            String roleId = Func.toStr(claims.get("role_id"));
            String account = Func.toStr(claims.get("account"));
            String roleName = Func.toStr(claims.get("role_name"));
            String userName = Func.toStr(claims.get("user_name"));
            String nickName = Func.toStr(claims.get("nick_name"));
            BladeUser bladeUser = new BladeUser();
            bladeUser.setClientId(clientId);
            bladeUser.setUserId(userId);
            bladeUser.setTenantId(tenantId);
            bladeUser.setAccount(account);
            bladeUser.setDeptId(deptId);
            bladeUser.setRoleId(roleId);
            bladeUser.setRoleName(roleName);
            bladeUser.setUserName(userName);
            bladeUser.setNickName(nickName);
            //目前是将用户ID和session来映射，后期根据业务需要来调整
            concurrentHashMap.put(session.getId()+","+ bladeUser.getUserId().toString(),session);
            r.setSuccess(true);
            r.setCode(200);
            r.setMsg("鉴权成功");
        }
        String respMsg = JSON.toJSON(r).toString();
        session.sendMessage(new TextMessage(respMsg));
        // 如果鉴权未成功，关闭连接，防止占用websocket资源
        if(!r.isSuccess()){
            session.close();
        }
    }


    /**
     * 通过websocket 发送二进制消息到前端
     * @param client
     * @param message
     * @return
     * @throws IOException
     */
    public Boolean sendBinaryMessageToWebSocket(String client, byte[] message) throws IOException {
        WebSocketSession webSocketSession = concurrentHashMap.get(client);

        if (webSocketSession == null) {
            return false;
        }
        if (!webSocketSession.isOpen()){
            concurrentHashMap.remove(client);
            return false;
        }
        WebSocketMessage<?> msg = new BinaryMessage(message);
        webSocketSession.sendMessage(msg);
        return true;
    }

    /**
     * 通过websocket发送字符串消息到前端
     * @param client
     * @param message
     * @return
     * @throws IOException
     */
    public Boolean sendTextMessageToWebSocket(String client, String message) throws IOException {
        WebSocketSession webSocketSession = concurrentHashMap.get(client);
        if (webSocketSession == null) {
            return false;
        }
        if (!webSocketSession.isOpen()){
            concurrentHashMap.remove(client);
            return false;
        }
        WebSocketMessage<?> msg = new TextMessage(message);
        webSocketSession.sendMessage(msg);
        return true;
    }


    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
