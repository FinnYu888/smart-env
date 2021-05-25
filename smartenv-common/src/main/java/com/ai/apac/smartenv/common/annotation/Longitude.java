package com.ai.apac.smartenv.common.annotation;

import java.lang.annotation.*;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: Longitude
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/6/1
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/6/1 15:30    panfeng          v1.0.0             修改原因
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Longitude {

    /**
     * 经纬度注解的标记，用来标记字段是属于那个经纬度组的。默认不用指定。如果Bean里面有多条经纬度信息，则需要对所有属于经纬度的字段加上对应的注解
     * 注解的时候，如果是多条，需要把同一对经纬度做相同的tag，以便于区分。 示例：某Bean 里面有四个字段：lat  lng   latitude longitude 四个字段。而 lat  lng 属于一组
     * 经纬度坐标，而latitude longitude 属于另外一组经纬度坐标。如果需要将两组都进行转换。则需要在lat  lng 上分别加上 @Latitude（tag=1） @Longitude(tag=1) 注解。
     * latitude  longitude 上分别加上 @Latitude（tag=2） @Longitude(tag=2) 注解，这样在转换的时候才知道哪两个字段是一对经纬度
     *
     * @return
     */
    int tag() default 0;
}
