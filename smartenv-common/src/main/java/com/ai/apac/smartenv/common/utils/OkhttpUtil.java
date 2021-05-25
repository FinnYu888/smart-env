package com.ai.apac.smartenv.common.utils;

import cn.hutool.http.HttpStatus;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: OkhttpUtil
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/5/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/5/19 10:41     panfeng          v1.0.0             修改原因
 */
public class OkhttpUtil {
    private static OkHttpClient okHttpClient;

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("Content-Type: binary/octet-stream");

    static {
        okHttpClient=new OkHttpClient().newBuilder().connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(10000, TimeUnit.MILLISECONDS)
                .build();
    }


    public static byte[] download(String url) throws IOException {
        Request request=new Request.Builder()
                .get()
                .url(url)
                .build();

        Call call = okHttpClient.newCall(request);
        Response execute = call.execute();
        if (execute.isSuccessful()){
            return execute.body().bytes();
        }
        return null;
    }


    /**
     * 同步get请求
     *
     * @param url 地址
     * @return Response 返回数据
     */
    public static Response getSync(final String url) throws IOException {
        final Request request = new Request.Builder().url(url).build();
        final Call call = okHttpClient.newCall(request);
        return call.execute();
    }

    /**
     * post同步请求，提交Json数据
     *
     * @param url  地址
     * @param json json格式的字符串
     * @return Response
     */
    public static Response postSyncJson(String url, String json) throws IOException {
        final RequestBody requestBody = RequestBody.create(JSON_TYPE, json);
        final Request request = new Request.Builder().url(url).post(requestBody).build();
        return okHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于post的文件上传
     *
     * @param url     地址
     * file    提交的文件
     * @param fileKey 提交的文件key
     * @return Response
     * @throws IOException
     */
    public static Response uploadSync(String url, String filepath, String fileKey) throws IOException {
        File file = new File(filepath);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return response;
    }

    /**
     * 同步基于post的文件上传
     *
     * @param url     地址
     * file    提交的文件
     * @param bytes
     * @return Response
     * @throws IOException
     */
    public static Response uploadImage(String url, byte[] bytes) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN,bytes))
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return response;
    }

    /**
     * 同步基于post的文件下载
     * @param fileUrl 下载地址
     * @return Response
     * @throws IOException
     */
    public Response downLoadFile(String fileUrl) throws IOException{
        Request request = new Request.Builder()
                .url(fileUrl)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return response;
    }

}
