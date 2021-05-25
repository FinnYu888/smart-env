package com.ai.apac.smartenv.job.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: JobClusterLockDto
 * @Description: 集群锁
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/4
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/4  17:38    panfeng          v1.0.0             修改原因
 */
@Data
public class JobClusterLockDto {
    /**
     * 当前锁定的UUID
     */
    private String lockUUID;

    /**
     * 锁定时间
     */
    private Date lockTime;
    /**
     * 下一个UUID。用于当前节点挂了以后其他节点重新争抢时生成的key
     */
    private String nextUUID;

    /**
     * 锁名称
     */
    private String lockName;

    /**
     * 锁失效时间，在过了失效时间后应当重新争抢新锁
     */
    private  Date expirationTime;

    /**
     * 存活时间，
     */
    private Long seconds;
    /**
     * 是否已经失效，用于非锁定节点用来判断主节点是否执行成功
     */
    private Boolean isInvalid;

    private List<String> keys;
}
