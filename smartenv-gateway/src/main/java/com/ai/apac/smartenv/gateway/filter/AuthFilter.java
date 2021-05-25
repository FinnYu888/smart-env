/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.gateway.filter;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.utils.MyTokenUtil;
import com.ai.apac.smartenv.gateway.props.AuthProperties;
import com.ai.apac.smartenv.gateway.provider.AuthProvider;
import com.ai.apac.smartenv.gateway.provider.RequestProvider;
import com.ai.apac.smartenv.gateway.provider.ResponseProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jodd.util.StringPool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.jwt.JwtUtil;
import org.springblade.core.redis.cache.BladeRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 鉴权认证
 *
 * @author Chill
 */
@Slf4j
@Component
@AllArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {
    private AuthProperties authProperties;
    private ObjectMapper objectMapper;

    @Autowired
    private BladeRedis bladeRedis;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String originalRequestUrl = RequestProvider.getOriginalRequestUrl(exchange);
        String path = exchange.getRequest().getURI().getPath();
        ServerHttpResponse resp = exchange.getResponse();
        Integer thirdPartyAuthResult = thirdPartyAuth(exchange, chain);
        switch (thirdPartyAuthResult){
            case 0:{
                return appUnAuth(resp, "APP请求未授权");
            }
            case 2:{
                return chain.filter(exchange);
            }
            default:{
                if (isSkip(path) || isSkip(originalRequestUrl)) {
                    return chain.filter(exchange);
                }
                String headerToken = exchange.getRequest().getHeaders().getFirst(AuthProvider.AUTH_KEY);
                String paramToken = exchange.getRequest().getQueryParams().getFirst(AuthProvider.AUTH_KEY);
                if (StringUtils.isAllBlank(headerToken, paramToken)) {
                    return unAuth(resp, "缺失令牌,鉴权失败");
                }
                String auth = StringUtils.isBlank(headerToken) ? paramToken : headerToken;
                String token = JwtUtil.getToken(auth);
                Claims claims = JwtUtil.parseJWT(token);
                if (claims == null) {
                    return unAuth(resp, "请求未授权");
                }
            }
        }
        return chain.filter(exchange);
    }

    private boolean isSkip(String path) {
        boolean isMatch = AuthProvider.getDefaultSkipUrl().stream().map(url -> url.replace(AuthProvider.TARGET, AuthProvider.REPLACEMENT)).anyMatch(path::startsWith)
                || authProperties.getSkipUrl().stream().map(url -> url.replace(AuthProvider.TARGET, AuthProvider.REPLACEMENT)).anyMatch(path::startsWith);
        return isMatch;
    }

    private Integer thirdPartyAuth(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String filterUrl = AuthProvider.getThirdPartyAuthUrl().stream().map(url ->
                url.replace(AuthProvider.TARGET, AuthProvider.REPLACEMENT)).findAny().orElse(null);
        log.info("filterUrl:{}", filterUrl);
        if (filterUrl.startsWith(path)) {
            String bladeAuth = exchange.getRequest().getHeaders().getFirst(AuthProvider.AUTH_KEY);
            if (StringUtils.isNotEmpty(bladeAuth)) {
                return 1;
            }
            String appAuth = exchange.getRequest().getHeaders().getFirst("App-Auth");
            if (StringUtils.isEmpty(appAuth)) {
                return 0;
            }
            //校验缓存中是否有该Token
            String key = CacheNames.WX_MA_TOKEN + StringPool.COLON + appAuth;
            Object appTokenInfo = bladeRedis.get(key);
            if (appTokenInfo != null) {
                return 2;
            }
            return 0;
        } else {
            return 1;
        }
    }

    private Mono<Void> unAuth(ServerHttpResponse resp, String msg) {
        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
        resp.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String result = "";
        try {
            result = objectMapper.writeValueAsString(ResponseProvider.unAuth(msg));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        DataBuffer buffer = resp.bufferFactory().wrap(result.getBytes(StandardCharsets.UTF_8));
        return resp.writeWith(Flux.just(buffer));
    }

    private Mono<Void> appUnAuth(ServerHttpResponse resp, String msg) {
        resp.setStatusCode(HttpStatus.BAD_REQUEST);
        resp.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String result = "";
        try {
            result = objectMapper.writeValueAsString(ResponseProvider.fail(msg));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        DataBuffer buffer = resp.bufferFactory().wrap(result.getBytes(StandardCharsets.UTF_8));
        return resp.writeWith(Flux.just(buffer));
    }


    @Override
    public int getOrder() {
        return -100;
    }

}
