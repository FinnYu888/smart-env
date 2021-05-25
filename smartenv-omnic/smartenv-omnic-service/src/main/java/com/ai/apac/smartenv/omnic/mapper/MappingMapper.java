package com.ai.apac.smartenv.omnic.mapper;

import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.vo.MappingVO;
import com.ai.apac.smartenv.person.entity.Group;
import com.ai.apac.smartenv.person.vo.GroupVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface MappingMapper extends BaseMapper<AiMapping> {

    /**
     * 自定义分页
     *
     * @param page
     * @return
     */
    List<MappingVO> selectMappingPage(IPage page, MappingVO mapping);

}
