package com.ai.apac.smartenv.common.utils;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import org.springblade.core.tool.utils.Charsets;

import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/31 3:50 下午
 **/
public class MyTokenUtil {

    /**
     * 创建秘钥
     */
    private static final byte[] SECRET = "6MNSobBRCHGI&^21O0fS6MNasiainfoSobBRCHGIO0fS".getBytes();

    /**
     * 生成Token
     *
     * @param account
     * @return
     */
    public static String buildJWT(String account) {
        try {
            /**
             * 1.创建一个32-byte的密匙
             */
            MACSigner macSigner = new MACSigner(SECRET);
            /**
             * 2. 建立payload 载体
             */
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject("asiainfo")
                    .issuer("http://www.asiainfo.tech")
                    .expirationTime(new Date(System.currentTimeMillis() + CacheNames.ExpirationTime.EXPIRATION_TIME_7DAYS))
                    .claim("ACCOUNT", account)
                    .build();

            /**
             * 3. 建立签名
             */
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(macSigner);

            /**
             * 4. 生成token
             */
            String token = signedJWT.serialize();
            return token;
        } catch (KeyLengthException e) {
            e.printStackTrace();
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 校验token
     *
     * @param token
     * @return
     */
    public static String vaildToken(String token) throws Exception {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SECRET);
            //校验是否有效
            if (!jwt.verify(verifier)) {
                throw new Exception("Token 无效");
            }

            //校验超时
            Date expirationTime = jwt.getJWTClaimsSet().getExpirationTime();
            if (new Date().after(expirationTime)) {
                throw new Exception("Token 已过期");
            }

            //获取载体中的数据
            Object account = jwt.getJWTClaimsSet().getClaim("ACCOUNT");
            //是否有openUid
            if (Objects.isNull(account)) {
                throw new Exception("账号为空");
            }
            return account.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SneakyThrows
    public static String decodeBladeToken(String tokenStr){
        byte[] base64Token = tokenStr.getBytes(Charsets.UTF_8_NAME);

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException var7) {
            throw new Exception("Failed to decode basic authentication token");
        }

        String token = new String(decoded, Charsets.UTF_8_NAME);
        return token;
    }
}
