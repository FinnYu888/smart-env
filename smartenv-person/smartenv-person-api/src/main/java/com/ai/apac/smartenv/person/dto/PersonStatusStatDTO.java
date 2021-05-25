package com.ai.apac.smartenv.person.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author qianlong
 * @description 车辆状态统计DTO
 * @Date 2020/3/20 9:04 上午
 **/
@Data
public class PersonStatusStatDTO implements Serializable {

    private static final long serialVersionUID = -3152263788019507877L;
    /**
     * 工作中人员
     */
    @ApiModelProperty(value = "工作中人员")
    private List<BasicPersonDTO> workingList;

    /**
     * 脱岗人员
     */
    @ApiModelProperty(value = "脱岗人员")
    private List<BasicPersonDTO> departureList;

    /**
     * 休息中人员
     */
    @ApiModelProperty(value = "休息中人员")
    private List<BasicPersonDTO> sitBackList;
}
