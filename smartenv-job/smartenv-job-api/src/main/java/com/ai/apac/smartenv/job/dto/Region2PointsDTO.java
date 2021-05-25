package com.ai.apac.smartenv.job.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName Region2PointsDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/20 16:18
 * @Version 1.0
 */
@Data
@ApiModel(value = "片区对应的坐标集合对象", description = "片区对应的坐标集合对象")
public class Region2PointsDTO implements Serializable {

    private static final long serialVersionUID = -8400328240837856787L;

    private Long regionId;

    private Long parentRegionId;

    List<Point2D.Double> pts;


}
