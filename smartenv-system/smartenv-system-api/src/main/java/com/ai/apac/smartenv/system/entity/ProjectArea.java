package com.ai.apac.smartenv.system.entity;

import com.ai.apac.smartenv.common.dto.AreaNode;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

/**
 * @author qianlong
 * @description 项目区域坐标范围信息
 * @Date 2020/11/29 10:43 下午
 **/
@Data
@Document(collection = "project_area")
public class ProjectArea {

    private static final long serialVersionUID = 1L;

    @Id
    @Field("area_id")
    private String areaId;

    @Field("project_code")
    @Indexed
    private String projectCode;

    @Field("area_node")
    private List<List<AreaNode>> areaNodeList;
}
