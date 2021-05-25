package com.ai.apac.smartenv.system.vo;

import com.ai.apac.smartenv.system.entity.Station;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tool.node.INode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/4/27 11:41 上午
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "StationVO对象", description = "StationVO对象")
public class StationVO extends Station implements INode {

    private static final long serialVersionUID = 1L;

    private String parentStationName;

    private String stationLevelName;

    private String statusName;

    /**
     * 子孙节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<INode> children;

    @Override
    public List<INode> getChildren() {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        return this.children;
    }
}
