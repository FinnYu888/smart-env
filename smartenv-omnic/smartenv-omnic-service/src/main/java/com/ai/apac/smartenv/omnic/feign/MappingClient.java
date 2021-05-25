package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.service.IArrangeService;
import com.ai.apac.smartenv.omnic.service.IMappingService;
import com.ai.apac.smartenv.omnic.vo.MappingVO;
import com.ai.apac.smartenv.omnic.vo.QScheduleObjectVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MappingClient implements IMappingClient{

    @Autowired
    private IMappingService mappingService;


    @Override
    public R<AiMapping> getSscpCodeByThirdCode(AiMapping mapping) {
        QueryWrapper<AiMapping> mappingQueryWrapper = new QueryWrapper<AiMapping>();
        if(ObjectUtil.isNotEmpty(mapping.getThirdCode())){
            mappingQueryWrapper.lambda().eq(AiMapping::getThirdCode,mapping.getThirdCode());
        }
        if(ObjectUtil.isNotEmpty(mapping.getCodeType())){
            mappingQueryWrapper.lambda().eq(AiMapping::getCodeType,mapping.getCodeType());
        }
        if(ObjectUtil.isNotEmpty(mapping.getTenantId())){
            mappingQueryWrapper.lambda().eq(AiMapping::getTenantId,mapping.getTenantId());
        }
        List<AiMapping> mappingList = mappingService.list(mappingQueryWrapper);
        if(ObjectUtil.isNotEmpty(mappingList) && mappingList.size() > 0 ){
            return R.data(mappingList.get(0));
        }
        return R.data(null);
    }

    @Override
    public R<Boolean> delMapping(String sscpCode, Integer codeType) {
        QueryWrapper<AiMapping> wrapper = new QueryWrapper<AiMapping>();
        wrapper.lambda().eq(AiMapping::getSscpCode,sscpCode).eq(AiMapping::getCodeType,codeType);
        return R.data(mappingService.remove(wrapper));
    }

    @Override
    public R<Boolean> saveMappingCode(AiMapping mapping) {
        return R.data(mappingService.save(mapping));
    }
}
