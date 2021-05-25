package com.ai.apac.smartenv.job.cache;

import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.job.dto.JobClusterLockDto;
import com.alibaba.fastjson.JSON;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: DistributedLockHandler
 * @Description: redis 集群锁
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/4
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/4  17:38    panfeng          v1.0.0             修改原因
 */
@Component
public class DistributedLockHandler {


    @Autowired
    private BladeRedis redisCache;
    private final static long LOCK_EXPIRE = 30 * 1000L;//单个业务持有锁的时间30s，防止死锁
    private final static long LOCK_TRY_INTERVAL = 30000L;//默认30s尝试一次
    private final static long LOCK_TRY_TIMEOUT = 20 * 1000L;//默认尝试20s
    private final static String LOCK_KEY_PREFIX = "LOCK:";



    public boolean getTraditionalLock(JobClusterLockDto jobClusterLockDto){

        Boolean execute = redisCache.getRedisTemplate().execute((RedisCallback<? extends Boolean>) connection -> {
            JobClusterLockDto currentDto = jobClusterLockDto;// 复制一份本地变量，便于修改
            currentDto.setNextUUID(UUID.randomUUID().toString());// 生成下一个uuid。如果是自己得到了锁。应该告诉别人下一个uuid应该是什么
            byte[] key = (LOCK_KEY_PREFIX + currentDto.getLockName()).getBytes();// 锁的key
            List<String> arrayList = new ArrayList();
            arrayList.add(new String(key));
            jobClusterLockDto.setKeys(arrayList);
            JSONObject jsonObject = new JSONObject(currentDto);// 锁的value
            String json = jsonObject.toString();
            Boolean success = connection.setNX(key, json.getBytes());// setNX。如果成功说明自己获得了锁
            if (success) {

                return true;
            }
            return false;
        });
        return execute;

    }


    /**
     * 获取一个redis 的单一分布式锁。场景是一个定时任务，只有集群中的一个节点可以执行到。并且当有人执行到的时候，不允许其他人再执行
     *
     * @param jobClusterLockDto
     * @return
     */
    public boolean getScheduleLock(JobClusterLockDto jobClusterLockDto) {
        Boolean execute = redisCache.getRedisTemplate().execute((RedisCallback<? extends Boolean>) connection -> {
            JobClusterLockDto currentDto = jobClusterLockDto;// 复制一份本地变量，便于修改
            currentDto.setNextUUID(UUID.randomUUID().toString());// 生成下一个uuid。如果是自己得到了锁。应该告诉别人下一个uuid应该是什么
            byte[] key = (LOCK_KEY_PREFIX + currentDto.getLockName()).getBytes();// 锁的key
            List<String> arrayList = new ArrayList();
            arrayList.add(new String(key));
            jobClusterLockDto.setKeys(arrayList);
            JSONObject jsonObject = new JSONObject(currentDto);// 锁的value
            String json = jsonObject.toString();
            Boolean success = connection.setNX(key, json.getBytes());// setNX。如果成功说明自己获得了锁
            if (success) {
                return true;
            }
            do {// 如果自己没有获得锁。不断刷锁里面的数据。当刷新到锁里面的内容已经过时的时候。重新抢占锁，默认30秒刷一次
                byte[] value = connection.get(key);//先获取得到锁的人往锁里面放的内容，
                if (value != null && value.length != 0) {
                    String str = new String(value);
                    JobClusterLockDto otherLock = JSON.parseObject(str, JobClusterLockDto.class);
                    Date expirationTime = otherLock.getExpirationTime();
                    if (otherLock.getIsInvalid()) {// 判断旧锁是否失效，如果失效，说明得到锁的人已经将任务执行完了，自己就不需要锁了。直接返回
                        return false;
                    } else if (System.currentTimeMillis() > expirationTime.getTime()) {// 如果得到锁的人没有在指定时间内执行完任务，可以认为他已经挂了。自己重新开始抢锁。
                        key = (LOCK_KEY_PREFIX + currentDto.getLockName() + ":" + currentDto.getNextUUID()).getBytes();
                        arrayList.add(new String(key));
                        jsonObject = new JSONObject(currentDto);
                        json = jsonObject.toString();
                        currentDto.setNextUUID(otherLock.getNextUUID());// 自己生成一个UUID，然后去抢锁
                        success = connection.setNX(key, json.getBytes());
                        if (success) {
                            return true;
                        }
                    }
                } else {// 如果其他人抢到锁以后将锁直接释放了。自己也放弃
                    return false;
                }
                try {
                    Thread.sleep(LOCK_TRY_INTERVAL);//30 秒抢一次
                } catch (InterruptedException e) {
                }
            } while (true);

        });
        return execute;
    }


    /**
     * 释放锁
     */
    public void releaseLock(JobClusterLockDto jobClusterLockDto) {
        if (CollectionUtil.isNotEmpty(jobClusterLockDto.getKeys())) {
            jobClusterLockDto.getKeys().forEach(key -> {
                redisCache.del(key);
            });
        }
    }


}
