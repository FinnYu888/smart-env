package com.ai.apac.smartenv.omnic.controller.base;

import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.omnic.vo.WebSocketDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.jwt.JwtUtil;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: BaseSmartenvWebsocketController
 * @Description: 环卫项目websocket基本实现类
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/12  14:17    panfeng          v1.0.0             修改原因
 */
@Deprecated
public abstract class BaseSmartenvWebsocketController extends TextWebSocketHandler {

    private Logger logger = LoggerFactory.getLogger(BaseSmartenvWebsocketController.class);

    private static ConcurrentHashMap<WebSocketSession, BladeUser> concurrentHashMap = new ConcurrentHashMap();

    /**
     * 处理websocket消息
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected final void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 提取鉴权信息，存储当前session到map中。
        WebSocketDataVo r = null;
        JSONObject jsonObject = JSONObject.parseObject(new String(message.asBytes()));
        if (jsonObject == null) {
            r = new WebSocketDataVo();
            r.setMsgType(OmnicConstant.WebSocketRespMsgType.CONNECTION_MSG);
            r.setSuccess(false);
            r.setCode(401);
            r.setMsg("未知的数据格式");
            WebSocketMessage<?> textmsg = new TextMessage(JSON.toJSONString(r));
            session.sendMessage(textmsg);
            session.close();
            return;
        }
        String dataType = jsonObject.getString("dataType");

        switch (dataType) {
            case "Authorization"://鉴权
                authorization(session, message);
                break;
            default:{
                BladeUser bladeUser = concurrentHashMap.get(session);
                if(bladeUser==null){
                    handNotAuthorizationMessage(session);
                    return;
                }
                handBusinessMessage(session,dataType,jsonObject.getJSONObject("data"));
            }

        }
    }

    /**
     * 处理业务数据
     * @param session
     * @param msgType 消息类型
     * @param data 数据
     * @throws Exception
     */

    protected abstract void handBusinessMessage(WebSocketSession session,String msgType,JSONObject data) throws Exception;

    //当鉴权成功之后
    protected void afterAuthorizationSuccess(WebSocketSession session) throws Exception{
        WebSocketDataVo r = new WebSocketDataVo();
        r.setSuccess(false);
        r.setCode(200);
        r.setMsg("鉴权成功");
        WebSocketMessage<?> msg = new TextMessage(JSON.toJSONString(r));
        session.sendMessage(msg);
    }
    //处理当未进行鉴权的消息
    protected void handNotAuthorizationMessage(WebSocketSession session) throws Exception{
        WebSocketDataVo r = new WebSocketDataVo();
        r.setSuccess(false);
        r.setCode(401);
        r.setMsg("请求未鉴权");
        WebSocketMessage<?> msg = new TextMessage(JSON.toJSONString(r));
        session.sendMessage(msg);
        session.close();
    }


    //鉴权失败
    protected void afterAuthorizationfail(WebSocketSession session) throws Exception{
        WebSocketDataVo r = new WebSocketDataVo();
        r.setCode(401);
        r.setSuccess(false);
        r.setMsg("鉴权失败");
        r.setMsgType(OmnicConstant.WebSocketRespMsgType.CONNECTION_MSG);
        WebSocketMessage<?> msg = new TextMessage(JSON.toJSONString(r));
        session.sendMessage(msg);
        session.close();
    }

    /**
     * 鉴权
     * @param session
     * @param message
     * @return
     * @throws IOException
     */
    private void authorization(WebSocketSession session, TextMessage message) throws Exception {
        WebSocketDataVo r = new WebSocketDataVo();
        JSONObject jsonObject = JSONObject.parseObject(new String(message.asBytes()));
        JSONObject data = jsonObject.getJSONObject("data");
        String auth = data.getString("blade-auth");
        if (!StringUtil.isNotBlank(auth)) {
            afterAuthorizationfail(session);
        }
        String token = JwtUtil.getToken(auth);
        Claims claims = JwtUtil.parseJWT(token);
        if (claims == null) {
            afterAuthorizationfail(session);
        } else {
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
            concurrentHashMap.put(session,bladeUser);
            afterAuthorizationSuccess(session);
        }
    }

    /**
     * 是否允许半包。用Json作为承载数据的，禁止使用半包
     * @return
     */
    @Override
    public final boolean supportsPartialMessages() {
        return false;
    }


}
