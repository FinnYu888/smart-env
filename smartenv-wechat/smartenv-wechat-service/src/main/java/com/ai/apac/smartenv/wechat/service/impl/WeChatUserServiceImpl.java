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
 * @Date 2020/4/15 4:27 下午
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
     * 用户登录获取token
     *
     * @param account
     * @param password
     * @param wxSession
     * @return
     */
    @Override
    public R<JSONObject> auth(String account, String password, WxMaJscode2SessionResult wxSession) {
        //对密码进行base64解密
        String loginPassword = Base64Util.decode(password);

        //校验该操作员是否与员工绑定,如果该帐号角色只是普通用户并且没有绑定员工则不能登录
        User user = UserCache.getUserByAcct(account);
        if (user == null || user.getId() == null) {
            throw new ServiceException("帐号或密码错误");
        }
        if (user.getRoleGroup().indexOf(SystemConstant.RoleAlias.USER) >= 0) {
            String personName = PersonUserRelCache.getPersonNameByUser(user.getId());
            if (StringUtils.isBlank(personName)) {
                throw new ServiceException("该帐号尚未和任何员工绑定,无法登录,请联系管理员");
            }
        }

        //获取unionid

        //通过HTTP方式请求auth服务
        HttpResponse httpResponse = this.authAccount(account, loginPassword);
        int httpStatus = httpResponse.getStatus();
        String body = httpResponse.body();
        JSONObject result = JSON.parseObject(body);
        if (httpStatus == HttpStatus.HTTP_OK) {
            //保存用户的openId
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
     * 保存微信用户信息
     *
     * @param weChatUser
     * @return
     */
    public boolean submitWechatUser(WeChatUser weChatUser) {
        //为避名多个user绑定同一个微信,需要去除重复记录
        //判断该用户是否已经绑定过微信,如果已经绑定过则update,否则就新增一条记录
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
     * 将微信公众号用户ID与用户绑定
     *
     * @param account  登录用户名或手机号
     * @param password 登录密码
     * @param mpOpenId 微信公众号ID
     * @return
     */
    @Override
    public R bindMpAccount(String account, String password, String mpOpenId) {
        if (StringUtils.isEmpty(mpOpenId)) {
            throw new ServiceException("缺少必要的参数[微信用户ID]");
        }
        //对密码进行base64解密
        String loginPassword = Base64Util.decode(password);
        HttpResponse httpResponse = this.authAccount(account, loginPassword);
        String body = httpResponse.body();
        JSONObject result = JSON.parseObject(body);
        if (httpResponse.getStatus() == HttpStatus.HTTP_OK) {
            //校验通过
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
            //校验不通过
            String errorMsg = result.getString("error_description");
            if (StringUtils.isNotBlank(errorMsg)) {
                throw new ServiceException(errorMsg);
            } else {
                throw new ServiceException(result.toJSONString());
            }
        }
    }

    /**
     * 校验用户登录帐号是否正确
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
     * 根据小程序用户openId获取用户信息
     *
     * @param appOpenId
     * @return
     */
    @Override
    public WeChatUser getWechatUserByAppOpenId(String appOpenId) {
        return weChatUserMapper.selectOne(new LambdaQueryWrapper<WeChatUser>().eq(WeChatUser::getAppOpenId, appOpenId));
    }

    /**
     * 根据小程序用户unionId获取用户信息
     *
     * @param unionId
     * @return
     */
    @Override
    public WeChatUser getWechatUserByUnionId(String unionId) {
        return weChatUserMapper.selectOne(new LambdaQueryWrapper<WeChatUser>().eq(WeChatUser::getUnionId, unionId));
    }

    /**
     * 根据用户ID获取微信用户信息
     *
     * @param userId
     * @return
     */
    @Override
    public WeChatUser getWechatUserByUserId(Long userId) {
        return weChatUserMapper.selectOne(new LambdaQueryWrapper<WeChatUser>().eq(WeChatUser::getUserId, userId));
    }

    /**
     * 更新或保存微信用户信息
     *
     * @param wxMaUserInfo
     */
    @Override
    public void saveOrUpdateWechatUser(WxMaUserInfo wxMaUserInfo) {
        if (wxMaUserInfo == null) {
            log.error("微信用户信息为空");
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
            //有多条记录则合并成一条
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
     * 对微信免登录用户进行鉴权
     *
     * @param wxMaUserInfo
     * @return
     */
    @Override
    public PublicAuthInfoDTO publicAuth(WxMaUserInfo wxMaUserInfo) {
//        if (wxMaUserInfo != null) {
//            //更新数据库中最新的数据
//            saveOrUpdateWechatUser(wxMaUserInfo);
//        }

        //生成一个token供前端调用使用并放在缓存中,过期时间为7天
        String openId = wxMaUserInfo.getOpenId();
        String token = MyTokenUtil.buildJWT(openId);
        PublicAuthInfoDTO publicAuthInfoDTO = new PublicAuthInfoDTO();
        publicAuthInfoDTO.setAppOpenId(openId);
        publicAuthInfoDTO.setUnionId(wxMaUserInfo.getUnionId());
        publicAuthInfoDTO.setToken(token);

        //没用使用BladeRedis设值是发现读取的时候有JSON异常,所以此处特殊处理下
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.boundValueOps(CacheNames.WX_MA_TOKEN + StringPool.COLON + token).set(openId,7, TimeUnit.DAYS);
//        bladeRedis.setEx(CacheNames.WX_MA_TOKEN + StringPool.COLON + token, "1234", CacheNames.ExpirationTime.EXPIRATION_TIME_7DAYS);
        return publicAuthInfoDTO;
    }
}
