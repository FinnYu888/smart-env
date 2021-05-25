package com.ai.apac.smartenv.omnic.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: xubr
 * @Description:
 * @Company: AsiaInfo International LTD.
 * @Date: Created at 2020/7/28 15:38.
 * <p>Modification History:
 * <p>Date          Author        Version        Description
 * <p>---------------------------------------------------------
 * <p>2020/7/28      xubr           1.0          first version
 */
@Data
@ApiModel(value = "大屏基础查询对象", description = "大屏基础查询对象")
public class BasicInfo4BSVO implements Serializable{
    private static final long serialVersionUID = 6207272330012010963L;
    String id;
    String name;

}
