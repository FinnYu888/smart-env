package com.ai.apac.smartenv.common.enums;

/**
 * Created by qianlong on 2020/2/17.
 */
public interface EnumValue<K,V> {

    /**
     * @return 返回这个枚举对象的值
     */
    public K getValue();

    /**
     * @return 返回这个值的描述
     */
    public V getDesc();

}
