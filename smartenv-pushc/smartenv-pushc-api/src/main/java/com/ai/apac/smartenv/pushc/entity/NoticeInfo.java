package com.ai.apac.smartenv.pushc.entity;

import com.ai.apac.smartenv.common.constant.PushcConstant;
import lombok.Data;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author qianlong
 * @description 发送的消息内容
 * @Date 2020/6/23 4:09 下午
 **/
@Data
@Document(collection = PushcConstant.MONGODB_NOTICE_INFO)
public class NoticeInfo {

    private static final long serialVersionUID = 1L;

    @Id
    @Field("notice_id")
    private Long noticeId;

    /**
     * 发送渠道
     * 1-Email
     * 2-微信公众号
     * 3-短信
     */
    @Indexed
    @Field("notice_channel")
    private Integer noticeChannel;

    /**
     * 发送状态
     * 1-待发送
     * 2-已发送
     * 3-发送失败
     */
    @Indexed
    @Field("status")
    private Integer status;

    @Field("subject")
    private String subject;

    @Field("receiver")
    private String receiver;

    @Field("cc")
    private String cc;

    @Field("content")
    private String content;

    /**
     * 创建时间
     */
    @Field("create_time")
    private Date createTime;

    /**
     * 创建时间
     */
    @Field("update_time")
    private Date updateTime;

    /**
     * 发送时间
     */
    @Field("send_time")
    private Date sendTime;

    @Field("tenant_id")
    private String tenantId;
}
