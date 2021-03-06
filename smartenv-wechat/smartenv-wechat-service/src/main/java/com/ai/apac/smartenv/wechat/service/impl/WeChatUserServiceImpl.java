package com.ai.apac.smartenv.wechat.service.impl;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.*;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.ParamConstant;
import com.ai.apac.smartenv.common.constant.SystemConstant;
import com.ai.apac.smartenv.common.utils.MyTokenUtil;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.system.cache.ParamCache;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.wechat.dto.PublicAuthInfoDTO;
import com.ai.apac.smartenv.wechat.entity.WeChatUser;
import com.ai.apac.smartenv.wechat.feign.WechatUserClient;
import com.ai.apac.smartenv.wechat.mapper.WeChatUserMapper;
import com.ai.apac.smartenv.wechat.service.IWeChatUserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.jwt.JwtUtil;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Base64Util;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import org.terracotta.offheapstore.HashingMap;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/4/15 4:27 ??????
 **/
@Service
@Slf4j
public class WeChatUserServiceImpl extends BaseServiceImpl<WeChatUserMapper, WeChatUser> implements IWeChatUserService {

    @Value("${smartenv.app.baseUrl}")
    private String appBaseUrl;

    private static final String AUTH_REQUEST_URL = "/smartenv-auth/oauth/token";

    private static final String AUTHORIZATION = "Basic d2VjaGF0OmFzaWFpbmZvMTIz";

    @Autowired
    private WeChatUserMapper weChatUserMapper;

    @Autowired
    private BladeRedis bladeRedis;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * ??????????????????token
     *
     * @param account
     * @param password
     * @param wxSession
     * @return
     */
    @Override
    public R<JSONObject> auth(String account, String password, WxMaJscode2SessionResult wxSession) {
        //???????????????base64??????
        String loginPassword = Base64Util.decode(password);

        //???????????????????????????????????????,??????????????????????????????????????????????????????????????????????????????
        User user = UserCache.getUserByAcct(account);
        if (user == null || user.getId() == null) {
            throw new ServiceException("?????????????????????");
        }
        if (user.getRoleGroup().indexOf(SystemConstant.RoleAlias.USER) >= 0) {
            String personName = PersonUserRelCache.getPersonNameByUser(user.getId());
            if (StringUtils.isBlank(personName)) {
                throw new ServiceException("????????????????????????????????????,????????????,??????????????????");
            }
        }

        //??????unionid

        //??????HTTP????????????auth??????
        HttpResponse httpResponse = this.authAccount(account, loginPassword);
        int httpStatus = httpResponse.getStatus();
        String body = httpResponse.body();
        JSONObject result = JSON.parseObject(body);
        if (httpStatus == HttpStatus.HTTP_OK) {
            //???????????????openId
            WeChatUser weChatUser = new WeChatUser();
            weChatUser.setUserId(Long.valueOf(result.getString("user_id")));
//            weChatUser.setTenantId(result.getString("tenant_id"));
            if (wxSession != null) {
                weChatUser.setAppOpenId(wxSession.getOpenid());
            }
            this.submitWechatUser(weChatUser);
            result.put("openId", wxSession.getOpenid());
            return R.data(result);
        } else {
            String errorMsg = result.getString("error_description");
            if (StringUtils.isNotBlank(errorMsg)) {
                throw new ServiceException(errorMsg);
            } else {
                throw new ServiceException(result.toJSONString());
            }
        }
    }

    /**
     * ????????????????????????
     *
     * @param weChatUser
     * @return
     */
    public boolean submitWechatUser(WeChatUser weChatUser) {
        //???????????????user?????????????????????,????????????????????????
        //??????????????????????????????????????????,????????????????????????update,???????????????????????????
        Long userId = weChatUser.getUserId();
        List<WeChatUser> weChatUserList = this.list(new LambdaQueryWrapper<WeChatUser>().eq(WeChatUser::getUserId, userId));
        weChatUser.setUpdateTime(new Date());
        if (CollUtil.isEmpty(weChatUserList)) {
            weChatUser.setCreateTime(new Date());
            return this.save(weChatUser);
        } else if (weChatUserList.size() == 1) {
            return this.updateById(weChatUser);
        } else if (weChatUserList.size() > 1) {
            List<Long> ids = new ArrayList<Long>();
            weChatUserList.stream().forEach(weChatUserTmp -> {
                ids.add(weChatUserTmp.getId());
            });
            this.deleteLogic(ids);
            return this.save(weChatUser);
        }
        return true;
    }

