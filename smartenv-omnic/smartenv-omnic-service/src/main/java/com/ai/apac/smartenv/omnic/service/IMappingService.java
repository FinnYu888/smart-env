package com.ai.apac.smartenv.omnic.service;

import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.vo.MappingVO;
import com.ai.apac.smartenv.person.entity.Group;
import com.ai.apac.smartenv.person.vo.GroupVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;

import java.util.List;

public interface IMappingService extends BaseService<AiMapping> {

    IPage<MappingVO> selectGroupPage(IPage<MappingVO> page, MappingVO mappingVO);

}
