package com.ai.apac.smartenv.security.constant;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: SecurityConstant
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/8/21
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/8/21     zhaidx           v1.0.0               修改原因
 */
public class SecurityConstant {
    /**
     * 培训附件类型
     */
    public interface AttachType {
        int PICTURE = 1; // 图片
        int DOC = 2; // 文档
    }

    /**
     * 培训对象类型
     */
    public interface TrainingObjectType {
        int PERSON = 1; // 人
    }
}