    /**
     * ????????????????????????ID???????????????
     *
     * @param account  ???????????????????????????
     * @param password ????????????
     * @param mpOpenId ???????????????ID
     * @return
     */
    @Override
    public R bindMpAccount(String account, String password, String mpOpenId) {
        if (StringUtils.isEmpty(mpOpenId)) {
            throw new ServiceException("?????????????????????[????????????ID]");
        }
        //???????????????base64??????
        String loginPassword = Base64Util.decode(password);
        HttpResponse httpResponse = this.authAccount(account, loginPassword);
        String body = httpResponse.body();
        JSONObject result = JSON.parseObject(body);
        if (httpResponse.getStatus() == HttpStatus.HTTP_OK) {
            //????????????
            Long userId = Long.valueOf(result.getString("user_id"));
//            String tenantId = result.getString("tenant_id");
            WeChatUser weChatUser = getOne(new LambdaQueryWrapper<WeChatUser>().eq(WeChatUser::getUserId, userId));
            if (weChatUser != null) {
                weChatUser.setMpOpenId(mpOpenId);
                weChatUser.setUpdateTime(new Date());
                updateById(weChatUser);
            }
            return R.data(weChatUser);
        } else {
            //???????????????
            String errorMsg = result.getString("error_description");
            if (StringUtils.isNotBlank(errorMsg)) {
                throw new ServiceException(errorMsg);
            } else {
                throw new ServiceException(result.toJSONString());
            }
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param account
     * @param loginPassword
     * @return
     */
    private HttpResponse authAccount(String account, String loginPassword) {
        Map<String, Object> bodyParams = new HashMap<String, Object>();
        bodyParams.put("grant_type", "password");
        bodyParams.put("scope", "all");
        bodyParams.put("username", account);
        bodyParams.put("password", loginPassword);
        HttpResponse httpResponse = HttpUtil.createRequest(Method.POST, appBaseUrl + AUTH_REQUEST_URL)
                .contentType(ContentType.FORM_URLENCODED.toString())
                .header("Authorization", AUTHORIZATION)
                .form(bodyParams)
                .execute();
        log.info("HTTP Status is:{}", httpResponse.getStatus());
        String body = httpResponse.body();
        log.info("AUTH Response Body={}", body);
        return httpResponse;
    }

    /**
     * ?????????????????????openId??????????????????
     *
     * @param appOpenId
     * @return
     */
    @Override
    public WeChatUser getWechatUserByAppOpenId(String appOpenId) {
        return weChatUserMapper.selectOne(new LambdaQueryWrapper<WeChatUser>().eq(WeChatUser::getAppOpenId, appOpenId));
    }

    /**
     * ?????????????????????unionId??????????????????
     *
     * @param unionId
     * @return
     */
    @Override
    public WeChatUser getWechatUserByUnionId(String unionId) {
        return weChatUserMapper.selectOne(new LambdaQueryWrapper<WeChatUser>().eq(WeChatUser::getUnionId, unionId));
    }

    /**
     * ????????????ID????????????????????????
     *
     * @param userId
     * @return
     */
    @Override
    public WeChatUser getWechatUserByUserId(Long userId) {
        return weChatUserMapper.selectOne(new LambdaQueryWrapper<WeChatUser>().eq(WeChatUser::getUserId, userId));
    }

    /**
     * ?????????????????????????????????
     *
     * @param wxMaUserInfo
     */
    @Override
    public void saveOrUpdateWechatUser(WxMaUserInfo wxMaUserInfo) {
        if (wxMaUserInfo == null) {
            log.error("????????????????????????");
            return;
        }
        String maOpenId = wxMaUserInfo.getOpenId();
        String unionId = wxMaUserInfo.getUnionId();
        WeChatUser weChatUser = null;
        boolean createNew = false;
        List<WeChatUser> weChatUserList = new ArrayList<>();
        if (StringUtils.isNotEmpty(unionId)) {
            List<WeChatUser> list = baseMapper.selectList(new LambdaQueryWrapper<WeChatUser>().eq(WeChatUser::getUnionId, unionId).eq(WeChatUser::getIsDeleted, 0));
            if (CollUtil.isNotEmpty(list)) {
                weChatUserList.addAll(list);
            }
        }
        if (StringUtils.isNotEmpty(maOpenId)) {
            List<WeChatUser> list = baseMapper.selectList(new LambdaQueryWrapper<WeChatUser>().eq(WeChatUser::getAppOpenId, maOpenId).eq(WeChatUser::getIsDeleted, 0));
            if (CollUtil.isNotEmpty(list)) {
                weChatUserList.addAll(list);
            }
        }
        if (CollUtil.isEmpty(weChatUserList)) {
            weChatUser = new WeChatUser();
            weChatUser.setCreateTime(new Date());
            createNew = true;
        } else if (weChatUserList.size() == 1) {
            createNew = false;
            weChatUser = weChatUserList.get(0);
        } else {
            String appOpenId = null;
            String mpOpenId = null;
            Long userId = null;
            //?????????????????????????????????
            for (WeChatUser weChatUserTmp : weChatUserList) {
                if (StringUtils.isNotEmpty(weChatUserTmp.getAppOpenId())) {
                    appOpenId = weChatUserTmp.getAppOpenId();
                }
                if (StringUtils.isNotEmpty(weChatUserTmp.getMpOpenId())) {
                    mpOpenId = weChatUserTmp.getMpOpenId();
                }
                if (weChatUserTmp.getUserId() != null && weChatUserTmp.getUserId() > 0L) {
                    userId = weChatUserTmp.getUserId();
                }
            }
            baseMapper.delete(new LambdaQueryWrapper<WeChatUser>().eq(WeChatUser::getUnionId, unionId));
            baseMapper.delete(new LambdaQueryWrapper<WeChatUser>().eq(WeChatUser::getAppOpenId, appOpenId));
            weChatUser = new WeChatUser();
            weChatUser.setUserId(userId);
            weChatUser.setCreateTime(new Date());
            weChatUser.setMpOpenId(mpOpenId);
            createNew = true;
        }
        weChatUser.setNickName(wxMaUserInfo.getNickName());
        weChatUser.setAvatar(wxMaUserInfo.getAvatarUrl());
        weChatUser.setStatus(1);
        weChatUser.setIsDeleted(0);
        weChatUser.setUnionId(unionId);
        weChatUser.setAppOpenId(maOpenId);
        weChatUser.setUpdateTime(new Date());
        if (createNew) {
            baseMapper.insert(weChatUser);
        } else {
            baseMapper.updateById(weChatUser);
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param wxMaUserInfo
     * @return
     */
    @Override
    public PublicAuthInfoDTO publicAuth(WxMaUserInfo wxMaUserInfo) {
//        if (wxMaUserInfo != null) {
//            //?????????????????????????????????
//            saveOrUpdateWechatUser(wxMaUserInfo);
//        }

        //????????????token???????????????????????????????????????,???????????????7???
        String openId = wxMaUserInfo.getOpenId();
        String token = MyTokenUtil.buildJWT(openId);
        PublicAuthInfoDTO publicAuthInfoDTO = new PublicAuthInfoDTO();
        publicAuthInfoDTO.setAppOpenId(openId);
        publicAuthInfoDTO.setUnionId(wxMaUserInfo.getUnionId());
        publicAuthInfoDTO.setToken(token);

        //????????????BladeRedis?????????????????????????????????JSON??????,???????????????????????????
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.boundValueOps(CacheNames.WX_MA_TOKEN + StringPool.COLON + token).set(openId,7, TimeUnit.DAYS);
//        bladeRedis.setEx(CacheNames.WX_MA_TOKEN + StringPool.COLON + token, "1234", CacheNames.ExpirationTime.EXPIRATION_TIME_7DAYS);
        return publicAuthInfoDTO;
    }
}
