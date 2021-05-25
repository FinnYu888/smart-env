package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.system.entity.CharSpec;
import com.ai.apac.smartenv.system.entity.CharSpecValue;
import com.ai.apac.smartenv.system.service.ICharSpecService;
import com.ai.apac.smartenv.system.service.ICharSpecValueService;
import com.ai.apac.smartenv.system.vo.CharSpecVO;
import com.ai.apac.smartenv.system.wrapper.CharSpecWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: CharSepcClient
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/2/7
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/2/7     zhaidx           v1.0.0               修改原因
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class CharSpecClient implements ICharSpecClient {

    private ICharSpecService charSpecService;

    private ICharSpecValueService charSpecValueService;


    @Override
    public R<CharSpec> getCharSpecById(Long id) {
        return R.data(charSpecService.getById(id));
    }

    @Override
    public R<CharSpecValue> getCharSpecValue(Long charSpecId,String value) {
        QueryWrapper<CharSpecValue>  charSpecValueQueryWrapper = new QueryWrapper<CharSpecValue>();
        if(ObjectUtil.isNotEmpty(charSpecId)){
            charSpecValueQueryWrapper.lambda().eq(CharSpecValue::getCharSpecId,charSpecId);
        }

        if(ObjectUtil.isNotEmpty(value)){
            charSpecValueQueryWrapper.lambda().eq(CharSpecValue::getValue,value);
        }

        List<CharSpecValue>  charSpecValueList = charSpecValueService.list(charSpecValueQueryWrapper);
        if(ObjectUtil.isNotEmpty(charSpecValueList) && charSpecValueList.size() > 0 ){
            return R.data(charSpecValueList.get(0));
        }else {
            return R.data(null);
        }
    }

    @Override
    public R<List<CharSpecVO>> listCharSpecByEntityCategoryId(@NotEmpty String entityCategoryId) {
        List<CharSpecVO> charSpecVOList = charSpecService.listCharSpecsByEntityCategoryId(entityCategoryId);
        return R.data(charSpecVOList);
    }
}
