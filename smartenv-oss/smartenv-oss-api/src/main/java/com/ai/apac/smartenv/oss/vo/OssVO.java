package com.ai.apac.smartenv.oss.vo;

import com.ai.apac.smartenv.oss.entity.Oss;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OssVO
 *
 * @author Chill
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "OssVO对象", description = "对象存储表")
public class OssVO extends Oss {
	private static final long serialVersionUID = 1L;

	/**
	 * 分类名
	 */
	private String categoryName;

	/**
	 * 是否启用
	 */
	private String statusName;

}
