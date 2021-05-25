package com.ai.apac.smartenv.person.vo;

import com.ai.apac.smartenv.person.entity.Person;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.util.Date;
import java.util.List;

/**
 * 车辆基本信息表视图实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PersonViewDetailsVO对象", description = "人员360人员完整信息对象")
public class PersonViewDetailsVO extends Person {
    private static final long serialVersionUID = 1L;

}
