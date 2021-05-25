package com.ai.apac.smartenv.omnic.service.impl;

import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.mapper.MappingMapper;
import com.ai.apac.smartenv.omnic.service.IMappingService;
import com.ai.apac.smartenv.omnic.vo.MappingVO;
import com.ai.apac.smartenv.person.entity.Group;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MappingServiceImpl  extends BaseServiceImpl<MappingMapper, AiMapping> implements IMappingService {

    @Override
    public IPage<MappingVO> selectGroupPage(IPage<MappingVO> page, MappingVO mappingVO) {
        return page.setRecords(baseMapper.selectMappingPage(page, mappingVO));

    }
}
