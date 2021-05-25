package com.ai.apac.smartenv.common.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import org.springblade.core.tool.utils.StringUtil;

import java.sql.Timestamp;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: StringTimestampConvert
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/5
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/5  21:00    panfeng          v1.0.0             修改原因
 */
public class StringTimestampConvert implements Converter<String, Timestamp> {
    @Override
    public Timestamp convert(String value) {
        if (StringUtil.isNotBlank(value)){
            long l = Long.parseLong(value);
            return new Timestamp(l);
        }

        return null;
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return null;
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return null;
    }
}
